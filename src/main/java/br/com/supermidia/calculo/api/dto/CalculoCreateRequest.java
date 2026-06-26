package br.com.supermidia.calculo.api.dto;

import br.com.supermidia.calculo.domain.BaseOperacionalCalculo;
import br.com.supermidia.calculo.domain.TipoCalculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CalculoCreateRequest {

	@NotBlank
	@Size(max = 140)
	private String nome;

	@NotNull
	private TipoCalculo tipoCalculo;

	@NotNull
	private BaseOperacionalCalculo baseOperacional;

	private boolean permiteOverrideParametro;

	private boolean permiteOverrideResultado;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public TipoCalculo getTipoCalculo() {
		return tipoCalculo;
	}

	public void setTipoCalculo(TipoCalculo tipoCalculo) {
		this.tipoCalculo = tipoCalculo;
	}

	public BaseOperacionalCalculo getBaseOperacional() {
		return baseOperacional;
	}

	public void setBaseOperacional(BaseOperacionalCalculo baseOperacional) {
		this.baseOperacional = baseOperacional;
	}

	public boolean isPermiteOverrideParametro() {
		return permiteOverrideParametro;
	}

	public void setPermiteOverrideParametro(boolean permiteOverrideParametro) {
		this.permiteOverrideParametro = permiteOverrideParametro;
	}

	public boolean isPermiteOverrideResultado() {
		return permiteOverrideResultado;
	}

	public void setPermiteOverrideResultado(boolean permiteOverrideResultado) {
		this.permiteOverrideResultado = permiteOverrideResultado;
	}
}
