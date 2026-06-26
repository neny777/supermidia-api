package br.com.supermidia.pessoa.usuario.api;

import org.springframework.stereotype.Component;

import br.com.supermidia.pessoa.usuario.api.dto.UsuarioAtualDTO;
import br.com.supermidia.pessoa.usuario.domain.Usuario;

@Component
public class UsuarioAtualMapper {

	public UsuarioAtualDTO toResponse(Usuario usuario) {
		UsuarioAtualDTO dto = new UsuarioAtualDTO();
		dto.setId(usuario.getId());
		dto.setNome(usuario.getColaborador().getFisica().getNome());
		dto.setEmail(usuario.getColaborador().getFisica().getEmail());
		dto.setFotoUrl(null);
		return dto;
	}
}
