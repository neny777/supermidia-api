package br.com.supermidia.pessoa.fornecedor.infra;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.supermidia.pessoa.fornecedor.domain.Fornecedor;

public interface FornecedorRepository extends JpaRepository<Fornecedor, UUID> {
	boolean existsByPessoaId(UUID pessoaId);

	boolean existsByPessoaNome(String nome);

	boolean existsByPessoaEmail(String email);

	boolean existsByPessoaTelefone(String telefone);

	boolean existsByPessoaNomeAndIdNot(String nome, UUID id);

	boolean existsByPessoaEmailAndIdNot(String email, UUID id);

	boolean existsByPessoaTelefoneAndIdNot(String telefone, UUID id);

	Fornecedor findByPessoaNome(String nome);

	Fornecedor findByPessoaEmail(String email);

	Fornecedor findByPessoaTelefone(String telefone);

	@Query(value = """
			    SELECT BIN_TO_UUID(s.pessoa_id) AS fornecedorId,
			           p.nome AS nome,
			           p.email AS email,
			           p.telefone AS telefone,
			           p.municipio AS municipio,
			           p.uf AS uf
			    FROM fornecedores s
			    JOIN pessoas p ON s.pessoa_id = p.id
			    LEFT JOIN pessoas_fisica f ON p.id = f.id
			    LEFT JOIN pessoas_juridica j ON p.id = j.id
			""", nativeQuery = true)
	List<Object[]> findAllfornecedores();

	@Query("SELECT s FROM Fornecedor s WHERE s.pessoa.id = :pessoaId")
	Optional<Fornecedor> findByPessoaId(@Param("pessoaId") UUID pessoaId);
}
