package br.com.supermidia.calculo.domain;

public enum CodigoParametroCalculo {
	FATOR("fator", false),
	ACRESCIMO_ALTURA("cm", true),
	ACRESCIMO_LARGURA("cm", true),
	ESPACAMENTO("cm", true),
	QUANTIDADE_FIXA("qtde", false);

	private final String unidadeInformada;
	private final boolean linear;

	CodigoParametroCalculo(String unidadeInformada, boolean linear) {
		this.unidadeInformada = unidadeInformada;
		this.linear = linear;
	}

	public String getUnidadeInformada() {
		return unidadeInformada;
	}

	public boolean isLinear() {
		return linear;
	}
}
