package br.com.supermidia.calculo.domain;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class CalculoRules {

	private static final Map<TipoCalculo, List<CodigoParametroCalculo>> PARAMETROS_OBRIGATORIOS = new EnumMap<>(TipoCalculo.class);

	static {
		PARAMETROS_OBRIGATORIOS.put(TipoCalculo.AREA_BASE, List.of());
		PARAMETROS_OBRIGATORIOS.put(TipoCalculo.AREA_COM_FATOR, List.of(CodigoParametroCalculo.FATOR));
		PARAMETROS_OBRIGATORIOS.put(TipoCalculo.AREA_COM_ACRESCIMOS_E_FATOR,
				List.of(CodigoParametroCalculo.ACRESCIMO_ALTURA, CodigoParametroCalculo.ACRESCIMO_LARGURA,
						CodigoParametroCalculo.FATOR));
		PARAMETROS_OBRIGATORIOS.put(TipoCalculo.PERIMETRO_BASE, List.of());
		PARAMETROS_OBRIGATORIOS.put(TipoCalculo.PERIMETRO_COM_ESPACAMENTO, List.of(CodigoParametroCalculo.ESPACAMENTO));
		PARAMETROS_OBRIGATORIOS.put(TipoCalculo.UNIDADE_FIXA, List.of(CodigoParametroCalculo.QUANTIDADE_FIXA));
		PARAMETROS_OBRIGATORIOS.put(TipoCalculo.TAXA_FIXA, List.of(CodigoParametroCalculo.QUANTIDADE_FIXA));
		PARAMETROS_OBRIGATORIOS.put(TipoCalculo.SELECAO_POR_MEDIDA, List.of());
		PARAMETROS_OBRIGATORIOS.put(TipoCalculo.QUANTIDADE_INFORMADA, List.of());
		PARAMETROS_OBRIGATORIOS.put(TipoCalculo.METRO_LINEAR_INFORMADO, List.of());
	}

	private CalculoRules() {
	}

	public static List<CodigoParametroCalculo> parametrosObrigatorios(TipoCalculo tipoCalculo) {
		return PARAMETROS_OBRIGATORIOS.getOrDefault(tipoCalculo, List.of());
	}
}
