package br.com.supermidia.produto.api.dto;

import java.math.BigDecimal;

import br.com.supermidia.calculo.domain.CodigoParametroCalculo;
import jakarta.validation.constraints.NotNull;

public class ProdutoParametroCalculoRequest {

	@NotNull
	private CodigoParametroCalculo codigo;

	@NotNull
	private BigDecimal valor;

	public CodigoParametroCalculo getCodigo() {
		return codigo;
	}

	public void setCodigo(CodigoParametroCalculo codigo) {
		this.codigo = codigo;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
}
