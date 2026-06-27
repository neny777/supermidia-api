package br.com.supermidia.venda.infra;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import br.com.supermidia.calculo.domain.BaseOperacionalCalculo;
import br.com.supermidia.calculo.domain.TipoCalculo;
import br.com.supermidia.venda.domain.ItemVenda;
import br.com.supermidia.venda.domain.ItemVendaDetalhe;
import br.com.supermidia.venda.domain.StatusVenda;
import br.com.supermidia.venda.domain.Venda;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class VendaPersistenceTest {

	@Autowired
	private VendaRepository vendaRepository;

	@Autowired
	private TestEntityManager em;

	@Test
	void devePersistirERecarregarOGrafoDaVendaComSnapshot() {
		Venda venda = new Venda(); // status default = ORCAMENTO

		ItemVenda item = new ItemVenda();
		item.setProdutoId(UUID.randomUUID());
		item.setProdutoNome("LONA 440 BASICA");
		item.setAltura(new BigDecimal("100"));
		item.setLargura(new BigDecimal("200"));
		item.setQuantidade(new BigDecimal("2"));
		item.setCustoTotal(new BigDecimal("94.34"));
		item.setMarkupAplicado(new BigDecimal("80"));
		item.setPrecoSugerido(new BigDecimal("169.81"));
		item.setPrecoFinal(new BigDecimal("169.81"));
		item.setDetalhes(List.of(
				detalhe("LONA", "MATERIA", "AREA COM FATOR", TipoCalculo.AREA_COM_FATOR, BaseOperacionalCalculo.AREA,
						"4.84", "M2", "8.50", "41.14"),
				detalhe("IMPRESSAO", "SERVICO", "IMPRESSAO", TipoCalculo.AREA_BASE, BaseOperacionalCalculo.AREA,
						"4", "M2", "8.50", "34.00")));

		venda.addItem(item);
		venda.recalcularTotal();

		UUID id = vendaRepository.saveAndFlush(venda).getId();
		em.clear(); // garante leitura vinda do banco, não do cache de 1º nível

		Venda recarregada = vendaRepository.findById(id).orElseThrow();
		assertThat(recarregada.getStatus()).isEqualTo(StatusVenda.ORCAMENTO);
		assertThat(recarregada.getDataCriacao()).isNotNull();
		assertThat(recarregada.getTotal()).isEqualByComparingTo("169.81");

		assertThat(recarregada.getItens()).hasSize(1);
		ItemVenda itemRecarregado = recarregada.getItens().get(0);
		assertThat(itemRecarregado.getProdutoNome()).isEqualTo("LONA 440 BASICA");
		assertThat(itemRecarregado.getPrecoSugerido()).isEqualByComparingTo("169.81");
		assertThat(itemRecarregado.getPrecoFinal()).isEqualByComparingTo("169.81");

		assertThat(itemRecarregado.getDetalhes()).hasSize(2);
		ItemVendaDetalhe lona = itemRecarregado.getDetalhes().stream()
				.filter(d -> "LONA".equals(d.getNome())).findFirst().orElseThrow();
		assertThat(lona.getTipoItem()).isEqualTo("MATERIA");
		assertThat(lona.getTipoCalculo()).isEqualTo(TipoCalculo.AREA_COM_FATOR);
		assertThat(lona.getQuantidadeCalculada()).isEqualByComparingTo("4.84");
		assertThat(lona.getValorTotal()).isEqualByComparingTo("41.14");
	}

	@Test
	void findByStatusDeveSepararOrcamentosDeOrdensDeServico() {
		Venda orcamento = new Venda();
		orcamento.setStatus(StatusVenda.ORCAMENTO);
		Venda os = new Venda();
		os.setStatus(StatusVenda.ORDEM_SERVICO);
		vendaRepository.saveAll(List.of(orcamento, os));
		vendaRepository.flush();

		assertThat(vendaRepository.findByStatus(StatusVenda.ORCAMENTO)).extracting(Venda::getId)
				.contains(orcamento.getId()).doesNotContain(os.getId());
		assertThat(vendaRepository.findByStatus(StatusVenda.ORDEM_SERVICO)).extracting(Venda::getId)
				.contains(os.getId()).doesNotContain(orcamento.getId());
	}

	private ItemVendaDetalhe detalhe(String nome, String tipoItem, String calculoNome, TipoCalculo tipoCalculo,
			BaseOperacionalCalculo base, String qtd, String unidade, String precoUnitario, String valorTotal) {
		ItemVendaDetalhe d = new ItemVendaDetalhe();
		d.setNome(nome);
		d.setTipoItem(tipoItem);
		d.setCalculoNome(calculoNome);
		d.setTipoCalculo(tipoCalculo);
		d.setBaseOperacional(base);
		d.setQuantidadeCalculada(new BigDecimal(qtd));
		d.setUnidade(unidade);
		d.setPrecoUnitario(new BigDecimal(precoUnitario));
		d.setValorTotal(new BigDecimal(valorTotal));
		return d;
	}
}
