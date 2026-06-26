package br.com.supermidia.servico.infra;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.supermidia.servico.domain.Servico;

public interface ServicoRepository extends JpaRepository<Servico, UUID> {

	Optional<Servico> findByNomeIgnoreCase(String nome);
}
