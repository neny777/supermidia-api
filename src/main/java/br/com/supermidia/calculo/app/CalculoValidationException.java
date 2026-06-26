package br.com.supermidia.calculo.app;

public class CalculoValidationException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public CalculoValidationException(String message) {
		super(message);
	}
}
