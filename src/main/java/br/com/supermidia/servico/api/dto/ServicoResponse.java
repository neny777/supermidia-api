package br.com.supermidia.servico.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.supermidia.servico.domain.UnidadeServico;

public class ServicoResponse {

	private UUID id;
	private String nome;
	private UnidadeServico unidade;
	private BigDecimal preco;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public UnidadeServico getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadeServico unidade) {
		this.unidade = unidade;
	}

	public BigDecimal getPreco() {
		return preco;
	}

	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}
}
