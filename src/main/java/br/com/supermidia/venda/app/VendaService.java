package br.com.supermidia.venda.app;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import br.com.supermidia.venda.domain.ItemVendaDetalhe;
import br.com.supermidia.venda.domain.StatusVenda;
import br.com.supermidia.venda.domain.Venda;
import br.com.supermidia.venda.infra.VendaRepository;

@Service
public class VendaService {

	private final VendaRepository vendaRepository;
	private final ClienteRepository clienteRepository;
	private final ProdutoCalculoService produtoCalculoService;

	public VendaService(VendaRepository vendaRepository, ClienteRepository clienteRepository,
			ProdutoCalculoService produtoCalculoService) {
		this.vendaRepository = vendaRepository;
		this.clienteRepository = clienteRepository;
		this.produtoCalculoService = produtoCalculoService;
	}

	/**
	 * Cria um orçamento: para cada item, roda o motor de cálculo com a categoria do
	 * cliente e CONGELA o resultado (custos, preços e detalhamento) no snapshot.
	 */
	@Transactional
	public Venda criarOrcamento(VendaCreateRequest request) {
		Cliente cliente = clienteRepository.findById(request.getClienteId())
				.orElseThrow(() -> new VendaValidationException("Cliente não encontrado: " + request.getClienteId()));

		Venda venda = new Venda();
		venda.setCliente(cliente);
		venda.setStatus(StatusVenda.ORCAMENTO);

		for (VendaItemRequest itemRequest : request.getItens()) {
			venda.addItem(congelarItem(itemRequest, cliente.getCategoria()));
		}

		venda.recalcularTotal();
		return vendaRepository.save(venda);
	}

	@Transactional(readOnly = true)
	public List<Venda> findAll() {
		return vendaRepository.findAll();
	}

	@Transactional(readOnly = true)
	public List<Venda> findByStatus(StatusVenda status) {
		return vendaRepository.findByStatus(status);
	}

	@Transactional(readOnly = true)
	public Venda findById(UUID id) {
		return vendaRepository.findById(id)
				.orElseThrow(() -> new VendaNotFoundException("Venda não encontrada: " + id));
	}

	private ItemVenda congelarItem(VendaItemRequest itemRequest, Categoria categoria) {
		ProdutoCalculoRequest calculoRequest = new ProdutoCalculoRequest();
		calculoRequest.setAltura(itemRequest.getAltura());
		calculoRequest.setLargura(itemRequest.getLargura());
		calculoRequest.setQuantidade(itemRequest.getQuantidade());
		calculoRequest.setCategoria(categoria);

		ProdutoCalculoResponse calculo = produtoCalculoService.calcular(itemRequest.getProdutoId(), calculoRequest);

		ItemVenda item = new ItemVenda();
		item.setProdutoId(calculo.getProdutoId());
		item.setProdutoNome(calculo.getProdutoNome());
		item.setAltura(calculo.getAltura());
		item.setLargura(calculo.getLargura());
		item.setQuantidade(calculo.getQuantidade());
		item.setCustoTotal(calculo.getTotalGeral());
		item.setMarkupAplicado(categoria == Categoria.R ? calculo.getMarkupAtacado() : calculo.getMarkupVarejo());
		item.setPrecoSugerido(calculo.getPrecoSugerido());
		item.setPrecoFinal(calculo.getPrecoSugerido()); // default editável

		List<ItemVendaDetalhe> detalhes = new ArrayList<>();
		calculo.getMateriais().forEach(linha -> detalhes.add(congelarDetalhe(linha)));
		calculo.getServicos().forEach(linha -> detalhes.add(congelarDetalhe(linha)));
		item.setDetalhes(detalhes);

		return item;
	}

	private ItemVendaDetalhe congelarDetalhe(ProdutoCalculoItemResponse linha) {
		ItemVendaDetalhe detalhe = new ItemVendaDetalhe();
		detalhe.setNome(linha.getNome());
		detalhe.setTipoItem(linha.getTipoItem());
		detalhe.setCalculoNome(linha.getCalculo());
		detalhe.setTipoCalculo(linha.getTipoCalculo());
		detalhe.setBaseOperacional(linha.getBaseOperacional());
		detalhe.setQuantidadeCalculada(linha.getQuantidadeCalculada());
		detalhe.setUnidade(linha.getUnidade());
		detalhe.setPrecoUnitario(linha.getPrecoUnitario());
		detalhe.setValorTotal(linha.getValorTotal());
		return detalhe;
	}
}
