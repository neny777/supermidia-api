package br.com.supermidia.produto.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProdutoVinculoMedidaRequest {

	@NotBlank
	@Size(max = 40)
	private String medidaNome;

	@NotNull
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
