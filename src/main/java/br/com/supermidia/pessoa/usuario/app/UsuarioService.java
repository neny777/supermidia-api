package br.com.supermidia.pessoa.usuario.app;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.supermidia.pessoa.colaborador.api.ColaboradorMapper;
import br.com.supermidia.pessoa.colaborador.api.dto.ColaboradorDTO;
import br.com.supermidia.pessoa.colaborador.app.ColaboradorService;
import br.com.supermidia.pessoa.colaborador.domain.Colaborador;
import br.com.supermidia.pessoa.usuario.api.UsuarioMapper;
import br.com.supermidia.pessoa.usuario.api.dto.UsuarioDTO;
import br.com.supermidia.pessoa.usuario.domain.Usuario;
import br.com.supermidia.pessoa.usuario.domain.UsuarioPermissoes;
import br.com.supermidia.pessoa.usuario.infra.UsuarioRepository;
import br.com.supermidia.security.Permissoes;
import jakarta.transaction.Transactional;

@Service
public class UsuarioService {
	private final UsuarioRepository usuarioRepository;
	private final ColaboradorService colaboradorService;
	private final UsuarioMapper usuarioMapper;
	private final ColaboradorMapper colaboradorMapper;
	private final PasswordEncoder passwordEncoder;

	public UsuarioService(UsuarioRepository usuarioRepository, ColaboradorService colaboradorService,
			UsuarioMapper usuarioMapper, ColaboradorMapper colaboradorMapper, PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.colaboradorService = colaboradorService;
		this.usuarioMapper = usuarioMapper;
		this.colaboradorMapper = colaboradorMapper;
		this.passwordEncoder = passwordEncoder;
	}

	// Criar um novo usuário
	public Usuario create(UsuarioDTO usuarioDTO) {
		// Buscar colaborador existente
		ColaboradorDTO colaboradorDTO = colaboradorService.findById(usuarioDTO.getId());
		
		if (colaboradorDTO == null) {
			throw new IllegalArgumentException("Colaborador não encontrado.");
		}
		
		Colaborador colaborador = colaboradorMapper.toColaborador(colaboradorDTO);

		if (colaborador.getUsuario() != null) {
			throw new IllegalArgumentException("Este colaborador já possui um usuário associado.");
		}

		// Criar usuário
		usuarioDTO.setPermissoes(validarPermissoes(usuarioDTO.getPermissoes()));
		Usuario usuario = usuarioMapper.toEntity(usuarioDTO);
		usuario.setColaborador(colaborador);

		// Gerar senha aleatória com base no UUID
		String senhaAleatoria = gerarSenhaAleatoria();
		usuario.setSenha(passwordEncoder.encode(senhaAleatoria));

		// Persistir
		return usuarioRepository.save(usuario);
	}

	// Editar um novo usuário
	public Usuario update(UUID usuarioId, Set<String> novasPermissoes) {
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

		// Salvar alterações
		return usuarioRepository.save(usuario);
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
		return UUID.randomUUID().toString().replace("-", "");
	}

	private Set<String> validarPermissoes(Set<String> permissoes) {
		Set<String> invalidas = Permissoes.invalidas(permissoes);
		if (!invalidas.isEmpty()) {
			throw new IllegalArgumentException("Permissões inválidas: " + String.join(", ", invalidas));
		}
		return Permissoes.normalizar(permissoes);
	}
}
