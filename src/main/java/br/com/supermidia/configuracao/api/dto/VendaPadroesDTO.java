package br.com.supermidia.configuracao.api.dto;

import java.util.Arrays;
import java.util.List;

import br.com.supermidia.configuracao.domain.Configuracao;

/**
 * Padrões pré-preenchidos no formulário da venda — versão enxuta da
 * configuração, legível pelo papel 'vendas' (sem expor margens/fatores).
 */
public class VendaPadroesDTO {

	private String formaPagamento;
	private String condicaoPagamento;
	private List<String> condicoesSugeridas;
	private String formaEntrega;
	private String prazoEntrega;

	public static VendaPadroesDTO de(Configuracao configuracao) {
		VendaPadroesDTO dto = new VendaPadroesDTO();
		dto.formaPagamento = configuracao.getFormaPagamentoPadrao();
		dto.condicaoPagamento = configuracao.getCondicaoPagamentoPadrao();
		dto.formaEntrega = configuracao.getFormaEntregaPadrao();
		dto.prazoEntrega = configuracao.getPrazoEntregaPadrao();
		String sugeridas = configuracao.getCondicoesSugeridas() == null ? "" : configuracao.getCondicoesSugeridas();
		dto.condicoesSugeridas = Arrays.stream(sugeridas.split("\n"))
				.map(String::trim)
				.filter(linha -> !linha.isBlank())
				.toList();
		return dto;
	}

	public String getFormaPagamento() {
		return formaPagamento;
	}

	public String getCondicaoPagamento() {
		return condicaoPagamento;
	}

	public List<String> getCondicoesSugeridas() {
		return condicoesSugeridas;
	}

	public String getFormaEntrega() {
		return formaEntrega;
	}

	public String getPrazoEntrega() {
		return prazoEntrega;
	}
}
