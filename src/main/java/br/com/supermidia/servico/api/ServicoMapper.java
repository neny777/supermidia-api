package br.com.supermidia.servico.api;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import br.com.supermidia.servico.api.dto.ServicoCreateRequest;
import br.com.supermidia.servico.api.dto.ServicoResponse;
import br.com.supermidia.servico.api.dto.ServicoUpdateRequest;
import br.com.supermidia.servico.domain.Servico;

@Mapper(componentModel = "spring")
public interface ServicoMapper {

	ServicoResponse toResponse(Servico entity);

	@Mapping(target = "id", ignore = true)
	Servico toEntity(ServicoCreateRequest request);

	@Mapping(target = "id", ignore = true)
	void updateEntity(ServicoUpdateRequest request, @MappingTarget Servico entity);
}
