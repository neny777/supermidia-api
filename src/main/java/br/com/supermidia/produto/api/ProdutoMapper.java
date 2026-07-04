package br.com.supermidia.produto.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mapstruct.Mapper;

import br.com.supermidia.calculo.domain.Calculo;
import br.com.supermidia.materia.domain.Materia;
import br.com.supermidia.produto.api.dto.ProdutoContribuicaoRequest;
import br.com.supermidia.produto.api.dto.ProdutoContribuicaoResponse;
import br.com.supermidia.produto.api.dto.ProdutoCreateRequest;
import br.com.supermidia.produto.api.dto.ProdutoGrupoOpcaoRequest;
import br.com.supermidia.produto.api.dto.ProdutoGrupoOpcaoResponse;
import br.com.supermidia.produto.api.dto.ProdutoMateriaCalculoRequest;
import br.com.supermidia.produto.api.dto.ProdutoMateriaCalculoResponse;
import br.com.supermidia.produto.api.dto.ProdutoMedidaRequest;
import br.com.supermidia.produto.api.dto.ProdutoMedidaResponse;
import br.com.supermidia.produto.api.dto.ProdutoOpcaoRequest;
import br.com.supermidia.produto.api.dto.ProdutoOpcaoResponse;
import br.com.supermidia.produto.api.dto.ProdutoParametroCalculoRequest;
import br.com.supermidia.produto.api.dto.ProdutoParametroCalculoResponse;
import br.com.supermidia.produto.api.dto.ProdutoResponse;
import br.com.supermidia.produto.api.dto.ProdutoServicoCalculoRequest;
import br.com.supermidia.produto.api.dto.ProdutoServicoCalculoResponse;
import br.com.supermidia.produto.api.dto.ProdutoUpdateRequest;
import br.com.supermidia.produto.api.dto.ProdutoVinculoMedidaRequest;
import br.com.supermidia.produto.api.dto.ProdutoVinculoMedidaResponse;
import br.com.supermidia.produto.domain.Produto;
import br.com.supermidia.produto.domain.ProdutoComponente;
import br.com.supermidia.produto.domain.ProdutoComponenteParametro;
import br.com.supermidia.produto.domain.ProdutoGrupoOpcao;
import br.com.supermidia.produto.domain.ProdutoMedida;
import br.com.supermidia.produto.domain.ProdutoOpcao;
import br.com.supermidia.produto.domain.ProdutoOpcaoContribuicao;
import br.com.supermidia.produto.domain.ProdutoParametroVinculoMedida;
import br.com.supermidia.produto.domain.TipoItemComponente;
import br.com.supermidia.servico.domain.Servico;

/**
 * Montagem manual do grafo produto <-> DTOs. A API mantém as listas
 * materiasCalculo/servicosCalculo (compatibilidade) sobre o modelo unificado
 * de componentes.
 */
@Mapper(componentModel = "spring")
public interface ProdutoMapper {

	// ---------- request -> entidade ----------

	default Produto toEntity(ProdutoCreateRequest request) {
		Produto produto = new Produto();
		aplicar(produto, request.getNome(), request.getMedidas(), request.getMateriasCalculo(),
				request.getServicosCalculo(), request.getGruposOpcoes());
		return produto;
	}

	default void updateEntity(ProdutoUpdateRequest request, Produto produto) {
		aplicar(produto, request.getNome(), request.getMedidas(), request.getMateriasCalculo(),
				request.getServicosCalculo(), request.getGruposOpcoes());
	}

	private void aplicar(Produto produto, String nome, List<ProdutoMedidaRequest> medidas,
			List<ProdutoMateriaCalculoRequest> materias, List<ProdutoServicoCalculoRequest> servicos,
			List<ProdutoGrupoOpcaoRequest> grupos) {
		produto.setNome(nome);
		produto.setMedidas(toMedidas(medidas));
		produto.setComponentes(toComponentes(materias, servicos));
		produto.setGruposOpcoes(toGrupos(grupos));
	}

	private List<ProdutoMedida> toMedidas(List<ProdutoMedidaRequest> requests) {
		List<ProdutoMedida> medidas = new ArrayList<>();
		for (ProdutoMedidaRequest request : requests == null ? List.<ProdutoMedidaRequest>of() : requests) {
			ProdutoMedida medida = new ProdutoMedida();
			medida.setNome(request.getNome());
			medida.setUnidade(request.getUnidade());
			medida.setObrigatoria(request.isObrigatoria());
			medida.setValorPadrao(request.getValorPadrao());
			medida.setMinimo(request.getMinimo());
			medida.setMaximo(request.getMaximo());
			medidas.add(medida);
		}
		return medidas;
	}

	private List<ProdutoComponente> toComponentes(List<ProdutoMateriaCalculoRequest> materias,
			List<ProdutoServicoCalculoRequest> servicos) {
		List<ProdutoComponente> componentes = new ArrayList<>();
		for (ProdutoMateriaCalculoRequest request : materias == null ? List.<ProdutoMateriaCalculoRequest>of()
				: materias) {
			componentes.add(toMateriaComponente(request));
		}
		for (ProdutoServicoCalculoRequest request : servicos == null ? List.<ProdutoServicoCalculoRequest>of()
				: servicos) {
			componentes.add(toServicoComponente(request));
		}
		return componentes;
	}

	private ProdutoComponente toMateriaComponente(ProdutoMateriaCalculoRequest request) {
		ProdutoComponente componente = new ProdutoComponente();
		componente.setTipoItem(TipoItemComponente.MATERIA);
		componente.setMateria(materiaRef(request.getMateriaId()));
		componente.setGrupoMateriaSlot(request.getGrupoSlot());
		componente.setCalculo(calculoRef(request.getCalculoId()));
		componente.setParametros(toParametros(request.getParametros()));
		return componente;
	}

	private ProdutoComponente toServicoComponente(ProdutoServicoCalculoRequest request) {
		ProdutoComponente componente = new ProdutoComponente();
		componente.setTipoItem(TipoItemComponente.SERVICO);
		componente.setServico(servicoRef(request.getServicoId()));
		componente.setCalculo(calculoRef(request.getCalculoId()));
		componente.setParametros(toParametros(request.getParametros()));
		return componente;
	}

	private List<ProdutoComponenteParametro> toParametros(List<ProdutoParametroCalculoRequest> requests) {
		List<ProdutoComponenteParametro> parametros = new ArrayList<>();
		for (ProdutoParametroCalculoRequest request : requests == null ? List.<ProdutoParametroCalculoRequest>of()
				: requests) {
			ProdutoComponenteParametro parametro = new ProdutoComponenteParametro();
			parametro.setCodigo(request.getCodigo());
			parametro.setValorConstante(request.getValor());
			List<ProdutoParametroVinculoMedida> vinculos = new ArrayList<>();
			for (ProdutoVinculoMedidaRequest vinculoRequest : request.getVinculos() == null
					? List.<ProdutoVinculoMedidaRequest>of()
					: request.getVinculos()) {
				ProdutoParametroVinculoMedida vinculo = new ProdutoParametroVinculoMedida();
				vinculo.setMedidaNome(vinculoRequest.getMedidaNome());
				vinculo.setMultiplicador(vinculoRequest.getMultiplicador());
				vinculos.add(vinculo);
			}
			parametro.setVinculos(vinculos);
			parametros.add(parametro);
		}
		return parametros;
	}

	private List<ProdutoGrupoOpcao> toGrupos(List<ProdutoGrupoOpcaoRequest> requests) {
		List<ProdutoGrupoOpcao> grupos = new ArrayList<>();
		for (ProdutoGrupoOpcaoRequest request : requests == null ? List.<ProdutoGrupoOpcaoRequest>of() : requests) {
			ProdutoGrupoOpcao grupo = new ProdutoGrupoOpcao();
			grupo.setNome(request.getNome());
			grupo.setObrigatorio(request.isObrigatorio());
			List<ProdutoOpcao> opcoes = new ArrayList<>();
			for (ProdutoOpcaoRequest opcaoRequest : request.getOpcoes()) {
				opcoes.add(toOpcao(opcaoRequest));
			}
			grupo.setOpcoes(opcoes);
			grupos.add(grupo);
		}
		return grupos;
	}

	private ProdutoOpcao toOpcao(ProdutoOpcaoRequest request) {
		ProdutoOpcao opcao = new ProdutoOpcao();
		opcao.setNome(request.getNome());
		opcao.setComponentes(toComponentes(request.getMateriasCalculo(), request.getServicosCalculo()));
		List<ProdutoOpcaoContribuicao> contribuicoes = new ArrayList<>();
		for (ProdutoContribuicaoRequest contribuicaoRequest : request.getContribuicoes() == null
				? List.<ProdutoContribuicaoRequest>of()
				: request.getContribuicoes()) {
			ProdutoOpcaoContribuicao contribuicao = new ProdutoOpcaoContribuicao();
			contribuicao.setCodigo(contribuicaoRequest.getCodigo());
			contribuicao.setValor(contribuicaoRequest.getValor());
			contribuicoes.add(contribuicao);
		}
		opcao.setContribuicoes(contribuicoes);
		return opcao;
	}

	private Materia materiaRef(UUID id) {
		if (id == null) {
			return null;
		}
		Materia materia = new Materia();
		materia.setId(id);
		return materia;
	}

	private Servico servicoRef(UUID id) {
		if (id == null) {
			return null;
		}
		Servico servico = new Servico();
		servico.setId(id);
		return servico;
	}

	private Calculo calculoRef(UUID id) {
		if (id == null) {
			return null;
		}
		Calculo calculo = new Calculo();
		calculo.setId(id);
		return calculo;
	}

	// ---------- entidade -> response ----------

	default ProdutoResponse toResponse(Produto entity) {
		ProdutoResponse response = new ProdutoResponse();
		response.setId(entity.getId());
		response.setNome(entity.getNome());
		response.setMedidas(entity.getMedidas().stream().map(this::toMedidaResponse).toList());
		response.setMateriasCalculo(materiaResponses(entity.getComponentes()));
		response.setServicosCalculo(servicoResponses(entity.getComponentes()));
		response.setGruposOpcoes(entity.getGruposOpcoes().stream().map(this::toGrupoResponse).toList());
		return response;
	}

	private ProdutoMedidaResponse toMedidaResponse(ProdutoMedida medida) {
		ProdutoMedidaResponse response = new ProdutoMedidaResponse();
		response.setId(medida.getId());
		response.setNome(medida.getNome());
		response.setUnidade(medida.getUnidade());
		response.setObrigatoria(medida.isObrigatoria());
		response.setValorPadrao(medida.getValorPadrao());
		response.setMinimo(medida.getMinimo());
		response.setMaximo(medida.getMaximo());
		return response;
	}

	private List<ProdutoMateriaCalculoResponse> materiaResponses(List<ProdutoComponente> componentes) {
		return componentes.stream().filter(c -> c.getTipoItem() == TipoItemComponente.MATERIA)
				.map(this::toMateriaResponse).toList();
	}

	private List<ProdutoServicoCalculoResponse> servicoResponses(List<ProdutoComponente> componentes) {
		return componentes.stream().filter(c -> c.getTipoItem() == TipoItemComponente.SERVICO)
				.map(this::toServicoResponse).toList();
	}

	private ProdutoMateriaCalculoResponse toMateriaResponse(ProdutoComponente componente) {
		ProdutoMateriaCalculoResponse response = new ProdutoMateriaCalculoResponse();
		response.setId(componente.getId());
		if (componente.getMateria() != null) {
			response.setMateriaId(componente.getMateria().getId());
			response.setMateriaNome(componente.getMateria().getNome());
		}
		response.setGrupoSlot(componente.getGrupoMateriaSlot());
		response.setCalculoId(componente.getCalculo().getId());
		response.setCalculoNome(componente.getCalculo().getNome());
		response.setParametros(parametroResponses(componente.getParametros()));
		return response;
	}

	private ProdutoServicoCalculoResponse toServicoResponse(ProdutoComponente componente) {
		ProdutoServicoCalculoResponse response = new ProdutoServicoCalculoResponse();
		response.setId(componente.getId());
		response.setServicoId(componente.getServico().getId());
		response.setServicoNome(componente.getServico().getNome());
		response.setCalculoId(componente.getCalculo().getId());
		response.setCalculoNome(componente.getCalculo().getNome());
		response.setParametros(parametroResponses(componente.getParametros()));
		return response;
	}

	private List<ProdutoParametroCalculoResponse> parametroResponses(List<ProdutoComponenteParametro> parametros) {
		List<ProdutoParametroCalculoResponse> responses = new ArrayList<>();
		for (ProdutoComponenteParametro parametro : parametros) {
			ProdutoParametroCalculoResponse response = new ProdutoParametroCalculoResponse();
			response.setId(parametro.getId());
			response.setCodigo(parametro.getCodigo());
			response.setValor(parametro.getValorConstante());
			response.setVinculos(parametro.getVinculos().stream().map(vinculo -> {
				ProdutoVinculoMedidaResponse vinculoResponse = new ProdutoVinculoMedidaResponse();
				vinculoResponse.setMedidaNome(vinculo.getMedidaNome());
				vinculoResponse.setMultiplicador(vinculo.getMultiplicador());
				return vinculoResponse;
			}).toList());
			responses.add(response);
		}
		return responses;
	}

	private ProdutoGrupoOpcaoResponse toGrupoResponse(ProdutoGrupoOpcao grupo) {
		ProdutoGrupoOpcaoResponse response = new ProdutoGrupoOpcaoResponse();
		response.setId(grupo.getId());
		response.setNome(grupo.getNome());
		response.setObrigatorio(grupo.isObrigatorio());
		response.setOpcoes(grupo.getOpcoes().stream().map(this::toOpcaoResponse).toList());
		return response;
	}

	private ProdutoOpcaoResponse toOpcaoResponse(ProdutoOpcao opcao) {
		ProdutoOpcaoResponse response = new ProdutoOpcaoResponse();
		response.setId(opcao.getId());
		response.setNome(opcao.getNome());
		response.setMateriasCalculo(materiaResponses(opcao.getComponentes()));
		response.setServicosCalculo(servicoResponses(opcao.getComponentes()));
		response.setContribuicoes(opcao.getContribuicoes().stream().map(contribuicao -> {
			ProdutoContribuicaoResponse contribuicaoResponse = new ProdutoContribuicaoResponse();
			contribuicaoResponse.setCodigo(contribuicao.getCodigo());
			contribuicaoResponse.setValor(contribuicao.getValor());
			return contribuicaoResponse;
		}).toList());
		return response;
	}
}
