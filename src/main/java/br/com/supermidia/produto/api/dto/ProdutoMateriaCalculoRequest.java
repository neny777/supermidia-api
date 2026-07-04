package br.com.supermidia.produto.api.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProdutoMateriaCalculoRequest {

	// Matéria fixa OU slot (grupoSlot) — exatamente um dos dois.
	private UUID materiaId;

	@Size(max = 40)
	private String grupoSlot;

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

	public String getGrupoSlot() {
		return grupoSlot;
	}

	public void setGrupoSlot(String grupoSlot) {
		this.grupoSlot = grupoSlot;
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
