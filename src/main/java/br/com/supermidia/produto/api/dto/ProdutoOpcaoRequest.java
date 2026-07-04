package br.com.supermidia.produto.api.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProdutoOpcaoRequest {

	@NotBlank
	@Size(max = 60)
	private String nome;

	@Valid
	private List<ProdutoMateriaCalculoRequest> materiasCalculo = new ArrayList<>();

	@Valid
	private List<ProdutoServicoCalculoRequest> servicosCalculo = new ArrayList<>();

	@Valid
	private List<ProdutoContribuicaoRequest> contribuicoes = new ArrayList<>();

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<ProdutoMateriaCalculoRequest> getMateriasCalculo() {
		return materiasCalculo;
	}

	public void setMateriasCalculo(List<ProdutoMateriaCalculoRequest> materiasCalculo) {
		this.materiasCalculo = materiasCalculo;
	}

	public List<ProdutoServicoCalculoRequest> getServicosCalculo() {
		return servicosCalculo;
	}

	public void setServicosCalculo(List<ProdutoServicoCalculoRequest> servicosCalculo) {
		this.servicosCalculo = servicosCalculo;
	}

	public List<ProdutoContribuicaoRequest> getContribuicoes() {
		return contribuicoes;
	}

	public void setContribuicoes(List<ProdutoContribuicaoRequest> contribuicoes) {
		this.contribuicoes = contribuicoes;
	}
}
