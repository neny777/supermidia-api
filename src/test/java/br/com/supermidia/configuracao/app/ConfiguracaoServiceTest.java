package br.com.supermidia.configuracao.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.supermidia.configuracao.api.dto.ConfiguracaoDTO;
import br.com.supermidia.configuracao.domain.Configuracao;
import br.com.supermidia.configuracao.domain.ConfiguracaoGlobal;
import br.com.supermidia.configuracao.infra.ConfiguracaoRepository;

@ExtendWith(MockitoExtension.class)
class ConfiguracaoServiceTest {

	@Mock
	private ConfiguracaoRepository repository;

	@InjectMocks
	private ConfiguracaoService service;

	@AfterEach
	void restaurar() {
		// O espelho é estático: devolve os padrões para não vazar em outros testes.
		ConfiguracaoGlobal.restaurarPadroes();
	}

	@Test
	void obterSemeiaOsPadroesDeFabricaQuandoNaoHaLinha() {
		when(repository.findById(Configuracao.ID_UNICO)).thenReturn(Optional.empty());
		when(repository.save(any(Configuracao.class))).thenAnswer(inv -> inv.getArgument(0));

		Configuracao configuracao = service.obter();

		assertThat(configuracao.getValidadeOrcamentoDias()).isEqualTo(ConfiguracaoGlobal.VALIDADE_ORCAMENTO_DIAS_PADRAO);
		assertThat(configuracao.getEdicaoHoras()).isEqualTo(ConfiguracaoGlobal.EDICAO_HORAS_PADRAO);
		assertThat(configuracao.getPisoMargem()).isEqualByComparingTo(ConfiguracaoGlobal.PISO_MARGEM_PADRAO);
		assertThat(configuracao.getFatorVarejo()).isEqualByComparingTo(ConfiguracaoGlobal.FATOR_VAREJO_PADRAO);
	}

	@Test
	void atualizarPersisteEAplicaImediatamenteNoEspelhoEstatico() {
		Configuracao existente = new Configuracao();
		existente.setValidadeOrcamentoDias(15);
		existente.setEdicaoHoras(1);
		existente.setPisoMargem(new BigDecimal("0.35"));
		existente.setFatorVarejo(new BigDecimal("1.3846"));
		when(repository.findById(Configuracao.ID_UNICO)).thenReturn(Optional.of(existente));
		when(repository.save(any(Configuracao.class))).thenAnswer(inv -> inv.getArgument(0));

		ConfiguracaoDTO dto = new ConfiguracaoDTO();
		dto.setValidadeOrcamentoDias(30);
		dto.setEdicaoHoras(24);
		dto.setPisoMargem(new BigDecimal("0.40"));
		dto.setFatorVarejo(new BigDecimal("1.5"));

		service.atualizar(dto);

		assertThat(ConfiguracaoGlobal.getValidadeOrcamentoDias()).isEqualTo(30);
		assertThat(ConfiguracaoGlobal.getEdicaoHoras()).isEqualTo(24);
		assertThat(ConfiguracaoGlobal.getPisoMargem()).isEqualByComparingTo("0.40");
		assertThat(ConfiguracaoGlobal.getFatorVarejo()).isEqualByComparingTo("1.5");
	}
}
