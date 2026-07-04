package br.com.supermidia.venda.api.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.supermidia.venda.domain.StatusVenda;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class VendaCreateRequest {

	@NotNull(message = "O cliente é obrigatório.")
	private UUID clienteId;

	// Opcional: ORCAMENTO (default) ou ORDEM_SERVICO (venda direta, sem orçar).
	private StatusVenda status;

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

	public List<VendaItemRequest> getItens() {
		return itens;
	}

	public void setItens(List<VendaItemRequest> itens) {
		this.itens = itens;
	}
}
