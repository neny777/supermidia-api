package br.com.supermidia.pessoa.shared;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public final class UniqueValidationSupport {

	private UniqueValidationSupport() {
	}

	public static void uniquenessValidation(String valor, UUID id, String campo,
			Function<String, Boolean> verificaUnicidade, List<String> erros) {
		if (valor == null || valor.isBlank()) {
			return;
		}
		boolean duplicado = verificaUnicidade.apply(valor);
		if (duplicado) {
			erros.add(campo.toUpperCase() + " " + valor + " já está cadastrado");
		}
	}
}
