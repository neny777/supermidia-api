package br.com.supermidia.calculo.app;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.supermidia.calculo.domain.Calculo;
import br.com.supermidia.calculo.infra.CalculoRepository;

@Service
public class CalculoService {

	private final CalculoRepository calculoRepository;

	public CalculoService(CalculoRepository calculoRepository) {
		this.calculoRepository = calculoRepository;
	}

	@Transactional
	public Calculo create(Calculo calculo) {
		validarNomeUnico(calculo.getNome(), null);
		return calculoRepository.save(calculo);
	}

	@Transactional(readOnly = true)
	public List<Calculo> findAll() {
		return calculoRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Calculo findById(UUID id) {
		return calculoRepository.findById(id)
				.orElseThrow(() -> new CalculoNotFoundException("Cálculo não encontrado: " + id));
	}

	@Transactional
	public Calculo update(UUID id, Consumer<Calculo> updater) {
		Calculo current = findById(id);
		updater.accept(current);
		validarNomeUnico(current.getNome(), id);
		return calculoRepository.save(current);
	}

	@Transactional
	public void delete(UUID id) {
		Calculo current = findById(id);
		calculoRepository.delete(current);
	}

	private void validarNomeUnico(String nome, UUID idAtual) {
		calculoRepository.findByNomeIgnoreCase(nome)
				.filter(calculo -> !calculo.getId().equals(idAtual))
				.ifPresent(calculo -> {
					throw new CalculoValidationException("Já existe um cálculo cadastrado com este nome.");
				});
	}
}
