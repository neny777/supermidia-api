package br.com.supermidia.pessoa.dominio.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.DynamicUpdate;

import br.com.supermidia.converter.LowercaseConverter;
import br.com.supermidia.converter.UppercaseConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "pessoas")
@DynamicUpdate
public abstract class Pessoa implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	@NotNull(message = "O nome é obrigatório.")
	@Size(max = 60, message = "O nome deve ter no máximo 60 caracteres.")
	@Convert(converter = UppercaseConverter.class)
	private String nome;
	@Email(message = "O e-mail deve ser válido.")
	@Size(max = 70, message = "O e-mail deve ter no máximo 70 caracteres.")
	@Convert(converter = LowercaseConverter.class)
	@Column(unique = true)
	private String email;
	@Size(max = 15, message = "O telefone deve ter no máximo 15 caracteres.")
	@Column(unique = true)
	private String telefone;
	@Size(max = 9, message = "O cep deve ter no máximo 9 caracteres.")
	private String cep;
	@Size(max = 60, message = "O logradouro deve ter no máximo 60 caracteres.")
	@Convert(converter = UppercaseConverter.class)
	private String logradouro;
	@Size(max = 6, message = "O número deve ter no máximo 6 caracteres.")
	@Convert(converter = UppercaseConverter.class)
	private String numero;
	@Size(max = 60, message = "O bairro deve ter no máximo 60 caracteres.")
	@Convert(converter = UppercaseConverter.class)
	private String bairro;
	@Size(max = 60, message = "O município deve ter no máximo 60 caracteres.")
	@Convert(converter = UppercaseConverter.class)
	private String municipio;
	@Size(max = 2, message = "A unidade federal deve ter no máximo 2 caracteres.")
	@Convert(converter = UppercaseConverter.class)
	private String uf;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pessoa other = (Pessoa) obj;
		return Objects.equals(id, other.id);
	}
	
	 public abstract String getTipo();
}
