package br.com.supermidia.produto.api.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProdutoMateriaCalculoResponse {

	private UUID id;
	private UUID materiaId;
	private String materiaNome;
	private UUID calculoId;
	private String calculoNome;
	private List<ProdutoParametroCalculoResponse> parametros = new ArrayList<>();

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getMateriaId() {
		return materiaId;
	}

	public void setMateriaId(UUID materiaId) {
		this.materiaId = materiaId;
	}

	public String getMateriaNome() {
		return materiaNome;
	}

	public void setMateriaNome(String materiaNome) {
		this.materiaNome = materiaNome;
	}

	public UUID getCalculoId() {
		return calculoId;
	}

	public void setCalculoId(UUID calculoId) {
		this.calculoId = calculoId;
	}

	public String getCalculoNome() {
		return calculoNome;
	}

	public void setCalculoNome(String calculoNome) {
		this.calculoNome = calculoNome;
	}

	public List<ProdutoParametroCalculoResponse> getParametros() {
		return parametros;
	}

	public void setParametros(List<ProdutoParametroCalculoResponse> parametros) {
		this.parametros = parametros;
	}
}
