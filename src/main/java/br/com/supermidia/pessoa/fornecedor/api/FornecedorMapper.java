package br.com.supermidia.pessoa.fornecedor.api;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import br.com.supermidia.pessoa.fornecedor.api.dto.FornecedorFisicoDTO;
import br.com.supermidia.pessoa.fornecedor.api.dto.FornecedorJuridicoDTO;
import br.com.supermidia.pessoa.fornecedor.domain.Fornecedor;
import br.com.supermidia.pessoa.dominio.domain.Fisica;
import br.com.supermidia.pessoa.dominio.domain.Juridica;
import br.com.supermidia.pessoa.dominio.domain.Pessoa;

@Mapper(componentModel = "spring")
public interface FornecedorMapper {

	// Fornecedor -> FornecedorFisicoDTO
	@Mapping(source = "fornecedor.id", target = "id")
	@Mapping(source = "fisica.nome", target = "nome")
	@Mapping(source = "fisica.email", target = "email")
	@Mapping(source = "fisica.telefone", target = "telefone")
	@Mapping(source = "fisica.cep", target = "cep")
	@Mapping(source = "fisica.logradouro", target = "logradouro")
	@Mapping(source = "fisica.numero", target = "numero")
	@Mapping(source = "fisica.bairro", target = "bairro")
	@Mapping(source = "fisica.municipio", target = "municipio")
	@Mapping(source = "fisica.uf", target = "uf")
	@Mapping(source = "fisica.cpf", target = "cpf")
	@Mapping(source = "fisica.rg", target = "rg")
	@Mapping(source = "fisica.sexo", target = "sexo")
	@Mapping(source = "fisica.dataNascimento", target = "nascimento")
	@Mapping(target = "tipo", expression = "java(getTipo(fisica))") // Mapeia o tipo de Pessoa
	FornecedorFisicoDTO toFornecedorFisicoDTO(Fisica fisica, Fornecedor fornecedor);

	// Fornecedor -> FornecedorJuridicoDTO
	@Mapping(source = "fornecedor.id", target = "id")
	@Mapping(source = "juridica.nome", target = "nome")
	@Mapping(source = "juridica.email", target = "email")
	@Mapping(source = "juridica.telefone", target = "telefone")
	@Mapping(source = "juridica.cep", target = "cep")
	@Mapping(source = "juridica.logradouro", target = "logradouro")
	@Mapping(source = "juridica.numero", target = "numero")
	@Mapping(source = "juridica.bairro", target = "bairro")
	@Mapping(source = "juridica.municipio", target = "municipio")
	@Mapping(source = "juridica.uf", target = "uf")
	@Mapping(source = "juridica.cnpj", target = "cnpj")
	@Mapping(source = "juridica.ie", target = "ie")
	@Mapping(target = "tipo", expression = "java(getTipo(juridica))") // Mapeia o tipo de Pessoa
	FornecedorJuridicoDTO toFornecedorJuridicoDTO(Juridica juridica, Fornecedor fornecedor);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "pessoa", expression = "java(updatePessoaFisica(fornecedorFisicoDTO, fornecedor.getPessoa()))")
	void updateFornecedorFisicoFromDTO(FornecedorFisicoDTO fornecedorFisicoDTO, @MappingTarget Fornecedor fornecedor);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "pessoa", expression = "java(updatePessoaJuridica(fornecedorJuridicoDTO, fornecedor.getPessoa()))")
	void updateFornecedorJuridicoFromDTO(FornecedorJuridicoDTO fornecedorJuridicoDTO, @MappingTarget Fornecedor fornecedor);

	@Mapping(target = "cpf", source = "cpf")
	@Mapping(target = "rg", source = "rg")
	@Mapping(target = "sexo", source = "sexo")
	@Mapping(target = "dataNascimento", source = "nascimento")
	@Mapping(target = "nome", source = "nome")
	@Mapping(target = "email", source = "email")
	@Mapping(target = "telefone", source = "telefone")
	@Mapping(target = "cep", source = "cep")
	@Mapping(target = "logradouro", source = "logradouro")
	@Mapping(target = "numero", source = "numero")
	@Mapping(target = "bairro", source = "bairro")
	@Mapping(target = "municipio", source = "municipio")
	@Mapping(target = "uf", source = "uf")
	void updateFisicaFromDTO(FornecedorFisicoDTO fornecedorFisicoDTO, @MappingTarget Fisica fisica);
	
	@Mapping(target = "cnpj", source = "cnpj")
	@Mapping(target = "ie", source = "ie")
	@Mapping(target = "nome", source = "nome")
	@Mapping(target = "email", source = "email")
	@Mapping(target = "telefone", source = "telefone")
	@Mapping(target = "cep", source = "cep")
	@Mapping(target = "logradouro", source = "logradouro")
	@Mapping(target = "numero", source = "numero")
	@Mapping(target = "bairro", source = "bairro")
	@Mapping(target = "municipio", source = "municipio")
	@Mapping(target = "uf", source = "uf")
	void updateJuridicaFromDTO(FornecedorJuridicoDTO fornecedorJuridicoDTO, @MappingTarget Juridica juridica);

	@Mapping(target = "id", source = "id")
	@Mapping(target = "pessoa", expression = "java(toFisica(fornecedorFisicoDTO))")
	Fornecedor toFornecedorFisico(FornecedorFisicoDTO fornecedorFisicoDTO);
	
	@Mapping(target = "id", source = "id")
	@Mapping(target = "pessoa", expression = "java(toJuridica(fornecedorJuridicoDTO))")
	Fornecedor toFornecedorJuridico(FornecedorJuridicoDTO fornecedorJuridicoDTO);

	@Mapping(target = "id", ignore = true) // ID será gerado automaticamente
	@Mapping(target = "cpf", source = "cpf")
	@Mapping(target = "rg", source = "rg")
	@Mapping(target = "sexo", source = "sexo")
	@Mapping(target = "dataNascimento", source = "nascimento")
	@Mapping(target = "nome", source = "nome")
	@Mapping(target = "email", source = "email")
	@Mapping(target = "telefone", source = "telefone")
	@Mapping(target = "cep", source = "cep")
	@Mapping(target = "logradouro", source = "logradouro")
	@Mapping(target = "numero", source = "numero")
	@Mapping(target = "bairro", source = "bairro")
	@Mapping(target = "municipio", source = "municipio")
	@Mapping(target = "uf", source = "uf")
	Fisica toFisica(FornecedorFisicoDTO fornecedorFisicoDTO);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "cnpj", source = "cnpj")
	@Mapping(target = "ie", source = "ie")
	@Mapping(target = "nome", source = "nome")
	@Mapping(target = "email", source = "email")
	@Mapping(target = "telefone", source = "telefone")
	@Mapping(target = "cep", source = "cep")
	@Mapping(target = "logradouro", source = "logradouro")
	@Mapping(target = "numero", source = "numero")
	@Mapping(target = "bairro", source = "bairro")
	@Mapping(target = "municipio", source = "municipio")
	@Mapping(target = "uf", source = "uf")
	Juridica toJuridica(FornecedorJuridicoDTO fornecedorJuridicoDTO);

	default Pessoa updatePessoaFisica(FornecedorFisicoDTO fornecedorFisicoDTO, Pessoa pessoa) {
		if (pessoa instanceof Fisica fisica) {
			updateFisicaFromDTO(fornecedorFisicoDTO, fisica);
		} else {
			throw new IllegalStateException("A pessoa associada não é do tipo Física.");
		}
		return pessoa;
	}
	
	default Pessoa updatePessoaJuridica(FornecedorJuridicoDTO fornecedorJuridicoDTO, Pessoa pessoa) {
		if (pessoa instanceof Juridica juridica) {
			updateJuridicaFromDTO(fornecedorJuridicoDTO, juridica);
		} else {
			throw new IllegalStateException("A pessoa associada não é do tipo Jurídica.");
		}
		return pessoa;
	}
	
	default String getTipo(Pessoa pessoa) {
        if (pessoa instanceof Fisica) {
            return "Fisica";
        } else if (pessoa instanceof Juridica) {
            return "Juridica";
        }
        throw new IllegalArgumentException("Tipo de pessoa desconhecido: " + pessoa.getClass().getName());
    }
}
