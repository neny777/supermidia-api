package br.com.supermidia.produto.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import br.com.supermidia.converter.UppercaseConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Produto-template: declara o que o orçamento pergunta e como se calcula.
 * - medidas: entradas extras do orçamento (ex.: BORDA), com limites;
 * - componentes: insumos BASE (formam a margem; matéria fixa ou slot por grupo);
 * - gruposOpcoes: escolhas do orçamento (acabamentos/seleções — herdam a margem).
 */
@Entity
@Table(name = "produtos")
public class Produto {

	@Id
	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "id", columnDefinition = "BINARY(16)")
	private UUID id;

	@Column(name = "nome", nullable = false, length = 140, unique = true)
	@Convert(converter = UppercaseConverter.class)
	private String nome;

	// @OrderBy: ordem ESTÁVEL entre carregamentos — as telas endereçam itens por índice.
	@OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@OrderBy("id")
	private List<ProdutoMedida> medidas = new ArrayList<>();

	@OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@OrderBy("id")
	private List<ProdutoComponente> componentes = new ArrayList<>();

	@OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@OrderBy("id")
	private List<ProdutoGrupoOpcao> gruposOpcoes = new ArrayList<>();

	@PrePersist
	public void prePersist() {
		if (this.id == null) {
			this.id = UUID.randomUUID();
		}
	}

	public void setMedidas(List<ProdutoMedida> medidas) {
		this.medidas.clear();
		if (medidas == null) {
			return;
		}
		medidas.forEach(this::addMedida);
	}

	public void addMedida(ProdutoMedida medida) {
		medida.setProduto(this);
		this.medidas.add(medida);
	}

	public void setComponentes(List<ProdutoComponente> componentes) {
		this.componentes.clear();
		if (componentes == null) {
			return;
		}
		componentes.forEach(this::addComponente);
	}

	public void addComponente(ProdutoComponente componente) {
		componente.setProduto(this);
		this.componentes.add(componente);
	}

	public void setGruposOpcoes(List<ProdutoGrupoOpcao> gruposOpcoes) {
		this.gruposOpcoes.clear();
		if (gruposOpcoes == null) {
			return;
		}
		gruposOpcoes.forEach(this::addGrupoOpcao);
	}

	public void addGrupoOpcao(ProdutoGrupoOpcao grupo) {
		grupo.setProduto(this);
		this.gruposOpcoes.add(grupo);
	}

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

	public List<ProdutoMedida> getMedidas() {
		return medidas;
	}

	public List<ProdutoComponente> getComponentes() {
		return componentes;
	}

	public List<ProdutoGrupoOpcao> getGruposOpcoes() {
		return gruposOpcoes;
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
		Produto other = (Produto) obj;
		return Objects.equals(id, other.id);
	}
}
