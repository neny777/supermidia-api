package br.com.supermidia.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public final class Permissoes {
	public static final String CLIENTES = "clientes";
	public static final String COLABORADORES = "colaboradores";
	public static final String FORNECEDORES = "fornecedores";
	public static final String PARCEIROS = "parceiros";
	public static final String GRUPOS = "grupos";
	public static final String MATERIAS = "materias";
	public static final String SERVICOS = "servicos";
	public static final String PRODUTOS = "produtos";
	public static final String VENDAS = "vendas";
	// Ver custos, margens e detalhamento das vendas (informação sensível).
	public static final String CUSTOS = "custos";
	// Alterar os parâmetros globais do sistema (validade, janela de edição, margens).
	public static final String CONFIGURACOES = "configuracoes";
	public static final String USUARIOS = "usuarios";

	public static final Set<String> TODAS = Set.of(CLIENTES, COLABORADORES, FORNECEDORES, PARCEIROS, GRUPOS,
			MATERIAS, SERVICOS, PRODUTOS, VENDAS, CUSTOS, CONFIGURACOES, USUARIOS);

	private Permissoes() {
	}

	public static Set<String> normalizar(Collection<String> permissoes) {
		if (permissoes == null) {
			return Collections.emptySet();
		}
		return permissoes.stream().filter(valor -> valor != null && !valor.isBlank())
				.map(valor -> valor.trim().toLowerCase(Locale.ROOT)).map(valor -> valor.replaceFirst("^role_", ""))
				.collect(Collectors.toSet());
	}

	public static Set<String> invalidas(Collection<String> permissoes) {
		Set<String> normalizadas = normalizar(permissoes);
		return normalizadas.stream().filter(valor -> !TODAS.contains(valor))
				.collect(Collectors.toCollection(TreeSet::new));
	}
}
