package br.com.supermidia.materia.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.supermidia.materia.domain.UnidadeMateria;

public class MateriaResponse {

	private UUID id;
	private String nome;
	private UnidadeMateria unidade;
	private BigDecimal preco;

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

	public UnidadeMateria getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadeMateria unidade) {
		this.unidade = unidade;
	}

	public BigDecimal getPreco() {
		return preco;
	}

	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}
}
