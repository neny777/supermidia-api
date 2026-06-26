package br.com.supermidia.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CPFValidator implements ConstraintValidator<CPF, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.isEmpty()) return true; // CPF nulo/vazio é permitido

        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("\\D", "");

        // Verifica se tem 11 dígitos e não são números iguais (111.111...)
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        // Calcula os dígitos verificadores
        return isValidCPF(cpf);
    }

    private boolean isValidCPF(String cpf) {
        int sum = 0, weight = 10;

        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * weight--;
        }

        int firstDigit = 11 - (sum % 11);
        if (firstDigit > 9) firstDigit = 0;

        if (firstDigit != cpf.charAt(9) - '0') return false;

        sum = 0; weight = 11;
        for (int i = 0; i < 10; i++) {
            sum += (cpf.charAt(i) - '0') * weight--;
        }

        int secondDigit = 11 - (sum % 11);
        if (secondDigit > 9) secondDigit = 0;

        return secondDigit == cpf.charAt(10) - '0';
    }
}
