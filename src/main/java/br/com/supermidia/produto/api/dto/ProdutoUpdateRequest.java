package br.com.supermidia.produto.api.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProdutoUpdateRequest {

	@NotBlank
	@Size(max = 140)
	private String nome;

	@NotNull(message = "O markup de atacado é obrigatório.")
	@DecimalMin(value = "0.0", message = "O markup de atacado não pode ser negativo.")
	private BigDecimal markupAtacado;

	@NotNull(message = "O markup de varejo é obrigatório.")
	@DecimalMin(value = "0.0", message = "O markup de varejo não pode ser negativo.")
	private BigDecimal markupVarejo;

	@Valid
	private List<ProdutoMateriaCalculoRequest> materiasCalculo = new ArrayList<>();

	@Valid
	private List<ProdutoServicoCalculoRequest> servicosCalculo = new ArrayList<>();

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

	public List<ProdutoMateriaCalculoRequest> getMateriasCalculo() {
		return materiasCalculo;
	}

	public void setMateriasCalculo(List<ProdutoMateriaCalculoRequest> materiasCalculo) {
		this.materiasCalculo = materiasCalculo;
	}

	public List<ProdutoServicoCalculoRequest> getServicosCalculo() {
		return servicosCalculo;
	}

	public void setServicosCalculo(List<ProdutoServicoCalculoRequest> servicosCalculo) {
		this.servicosCalculo = servicosCalculo;
	}
}
