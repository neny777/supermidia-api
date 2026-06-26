package br.com.supermidia.pessoa.dominio.app;

import org.springframework.stereotype.Service;

import br.com.supermidia.pessoa.dominio.domain.Fisica;
import br.com.supermidia.pessoa.dominio.infra.FisicaRepository;

@Service
public class FisicaService {
	private final FisicaRepository fisicaRepository;

	public FisicaService(FisicaRepository fisicaRepository) {
		this.fisicaRepository = fisicaRepository;
	}

	public Fisica cadastrarOuAtualizar(Fisica fisica) {
		validarAtributosUnicos(fisica);
		return fisicaRepository.save(fisica);
	}

	private void validarAtributosUnicos(Fisica fisica) {
		
		if (fisica.getRg() != null && fisicaRepository.findByRg(fisica.getRg()).isPresent()) {
			throw new IllegalArgumentException("Já existe uma pessoa física com o RG informado.");
		}
		if (fisica.getCpf() != null && fisicaRepository.findByCpf(fisica.getCpf()).isPresent()) {
			throw new IllegalArgumentException("Já existe uma pessoa física com o CPF informado.");
		}
		if (fisica.getEmail() != null && fisicaRepository.findByEmail(fisica.getEmail()).isPresent()) {
			throw new IllegalArgumentException("Já existe uma pessoa física com o e-mail informado.");
		}
		if (fisica.getTelefone() != null && fisicaRepository.findByTelefone(fisica.getTelefone()).isPresent()) {
			throw new IllegalArgumentException("Já existe uma pessoa física com o telefone informado.");
		}
		if (fisica.getNome() != null && fisicaRepository.findByNome(fisica.getNome()).isPresent()) {
			throw new IllegalArgumentException("Já existe uma pessoa física com o nome informado.");
		}
	}
}
