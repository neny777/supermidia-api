package br.com.supermidia.produto.api.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ProdutoGrupoOpcaoRequest {

	@NotBlank
	@Size(max = 60)
	private String nome;

	private boolean obrigatorio;

	@Valid
	@NotEmpty(message = "O grupo de opções precisa de ao menos uma opção.")
	private List<ProdutoOpcaoRequest> opcoes = new ArrayList<>();

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public boolean isObrigatorio() {
		return obrigatorio;
	}

	public void setObrigatorio(boolean obrigatorio) {
		this.obrigatorio = obrigatorio;
	}

	public List<ProdutoOpcaoRequest> getOpcoes() {
		return opcoes;
	}

	public void setOpcoes(List<ProdutoOpcaoRequest> opcoes) {
		this.opcoes = opcoes;
	}
}
