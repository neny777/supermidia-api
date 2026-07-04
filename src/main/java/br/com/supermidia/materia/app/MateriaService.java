package br.com.supermidia.materia.app;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.supermidia.materia.domain.Materia;
import br.com.supermidia.materia.infra.MateriaRepository;

@Service
public class MateriaService {

	private final MateriaRepository materiaRepository;

	public MateriaService(MateriaRepository materiaRepository) {
		this.materiaRepository = materiaRepository;
	}

	@Transactional
	public Materia create(Materia materia) {
		validarNomeUnico(materia.getNome(), null);
		return materiaRepository.save(materia);
	}

	@Transactional(readOnly = true)
	public List<Materia> findAll() {
		return materiaRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Materia findById(UUID id) {
		return materiaRepository.findById(id)
				.orElseThrow(() -> new MateriaNotFoundException("Matéria não encontrada: " + id));
	}

	@Transactional(readOnly = true)
	public List<String> findGrupos() {
		return materiaRepository.findGrupos();
	}

	@Transactional
	public Materia update(UUID id, java.util.function.Consumer<Materia> updater) {
		Materia current = findById(id);
		updater.accept(current);
		validarNomeUnico(current.getNome(), id);
		return materiaRepository.save(current);
	}

	@Transactional
	public void delete(UUID id) {
		Materia current = findById(id);
		materiaRepository.delete(current);
	}

	private void validarNomeUnico(String nome, UUID idAtual) {
		materiaRepository.findByNomeIgnoreCase(nome)
				.filter(materia -> !materia.getId().equals(idAtual))
				.ifPresent(materia -> {
					throw new MateriaValidationException("Já existe uma matéria cadastrada com este nome.");
				});
	}
}
