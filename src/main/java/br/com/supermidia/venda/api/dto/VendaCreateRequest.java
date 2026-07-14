package br.com.supermidia.venda.api.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.supermidia.venda.domain.StatusVenda;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VendaCreateRequest {

	@NotNull(message = "O cliente é obrigatório.")
	private UUID clienteId;

	// Opcional: ORCAMENTO (default) ou ORDEM_SERVICO (venda direta, sem orçar).
	private StatusVenda status;

	// Referência/apelido do trabalho (opcional; diferencia pedidos do mesmo cliente).
	@Size(max = 120, message = "Referência: máximo 120 caracteres.")
	private String referencia;

	// Condições (opcionais; também editáveis depois via PUT /{id}/cabecalho).
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

	@Valid
	@NotEmpty(message = "Informe ao menos um item.")
	private List<VendaItemRequest> itens = new ArrayList<>();

	public UUID getClienteId() {
		return clienteId;
	}

	public void setClienteId(UUID clienteId) {
		this.clienteId = clienteId;
	}

	public StatusVenda getStatus() {
		return status;
	}

	public void setStatus(StatusVenda status) {
		this.status = status;
	}

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

	public List<VendaItemRequest> getItens() {
		return itens;
	}

	public void setItens(List<VendaItemRequest> itens) {
		this.itens = itens;
	}
}
