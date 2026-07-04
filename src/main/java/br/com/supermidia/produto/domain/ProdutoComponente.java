package br.com.supermidia.produto.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import br.com.supermidia.calculo.domain.Calculo;
import br.com.supermidia.converter.UppercaseConverter;
import br.com.supermidia.materia.domain.Materia;
import br.com.supermidia.servico.domain.Servico;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Um insumo do produto (matéria ou serviço) com sua regra de cálculo.
 * Pertence ao produto (componente BASE — forma a margem) ou a uma
 * {@link ProdutoOpcao} (componente de opção — soma custo e herda a margem).
 *
 * Matéria pode ser fixa ({@code materia}) ou um SLOT ({@code grupoMateriaSlot}):
 * no slot, a matéria é escolhida no orçamento dentre as do grupo.
 */
@Entity
@Table(name = "produtos_componentes")
public class ProdutoComponente {

	@Id
	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "id", columnDefinition = "BINARY(16)")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "produto_id", columnDefinition = "BINARY(16)")
	private Produto produto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "opcao_id", columnDefinition = "BINARY(16)")
	private ProdutoOpcao opcao;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_item", nullable = false, length = 10)
	private TipoItemComponente tipoItem;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "materia_id", columnDefinition = "BINARY(16)")
	private Materia materia;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "servico_id", columnDefinition = "BINARY(16)")
	private Servico servico;

	@Column(name = "grupo_materia_slot", length = 40)
	@Convert(converter = UppercaseConverter.class)
	private String grupoMateriaSlot;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "calculo_id", nullable = false, columnDefinition = "BINARY(16)")
	private Calculo calculo;

	@OneToMany(mappedBy = "componente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<ProdutoComponenteParametro> parametros = new ArrayList<>();

	@PrePersist
	public void prePersist() {
		if (this.id == null) {
			this.id = UUID.randomUUID();
		}
	}

	public boolean isSlot() {
		return grupoMateriaSlot != null && !grupoMateriaSlot.isBlank();
	}

	public void setParametros(List<ProdutoComponenteParametro> parametros) {
		this.parametros.clear();
		if (parametros == null) {
			return;
		}
		parametros.forEach(this::addParametro);
	}

	public void addParametro(ProdutoComponenteParametro parametro) {
		parametro.setComponente(this);
		this.parametros.add(parametro);
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

	public ProdutoOpcao getOpcao() {
		return opcao;
	}

	public void setOpcao(ProdutoOpcao opcao) {
		this.opcao = opcao;
	}

	public TipoItemComponente getTipoItem() {
		return tipoItem;
	}

	public void setTipoItem(TipoItemComponente tipoItem) {
		this.tipoItem = tipoItem;
	}

	public Materia getMateria() {
		return materia;
	}

	public void setMateria(Materia materia) {
		this.materia = materia;
	}

	public Servico getServico() {
		return servico;
	}

	public void setServico(Servico servico) {
		this.servico = servico;
	}

	public String getGrupoMateriaSlot() {
		return grupoMateriaSlot;
	}

	public void setGrupoMateriaSlot(String grupoMateriaSlot) {
		this.grupoMateriaSlot = grupoMateriaSlot;
	}

	public Calculo getCalculo() {
		return calculo;
	}

	public void setCalculo(Calculo calculo) {
		this.calculo = calculo;
	}

	public List<ProdutoComponenteParametro> getParametros() {
		return parametros;
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
		ProdutoComponente other = (ProdutoComponente) obj;
		return Objects.equals(id, other.id);
	}
}
