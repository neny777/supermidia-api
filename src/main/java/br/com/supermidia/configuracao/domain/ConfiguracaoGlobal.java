package br.com.supermidia.configuracao.domain;

import java.math.BigDecimal;

/**
 * Valores VIGENTES da configuração global, em memória. Estático porque o
 * domínio (ex.: {@code Venda.isVencido()}) precisa ler sem injeção de
 * dependência — o sistema roda em instância única, então não há divergência.
 * O {@code ConfiguracaoService} carrega do banco na subida e reaplica a cada
 * atualização; fora do Spring (testes de unidade) valem os padrões de fábrica.
 */
public final class ConfiguracaoGlobal {

	public static final int VALIDADE_ORCAMENTO_DIAS_PADRAO = 15;
	public static final int EDICAO_HORAS_PADRAO = 1;
	public static final BigDecimal PISO_MARGEM_PADRAO = new BigDecimal("0.35");
	public static final BigDecimal FATOR_VAREJO_PADRAO = new BigDecimal("1.3846");

	private static volatile int validadeOrcamentoDias = VALIDADE_ORCAMENTO_DIAS_PADRAO;
	private static volatile int edicaoHoras = EDICAO_HORAS_PADRAO;
	private static volatile BigDecimal pisoMargem = PISO_MARGEM_PADRAO;
	private static volatile BigDecimal fatorVarejo = FATOR_VAREJO_PADRAO;

	private ConfiguracaoGlobal() {
	}

	public static void aplicar(Configuracao configuracao) {
		validadeOrcamentoDias = configuracao.getValidadeOrcamentoDias();
		edicaoHoras = configuracao.getEdicaoHoras();
		pisoMargem = configuracao.getPisoMargem();
		fatorVarejo = configuracao.getFatorVarejo();
	}

	/** Volta aos padrões de fábrica (usado por testes para não vazar estado). */
	public static void restaurarPadroes() {
		validadeOrcamentoDias = VALIDADE_ORCAMENTO_DIAS_PADRAO;
		edicaoHoras = EDICAO_HORAS_PADRAO;
		pisoMargem = PISO_MARGEM_PADRAO;
		fatorVarejo = FATOR_VAREJO_PADRAO;
	}

	public static int getValidadeOrcamentoDias() {
		return validadeOrcamentoDias;
	}

	public static int getEdicaoHoras() {
		return edicaoHoras;
	}

	public static BigDecimal getPisoMargem() {
		return pisoMargem;
	}

	public static BigDecimal getFatorVarejo() {
		return fatorVarejo;
	}
}
