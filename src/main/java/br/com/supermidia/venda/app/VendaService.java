package br.com.supermidia.venda.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import br.com.supermidia.venda.domain.ItemVendaDetalhe;
import br.com.supermidia.venda.domain.StatusVenda;
import br.com.supermidia.venda.domain.Venda;
import br.com.supermidia.venda.infra.VendaRepository;

@Service
public class VendaService {

	private final VendaRepository vendaRepository;
	private final ClienteRepository clienteRepository;
	private final ProdutoCalculoService produtoCalculoService;
	private final ObjectMapper objectMapper;

	public VendaService(VendaRepository vendaRepository, ClienteRepository clienteRepository,
			ProdutoCalculoService produtoCalculoService, ObjectMapper objectMapper) {
		this.vendaRepository = vendaRepository;
		this.clienteRepository = clienteRepository;
		this.produtoCalculoService = produtoCalculoService;
		this.objectMapper = objectMapper;
	}

	/**
	 * Cria a venda (orçamento por padrão, ou OS direta): para cada item, roda o
	 * motor com a categoria do cliente e CONGELA o resultado no snapshot, junto
	 * da entrada original (medidas/escolhas) para permitir recalcular.
	 */
	@Transactional
	public Venda criar(VendaCreateRequest request) {
		Cliente cliente = clienteRepository.findById(request.getClienteId())
				.orElseThrow(() -> new VendaValidationException("Cliente não encontrado: " + request.getClienteId()));

		StatusVenda statusInicial = request.getStatus() == null ? StatusVenda.ORCAMENTO : request.getStatus();
		if (statusInicial == StatusVenda.CANCELADO) {
			throw new VendaValidationException("Uma venda não pode ser criada já cancelada.");
		}

		Venda venda = new Venda();
		venda.setNumero(vendaRepository.proximoNumero());
		venda.setCliente(cliente);
		venda.setStatus(statusInicial);
		aplicarCabecalho(venda, request.getReferencia(), request.getFormaPagamento(), request.getPrazoEntrega(),
				request.getObservacoes());

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

	/** Exclusão definitiva, permitida só na janela de arrependimento (1h). */
	@Transactional
	public void excluir(UUID id) {
		Venda venda = findById(id);
		if (!venda.isEditavel()) {
			throw new VendaValidationException(
					"A janela de exclusão (" + ConfiguracaoGlobal.getEdicaoHoras()
							+ "h) expirou ou o status não permite — use o cancelamento.");
		}
		vendaRepository.delete(venda);
	}

	/** Edição completa (cliente/tipo/itens), permitida só na janela de arrependimento (1h). */
	@Transactional
	public Venda editar(UUID id, VendaCreateRequest request) {
		Venda venda = findById(id);
		if (!venda.isEditavel()) {
			throw new VendaValidationException(
					"A janela de edição (" + ConfiguracaoGlobal.getEdicaoHoras() + "h) expirou ou o status não permite.");
		}
		Cliente cliente = clienteRepository.findById(request.getClienteId())
				.orElseThrow(() -> new VendaValidationException("Cliente não encontrado: " + request.getClienteId()));
		StatusVenda status = request.getStatus() == null ? venda.getStatus() : request.getStatus();
		if (status == StatusVenda.CANCELADO) {
			throw new VendaValidationException("Use a ação de cancelamento para cancelar a venda.");
		}

		venda.setCliente(cliente);
		venda.setStatus(status);
		aplicarCabecalho(venda, request.getReferencia(), request.getFormaPagamento(), request.getPrazoEntrega(),
				request.getObservacoes());
		List<ItemVenda> itens = new ArrayList<>();
		for (VendaItemRequest itemRequest : request.getItens()) {
			itens.add(congelarItem(itemRequest, cliente.getCategoria()));
		}
		venda.setItens(itens);
		venda.renovarValidade(); // conteúdo novo = contagens novas (validade e janela)
		venda.recalcularTotal();
		return vendaRepository.save(venda);
	}

	/**
	 * Condições da venda (pagamento/prazo/observações): não afetam preço, por
	 * isso podem ser alteradas fora da janela de 1h — só venda cancelada trava.
	 */
	@Transactional
	public Venda atualizarCabecalho(UUID id, VendaCabecalhoRequest request) {
		Venda venda = findById(id);
		if (venda.getStatus() == StatusVenda.CANCELADO) {
			throw new VendaValidationException("Venda cancelada não pode ser alterada.");
		}
		aplicarCabecalho(venda, request.getReferencia(), request.getFormaPagamento(), request.getPrazoEntrega(),
				request.getObservacoes());
		return vendaRepository.save(venda);
	}

	private void aplicarCabecalho(Venda venda, String referencia, String formaPagamento, String prazoEntrega,
			String observacoes) {
		venda.setReferencia(limpar(referencia));
		venda.setFormaPagamento(limpar(formaPagamento));
		venda.setPrazoEntrega(limpar(prazoEntrega));
		venda.setObservacoes(limpar(observacoes));
	}

	private String limpar(String texto) {
		return texto == null || texto.isBlank() ? null : texto.trim();
	}

	/**
	 * Override do preço final de um item — a única edição manual permitida na
	 * venda (decisão de 2026-06: quantidades são sempre auto-calculadas).
	 */
	@Transactional
	public Venda overridePrecoFinal(UUID vendaId, UUID itemId, BigDecimal precoFinal) {
		Venda venda = findById(vendaId);
		if (venda.getStatus() == StatusVenda.CANCELADO) {
			throw new VendaValidationException("Venda cancelada não pode ter o preço ajustado.");
		}
		if (venda.isVencido()) {
			throw new VendaValidationException("Orçamento vencido: recalcule antes de ajustar o preço.");
		}
		ItemVenda item = venda.getItens().stream()
				.filter(itemVenda -> itemVenda.getId() != null && itemVenda.getId().equals(itemId))
				.findFirst()
				.orElseThrow(() -> new VendaValidationException("Item não encontrado nesta venda."));
		item.setPrecoFinal(precoFinal);
		venda.recalcularTotal();
		return vendaRepository.save(venda);
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
	 * Reprocessa um orçamento com os preços atuais do catálogo, reproduzindo a
	 * entrada original de cada item (medidas/escolhas gravadas no snapshot);
	 * reseta o preço final e reinicia a validade de 15 dias.
	 */
	@Transactional
	public Venda recalcular(UUID id) {
		Venda venda = findById(id);
		if (venda.getStatus() != StatusVenda.ORCAMENTO) {
			throw new VendaValidationException("Somente orçamentos podem ser recalculados.");
		}
		Categoria categoria = venda.getCliente() != null ? venda.getCliente().getCategoria() : null;
		venda.getItens().forEach(item -> aplicarCalculo(item, reconstruirEntrada(item), categoria));
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
		aplicarCalculo(item, itemRequest, categoria);
		return item;
	}

	/** Roda o motor com a entrada do item e congela custo/margem/preços/detalhamento. */
	private void aplicarCalculo(ItemVenda item, VendaItemRequest entrada, Categoria categoria) {
		ProdutoCalculoRequest calculoRequest = new ProdutoCalculoRequest();
		calculoRequest.setAltura(entrada.getAltura());
		calculoRequest.setLargura(entrada.getLargura());
		calculoRequest.setQuantidade(entrada.getQuantidade());
		calculoRequest.setCategoria(categoria);
		calculoRequest.setMedidas(entrada.getMedidas());
		calculoRequest.setEscolhasMateria(entrada.getEscolhasMateria());
		calculoRequest.setEscolhasOpcao(entrada.getEscolhasOpcao());

		ProdutoCalculoResponse calculo = produtoCalculoService.calcular(item.getProdutoId(), calculoRequest);

		item.setProdutoNome(calculo.getProdutoNome());
		item.setDescricao(montarDescricao(entrada, calculo));
		item.setCustoTotal(calculo.getTotalGeral());
		item.setMarkupAplicado(categoria == Categoria.R ? calculo.getMarkupAtacado() : calculo.getMarkupVarejo());
		item.setPrecoSugerido(calculo.getPrecoSugerido());
		item.setPrecoFinal(calculo.getPrecoSugerido()); // default editável; reset ao recalcular
		item.setEntradaJson(serializarEntrada(entrada));

		List<ItemVendaDetalhe> detalhes = new ArrayList<>();
		calculo.getMateriais().forEach(linha -> detalhes.add(congelarDetalhe(linha)));
		calculo.getServicos().forEach(linha -> detalhes.add(congelarDetalhe(linha)));
		item.setDetalhes(detalhes);
	}

	/**
	 * Descrição por extenso do item, congelada no snapshot. Formato estruturado
	 * (evita concordância de gênero da prosa):
	 * "1 × PRODUTO · MATERIAL ESCOLHIDO · A × L cm · MEDIDA v · OPÇÃO...".
	 */
	private String montarDescricao(VendaItemRequest entrada, ProdutoCalculoResponse calculo) {
		StringBuilder texto = new StringBuilder();
		texto.append(formatarNumero(entrada.getQuantidade())).append(" × ").append(calculo.getProdutoNome());
		for (String material : calculo.getMateriaisEscolhidos()) {
			texto.append(" · ").append(material);
		}
		texto.append(" · ").append(formatarNumero(entrada.getAltura())).append(" × ")
				.append(formatarNumero(entrada.getLargura())).append(" cm");
		if (entrada.getMedidas() != null) {
			entrada.getMedidas().forEach((nome, valor) -> {
				if (valor != null && valor.signum() != 0) {
					texto.append(" · ").append(nome.toUpperCase()).append(" ").append(formatarNumero(valor));
				}
			});
		}
		Set<String> opcoes = new LinkedHashSet<>();
		calculo.getMateriais().forEach(linha -> {
			if (linha.getOpcaoNome() != null) {
				opcoes.add(linha.getOpcaoNome());
			}
		});
		calculo.getServicos().forEach(linha -> {
			if (linha.getOpcaoNome() != null) {
				opcoes.add(linha.getOpcaoNome());
			}
		});
		opcoes.forEach(opcao -> texto.append(" · ").append(opcao));
		return texto.length() > 500 ? texto.substring(0, 500) : texto.toString();
	}

	private String formatarNumero(BigDecimal valor) {
		return valor == null ? "" : valor.stripTrailingZeros().toPlainString();
	}

	private String serializarEntrada(VendaItemRequest entrada) {
		try {
			return objectMapper.writeValueAsString(entrada);
		} catch (Exception e) {
			throw new VendaValidationException("Não foi possível registrar a entrada do item: " + e.getMessage());
		}
	}

	/** Reconstrói a entrada original do item; itens antigos (sem JSON) usam só as medidas básicas. */
	private VendaItemRequest reconstruirEntrada(ItemVenda item) {
		if (item.getEntradaJson() != null && !item.getEntradaJson().isBlank()) {
			try {
				VendaItemRequest entrada = objectMapper.readValue(item.getEntradaJson(), VendaItemRequest.class);
				entrada.setProdutoId(item.getProdutoId());
				return entrada;
			} catch (Exception e) {
				throw new VendaValidationException(
						"Não foi possível ler a entrada registrada do item: " + e.getMessage());
			}
		}
		VendaItemRequest entrada = new VendaItemRequest();
		entrada.setProdutoId(item.getProdutoId());
		entrada.setAltura(item.getAltura());
		entrada.setLargura(item.getLargura());
		entrada.setQuantidade(item.getQuantidade());
		return entrada;
	}

	private ItemVendaDetalhe congelarDetalhe(ProdutoCalculoItemResponse linha) {
		ItemVendaDetalhe detalhe = new ItemVendaDetalhe();
		detalhe.setNome(linha.getNome());
		detalhe.setTipoItem(linha.getTipoItem());
		detalhe.setOpcaoNome(linha.getOpcaoNome());
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
