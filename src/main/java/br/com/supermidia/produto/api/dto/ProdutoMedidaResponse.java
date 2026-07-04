package br.com.supermidia.produto.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ProdutoMedidaResponse {

	private UUID id;
	private String nome;
	private String unidade;
	private boolean obrigatoria;
	private BigDecimal valorPadrao;
	private BigDecimal minimo;
	private BigDecimal maximo;

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

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public boolean isObrigatoria() {
		return obrigatoria;
	}

	public void setObrigatoria(boolean obrigatoria) {
		this.obrigatoria = obrigatoria;
	}

	public BigDecimal getValorPadrao() {
		return valorPadrao;
	}

	public void setValorPadrao(BigDecimal valorPadrao) {
		this.valorPadrao = valorPadrao;
	}

	public BigDecimal getMinimo() {
		return minimo;
	}

	public void setMinimo(BigDecimal minimo) {
		this.minimo = minimo;
	}

	public BigDecimal getMaximo() {
		return maximo;
	}

	public void setMaximo(BigDecimal maximo) {
		this.maximo = maximo;
	}
}
