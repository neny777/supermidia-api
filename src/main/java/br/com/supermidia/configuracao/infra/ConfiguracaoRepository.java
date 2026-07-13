package br.com.supermidia.configuracao.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.supermidia.configuracao.domain.Configuracao;

public interface ConfiguracaoRepository extends JpaRepository<Configuracao, Integer> {
}
