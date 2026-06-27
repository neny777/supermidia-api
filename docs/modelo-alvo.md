# Supermídia — Modelo-Alvo da Arquitetura

> Documento de referência resultante da revisão de rumo de 2026-06-26.
> Conclusão: **a fundação atende ao propósito; não há reescrita, apenas extensões pontuais.**

## Propósito do sistema

ERP de orçamentação para gráfica / comunicação visual, de **uso interno**. O orçamento é o **meio**: a partir dele compõe-se um **produto-template**, e um **pedido** é uma composição desses produtos. Os preços são **sugestivos** e podem ser ajustados no pedido.

## As 5 camadas

```
┌─────────────────────────────────────────────────────────────┐
│ 5. PEDIDO            Pedido ── ItemPedido (snapshot)      NOVO│
│    "cesta de produtos orçados p/ um cliente"                 │
└───────────────▲─────────────────────────────────────────────┘
                │ congela (snapshot)
┌───────────────┴─────────────────────────────────────────────┐
│ 4. MOTOR            ProdutoCalculoService            EXISTE+ │
│    medidas + produto → custo → PREÇO SUGERIDO               │
└───────────────▲─────────────────────────────────────────────┘
                │ usa
┌───────────────┴─────────────────────────────────────────────┐
│ 3. PRODUTO (template)   Produto + markups            EXISTE+ │
│    composição de matérias/serviços, cada um c/ seu cálculo  │
└───────────────▲─────────────────────────────────────────────┘
                │ referencia
┌───────────────┴──────────────┐  ┌───────────────────────────┐
│ 2. CÁLCULO   Calculo  EXISTE+│  │ 1. CATÁLOGO Materia EXISTE │
│   regra de consumo           │  │             Servico EXISTE │
└──────────────────────────────┘  └───────────────────────────┘
```

### 1. Catálogo — pronto
`Materia` e `Servico`: nome, unidade, `preco` (= **custo unitário**). Sem mudança.

### 2. Cálculo — existe + 1 ajuste
`Calculo` modela a regra de consumo (área, perímetro, unidade). **Pendência: implementar `UNIDADE_FIXA`** no motor (hoje lança "não implementado") — cobre bastão, ponteira, grampo e o "fixo".

### 3. Produto (template) — existe + markup
Composição de matérias/serviços (cada um amarrado a um `Calculo`). **Acrescentar dois campos no `Produto`:**
- `markupAtacado`
- `markupVarejo`

Cada produto-template **define o seu próprio markup** (sem padrão global). Preço: `precoSugerido = custoTotal × (1 + markup)`. A separação material/serviço é mantida apenas para **detalhamento**, não para formar margem.

### 4. Motor — existe + extensão
`ProdutoCalculoService` já calcula o custo. Estender para, conforme `Cliente.categoria`, escolher o markup e devolver o **preço sugerido** com o detalhamento:
- `Cliente.categoria = REVENDA` → markup **atacado**
- `Cliente.categoria = FINAL` → markup **varejo**

Efêmero na fase de orçar (preview).

### 5. Venda (Orçamento / Ordem de Serviço) — NOVO
**Uma única entidade `Venda`** com `status` — não dois cadastros. O documento evolui de orçamento para OS por **mudança de status** (não há herança/subtipo). Na UI vira duas telas: "Orçamentos" e "Ordens de Serviço".

```
Venda
 ├─ cliente            (→ define atacado/varejo via categoria)
 ├─ status             (ORCAMENTO | ORDEM_SERVICO | CANCELADO; futuro: EM_PRODUCAO, ENTREGUE)
 ├─ dataCriacao        (base da validade de 15 dias do orçamento)
 ├─ itens: [ItemVenda]
 └─ total

ItemVenda  ("memória de cálculo" congelada / snapshot)
 ├─ produtoId + produtoNome   (qual template gerou)
 ├─ altura, largura, quantidade
 ├─ detalhamento[]   (insumo, qtd calculada, custo unit. CONGELADO, subtotal)
 ├─ custoTotal       (snapshot)
 ├─ markupAplicado   (snapshot)
 ├─ precoSugerido    (snapshot)
 └─ precoFinal       (editável — único override)
```

**Imutabilidade:** o produto-template aponta para o catálogo vivo; o `ItemVenda` guarda uma **fotografia** do cálculo. Mudança de preço no catálogo **não** altera venda já feita. A memória de cálculo é auditável e reproduzível.

**Por que estado e não herança (padrão Pessoa):** Pessoa→Cliente/Fornecedor/etc. são papéis que *coexistem* num mesmo indivíduo; orçamento e OS são *fases sequenciais* do mesmo documento. Estado (campo `status`) preserva identidade e histórico na conversão; subtipo exigiria recriar a linha (perdendo ambos).

**Validade do orçamento:** 15 dias, calculada como estado *derivado* (`dataCriacao + 15d < hoje`), sem rotina/agendador. Ao abrir um orçamento vencido: avisar e oferecer **"Recalcular/Renovar"** (reprocessa com preços atuais, regrava snapshot, reinicia os 15 dias) ou **"Cancelar"**. Futuramente o "15" migra para uma configuração global do sistema.

**Cabeçalho adiado:** forma de pagamento, forma de entrega, prazo de entrega e desconto no pedido serão modelados depois.

## Decisões fixadas (2026-06-26)

| Tema | Decisão |
|---|---|
| Margem | Markup **por produto-template**; sem default global |
| Atacado × Varejo | Definido pela **categoria do cliente** (REVENDA→atacado, FINAL→varejo) |
| Override na venda | **Apenas o `precoFinal`** do item. Quantidade de insumo é sempre auto-calculada |
| "Fixo" (R$5) | Por **item** (não por venda) |
| Entidade da venda | Nome interno **`Venda`** (UI: "Orçamentos"/"Ordens de Serviço"); **um registro + status**, sem herança |
| Validade do orçamento | **15 dias** (derivado); vencido → "Recalcular/Renovar" ou "Cancelar" |
| Bastão | Tratado por **unidade** (não por comprimento/tabela) |
| Regras "espertas" (#3) | **Fora de escopo** por ora: condicional por dimensão, seleção por tabela, sobretaxa de peça pequena |
| `permiteOverrideParametro` | Flag fica **dormente** nesta fase (mantida, não usada) |

## Origem da lógica de preço

Decodificada dos scripts PHP legados em `calculadoras/` (smlona, smbanner, smimpressaocomrecorte, smimpressaosemrecorte + `precos.php`). Cada script é um produto-template hardcoded; o sistema novo os torna configuráveis como dado. A heurística antiga de margem (`max(0,35; 1 − serviço/material)` e varejo = atacado × 1,8/1,3) foi **substituída** por markup explícito por produto.

## Ordem de implementação

1. **`UNIDADE_FIXA`** no motor (+ testes) — desbloqueia os produtos legados; pequeno e isolado.
2. **Markup no Produto** + seleção por `Cliente.categoria` no motor — fecha a precificação de orçamento.
3. **Validar** reproduzindo `smlona`/`smbanner` como produtos-template (prova real da arquitetura).
4. **Camada Venda** (`Venda` + `ItemVenda` com snapshot; status ORCAMENTO/ORDEM_SERVICO/CANCELADO).
