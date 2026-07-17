# Deploy do Supermídia em Windows (rede local, para testes)

> O pacote gerado por `deploy/build-pacote.sh` contém tudo: `supermidia.jar`
> (backend **e** frontend juntos), `start-supermidia.bat`, `supermidia-dump.sql`
> (seus dados) e este guia. Os colegas acessam pelo navegador, sem instalar nada
> nas máquinas deles.

---

## 1. O que instalar no computador Windows (uma vez só)

| Programa | Onde baixar | Observação |
|---|---|---|
| **Java 21** | adoptium.net → Temurin 21 (JRE, MSI) | no instalador, marque a opção "Set JAVA_HOME" / "Add to PATH" |
| **MySQL 8** | dev.mysql.com/downloads/installer | tipo "Server only"; defina a senha do usuário `root` (se usar diferente de `root`, edite o `.bat` depois) |

Para conferir, abra o **Prompt de Comando** e rode: `java -version` e `mysql --version`.

## 2. Copiar o pacote

Copie a pasta `deploy-supermidia` (pendrive/rede) para o computador Windows —
ex.: `C:\supermidia`.

## 3. Importar o banco de dados

> O instalador do MySQL **não** coloca o comando `mysql` no PATH do Windows —
> por isso use o cliente do menu Iniciar:

1. Menu **Iniciar** → **"MySQL 8.0 Command Line Client"** → digite a senha do root.
2. Dentro dele, rode (ajuste o caminho se a pasta for outra):

```sql
source C:/supermidia/supermidia-dump.sql
```

3. Confirme com `SHOW DATABASES;` — deve listar `supermidia`.

(Alternativa pelo Prompt de Comando, com caminho completo:
`"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < supermidia-dump.sql`)

Isso cria o banco `supermidia` com todos os cadastros: matérias, serviços,
cálculos, produtos e usuários — as mesmas senhas de login do ambiente atual.

## 4. Liberar a porta 8080 no Firewall do Windows

Prompt de Comando **como administrador**:

```
netsh advfirewall firewall add rule name="Supermidia" dir=in action=allow protocol=TCP localport=8080
```

(Ou pelo painel: Firewall do Windows Defender → Configurações avançadas →
Regras de Entrada → Nova Regra → Porta → TCP 8080 → Permitir.)

## 5. Iniciar o sistema

Dê dois cliques em **`start-supermidia.bat`**. A janela preta é o servidor —
**deixe-a aberta** (fechar = desligar o sistema).

> Se o MySQL tiver senha diferente de `root`, clique com o direito no `.bat` →
> Editar → ajuste `DB_PASSWORD` antes de iniciar.

## 6. Acessar da rede

1. No computador Windows, descubra o IP: `ipconfig` → "Endereço IPv4"
   (ex.: `192.168.3.50`).
2. Em qualquer máquina da rede, abra o navegador em:

```
http://192.168.3.50:8080
```

Login normal — os mesmos usuários e senhas de hoje.

## 7. (Recomendado) Rodar como serviço do Windows — sem janela aberta

O modo do passo 5 exige uma janela preta aberta — que alguém sempre fecha por
engano. O **modo serviço** resolve: sem janela nenhuma, inicia sozinho quando o
Windows liga e **reinicia sozinho se o servidor cair**.

1. Confira que estes arquivos do pacote estão em `C:\supermidia`:
   `supermidia-service.exe`, `supermidia-service.xml`, `instalar-servico.bat`.
2. **Se o sistema já rodava pelo `.bat`:** para manter os logins de todos,
   abra o `start-supermidia.bat` antigo, copie o valor de `JWT_SECRET_KEY` e
   cole no lugar do valor correspondente no `supermidia-service.xml`
   (botão direito → Abrir com → Bloco de Notas). Se pular este passo, o sistema
   funciona igual — só que todos precisam fazer login de novo uma vez.
3. Feche a janela preta do servidor (se estiver aberta).
4. Botão direito em **`instalar-servico.bat`** → **Executar como administrador**.

Pronto: o serviço "Supermidia" aparece em `services.msc` e sobe sozinho a cada
boot. A "janela preta" vira arquivos de log em `C:\supermidia\logs\` (com
rotação automática — é lá que se olha quando algo der errado, ex.: e-mail que
não saiu). Para remover o serviço: `desinstalar-servico.bat` como administrador.

> Com o serviço instalado, o `start-supermidia.bat` vira reserva para uso
> manual — não use os dois ao mesmo tempo (a porta 8080 conflita).

---

## Problemas comuns

| Sintoma | Causa provável | Solução |
|---|---|---|
| Janela fecha na hora | Java não instalado / não está no PATH | reinstale o Temurin marcando as opções de PATH |
| Erro de conexão com banco ao iniciar | senha do MySQL diferente do `.bat` | edite `DB_PASSWORD` no `.bat` |
| "Port 8080 already in use" | outro programa usa a porta | adicione `set SERVER_PORT=8081` no `.bat` (e use :8081 no navegador) |
| Acessa no próprio Windows, mas não da rede | firewall | refaça o passo 4 |
| Esqueci a senha de um usuário | recuperação por e-mail exige SMTP real | configure as variáveis `MAIL_*` no `.bat`, ou redefina no banco |

## Para atualizar o sistema depois

No Linux de desenvolvimento: rode `deploy/build-pacote.sh`, copie o novo
`supermidia.jar` por cima do antigo no Windows e reinicie o servidor —
no modo serviço: `services.msc` → Supermidia → Reiniciar (ou
`supermidia-service.exe restart` num prompt como administrador); no modo
janela: feche e abra o `.bat`.
**Não** reimporte o dump (apagaria os dados criados no teste!) — o banco
evolui sozinho a cada versão (`ddl-auto=update`).
