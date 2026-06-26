package br.com.supermidia.pessoa.cliente.app;

public class ClienteValidationException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public ClienteValidationException(String message) {
		super(message);
	}
}

