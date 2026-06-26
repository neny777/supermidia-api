package br.com.supermidia.pessoa.dominio.infra;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.supermidia.pessoa.dominio.domain.Juridica;

public interface JuridicaRepository extends JpaRepository<Juridica, UUID> {
	boolean existsByIe(String ie);

	boolean existsByCnpj(String cnpj);

	boolean existsByIeAndIdNot(String ie, UUID id);

	boolean existsByCnpjAndIdNot(String cnpj, UUID id);
	
	Optional<Juridica> findByIe(String ie);

	Optional<Juridica> findByCnpj(String cnpj);

	Optional<Juridica> findByEmail(String email);

	Optional<Juridica> findByTelefone(String telefone);

	Optional<Juridica> findByNome(String nome);

	@Query("SELECT j FROM Juridica j WHERE " + "(:nome IS NULL OR j.nome = :nome) AND "
			+ "(:email IS NULL OR j.email = :email) AND " + "(:telefone IS NULL OR j.telefone = :telefone) AND "
			+ "(:cnpj IS NULL OR j.cnpj = :cnpj) AND " + "(:ie IS NULL OR j.ie = :ie)")
	Optional<Juridica> findByAttributes(@Param("nome") String nome, @Param("email") String email,
			@Param("telefone") String telefone, @Param("cnpj") String cnpj, @Param("ie") String ie);
}
