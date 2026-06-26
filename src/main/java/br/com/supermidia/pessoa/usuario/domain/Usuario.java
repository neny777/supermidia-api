package br.com.supermidia.pessoa.usuario.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.supermidia.pessoa.colaborador.domain.Colaborador;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
@DynamicUpdate
public class Usuario implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	private UUID id;

	@JsonIgnore
	@Column
	private String senha;

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<UsuarioPermissoes> permissoes = new HashSet<>();

	@OneToOne
	@MapsId
	@JoinColumn(name = "pessoa_id")
	private Colaborador colaborador;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Set<UsuarioPermissoes> getPermissoes() {
		return permissoes;
	}

	public void setPermissoes(Set<UsuarioPermissoes> permissoes) {
		this.permissoes = permissoes;
	}

	public Colaborador getColaborador() {
		return colaborador;
	}

	public void setColaborador(Colaborador colaborador) {
		this.colaborador = colaborador;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return permissoes.stream().map(UsuarioPermissoes::getNome).map(nome -> nome == null ? "" : nome.trim())
				.filter(nome -> !nome.isBlank())
				.map(nome -> nome.startsWith("ROLE_") ? nome : "ROLE_" + nome)
				.map(SimpleGrantedAuthority::new).toList();
	}

	@Override
	public String getPassword() {
		return senha;
	}

	@Override
	public String getUsername() {
		return colaborador.getFisica().getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
