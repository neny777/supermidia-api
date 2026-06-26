package br.com.supermidia.pessoa.parceiro.app;

public class ParceiroValidationException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public ParceiroValidationException(String message) {
		super(message);
	}
}

