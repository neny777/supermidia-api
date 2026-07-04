package br.com.supermidia.produto.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import br.com.supermidia.calculo.domain.CodigoParametroCalculo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Contribuição de uma opção ativa aos parâmetros dos componentes BASE do produto:
 * soma {@code valor} a todo parâmetro base de mesmo {@code codigo}.
 * Ex.: opção "Com ilhós" → ACRESCIMO_ALTURA +6 e ACRESCIMO_LARGURA +6 (bainha).
 */
@Entity
@Table(name = "produtos_opcoes_contribuicoes")
public class ProdutoOpcaoContribuicao {

	@Id
	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "id", columnDefinition = "BINARY(16)")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "opcao_id", nullable = false, columnDefinition = "BINARY(16)")
	private ProdutoOpcao opcao;

	@Enumerated(EnumType.STRING)
	@Column(name = "codigo", nullable = false, length = 30)
	private CodigoParametroCalculo codigo;

	@Column(name = "valor", nullable = false, precision = 12, scale = 4)
	private BigDecimal valor;

	@PrePersist
	public void prePersist() {
		if (this.id == null) {
			this.id = UUID.randomUUID();
		}
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public ProdutoOpcao getOpcao() {
		return opcao;
	}

	public void setOpcao(ProdutoOpcao opcao) {
		this.opcao = opcao;
	}

	public CodigoParametroCalculo getCodigo() {
		return codigo;
	}

	public void setCodigo(CodigoParametroCalculo codigo) {
		this.codigo = codigo;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
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
		ProdutoOpcaoContribuicao other = (ProdutoOpcaoContribuicao) obj;
		return Objects.equals(id, other.id);
	}
}
