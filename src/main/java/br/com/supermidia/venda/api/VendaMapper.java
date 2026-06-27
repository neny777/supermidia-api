package br.com.supermidia.venda.api;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.supermidia.venda.api.dto.VendaItemDetalheResponse;
import br.com.supermidia.venda.api.dto.VendaItemResponse;
import br.com.supermidia.venda.api.dto.VendaResponse;
import br.com.supermidia.venda.domain.ItemVenda;
import br.com.supermidia.venda.domain.ItemVendaDetalhe;
import br.com.supermidia.venda.domain.Venda;

@Mapper(componentModel = "spring")
public interface VendaMapper {

	@Mapping(target = "clienteId", source = "cliente.id")
	VendaResponse toResponse(Venda entity);

	VendaItemResponse toResponse(ItemVenda entity);

	VendaItemDetalheResponse toResponse(ItemVendaDetalhe entity);
}
