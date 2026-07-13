package br.com.supermidia.venda.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.supermidia.calculo.domain.BaseOperacionalCalculo;
import br.com.supermidia.calculo.domain.TipoCalculo;
import br.com.supermidia.configuracao.domain.ConfiguracaoGlobal;
import br.com.supermidia.pessoa.cliente.domain.Cliente;
import br.com.supermidia.pessoa.cliente.domain.Cliente.Categoria;
import br.com.supermidia.pessoa.cliente.infra.ClienteRepository;
import br.com.supermidia.produto.api.dto.ProdutoCalculoItemResponse;
import br.com.supermidia.produto.api.dto.ProdutoCalculoRequest;
import br.com.supermidia.produto.api.dto.ProdutoCalculoResponse;
import br.com.supermidia.produto.app.ProdutoCalculoService;
import br.com.supermidia.venda.api.dto.VendaCabecalhoRequest;
import br.com.supermidia.venda.api.dto.VendaCreateRequest;
import br.com.supermidia.venda.api.dto.VendaItemRequest;
import br.com.supermidia.venda.domain.ItemVenda;
import br.com.supermidia.venda.domain.StatusVenda;
import br.com.supermidia.venda.domain.Venda;
import br.com.supermidia.venda.infra.VendaRepository;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

	@Mock
	private VendaRepository vendaRepository;
	@Mock
	private ClienteRepository clienteRepository;
	@Mock
	private ProdutoCalculoService produtoCalculoService;

	private VendaService vendaService;

	@BeforeEach
	void setUp() {
		vendaService = new VendaService(vendaRepository, clienteRepository, produtoCalculoService, new ObjectMapper());
	}

	@Test
	void deveCriarOrcamentoCongelandoOSnapshotComPrecoDeAtacadoParaRevenda() {
		UUID clienteId = UUID.randomUUID();
		Cliente cliente = cliente(clienteId, Categoria.R);
		when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));

		UUID produtoId = UUID.randomUUID();
		when(produtoCalculoService.calcular(eq(produtoId), any())).thenReturn(calculoLona(produtoId, "135.25"));
		when(vendaRepository.save(any(Venda.class))).thenAnswer(inv -> inv.getArgument(0));
		when(vendaRepository.proximoNumero()).thenReturn(7L);

		Venda venda = vendaService.criar(request(clienteId, produtoId));

		assertThat(venda.getNumero()).isEqualTo(7L); // número humano sequencial

		// a categoria do cliente foi repassada ao motor
		ArgumentCaptor<ProdutoCalculoRequest> captor = ArgumentCaptor.forClass(ProdutoCalculoRequest.class);
		verify(produtoCalculoService).calcular(eq(produtoId), captor.capture());
		assertThat(captor.getValue().getCategoria()).isEqualTo(Categoria.R);

		assertThat(venda.getStatus()).isEqualTo(StatusVenda.ORCAMENTO);
		assertThat(venda.getCliente()).isSameAs(cliente);
		assertThat(venda.getItens()).hasSize(1);

		ItemVenda item = venda.getItens().get(0);
		assertThat(item.getVenda()).isSameAs(venda); // ligação bidirecional
		assertThat(item.getProdutoNome()).isEqualTo("LONA");
		assertThat(item.getCustoTotal()).isEqualByComparingTo("75.14");
		assertThat(item.getMarkupAplicado()).isEqualByComparingTo("80"); // markup de atacado
		assertThat(item.getPrecoSugerido()).isEqualByComparingTo("135.25");
		assertThat(item.getPrecoFinal()).isEqualByComparingTo("135.25"); // default = sugerido

		assertThat(item.getDetalhes()).hasSize(2);
		assertThat(item.getDetalhes()).anySatisfy(d -> {
			assertThat(d.getNome()).isEqualTo("LONA");
			assertThat(d.getTipoItem()).isEqualTo("MATERIA");
			assertThat(d.getValorTotal()).isEqualByComparingTo("41.14");
		});

		assertThat(venda.getTotal()).isEqualByComparingTo("135.25");
		// descrição por extenso congelada no snapshot
		assertThat(item.getDescricao()).isEqualTo("2 × LONA · 100 × 200 cm");
		verify(vendaRepository).save(venda);
	}

	@Test
	void deveAplicarMarkupDeVarejoParaConsumidorFinal() {
		UUID clienteId = UUID.randomUUID();
		when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente(clienteId, Categoria.F)));

		UUID produtoId = UUID.randomUUID();
		when(produtoCalculoService.calcular(eq(produtoId), any())).thenReturn(calculoLona(produtoId, "165.31"));
		when(vendaRepository.save(any(Venda.class))).thenAnswer(inv -> inv.getArgument(0));

		Venda venda = vendaService.criar(request(clienteId, produtoId));

		ItemVenda item = venda.getItens().get(0);
		assertThat(item.getMarkupAplicado()).isEqualByComparingTo("120"); // markup de varejo
		assertThat(item.getPrecoSugerido()).isEqualByComparingTo("165.31");
	}

	@Test
	void deveFalharQuandoClienteNaoExiste() {
		UUID clienteId = UUID.randomUUID();
		when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

		org.assertj.core.api.Assertions
				.assertThatThrownBy(() -> vendaService.criar(request(clienteId, UUID.randomUUID())))
				.isInstanceOf(VendaValidationException.class)
				.hasMessageContaining("Cliente não encontrado");
	}

	@Test
	void recalcularDeveAtualizarOSnapshotComPrecosAtuaisERenovarAValidade() {
		UUID vendaId = UUID.randomUUID();
		UUID produtoId = UUID.randomUUID();

		Venda venda = new Venda();
		venda.setCliente(cliente(UUID.randomUUID(), Categoria.R));
		venda.setStatus(StatusVenda.ORCAMENTO);
		venda.setDataCriacao(LocalDateTime.now().minusDays(20)); // orçamento antigo/vencido
		ItemVenda item = new ItemVenda();
		item.setProdutoId(produtoId);
		item.setAltura(new BigDecimal("100"));
		item.setLargura(new BigDecimal("200"));
		item.setQuantidade(new BigDecimal("2"));
		item.setCustoTotal(new BigDecimal("50.00")); // valores antigos
		item.setPrecoSugerido(new BigDecimal("90.00"));
		item.setPrecoFinal(new BigDecimal("999.00")); // override manual antigo
		venda.addItem(item);

		when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(venda));
		when(produtoCalculoService.calcular(eq(produtoId), any())).thenReturn(calculoLona(produtoId, "135.25"));
		when(vendaRepository.save(any(Venda.class))).thenAnswer(inv -> inv.getArgument(0));

		Venda recalculada = vendaService.recalcular(vendaId);

		ItemVenda atualizado = recalculada.getItens().get(0);
		assertThat(atualizado.getCustoTotal()).isEqualByComparingTo("75.14"); // preço atual
		assertThat(atualizado.getPrecoSugerido()).isEqualByComparingTo("135.25");
		assertThat(atualizado.getPrecoFinal()).isEqualByComparingTo("135.25"); // override resetado
		assertThat(atualizado.getDetalhes()).hasSize(2);
		assertThat(recalculada.getTotal()).isEqualByComparingTo("135.25");
		assertThat(recalculada.isVencido()).isFalse(); // validade renovada
	}

	@Test
	void recalcularDeveFalharSeNaoForOrcamento() {
		UUID vendaId = UUID.randomUUID();
		Venda venda = new Venda();
		venda.setStatus(StatusVenda.ORDEM_SERVICO);
		when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(venda));

		org.assertj.core.api.Assertions.assertThatThrownBy(() -> vendaService.recalcular(vendaId))
				.isInstanceOf(VendaValidationException.class)
				.hasMessageContaining("orçamentos");
	}

	@Test
	void deveCriarOrdemDeServicoDiretaQuandoSolicitado() {
		UUID clienteId = UUID.randomUUID();
		when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente(clienteId, Categoria.F)));
		UUID produtoId = UUID.randomUUID();
		when(produtoCalculoService.calcular(eq(produtoId), any())).thenReturn(calculoLona(produtoId, "165.31"));
		when(vendaRepository.save(any(Venda.class))).thenAnswer(inv -> inv.getArgument(0));

		VendaCreateRequest request = request(clienteId, produtoId);
		request.setStatus(StatusVenda.ORDEM_SERVICO);

		assertThat(vendaService.criar(request).getStatus()).isEqualTo(StatusVenda.ORDEM_SERVICO);
	}

	@Test
	void criarJaCanceladaDeveFalhar() {
		UUID clienteId = UUID.randomUUID();
		when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente(clienteId, Categoria.F)));
		VendaCreateRequest request = request(clienteId, UUID.randomUUID());
		request.setStatus(StatusVenda.CANCELADO);

		org.assertj.core.api.Assertions.assertThatThrownBy(() -> vendaService.criar(request))
				.isInstanceOf(VendaValidationException.class);
	}

	@Test
	void recalcularDeveReproduzirMedidasEEscolhasGravadasNoSnapshot() {
		UUID vendaId = UUID.randomUUID();
		UUID produtoId = UUID.randomUUID();
		UUID opcaoId = UUID.randomUUID();

		Venda venda = new Venda();
		venda.setCliente(cliente(UUID.randomUUID(), Categoria.R));
		venda.setStatus(StatusVenda.ORCAMENTO);
		venda.setDataCriacao(LocalDateTime.now());
		ItemVenda item = new ItemVenda();
		item.setProdutoId(produtoId);
		item.setAltura(new BigDecimal("100"));
		item.setLargura(new BigDecimal("200"));
		item.setQuantidade(new BigDecimal("2"));
		item.setEntradaJson("{\"altura\":100,\"largura\":200,\"quantidade\":2,"
				+ "\"medidas\":{\"BORDA\":10},\"escolhasOpcao\":[\"" + opcaoId + "\"]}");
		venda.addItem(item);

		when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(venda));
		when(produtoCalculoService.calcular(eq(produtoId), any())).thenReturn(calculoLona(produtoId, "135.25"));
		when(vendaRepository.save(any(Venda.class))).thenAnswer(inv -> inv.getArgument(0));

		vendaService.recalcular(vendaId);

		ArgumentCaptor<ProdutoCalculoRequest> captor = ArgumentCaptor.forClass(ProdutoCalculoRequest.class);
		verify(produtoCalculoService).calcular(eq(produtoId), captor.capture());
		assertThat(captor.getValue().getMedidas()).containsEntry("BORDA", new BigDecimal("10"));
		assertThat(captor.getValue().getEscolhasOpcao()).containsExactly(opcaoId);
	}

	@Test
	void excluirDentroDaJanelaApagaAVenda() {
		UUID vendaId = UUID.randomUUID();
		Venda venda = new Venda();
		venda.setDataCriacao(LocalDateTime.now());
		when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(venda));

		vendaService.excluir(vendaId);

		verify(vendaRepository).delete(venda);
	}

	@Test
	void excluirForaDaJanelaDeveFalhar() {
		UUID vendaId = UUID.randomUUID();
		Venda venda = new Venda();
		venda.setDataCriacao(LocalDateTime.now().minusHours(ConfiguracaoGlobal.getEdicaoHoras() + 1));
		when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(venda));

		org.assertj.core.api.Assertions.assertThatThrownBy(() -> vendaService.excluir(vendaId))
				.isInstanceOf(VendaValidationException.class)
				.hasMessageContaining("janela");
	}

	@Test
	void editarDentroDaJanelaSubstituiOsItens() {
		UUID vendaId = UUID.randomUUID();
		UUID clienteId = UUID.randomUUID();
		UUID produtoId = UUID.randomUUID();

		Venda venda = new Venda();
		venda.setDataCriacao(LocalDateTime.now());
		venda.setStatus(StatusVenda.ORCAMENTO);
		ItemVenda itemAntigo = new ItemVenda();
		itemAntigo.setProdutoNome("ANTIGO");
		venda.addItem(itemAntigo);

		when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(venda));
		when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente(clienteId, Categoria.R)));
		when(produtoCalculoService.calcular(eq(produtoId), any())).thenReturn(calculoLona(produtoId, "135.25"));
		when(vendaRepository.save(any(Venda.class))).thenAnswer(inv -> inv.getArgument(0));

		Venda editada = vendaService.editar(vendaId, request(clienteId, produtoId));

		assertThat(editada.getItens()).hasSize(1);
		assertThat(editada.getItens().get(0).getProdutoNome()).isEqualTo("LONA");
		assertThat(editada.getTotal()).isEqualByComparingTo("135.25");
	}

	@Test
	void editarForaDaJanelaDeveFalhar() {
		UUID vendaId = UUID.randomUUID();
		Venda venda = new Venda();
		venda.setDataCriacao(LocalDateTime.now().minusHours(ConfiguracaoGlobal.getEdicaoHoras() + 1));
		when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(venda));

		org.assertj.core.api.Assertions
				.assertThatThrownBy(() -> vendaService.editar(vendaId, request(UUID.randomUUID(), UUID.randomUUID())))
				.isInstanceOf(VendaValidationException.class)
				.hasMessageContaining("janela");
	}

	@Test
	void overridePrecoFinalAtualizaOItemEOTotal() {
		UUID vendaId = UUID.randomUUID();
		UUID itemId = UUID.randomUUID();

		Venda venda = new Venda();
		venda.setStatus(StatusVenda.ORCAMENTO);
		venda.setDataCriacao(LocalDateTime.now());
		ItemVenda item = new ItemVenda();
		item.setId(itemId);
		item.setPrecoSugerido(new BigDecimal("135.25"));
		item.setPrecoFinal(new BigDecimal("135.25"));
		venda.addItem(item);

		when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(venda));
		when(vendaRepository.save(any(Venda.class))).thenAnswer(inv -> inv.getArgument(0));

		Venda atualizada = vendaService.overridePrecoFinal(vendaId, itemId, new BigDecimal("120.00"));

		assertThat(atualizada.getItens().get(0).getPrecoFinal()).isEqualByComparingTo("120.00");
		assertThat(atualizada.getItens().get(0).getPrecoSugerido()).isEqualByComparingTo("135.25"); // sugerido preservado
		assertThat(atualizada.getTotal()).isEqualByComparingTo("120.00");
	}

	@Test
	void overridePrecoFinalDeveFalharEmVendaCancelada() {
		UUID vendaId = UUID.randomUUID();
		Venda venda = new Venda();
		venda.setStatus(StatusVenda.CANCELADO);
		venda.setDataCriacao(LocalDateTime.now());
		when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(venda));

		org.assertj.core.api.Assertions
				.assertThatThrownBy(() -> vendaService.overridePrecoFinal(vendaId, UUID.randomUUID(), new BigDecimal("10")))
				.isInstanceOf(VendaValidationException.class);
	}

	@Test
	void overridePrecoFinalDeveFalharSeItemNaoPertenceAVenda() {
		UUID vendaId = UUID.randomUUID();
		Venda venda = new Venda();
		venda.setStatus(StatusVenda.ORCAMENTO);
		venda.setDataCriacao(LocalDateTime.now());
		when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(venda));

		org.assertj.core.api.Assertions
				.assertThatThrownBy(() -> vendaService.overridePrecoFinal(vendaId, UUID.randomUUID(), new BigDecimal("10")))
				.isInstanceOf(VendaValidationException.class)
				.hasMessageContaining("Item não encontrado");
	}

	@Test
	void atualizarCabecalhoGravaCondicoesLimpandoBrancos() {
		UUID vendaId = UUID.randomUUID();
		Venda venda = new Venda();
		venda.setStatus(StatusVenda.ORDEM_SERVICO);
		venda.setDataCriacao(LocalDateTime.now());
		when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(venda));
		when(vendaRepository.save(any(Venda.class))).thenAnswer(inv -> inv.getArgument(0));

		VendaCabecalhoRequest request = new VendaCabecalhoRequest();
		request.setFormaPagamento("  50% entrada + 50% na entrega  ");
		request.setPrazoEntrega("5 dias úteis");
		request.setObservacoes("   "); // em branco vira nulo

		Venda atualizada = vendaService.atualizarCabecalho(vendaId, request);

		assertThat(atualizada.getFormaPagamento()).isEqualTo("50% entrada + 50% na entrega");
		assertThat(atualizada.getPrazoEntrega()).isEqualTo("5 dias úteis");
		assertThat(atualizada.getObservacoes()).isNull();
	}

	@Test
	void atualizarCabecalhoDeveFalharEmVendaCancelada() {
		UUID vendaId = UUID.randomUUID();
		Venda venda = new Venda();
		venda.setStatus(StatusVenda.CANCELADO);
		when(vendaRepository.findById(vendaId)).thenReturn(Optional.of(venda));

		org.assertj.core.api.Assertions
				.assertThatThrownBy(() -> vendaService.atualizarCabecalho(vendaId, new VendaCabecalhoRequest()))
				.isInstanceOf(VendaValidationException.class);
	}

	// --- fixtures ---

	private Cliente cliente(UUID id, Categoria categoria) {
		Cliente cliente = new Cliente();
		cliente.setId(id);
		cliente.setCategoria(categoria);
		return cliente;
	}

	private VendaCreateRequest request(UUID clienteId, UUID produtoId) {
		VendaItemRequest item = new VendaItemRequest();
		item.setProdutoId(produtoId);
		item.setAltura(new BigDecimal("100"));
		item.setLargura(new BigDecimal("200"));
		item.setQuantidade(new BigDecimal("2"));

		VendaCreateRequest request = new VendaCreateRequest();
		request.setClienteId(clienteId);
		request.setItens(List.of(item));
		return request;
	}

	private ProdutoCalculoResponse calculoLona(UUID produtoId, String precoSugerido) {
		ProdutoCalculoResponse response = new ProdutoCalculoResponse();
		response.setProdutoId(produtoId);
		response.setProdutoNome("LONA");
		response.setAltura(new BigDecimal("100"));
		response.setLargura(new BigDecimal("200"));
		response.setQuantidade(new BigDecimal("2"));
		response.setMateriais(List.of(linha("LONA", "MATERIA", "AREA COM FATOR", TipoCalculo.AREA_COM_FATOR,
				BaseOperacionalCalculo.AREA, "4.84", "M2", "8.50", "41.14")));
		response.setServicos(List.of(linha("IMPRESSAO", "SERVICO", "IMPRESSAO", TipoCalculo.AREA_BASE,
				BaseOperacionalCalculo.AREA, "4", "M2", "8.50", "34.00")));
		response.setTotalMateriais(new BigDecimal("41.14"));
		response.setTotalServicos(new BigDecimal("34.00"));
		response.setTotalGeral(new BigDecimal("75.14"));
		response.setMarkupAtacado(new BigDecimal("80"));
		response.setMarkupVarejo(new BigDecimal("120"));
		response.setPrecoAtacado(new BigDecimal("135.25"));
		response.setPrecoVarejo(new BigDecimal("165.31"));
		response.setPrecoSugerido(new BigDecimal(precoSugerido));
		return response;
	}

	private ProdutoCalculoItemResponse linha(String nome, String tipoItem, String calculo, TipoCalculo tipoCalculo,
			BaseOperacionalCalculo base, String qtd, String unidade, String precoUnitario, String valorTotal) {
		ProdutoCalculoItemResponse linha = new ProdutoCalculoItemResponse();
		linha.setNome(nome);
		linha.setTipoItem(tipoItem);
		linha.setCalculo(calculo);
		linha.setTipoCalculo(tipoCalculo);
		linha.setBaseOperacional(base);
		linha.setQuantidadeCalculada(new BigDecimal(qtd));
		linha.setUnidade(unidade);
		linha.setPrecoUnitario(new BigDecimal(precoUnitario));
		linha.setValorTotal(new BigDecimal(valorTotal));
		return linha;
	}
}
