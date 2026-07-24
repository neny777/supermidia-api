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
		Configuracao configuracao = repository.findById(Configuracao.ID_UNICO)
				.orElseGet(() -> repository.save(padraoDeFabrica()));
		// Migração suave: campos novos nascem nulos em linhas antigas.
		if (preencherPadroesFaltantes(configuracao)) {
			configuracao = repository.save(configuracao);
		}
		return configuracao;
	}

	@Transactional
	public Configuracao atualizar(ConfiguracaoDTO dto) {
		Configuracao configuracao = obter();
		configuracao.setValidadeOrcamentoDias(dto.getValidadeOrcamentoDias());
		configuracao.setEdicaoHoras(dto.getEdicaoHoras());
		configuracao.setPisoMargem(dto.getPisoMargem());
		configuracao.setFatorVarejo(dto.getFatorVarejo());
		configuracao.setFormaPagamentoPadrao(dto.getFormaPagamentoPadrao());
		configuracao.setCondicaoPagamentoPadrao(dto.getCondicaoPagamentoPadrao());
		configuracao.setCondicoesSugeridas(dto.getCondicoesSugeridas());
		configuracao.setFormaEntregaPadrao(dto.getFormaEntregaPadrao());
		configuracao.setPrazoEntregaPadrao(dto.getPrazoEntregaPadrao());
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
		preencherPadroesFaltantes(configuracao);
		return configuracao;
	}

	/** Padrões de fábrica dos textos da venda (Denis ajusta na tela depois). */
	private boolean preencherPadroesFaltantes(Configuracao configuracao) {
		boolean mudou = false;
		if (configuracao.getFormaPagamentoPadrao() == null) {
			configuracao.setFormaPagamentoPadrao("PIX, DINHEIRO OU CARTÃO");
			mudou = true;
		}
		if (configuracao.getCondicaoPagamentoPadrao() == null) {
			configuracao.setCondicaoPagamentoPadrao("PAGAMENTO NA APROVAÇÃO");
			mudou = true;
		}
		if (configuracao.getCondicoesSugeridas() == null) {
			configuracao.setCondicoesSugeridas("PAGAMENTO NA APROVAÇÃO\n50% NA APROVAÇÃO + 50% NA RETIRADA");
			mudou = true;
		}
		if (configuracao.getFormaEntregaPadrao() == null) {
			configuracao.setFormaEntregaPadrao("RETIRADA NA LOJA");
			mudou = true;
		}
		if (configuracao.getPrazoEntregaPadrao() == null) {
			configuracao.setPrazoEntregaPadrao("A COMBINAR");
			mudou = true;
		}
		return mudou;
	}
}
