package br.com.supermidia.venda.api.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import br.com.supermidia.produto.api.dto.ProdutoEscolhaMateriaRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class VendaItemRequest {

	@NotNull(message = "O produto é obrigatório.")
	private UUID produtoId;

	@NotNull(message = "A altura é obrigatória.")
	@DecimalMin(value = "0.01", message = "A altura deve ser maior que zero.")
	private BigDecimal altura;

	@NotNull(message = "A largura é obrigatória.")
	@DecimalMin(value = "0.01", message = "A largura deve ser maior que zero.")
	private BigDecimal largura;

	@NotNull(message = "A quantidade é obrigatória.")
	@DecimalMin(value = "0.01", message = "A quantidade deve ser maior que zero.")
	private BigDecimal quantidade;

	// Medidas extras do produto (nome -> valor) e escolhas do orçamento.
	private Map<String, BigDecimal> medidas = new HashMap<>();

	@Valid
	private List<ProdutoEscolhaMateriaRequest> escolhasMateria = new ArrayList<>();

	private List<UUID> escolhasOpcao = new ArrayList<>();

	public UUID getProdutoId() {
		return produtoId;
	}

	public void setProdutoId(UUID produtoId) {
		this.produtoId = produtoId;
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

	public Map<String, BigDecimal> getMedidas() {
		return medidas;
	}

	public void setMedidas(Map<String, BigDecimal> medidas) {
		this.medidas = medidas;
	}

	public List<ProdutoEscolhaMateriaRequest> getEscolhasMateria() {
		return escolhasMateria;
	}

	public void setEscolhasMateria(List<ProdutoEscolhaMateriaRequest> escolhasMateria) {
		this.escolhasMateria = escolhasMateria;
	}

	public List<UUID> getEscolhasOpcao() {
		return escolhasOpcao;
	}

	public void setEscolhasOpcao(List<UUID> escolhasOpcao) {
		this.escolhasOpcao = escolhasOpcao;
	}
}
