package br.com.supermidia.produto.app;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import br.com.supermidia.produto.domain.ProdutoComponente;
import br.com.supermidia.produto.domain.ProdutoComponenteParametro;
import br.com.supermidia.produto.domain.ProdutoGrupoOpcao;
import br.com.supermidia.produto.domain.ProdutoMedida;
import br.com.supermidia.produto.domain.ProdutoOpcao;
import br.com.supermidia.produto.domain.ProdutoParametroVinculoMedida;
import br.com.supermidia.produto.domain.TipoItemComponente;
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
		validarMedidas(produto);
		produto.getComponentes().forEach(componente -> resolverComponente(componente, produto));
		produto.getGruposOpcoes().forEach(grupo -> resolverGrupo(grupo, produto));
	}

	private void validarMedidas(Produto produto) {
		Set<String> nomes = new HashSet<>();
		for (ProdutoMedida medida : produto.getMedidas()) {
			if (medida.getNome() == null || medida.getNome().isBlank()) {
				throw new ProdutoValidationException("Toda medida do produto precisa ter um nome.");
			}
			if (!nomes.add(medida.getNome().trim().toUpperCase())) {
				throw new ProdutoValidationException("A medida " + medida.getNome() + " foi declarada mais de uma vez.");
			}
			if (medida.getMinimo() != null && medida.getMaximo() != null
					&& medida.getMinimo().compareTo(medida.getMaximo()) > 0) {
				throw new ProdutoValidationException(
						"A medida " + medida.getNome() + " tem mínimo maior que o máximo.");
			}
		}
	}

	private void resolverGrupo(ProdutoGrupoOpcao grupo, Produto produto) {
		if (grupo.getNome() == null || grupo.getNome().isBlank()) {
			throw new ProdutoValidationException("Todo grupo de opções precisa ter um nome.");
		}
		if (grupo.getOpcoes().isEmpty()) {
			throw new ProdutoValidationException(
					"O grupo de opções " + grupo.getNome() + " precisa de ao menos uma opção.");
		}
		for (ProdutoOpcao opcao : grupo.getOpcoes()) {
			if (opcao.getNome() == null || opcao.getNome().isBlank()) {
				throw new ProdutoValidationException(
						"Toda opção do grupo " + grupo.getNome() + " precisa ter um nome.");
			}
			opcao.getComponentes().forEach(componente -> resolverComponente(componente, produto));
			opcao.getContribuicoes().forEach(contribuicao -> {
				if (contribuicao.getCodigo() == null || contribuicao.getValor() == null) {
					throw new ProdutoValidationException(
							"Toda contribuição da opção " + opcao.getNome() + " precisa de código e valor.");
				}
			});
		}
	}

	private void resolverComponente(ProdutoComponente componente, Produto produto) {
		if (componente.getTipoItem() == null) {
			throw new ProdutoValidationException("Todo componente precisa indicar se é matéria ou serviço.");
		}
		if (componente.getTipoItem() == TipoItemComponente.MATERIA) {
			boolean temMateria = componente.getMateria() != null && componente.getMateria().getId() != null;
			boolean temSlot = componente.isSlot();
			if (temMateria == temSlot) {
				throw new ProdutoValidationException(
						"Componente de matéria precisa de uma matéria fixa OU de um grupo de slot (apenas um dos dois).");
			}
			if (temMateria) {
				componente.setMateria(resolveMateria(componente.getMateria()));
			} else {
				validarGrupoSlot(componente.getGrupoMateriaSlot());
			}
			componente.setServico(null);
		} else {
			if (componente.getServico() == null || componente.getServico().getId() == null) {
				throw new ProdutoValidationException("Componente de serviço precisa do serviço informado.");
			}
			componente.setServico(resolveServico(componente.getServico()));
			componente.setMateria(null);
			componente.setGrupoMateriaSlot(null);
		}
		Calculo calculo = resolveCalculo(componente.getCalculo());
		componente.setCalculo(calculo);
		validarParametros(componente, calculo, produto);
	}

	private void validarGrupoSlot(String grupo) {
		if (materiaRepository.findByGrupoOrderByNome(grupo.trim().toUpperCase()).isEmpty()) {
			throw new ProdutoValidationException(
					"Não há matérias cadastradas no grupo '" + grupo + "' para o slot de material.");
		}
	}

	private Materia resolveMateria(Materia materia) {
		return materiaRepository.findById(materia.getId())
				.orElseThrow(() -> new ProdutoValidationException("Matéria não encontrada: " + materia.getId()));
	}

	private Servico resolveServico(Servico servico) {
		return servicoRepository.findById(servico.getId())
				.orElseThrow(() -> new ProdutoValidationException("Serviço não encontrado: " + servico.getId()));
	}

	private Calculo resolveCalculo(Calculo calculo) {
		if (calculo == null || calculo.getId() == null) {
			throw new ProdutoValidationException("Todo componente do produto precisa ter um cálculo informado.");
		}
		return calculoRepository.findById(calculo.getId())
				.orElseThrow(() -> new ProdutoValidationException("Cálculo não encontrado: " + calculo.getId()));
	}

	private void validarParametros(ProdutoComponente componente, Calculo calculo, Produto produto) {
		List<CodigoParametroCalculo> obrigatorios = CalculoRules.parametrosObrigatorios(calculo.getTipoCalculo());
		Set<CodigoParametroCalculo> presentes = new HashSet<>();

		for (ProdutoComponenteParametro parametro : componente.getParametros()) {
			if (parametro.getCodigo() == null) {
				throw new ProdutoValidationException("Todo parâmetro precisa de um código.");
			}
			if (!presentes.add(parametro.getCodigo())) {
				throw new ProdutoValidationException(
						"O parâmetro " + parametro.getCodigo() + " foi informado mais de uma vez.");
			}
			validarValorParametro(parametro, produto);
		}

		for (CodigoParametroCalculo codigo : obrigatorios) {
			if (!presentes.contains(codigo)) {
				throw new ProdutoValidationException(
						"O parâmetro " + codigo + " é obrigatório para o cálculo " + calculo.getNome() + ".");
			}
		}
	}

	private void validarValorParametro(ProdutoComponenteParametro parametro, Produto produto) {
		boolean temVinculo = !parametro.getVinculos().isEmpty();

		if (!temVinculo) {
			BigDecimal valor = parametro.getValorConstante();
			if (valor == null) {
				throw new ProdutoValidationException("O parâmetro " + parametro.getCodigo()
						+ " precisa de um valor constante ou de um vínculo de medida.");
			}
			if (permiteZero(parametro.getCodigo())) {
				if (valor.compareTo(BigDecimal.ZERO) < 0) {
					throw new ProdutoValidationException(
							"O parâmetro " + parametro.getCodigo() + " não pode ser negativo.");
				}
			} else if (valor.compareTo(BigDecimal.ZERO) <= 0) {
				throw new ProdutoValidationException(
						"O parâmetro " + parametro.getCodigo() + " precisa ser maior que zero.");
			}
			return;
		}

		if (parametro.getValorConstante() != null
				&& parametro.getValorConstante().compareTo(BigDecimal.ZERO) < 0) {
			throw new ProdutoValidationException(
					"A constante do parâmetro " + parametro.getCodigo() + " não pode ser negativa.");
		}
		for (ProdutoParametroVinculoMedida vinculo : parametro.getVinculos()) {
			if (vinculo.getMedidaNome() == null || vinculo.getMedidaNome().isBlank()) {
				throw new ProdutoValidationException(
						"Vínculo do parâmetro " + parametro.getCodigo() + " sem a medida informada.");
			}
			if (!medidaDeclarada(produto, vinculo.getMedidaNome())) {
				throw new ProdutoValidationException("A medida " + vinculo.getMedidaNome()
						+ " usada no parâmetro " + parametro.getCodigo() + " não foi declarada no produto.");
			}
			if (vinculo.getMultiplicador() == null
					|| vinculo.getMultiplicador().compareTo(BigDecimal.ZERO) == 0) {
				throw new ProdutoValidationException("O multiplicador do vínculo com a medida "
						+ vinculo.getMedidaNome() + " precisa ser diferente de zero.");
			}
		}
	}

	private boolean medidaDeclarada(Produto produto, String nome) {
		return produto.getMedidas().stream()
				.anyMatch(medida -> medida.getNome() != null
						&& medida.getNome().trim().equalsIgnoreCase(nome.trim()));
	}

	private boolean permiteZero(CodigoParametroCalculo codigo) {
		return codigo == CodigoParametroCalculo.ACRESCIMO_ALTURA || codigo == CodigoParametroCalculo.ACRESCIMO_LARGURA;
	}
}
