package br.com.supermidia.produto.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import br.com.supermidia.converter.UppercaseConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Medida extra que o produto pede no orçamento, além de altura/largura/quantidade
 * (ex.: BORDA em cm). Alimenta parâmetros de cálculo via vínculos
 * ({@link ProdutoParametroVinculoMedida}) e carrega limites de validação.
 */
@Entity
@Table(name = "produtos_medidas")
public class ProdutoMedida {

	@Id
	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "id", columnDefinition = "BINARY(16)")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "produto_id", nullable = false, columnDefinition = "BINARY(16)")
	private Produto produto;

	@Column(name = "nome", nullable = false, length = 40)
	@Convert(converter = UppercaseConverter.class)
	private String nome;

	@Column(name = "unidade", length = 10)
	private String unidade;

	@Column(name = "obrigatoria", nullable = false)
	private boolean obrigatoria;

	@Column(name = "valor_padrao", precision = 12, scale = 2)
	private BigDecimal valorPadrao;

	@Column(name = "minimo", precision = 12, scale = 2)
	private BigDecimal minimo;

	@Column(name = "maximo", precision = 12, scale = 2)
	private BigDecimal maximo;

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

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public boolean isObrigatoria() {
		return obrigatoria;
	}

	public void setObrigatoria(boolean obrigatoria) {
		this.obrigatoria = obrigatoria;
	}

	public BigDecimal getValorPadrao() {
		return valorPadrao;
	}

	public void setValorPadrao(BigDecimal valorPadrao) {
		this.valorPadrao = valorPadrao;
	}

	public BigDecimal getMinimo() {
		return minimo;
	}

	public void setMinimo(BigDecimal minimo) {
		this.minimo = minimo;
	}

	public BigDecimal getMaximo() {
		return maximo;
	}

	public void setMaximo(BigDecimal maximo) {
		this.maximo = maximo;
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
		ProdutoMedida other = (ProdutoMedida) obj;
		return Objects.equals(id, other.id);
	}
}
