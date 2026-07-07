package br.com.supermidia.produto.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import br.com.supermidia.calculo.domain.CodigoParametroCalculo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Parâmetro de cálculo de um componente. O valor efetivo é uma SOMA de fontes:
 * constante + Σ(medida informada × multiplicador dos vínculos) + Σ(contribuições
 * das opções ativas com o mesmo código).
 */
@Entity
@Table(name = "produtos_componentes_parametros")
public class ProdutoComponenteParametro {

	@Id
	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "id", columnDefinition = "BINARY(16)")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "componente_id", nullable = false, columnDefinition = "BINARY(16)")
	private ProdutoComponente componente;

	@Enumerated(EnumType.STRING)
	@Column(name = "codigo", nullable = false, length = 30)
	private CodigoParametroCalculo codigo;

	@Column(name = "valor_constante", precision = 12, scale = 4)
	private BigDecimal valorConstante;

	@OneToMany(mappedBy = "parametro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderBy("id")
	private List<ProdutoParametroVinculoMedida> vinculos = new ArrayList<>();

	@PrePersist
	public void prePersist() {
		if (this.id == null) {
			this.id = UUID.randomUUID();
		}
	}

	public void setVinculos(List<ProdutoParametroVinculoMedida> vinculos) {
		this.vinculos.clear();
		if (vinculos == null) {
			return;
		}
		vinculos.forEach(this::addVinculo);
	}

	public void addVinculo(ProdutoParametroVinculoMedida vinculo) {
		vinculo.setParametro(this);
		this.vinculos.add(vinculo);
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public ProdutoComponente getComponente() {
		return componente;
	}

	public void setComponente(ProdutoComponente componente) {
		this.componente = componente;
	}

	public CodigoParametroCalculo getCodigo() {
		return codigo;
	}

	public void setCodigo(CodigoParametroCalculo codigo) {
		this.codigo = codigo;
	}

	public BigDecimal getValorConstante() {
		return valorConstante;
	}

	public void setValorConstante(BigDecimal valorConstante) {
		this.valorConstante = valorConstante;
	}

	public List<ProdutoParametroVinculoMedida> getVinculos() {
		return vinculos;
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
		ProdutoComponenteParametro other = (ProdutoComponenteParametro) obj;
		return Objects.equals(id, other.id);
	}
}
