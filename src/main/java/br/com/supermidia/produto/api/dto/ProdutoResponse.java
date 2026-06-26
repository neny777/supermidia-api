package br.com.supermidia.produto.api.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProdutoResponse {

	private UUID id;
	private String nome;
	private BigDecimal markupAtacado;
	private BigDecimal markupVarejo;
	private List<ProdutoMateriaCalculoResponse> materiasCalculo = new ArrayList<>();
	private List<ProdutoServicoCalculoResponse> servicosCalculo = new ArrayList<>();

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
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

	public List<ProdutoMateriaCalculoResponse> getMateriasCalculo() {
		return materiasCalculo;
	}

	public void setMateriasCalculo(List<ProdutoMateriaCalculoResponse> materiasCalculo) {
		this.materiasCalculo = materiasCalculo;
	}

	public List<ProdutoServicoCalculoResponse> getServicosCalculo() {
		return servicosCalculo;
	}

	public void setServicosCalculo(List<ProdutoServicoCalculoResponse> servicosCalculo) {
		this.servicosCalculo = servicosCalculo;
	}
}
