package br.com.supermidia.produto.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import br.com.supermidia.converter.UppercaseConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Grupo de escolha do produto no orçamento (unifica acabamento e seleção).
 * Escolha única entre as opções; se não for obrigatório, "Nenhum" é permitido.
 * Ex.: "RECORTE" [Reto | Contorno], "ILHÓS" [Com ilhós].
 */
@Entity
@Table(name = "produtos_grupos_opcoes")
public class ProdutoGrupoOpcao {

	@Id
	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "id", columnDefinition = "BINARY(16)")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "produto_id", nullable = false, columnDefinition = "BINARY(16)")
	private Produto produto;

	@Column(name = "nome", nullable = false, length = 60)
	@Convert(converter = UppercaseConverter.class)
	private String nome;

	@Column(name = "obrigatorio", nullable = false)
	private boolean obrigatorio;

	@OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderBy("id")
	private List<ProdutoOpcao> opcoes = new ArrayList<>();

	@PrePersist
	public void prePersist() {
		if (this.id == null) {
			this.id = UUID.randomUUID();
		}
	}

	public void setOpcoes(List<ProdutoOpcao> opcoes) {
		this.opcoes.clear();
		if (opcoes == null) {
			return;
		}
		opcoes.forEach(this::addOpcao);
	}

	public void addOpcao(ProdutoOpcao opcao) {
		opcao.setGrupo(this);
		this.opcoes.add(opcao);
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public boolean isObrigatorio() {
		return obrigatorio;
	}

	public void setObrigatorio(boolean obrigatorio) {
		this.obrigatorio = obrigatorio;
	}

	public List<ProdutoOpcao> getOpcoes() {
		return opcoes;
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
		ProdutoGrupoOpcao other = (ProdutoGrupoOpcao) obj;
		return Objects.equals(id, other.id);
	}
}
