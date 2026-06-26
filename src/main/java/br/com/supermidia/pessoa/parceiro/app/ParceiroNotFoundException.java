package br.com.supermidia.pessoa.parceiro.app;

import br.com.supermidia.handler.NotFoundException;

public class ParceiroNotFoundException extends NotFoundException {
	private static final long serialVersionUID = 1L;

	public ParceiroNotFoundException(String message) {
		super(message);
	}
}

