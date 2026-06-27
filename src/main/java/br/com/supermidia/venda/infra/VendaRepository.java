package br.com.supermidia.venda.infra;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.supermidia.venda.domain.StatusVenda;
import br.com.supermidia.venda.domain.Venda;

public interface VendaRepository extends JpaRepository<Venda, UUID> {

	// Sustenta as duas telas da UI: "Orçamentos" (ORCAMENTO) e "Ordens de Serviço" (ORDEM_SERVICO).
	List<Venda> findByStatus(StatusVenda status);
}
