package br.com.supermidia.configuracao.api.dto;

import java.math.BigDecimal;

import br.com.supermidia.configuracao.domain.Configuracao;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ConfiguracaoDTO {

	@NotNull(message = "Informe a validade do orçamento (dias).")
	@Min(value = 1, message = "Validade mínima: 1 dia.")
	@Max(value = 365, message = "Validade máxima: 365 dias.")
	private Integer validadeOrcamentoDias;

	@NotNull(message = "Informe a janela de edição (horas).")
	@Min(value = 0, message = "Janela de edição não pode ser negativa (0 desliga a edição).")
	@Max(value = 720, message = "Janela de edição máxima: 720 horas (30 dias).")
	private Integer edicaoHoras;

	// Fração: 0.35 = 35%.
	@NotNull(message = "Informe o piso de margem.")
	@DecimalMin(value = "0", message = "Piso de margem não pode ser negativo.")
	@DecimalMax(value = "0.95", message = "Piso de margem máximo: 0,95 (95%).")
	private BigDecimal pisoMargem;

	@NotNull(message = "Informe o fator de varejo.")
	@DecimalMin(value = "1", message = "Fator de varejo mínimo: 1 (varejo nunca abaixo do atacado).")
	@DecimalMax(value = "10", message = "Fator de varejo máximo: 10.")
	private BigDecimal fatorVarejo;

	@Size(max = 120, message = "Forma de pagamento padrão: máximo 120 caracteres.")
	private String formaPagamentoPadrao;

	@Size(max = 120, message = "Condição de pagamento padrão: máximo 120 caracteres.")
	private String condicaoPagamentoPadrao;

	@Size(max = 500, message = "Condições sugeridas: máximo 500 caracteres.")
	private String condicoesSugeridas;

	@Size(max = 120, message = "Forma de entrega padrão: máximo 120 caracteres.")
	private String formaEntregaPadrao;

	@Size(max = 60, message = "Prazo de entrega padrão: máximo 60 caracteres.")
	private String prazoEntregaPadrao;

	public static ConfiguracaoDTO de(Configuracao configuracao) {
		ConfiguracaoDTO dto = new ConfiguracaoDTO();
		dto.setValidadeOrcamentoDias(configuracao.getValidadeOrcamentoDias());
		dto.setEdicaoHoras(configuracao.getEdicaoHoras());
		dto.setPisoMargem(configuracao.getPisoMargem());
		dto.setFatorVarejo(configuracao.getFatorVarejo());
		dto.setFormaPagamentoPadrao(configuracao.getFormaPagamentoPadrao());
		dto.setCondicaoPagamentoPadrao(configuracao.getCondicaoPagamentoPadrao());
		dto.setCondicoesSugeridas(configuracao.getCondicoesSugeridas());
		dto.setFormaEntregaPadrao(configuracao.getFormaEntregaPadrao());
		dto.setPrazoEntregaPadrao(configuracao.getPrazoEntregaPadrao());
		return dto;
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
