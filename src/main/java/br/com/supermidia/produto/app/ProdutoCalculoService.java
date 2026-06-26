package br.com.supermidia.produto.app;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.supermidia.calculo.domain.BaseOperacionalCalculo;
import br.com.supermidia.calculo.domain.CodigoParametroCalculo;
import br.com.supermidia.calculo.domain.TipoCalculo;
import br.com.supermidia.produto.api.dto.ProdutoCalculoItemResponse;
import br.com.supermidia.produto.api.dto.ProdutoCalculoRequest;
import br.com.supermidia.produto.api.dto.ProdutoCalculoResponse;
import br.com.supermidia.produto.domain.Produto;
import br.com.supermidia.produto.domain.ProdutoMateriaCalculo;
import br.com.supermidia.produto.domain.ProdutoMateriaParametroCalculo;
import br.com.supermidia.produto.domain.ProdutoServicoCalculo;
import br.com.supermidia.produto.domain.ProdutoServicoParametroCalculo;

@Service
public class ProdutoCalculoService {

	private static final BigDecimal CEM = new BigDecimal("100");
	private static final BigDecimal DEZ_MIL = new BigDecimal("10000");
	private static final int SCALE_QUANTIDADE = 4;
	private static final int SCALE_VALOR = 2;

	private final ProdutoService produtoService;

	public ProdutoCalculoService(ProdutoService produtoService) {
		this.produtoService = produtoService;
	}

	@Transactional(readOnly = true)
	public ProdutoCalculoResponse calcular(UUID produtoId, ProdutoCalculoRequest request) {
		Produto produto = produtoService.findById(produtoId);

		ProdutoCalculoContext context = new ProdutoCalculoContext(request.getAltura(), request.getLargura(),
				request.getQuantidade());

		ProdutoCalculoResponse response = new ProdutoCalculoResponse();
		response.setProdutoId(produto.getId());
		response.setProdutoNome(produto.getNome());
		response.setAltura(request.getAltura());
		response.setLargura(request.getLargura());
		response.setQuantidade(request.getQuantidade());

		List<ProdutoCalculoItemResponse> materiais = new ArrayList<>();
		for (ProdutoMateriaCalculo item : produto.getMateriasCalculo()) {
			materiais.add(calcularMateria(item, context));
		}

		List<ProdutoCalculoItemResponse> servicos = new ArrayList<>();
		for (ProdutoServicoCalculo item : produto.getServicosCalculo()) {
			servicos.add(calcularServico(item, context));
		}

		BigDecimal totalMateriais = somarTotais(materiais);
		BigDecimal totalServicos = somarTotais(servicos);

		response.setMateriais(materiais);
		response.setServicos(servicos);
		response.setTotalMateriais(totalMateriais);
		response.setTotalServicos(totalServicos);
		response.setTotalGeral(totalMateriais.add(totalServicos).setScale(SCALE_VALOR, RoundingMode.HALF_UP));

		return response;
	}

	private ProdutoCalculoItemResponse calcularMateria(ProdutoMateriaCalculo item, ProdutoCalculoContext context) {
		BigDecimal quantidadeCalculada = calcularQuantidade(item.getCalculo().getTipoCalculo(),
				item.getCalculo().getBaseOperacional(), extrairParametrosMateria(item), context);
		return toResponse(item.getMateria().getNome(), "MATERIA", item.getCalculo().getNome(),
				item.getCalculo().getTipoCalculo(), item.getCalculo().getBaseOperacional(), quantidadeCalculada,
				item.getMateria().getUnidade().name(), item.getMateria().getPreco());
	}

	private ProdutoCalculoItemResponse calcularServico(ProdutoServicoCalculo item, ProdutoCalculoContext context) {
		BigDecimal quantidadeCalculada = calcularQuantidade(item.getCalculo().getTipoCalculo(),
				item.getCalculo().getBaseOperacional(), extrairParametrosServico(item), context);
		return toResponse(item.getServico().getNome(), "SERVICO", item.getCalculo().getNome(),
				item.getCalculo().getTipoCalculo(), item.getCalculo().getBaseOperacional(), quantidadeCalculada,
				item.getServico().getUnidade().name(), item.getServico().getPreco());
	}

	private ProdutoCalculoItemResponse toResponse(String nome, String tipoItem, String calculo, TipoCalculo tipoCalculo,
			BaseOperacionalCalculo baseOperacional, BigDecimal quantidadeCalculada, String unidade,
			BigDecimal precoUnitario) {
		ProdutoCalculoItemResponse response = new ProdutoCalculoItemResponse();
		response.setNome(nome);
		response.setTipoItem(tipoItem);
		response.setCalculo(calculo);
		response.setTipoCalculo(tipoCalculo);
		response.setBaseOperacional(baseOperacional);
		response.setQuantidadeCalculada(quantidadeCalculada);
		response.setUnidade(unidade);
		response.setPrecoUnitario(precoUnitario.setScale(SCALE_VALOR, RoundingMode.HALF_UP));
		response.setValorTotal(quantidadeCalculada.multiply(precoUnitario).setScale(SCALE_VALOR, RoundingMode.HALF_UP));
		return response;
	}

	private BigDecimal somarTotais(List<ProdutoCalculoItemResponse> itens) {
		return itens.stream().map(ProdutoCalculoItemResponse::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add)
				.setScale(SCALE_VALOR, RoundingMode.HALF_UP);
	}

	private Map<CodigoParametroCalculo, BigDecimal> extrairParametrosMateria(ProdutoMateriaCalculo item) {
		Map<CodigoParametroCalculo, BigDecimal> parametros = new EnumMap<>(CodigoParametroCalculo.class);
		for (ProdutoMateriaParametroCalculo parametro : item.getParametros()) {
			parametros.put(parametro.getCodigo(), parametro.getValor());
		}
		return parametros;
	}

	private Map<CodigoParametroCalculo, BigDecimal> extrairParametrosServico(ProdutoServicoCalculo item) {
		Map<CodigoParametroCalculo, BigDecimal> parametros = new EnumMap<>(CodigoParametroCalculo.class);
		for (ProdutoServicoParametroCalculo parametro : item.getParametros()) {
			parametros.put(parametro.getCodigo(), parametro.getValor());
		}
		return parametros;
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
		case QUANTIDADE_INFORMADA, METRO_LINEAR_INFORMADO, SELECAO_POR_MEDIDA, UNIDADE_FIXA ->
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
