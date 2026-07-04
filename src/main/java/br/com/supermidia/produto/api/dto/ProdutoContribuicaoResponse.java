package br.com.supermidia.produto.api.dto;

import java.math.BigDecimal;

import br.com.supermidia.calculo.domain.CodigoParametroCalculo;

public class ProdutoContribuicaoResponse {

	private CodigoParametroCalculo codigo;
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
