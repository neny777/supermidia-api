package br.com.supermidia.venda.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import br.com.supermidia.calculo.domain.BaseOperacionalCalculo;
import br.com.supermidia.calculo.domain.TipoCalculo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Uma linha do detalhamento congelado de um {@link ItemVenda}: qual insumo
 * (matéria/serviço), como foi calculado, quanto foi consumido, preço unitário
 * congelado e subtotal. Espelha o resultado do motor de cálculo no momento da
 * criação — torna a venda auditável e reproduzível.
 */
@Entity
@Table(name = "vendas_itens_detalhes")
public class ItemVendaDetalhe {

	@Id
	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "id", columnDefinition = "BINARY(16)")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "item_venda_id", nullable = false, columnDefinition = "BINARY(16)")
	private ItemVenda itemVenda;

	@Column(name = "nome", length = 140)
	private String nome;

	// MATERIA ou SERVICO (espelha o tipoItem do motor de cálculo).
	@Column(name = "tipo_item", length = 10)
	private String tipoItem;

	@Column(name = "calculo_nome", length = 140)
	private String calculoNome;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_calculo", length = 40)
	private TipoCalculo tipoCalculo;

	@Enumerated(EnumType.STRING)
	@Column(name = "base_operacional", length = 40)
	private BaseOperacionalCalculo baseOperacional;

	@Column(name = "quantidade_calculada", precision = 12, scale = 4)
	private BigDecimal quantidadeCalculada;

	@Column(name = "unidade", length = 4)
	private String unidade;

	@Column(name = "preco_unitario", precision = 12, scale = 2)
	private BigDecimal precoUnitario;

	@Column(name = "valor_total", precision = 12, scale = 2)
	private BigDecimal valorTotal;

	@PrePersist
	public void prePersist() {
		if (this.id == null) {
			this.id = UUID.randomUUID();
		}
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public ItemVenda getItemVenda() {
		return itemVenda;
	}

	public void setItemVenda(ItemVenda itemVenda) {
		this.itemVenda = itemVenda;
	}

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

	public String getCalculoNome() {
		return calculoNome;
	}

	public void setCalculoNome(String calculoNome) {
		this.calculoNome = calculoNome;
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
		ItemVendaDetalhe other = (ItemVendaDetalhe) obj;
		return Objects.equals(id, other.id);
	}
}
