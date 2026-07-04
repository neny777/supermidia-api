package br.com.supermidia.materia.infra;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.supermidia.materia.domain.Materia;

public interface MateriaRepository extends JpaRepository<Materia, UUID> {

	boolean existsByNomeIgnoreCase(String nome);

	Optional<Materia> findByNomeIgnoreCase(String nome);

	@Query("SELECT DISTINCT m.grupo FROM Materia m WHERE m.grupo IS NOT NULL ORDER BY m.grupo")
	List<String> findGrupos();

	List<Materia> findByGrupoOrderByNome(String grupo);
}
