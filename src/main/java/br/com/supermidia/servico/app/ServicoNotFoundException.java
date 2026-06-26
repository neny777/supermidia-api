package br.com.supermidia.servico.app;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ServicoNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ServicoNotFoundException(String message) {
		super(message);
	}
}
