package br.com.supermidia.produto.api.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProdutoCalculoResponse {
	private UUID produtoId;
	private String produtoNome;
	private BigDecimal altura;
	private BigDecimal largura;
	private BigDecimal quantidade;
	private List<ProdutoCalculoItemResponse> materiais = new ArrayList<>();
	private List<ProdutoCalculoItemResponse> servicos = new ArrayList<>();
	// Nomes dos materiais escolhidos nos slots (para a descrição do item da venda).
	private List<String> materiaisEscolhidos = new ArrayList<>();
	private BigDecimal totalMateriais;
	private BigDecimal totalServicos;
	private BigDecimal totalGeral;
	private BigDecimal markupAtacado;
	private BigDecimal markupVarejo;
	private BigDecimal precoAtacado;
	private BigDecimal precoVarejo;
	private BigDecimal precoSugerido;

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

	public List<ProdutoCalculoItemResponse> getMateriais() {
		return materiais;
	}

	public void setMateriais(List<ProdutoCalculoItemResponse> materiais) {
		this.materiais = materiais;
	}

	public List<ProdutoCalculoItemResponse> getServicos() {
		return servicos;
	}

	public void setServicos(List<ProdutoCalculoItemResponse> servicos) {
		this.servicos = servicos;
	}

	public List<String> getMateriaisEscolhidos() {
		return materiaisEscolhidos;
	}

	public void setMateriaisEscolhidos(List<String> materiaisEscolhidos) {
		this.materiaisEscolhidos = materiaisEscolhidos;
	}

	public BigDecimal getTotalMateriais() {
		return totalMateriais;
	}

	public void setTotalMateriais(BigDecimal totalMateriais) {
		this.totalMateriais = totalMateriais;
	}

	public BigDecimal getTotalServicos() {
		return totalServicos;
	}

	public void setTotalServicos(BigDecimal totalServicos) {
		this.totalServicos = totalServicos;
	}

	public BigDecimal getTotalGeral() {
		return totalGeral;
	}

	public void setTotalGeral(BigDecimal totalGeral) {
		this.totalGeral = totalGeral;
	}

	public BigDecimal getMarkupAtacado() {
		return markupAtacado;
	}

	public void setMarkupAtacado(BigDecimal markupAtacado) {
		this.markupAtacado = markupAtacado;
	}

	public BigDecimal getMarkupVarejo() {
		return markupVarejo;
	}

	public void setMarkupVarejo(BigDecimal markupVarejo) {
		this.markupVarejo = markupVarejo;
	}

	public BigDecimal getPrecoAtacado() {
		return precoAtacado;
	}

	public void setPrecoAtacado(BigDecimal precoAtacado) {
		this.precoAtacado = precoAtacado;
	}

	public BigDecimal getPrecoVarejo() {
		return precoVarejo;
	}

	public void setPrecoVarejo(BigDecimal precoVarejo) {
		this.precoVarejo = precoVarejo;
	}

	public BigDecimal getPrecoSugerido() {
		return precoSugerido;
	}

	public void setPrecoSugerido(BigDecimal precoSugerido) {
		this.precoSugerido = precoSugerido;
	}
}
