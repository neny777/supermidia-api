package br.com.supermidia.materia.api;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import br.com.supermidia.materia.api.dto.MateriaCreateRequest;
import br.com.supermidia.materia.api.dto.MateriaResponse;
import br.com.supermidia.materia.api.dto.MateriaUpdateRequest;
import br.com.supermidia.materia.domain.Materia;

@Mapper(componentModel = "spring")
public interface MateriaMapper {

	MateriaResponse toResponse(Materia entity);

	@Mapping(target = "id", ignore = true)
	Materia toEntity(MateriaCreateRequest request);

	@Mapping(target = "id", ignore = true)
	void updateEntity(MateriaUpdateRequest request, @MappingTarget Materia entity);
}
