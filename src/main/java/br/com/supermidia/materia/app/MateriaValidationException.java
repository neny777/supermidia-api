package br.com.supermidia.materia.app;

public class MateriaValidationException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public MateriaValidationException(String message) {
		super(message);
	}
}
