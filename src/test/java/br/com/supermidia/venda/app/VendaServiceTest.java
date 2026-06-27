package br.com.supermidia.venda.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.supermidia.calculo.domain.BaseOperacionalCalculo;
import br.com.supermidia.calculo.domain.TipoCalculo;
import br.com.supermidia.pessoa.cliente.domain.Cliente;
import br.com.supermidia.pessoa.cliente.domain.Cliente.Categoria;
import br.com.supermidia.pessoa.cliente.infra.ClienteRepository;
import br.com.supermidia.produto.api.dto.ProdutoCalculoItemResponse;
import br.com.supermidia.produto.api.dto.ProdutoCalculoRequest;
import br.com.supermidia.produto.api.dto.ProdutoCalculoResponse;
import br.com.supermidia.produto.app.ProdutoCalculoService;
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

	@InjectMocks
	private VendaService vendaService;

	@Test
	void deveCriarOrcamentoCongelandoOSnapshotComPrecoDeAtacadoParaRevenda() {
		UUID clienteId = UUID.randomUUID();
		Cliente cliente = cliente(clienteId, Categoria.R);
		when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));

		UUID produtoId = UUID.randomUUID();
		when(produtoCalculoService.calcular(eq(produtoId), any())).thenReturn(calculoLona(produtoId, "135.25"));
		when(vendaRepository.save(any(Venda.class))).thenAnswer(inv -> inv.getArgument(0));

		Venda venda = vendaService.criarOrcamento(request(clienteId, produtoId));

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
		verify(vendaRepository).save(venda);
	}

	@Test
	void deveAplicarMarkupDeVarejoParaConsumidorFinal() {
		UUID clienteId = UUID.randomUUID();
		when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente(clienteId, Categoria.F)));

		UUID produtoId = UUID.randomUUID();
		when(produtoCalculoService.calcular(eq(produtoId), any())).thenReturn(calculoLona(produtoId, "165.31"));
		when(vendaRepository.save(any(Venda.class))).thenAnswer(inv -> inv.getArgument(0));

		Venda venda = vendaService.criarOrcamento(request(clienteId, produtoId));

		ItemVenda item = venda.getItens().get(0);
		assertThat(item.getMarkupAplicado()).isEqualByComparingTo("120"); // markup de varejo
		assertThat(item.getPrecoSugerido()).isEqualByComparingTo("165.31");
	}

	@Test
	void deveFalharQuandoClienteNaoExiste() {
		UUID clienteId = UUID.randomUUID();
		when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

		org.assertj.core.api.Assertions
				.assertThatThrownBy(() -> vendaService.criarOrcamento(request(clienteId, UUID.randomUUID())))
				.isInstanceOf(VendaValidationException.class)
				.hasMessageContaining("Cliente não encontrado");
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
