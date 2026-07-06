# Guia do Mecanismo — Cálculos, Produtos e Preço

> Documento do administrador (Denis). Objetivo: depois de lê-lo, montar qualquer
> produto novo **sem ajuda**. Documento vivo — atualizado a cada mudança no motor.
> (O manual completo do sistema será feito no futuro, quando o desenvolvimento estabilizar.)

---

## 1. O mapa geral (o fluxo da informação)

```
CATÁLOGO                    PRODUTO-TEMPLATE                 VENDA
─────────                   ────────────────                 ─────
Matérias (com grupos)  ──►  Componentes BASE            ──►  Orçamento
Serviços               ──►  + Medidas declaradas             (escolhas + medidas
Cálculos               ──►  + Grupos de Opções                → preço automático,
                                                              CONGELADO por 15 dias)
                                                                   │ converter
                                                                   ▼
                                                              Ordem de Serviço
                                                              (mesmo registro,
                                                               outro status)
```

- **Catálogo** = os ingredientes e as fórmulas genéricas.
- **Produto** = a receita: quais ingredientes, com qual fórmula cada um, e o que o operador pode escolher.
- **Venda** = a receita executada com números reais — e fotografada (snapshot): mudar o catálogo depois não altera vendas já feitas.

---

## 2. A pergunta que move tudo

Todo componente de produto existe para responder **uma única pergunta**:

> **"Quanto deste insumo é consumido para uma peça de Altura × Largura × Quantidade?"**

Quem responde é o **CÁLCULO**. Ele é uma fórmula genérica com **lacunas** — e as lacunas são os **PARÂMETROS**.

### Os 8 cálculos do catálogo (e quando usar cada um)

| Cálculo | Responde | Parâmetros que pede | Exemplo real |
|---|---|---|---|
| **ÁREA BASE** | A × L × qtd (m²) | nenhum | tinta, processo de impressão, depile |
| **MATERIAL COM ACRESCIMOS E FATOR** | (A+acr) × (L+acr) × fator × qtd (m²) | ACRESCIMO_ALTURA, ACRESCIMO_LARGURA, FATOR | lona, adesivo (material principal) |
| **POR PERÍMETRO** | (A+L) × 2 × qtd (m) | nenhum | refile, embainhagem, recorte |
| **PERÍMETRO COM ESPAÇAMENTO** | perímetro ÷ espaçamento, arredondado p/ cima (un) | ESPACAMENTO | ilhós, ilhosagem |
| **POR UNIDADE** | qtd_fixa × qtd de peças (un) | QUANTIDADE_FIXA | bastões (2), ponteiras (4), grampos (6) |
| **TAXA FIXA POR ITEM** | qtd_fixa, **uma vez só** (não multiplica pelas peças) | QUANTIDADE_FIXA | ajuste de arte (R$ 5, seja 1 ou 50 peças) |
| **LARGURA SIMPLES** | L × qtd (m) | nenhum | cordão |
| **LARGURA DUPLA** | 2 × L × qtd (m) | nenhum | soldagem, arte da bolsa |

> **Tipo × Base operacional** (tela de Cálculos): o *tipo* é a fórmula; a *base* diz de
> onde vêm os metros (perímetro? só a largura? dobrada?). Os 8 acima cobrem tudo que
> vendemos hoje — criar cálculo novo é raro. Se um dia precisar de "metros da altura ×2",
> por exemplo: novo cálculo tipo `PERIMETRO_BASE`, base `ALTURA_DUPLA`. Pronto.

---

## 3. Parâmetros: o coração do mecanismo (a parte que confunde)

**Regra nº 1 — onde cada coisa mora:**

- O **cálculo** (catálogo) só diz **QUAIS** parâmetros existem. Ex.: MATERIAL COM
  ACRESCIMOS E FATOR exige acréscimos e fator.
- O **VALOR** de cada parâmetro é preenchido **no componente do produto** — porque cada
  produto usa números diferentes (a lona usa fator 1,21; uma chapa usaria outro).

**Regra nº 2 — o valor é uma SOMA de até 3 fontes:**

```
VALOR DO PARÂMETRO  =  constante   +   Σ (medida × multiplicador)   +   Σ contribuições
                       (digitada        (vínculos com medidas            (das OPÇÕES que o
                        no produto)      digitadas no orçamento)          operador marcou)
```

| Fonte | Quem define | Quando usar |
|---|---|---|
| **Constante** | você, no cadastro do produto | o número nunca muda (fator 1,21; sangria 0,2) |
| **Vínculo de medida** | você declara a medida + botão "Vincular medida" no parâmetro | o número vem do balcão a cada orçamento (borda) |
| **Contribuição** | você, dentro da opção do grupo | o número só entra se o operador marcar a opção (bainha do ilhós) |

### Exemplo completo — o acréscimo de altura da lona

Orçamento: lona com **borda 10 cm** e **com ilhós** marcados.

```
ACRESCIMO_ALTURA = 0 (constante) + BORDA(10) × 2 (vínculo) + 6 (contribuição do ilhós)
                 = 26 cm
```

A lona é cortada 26 cm mais alta: 20 cm de borda (10 de cada lado) + 6 cm de bainha
(3 cm dobrados de cada lado). **Um parâmetro, três fontes, tudo automático.** Sem borda
e sem ilhós, vale 0 — e a fórmula vira uma área simples com fator.

---

## 4. Grupos de opções por dentro (os botões que te confundiram)

Uma **opção** (ex.: "COM ILHÓS") é um pacotinho que, quando o operador marca, pode fazer
**duas coisas diferentes** — e é por isso que existem dois tipos de botão no editor:

| Botão | O que faz | Quando usar | Gera linha no orçamento? |
|---|---|---|---|
| **Nova matéria / Novo serviço** | ADICIONA componentes próprios da opção (cada um com seu cálculo e parâmetros, igual à base) | o acabamento **consome algo novo** (ilhós, ilhosagem, embainhagem, recorte, refile) | **Sim** — aparece identificada: "ILHÓS (COM ILHÓS)" |
| **Contribuição** | MODIFICA a base: soma um valor ao parâmetro de mesmo código de **todos** os componentes base que o declaram | o acabamento **muda o consumo do que já existe** (bainha engorda a lona; bolsa do banner engorda lona E montagem) | **Não** — engorda linhas existentes |

**O ilhós usa os dois ao mesmo tempo:** adiciona ilhós+ilhosagem+embainhagem
(componentes) **e** contribui +6 nos acréscimos (a lona cresce pra dobra).

**Teste mental para decidir:** *"marcar esta opção faz a gráfica gastar um insumo novo,
ou gastar MAIS do mesmo insumo?"* — Novo → componente. Mais do mesmo → contribuição.

> ⚠️ As pegadinhas que você já viveu:
> 1. **Opção vazia não faz nada** (a tela avisa em amarelo agora).
> 2. Grupo novo: **salve primeiro, reabra depois** para adicionar componentes às opções.
> 3. **Slot**: a matéria só aparece na escolha do orçamento se tiver o **grupo**
>    preenchido no cadastro dela (LONAS, ADESIVOS, BASTÕES...).

---

## 5. O preço (resumo do que já decidimos)

1. Motor calcula o **custo** de cada componente (quantidade × preço do catálogo).
2. **Margem automática** = `1 − (serviços base ÷ materiais base)`, piso de **35%**.
   Só a **BASE** entra nessa conta — material puxa margem, mão de obra não.
3. As **opções** somam custo e **herdam** a margem (nunca a derrubam).
4. **Atacado** = custo total × (1 + margem). **Varejo** = atacado × 1,3846.
5. Cliente **REVENDA** → atacado; **FINAL** → varejo. Congelado no orçamento por 15 dias.

---

## 6. Receita de bolo: montando um produto novo sozinho

**Passo 1 — Separe os insumos em três caixas:**
- **BASE** — entra sempre (material principal, tinta, processo, taxa de arte);
- **ESCOLHA DE MATERIAL** — o operador escolhe qual variante (→ slot por grupo);
- **OPCIONAL** — o cliente decide se quer (→ grupo de opções).

**Passo 2 — Para cada insumo, escolha o cálculo** pela pergunta *"como ele se consome?"*
(use a tabela da seção 2).

**Passo 3 — Para cada parâmetro que o cálculo pedir, escolha a fonte:**
- número fixo do produto? → **constante**;
- digitado no balcão a cada venda? → declare a **medida** (com mín/máx/padrão) e
  **vincule** com o multiplicador certo (lembre: borda em volta = **×2**);
- só quando uma opção for marcada? → **contribuição** dentro da opção.

**Passo 4 — Monte nesta ordem na tela:**
medidas → componentes base → grupos (salvar → reabrir → compor opções) → **orçamento de
conferência** com valores calculados à mão antes.

### Exercício resolvido: "IMPRESSÃO EM CHAPA PVC" (o próximo produto natural)

| Decisão | Resposta |
|---|---|
| Material principal? | Slot → grupo **CHAPAS** (1mm e 2mm já cadastradas) |
| Como a chapa se consome? | MATERIAL COM ACRESCIMOS E FATOR — acréscimos 0; **fator 1** (chapa não tem bobina; ajuste se houver desperdício de corte) |
| Impressão? | INSUMO (ÁREA BASE) + PROCESSO (ÁREA BASE) — nosso padrão tinta+trabalho |
| Taxa de arte? | AJUSTE DE ARTE + TAXA FIXA POR ITEM (1) |
| Recorte opcional? | Grupo RECORTE [Reto \| Contorno], serviços já existentes + POR PERÍMETRO |
| Medidas extras? | Nenhuma (sem borda em chapa) |

Seis decisões e o produto está desenhado — **este é o nível de raciocínio, não decoreba
de telas.** Quando quiser, monte-o como treino; confiro no banco depois.

---

## 7. Onde cada coisa se edita (referência rápida)

| Quero mudar... | Onde |
|---|---|
| Preço de um insumo | Matérias / Serviços (afeta orçamentos **novos**; antigos são snapshot) |
| Que matérias aparecem num slot | campo **Grupo** no cadastro da matéria |
| Fator, sangria, acréscimos fixos | parâmetros do componente, dentro do produto |
| O que a borda faz | vínculos da medida nos parâmetros do componente |
| O que um acabamento inclui | grupo de opções → opção → componentes/contribuições |
| Margem (35% / 1,3846) e validade (15d) | ainda no código — futura tela de configurações |
