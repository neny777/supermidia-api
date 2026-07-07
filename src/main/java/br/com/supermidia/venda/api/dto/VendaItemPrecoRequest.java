package br.com.supermidia.venda.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/** Override do preço final de um item (a única edição manual permitida na venda). */
public class VendaItemPrecoRequest {

	@NotNull(message = "Informe o preço final.")
	@DecimalMin(value = "0.01", message = "O preço final deve ser maior que zero.")
	private BigDecimal precoFinal;

	public BigDecimal getPrecoFinal() {
		return precoFinal;
	}

	public void setPrecoFinal(BigDecimal precoFinal) {
		this.precoFinal = precoFinal;
	}
}
