package br.com.supermidia.pessoa.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public final class UniqueRuleEngine {

	private UniqueRuleEngine() {
	}

	public static List<String> validate(UUID id, List<UniqueFieldRule> rules) {
		List<String> erros = new ArrayList<>();
		for (UniqueFieldRule rule : rules) {
			Function<String, Boolean> verificacao = id == null ? rule.existsOnCreate() : rule.existsOnUpdate();
			UniqueValidationSupport.uniquenessValidation(rule.valor(), id, rule.campo(), verificacao, erros);
		}
		return erros;
	}
}
