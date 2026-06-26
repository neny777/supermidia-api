package br.com.supermidia.produto.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.supermidia.calculo.domain.CodigoParametroCalculo;

public class ProdutoParametroCalculoResponse {

	private UUID id;
	private CodigoParametroCalculo codigo;
	private BigDecimal valor;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

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
