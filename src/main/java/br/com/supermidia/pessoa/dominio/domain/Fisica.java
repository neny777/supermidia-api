package br.com.supermidia.pessoa.dominio.domain;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicUpdate;

import br.com.supermidia.converter.UppercaseConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "pessoas_fisica")
@DynamicUpdate
public class Fisica extends Pessoa {
	private static final long serialVersionUID = 1L;
	@Size(max = 14, message = "O cpf deve ter no máximo 14 caracteres.")
	@Column(unique = true)
	private String cpf;
	@Size(max = 14, message = "O rg deve ter no máximo 14 caracteres.")
	@Column(unique = true)
	@Convert(converter = UppercaseConverter.class)
	private String rg;
	@Enumerated(EnumType.STRING)
	private Sexo sexo;
	private LocalDate dataNascimento;

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public Sexo getSexo() {
		return sexo;
	}

	public void setSexo(Sexo sexo) {
		this.sexo = sexo;
	}

	public LocalDate getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public enum Sexo {
		M("MASCULINO"), F("FEMININO");

		private final String descricao;

		Sexo(String descricao) {
			this.descricao = descricao;
		}

		public String getDescricao() {
			return descricao;
		}
	}

	@Override
	public String getTipo() {
		return "FÍSICA";
	}
}
