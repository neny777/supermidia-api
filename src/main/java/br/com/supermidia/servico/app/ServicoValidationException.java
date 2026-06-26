package br.com.supermidia.servico.app;

public class ServicoValidationException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public ServicoValidationException(String message) {
		super(message);
	}
}
