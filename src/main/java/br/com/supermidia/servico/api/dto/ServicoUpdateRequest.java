package br.com.supermidia.servico.api.dto;

import java.math.BigDecimal;

import br.com.supermidia.servico.domain.UnidadeServico;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ServicoUpdateRequest {

	@NotBlank
	@Size(max = 140)
	private String nome;

	@NotNull
	private UnidadeServico unidade;

	@NotNull
	@DecimalMin(value = "0.01")
	private BigDecimal preco;

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
