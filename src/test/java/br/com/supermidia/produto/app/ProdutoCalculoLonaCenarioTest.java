package br.com.supermidia.produto.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.supermidia.calculo.domain.BaseOperacionalCalculo;
import br.com.supermidia.calculo.domain.Calculo;
import br.com.supermidia.calculo.domain.CodigoParametroCalculo;
import br.com.supermidia.calculo.domain.TipoCalculo;
import br.com.supermidia.materia.domain.Materia;
import br.com.supermidia.materia.domain.UnidadeMateria;
import br.com.supermidia.materia.infra.MateriaRepository;
import br.com.supermidia.pessoa.cliente.domain.Cliente.Categoria;
import br.com.supermidia.produto.api.dto.ProdutoCalculoItemResponse;
import br.com.supermidia.produto.api.dto.ProdutoCalculoRequest;
import br.com.supermidia.produto.api.dto.ProdutoCalculoResponse;
import br.com.supermidia.produto.domain.Produto;
import br.com.supermidia.produto.domain.ProdutoComponente;
import br.com.supermidia.produto.domain.ProdutoComponenteParametro;
import br.com.supermidia.produto.domain.ProdutoMedida;
import br.com.supermidia.produto.domain.ProdutoParametroVinculoMedida;
import br.com.supermidia.produto.domain.TipoItemComponente;
import br.com.supermidia.servico.domain.Servico;
import br.com.supermidia.servico.domain.UnidadeServico;

/**
 * Prova de fogo da arquitetura: um produto "LONA" declarado como template
 * (componentes base + medida BORDA vinculada ×2) reproduz a composição
 * esperada. Preços da tabela legada (calculadoras/precos.php).
 *
 * Cenário: lona 1,00m x 2,00m, quantidade 2, cliente REVENDA.
 */
@ExtendWith(MockitoExtension.class)
class ProdutoCalculoLonaCenarioTest {

	@Mock
	private ProdutoService produtoService;

	@Mock
	private MateriaRepository materiaRepository;

	private ProdutoCalculoService calculoService;

	@BeforeEach
	void setUp() {
		calculoService = new ProdutoCalculoService(produtoService, materiaRepository);
	}

	@Test
	void deveReproduzirOTemplateDeLonaComposto() {
		UUID produtoId = UUID.randomUUID();
		Produto lona = montarProdutoLona(produtoId, null); // sem borda padrão
		when(produtoService.findById(produtoId)).thenReturn(lona);

		ProdutoCalculoRequest request = request("100", "200", "2");
		request.setCategoria(Categoria.R);

		ProdutoCalculoResponse response = calculoService.calcular(produtoId, request);

		// Lona: (1,00 x 2,00) x 2 = 4 m² x fator 1,21 => 4,84 m² x 8,50 = 41,14
		ProdutoCalculoItemResponse lonaItem = item(response.getMateriais(), "LONA");
		assertThat(lonaItem.getQuantidadeCalculada()).isEqualByComparingTo("4.84");
		assertThat(lonaItem.getValorTotal()).isEqualByComparingTo("41.14");

		// Ilhós: perímetro 12 m ; ceil(12 / 0,25) = 48 un x 0,15 = 7,20
		ProdutoCalculoItemResponse ilhosItem = item(response.getMateriais(), "ILHOS");
		assertThat(ilhosItem.getQuantidadeCalculada()).isEqualByComparingTo("48");
		assertThat(ilhosItem.getValorTotal()).isEqualByComparingTo("7.20");

		// Impressão: 4 m² x 8,50 = 34,00 · Refile: 12 m x 1,00 = 12,00
		assertThat(item(response.getServicos(), "IMPRESSAO").getValorTotal()).isEqualByComparingTo("34.00");
		assertThat(item(response.getServicos(), "REFILE").getValorTotal()).isEqualByComparingTo("12.00");

		// Totais e preços (margem trava no piso de 35%)
		assertThat(response.getTotalMateriais()).isEqualByComparingTo("48.34");
		assertThat(response.getTotalServicos()).isEqualByComparingTo("46.00");
		assertThat(response.getTotalGeral()).isEqualByComparingTo("94.34");
		assertThat(response.getMarkupAtacado()).isEqualByComparingTo("35.00");
		assertThat(response.getPrecoAtacado()).isEqualByComparingTo("127.36");
		assertThat(response.getPrecoVarejo()).isEqualByComparingTo("176.34");
		assertThat(response.getPrecoSugerido()).isEqualByComparingTo("127.36");
	}

	@Test
	void bordaDeclaradaDeveCrescerEmDobroPorDimensao() {
		// Borda padrão 10cm ×2 => lona vira (1,20 x 2,20) x 2 = 5,28 m² x 1,21 = 6,3888 m²
		UUID produtoId = UUID.randomUUID();
		Produto lona = montarProdutoLona(produtoId, "10");
		when(produtoService.findById(produtoId)).thenReturn(lona);

		ProdutoCalculoResponse response = calculoService.calcular(produtoId, request("100", "200", "2"));

		ProdutoCalculoItemResponse lonaItem = item(response.getMateriais(), "LONA");
		assertThat(lonaItem.getQuantidadeCalculada()).isEqualByComparingTo("6.3888");
		// impressão continua cobrindo só a área nominal
		assertThat(item(response.getServicos(), "IMPRESSAO").getQuantidadeCalculada()).isEqualByComparingTo("4");
	}

	// --- montagem do template ---

	private Produto montarProdutoLona(UUID produtoId, String bordaPadrao) {
		Produto produto = new Produto();
		produto.setId(produtoId);
		produto.setNome("LONA 440 BASICA");

		ProdutoMedida borda = new ProdutoMedida();
		borda.setNome("BORDA");
		borda.setUnidade("cm");
		borda.setValorPadrao(bordaPadrao == null ? BigDecimal.ZERO : new BigDecimal(bordaPadrao));
		produto.addMedida(borda);

		// Lona: área com acréscimos (borda ×2) e fator 1,21
		produto.addComponente(componenteMateria(materia("LONA", UnidadeMateria.M2, "8.50"),
				calculo("LONA COM ACRESCIMOS E FATOR", TipoCalculo.AREA_COM_ACRESCIMOS_E_FATOR, BaseOperacionalCalculo.AREA),
				paramComVinculoBorda(CodigoParametroCalculo.ACRESCIMO_ALTURA),
				paramComVinculoBorda(CodigoParametroCalculo.ACRESCIMO_LARGURA),
				param(CodigoParametroCalculo.FATOR, "1.21")));

		// Ilhós por espaçamento de 25cm
		produto.addComponente(componenteMateria(materia("ILHOS", UnidadeMateria.UN, "0.15"),
				calculo("ILHOS", TipoCalculo.PERIMETRO_COM_ESPACAMENTO, BaseOperacionalCalculo.PERIMETRO),
				param(CodigoParametroCalculo.ESPACAMENTO, "25")));

		// Impressão e refile
		produto.addComponente(componenteServico(servico("IMPRESSAO", UnidadeServico.M2, "8.50"),
				calculo("IMPRESSAO", TipoCalculo.AREA_BASE, BaseOperacionalCalculo.AREA)));
		produto.addComponente(componenteServico(servico("REFILE", UnidadeServico.M, "1.00"),
				calculo("REFILE", TipoCalculo.PERIMETRO_BASE, BaseOperacionalCalculo.PERIMETRO)));

		return produto;
	}

	private ProdutoCalculoItemResponse item(List<ProdutoCalculoItemResponse> itens, String nome) {
		return itens.stream().filter(i -> nome.equals(i.getNome())).findFirst()
				.orElseThrow(() -> new AssertionError("Item não encontrado: " + nome));
	}

	// --- fixtures ---

	private Materia materia(String nome, UnidadeMateria unidade, String preco) {
		Materia materia = new Materia();
		materia.setId(UUID.randomUUID());
		materia.setNome(nome);
		materia.setUnidade(unidade);
		materia.setPreco(new BigDecimal(preco));
		return materia;
	}

	private Servico servico(String nome, UnidadeServico unidade, String preco) {
		Servico servico = new Servico();
		servico.setId(UUID.randomUUID());
		servico.setNome(nome);
		servico.setUnidade(unidade);
		servico.setPreco(new BigDecimal(preco));
		return servico;
	}

	private Calculo calculo(String nome, TipoCalculo tipo, BaseOperacionalCalculo base) {
		Calculo calculo = new Calculo();
		calculo.setId(UUID.randomUUID());
		calculo.setNome(nome);
		calculo.setTipoCalculo(tipo);
		calculo.setBaseOperacional(base);
		return calculo;
	}

	private ProdutoComponenteParametro param(CodigoParametroCalculo codigo, String valor) {
		ProdutoComponenteParametro parametro = new ProdutoComponenteParametro();
		parametro.setCodigo(codigo);
		parametro.setValorConstante(new BigDecimal(valor));
		return parametro;
	}

	private ProdutoComponenteParametro paramComVinculoBorda(CodigoParametroCalculo codigo) {
		ProdutoComponenteParametro parametro = new ProdutoComponenteParametro();
		parametro.setCodigo(codigo);
		ProdutoParametroVinculoMedida vinculo = new ProdutoParametroVinculoMedida();
		vinculo.setMedidaNome("BORDA");
		vinculo.setMultiplicador(new BigDecimal("2"));
		parametro.addVinculo(vinculo);
		return parametro;
	}

	private ProdutoComponente componenteMateria(Materia materia, Calculo calculo,
			ProdutoComponenteParametro... parametros) {
		ProdutoComponente componente = new ProdutoComponente();
		componente.setTipoItem(TipoItemComponente.MATERIA);
		componente.setMateria(materia);
		componente.setCalculo(calculo);
		componente.setParametros(List.of(parametros));
		return componente;
	}

	private ProdutoComponente componenteServico(Servico servico, Calculo calculo,
			ProdutoComponenteParametro... parametros) {
		ProdutoComponente componente = new ProdutoComponente();
		componente.setTipoItem(TipoItemComponente.SERVICO);
		componente.setServico(servico);
		componente.setCalculo(calculo);
		componente.setParametros(List.of(parametros));
		return componente;
	}

	private ProdutoCalculoRequest request(String altura, String largura, String quantidade) {
		ProdutoCalculoRequest request = new ProdutoCalculoRequest();
		request.setAltura(new BigDecimal(altura));
		request.setLargura(new BigDecimal(largura));
		request.setQuantidade(new BigDecimal(quantidade));
		return request;
	}
}
