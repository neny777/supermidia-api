package br.com.supermidia.pessoa.cliente.app;

import br.com.supermidia.handler.NotFoundException;

public class ClienteNotFoundException extends NotFoundException {
	private static final long serialVersionUID = 1L;

	public ClienteNotFoundException(String message) {
		super(message);
	}
}

