package br.com.supermidia.pessoa.usuario.app;

import br.com.supermidia.handler.NotFoundException;

public class UsuarioNotFoundException extends NotFoundException {
	private static final long serialVersionUID = 1L;

	public UsuarioNotFoundException(String message) {
		super(message);
	}
}
