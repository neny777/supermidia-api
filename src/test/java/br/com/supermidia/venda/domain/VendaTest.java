package br.com.supermidia.venda.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import br.com.supermidia.configuracao.domain.ConfiguracaoGlobal;

class VendaTest {

	@Test
	void deveConverterOrcamentoRecenteEmOrdemDeServico() {
		Venda venda = new Venda(); // status default ORCAMENTO
		venda.setDataCriacao(LocalDateTime.now());

		venda.converterParaOrdemServico();

		assertThat(venda.getStatus()).isEqualTo(StatusVenda.ORDEM_SERVICO);
	}

	@Test
	void naoDeveConverterOrcamentoVencido() {
		Venda venda = new Venda();
		venda.setDataCriacao(LocalDateTime.now().minusDays(ConfiguracaoGlobal.getValidadeOrcamentoDias() + 1));

		assertThatThrownBy(venda::converterParaOrdemServico)
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("vencido");
	}

	@Test
	void naoDeveConverterQuandoNaoForOrcamento() {
		Venda venda = new Venda();
		venda.setStatus(StatusVenda.ORDEM_SERVICO);

		assertThatThrownBy(venda::converterParaOrdemServico).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void deveCancelarEImpedirCancelamentoDuplo() {
		Venda venda = new Venda();
		venda.cancelar();
		assertThat(venda.getStatus()).isEqualTo(StatusVenda.CANCELADO);

		assertThatThrownBy(venda::cancelar).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void janelaDeEdicaoValeParaOrcamentoEOsSoNaPrimeiraHora() {
		Venda orcamentoNovo = new Venda();
		orcamentoNovo.setDataCriacao(LocalDateTime.now());
		assertThat(orcamentoNovo.isEditavel()).isTrue();

		Venda osNova = new Venda();
		osNova.setStatus(StatusVenda.ORDEM_SERVICO);
		osNova.setDataCriacao(LocalDateTime.now());
		assertThat(osNova.isEditavel()).isTrue();

		Venda antiga = new Venda();
		antiga.setDataCriacao(LocalDateTime.now().minusHours(ConfiguracaoGlobal.getEdicaoHoras() + 1));
		assertThat(antiga.isEditavel()).isFalse();

		Venda cancelada = new Venda();
		cancelada.setStatus(StatusVenda.CANCELADO);
		cancelada.setDataCriacao(LocalDateTime.now());
		assertThat(cancelada.isEditavel()).isFalse();
	}

	@Test
	void isVencidoConsideraValidadeEStatus() {
		Venda vencido = new Venda();
		vencido.setDataCriacao(LocalDateTime.now().minusDays(ConfiguracaoGlobal.getValidadeOrcamentoDias() + 1));
		assertThat(vencido.isVencido()).isTrue();

		Venda dentroDoPrazo = new Venda();
		dentroDoPrazo.setDataCriacao(LocalDateTime.now().minusDays(ConfiguracaoGlobal.getValidadeOrcamentoDias() - 1));
		assertThat(dentroDoPrazo.isVencido()).isFalse();

		// validade só se aplica a orçamentos; uma OS antiga não está "vencida"
		Venda os = new Venda();
		os.setStatus(StatusVenda.ORDEM_SERVICO);
		os.setDataCriacao(LocalDateTime.now().minusDays(100));
		assertThat(os.isVencido()).isFalse();
	}
}
