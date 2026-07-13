package br.com.supermidia.configuracao.app;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.supermidia.configuracao.api.dto.ConfiguracaoDTO;
import br.com.supermidia.configuracao.domain.Configuracao;
import br.com.supermidia.configuracao.domain.ConfiguracaoGlobal;
import br.com.supermidia.configuracao.infra.ConfiguracaoRepository;

@Service
public class ConfiguracaoService {

	private final ConfiguracaoRepository repository;

	public ConfiguracaoService(ConfiguracaoRepository repository) {
		this.repository = repository;
	}

	/** Na subida: semeia a linha única (se ausente) e espelha na memória. */
	@EventListener(ApplicationReadyEvent.class)
	@Transactional
	public void carregarNaSubida() {
		ConfiguracaoGlobal.aplicar(obter());
	}

	@Transactional
	public Configuracao obter() {
		return repository.findById(Configuracao.ID_UNICO).orElseGet(() -> repository.save(padraoDeFabrica()));
	}

	@Transactional
	public Configuracao atualizar(ConfiguracaoDTO dto) {
		Configuracao configuracao = obter();
		configuracao.setValidadeOrcamentoDias(dto.getValidadeOrcamentoDias());
		configuracao.setEdicaoHoras(dto.getEdicaoHoras());
		configuracao.setPisoMargem(dto.getPisoMargem());
		configuracao.setFatorVarejo(dto.getFatorVarejo());
		Configuracao salva = repository.save(configuracao);
		ConfiguracaoGlobal.aplicar(salva); // vale imediatamente, sem reiniciar
		return salva;
	}

	private Configuracao padraoDeFabrica() {
		Configuracao configuracao = new Configuracao();
		configuracao.setValidadeOrcamentoDias(ConfiguracaoGlobal.VALIDADE_ORCAMENTO_DIAS_PADRAO);
		configuracao.setEdicaoHoras(ConfiguracaoGlobal.EDICAO_HORAS_PADRAO);
		configuracao.setPisoMargem(ConfiguracaoGlobal.PISO_MARGEM_PADRAO);
		configuracao.setFatorVarejo(ConfiguracaoGlobal.FATOR_VAREJO_PADRAO);
		return configuracao;
	}
}
