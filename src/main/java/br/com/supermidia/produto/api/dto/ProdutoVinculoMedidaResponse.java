package br.com.supermidia.produto.api.dto;

import java.math.BigDecimal;

public class ProdutoVinculoMedidaResponse {

	private String medidaNome;
	private BigDecimal multiplicador;

	public String getMedidaNome() {
		return medidaNome;
	}

	public void setMedidaNome(String medidaNome) {
		this.medidaNome = medidaNome;
	}

	public BigDecimal getMultiplicador() {
		return multiplicador;
	}

	public void setMultiplicador(BigDecimal multiplicador) {
		this.multiplicador = multiplicador;
	}
}
