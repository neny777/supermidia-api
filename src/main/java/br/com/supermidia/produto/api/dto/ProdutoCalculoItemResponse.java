package br.com.supermidia.produto.api.dto;

import java.math.BigDecimal;

import br.com.supermidia.calculo.domain.BaseOperacionalCalculo;
import br.com.supermidia.calculo.domain.TipoCalculo;

public class ProdutoCalculoItemResponse {
	private String nome;
	private String tipoItem;
	// Nome da opção que originou o item; nulo para componentes base.
	private String opcaoNome;
	private String calculo;
	private TipoCalculo tipoCalculo;
	private BaseOperacionalCalculo baseOperacional;
	private BigDecimal quantidadeCalculada;
	private String unidade;
	private BigDecimal precoUnitario;
	private BigDecimal valorTotal;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipoItem() {
		return tipoItem;
	}

	public void setTipoItem(String tipoItem) {
		this.tipoItem = tipoItem;
	}

	public String getOpcaoNome() {
		return opcaoNome;
	}

	public void setOpcaoNome(String opcaoNome) {
		this.opcaoNome = opcaoNome;
	}

	public String getCalculo() {
		return calculo;
	}

	public void setCalculo(String calculo) {
		this.calculo = calculo;
	}

	public TipoCalculo getTipoCalculo() {
		return tipoCalculo;
	}

	public void setTipoCalculo(TipoCalculo tipoCalculo) {
		this.tipoCalculo = tipoCalculo;
	}

	public BaseOperacionalCalculo getBaseOperacional() {
		return baseOperacional;
	}

	public void setBaseOperacional(BaseOperacionalCalculo baseOperacional) {
		this.baseOperacional = baseOperacional;
	}

	public BigDecimal getQuantidadeCalculada() {
		return quantidadeCalculada;
	}

	public void setQuantidadeCalculada(BigDecimal quantidadeCalculada) {
		this.quantidadeCalculada = quantidadeCalculada;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public BigDecimal getPrecoUnitario() {
		return precoUnitario;
	}

	public void setPrecoUnitario(BigDecimal precoUnitario) {
		this.precoUnitario = precoUnitario;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
}
