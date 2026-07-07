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
 * Uma opção escolhível de um {@link ProdutoGrupoOpcao}. Quando ativa no orçamento,
 * seus componentes entram no cálculo (herdando a margem, sem formá-la) e suas
 * contribuições somam-se aos parâmetros de mesmo código dos componentes BASE
 * (ex.: Ilhós → +6cm nos acréscimos da lona).
 */
@Entity
@Table(name = "produtos_opcoes")
public class ProdutoOpcao {

	@Id
	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "id", columnDefinition = "BINARY(16)")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "grupo_id", nullable = false, columnDefinition = "BINARY(16)")
	private ProdutoGrupoOpcao grupo;

	@Column(name = "nome", nullable = false, length = 60)
	@Convert(converter = UppercaseConverter.class)
	private String nome;

	@OneToMany(mappedBy = "opcao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderBy("id")
	private List<ProdutoComponente> componentes = new ArrayList<>();

	@OneToMany(mappedBy = "opcao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderBy("id")
	private List<ProdutoOpcaoContribuicao> contribuicoes = new ArrayList<>();

	@PrePersist
	public void prePersist() {
		if (this.id == null) {
			this.id = UUID.randomUUID();
		}
	}

	public void setComponentes(List<ProdutoComponente> componentes) {
		this.componentes.clear();
		if (componentes == null) {
			return;
		}
		componentes.forEach(this::addComponente);
	}

	public void addComponente(ProdutoComponente componente) {
		componente.setOpcao(this);
		this.componentes.add(componente);
	}

	public void setContribuicoes(List<ProdutoOpcaoContribuicao> contribuicoes) {
		this.contribuicoes.clear();
		if (contribuicoes == null) {
			return;
		}
		contribuicoes.forEach(this::addContribuicao);
	}

	public void addContribuicao(ProdutoOpcaoContribuicao contribuicao) {
		contribuicao.setOpcao(this);
		this.contribuicoes.add(contribuicao);
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public ProdutoGrupoOpcao getGrupo() {
		return grupo;
	}

	public void setGrupo(ProdutoGrupoOpcao grupo) {
		this.grupo = grupo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<ProdutoComponente> getComponentes() {
		return componentes;
	}

	public List<ProdutoOpcaoContribuicao> getContribuicoes() {
		return contribuicoes;
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
		ProdutoOpcao other = (ProdutoOpcao) obj;
		return Objects.equals(id, other.id);
	}
}
