package br.com.supermidia.pessoa.shared;

import java.util.function.Function;

public record UniqueFieldRule(String campo, String valor, Function<String, Boolean> existsOnCreate,
		Function<String, Boolean> existsOnUpdate) {
}
