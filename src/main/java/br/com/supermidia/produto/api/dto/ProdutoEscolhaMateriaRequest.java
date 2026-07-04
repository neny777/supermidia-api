package br.com.supermidia.produto.api.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

/** Escolha do material de um componente-slot: qual matéria do grupo entra. */
public class ProdutoEscolhaMateriaRequest {

	@NotNull
	private UUID componenteId;

	@NotNull
	private UUID materiaId;

	public UUID getComponenteId() {
		return componenteId;
	}

	public void setComponenteId(UUID componenteId) {
		this.componenteId = componenteId;
	}

	public UUID getMateriaId() {
		return materiaId;
	}

	public void setMateriaId(UUID materiaId) {
		this.materiaId = materiaId;
	}
}
