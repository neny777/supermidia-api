package br.com.supermidia.pessoa.dominio.app;

import org.springframework.stereotype.Service;

import br.com.supermidia.pessoa.dominio.domain.Juridica;
import br.com.supermidia.pessoa.dominio.infra.JuridicaRepository;

@Service
public class JuridicaService {

    private final JuridicaRepository juridicaRepository;

    public JuridicaService(JuridicaRepository juridicaRepository) {
        this.juridicaRepository = juridicaRepository;
    }

    public Juridica cadastrarOuAtualizar(Juridica juridica) {
        validarAtributosUnicos(juridica);

        // Salva ou atualiza a entidade
        return juridicaRepository.save(juridica);
    }

    private void validarAtributosUnicos(Juridica juridica) {
    	// Verificar IE
        if (juridica.getIe() != null && juridicaRepository.findByIe(juridica.getIe()).isPresent()) {
            throw new IllegalArgumentException("Já existe uma pessoa jurídica com a Inscrição Estadual informada.");
        }
        // Verificar CNPJ
        if (juridica.getCnpj() != null && juridicaRepository.findByCnpj(juridica.getCnpj()).isPresent()) {
            throw new IllegalArgumentException("Já existe uma pessoa jurídica com o CNPJ informado.");
        }

        // Verificar e-mail
        if (juridica.getEmail() != null && juridicaRepository.findByEmail(juridica.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Já existe uma pessoa jurídica com o e-mail informado.");
        }

        // Verificar telefone
        if (juridica.getTelefone() != null && juridicaRepository.findByTelefone(juridica.getTelefone()).isPresent()) {
            throw new IllegalArgumentException("Já existe uma pessoa jurídica com o telefone informado.");
        }

        // Verificar nome
        if (juridica.getNome() != null && juridicaRepository.findByNome(juridica.getNome()).isPresent()) {
            throw new IllegalArgumentException("Já existe uma pessoa jurídica com o nome informado.");
        }
    }
}
