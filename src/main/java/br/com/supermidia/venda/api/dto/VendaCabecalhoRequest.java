package br.com.supermidia.venda.api.dto;

import jakarta.validation.constraints.Size;

/** Condições da venda (texto livre) — editáveis fora da janela de 1h. */
public class VendaCabecalhoRequest {

	@Size(max = 120, message = "Forma de pagamento: máximo 120 caracteres.")
	private String formaPagamento;

	@Size(max = 60, message = "Prazo de entrega: máximo 60 caracteres.")
	private String prazoEntrega;

	@Size(max = 1000, message = "Observações: máximo 1000 caracteres.")
	private String observacoes;

	public String getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(String formaPagamento) {
		this.formaPagamento = formaPagamento;
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
