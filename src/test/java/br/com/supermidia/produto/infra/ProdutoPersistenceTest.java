package br.com.supermidia.produto.infra;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import br.com.supermidia.calculo.domain.BaseOperacionalCalculo;
import br.com.supermidia.calculo.domain.Calculo;
import br.com.supermidia.calculo.domain.CodigoParametroCalculo;
import br.com.supermidia.calculo.domain.TipoCalculo;
import br.com.supermidia.materia.domain.Materia;
import br.com.supermidia.materia.domain.UnidadeMateria;
import br.com.supermidia.produto.domain.Produto;
import br.com.supermidia.produto.domain.ProdutoComponente;
import br.com.supermidia.produto.domain.ProdutoComponenteParametro;
import br.com.supermidia.produto.domain.ProdutoGrupoOpcao;
import br.com.supermidia.produto.domain.ProdutoMedida;
import br.com.supermidia.produto.domain.ProdutoOpcao;
import br.com.supermidia.produto.domain.ProdutoOpcaoContribuicao;
import br.com.supermidia.produto.domain.ProdutoParametroVinculoMedida;
import br.com.supermidia.produto.domain.TipoItemComponente;
import br.com.supermidia.servico.domain.Servico;
import br.com.supermidia.servico.domain.UnidadeServico;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ProdutoPersistenceTest {

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private TestEntityManager em;

	@Test
	void devePersistirERecarregarOGrafoCompletoDoProduto() {
		Calculo calculoArea = calculo("AREA COM ACRESCIMOS", TipoCalculo.AREA_COM_ACRESCIMOS_E_FATOR);
		Calculo calculoPerimetro = calculo("PERIMETRO", TipoCalculo.PERIMETRO_BASE);
		Servico refile = servico("REFILE LONA");
		em.persist(calculoArea);
		em.persist(calculoPerimetro);
		em.persist(refile);

		Produto produto = new Produto();
		produto.setNome("IMPRESSAO EM LONA");

		// medida BORDA com limites
		ProdutoMedida borda = new ProdutoMedida();
		borda.setNome("BORDA");
		borda.setUnidade("cm");
		borda.setValorPadrao(BigDecimal.ZERO);
		borda.setMinimo(BigDecimal.ZERO);
		borda.setMaximo(new BigDecimal("100"));
		produto.addMedida(borda);

		// componente base com SLOT (grupo LONAS) e parâmetro com vínculo BORDA ×2
		ProdutoComponente slot = new ProdutoComponente();
		slot.setTipoItem(TipoItemComponente.MATERIA);
		slot.setGrupoMateriaSlot("LONAS");
		slot.setCalculo(calculoArea);
		ProdutoComponenteParametro acrescimo = new ProdutoComponenteParametro();
		acrescimo.setCodigo(CodigoParametroCalculo.ACRESCIMO_ALTURA);
		ProdutoParametroVinculoMedida vinculo = new ProdutoParametroVinculoMedida();
		vinculo.setMedidaNome("BORDA");
		vinculo.setMultiplicador(new BigDecimal("2"));
		acrescimo.addVinculo(vinculo);
		slot.addParametro(acrescimo);
		produto.addComponente(slot);

		// grupo de opções REFILE com uma opção que carrega um serviço e contribuição
		ProdutoGrupoOpcao grupo = new ProdutoGrupoOpcao();
		grupo.setNome("REFILE");
		ProdutoOpcao opcao = new ProdutoOpcao();
		opcao.setNome("COM REFILE");
		ProdutoComponente servicoRefile = new ProdutoComponente();
		servicoRefile.setTipoItem(TipoItemComponente.SERVICO);
		servicoRefile.setServico(refile);
		servicoRefile.setCalculo(calculoPerimetro);
		opcao.addComponente(servicoRefile);
		ProdutoOpcaoContribuicao contribuicao = new ProdutoOpcaoContribuicao();
		contribuicao.setCodigo(CodigoParametroCalculo.ACRESCIMO_ALTURA);
		contribuicao.setValor(new BigDecimal("6"));
		opcao.addContribuicao(contribuicao);
		grupo.addOpcao(opcao);
		produto.addGrupoOpcao(grupo);

		UUID id = produtoRepository.saveAndFlush(produto).getId();
		em.clear();

		Produto recarregado = produtoRepository.findById(id).orElseThrow();
		assertThat(recarregado.getMedidas()).hasSize(1);
		assertThat(recarregado.getMedidas().get(0).getNome()).isEqualTo("BORDA");
		assertThat(recarregado.getMedidas().get(0).getMaximo()).isEqualByComparingTo("100");

		assertThat(recarregado.getComponentes()).hasSize(1);
		ProdutoComponente slotRecarregado = recarregado.getComponentes().get(0);
		assertThat(slotRecarregado.isSlot()).isTrue();
		assertThat(slotRecarregado.getGrupoMateriaSlot()).isEqualTo("LONAS");
		assertThat(slotRecarregado.getParametros()).hasSize(1);
		assertThat(slotRecarregado.getParametros().get(0).getVinculos()).hasSize(1);
		assertThat(slotRecarregado.getParametros().get(0).getVinculos().get(0).getMultiplicador())
				.isEqualByComparingTo("2");

		assertThat(recarregado.getGruposOpcoes()).hasSize(1);
		ProdutoGrupoOpcao grupoRecarregado = recarregado.getGruposOpcoes().get(0);
		assertThat(grupoRecarregado.getNome()).isEqualTo("REFILE");
		assertThat(grupoRecarregado.getOpcoes()).hasSize(1);
		ProdutoOpcao opcaoRecarregada = grupoRecarregado.getOpcoes().get(0);
		assertThat(opcaoRecarregada.getComponentes()).hasSize(1);
		assertThat(opcaoRecarregada.getComponentes().get(0).getServico().getNome()).isEqualTo("REFILE LONA");
		assertThat(opcaoRecarregada.getContribuicoes()).hasSize(1);
		assertThat(opcaoRecarregada.getContribuicoes().get(0).getValor()).isEqualByComparingTo("6");
	}

	private Calculo calculo(String nome, TipoCalculo tipo) {
		Calculo calculo = new Calculo();
		calculo.setNome(nome);
		calculo.setTipoCalculo(tipo);
		calculo.setBaseOperacional(BaseOperacionalCalculo.AREA);
		return calculo;
	}

	private Servico servico(String nome) {
		Servico servico = new Servico();
		servico.setNome(nome);
		servico.setUnidade(UnidadeServico.M);
		servico.setPreco(new BigDecimal("1.00"));
		return servico;
	}

	@SuppressWarnings("unused")
	private Materia materia(String nome, String grupo) {
		Materia materia = new Materia();
		materia.setNome(nome);
		materia.setGrupo(grupo);
		materia.setUnidade(UnidadeMateria.M2);
		materia.setPreco(new BigDecimal("8.50"));
		return materia;
	}
}
