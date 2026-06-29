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
import br.com.supermidia.pessoa.cliente.domain.Cliente.Categoria;
import br.com.supermidia.produto.api.dto.ProdutoCalculoItemResponse;
import br.com.supermidia.produto.api.dto.ProdutoCalculoRequest;
import br.com.supermidia.produto.api.dto.ProdutoCalculoResponse;
import br.com.supermidia.produto.domain.Produto;
import br.com.supermidia.produto.domain.ProdutoMateriaCalculo;
import br.com.supermidia.produto.domain.ProdutoMateriaParametroCalculo;
import br.com.supermidia.produto.domain.ProdutoServicoCalculo;
import br.com.supermidia.produto.domain.ProdutoServicoParametroCalculo;
import br.com.supermidia.servico.domain.Servico;
import br.com.supermidia.servico.domain.UnidadeServico;

/**
 * Prova de fogo da arquitetura: monta um produto "LONA" como template real
 * (composição de matérias + serviços + cálculos + parâmetros + markup) e
 * confere se o motor reproduz a composição esperada.
 *
 * Preços baseados na tabela legada (calculadoras/precos.php):
 * impressão 8,50 · lona padrão 8,50 · lona refile 1,00 · ilhós 0,15.
 *
 * Cenário: lona 1,00m (altura) x 2,00m (largura), quantidade 2, cliente REVENDA.
 */
@ExtendWith(MockitoExtension.class)
class ProdutoCalculoLonaCenarioTest {

	@Mock
	private ProdutoService produtoService;

	private ProdutoCalculoService calculoService;

	@BeforeEach
	void setUp() {
		calculoService = new ProdutoCalculoService(produtoService);
	}

	@Test
	void deveReproduzirOTemplateDeLonaComposto() {
		UUID produtoId = UUID.randomUUID();
		Produto lona = montarProdutoLona(produtoId);
		when(produtoService.findById(produtoId)).thenReturn(lona);

		ProdutoCalculoRequest request = request("100", "200", "2");
		request.setCategoria(Categoria.R);

		ProdutoCalculoResponse response = calculoService.calcular(produtoId, request);

		// --- Matérias ---
		// Lona: areaBase = (1,00 x 2,00) x 2 = 4 m² ; com fator 1,21 => 4,84 m² ; x 8,50 = 41,14
		ProdutoCalculoItemResponse lonaItem = item(response.getMateriais(), "LONA");
		assertThat(lonaItem.getQuantidadeCalculada()).isEqualByComparingTo("4.84");
		assertThat(lonaItem.getValorTotal()).isEqualByComparingTo("41.14");

		// Ilhós: perímetro = (1,00 + 2,00) x 2 x 2 = 12 m ; ceil(12 / 0,25) = 48 un ; x 0,15 = 7,20
		ProdutoCalculoItemResponse ilhosItem = item(response.getMateriais(), "ILHOS");
		assertThat(ilhosItem.getQuantidadeCalculada()).isEqualByComparingTo("48");
		assertThat(ilhosItem.getValorTotal()).isEqualByComparingTo("7.20");

		// --- Serviços ---
		// Impressão: 4 m² x 8,50 = 34,00
		ProdutoCalculoItemResponse impressaoItem = item(response.getServicos(), "IMPRESSAO");
		assertThat(impressaoItem.getQuantidadeCalculada()).isEqualByComparingTo("4");
		assertThat(impressaoItem.getValorTotal()).isEqualByComparingTo("34.00");

		// Refile: perímetro 12 m x 1,00 = 12,00
		ProdutoCalculoItemResponse refileItem = item(response.getServicos(), "REFILE");
		assertThat(refileItem.getQuantidadeCalculada()).isEqualByComparingTo("12");
		assertThat(refileItem.getValorTotal()).isEqualByComparingTo("12.00");

		// --- Totais ---
		assertThat(response.getTotalMateriais()).isEqualByComparingTo("48.34"); // 41,14 + 7,20
		assertThat(response.getTotalServicos()).isEqualByComparingTo("46.00"); // 34,00 + 12,00
		assertThat(response.getTotalGeral()).isEqualByComparingTo("94.34"); // custo total

		// --- Preços (margem automática) ---
		// razão serviço/material = 46,00/48,34 ≈ 0,9516 ; 1 - 0,9516 < 0,35 => trava no piso de 35%
		assertThat(response.getMarkupAtacado()).isEqualByComparingTo("35.00");
		assertThat(response.getPrecoAtacado()).isEqualByComparingTo("127.36"); // 94,34 x 1,35
		assertThat(response.getPrecoVarejo()).isEqualByComparingTo("176.34"); // 127,36 x 1,3846
		// Cliente REVENDA => preço sugerido é o de atacado
		assertThat(response.getPrecoSugerido()).isEqualByComparingTo("127.36");
	}

	// --- montagem do template ---

	private Produto montarProdutoLona(UUID produtoId) {
		Produto produto = new Produto();
		produto.setId(produtoId);
		produto.setNome("LONA 440 BASICA");

		ProdutoMateriaCalculo lona = materiaCalculo(
				materia("LONA", UnidadeMateria.M2, "8.50"),
				calculo("AREA COM FATOR", TipoCalculo.AREA_COM_FATOR, BaseOperacionalCalculo.AREA),
				materiaParametro(CodigoParametroCalculo.FATOR, "1.21"));

		ProdutoMateriaCalculo ilhos = materiaCalculo(
				materia("ILHOS", UnidadeMateria.UN, "0.15"),
				calculo("ILHOS", TipoCalculo.PERIMETRO_COM_ESPACAMENTO, BaseOperacionalCalculo.PERIMETRO),
				materiaParametro(CodigoParametroCalculo.ESPACAMENTO, "25"));

		ProdutoServicoCalculo impressao = servicoCalculo(
				servico("IMPRESSAO", UnidadeServico.M2, "8.50"),
				calculo("IMPRESSAO", TipoCalculo.AREA_BASE, BaseOperacionalCalculo.AREA));

		ProdutoServicoCalculo refile = servicoCalculo(
				servico("REFILE", UnidadeServico.M, "1.00"),
				calculo("REFILE", TipoCalculo.PERIMETRO_BASE, BaseOperacionalCalculo.PERIMETRO));

		produto.setMateriasCalculo(List.of(lona, ilhos));
		produto.setServicosCalculo(List.of(impressao, refile));
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

	private ProdutoMateriaParametroCalculo materiaParametro(CodigoParametroCalculo codigo, String valor) {
		ProdutoMateriaParametroCalculo parametro = new ProdutoMateriaParametroCalculo();
		parametro.setCodigo(codigo);
		parametro.setValor(new BigDecimal(valor));
		return parametro;
	}

	private ProdutoMateriaCalculo materiaCalculo(Materia materia, Calculo calculo,
			ProdutoMateriaParametroCalculo... parametros) {
		ProdutoMateriaCalculo item = new ProdutoMateriaCalculo();
		item.setMateria(materia);
		item.setCalculo(calculo);
		if (parametros.length > 0) {
			item.setParametros(List.of(parametros));
		}
		return item;
	}

	private ProdutoServicoCalculo servicoCalculo(Servico servico, Calculo calculo,
			ProdutoServicoParametroCalculo... parametros) {
		ProdutoServicoCalculo item = new ProdutoServicoCalculo();
		item.setServico(servico);
		item.setCalculo(calculo);
		if (parametros.length > 0) {
			item.setParametros(List.of(parametros));
		}
		return item;
	}

	private ProdutoCalculoRequest request(String altura, String largura, String quantidade) {
		ProdutoCalculoRequest request = new ProdutoCalculoRequest();
		request.setAltura(new BigDecimal(altura));
		request.setLargura(new BigDecimal(largura));
		request.setQuantidade(new BigDecimal(quantidade));
		return request;
	}
}
