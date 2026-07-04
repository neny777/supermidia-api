package br.com.supermidia.produto.api.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProdutoGrupoOpcaoResponse {

	private UUID id;
	private String nome;
	private boolean obrigatorio;
	private List<ProdutoOpcaoResponse> opcoes = new ArrayList<>();

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

	public boolean isObrigatorio() {
		return obrigatorio;
	}

	public void setObrigatorio(boolean obrigatorio) {
		this.obrigatorio = obrigatorio;
	}

	public List<ProdutoOpcaoResponse> getOpcoes() {
		return opcoes;
	}

	public void setOpcoes(List<ProdutoOpcaoResponse> opcoes) {
		this.opcoes = opcoes;
	}
}
