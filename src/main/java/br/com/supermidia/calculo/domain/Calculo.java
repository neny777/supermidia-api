package br.com.supermidia.calculo.domain;

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
@Table(name = "calculos")
public class Calculo {

	@Id
	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "id", columnDefinition = "BINARY(16)")
	private UUID id;

	@Column(name = "nome", nullable = false, length = 140, unique = true)
	@Convert(converter = UppercaseConverter.class)
	private String nome;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_calculo", nullable = false, length = 40)
	private TipoCalculo tipoCalculo;

	@Enumerated(EnumType.STRING)
	@Column(name = "base_operacional", nullable = false, length = 40)
	private BaseOperacionalCalculo baseOperacional;

	@Column(name = "permite_override_parametro", nullable = false)
	private boolean permiteOverrideParametro;

	@Column(name = "permite_override_resultado", nullable = false)
	private boolean permiteOverrideResultado;

	public Calculo() {
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

	public boolean isPermiteOverrideParametro() {
		return permiteOverrideParametro;
	}

	public void setPermiteOverrideParametro(boolean permiteOverrideParametro) {
		this.permiteOverrideParametro = permiteOverrideParametro;
	}

	public boolean isPermiteOverrideResultado() {
		return permiteOverrideResultado;
	}

	public void setPermiteOverrideResultado(boolean permiteOverrideResultado) {
		this.permiteOverrideResultado = permiteOverrideResultado;
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
		Calculo other = (Calculo) obj;
		return Objects.equals(id, other.id);
	}
}
