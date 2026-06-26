package br.com.supermidia.pessoa.dominio.infra;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.supermidia.pessoa.dominio.domain.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, UUID> {
	List<Pessoa> findByNomeContainingIgnoreCase(String nome);

	boolean existsByNome(String nome);

	boolean existsByEmail(String email);

	boolean existsByTelefone(String telefone);

	boolean existsByNomeAndIdNot(String nome, UUID id);

	boolean existsByEmailAndIdNot(String email, UUID id);

	boolean existsByTelefoneAndIdNot(String telefone, UUID id);
}
