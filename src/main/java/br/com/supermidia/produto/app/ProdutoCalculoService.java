package br.com.supermidia.produto.app;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.supermidia.calculo.domain.BaseOperacionalCalculo;
import br.com.supermidia.calculo.domain.CodigoParametroCalculo;
import br.com.supermidia.calculo.domain.TipoCalculo;
import br.com.supermidia.materia.domain.Materia;
import br.com.supermidia.materia.infra.MateriaRepository;
import br.com.supermidia.pessoa.cliente.domain.Cliente.Categoria;
import br.com.supermidia.produto.api.dto.ProdutoCalculoItemResponse;
import br.com.supermidia.produto.api.dto.ProdutoCalculoRequest;
import br.com.supermidia.produto.api.dto.ProdutoCalculoResponse;
import br.com.supermidia.produto.api.dto.ProdutoEscolhaMateriaRequest;
import br.com.supermidia.produto.domain.Produto;
import br.com.supermidia.produto.domain.ProdutoComponente;
import br.com.supermidia.produto.domain.ProdutoComponenteParametro;
import br.com.supermidia.produto.domain.ProdutoGrupoOpcao;
import br.com.supermidia.produto.domain.ProdutoMedida;
import br.com.supermidia.produto.domain.ProdutoOpcao;
import br.com.supermidia.produto.domain.ProdutoOpcaoContribuicao;
import br.com.supermidia.produto.domain.ProdutoParametroVinculoMedida;
import br.com.supermidia.produto.domain.TipoItemComponente;

@Service
public class ProdutoCalculoService {

	private static final BigDecimal CEM = new BigDecimal("100");
	private static final BigDecimal DEZ_MIL = new BigDecimal("10000");
	private static final int SCALE_QUANTIDADE = 4;
	private static final int SCALE_VALOR = 2;
	// Constantes de margem (futuramente uma configuração global do sistema).
	private static final BigDecimal PISO_MARGEM = new BigDecimal("0.35");
	private static final BigDecimal FATOR_VAREJO = new BigDecimal("1.3846");

	private final ProdutoService produtoService;
	private final MateriaRepository materiaRepository;

	public ProdutoCalculoService(ProdutoService produtoService, MateriaRepository materiaRepository) {
		this.produtoService = produtoService;
		this.materiaRepository = materiaRepository;
	}

	@Transactional(readOnly = true)
	public ProdutoCalculoResponse calcular(UUID produtoId, ProdutoCalculoRequest request) {
		Produto produto = produtoService.findById(produtoId);

		ProdutoCalculoContext context = new ProdutoCalculoContext(request.getAltura(), request.getLargura(),
				request.getQuantidade());
		Map<String, BigDecimal> medidas = resolverMedidas(produto, request.getMedidas());
		List<ProdutoOpcao> opcoesAtivas = resolverOpcoes(produto, request.getEscolhasOpcao());
		Map<CodigoParametroCalculo, BigDecimal> contribuicoes = somarContribuicoes(opcoesAtivas);
		Map<UUID, Materia> materiasEscolhidas = resolverEscolhasDeMateria(request.getEscolhasMateria());

		ProdutoCalculoResponse response = new ProdutoCalculoResponse();
		response.setProdutoId(produto.getId());
		response.setProdutoNome(produto.getNome());
		response.setAltura(request.getAltura());
		response.setLargura(request.getLargura());
		response.setQuantidade(request.getQuantidade());

		List<ProdutoCalculoItemResponse> materiais = new ArrayList<>();
		List<ProdutoCalculoItemResponse> servicos = new ArrayList<>();
		List<String> materiaisEscolhidosNomes = new ArrayList<>();
		BigDecimal baseMateriais = BigDecimal.ZERO;
		BigDecimal baseServicos = BigDecimal.ZERO;

		// Componentes BASE: recebem contribuições das opções e formam a margem.
		for (ProdutoComponente componente : produto.getComponentes()) {
			ProdutoCalculoItemResponse item = calcularComponente(componente, medidas, contribuicoes,
					materiasEscolhidas, materiaisEscolhidosNomes, null, context);
			if (componente.getTipoItem() == TipoItemComponente.MATERIA) {
				materiais.add(item);
				baseMateriais = baseMateriais.add(item.getValorTotal());
			} else {
				servicos.add(item);
				baseServicos = baseServicos.add(item.getValorTotal());
			}
		}

		// Componentes das opções ativas: somam custo e herdam a margem (modelo A).
		for (ProdutoOpcao opcao : opcoesAtivas) {
			for (ProdutoComponente componente : opcao.getComponentes()) {
				ProdutoCalculoItemResponse item = calcularComponente(componente, medidas, Map.of(),
						materiasEscolhidas, materiaisEscolhidosNomes, opcao.getNome(), context);
				if (componente.getTipoItem() == TipoItemComponente.MATERIA) {
					materiais.add(item);
				} else {
					servicos.add(item);
				}
			}
		}
		response.setMateriaisEscolhidos(materiaisEscolhidosNomes);

		BigDecimal totalMateriais = somarTotais(materiais);
		BigDecimal totalServicos = somarTotais(servicos);
		BigDecimal custoTotal = totalMateriais.add(totalServicos).setScale(SCALE_VALOR, RoundingMode.HALF_UP);

		response.setMateriais(materiais);
		response.setServicos(servicos);
		response.setTotalMateriais(totalMateriais);
		response.setTotalServicos(totalServicos);
		response.setTotalGeral(custoTotal);

		aplicarPrecificacao(response, baseMateriais, baseServicos, custoTotal, request.getCategoria());

		return response;
	}

	/** Valores efetivos das medidas: informado > padrão; valida obrigatoriedade e limites. */
	private Map<String, BigDecimal> resolverMedidas(Produto produto, Map<String, BigDecimal> informadas) {
		Map<String, BigDecimal> normalizadas = new HashMap<>();
		if (informadas != null) {
			informadas.forEach((nome, valor) -> normalizadas.put(nome.trim().toUpperCase(), valor));
		}

		Map<String, BigDecimal> medidas = new HashMap<>();
		for (ProdutoMedida medida : produto.getMedidas()) {
			String nome = medida.getNome().trim().toUpperCase();
			BigDecimal valor = normalizadas.remove(nome);
			if (valor == null) {
				valor = medida.getValorPadrao();
			}
			if (valor == null) {
				if (medida.isObrigatoria()) {
					throw new ProdutoCalculoValidationException("A medida " + medida.getNome() + " é obrigatória.");
				}
				valor = BigDecimal.ZERO;
			}
			if (medida.getMinimo() != null && valor.compareTo(medida.getMinimo()) < 0) {
				throw new ProdutoCalculoValidationException("A medida " + medida.getNome() + " deve ser no mínimo "
						+ medida.getMinimo() + (medida.getUnidade() != null ? " " + medida.getUnidade() : "") + ".");
			}
			if (medida.getMaximo() != null && valor.compareTo(medida.getMaximo()) > 0) {
				throw new ProdutoCalculoValidationException("A medida " + medida.getNome() + " deve ser no máximo "
						+ medida.getMaximo() + (medida.getUnidade() != null ? " " + medida.getUnidade() : "") + ".");
			}
			medidas.put(nome, valor);
		}

		if (!normalizadas.isEmpty()) {
			throw new ProdutoCalculoValidationException(
					"Medida não declarada no produto: " + normalizadas.keySet().iterator().next());
		}
		return medidas;
	}

	/** Opções ativas: no máximo uma por grupo; grupos obrigatórios exigem escolha. */
	private List<ProdutoOpcao> resolverOpcoes(Produto produto, List<UUID> escolhas) {
		Map<UUID, ProdutoOpcao> porId = new HashMap<>();
		for (ProdutoGrupoOpcao grupo : produto.getGruposOpcoes()) {
			for (ProdutoOpcao opcao : grupo.getOpcoes()) {
				porId.put(opcao.getId(), opcao);
			}
		}

		List<ProdutoOpcao> ativas = new ArrayList<>();
		Set<UUID> gruposUsados = new HashSet<>();
		for (UUID escolha : escolhas == null ? List.<UUID>of() : escolhas) {
			ProdutoOpcao opcao = porId.get(escolha);
			if (opcao == null) {
				throw new ProdutoCalculoValidationException("Opção não pertence ao produto: " + escolha);
			}
			if (!gruposUsados.add(opcao.getGrupo().getId())) {
				throw new ProdutoCalculoValidationException(
						"Escolha apenas uma opção do grupo " + opcao.getGrupo().getNome() + ".");
			}
			ativas.add(opcao);
		}

		for (ProdutoGrupoOpcao grupo : produto.getGruposOpcoes()) {
			if (grupo.isObrigatorio() && !gruposUsados.contains(grupo.getId())) {
				throw new ProdutoCalculoValidationException(
						"Escolha uma opção do grupo " + grupo.getNome() + ".");
			}
		}
		return ativas;
	}

	private Map<CodigoParametroCalculo, BigDecimal> somarContribuicoes(List<ProdutoOpcao> opcoesAtivas) {
		Map<CodigoParametroCalculo, BigDecimal> contribuicoes = new EnumMap<>(CodigoParametroCalculo.class);
		for (ProdutoOpcao opcao : opcoesAtivas) {
			for (ProdutoOpcaoContribuicao contribuicao : opcao.getContribuicoes()) {
				contribuicoes.merge(contribuicao.getCodigo(), contribuicao.getValor(), BigDecimal::add);
			}
		}
		return contribuicoes;
	}

	private Map<UUID, Materia> resolverEscolhasDeMateria(List<ProdutoEscolhaMateriaRequest> escolhas) {
		Map<UUID, Materia> materiasEscolhidas = new HashMap<>();
		for (ProdutoEscolhaMateriaRequest escolha : escolhas == null ? List.<ProdutoEscolhaMateriaRequest>of()
				: escolhas) {
			Materia materia = materiaRepository.findById(escolha.getMateriaId())
					.orElseThrow(() -> new ProdutoCalculoValidationException(
							"Matéria escolhida não encontrada: " + escolha.getMateriaId()));
			materiasEscolhidas.put(escolha.getComponenteId(), materia);
		}
		return materiasEscolhidas;
	}

	private ProdutoCalculoItemResponse calcularComponente(ProdutoComponente componente,
			Map<String, BigDecimal> medidas, Map<CodigoParametroCalculo, BigDecimal> contribuicoes,
			Map<UUID, Materia> materiasEscolhidas, List<String> materiaisEscolhidosNomes, String opcaoNome,
			ProdutoCalculoContext context) {

		String nome;
		String unidade;
		BigDecimal precoUnitario;
		if (componente.getTipoItem() == TipoItemComponente.MATERIA) {
			Materia materia;
			if (componente.isSlot()) {
				materia = resolverMateriaDoSlot(componente, materiasEscolhidas);
				materiaisEscolhidosNomes.add(materia.getNome());
			} else {
				materia = componente.getMateria();
			}
			nome = materia.getNome();
			unidade = materia.getUnidade().name();
			precoUnitario = materia.getPreco();
		} else {
			nome = componente.getServico().getNome();
			unidade = componente.getServico().getUnidade().name();
			precoUnitario = componente.getServico().getPreco();
		}

		BigDecimal quantidadeCalculada = calcularQuantidade(componente.getCalculo().getTipoCalculo(),
				componente.getCalculo().getBaseOperacional(),
				extrairParametros(componente, medidas, contribuicoes), context);

		ProdutoCalculoItemResponse response = new ProdutoCalculoItemResponse();
		response.setNome(nome);
		response.setTipoItem(componente.getTipoItem().name());
		response.setOpcaoNome(opcaoNome);
		response.setCalculo(componente.getCalculo().getNome());
		response.setTipoCalculo(componente.getCalculo().getTipoCalculo());
		response.setBaseOperacional(componente.getCalculo().getBaseOperacional());
		response.setQuantidadeCalculada(quantidadeCalculada);
		response.setUnidade(unidade);
		response.setPrecoUnitario(precoUnitario.setScale(SCALE_VALOR, RoundingMode.HALF_UP));
		response.setValorTotal(quantidadeCalculada.multiply(precoUnitario).setScale(SCALE_VALOR, RoundingMode.HALF_UP));
		return response;
	}

	private Materia resolverMateriaDoSlot(ProdutoComponente componente, Map<UUID, Materia> materiasEscolhidas) {
		Materia materia = materiasEscolhidas.get(componente.getId());
		if (materia == null) {
			throw new ProdutoCalculoValidationException("Escolha o material do grupo '"
					+ componente.getGrupoMateriaSlot() + "' para calcular este produto.");
		}
		String grupoSlot = componente.getGrupoMateriaSlot().trim();
		if (materia.getGrupo() == null || !materia.getGrupo().trim().equalsIgnoreCase(grupoSlot)) {
			throw new ProdutoCalculoValidationException("A matéria " + materia.getNome()
					+ " não pertence ao grupo '" + grupoSlot + "' deste componente.");
		}
		return materia;
	}

	/**
	 * Parâmetro efetivo = constante + Σ(medida × multiplicador dos vínculos)
	 * + Σ(contribuições das opções ativas com o mesmo código — só componentes base).
	 */
	private Map<CodigoParametroCalculo, BigDecimal> extrairParametros(ProdutoComponente componente,
			Map<String, BigDecimal> medidas, Map<CodigoParametroCalculo, BigDecimal> contribuicoes) {
		Map<CodigoParametroCalculo, BigDecimal> parametros = new EnumMap<>(CodigoParametroCalculo.class);
		for (ProdutoComponenteParametro parametro : componente.getParametros()) {
			BigDecimal valor = parametro.getValorConstante() != null ? parametro.getValorConstante() : BigDecimal.ZERO;
			for (ProdutoParametroVinculoMedida vinculo : parametro.getVinculos()) {
				BigDecimal valorMedida = medidas.getOrDefault(vinculo.getMedidaNome().trim().toUpperCase(),
						BigDecimal.ZERO);
				valor = valor.add(valorMedida.multiply(vinculo.getMultiplicador()));
			}
			BigDecimal contribuicao = contribuicoes.get(parametro.getCodigo());
			if (contribuicao != null) {
				valor = valor.add(contribuicao);
			}
			parametros.put(parametro.getCodigo(), valor);
		}
		return parametros;
	}

	/**
	 * Margem automática (modelo A): calculada SÓ sobre os componentes base
	 * (material puxa margem, mão de obra não, piso de 35%); aplicada sobre o
	 * custo total (opções herdam a margem). Varejo = atacado × fator fixo.
	 */
	private void aplicarPrecificacao(ProdutoCalculoResponse response, BigDecimal baseMateriais,
			BigDecimal baseServicos, BigDecimal custoTotal, Categoria categoria) {
		BigDecimal margemAtacado = calcularMargemAtacado(baseMateriais, baseServicos);
		BigDecimal precoAtacado = custoTotal.multiply(BigDecimal.ONE.add(margemAtacado))
				.setScale(SCALE_VALOR, RoundingMode.HALF_UP);
		BigDecimal precoVarejo = precoAtacado.multiply(FATOR_VAREJO).setScale(SCALE_VALOR, RoundingMode.HALF_UP);

		BigDecimal markupAtacado = margemAtacado.multiply(CEM).setScale(SCALE_VALOR, RoundingMode.HALF_UP);
		BigDecimal markupVarejo = BigDecimal.ONE.add(margemAtacado).multiply(FATOR_VAREJO).subtract(BigDecimal.ONE)
				.multiply(CEM).setScale(SCALE_VALOR, RoundingMode.HALF_UP);

		response.setMarkupAtacado(markupAtacado);
		response.setMarkupVarejo(markupVarejo);
		response.setPrecoAtacado(precoAtacado);
		response.setPrecoVarejo(precoVarejo);

		if (categoria != null) {
			response.setPrecoSugerido(categoria == Categoria.R ? precoAtacado : precoVarejo);
		}
	}

	private BigDecimal calcularMargemAtacado(BigDecimal custoMateriais, BigDecimal custoServicos) {
		if (custoMateriais.signum() <= 0) {
			return PISO_MARGEM; // sem material não há razão a calcular: aplica o piso
		}
		BigDecimal razao = custoServicos.divide(custoMateriais, 10, RoundingMode.HALF_UP);
		return BigDecimal.ONE.subtract(razao).max(PISO_MARGEM);
	}

	private BigDecimal somarTotais(List<ProdutoCalculoItemResponse> itens) {
		return itens.stream().map(ProdutoCalculoItemResponse::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add)
				.setScale(SCALE_VALOR, RoundingMode.HALF_UP);
	}

	private BigDecimal calcularQuantidade(TipoCalculo tipoCalculo, BaseOperacionalCalculo baseOperacional,
			Map<CodigoParametroCalculo, BigDecimal> parametros, ProdutoCalculoContext context) {
		BigDecimal quantidade = switch (tipoCalculo) {
		case AREA_BASE -> context.areaBaseM2();
		case AREA_COM_FATOR ->
			context.areaBaseM2().multiply(getParametroObrigatorio(parametros, CodigoParametroCalculo.FATOR));
		case AREA_COM_ACRESCIMOS_E_FATOR -> calcularAreaComAcrescimosEFator(parametros, context);
		case PERIMETRO_BASE -> resolverBase(baseOperacional, context);
		case PERIMETRO_COM_ESPACAMENTO -> calcularPorEspacamento(baseOperacional, parametros, context);
		case UNIDADE_FIXA -> getParametroObrigatorio(parametros, CodigoParametroCalculo.QUANTIDADE_FIXA)
				.multiply(context.quantidade());
		// Taxa por item do orçamento (ex.: ajuste de arte): não escala com a quantidade.
		case TAXA_FIXA -> getParametroObrigatorio(parametros, CodigoParametroCalculo.QUANTIDADE_FIXA);
		case QUANTIDADE_INFORMADA, METRO_LINEAR_INFORMADO, SELECAO_POR_MEDIDA ->
			throw new ProdutoCalculoValidationException(
					"O tipo de cálculo " + tipoCalculo + " ainda não foi implementado na execução do produto.");
		};
		return quantidade.setScale(SCALE_QUANTIDADE, RoundingMode.HALF_UP);
	}

	private BigDecimal calcularAreaComAcrescimosEFator(Map<CodigoParametroCalculo, BigDecimal> parametros,
			ProdutoCalculoContext context) {
		BigDecimal acrescimoAlturaCm = getParametroObrigatorio(parametros, CodigoParametroCalculo.ACRESCIMO_ALTURA);
		BigDecimal acrescimoLarguraCm = getParametroObrigatorio(parametros, CodigoParametroCalculo.ACRESCIMO_LARGURA);
		BigDecimal fator = getParametroObrigatorio(parametros, CodigoParametroCalculo.FATOR);

		BigDecimal alturaComAcrescimoM = context.alturaCm().add(acrescimoAlturaCm).divide(CEM, 10, RoundingMode.HALF_UP);
		BigDecimal larguraComAcrescimoM = context.larguraCm().add(acrescimoLarguraCm).divide(CEM, 10, RoundingMode.HALF_UP);
		BigDecimal areaPorPeca = alturaComAcrescimoM.multiply(larguraComAcrescimoM);

		return areaPorPeca.multiply(fator).multiply(context.quantidade());
	}

	private BigDecimal calcularPorEspacamento(BaseOperacionalCalculo baseOperacional,
			Map<CodigoParametroCalculo, BigDecimal> parametros, ProdutoCalculoContext context) {
		BigDecimal espacamentoCm = getParametroObrigatorio(parametros, CodigoParametroCalculo.ESPACAMENTO);
		BigDecimal baseEmMetros = resolverBase(baseOperacional, context);
		BigDecimal espacamentoEmMetros = espacamentoCm.divide(CEM, 10, RoundingMode.HALF_UP);

		return BigDecimal.valueOf(
				Math.ceil(baseEmMetros.divide(espacamentoEmMetros, 10, RoundingMode.HALF_UP).doubleValue()));
	}

	private BigDecimal resolverBase(BaseOperacionalCalculo baseOperacional, ProdutoCalculoContext context) {
		return switch (baseOperacional) {
		case AREA -> context.areaBaseM2();
		case PERIMETRO -> context.perimetroM();
		case ALTURA_SIMPLES -> context.alturaM().multiply(context.quantidade());
		case ALTURA_DUPLA -> context.alturaM().multiply(context.quantidade()).multiply(new BigDecimal("2"));
		case LARGURA_SIMPLES -> context.larguraM().multiply(context.quantidade());
		case LARGURA_DUPLA -> context.larguraM().multiply(context.quantidade()).multiply(new BigDecimal("2"));
		case QUANTIDADE_INFORMADA, METRO_LINEAR_INFORMADO ->
			throw new ProdutoCalculoValidationException("A base operacional " + baseOperacional + " ainda não foi implementada.");
		};
	}

	private BigDecimal getParametroObrigatorio(Map<CodigoParametroCalculo, BigDecimal> parametros,
			CodigoParametroCalculo codigo) {
		BigDecimal valor = parametros.get(codigo);
		if (valor == null) {
			throw new ProdutoCalculoValidationException("O parâmetro " + codigo + " é obrigatório para executar o cálculo.");
		}
		return valor;
	}

	private record ProdutoCalculoContext(BigDecimal alturaCm, BigDecimal larguraCm, BigDecimal quantidade) {
		private BigDecimal alturaM() {
			return alturaCm.divide(CEM, 10, RoundingMode.HALF_UP);
		}

		private BigDecimal larguraM() {
			return larguraCm.divide(CEM, 10, RoundingMode.HALF_UP);
		}

		private BigDecimal areaBaseM2() {
			return alturaCm.multiply(larguraCm).divide(DEZ_MIL, 10, RoundingMode.HALF_UP).multiply(quantidade);
		}

		private BigDecimal perimetroM() {
			return alturaM().add(larguraM()).multiply(new BigDecimal("2")).multiply(quantidade);
		}
	}
}
