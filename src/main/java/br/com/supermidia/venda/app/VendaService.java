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

	@Transactional
	public Venda converterParaOrdemServico(UUID id) {
		Venda venda = findById(id);
		venda.converterParaOrdemServico();
		return vendaRepository.save(venda);
	}

	@Transactional
	public Venda cancelar(UUID id) {
		Venda venda = findById(id);
		venda.cancelar();
		return vendaRepository.save(venda);
	}

	/**
	 * Reprocessa um orçamento com os preços atuais do catálogo (regrava o snapshot
	 * de cada item, reseta o preço final e reinicia a validade de 15 dias).
	 */
	@Transactional
	public Venda recalcular(UUID id) {
		Venda venda = findById(id);
		if (venda.getStatus() != StatusVenda.ORCAMENTO) {
			throw new VendaValidationException("Somente orçamentos podem ser recalculados.");
		}
		Categoria categoria = venda.getCliente() != null ? venda.getCliente().getCategoria() : null;
		venda.getItens().forEach(item -> aplicarCalculo(item, categoria));
		venda.renovarValidade();
		venda.recalcularTotal();
		return vendaRepository.save(venda);
	}

	private ItemVenda congelarItem(VendaItemRequest itemRequest, Categoria categoria) {
		ItemVenda item = new ItemVenda();
		item.setProdutoId(itemRequest.getProdutoId());
		item.setAltura(itemRequest.getAltura());
		item.setLargura(itemRequest.getLargura());
		item.setQuantidade(itemRequest.getQuantidade());
		aplicarCalculo(item, categoria);
		return item;
	}

	/** Roda o motor com as medidas do item e congela custo/markup/preços/detalhamento. */
	private void aplicarCalculo(ItemVenda item, Categoria categoria) {
		ProdutoCalculoRequest calculoRequest = new ProdutoCalculoRequest();
		calculoRequest.setAltura(item.getAltura());
		calculoRequest.setLargura(item.getLargura());
		calculoRequest.setQuantidade(item.getQuantidade());
		calculoRequest.setCategoria(categoria);

		ProdutoCalculoResponse calculo = produtoCalculoService.calcular(item.getProdutoId(), calculoRequest);

		item.setProdutoNome(calculo.getProdutoNome());
		item.setCustoTotal(calculo.getTotalGeral());
		item.setMarkupAplicado(categoria == Categoria.R ? calculo.getMarkupAtacado() : calculo.getMarkupVarejo());
		item.setPrecoSugerido(calculo.getPrecoSugerido());
		item.setPrecoFinal(calculo.getPrecoSugerido()); // default editável; reset ao recalcular

		List<ItemVendaDetalhe> detalhes = new ArrayList<>();
		calculo.getMateriais().forEach(linha -> detalhes.add(congelarDetalhe(linha)));
		calculo.getServicos().forEach(linha -> detalhes.add(congelarDetalhe(linha)));
		item.setDetalhes(detalhes);
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
