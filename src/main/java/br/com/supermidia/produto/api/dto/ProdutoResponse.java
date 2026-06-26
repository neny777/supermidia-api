package br.com.supermidia.produto.api.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProdutoResponse {

	private UUID id;
	private String nome;
	private List<ProdutoMateriaCalculoResponse> materiasCalculo = new ArrayList<>();
	private List<ProdutoServicoCalculoResponse> servicosCalculo = new ArrayList<>();

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<ProdutoMateriaCalculoResponse> getMateriasCalculo() {
		return materiasCalculo;
	}

	public void setMateriasCalculo(List<ProdutoMateriaCalculoResponse> materiasCalculo) {
		this.materiasCalculo = materiasCalculo;
	}

	public List<ProdutoServicoCalculoResponse> getServicosCalculo() {
		return servicosCalculo;
	}

	public void setServicosCalculo(List<ProdutoServicoCalculoResponse> servicosCalculo) {
		this.servicosCalculo = servicosCalculo;
	}
}
