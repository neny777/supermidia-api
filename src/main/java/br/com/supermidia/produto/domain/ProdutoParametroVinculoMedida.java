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
 * Vincula uma medida declarada do produto a um parâmetro de cálculo:
 * o valor da medida × multiplicador soma-se ao parâmetro.
 * Ex.: BORDA ×2 no acréscimo de altura (10cm de borda = +20cm).
 */
@Entity
@Table(name = "produtos_parametros_vinculos")
public class ProdutoParametroVinculoMedida {

	@Id
	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "id", columnDefinition = "BINARY(16)")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "parametro_id", nullable = false, columnDefinition = "BINARY(16)")
	private ProdutoComponenteParametro parametro;

	@Column(name = "medida_nome", nullable = false, length = 40)
	@Convert(converter = UppercaseConverter.class)
	private String medidaNome;

	@Column(name = "multiplicador", nullable = false, precision = 8, scale = 4)
	private BigDecimal multiplicador = BigDecimal.ONE;

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

	public ProdutoComponenteParametro getParametro() {
		return parametro;
	}

	public void setParametro(ProdutoComponenteParametro parametro) {
		this.parametro = parametro;
	}

	public String getMedidaNome() {
		return medidaNome;
	}

	public void setMedidaNome(String medidaNome) {
		this.medidaNome = medidaNome;
	}

	public BigDecimal getMultiplicador() {
		return multiplicador;
	}

	public void setMultiplicador(BigDecimal multiplicador) {
		this.multiplicador = multiplicador;
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
		ProdutoParametroVinculoMedida other = (ProdutoParametroVinculoMedida) obj;
		return Objects.equals(id, other.id);
	}
}
