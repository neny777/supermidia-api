package br.com.supermidia.pessoa.usuario.app;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.supermidia.pessoa.colaborador.domain.Colaborador;
import br.com.supermidia.pessoa.colaborador.infra.ColaboradorRepository;
import br.com.supermidia.pessoa.usuario.api.UsuarioMapper;
import br.com.supermidia.pessoa.usuario.api.dto.UsuarioDTO;
import br.com.supermidia.pessoa.usuario.domain.Usuario;
import br.com.supermidia.pessoa.usuario.domain.UsuarioPermissoes;
import br.com.supermidia.pessoa.usuario.infra.UsuarioRepository;
import br.com.supermidia.security.Permissoes;

@Service
public class UsuarioService {
	private final UsuarioRepository usuarioRepository;
	private final ColaboradorRepository colaboradorRepository;
	private final UsuarioMapper usuarioMapper;
	private final PasswordEncoder passwordEncoder;

	public UsuarioService(UsuarioRepository usuarioRepository, ColaboradorRepository colaboradorRepository,
			UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.colaboradorRepository = colaboradorRepository;
		this.usuarioMapper = usuarioMapper;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Cria o usuário VINCULANDO o colaborador já existente (entidade gerenciada —
	 * jamais recriar o grafo pessoa/física, senão o JPA insere uma Pessoa
	 * duplicada). Devolve a senha inicial gerada, exibida uma única vez.
	 */
	@Transactional
	public UsuarioDTO create(UsuarioDTO usuarioDTO) {
		Colaborador colaborador = colaboradorRepository.findById(usuarioDTO.getId())
				.orElseThrow(() -> new IllegalArgumentException("Colaborador não encontrado."));

		if (usuarioRepository.existsById(colaborador.getId())) {
			throw new IllegalArgumentException("Este colaborador já possui um usuário associado.");
		}

		usuarioDTO.setPermissoes(validarPermissoes(usuarioDTO.getPermissoes()));
		Usuario usuario = usuarioMapper.toEntity(usuarioDTO);
		usuario.setId(null); // o id deriva do colaborador (@MapsId) na persistência
		usuario.setColaborador(colaborador);

		String senhaInicial = gerarSenhaAleatoria();
		usuario.setSenha(passwordEncoder.encode(senhaInicial));

		UsuarioDTO response = usuarioMapper.toResponse(usuarioRepository.save(usuario));
		response.setSenhaInicial(senhaInicial);
		return response;
	}

	// Atualizar as permissões de um usuário
	@Transactional
	public UsuarioDTO update(UUID usuarioId, Set<String> novasPermissoes) {
		Set<String> permissoesValidadas = validarPermissoes(novasPermissoes);
		Usuario usuario = usuarioRepository.findById(usuarioId)
				.orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado."));

		// Remover permissões antigas
		usuario.getPermissoes().clear();

		// Adicionar novas permissões
		permissoesValidadas.forEach(nomePermissao -> {
			UsuarioPermissoes permissao = new UsuarioPermissoes();
			permissao.setNome(nomePermissao);
			permissao.setUsuario(usuario);
			usuario.getPermissoes().add(permissao);
		});

		return usuarioMapper.toResponse(usuarioRepository.save(usuario));
	}

	// Listar todos os usuários retornando DTOs
	public List<UsuarioDTO> findAll() {
		List<Usuario> usuarios = usuarioRepository.findAll();
		return usuarios.stream().map(usuarioMapper::toResponse).toList(); // Converte para uma lista de UsuarioDTO
	}

	// Buscar um usuário por ID
	public UsuarioDTO findById(UUID id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado."));
		return usuarioMapper.toResponse(usuario); // Converte para DTO
	}

	// Excluir um usuário.
	@Transactional
	public void deleteById(UUID usuarioId) {
		Usuario usuario = usuarioRepository.findById(usuarioId)
				.orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado."));

		// Desassocia o usuário do colaborador
		Colaborador colaborador = usuario.getColaborador();
		if (colaborador != null) {
			colaborador.setUsuario(null);
		}

		// Excluir o usuário e as permissões automaticamente
		usuarioRepository.delete(usuario);
		usuarioRepository.flush();

	}

	public UsuarioDTO findByEmail(String email) {
		Usuario usuario = usuarioRepository.findByFisicaEmail(email)
				.orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado."));
		return usuarioMapper.toResponse(usuario); // Converte para DTO
	}

	public Usuario findEntityByEmail(String email) {
		return usuarioRepository.findByFisicaEmail(email)
				.orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado."));
	}

	private String gerarSenhaAleatoria() {
		// 10 caracteres: forte o bastante e digitável (entregue em mãos ao colaborador)
		return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
	}

	private Set<String> validarPermissoes(Set<String> permissoes) {
		Set<String> invalidas = Permissoes.invalidas(permissoes);
		if (!invalidas.isEmpty()) {
			throw new IllegalArgumentException("Permissões inválidas: " + String.join(", ", invalidas));
		}
		return Permissoes.normalizar(permissoes);
	}
}
