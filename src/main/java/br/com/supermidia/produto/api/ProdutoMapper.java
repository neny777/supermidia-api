package br.com.supermidia.produto.api;

import java.util.List;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import br.com.supermidia.calculo.domain.Calculo;
import br.com.supermidia.materia.domain.Materia;
import br.com.supermidia.produto.api.dto.ProdutoCreateRequest;
import br.com.supermidia.produto.api.dto.ProdutoMateriaCalculoRequest;
import br.com.supermidia.produto.api.dto.ProdutoMateriaCalculoResponse;
import br.com.supermidia.produto.api.dto.ProdutoParametroCalculoRequest;
import br.com.supermidia.produto.api.dto.ProdutoParametroCalculoResponse;
import br.com.supermidia.produto.api.dto.ProdutoResponse;
import br.com.supermidia.produto.api.dto.ProdutoServicoCalculoRequest;
import br.com.supermidia.produto.api.dto.ProdutoServicoCalculoResponse;
import br.com.supermidia.produto.api.dto.ProdutoUpdateRequest;
import br.com.supermidia.produto.domain.Produto;
import br.com.supermidia.produto.domain.ProdutoMateriaCalculo;
import br.com.supermidia.produto.domain.ProdutoMateriaParametroCalculo;
import br.com.supermidia.produto.domain.ProdutoServicoCalculo;
import br.com.supermidia.produto.domain.ProdutoServicoParametroCalculo;
import br.com.supermidia.servico.domain.Servico;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "materiasCalculo", ignore = true)
	@Mapping(target = "servicosCalculo", ignore = true)
	Produto toEntity(ProdutoCreateRequest request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "materiasCalculo", ignore = true)
	@Mapping(target = "servicosCalculo", ignore = true)
	void updateEntity(ProdutoUpdateRequest request, @MappingTarget Produto entity);

	ProdutoResponse toResponse(Produto entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "produto", ignore = true)
	@Mapping(target = "materia", source = "materiaId")
	@Mapping(target = "calculo", source = "calculoId")
	@Mapping(target = "parametros", ignore = true)
	ProdutoMateriaCalculo toEntity(ProdutoMateriaCalculoRequest request);

	List<ProdutoMateriaCalculo> toMateriaCalculoEntities(List<ProdutoMateriaCalculoRequest> requests);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "produto", ignore = true)
	@Mapping(target = "servico", source = "servicoId")
	@Mapping(target = "calculo", source = "calculoId")
	@Mapping(target = "parametros", ignore = true)
	ProdutoServicoCalculo toEntity(ProdutoServicoCalculoRequest request);

	List<ProdutoServicoCalculo> toServicoCalculoEntities(List<ProdutoServicoCalculoRequest> requests);

	@Mapping(target = "materiaId", source = "materia.id")
	@Mapping(target = "materiaNome", source = "materia.nome")
	@Mapping(target = "calculoId", source = "calculo.id")
	@Mapping(target = "calculoNome", source = "calculo.nome")
	ProdutoMateriaCalculoResponse toResponse(ProdutoMateriaCalculo entity);

	@Mapping(target = "servicoId", source = "servico.id")
	@Mapping(target = "servicoNome", source = "servico.nome")
	@Mapping(target = "calculoId", source = "calculo.id")
	@Mapping(target = "calculoNome", source = "calculo.nome")
	ProdutoServicoCalculoResponse toResponse(ProdutoServicoCalculo entity);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "produtoMateriaCalculo", ignore = true)
	ProdutoMateriaParametroCalculo toMateriaParametroEntity(ProdutoParametroCalculoRequest request);

	default List<ProdutoMateriaParametroCalculo> toMateriaParametroEntities(List<ProdutoParametroCalculoRequest> requests) {
		if (requests == null) {
			return List.of();
		}
		return requests.stream().map(this::toMateriaParametroEntity).toList();
	}

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "produtoServicoCalculo", ignore = true)
	ProdutoServicoParametroCalculo toServicoParametroEntity(ProdutoParametroCalculoRequest request);

	default List<ProdutoServicoParametroCalculo> toServicoParametroEntities(List<ProdutoParametroCalculoRequest> requests) {
		if (requests == null) {
			return List.of();
		}
		return requests.stream().map(this::toServicoParametroEntity).toList();
	}

	ProdutoParametroCalculoResponse toResponse(ProdutoMateriaParametroCalculo entity);

	ProdutoParametroCalculoResponse toResponse(ProdutoServicoParametroCalculo entity);

	default Materia mapMateria(UUID id) {
		if (id == null) {
			return null;
		}
		Materia materia = new Materia();
		materia.setId(id);
		return materia;
	}

	default Servico mapServico(UUID id) {
		if (id == null) {
			return null;
		}
		Servico servico = new Servico();
		servico.setId(id);
		return servico;
	}

	default Calculo mapCalculo(UUID id) {
		if (id == null) {
			return null;
		}
		Calculo calculo = new Calculo();
		calculo.setId(id);
		return calculo;
	}
}
