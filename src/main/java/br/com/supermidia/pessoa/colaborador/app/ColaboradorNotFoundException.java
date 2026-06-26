package br.com.supermidia.pessoa.colaborador.app;

import br.com.supermidia.handler.NotFoundException;

public class ColaboradorNotFoundException extends NotFoundException {
	private static final long serialVersionUID = 1L;

	public ColaboradorNotFoundException(String message) {
		super(message);
	}
}

