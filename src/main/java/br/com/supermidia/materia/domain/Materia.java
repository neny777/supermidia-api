package br.com.supermidia.materia.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import br.com.supermidia.converter.UppercaseConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "materias")
public class Materia {

	@Id
	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "id", columnDefinition = "BINARY(16)")
	private UUID id;

	@Column(name = "nome", nullable = false, length = 140, unique = true)
	@Convert(converter = UppercaseConverter.class)
	private String nome;

	// Categoria para slots de material nos produtos (ex.: LONAS, ADESIVOS, CHAPAS).
	// Opcional: matérias sem grupo não aparecem em slot algum (só como componente fixo).
	@Column(name = "grupo", length = 40)
	@Convert(converter = UppercaseConverter.class)
	private String grupo;

	@Enumerated(EnumType.STRING)
	@Column(name = "unidade", nullable = false, length = 2)
	private UnidadeMateria unidade;

	@Column(name = "preco", nullable = false, precision = 12, scale = 2)
	private BigDecimal preco;

	public Materia() {
	}

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

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public UnidadeMateria getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadeMateria unidade) {
		this.unidade = unidade;
	}

	public BigDecimal getPreco() {
		return preco;
	}

	public void setPreco(BigDecimal preco) {
		this.preco = preco;
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
		Materia other = (Materia) obj;
		return Objects.equals(id, other.id);
	}
}
