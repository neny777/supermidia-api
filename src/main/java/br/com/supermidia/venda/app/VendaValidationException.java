package br.com.supermidia.venda.app;

public class VendaValidationException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public VendaValidationException(String message) {
		super(message);
	}
}
