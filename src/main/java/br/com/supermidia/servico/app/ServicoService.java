package br.com.supermidia.servico.app;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.supermidia.servico.domain.Servico;
import br.com.supermidia.servico.infra.ServicoRepository;

@Service
public class ServicoService {

	private final ServicoRepository servicoRepository;

	public ServicoService(ServicoRepository servicoRepository) {
		this.servicoRepository = servicoRepository;
	}

	@Transactional
	public Servico create(Servico servico) {
		validarNomeUnico(servico.getNome(), null);
		return servicoRepository.save(servico);
	}

	@Transactional(readOnly = true)
	public List<Servico> findAll() {
		return servicoRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Servico findById(UUID id) {
		return servicoRepository.findById(id)
				.orElseThrow(() -> new ServicoNotFoundException("Serviço não encontrado: " + id));
	}

	@Transactional
	public Servico update(UUID id, Consumer<Servico> updater) {
		Servico current = findById(id);
		updater.accept(current);
		validarNomeUnico(current.getNome(), id);
		return servicoRepository.save(current);
	}

	@Transactional
	public void delete(UUID id) {
		Servico current = findById(id);
		servicoRepository.delete(current);
	}

	private void validarNomeUnico(String nome, UUID idAtual) {
		servicoRepository.findByNomeIgnoreCase(nome)
				.filter(servico -> !servico.getId().equals(idAtual))
				.ifPresent(servico -> {
					throw new ServicoValidationException("Já existe um serviço cadastrado com este nome.");
				});
	}
}
