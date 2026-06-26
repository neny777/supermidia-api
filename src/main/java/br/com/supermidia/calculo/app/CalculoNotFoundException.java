package br.com.supermidia.calculo.app;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CalculoNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CalculoNotFoundException(String message) {
		super(message);
	}
}
