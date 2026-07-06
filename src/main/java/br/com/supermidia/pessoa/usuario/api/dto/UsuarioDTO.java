package br.com.supermidia.pessoa.usuario.api.dto;

import java.util.Set;
import java.util.UUID;

import br.com.supermidia.pessoa.colaborador.api.dto.ColaboradorDTO;

public class UsuarioDTO {
    private UUID Id;
    private Set<String> permissoes;
    private ColaboradorDTO colaborador;
    // Preenchida SÓ na criação: senha gerada, exibida uma única vez ao administrador.
    private String senhaInicial;
    
	public UUID getId() {
		return Id;
	}
	public void setId(UUID id) {
		Id = id;
	}
	public Set<String> getPermissoes() {
		return permissoes;
	}
	public void setPermissoes(Set<String> permissoes) {
		this.permissoes = permissoes;
	}
	public ColaboradorDTO getColaborador() {
		return colaborador;
	}
	public void setColaborador(ColaboradorDTO colaborador) {
		this.colaborador = colaborador;
	}
	public String getSenhaInicial() {
		return senhaInicial;
	}
	public void setSenhaInicial(String senhaInicial) {
		this.senhaInicial = senhaInicial;
	}
}
