package br.com.supermidia.venda.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.supermidia.venda.domain.StatusVenda;

public class VendaResponse {
	private UUID id;
	private Long numero;
	private UUID clienteId;
	private StatusVenda status;
	private boolean vencido;
	private boolean editavel;
	private LocalDateTime dataCriacao;
	private LocalDate validoAte;
	private BigDecimal total;
	private String formaPagamento;
	private String prazoEntrega;
	private String observacoes;
	private List<VendaItemResponse> itens = new ArrayList<>();

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	public LocalDate getValidoAte() {
		return validoAte;
	}

	public void setValidoAte(LocalDate validoAte) {
		this.validoAte = validoAte;
	}

	public UUID getClienteId() {
		return clienteId;
	}

	public void setClienteId(UUID clienteId) {
		this.clienteId = clienteId;
	}

	public StatusVenda getStatus() {
		return status;
	}

	public void setStatus(StatusVenda status) {
		this.status = status;
	}

	public boolean isVencido() {
		return vencido;
	}

	public void setVencido(boolean vencido) {
		this.vencido = vencido;
	}

	public boolean isEditavel() {
		return editavel;
	}

	public void setEditavel(boolean editavel) {
		this.editavel = editavel;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(String formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public String getPrazoEntrega() {
		return prazoEntrega;
	}

	public void setPrazoEntrega(String prazoEntrega) {
		this.prazoEntrega = prazoEntrega;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public List<VendaItemResponse> getItens() {
		return itens;
	}

	public void setItens(List<VendaItemResponse> itens) {
		this.itens = itens;
	}
}
