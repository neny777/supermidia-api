package br.com.supermidia.calculo.api.dto;
import java.util.UUID;

import br.com.supermidia.calculo.domain.BaseOperacionalCalculo;
import br.com.supermidia.calculo.domain.TipoCalculo;

public class CalculoResponse {

	private UUID id;
	private String nome;
	private TipoCalculo tipoCalculo;
	private BaseOperacionalCalculo baseOperacional;
	private boolean permiteOverrideParametro;
	private boolean permiteOverrideResultado;

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
