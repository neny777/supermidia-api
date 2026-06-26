package br.com.supermidia.pessoa.colaborador.api;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import br.com.supermidia.pessoa.colaborador.api.dto.ColaboradorDTO;
import br.com.supermidia.pessoa.colaborador.domain.Colaborador;
import br.com.supermidia.pessoa.dominio.domain.Fisica;

@Mapper(componentModel = "spring")
public interface ColaboradorMapper {

	// Converte de ColaboradorDTO para Colaborador
	@Mapping(source = "cpf", target = "fisica.cpf")
	@Mapping(source = "nome", target = "fisica.nome")
	@Mapping(source = "email", target = "fisica.email")
	@Mapping(source = "telefone", target = "fisica.telefone")
	@Mapping(source = "cep", target = "fisica.cep")
	@Mapping(source = "logradouro", target = "fisica.logradouro")
	@Mapping(source = "numero", target = "fisica.numero")
	@Mapping(source = "bairro", target = "fisica.bairro")
	@Mapping(source = "municipio", target = "fisica.municipio")
	@Mapping(source = "uf", target = "fisica.uf")
	@Mapping(source = "rg", target = "fisica.rg")
	@Mapping(source = "sexo", target = "fisica.sexo")
	@Mapping(source = "nascimento", target = "fisica.dataNascimento")
	@Mapping(target = "usuario", ignore = true) // Ignora o campo usuario
	Colaborador toColaborador(ColaboradorDTO dto);

	// Converte de Colaborador para ColaboradorDTO
	@Mapping(source = "fisica.cpf", target = "cpf")
	@Mapping(source = "fisica.nome", target = "nome")
	@Mapping(source = "fisica.email", target = "email")
	@Mapping(source = "fisica.telefone", target = "telefone")
	@Mapping(source = "fisica.cep", target = "cep")
	@Mapping(source = "fisica.logradouro", target = "logradouro")
	@Mapping(source = "fisica.numero", target = "numero")
	@Mapping(source = "fisica.bairro", target = "bairro")
	@Mapping(source = "fisica.municipio", target = "municipio")
	@Mapping(source = "fisica.uf", target = "uf")
	@Mapping(source = "fisica.rg", target = "rg")
	@Mapping(source = "fisica.sexo", target = "sexo")
	@Mapping(source = "fisica.dataNascimento", target = "nascimento")
	ColaboradorDTO toColaboradorDTO(Colaborador colaborador);

	// Atualiza um Colaborador existente a partir de um ColaboradorDTO
	@Mapping(source = "cpf", target = "fisica.cpf")
	@Mapping(source = "nome", target = "fisica.nome")
	@Mapping(source = "email", target = "fisica.email")
	@Mapping(source = "telefone", target = "fisica.telefone")
	@Mapping(source = "cep", target = "fisica.cep")
	@Mapping(source = "logradouro", target = "fisica.logradouro")
	@Mapping(source = "numero", target = "fisica.numero")
	@Mapping(source = "bairro", target = "fisica.bairro")
	@Mapping(source = "municipio", target = "fisica.municipio")
	@Mapping(source = "uf", target = "fisica.uf")
	@Mapping(source = "rg", target = "fisica.rg")
	@Mapping(source = "sexo", target = "fisica.sexo")
	@Mapping(source = "nascimento", target = "fisica.dataNascimento")
	@Mapping(target = "usuario", ignore = true) // Ignora o campo usuario
	void updateColaboradorFromDTO(ColaboradorDTO dto, @MappingTarget Colaborador colaborador);

	// Mapeamento adicional para quando Fisica e Colaborador são fornecidos
	// separadamente
	@Mapping(source = "fisica.cpf", target = "cpf")
	@Mapping(source = "fisica.nome", target = "nome")
	@Mapping(source = "fisica.email", target = "email")
	@Mapping(source = "fisica.telefone", target = "telefone")
	@Mapping(source = "fisica.cep", target = "cep")
	@Mapping(source = "fisica.logradouro", target = "logradouro")
	@Mapping(source = "fisica.numero", target = "numero")
	@Mapping(source = "fisica.bairro", target = "bairro")
	@Mapping(source = "fisica.municipio", target = "municipio")
	@Mapping(source = "fisica.uf", target = "uf")
	@Mapping(source = "fisica.rg", target = "rg")
	@Mapping(source = "fisica.sexo", target = "sexo")
	@Mapping(source = "fisica.dataNascimento", target = "nascimento")
	@Mapping(source = "colaborador.ctps", target = "ctps")
	@Mapping(source = "colaborador.id", target = "id")
	ColaboradorDTO toColaboradorDTO(Fisica fisica, Colaborador colaborador);
}
