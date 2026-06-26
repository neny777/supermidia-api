package br.com.supermidia.materia.infra;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.supermidia.materia.domain.Materia;

public interface MateriaRepository extends JpaRepository<Materia, UUID> {

	boolean existsByNomeIgnoreCase(String nome);

	Optional<Materia> findByNomeIgnoreCase(String nome);
}
