package br.com.supermidia.produto.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import br.com.supermidia.calculo.domain.Calculo;
import br.com.supermidia.materia.domain.Materia;
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

@Entity
@Table(name = "produtos_materias_calculos")
public class ProdutoMateriaCalculo {

	@Id
	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "id", columnDefinition = "BINARY(16)")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "produto_id", nullable = false, columnDefinition = "BINARY(16)")
	private Produto produto;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "materia_id", nullable = false, columnDefinition = "BINARY(16)")
	private Materia materia;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "calculo_id", nullable = false, columnDefinition = "BINARY(16)")
	private Calculo calculo;

	@OneToMany(mappedBy = "produtoMateriaCalculo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<ProdutoMateriaParametroCalculo> parametros = new ArrayList<>();

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

	public Materia getMateria() {
		return materia;
	}

	public void setMateria(Materia materia) {
		this.materia = materia;
	}

	public Calculo getCalculo() {
		return calculo;
	}

	public void setCalculo(Calculo calculo) {
		this.calculo = calculo;
	}

	public void setParametros(List<ProdutoMateriaParametroCalculo> parametros) {
		this.parametros.clear();
		if (parametros == null) {
			return;
		}
		parametros.forEach(this::addParametro);
	}

	public void addParametro(ProdutoMateriaParametroCalculo parametro) {
		parametro.setProdutoMateriaCalculo(this);
		this.parametros.add(parametro);
	}

	public List<ProdutoMateriaParametroCalculo> getParametros() {
		return parametros;
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
		ProdutoMateriaCalculo other = (ProdutoMateriaCalculo) obj;
		return Objects.equals(id, other.id);
	}
}
