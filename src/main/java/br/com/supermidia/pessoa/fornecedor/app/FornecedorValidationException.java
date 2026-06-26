package br.com.supermidia.pessoa.fornecedor.app;

public class FornecedorValidationException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public FornecedorValidationException(String message) {
		super(message);
	}
}

