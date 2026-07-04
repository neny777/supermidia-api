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
import br.com.supermidia.produto.domain.ProdutoComponente;
import br.com.supermidia.produto.domain.ProdutoComponenteParametro;
import br.com.supermidia.produto.domain.ProdutoMedida;
import br.com.supermidia.produto.domain.ProdutoParametroVinculoMedida;
import br.com.supermidia.produto.domain.TipoItemComponente;
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
		Produto produto = produtoSoServico(produtoId, "BASTAO", "1.50", TipoCalculo.UNIDADE_FIXA,
				param(CodigoParametroCalculo.QUANTIDADE_FIXA, "2"));
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
		UUID produtoId = UUID.randomUUID();
		Produto produto = produtoSoServico(produtoId, "PONTEIRA", "0.24", TipoCalculo.UNIDADE_FIXA,
				param(CodigoParametroCalculo.QUANTIDADE_FIXA, "4"));
		when(produtoService.findById(produtoId)).thenReturn(produto);

		ProdutoCalculoResponse pequeno = calculoService.calcular(produtoId, request("30", "30", "5"));
		ProdutoCalculoResponse grande = calculoService.calcular(produtoId, request("300", "200", "5"));

		assertThat(pequeno.getServicos().get(0).getQuantidadeCalculada()).isEqualByComparingTo("20");
		assertThat(grande.getServicos().get(0).getQuantidadeCalculada())
				.isEqualByComparingTo(pequeno.getServicos().get(0).getQuantidadeCalculada());
	}

	@Test
	void deveFalharQuandoFaltaParametroQuantidadeFixa() {
		UUID produtoId = UUID.randomUUID();
		Produto produto = produtoSoServico(produtoId, "GRAMPO", "0.02", TipoCalculo.UNIDADE_FIXA);
		when(produtoService.findById(produtoId)).thenReturn(produto);

		assertThatThrownBy(() -> calculoService.calcular(produtoId, request("100", "80", "1")))
				.isInstanceOf(ProdutoCalculoValidationException.class)
				.hasMessageContaining("QUANTIDADE_FIXA");
	}

	@Test
	void taxaFixaNaoDeveEscalarComAQuantidade() {
		// Ajuste de arte: R$ 5,00 uma vez por item do orçamento, seja 1 ou 10 peças
		UUID produtoId = UUID.randomUUID();
		Produto produto = produtoSoServico(produtoId, "AJUSTE DE ARTE SIMPLES", "5.00", TipoCalculo.TAXA_FIXA,
				param(CodigoParametroCalculo.QUANTIDADE_FIXA, "1"));
		when(produtoService.findById(produtoId)).thenReturn(produto);

		ProdutoCalculoResponse umaPeca = calculoService.calcular(produtoId, request("100", "80", "1"));
		ProdutoCalculoResponse dezPecas = calculoService.calcular(produtoId, request("100", "80", "10"));

		assertThat(umaPeca.getServicos().get(0).getValorTotal()).isEqualByComparingTo("5.00");
		assertThat(dezPecas.getServicos().get(0).getQuantidadeCalculada()).isEqualByComparingTo("1");
		assertThat(dezPecas.getServicos().get(0).getValorTotal()).isEqualByComparingTo("5.00");
	}

	@Test
	void margemAutomaticaCresceQuandoOMaterialDominaOCusto() {
		// material 100 (1 m² x R$100) + serviço 30 (1 un x R$30) => custo 130
		// razão serviço/material = 0,30 => margem atacado = 0,70 (acima do piso)
		UUID produtoId = UUID.randomUUID();
		Produto produto = produtoMaterialEServico(produtoId, "100", "30", "1");
		when(produtoService.findById(produtoId)).thenReturn(produto);

		ProdutoCalculoResponse response = calculoService.calcular(produtoId, request("100", "100", "1"));

		assertThat(response.getTotalMateriais()).isEqualByComparingTo("100.00");
		assertThat(response.getTotalServicos()).isEqualByComparingTo("30.00");
		assertThat(response.getTotalGeral()).isEqualByComparingTo("130.00");
		assertThat(response.getMarkupAtacado()).isEqualByComparingTo("70.00");
		assertThat(response.getPrecoAtacado()).isEqualByComparingTo("221.00");
		assertThat(response.getPrecoVarejo()).isEqualByComparingTo("306.00"); // 221 x 1,3846
		assertThat(response.getPrecoSugerido()).isNull();
	}

	@Test
	void semCustoDeMaterialDeveAplicarOPisoDe35() {
		UUID produtoId = UUID.randomUUID();
		Produto produto = produtoSoServico(produtoId, "BASTAO", "1.50", TipoCalculo.UNIDADE_FIXA,
				param(CodigoParametroCalculo.QUANTIDADE_FIXA, "2"));
		when(produtoService.findById(produtoId)).thenReturn(produto);

		ProdutoCalculoResponse response = calculoService.calcular(produtoId, request("100", "80", "3"));

		assertThat(response.getTotalMateriais()).isEqualByComparingTo("0.00");
		assertThat(response.getMarkupAtacado()).isEqualByComparingTo("35.00");
		assertThat(response.getPrecoAtacado()).isEqualByComparingTo("12.15"); // 9,00 x 1,35
	}

	@Test
	void deveSugerirAtacadoParaRevendaEVarejoParaConsumidorFinal() {
		UUID produtoId = UUID.randomUUID();
		Produto produto = produtoMaterialEServico(produtoId, "100", "30", "1");
		when(produtoService.findById(produtoId)).thenReturn(produto);

		ProdutoCalculoRequest revenda = request("100", "100", "1");
		revenda.setCategoria(Categoria.R);
		ProdutoCalculoRequest finalConsumidor = request("100", "100", "1");
		finalConsumidor.setCategoria(Categoria.F);

		assertThat(calculoService.calcular(produtoId, revenda).getPrecoSugerido()).isEqualByComparingTo("221.00");
		assertThat(calculoService.calcular(produtoId, finalConsumidor).getPrecoSugerido()).isEqualByComparingTo("306.00");
	}

	@Test
	void medidaVinculadaDeveAlimentarOParametroComMultiplicador() {
		// BORDA padrão 10cm, vínculo ×2 => acréscimo de 20cm por dimensão.
		// Lona 100x100 => (1,20 × 1,20) = 1,44 m² × R$10 = R$ 14,40
		UUID produtoId = UUID.randomUUID();
		Produto produto = new Produto();
		produto.setId(produtoId);
		produto.setNome("LONA COM BORDA");
		produto.addMedida(medida("BORDA", "10"));

		ProdutoComponenteParametro acrescimoAltura = paramComVinculo(CodigoParametroCalculo.ACRESCIMO_ALTURA, null,
				"BORDA", "2");
		ProdutoComponenteParametro acrescimoLargura = paramComVinculo(CodigoParametroCalculo.ACRESCIMO_LARGURA, null,
				"BORDA", "2");
		ProdutoComponenteParametro fator = param(CodigoParametroCalculo.FATOR, "1");
		produto.addComponente(componenteMateria(materia("LONA", "10.00"),
				calculo("LONA COM ACRESCIMOS", TipoCalculo.AREA_COM_ACRESCIMOS_E_FATOR, BaseOperacionalCalculo.AREA),
				acrescimoAltura, acrescimoLargura, fator));

		when(produtoService.findById(produtoId)).thenReturn(produto);

		ProdutoCalculoResponse response = calculoService.calcular(produtoId, request("100", "100", "1"));

		assertThat(response.getMateriais().get(0).getQuantidadeCalculada()).isEqualByComparingTo("1.44");
		assertThat(response.getMateriais().get(0).getValorTotal()).isEqualByComparingTo("14.40");
	}

	@Test
	void slotSemEscolhaDeMaterialDeveFalhar() {
		UUID produtoId = UUID.randomUUID();
		Produto produto = new Produto();
		produto.setId(produtoId);
		produto.setNome("IMPRESSAO EM LONA");
		ProdutoComponente slot = new ProdutoComponente();
		slot.setTipoItem(TipoItemComponente.MATERIA);
		slot.setGrupoMateriaSlot("LONAS");
		slot.setCalculo(calculo("AREA", TipoCalculo.AREA_BASE, BaseOperacionalCalculo.AREA));
		produto.addComponente(slot);
		when(produtoService.findById(produtoId)).thenReturn(produto);

		assertThatThrownBy(() -> calculoService.calcular(produtoId, request("100", "100", "1")))
				.isInstanceOf(ProdutoCalculoValidationException.class)
				.hasMessageContaining("LONAS");
	}

	// --- fixtures ---

	private Produto produtoSoServico(UUID produtoId, String nomeServico, String preco, TipoCalculo tipo,
			ProdutoComponenteParametro... parametros) {
		Produto produto = new Produto();
		produto.setId(produtoId);
		produto.setNome("PRODUTO TESTE");
		produto.addComponente(componenteServico(servico(nomeServico, preco),
				calculo(nomeServico + " " + tipo, tipo, BaseOperacionalCalculo.QUANTIDADE_INFORMADA), parametros));
		return produto;
	}

	private Produto produtoMaterialEServico(UUID produtoId, String materiaPreco, String servicoPreco,
			String quantidadeFixa) {
		Produto produto = new Produto();
		produto.setId(produtoId);
		produto.setNome("PRODUTO MAT+SERV");
		produto.addComponente(componenteMateria(materia("LONA", materiaPreco),
				calculo("AREA", TipoCalculo.AREA_BASE, BaseOperacionalCalculo.AREA)));
		produto.addComponente(componenteServico(servico("TAXA", servicoPreco),
				calculo("TAXA POR UNIDADE", TipoCalculo.UNIDADE_FIXA, BaseOperacionalCalculo.QUANTIDADE_INFORMADA),
				param(CodigoParametroCalculo.QUANTIDADE_FIXA, quantidadeFixa)));
		return produto;
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

	private Materia materia(String nome, String preco) {
		Materia materia = new Materia();
		materia.setId(UUID.randomUUID());
		materia.setNome(nome);
		materia.setUnidade(UnidadeMateria.M2);
		materia.setPreco(new BigDecimal(preco));
		return materia;
	}

	private Servico servico(String nome, String preco) {
		Servico servico = new Servico();
		servico.setId(UUID.randomUUID());
		servico.setNome(nome);
		servico.setUnidade(UnidadeServico.UN);
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

	private ProdutoComponenteParametro param(CodigoParametroCalculo codigo, String valorConstante) {
		ProdutoComponenteParametro parametro = new ProdutoComponenteParametro();
		parametro.setCodigo(codigo);
		parametro.setValorConstante(new BigDecimal(valorConstante));
		return parametro;
	}

	private ProdutoComponenteParametro paramComVinculo(CodigoParametroCalculo codigo, String constante,
			String medidaNome, String multiplicador) {
		ProdutoComponenteParametro parametro = new ProdutoComponenteParametro();
		parametro.setCodigo(codigo);
		parametro.setValorConstante(constante == null ? null : new BigDecimal(constante));
		ProdutoParametroVinculoMedida vinculo = new ProdutoParametroVinculoMedida();
		vinculo.setMedidaNome(medidaNome);
		vinculo.setMultiplicador(new BigDecimal(multiplicador));
		parametro.addVinculo(vinculo);
		return parametro;
	}

	private ProdutoMedida medida(String nome, String valorPadrao) {
		ProdutoMedida medida = new ProdutoMedida();
		medida.setNome(nome);
		medida.setValorPadrao(valorPadrao == null ? null : new BigDecimal(valorPadrao));
		return medida;
	}

	private ProdutoCalculoRequest request(String altura, String largura, String quantidade) {
		ProdutoCalculoRequest request = new ProdutoCalculoRequest();
		request.setAltura(new BigDecimal(altura));
		request.setLargura(new BigDecimal(largura));
		request.setQuantidade(new BigDecimal(quantidade));
		return request;
	}
}
