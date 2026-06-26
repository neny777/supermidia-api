package br.com.supermidia.produto.infra;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.supermidia.produto.domain.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

	Optional<Produto> findByNomeIgnoreCase(String nome);
}
