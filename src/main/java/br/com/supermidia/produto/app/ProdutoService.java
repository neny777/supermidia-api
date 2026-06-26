package br.com.supermidia.produto.app;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.supermidia.calculo.domain.Calculo;
import br.com.supermidia.calculo.domain.CalculoRules;
import br.com.supermidia.calculo.domain.CodigoParametroCalculo;
import br.com.supermidia.calculo.infra.CalculoRepository;
import br.com.supermidia.materia.domain.Materia;
import br.com.supermidia.materia.infra.MateriaRepository;
import br.com.supermidia.produto.domain.Produto;
import br.com.supermidia.produto.domain.ProdutoMateriaCalculo;
import br.com.supermidia.produto.domain.ProdutoMateriaParametroCalculo;
import br.com.supermidia.produto.domain.ProdutoServicoCalculo;
import br.com.supermidia.produto.domain.ProdutoServicoParametroCalculo;
import br.com.supermidia.produto.infra.ProdutoRepository;
import br.com.supermidia.servico.domain.Servico;
import br.com.supermidia.servico.infra.ServicoRepository;

@Service
public class ProdutoService {

	private final ProdutoRepository produtoRepository;
	private final MateriaRepository materiaRepository;
	private final ServicoRepository servicoRepository;
	private final CalculoRepository calculoRepository;

	public ProdutoService(ProdutoRepository produtoRepository, MateriaRepository materiaRepository,
			ServicoRepository servicoRepository, CalculoRepository calculoRepository) {
		this.produtoRepository = produtoRepository;
		this.materiaRepository = materiaRepository;
		this.servicoRepository = servicoRepository;
		this.calculoRepository = calculoRepository;
	}

	@Transactional
	public Produto create(Produto produto) {
		validarNomeUnico(produto.getNome(), null);
		resolverReferencias(produto);
		return produtoRepository.save(produto);
	}

	@Transactional(readOnly = true)
	public List<Produto> findAll() {
		return produtoRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Produto findById(UUID id) {
		return produtoRepository.findById(id)
				.orElseThrow(() -> new ProdutoNotFoundException("Produto não encontrado: " + id));
	}

	@Transactional
	public Produto update(UUID id, Consumer<Produto> updater) {
		Produto current = findById(id);
		updater.accept(current);
		validarNomeUnico(current.getNome(), id);
		resolverReferencias(current);
		return produtoRepository.save(current);
	}

	@Transactional
	public void delete(UUID id) {
		Produto current = findById(id);
		produtoRepository.delete(current);
	}

	private void validarNomeUnico(String nome, UUID idAtual) {
		produtoRepository.findByNomeIgnoreCase(nome)
				.filter(produto -> !produto.getId().equals(idAtual))
				.ifPresent(produto -> {
					throw new ProdutoValidationException("Já existe um produto cadastrado com este nome.");
				});
	}

	private void resolverReferencias(Produto produto) {
		produto.getMateriasCalculo().forEach(item -> {
			item.setMateria(resolveMateria(item.getMateria()));
			Calculo calculo = resolveCalculo(item.getCalculo());
			item.setCalculo(calculo);
			item.setProduto(produto);
			validarParametrosMateria(item, calculo);
		});

		produto.getServicosCalculo().forEach(item -> {
			item.setServico(resolveServico(item.getServico()));
			Calculo calculo = resolveCalculo(item.getCalculo());
			item.setCalculo(calculo);
			item.setProduto(produto);
			validarParametrosServico(item, calculo);
		});
	}

	private Materia resolveMateria(Materia materia) {
		if (materia == null || materia.getId() == null) {
			throw new ProdutoValidationException("Toda matéria do produto precisa ser informada.");
		}

		return materiaRepository.findById(materia.getId())
				.orElseThrow(() -> new ProdutoValidationException("Matéria não encontrada: " + materia.getId()));
	}

	private Servico resolveServico(Servico servico) {
		if (servico == null || servico.getId() == null) {
			throw new ProdutoValidationException("Todo serviço do produto precisa ser informado.");
		}

		return servicoRepository.findById(servico.getId())
				.orElseThrow(() -> new ProdutoValidationException("Serviço não encontrado: " + servico.getId()));
	}

	private Calculo resolveCalculo(Calculo calculo) {
		if (calculo == null || calculo.getId() == null) {
			throw new ProdutoValidationException("Todo item do produto precisa ter um cálculo informado.");
		}

		return calculoRepository.findById(calculo.getId())
				.orElseThrow(() -> new ProdutoValidationException("Cálculo não encontrado: " + calculo.getId()));
	}

	private void validarParametrosMateria(ProdutoMateriaCalculo item, Calculo calculo) {
		validarParametros(item.getParametros().stream().map(ProdutoMateriaParametroCalculo::getCodigo).toList(),
				item.getParametros().stream().collect(java.util.stream.Collectors.groupingBy(ProdutoMateriaParametroCalculo::getCodigo,
						() -> new EnumMap<>(CodigoParametroCalculo.class), java.util.stream.Collectors.counting())),
				item.getParametros().stream().map(ProdutoMateriaParametroCalculo::getValor).toList(),
				calculo, "matéria");
	}

	private void validarParametrosServico(ProdutoServicoCalculo item, Calculo calculo) {
		validarParametros(item.getParametros().stream().map(ProdutoServicoParametroCalculo::getCodigo).toList(),
				item.getParametros().stream().collect(java.util.stream.Collectors.groupingBy(ProdutoServicoParametroCalculo::getCodigo,
						() -> new EnumMap<>(CodigoParametroCalculo.class), java.util.stream.Collectors.counting())),
				item.getParametros().stream().map(ProdutoServicoParametroCalculo::getValor).toList(),
				calculo, "serviço");
	}

	private void validarParametros(List<CodigoParametroCalculo> presentes, Map<CodigoParametroCalculo, Long> contagem,
			List<BigDecimal> valores, Calculo calculo, String grupo) {
		List<CodigoParametroCalculo> obrigatorios = CalculoRules.parametrosObrigatorios(calculo.getTipoCalculo());
		for (CodigoParametroCalculo codigo : obrigatorios) {
			if (!presentes.contains(codigo)) {
				throw new ProdutoValidationException(
						"O parâmetro " + codigo + " é obrigatório para o cálculo " + calculo.getNome() + " do " + grupo + ".");
			}
		}

		contagem.forEach((codigo, total) -> {
			if (total > 1) {
				throw new ProdutoValidationException("O parâmetro " + codigo + " foi informado mais de uma vez.");
			}
		});

		for (int i = 0; i < presentes.size(); i++) {
			CodigoParametroCalculo codigo = presentes.get(i);
			BigDecimal valor = valores.get(i);
			if (valor == null) {
				throw new ProdutoValidationException("Todo parâmetro do produto precisa ter um valor informado.");
			}
			if (permiteZero(codigo)) {
				if (valor.compareTo(BigDecimal.ZERO) < 0) {
					throw new ProdutoValidationException("O parâmetro " + codigo + " não pode ser negativo.");
				}
				continue;
			}
			if (valor.compareTo(BigDecimal.ZERO) <= 0) {
				throw new ProdutoValidationException("O parâmetro " + codigo + " precisa ser maior que zero.");
			}
		}
	}

	private boolean permiteZero(CodigoParametroCalculo codigo) {
		return codigo == CodigoParametroCalculo.ACRESCIMO_ALTURA || codigo == CodigoParametroCalculo.ACRESCIMO_LARGURA;
	}
}
