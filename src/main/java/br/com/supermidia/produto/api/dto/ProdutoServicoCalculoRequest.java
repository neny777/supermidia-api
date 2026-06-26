package br.com.supermidia.produto.api.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class ProdutoServicoCalculoRequest {

	@NotNull
	private UUID servicoId;

	@NotNull
	private UUID calculoId;

	@Valid
	private List<ProdutoParametroCalculoRequest> parametros = new ArrayList<>();

	public UUID getServicoId() {
		return servicoId;
	}

	public void setServicoId(UUID servicoId) {
		this.servicoId = servicoId;
	}

	public UUID getCalculoId() {
		return calculoId;
	}

	public void setCalculoId(UUID calculoId) {
		this.calculoId = calculoId;
	}

	public List<ProdutoParametroCalculoRequest> getParametros() {
		return parametros;
	}

	public void setParametros(List<ProdutoParametroCalculoRequest> parametros) {
		this.parametros = parametros;
	}
}
