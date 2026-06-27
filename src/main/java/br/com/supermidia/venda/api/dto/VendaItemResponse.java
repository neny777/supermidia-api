package br.com.supermidia.venda.api.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VendaItemResponse {
	private UUID id;
	private UUID produtoId;
	private String produtoNome;
	private BigDecimal altura;
	private BigDecimal largura;
	private BigDecimal quantidade;
	private BigDecimal custoTotal;
	private BigDecimal markupAplicado;
	private BigDecimal precoSugerido;
	private BigDecimal precoFinal;
	private List<VendaItemDetalheResponse> detalhes = new ArrayList<>();

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getProdutoId() {
		return produtoId;
	}

	public void setProdutoId(UUID produtoId) {
		this.produtoId = produtoId;
	}

	public String getProdutoNome() {
		return produtoNome;
	}

	public void setProdutoNome(String produtoNome) {
		this.produtoNome = produtoNome;
	}

	public BigDecimal getAltura() {
		return altura;
	}

	public void setAltura(BigDecimal altura) {
		this.altura = altura;
	}

	public BigDecimal getLargura() {
		return largura;
	}

	public void setLargura(BigDecimal largura) {
		this.largura = largura;
	}

	public BigDecimal getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getCustoTotal() {
		return custoTotal;
	}

	public void setCustoTotal(BigDecimal custoTotal) {
		this.custoTotal = custoTotal;
	}

	public BigDecimal getMarkupAplicado() {
		return markupAplicado;
	}

	public void setMarkupAplicado(BigDecimal markupAplicado) {
		this.markupAplicado = markupAplicado;
	}

	public BigDecimal getPrecoSugerido() {
		return precoSugerido;
	}

	public void setPrecoSugerido(BigDecimal precoSugerido) {
		this.precoSugerido = precoSugerido;
	}

	public BigDecimal getPrecoFinal() {
		return precoFinal;
	}

	public void setPrecoFinal(BigDecimal precoFinal) {
		this.precoFinal = precoFinal;
	}

	public List<VendaItemDetalheResponse> getDetalhes() {
		return detalhes;
	}

	public void setDetalhes(List<VendaItemDetalheResponse> detalhes) {
		this.detalhes = detalhes;
	}
}
