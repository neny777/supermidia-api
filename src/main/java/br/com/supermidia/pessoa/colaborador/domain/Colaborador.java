package br.com.supermidia.pessoa.colaborador.domain;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import br.com.supermidia.pessoa.dominio.domain.Fisica;
import br.com.supermidia.pessoa.usuario.domain.Usuario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "colaboradores")
public class Colaborador {
	@Id
	private UUID id;

	@Size(max = 15, message = "O número da carteira de trabalho deve ter no máximo 15 caracteres.")	
	@Column(unique = true)
	private String ctps;

	@OneToOne
	@MapsId
	@JoinColumn(name = "pessoa_id")
	private Fisica fisica;
	
	@JsonIgnore
	@JsonManagedReference
	@OneToOne(mappedBy = "colaborador", cascade = CascadeType.ALL, orphanRemoval = true)
	private Usuario usuario;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getCtps() {
		return ctps;
	}

	public void setCtps(String ctps) {
		this.ctps = ctps;
	}

	public Fisica getFisica() {
		return fisica;
	}

	public void setFisica(Fisica fisica) {
		this.fisica = fisica;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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
		Colaborador other = (Colaborador) obj;
		return Objects.equals(id, other.id);
	}
}
