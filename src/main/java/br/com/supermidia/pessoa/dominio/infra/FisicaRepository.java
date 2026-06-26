package br.com.supermidia.pessoa.dominio.infra;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.supermidia.pessoa.dominio.domain.Fisica;

public interface FisicaRepository extends JpaRepository<Fisica, UUID> {
	boolean existsByRg(String rg);

	boolean existsByCpf(String cpf);

	boolean existsByRgAndIdNot(String rg, UUID id);

	boolean existsByCpfAndIdNot(String cpf, UUID id);
	
	Optional<Fisica> findByRg(String rg);

	Optional<Fisica> findByCpf(String cpf);

	Optional<Fisica> findByEmail(String email);

	Optional<Fisica> findByTelefone(String telefone);

	Optional<Fisica> findByNome(String nome);

	@Query("SELECT f FROM Fisica f WHERE " + "(:nome IS NULL OR f.nome = :nome) AND "
			+ "(:email IS NULL OR f.email = :email) AND " + "(:telefone IS NULL OR f.telefone = :telefone) AND "
			+ "(:cpf IS NULL OR f.cpf = :cpf) AND " + "(:rg IS NULL OR f.rg = :rg)")
	Optional<Fisica> findByAttributes(@Param("nome") String nome, @Param("email") String email,
			@Param("telefone") String telefone, @Param("cpf") String cpf, @Param("rg") String rg);
}
