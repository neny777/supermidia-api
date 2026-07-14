package br.com.supermidia.venda.api.dto;

import jakarta.validation.constraints.Size;

/** Referência e condições da venda (texto livre) — editáveis fora da janela de 1h. */
public class VendaCabecalhoRequest {

	@Size(max = 120, message = "Referência: máximo 120 caracteres.")
	private String referencia;

	@Size(max = 120, message = "Forma de pagamento: máximo 120 caracteres.")
	private String formaPagamento;

	@Size(max = 120, message = "Condição de pagamento: máximo 120 caracteres.")
	private String condicaoPagamento;

	@Size(max = 120, message = "Forma de entrega: máximo 120 caracteres.")
	private String formaEntrega;

	@Size(max = 60, message = "Prazo de entrega: máximo 60 caracteres.")
	private String prazoEntrega;

	@Size(max = 1000, message = "Observações: máximo 1000 caracteres.")
	private String observacoes;

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(String formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public String getCondicaoPagamento() {
		return condicaoPagamento;
	}

	public void setCondicaoPagamento(String condicaoPagamento) {
		this.condicaoPagamento = condicaoPagamento;
	}

	public String getFormaEntrega() {
		return formaEntrega;
	}

	public void setFormaEntrega(String formaEntrega) {
		this.formaEntrega = formaEntrega;
	}

	public String getPrazoEntrega() {
		return prazoEntrega;
	}

	public void setPrazoEntrega(String prazoEntrega) {
		this.prazoEntrega = prazoEntrega;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}
}
