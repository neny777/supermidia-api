package br.com.supermidia.pessoa.usuario.api;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.supermidia.pessoa.colaborador.api.dto.ColaboradorDTO;
import br.com.supermidia.pessoa.colaborador.domain.Colaborador;
import br.com.supermidia.pessoa.usuario.api.dto.UsuarioDTO;
import br.com.supermidia.pessoa.usuario.domain.Usuario;
import br.com.supermidia.pessoa.usuario.domain.UsuarioPermissoes;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "senha", ignore = true)
	@Mapping(target = "permissoes", expression = "java(mapPermissoes(usuarioDTO.getPermissoes(), usuario))")
	@Mapping(target = "colaborador", ignore = true)
	@Mapping(target = "authorities", ignore = true)
	Usuario toEntity(UsuarioDTO usuarioDTO);

	@Mapping(target = "id", source = "colaborador.id")
	@Mapping(target = "permissoes", expression = "java(mapPermissoes(usuario))")
	@Mapping(target = "colaborador", expression = "java(mapColaboradorToDTO(usuario.getColaborador()))")
	UsuarioDTO toResponse(Usuario usuario);

	default Set<UsuarioPermissoes> mapPermissoes(Set<String> permissoes, Usuario usuario) {
		if (permissoes == null) {
			return Set.of();
		}
		return permissoes.stream().map(nome -> {
			UsuarioPermissoes permissao = new UsuarioPermissoes();
			permissao.setNome(nome);
			permissao.setUsuario(usuario);
			return permissao;
		}).collect(Collectors.toSet());
	}

	default Set<String> mapPermissoes(Usuario usuario) {
		if (usuario.getPermissoes() == null) {
			return Set.of();
		}
		return usuario.getPermissoes().stream().map(UsuarioPermissoes::getNome).collect(Collectors.toSet());
	}

	default ColaboradorDTO mapColaboradorToDTO(Colaborador colaborador) {
		if (colaborador == null) {
			return null;
		}
		ColaboradorDTO colaboradorDTO = new ColaboradorDTO();
		colaboradorDTO.setId(colaborador.getId());
		colaboradorDTO.setCtps(colaborador.getCtps());
		colaboradorDTO.setNome(colaborador.getFisica().getNome());
		colaboradorDTO.setEmail(colaborador.getFisica().getEmail());
		colaboradorDTO.setTelefone(colaborador.getFisica().getTelefone());
		colaboradorDTO.setCep(colaborador.getFisica().getCep());
		colaboradorDTO.setNumero(colaborador.getFisica().getNumero());
		colaboradorDTO.setLogradouro(colaborador.getFisica().getLogradouro());
		colaboradorDTO.setBairro(colaborador.getFisica().getBairro());
		colaboradorDTO.setMunicipio(colaborador.getFisica().getMunicipio());
		colaboradorDTO.setUf(colaborador.getFisica().getUf());
		colaboradorDTO.setCpf(colaborador.getFisica().getCpf());
		colaboradorDTO.setRg(colaborador.getFisica().getRg());
		colaboradorDTO.setSexo(colaborador.getFisica().getSexo());
		colaboradorDTO.setNascimento(colaborador.getFisica().getDataNascimento());
		return colaboradorDTO;
	}
}
