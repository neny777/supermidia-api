package br.com.supermidia.configuracao.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Configuração global do sistema — tabela de LINHA ÚNICA (id fixo = 1),
 * semeada com os padrões de fábrica na primeira subida. Os valores vigentes
 * ficam espelhados em memória em {@link ConfiguracaoGlobal}.
 */
@Entity
@Table(name = "configuracoes")
public class Configuracao {

	public static final Integer ID_UNICO = 1;

	@Id
	@Column(name = "id")
	private Integer id = ID_UNICO;

	@Column(name = "validade_orcamento_dias", nullable = false)
	private Integer validadeOrcamentoDias;

	@Column(name = "edicao_horas", nullable = false)
	private Integer edicaoHoras;

	// Fração (0.35 = 35%): margem mínima do atacado.
	@Column(name = "piso_margem", nullable = false, precision = 5, scale = 4)
	private BigDecimal pisoMargem;

	// Multiplicador: varejo = atacado × fator.
	@Column(name = "fator_varejo", nullable = false, precision = 8, scale = 4)
	private BigDecimal fatorVarejo;

	// Padrões pré-preenchidos no formulário da venda (anuláveis: linhas antigas
	// são completadas com os padrões de fábrica pelo service).
	@Column(name = "forma_pagamento_padrao", length = 120)
	private String formaPagamentoPadrao;

	@Column(name = "condicao_pagamento_padrao", length = 120)
	private String condicaoPagamentoPadrao;

	// Sugestões de condição (uma por linha) exibidas no campo da venda.
	@Column(name = "condicoes_sugeridas", length = 500)
	private String condicoesSugeridas;

	@Column(name = "forma_entrega_padrao", length = 120)
	private String formaEntregaPadrao;

	@Column(name = "prazo_entrega_padrao", length = 60)
	private String prazoEntregaPadrao;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getValidadeOrcamentoDias() {
		return validadeOrcamentoDias;
	}

	public void setValidadeOrcamentoDias(Integer validadeOrcamentoDias) {
		this.validadeOrcamentoDias = validadeOrcamentoDias;
	}

	public Integer getEdicaoHoras() {
		return edicaoHoras;
	}

	public void setEdicaoHoras(Integer edicaoHoras) {
		this.edicaoHoras = edicaoHoras;
	}

	public BigDecimal getPisoMargem() {
		return pisoMargem;
	}

	public void setPisoMargem(BigDecimal pisoMargem) {
		this.pisoMargem = pisoMargem;
	}

	public BigDecimal getFatorVarejo() {
		return fatorVarejo;
	}

	public void setFatorVarejo(BigDecimal fatorVarejo) {
		this.fatorVarejo = fatorVarejo;
	}

	public String getFormaPagamentoPadrao() {
		return formaPagamentoPadrao;
	}

	public void setFormaPagamentoPadrao(String formaPagamentoPadrao) {
		this.formaPagamentoPadrao = formaPagamentoPadrao;
	}

	public String getCondicaoPagamentoPadrao() {
		return condicaoPagamentoPadrao;
	}

	public void setCondicaoPagamentoPadrao(String condicaoPagamentoPadrao) {
		this.condicaoPagamentoPadrao = condicaoPagamentoPadrao;
	}

	public String getCondicoesSugeridas() {
		return condicoesSugeridas;
	}

	public void setCondicoesSugeridas(String condicoesSugeridas) {
		this.condicoesSugeridas = condicoesSugeridas;
	}

	public String getFormaEntregaPadrao() {
		return formaEntregaPadrao;
	}

	public void setFormaEntregaPadrao(String formaEntregaPadrao) {
		this.formaEntregaPadrao = formaEntregaPadrao;
	}

	public String getPrazoEntregaPadrao() {
		return prazoEntregaPadrao;
	}

	public void setPrazoEntregaPadrao(String prazoEntregaPadrao) {
		this.prazoEntregaPadrao = prazoEntregaPadrao;
	}
}
