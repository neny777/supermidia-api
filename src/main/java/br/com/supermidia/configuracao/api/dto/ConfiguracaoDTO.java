package br.com.supermidia.configuracao.api.dto;

import java.math.BigDecimal;

import br.com.supermidia.configuracao.domain.Configuracao;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

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

	public static ConfiguracaoDTO de(Configuracao configuracao) {
		ConfiguracaoDTO dto = new ConfiguracaoDTO();
		dto.setValidadeOrcamentoDias(configuracao.getValidadeOrcamentoDias());
		dto.setEdicaoHoras(configuracao.getEdicaoHoras());
		dto.setPisoMargem(configuracao.getPisoMargem());
		dto.setFatorVarejo(configuracao.getFatorVarejo());
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
}
