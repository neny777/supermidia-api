package br.com.supermidia.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CNPJValidator implements ConstraintValidator<CNPJ, String> {

	@Override
	public boolean isValid(String cnpj, ConstraintValidatorContext context) {
		if (cnpj == null || cnpj.isEmpty())
			return true; // CNPJ nulo/vazio é permitido

		// Remove caracteres não numéricos
		cnpj = cnpj.replaceAll("\\D", "");

		// Verifica se tem 14 dígitos e não são números iguais (111.111...)
		if (cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}"))
			return false;

		// Calcula os dígitos verificadores
		return isValidCNPJ(cnpj);
	}

	private boolean isValidCNPJ(String cnpj) {
		int[] weights1 = { 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
		int[] weights2 = { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
		int sum = 0;

		for (int i = 0; i < 12; i++) {
			sum += (cnpj.charAt(i) - '0') * weights1[i];
		}
		int firstDigit = sum % 11 < 2 ? 0 : 11 - (sum % 11);

		if (firstDigit != cnpj.charAt(12) - '0')
			return false;

		sum = 0;
		for (int i = 0; i < 13; i++) {
			sum += (cnpj.charAt(i) - '0') * weights2[i];
		}
		int secondDigit = sum % 11 < 2 ? 0 : 11 - (sum % 11);

		return secondDigit == cnpj.charAt(13) - '0';
	}
}
