package br.com.supermidia.venda.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Um produto-template instanciado com medidas concretas dentro de uma {@link Venda}.
 * É um SNAPSHOT: guarda produtoId/nome, medidas, custos e preços congelados no
 * momento da criação — mudanças no catálogo não o afetam. O único campo editável
 * depois é o {@code precoFinal}.
 */
@Entity
@Table(name = "vendas_itens")
public class ItemVenda {

	@Id
	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "id", columnDefinition = "BINARY(16)")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "venda_id", nullable = false, columnDefinition = "BINARY(16)")
	private Venda venda;

	// Referência ao produto-template que gerou o item (snapshot, sem FK viva).
	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "produto_id", columnDefinition = "BINARY(16)")
	private UUID produtoId;

	@Column(name = "produto_nome", length = 140)
	private String produtoNome;

	// Descrição por extenso congelada (título do item e futuro texto da OS impressa).
	@Column(name = "descricao", length = 500)
	private String descricao;

	@Column(name = "altura", precision = 12, scale = 2)
	private BigDecimal altura;

	@Column(name = "largura", precision = 12, scale = 2)
	private BigDecimal largura;

	@Column(name = "quantidade", precision = 12, scale = 2)
	private BigDecimal quantidade;

	@Column(name = "custo_total", precision = 12, scale = 2)
	private BigDecimal custoTotal;

	@Column(name = "markup_aplicado", precision = 7, scale = 2)
	private BigDecimal markupAplicado;

	@Column(name = "preco_sugerido", precision = 12, scale = 2)
	private BigDecimal precoSugerido;

	@Column(name = "preco_final", precision = 12, scale = 2)
	private BigDecimal precoFinal;

	// Entrada original do item (JSON do request: medidas, escolhas) — permite
	// recalcular com os preços atuais reproduzindo exatamente o que foi pedido.
	@Column(name = "entrada_json", length = 4000)
	private String entradaJson;

	@OneToMany(mappedBy = "itemVenda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<ItemVendaDetalhe> detalhes = new ArrayList<>();

	@PrePersist
	public void prePersist() {
		if (this.id == null) {
			this.id = UUID.randomUUID();
		}
	}

	public void setDetalhes(List<ItemVendaDetalhe> detalhes) {
		this.detalhes.clear();
		if (detalhes == null) {
			return;
		}
		detalhes.forEach(this::addDetalhe);
	}

	public void addDetalhe(ItemVendaDetalhe detalhe) {
		detalhe.setItemVenda(this);
		this.detalhes.add(detalhe);
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Venda getVenda() {
		return venda;
	}

	public void setVenda(Venda venda) {
		this.venda = venda;
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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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

	public String getEntradaJson() {
		return entradaJson;
	}

	public void setEntradaJson(String entradaJson) {
		this.entradaJson = entradaJson;
	}

	public List<ItemVendaDetalhe> getDetalhes() {
		return detalhes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		ItemVenda other = (ItemVenda) obj;
		return Objects.equals(id, other.id);
	}
}
