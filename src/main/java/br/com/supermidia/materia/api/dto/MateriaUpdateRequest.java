package br.com.supermidia.materia.api.dto;

import java.math.BigDecimal;

import br.com.supermidia.materia.domain.UnidadeMateria;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MateriaUpdateRequest {

	@NotBlank
	@Size(max = 140)
	private String nome;

	@Size(max = 40)
	private String grupo;

	@NotNull
	private UnidadeMateria unidade;

	@NotNull
	@DecimalMin(value = "0.01")
	private BigDecimal preco;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
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
