package br.com.supermidia.materia.app;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MateriaNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public MateriaNotFoundException(String message) {
        super(message);
    }
}