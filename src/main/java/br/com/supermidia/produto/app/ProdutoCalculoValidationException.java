package br.com.supermidia.produto.app;

public class ProdutoCalculoValidationException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public ProdutoCalculoValidationException(String message) {
		super(message);
	}
}
