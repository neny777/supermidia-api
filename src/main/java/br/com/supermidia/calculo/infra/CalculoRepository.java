package br.com.supermidia.calculo.infra;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.supermidia.calculo.domain.Calculo;

public interface CalculoRepository extends JpaRepository<Calculo, UUID> {

	Optional<Calculo> findByNomeIgnoreCase(String nome);
}
