package br.com.supermidia.produto.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import br.com.supermidia.produto.domain.ProdutoServicoCalculo;
import br.com.supermidia.produto.domain.ProdutoServicoParametroCalculo;
import br.com.supermidia.servico.domain.Servico;
import br.com.supermidia.servico.domain.UnidadeServico;

@ExtendWith(MockitoExtension.class)
class ProdutoCalculoServiceTest {

	@Mock
	private ProdutoService produtoService;

	private ProdutoCalculoService calculoService;

	@BeforeEach
	void setUp() {
		calculoService = new ProdutoCalculoService(produtoService);
	}

	@Test
	void deveCalcularUnidadeFixaMultiplicandoPelaQuantidadeDoItem() {
		// Bastão: 2 unidades por peça, a R$ 1,50 — pedido de 3 peças => 6 un => R$ 9,00
		UUID produtoId = UUID.randomUUID();
		Produto produto = produtoComServicoUnidadeFixa(produtoId, "BASTAO", "1.50", "2");
		when(produtoService.findById(produtoId)).thenReturn(produto);

		ProdutoCalculoResponse response = calculoService.calcular(produtoId, request("100", "80", "3"));

		assertThat(response.getServicos()).hasSize(1);
		ProdutoCalculoItemResponse item = response.getServicos().get(0);
		assertThat(item.getTipoCalculo()).isEqualTo(TipoCalculo.UNIDADE_FIXA);
		assertThat(item.getQuantidadeCalculada()).isEqualByComparingTo("6");
		assertThat(item.getValorTotal()).isEqualByComparingTo("9.00");
		assertThat(response.getTotalServicos()).isEqualByComparingTo("9.00");
		assertThat(response.getTotalMateriais()).isEqualByComparingTo("0.00");
		assertThat(response.getTotalGeral()).isEqualByComparingTo("9.00");
	}

	@Test
	void unidadeFixaNaoDeveDependerDasDimensoes() {
		// Mesma quantidade fixa e quantidade de peças, dimensões diferentes => mesmo resultado
		UUID produtoId = UUID.randomUUID();
		Produto produto = produtoComServicoUnidadeFixa(produtoId, "PONTEIRA", "0.24", "4");
		when(produtoService.findById(produtoId)).thenReturn(produto);

		ProdutoCalculoResponse pequeno = calculoService.calcular(produtoId, request("30", "30", "5"));
		ProdutoCalculoResponse grande = calculoService.calcular(produtoId, request("300", "200", "5"));

		// 4 por peça * 5 peças = 20 un, independente da medida
		assertThat(pequeno.getServicos().get(0).getQuantidadeCalculada()).isEqualByComparingTo("20");
		assertThat(grande.getServicos().get(0).getQuantidadeCalculada())
				.isEqualByComparingTo(pequeno.getServicos().get(0).getQuantidadeCalculada());
	}

	@Test
	void deveFalharQuandoFaltaParametroQuantidadeFixa() {
		UUID produtoId = UUID.randomUUID();
		Produto produto = produtoComServicoUnidadeFixa(produtoId, "GRAMPO", "0.02", null);
		when(produtoService.findById(produtoId)).thenReturn(produto);

		assertThatThrownBy(() -> calculoService.calcular(produtoId, request("100", "80", "1")))
				.isInstanceOf(ProdutoCalculoValidationException.class)
				.hasMessageContaining("QUANTIDADE_FIXA");
	}

	@Test
	void margemAutomaticaCresceQuandoOMaterialDominaOCusto() {
		// material 100 (1 m² x R$100) + serviço 30 (1 un x R$30) => custo 130
		// razão serviço/material = 0,30 => margem atacado = 1 - 0,30 = 0,70 (acima do piso)
		UUID produtoId = UUID.randomUUID();
		Produto produto = produtoComMaterialEServico(produtoId, "100", "30", "1");
		when(produtoService.findById(produtoId)).thenReturn(produto);

		ProdutoCalculoResponse response = calculoService.calcular(produtoId, request("100", "100", "1"));

		assertThat(response.getTotalMateriais()).isEqualByComparingTo("100.00");
		assertThat(response.getTotalServicos()).isEqualByComparingTo("30.00");
		assertThat(response.getTotalGeral()).isEqualByComparingTo("130.00");
		assertThat(response.getMarkupAtacado()).isEqualByComparingTo("70.00"); // 70%
		assertThat(response.getPrecoAtacado()).isEqualByComparingTo("221.00"); // 130 x 1,70
		assertThat(response.getPrecoVarejo()).isEqualByComparingTo("306.00"); // 221 x 1,3846
		assertThat(response.getPrecoSugerido()).isNull(); // sem categoria
	}

	@Test
	void semCustoDeMaterialDeveAplicarOPisoDe35() {
		// serviço puro (sem material): custo 9,00 => margem trava no piso de 35%
		UUID produtoId = UUID.randomUUID();
		Produto produto = produtoComServicoUnidadeFixa(produtoId, "BASTAO", "1.50", "2");
		when(produtoService.findById(produtoId)).thenReturn(produto);

		ProdutoCalculoResponse response = calculoService.calcular(produtoId, request("100", "80", "3"));

		assertThat(response.getTotalMateriais()).isEqualByComparingTo("0.00");
		assertThat(response.getMarkupAtacado()).isEqualByComparingTo("35.00");
		assertThat(response.getPrecoAtacado()).isEqualByComparingTo("12.15"); // 9,00 x 1,35
	}

	@Test
	void deveSugerirAtacadoParaRevendaEVarejoParaConsumidorFinal() {
		UUID produtoId = UUID.randomUUID();
		Produto produto = produtoComMaterialEServico(produtoId, "100", "30", "1");
		when(produtoService.findById(produtoId)).thenReturn(produto);

		ProdutoCalculoRequest revenda = request("100", "100", "1");
		revenda.setCategoria(Categoria.R);
		ProdutoCalculoRequest finalConsumidor = request("100", "100", "1");
		finalConsumidor.setCategoria(Categoria.F);

		assertThat(calculoService.calcular(produtoId, revenda).getPrecoSugerido()).isEqualByComparingTo("221.00");
		assertThat(calculoService.calcular(produtoId, finalConsumidor).getPrecoSugerido()).isEqualByComparingTo("306.00");
	}

	// --- fixtures ---

	private Produto produtoComServicoUnidadeFixa(UUID produtoId, String nomeServico, String preco,
			String quantidadeFixa) {
		Servico servico = new Servico();
		servico.setId(UUID.randomUUID());
		servico.setNome(nomeServico);
		servico.setUnidade(UnidadeServico.UN);
		servico.setPreco(new BigDecimal(preco));

		Calculo calculo = new Calculo();
		calculo.setId(UUID.randomUUID());
		calculo.setNome(nomeServico + " POR UNIDADE");
		calculo.setTipoCalculo(TipoCalculo.UNIDADE_FIXA);
		calculo.setBaseOperacional(BaseOperacionalCalculo.QUANTIDADE_INFORMADA);

		ProdutoServicoCalculo servicoCalculo = new ProdutoServicoCalculo();
		servicoCalculo.setServico(servico);
		servicoCalculo.setCalculo(calculo);
		if (quantidadeFixa != null) {
			ProdutoServicoParametroCalculo parametro = new ProdutoServicoParametroCalculo();
			parametro.setCodigo(CodigoParametroCalculo.QUANTIDADE_FIXA);
			parametro.setValor(new BigDecimal(quantidadeFixa));
			servicoCalculo.setParametros(List.of(parametro));
		}

		Produto produto = new Produto();
		produto.setId(produtoId);
		produto.setNome("PRODUTO TESTE");
		produto.setServicosCalculo(List.of(servicoCalculo));
		return produto;
	}

	private Produto produtoComMaterialEServico(UUID produtoId, String materiaPreco, String servicoPreco,
			String quantidadeFixa) {
		// Matéria por área (AREA_BASE)
		Materia materia = new Materia();
		materia.setId(UUID.randomUUID());
		materia.setNome("LONA");
		materia.setUnidade(UnidadeMateria.M2);
		materia.setPreco(new BigDecimal(materiaPreco));
		Calculo calcMateria = new Calculo();
		calcMateria.setId(UUID.randomUUID());
		calcMateria.setNome("AREA");
		calcMateria.setTipoCalculo(TipoCalculo.AREA_BASE);
		calcMateria.setBaseOperacional(BaseOperacionalCalculo.AREA);
		ProdutoMateriaCalculo materiaCalculo = new ProdutoMateriaCalculo();
		materiaCalculo.setMateria(materia);
		materiaCalculo.setCalculo(calcMateria);

		// Serviço por unidade (UNIDADE_FIXA)
		Servico servico = new Servico();
		servico.setId(UUID.randomUUID());
		servico.setNome("TAXA");
		servico.setUnidade(UnidadeServico.UN);
		servico.setPreco(new BigDecimal(servicoPreco));
		Calculo calcServico = new Calculo();
		calcServico.setId(UUID.randomUUID());
		calcServico.setNome("TAXA POR UNIDADE");
		calcServico.setTipoCalculo(TipoCalculo.UNIDADE_FIXA);
		calcServico.setBaseOperacional(BaseOperacionalCalculo.QUANTIDADE_INFORMADA);
		ProdutoServicoCalculo servicoCalculo = new ProdutoServicoCalculo();
		servicoCalculo.setServico(servico);
		servicoCalculo.setCalculo(calcServico);
		ProdutoServicoParametroCalculo parametro = new ProdutoServicoParametroCalculo();
		parametro.setCodigo(CodigoParametroCalculo.QUANTIDADE_FIXA);
		parametro.setValor(new BigDecimal(quantidadeFixa));
		servicoCalculo.setParametros(List.of(parametro));

		Produto produto = new Produto();
		produto.setId(produtoId);
		produto.setNome("PRODUTO MAT+SERV");
		produto.setMateriasCalculo(List.of(materiaCalculo));
		produto.setServicosCalculo(List.of(servicoCalculo));
		return produto;
	}

	private ProdutoCalculoRequest request(String altura, String largura, String quantidade) {
		ProdutoCalculoRequest request = new ProdutoCalculoRequest();
		request.setAltura(new BigDecimal(altura));
		request.setLargura(new BigDecimal(largura));
		request.setQuantidade(new BigDecimal(quantidade));
		return request;
	}
}
