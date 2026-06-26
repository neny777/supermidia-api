package br.com.supermidia.produto.app;

public class ProdutoValidationException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public ProdutoValidationException(String message) {
		super(message);
	}
}
