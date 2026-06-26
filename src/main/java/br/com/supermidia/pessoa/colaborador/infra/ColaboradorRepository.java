package br.com.supermidia.pessoa.colaborador.infra;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.supermidia.pessoa.colaborador.domain.Colaborador;

public interface ColaboradorRepository extends JpaRepository<Colaborador, UUID> {
	
	boolean existsByFisicaId(UUID id);

	boolean existsByFisicaNome(String nome);

	boolean existsByFisicaEmail(String email);

	boolean existsByFisicaTelefone(String telefone);

	boolean existsByFisicaNomeAndIdNot(String nome, UUID id);

	boolean existsByFisicaEmailAndIdNot(String email, UUID id);

	boolean existsByFisicaTelefoneAndIdNot(String telefone, UUID id);

	Colaborador findByFisicaNome(String nome);

	Colaborador findByFisicaEmail(String email);

	Colaborador findByFisicaTelefone(String telefone);
}
