package br.com.supermidia.pessoa.dominio.domain;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "pessoas_juridica")
@DynamicUpdate
public class Juridica extends Pessoa {
	private static final long serialVersionUID = 1L;
	@Size(max = 18, message = "O cnpj deve ter no máximo 18 caracteres.")
	private String cnpj;
	@Size(max = 18, message = "A inscrição estadual deve ter no máximo 18 caracteres.")
	private String ie;

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getIe() {
		return ie;
	}

	public void setIe(String ie) {
		this.ie = ie;
	}

	@Override
	public String getTipo() {
		return "JURÍDICA";		
	}
}
