package br.com.supermidia.venda.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import br.com.supermidia.pessoa.cliente.domain.Cliente;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Documento de venda. Vive como Orçamento ou Ordem de Serviço conforme o
 * {@link StatusVenda} — é um único registro com fases, não dois cadastros.
 * Os itens guardam uma "memória de cálculo" congelada (ver {@link ItemVenda}).
 */
@Entity
@Table(name = "vendas")
public class Venda {

	/** Validade padrão do orçamento em dias (futuro: configuração global do sistema). */
	public static final int VALIDADE_ORCAMENTO_DIAS = 15;

	/** Janela de edição/exclusão após a criação, em horas (futuro: configuração global). */
	public static final int EDICAO_HORAS = 1;

	@Id
	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "id", columnDefinition = "BINARY(16)")
	private UUID id;

	// Cliente do documento (define atacado/varejo pela categoria). Anulável por ora;
	// obrigatoriedade será garantida na camada de aplicação (fatia 2).
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cliente_id", columnDefinition = "BINARY(16)")
	private Cliente cliente;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private StatusVenda status = StatusVenda.ORCAMENTO;

	@Column(name = "data_criacao", nullable = false)
	private LocalDateTime dataCriacao;

	@OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("id")
	private List<ItemVenda> itens = new ArrayList<>();

	@Column(name = "total", precision = 12, scale = 2)
	private BigDecimal total;

	@PrePersist
	public void prePersist() {
		if (this.id == null) {
			this.id = UUID.randomUUID();
		}
		if (this.dataCriacao == null) {
			this.dataCriacao = LocalDateTime.now();
		}
		if (this.status == null) {
			this.status = StatusVenda.ORCAMENTO;
		}
	}

	/** Recalcula o total somando o preço final de cada item. */
	public void recalcularTotal() {
		this.total = itens.stream()
				.map(ItemVenda::getPrecoFinal)
				.filter(Objects::nonNull)
				.reduce(BigDecimal.ZERO, BigDecimal::add)
				.setScale(2, RoundingMode.HALF_UP);
	}

	/**
	 * Dentro da janela de arrependimento: orçamento OU OS podem ser editados/
	 * excluídos na primeira hora (fase de testes). Futuro: OS com pagamento
	 * recebido ou produção iniciada será bloqueada.
	 */
	public boolean isEditavel() {
		return (status == StatusVenda.ORCAMENTO || status == StatusVenda.ORDEM_SERVICO)
				&& dataCriacao != null && dataCriacao.plusHours(EDICAO_HORAS).isAfter(LocalDateTime.now());
	}

	/** Orçamento vencido = passou da validade (só se aplica a orçamentos). */
	public boolean isVencido() {
		return status == StatusVenda.ORCAMENTO && dataCriacao != null
				&& dataCriacao.plusDays(VALIDADE_ORCAMENTO_DIAS).isBefore(LocalDateTime.now());
	}

	/** Converte o orçamento em ordem de serviço. */
	public void converterParaOrdemServico() {
		if (status != StatusVenda.ORCAMENTO) {
			throw new IllegalStateException("Somente orçamentos podem ser convertidos em ordem de serviço.");
		}
		if (isVencido()) {
			throw new IllegalStateException("Orçamento vencido: recalcule antes de converter em ordem de serviço.");
		}
		this.status = StatusVenda.ORDEM_SERVICO;
	}

	public void cancelar() {
		if (status == StatusVenda.CANCELADO) {
			throw new IllegalStateException("A venda já está cancelada.");
		}
		this.status = StatusVenda.CANCELADO;
	}

	/** Reinicia a contagem de validade (usado ao recalcular um orçamento). */
	public void renovarValidade() {
		this.dataCriacao = LocalDateTime.now();
	}

	public void setItens(List<ItemVenda> itens) {
		this.itens.clear();
		if (itens == null) {
			return;
		}
		itens.forEach(this::addItem);
	}

	public void addItem(ItemVenda item) {
		item.setVenda(this);
		this.itens.add(item);
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public StatusVenda getStatus() {
		return status;
	}

	public void setStatus(StatusVenda status) {
		this.status = status;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public List<ItemVenda> getItens() {
		return itens;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Venda other = (Venda) obj;
		return Objects.equals(id, other.id);
	}
}
