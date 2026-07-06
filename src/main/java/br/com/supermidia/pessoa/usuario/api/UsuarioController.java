package br.com.supermidia.pessoa.usuario.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.supermidia.pessoa.usuario.api.dto.UsuarioDTO;
import br.com.supermidia.pessoa.usuario.api.dto.UsuarioAtualDTO;
import br.com.supermidia.pessoa.usuario.api.dto.TokenValidationResponseDTO;
import br.com.supermidia.pessoa.usuario.app.UsuarioService;
import br.com.supermidia.pessoa.usuario.domain.Usuario;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
	private final UsuarioService usuarioService;
	private final UsuarioAtualMapper usuarioAtualMapper;

	public UsuarioController(UsuarioService usuarioService, UsuarioAtualMapper usuarioAtualMapper) {
		this.usuarioService = usuarioService;
		this.usuarioAtualMapper = usuarioAtualMapper;
	}

	// Cadastro de um novo usuário (a resposta traz a senha inicial, exibida uma única vez)
	@PostMapping
	public ResponseEntity<UsuarioDTO> createUsuario(@RequestBody UsuarioDTO usuarioDTO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.create(usuarioDTO));
	}

	// Edição das permissões de um usuário
	@PutMapping
	public ResponseEntity<UsuarioDTO> updatePermissoesUsuario(@RequestBody UsuarioDTO usuarioDTO) {
		return ResponseEntity.ok(usuarioService.update(usuarioDTO.getId(), usuarioDTO.getPermissoes()));
	}

	// Endpoint para listar todos os usuários
	@GetMapping
	public ResponseEntity<List<UsuarioDTO>> findAll() {
		List<UsuarioDTO> usuarios = usuarioService.findAll();
		return ResponseEntity.ok(usuarios);
	}

	// Endpoint para buscar usuário por ID
	@GetMapping("/{id}")
	public ResponseEntity<UsuarioDTO> findById(@PathVariable UUID id) {
		UsuarioDTO usuarioDTO = usuarioService.findById(id); // Chama o serviço
		return ResponseEntity.ok(usuarioDTO); // Retorna o DTO
	}

	@GetMapping("/email/{email}")
	public ResponseEntity<UsuarioDTO> findByEmail(@PathVariable String email) {
		UsuarioDTO usuarioDTO = usuarioService.findByEmail(email); // Chama o serviço
		return ResponseEntity.ok(usuarioDTO); // Retorna o DTO
	}

	@GetMapping("/me")
	public ResponseEntity<UsuarioAtualDTO> findCurrentUser() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = usuarioService.findEntityByEmail(userDetails.getUsername());
		return ResponseEntity.ok(usuarioAtualMapper.toResponse(usuario));
	}

	// Endpoint para excluir usuário por ID
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
		usuarioService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	// Endpoint para validar o token
	@GetMapping("/validate-token")
	public ResponseEntity<TokenValidationResponseDTO> validateToken() {
		try {
			// Verifica se o token é válido
			UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();

			// Retorna uma mensagem de sucesso com o usuário autenticado
			TokenValidationResponseDTO response = new TokenValidationResponseDTO();
			response.setMessage("Token válido");
			response.setUser(userDetails.getUsername());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			// Retorna erro se o token for inválido
			TokenValidationResponseDTO response = new TokenValidationResponseDTO();
			response.setError("Token inválido");
			return ResponseEntity.status(401).body(response);
		}
	}
}
