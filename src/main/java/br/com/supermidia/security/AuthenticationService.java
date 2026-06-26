package br.com.supermidia.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import br.com.supermidia.pessoa.usuario.domain.Usuario;
import br.com.supermidia.pessoa.usuario.infra.UsuarioRepository;

@Service
public class AuthenticationService {
	private final UsuarioRepository usuarioRepository;

	private final AuthenticationManager authenticationManager;

	public AuthenticationService(UsuarioRepository usuarioRepository, AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
		this.usuarioRepository = usuarioRepository;
	}

	public Usuario authenticate(LoginUserDto input) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));

		return usuarioRepository.findByFisicaEmail(input.getEmail()).orElseThrow();
	}
}
