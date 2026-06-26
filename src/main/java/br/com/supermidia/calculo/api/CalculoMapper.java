package br.com.supermidia.calculo.api;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import br.com.supermidia.calculo.api.dto.CalculoCreateRequest;
import br.com.supermidia.calculo.api.dto.CalculoResponse;
import br.com.supermidia.calculo.api.dto.CalculoUpdateRequest;
import br.com.supermidia.calculo.domain.Calculo;

@Mapper(componentModel = "spring")
public interface CalculoMapper {

	CalculoResponse toResponse(Calculo entity);

	@Mapping(target = "id", ignore = true)
	Calculo toEntity(CalculoCreateRequest request);

	@Mapping(target = "id", ignore = true)
	void updateEntity(CalculoUpdateRequest request, @MappingTarget Calculo entity);
}
