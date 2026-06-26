package br.com.supermidia.produto.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import br.com.supermidia.converter.UppercaseConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "produtos")
public class Produto {

	@Id
	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "id", columnDefinition = "BINARY(16)")
	private UUID id;

	@Column(name = "nome", nullable = false, length = 140, unique = true)
	@Convert(converter = UppercaseConverter.class)
	private String nome;

	@OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private List<ProdutoMateriaCalculo> materiasCalculo = new ArrayList<>();

	@OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private List<ProdutoServicoCalculo> servicosCalculo = new ArrayList<>();

	@PrePersist
	public void prePersist() {
		if (this.id == null) {
			this.id = UUID.randomUUID();
		}
	}

	public void setMateriasCalculo(List<ProdutoMateriaCalculo> materiasCalculo) {
		this.materiasCalculo.clear();
		if (materiasCalculo == null) {
			return;
		}
		materiasCalculo.forEach(this::addMateriaCalculo);
	}

	public void addMateriaCalculo(ProdutoMateriaCalculo materiaCalculo) {
		materiaCalculo.setProduto(this);
		this.materiasCalculo.add(materiaCalculo);
	}

	public void setServicosCalculo(List<ProdutoServicoCalculo> servicosCalculo) {
		this.servicosCalculo.clear();
		if (servicosCalculo == null) {
			return;
		}
		servicosCalculo.forEach(this::addServicoCalculo);
	}

	public void addServicoCalculo(ProdutoServicoCalculo servicoCalculo) {
		servicoCalculo.setProduto(this);
		this.servicosCalculo.add(servicoCalculo);
	}

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

	public List<ProdutoMateriaCalculo> getMateriasCalculo() {
		return materiasCalculo;
	}

	public List<ProdutoServicoCalculo> getServicosCalculo() {
		return servicosCalculo;
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
		Produto other = (Produto) obj;
		return Objects.equals(id, other.id);
	}
}
