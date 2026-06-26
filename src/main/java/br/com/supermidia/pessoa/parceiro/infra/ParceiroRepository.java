package br.com.supermidia.pessoa.parceiro.infra;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.supermidia.pessoa.parceiro.domain.Parceiro;

public interface ParceiroRepository extends JpaRepository<Parceiro, UUID> {
	
	boolean existsByPessoaId(UUID pessoaId);

	boolean existsByPessoaNome(String nome);

	boolean existsByPessoaEmail(String email);

	boolean existsByPessoaTelefone(String telefone);

	boolean existsByPessoaNomeAndIdNot(String nome, UUID id);

	boolean existsByPessoaEmailAndIdNot(String email, UUID id);

	boolean existsByPessoaTelefoneAndIdNot(String telefone, UUID id);

	Parceiro findByPessoaNome(String nome);

	Parceiro findByPessoaEmail(String email);

	Parceiro findByPessoaTelefone(String telefone);

	@Query(value = """
			    SELECT BIN_TO_UUID(e.pessoa_id) AS parceiroId,
			           p.nome AS nome,
			           p.email AS email,
			           p.telefone AS telefone,
			           p.municipio AS municipio,
			           p.uf AS uf
			    FROM parceiros e
			    JOIN pessoas p ON e.pessoa_id = p.id
			    LEFT JOIN pessoas_fisica f ON p.id = f.id
			    LEFT JOIN pessoas_juridica j ON p.id = j.id
			""", nativeQuery = true)
	List<Object[]> findAllParceiros();

	@Query("SELECT e FROM Parceiro e WHERE e.pessoa.id = :pessoaId")
	Optional<Parceiro> findByPessoaId(@Param("pessoaId") UUID pessoaId);
}
