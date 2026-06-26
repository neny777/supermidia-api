package br.com.supermidia.pessoa.fornecedor.app;

import br.com.supermidia.handler.NotFoundException;

public class FornecedorNotFoundException extends NotFoundException {
	private static final long serialVersionUID = 1L;

	public FornecedorNotFoundException(String message) {
		super(message);
	}
}

