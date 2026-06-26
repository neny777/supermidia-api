package br.com.supermidia.produto.api.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class ProdutoMateriaCalculoRequest {

	@NotNull
	private UUID materiaId;

	@NotNull
	private UUID calculoId;

	@Valid
	private List<ProdutoParametroCalculoRequest> parametros = new ArrayList<>();

	public UUID getMateriaId() {
		return materiaId;
	}

	public void setMateriaId(UUID materiaId) {
		this.materiaId = materiaId;
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
