package br.com.supermidia.venda.domain;

/**
 * Fases do documento de venda. Orçamento e Ordem de Serviço são estágios do
 * MESMO registro (não entidades distintas) — a conversão é só mudança de status.
 * Futuro: EM_PRODUCAO, ENTREGUE.
 */
public enum StatusVenda {
	ORCAMENTO,
	ORDEM_SERVICO,
	CANCELADO
}
