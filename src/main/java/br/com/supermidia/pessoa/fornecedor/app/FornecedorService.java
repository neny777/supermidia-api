package br.com.supermidia.pessoa.fornecedor.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.supermidia.pessoa.fornecedor.api.FornecedorMapper;
import br.com.supermidia.pessoa.fornecedor.api.dto.FornecedorDTO;
import br.com.supermidia.pessoa.fornecedor.api.dto.FornecedorFisicoDTO;
import br.com.supermidia.pessoa.fornecedor.api.dto.FornecedorJuridicoDTO;
import br.com.supermidia.pessoa.fornecedor.domain.Fornecedor;
import br.com.supermidia.pessoa.fornecedor.infra.FornecedorRepository;
import br.com.supermidia.pessoa.dominio.domain.Fisica;
import br.com.supermidia.pessoa.dominio.infra.FisicaRepository;
import br.com.supermidia.pessoa.dominio.domain.Juridica;
import br.com.supermidia.pessoa.dominio.infra.JuridicaRepository;
import br.com.supermidia.pessoa.dominio.domain.Pessoa;
import br.com.supermidia.pessoa.dominio.infra.PessoaRepository;
import br.com.supermidia.pessoa.dominio.app.PessoaService;
import br.com.supermidia.pessoa.shared.UniqueFieldRule;
import br.com.supermidia.pessoa.shared.UniqueRuleEngine;
import jakarta.validation.Valid;

@Service
public class FornecedorService {
	private final PessoaService pessoaService;
	private final FornecedorRepository fornecedorRepository;
	private final PessoaRepository pessoaRepository;
	private final FisicaRepository fisicaRepository;
	private final JuridicaRepository juridicaRepository;
	private final FornecedorMapper fornecedorMapper;

	public FornecedorService(PessoaService pessoaService, FornecedorRepository fornecedorRepository,
			PessoaRepository pessoaRepository, FisicaRepository fisicaRepository, JuridicaRepository juridicaRepository,
			FornecedorMapper fornecedorMapper) {
		this.pessoaService = pessoaService;
		this.fornecedorRepository = fornecedorRepository;
		this.pessoaRepository = pessoaRepository;
		this.fisicaRepository = fisicaRepository;
		this.juridicaRepository = juridicaRepository;
		this.fornecedorMapper = fornecedorMapper;
	}

	@Transactional
	public Fornecedor saveFisico(FornecedorFisicoDTO dto) {
		Fornecedor fornecedor = fornecedorMapper.toFornecedorFisico(dto);
		if (dto.getId() != null) {
			Fisica fisica = fisicaRepository.findById(dto.getId())
					.orElseThrow(() -> new FornecedorNotFoundException("Pessoa física não encontrada."));
			fornecedor.setPessoa(fisica);
			if (fornecedorRepository.existsById(dto.getId())) {
				fornecedor.setId(dto.getId());
			} else {
				fornecedor.setId(null);
			}
		}
		return fornecedorRepository.save(fornecedor);
	}

	@Transactional
	public Fornecedor createFisico(FornecedorFisicoDTO dto) {
		List<String> erros = validateCadastroFisico(dto);
		if (!erros.isEmpty()) {
			throw new FornecedorValidationException(String.join(", ", erros));
		}
		return saveFisico(dto);
	}

	@Transactional
	public Fornecedor saveJuridico(FornecedorJuridicoDTO dto) {
		Fornecedor fornecedor = fornecedorMapper.toFornecedorJuridico(dto);
		if (dto.getId() != null) {
			Juridica juridica = juridicaRepository.findById(dto.getId())
					.orElseThrow(() -> new FornecedorNotFoundException("Pessoa jurídica não encontrada."));
			fornecedor.setPessoa(juridica);
			if (fornecedorRepository.existsById(dto.getId())) {
				fornecedor.setId(dto.getId());
			} else {
				fornecedor.setId(null);
			}
		}
		return fornecedorRepository.save(fornecedor);
	}

	@Transactional
	public Fornecedor createJuridico(FornecedorJuridicoDTO dto) {
		List<String> erros = validateCadastroJuridico(dto);
		if (!erros.isEmpty()) {
			throw new FornecedorValidationException(String.join(", ", erros));
		}
		return saveJuridico(dto);
	}

	@Transactional
	public void updateFisico(UUID id, FornecedorFisicoDTO fornecedorFisicoDTO) {
		validarConsistenciaIdRequisicao(id, fornecedorFisicoDTO.getId());
		// Busca o fornecedor existente
		Fornecedor fornecedor = fornecedorRepository.findByPessoaId(id)
				.orElseThrow(() -> new FornecedorNotFoundException("Fornecedor não encontrado."));
		// Atualiza os dados do fornecedor usando o mapper
		fornecedorFisicoDTO.setId(id);
		fornecedorMapper.updateFornecedorFisicoFromDTO(fornecedorFisicoDTO, fornecedor);
		// Salva o fornecedor atualizado
		fornecedorRepository.save(fornecedor);
	}

	@Transactional
	public void updateFisicoComValidacao(UUID id, FornecedorFisicoDTO fornecedorFisicoDTO) {
		List<String> erros = validateUniqueFisico(fornecedorFisicoDTO);
		if (!erros.isEmpty()) {
			throw new FornecedorValidationException(String.join(", ", erros));
		}
		updateFisico(id, fornecedorFisicoDTO);
	}

	@Transactional
	public void updateJuridico(UUID id, FornecedorJuridicoDTO fornecedorJuridicoDTO) {
		validarConsistenciaIdRequisicao(id, fornecedorJuridicoDTO.getId());
		// Busca o fornecedor existente
		Fornecedor fornecedor = fornecedorRepository.findByPessoaId(id)
				.orElseThrow(() -> new FornecedorNotFoundException("Fornecedor não encontrado."));
		// Atualiza os dados do fornecedor usando o mapper
		fornecedorJuridicoDTO.setId(id);
		fornecedorMapper.updateFornecedorJuridicoFromDTO(fornecedorJuridicoDTO, fornecedor);
		// Salva o fornecedor atualizado
		fornecedorRepository.save(fornecedor);
	}

	@Transactional
	public void updateJuridicoComValidacao(UUID id, FornecedorJuridicoDTO fornecedorJuridicoDTO) {
		List<String> erros = validateUniqueJuridico(fornecedorJuridicoDTO);
		if (!erros.isEmpty()) {
			throw new FornecedorValidationException(String.join(", ", erros));
		}
		updateJuridico(id, fornecedorJuridicoDTO);
	}

	@Transactional
	public void deleteById(UUID fornecedorId) {
		Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
				.orElseThrow(() -> new FornecedorNotFoundException("Fornecedor não encontrado."));
		Fisica fisica;
		if ((fornecedor.getPessoa()).getClass() == Fisica.class) {
			fisica = fisicaRepository.findById(fornecedorId)
					.orElseThrow(() -> new FornecedorNotFoundException("Pessoa física não encontrada."));
			if (pessoaService.fornecedorTemOutroPapel(fisica.getId())) {
				fornecedorRepository.deleteById(fornecedorId);
				return;
			} else {
				fornecedorRepository.deleteById(fornecedorId);
				fisicaRepository.deleteById(fornecedorId);
				pessoaRepository.deleteById(fornecedorId);
				return;
			}
		}
		Juridica juridica;
		if ((fornecedor.getPessoa()).getClass() == Juridica.class) {
			juridica = juridicaRepository.findById(fornecedorId)
					.orElseThrow(() -> new FornecedorNotFoundException("Pessoa jurídica não encontrada."));
			if (pessoaService.fornecedorTemOutroPapel(juridica.getId())) {
				fornecedorRepository.deleteById(fornecedorId);
				return;
			} else {
				fornecedorRepository.deleteById(fornecedorId);
				juridicaRepository.deleteById(fornecedorId);
				pessoaRepository.deleteById(fornecedorId);
				return;
			}
		}
	}

	public List<FornecedorDTO> findAll() {
		List<Fornecedor> fornecedores = fornecedorRepository.findAll();
		List<FornecedorDTO> fornecedoresDTO = new ArrayList<>();
		for (Fornecedor fornecedor : fornecedores) {
			FornecedorDTO dto = new FornecedorDTO();
			dto.setId(fornecedor.getId());
			dto.setNome(fornecedor.getPessoa().getNome());
			dto.setEmail(fornecedor.getPessoa().getEmail());
			dto.setTelefone(fornecedor.getPessoa().getTelefone());
			dto.setMunicipio(fornecedor.getPessoa().getMunicipio());
			dto.setUf(fornecedor.getPessoa().getUf());
			dto.setTipo(fornecedor.getPessoa().getTipo());
			fornecedoresDTO.add(dto);
		}
		return fornecedoresDTO;
	}

	@Transactional(readOnly = true)
	public FornecedorDTO findById(UUID id) {
		// Buscar na tabela de pessoas físicas
		Optional<Fisica> fisicaOptional = fisicaRepository.findById(id);
		if (fisicaOptional.isPresent()) {
			Fisica fisica = fisicaOptional.get();
			Fornecedor fornecedor = fornecedorRepository.findByPessoaId(fisica.getId())
					.orElseThrow(() -> new FornecedorNotFoundException("Fornecedor associado não encontrado."));
			return fornecedorMapper.toFornecedorFisicoDTO(fisica, fornecedor);
		}
		// Buscar na tabela de pessoas jurídicas
		Optional<Juridica> juridicaOptional = juridicaRepository.findById(id);
		if (juridicaOptional.isPresent()) {
			Juridica juridica = juridicaOptional.get();
			Fornecedor fornecedor = fornecedorRepository.findByPessoaId(juridica.getId())
					.orElseThrow(() -> new FornecedorNotFoundException("Fornecedor associado não encontrado."));
			return fornecedorMapper.toFornecedorJuridicoDTO(juridica, fornecedor);
		}
		// Se não encontrado em nenhum dos dois
		throw new FornecedorNotFoundException("Fornecedor não encontrado.");
	}

	public FornecedorFisicoDTO findFisicoById(UUID id) {
		// Buscar o fornecedor pelo ID
		Fornecedor fornecedor = fornecedorRepository.findById(id)
				.orElseThrow(() -> new FornecedorNotFoundException("Fornecedor não encontrado com o ID: " + id));
		// Garantir que a pessoa associada é do tipo Fisica
		if (!(fornecedor.getPessoa() instanceof Fisica fisica)) {
			throw new IllegalStateException("A pessoa associada ao fornecedor não é do tipo Física.");
		}
		// Mapear o fornecedor e a entidade Fisica para FornecedorFisicoDTO
		return fornecedorMapper.toFornecedorFisicoDTO(fisica, fornecedor);
	}

	public FornecedorJuridicoDTO findJuridicoById(UUID id) {
		// Buscar o fornecedor pelo ID
		Fornecedor fornecedor = fornecedorRepository.findById(id)
				.orElseThrow(() -> new FornecedorNotFoundException("Fornecedor não encontrado com o ID: " + id));
		// Garantir que a pessoa associada é do tipo Fisica
		if (!(fornecedor.getPessoa() instanceof Juridica juridica)) {
			throw new IllegalStateException("A pessoa associada ao fornecedor não é do tipo Jurídica.");
		}
		// Mapear o fornecedor e a entidade Fisica para FornecedorFisicoDTO
		return fornecedorMapper.toFornecedorJuridicoDTO(juridica, fornecedor);
	}

	public List<String> validateUniqueFisico(FornecedorFisicoDTO fornecedorFisicoDTO) {
		UUID id = fornecedorFisicoDTO.getId();
		List<UniqueFieldRule> rules = List.of(
				new UniqueFieldRule("nome", fornecedorFisicoDTO.getNome(), valor -> pessoaRepository.existsByNome(valor),
						valor -> pessoaRepository.existsByNomeAndIdNot(valor, id)),
				new UniqueFieldRule("email", fornecedorFisicoDTO.getEmail(),
						valor -> pessoaRepository.existsByEmail(valor),
						valor -> pessoaRepository.existsByEmailAndIdNot(valor, id)),
				new UniqueFieldRule("telefone", fornecedorFisicoDTO.getTelefone(),
						valor -> pessoaRepository.existsByTelefone(valor),
						valor -> pessoaRepository.existsByTelefoneAndIdNot(valor, id)),
				new UniqueFieldRule("rg", fornecedorFisicoDTO.getRg(), valor -> fisicaRepository.existsByRg(valor),
						valor -> fisicaRepository.existsByRgAndIdNot(valor, id)),
				new UniqueFieldRule("cpf", fornecedorFisicoDTO.getCpf(), valor -> fisicaRepository.existsByCpf(valor),
						valor -> fisicaRepository.existsByCpfAndIdNot(valor, id)));
		return UniqueRuleEngine.validate(id, rules);
	}

	public List<String> validateUniqueJuridico(@Valid FornecedorJuridicoDTO fornecedorJuridicoDTO) {
		UUID id = fornecedorJuridicoDTO.getId();
		List<UniqueFieldRule> rules = List.of(
				new UniqueFieldRule("nome", fornecedorJuridicoDTO.getNome(),
						valor -> pessoaRepository.existsByNome(valor),
						valor -> pessoaRepository.existsByNomeAndIdNot(valor, id)),
				new UniqueFieldRule("email", fornecedorJuridicoDTO.getEmail(),
						valor -> pessoaRepository.existsByEmail(valor),
						valor -> pessoaRepository.existsByEmailAndIdNot(valor, id)),
				new UniqueFieldRule("telefone", fornecedorJuridicoDTO.getTelefone(),
						valor -> pessoaRepository.existsByTelefone(valor),
						valor -> pessoaRepository.existsByTelefoneAndIdNot(valor, id)),
				new UniqueFieldRule("ie", fornecedorJuridicoDTO.getIe(), valor -> juridicaRepository.existsByIe(valor),
						valor -> juridicaRepository.existsByIeAndIdNot(valor, id)),
				new UniqueFieldRule("cnpj", fornecedorJuridicoDTO.getCnpj(),
						valor -> juridicaRepository.existsByCnpj(valor),
						valor -> juridicaRepository.existsByCnpjAndIdNot(valor, id)));
		return UniqueRuleEngine.validate(id, rules);
	}

	public boolean existsById(UUID id) {
		return fornecedorRepository.existsById(id);
	}

	public List<String> validateCadastroFisico(FornecedorFisicoDTO dto) {
		List<String> erros = validateUniqueFisico(dto);
		if (dto.getId() != null && existsById(dto.getId())) {
			erros.add("Fornecedor já está cadastrado");
		}
		return erros;
	}

	public List<String> validateCadastroJuridico(FornecedorJuridicoDTO dto) {
		List<String> erros = validateUniqueJuridico(dto);
		if (dto.getId() != null && existsById(dto.getId())) {
			erros.add("Fornecedor já está cadastrado");
		}
		return erros;
	}

	public FornecedorFisicoDTO findPessoaFisicaById(UUID id) {
		// Buscar a pessoa independente do tipo
		Pessoa pessoa = pessoaRepository.findById(id)
				.orElseThrow(() -> new FornecedorNotFoundException("Pessoa não encontrada."));
		// Verificar se a pessoa é do tipo correto
		if (!"FÍSICA".equals(pessoa.getTipo())) {
			throw new IllegalStateException("Pessoa selecionada não é Física.");
		}
			Fisica fisica = fisicaRepository.findById(id)
					.orElseThrow(() -> new FornecedorNotFoundException("Pessoa física não encontrada."));
			Fornecedor fornecedor = new Fornecedor();
			fornecedor.setId(id);
			// Mapear para FornecedorFisicoDTO
		return fornecedorMapper.toFornecedorFisicoDTO(fisica, fornecedor);
	}

	public FornecedorJuridicoDTO findPessoaJuridicaById(UUID id) {
		// Buscar a pessoa independente do tipo
		Pessoa pessoa = pessoaRepository.findById(id)
				.orElseThrow(() -> new FornecedorNotFoundException("Pessoa não encontrada."));
		// Verificar se a pessoa é do tipo correto
		if (!"JURÍDICA".equals(pessoa.getTipo())) {
			throw new IllegalStateException("Pessoa selecionada não é Jurídica.");
		}
			Juridica juridica = juridicaRepository.findById(id)
					.orElseThrow(() -> new FornecedorNotFoundException("Pessoa jurídica não encontrada."));
			Fornecedor fornecedor = new Fornecedor();
			fornecedor.setId(id);
			// Mapear para FornecedorJuridicoDTO
			return fornecedorMapper.toFornecedorJuridicoDTO(juridica, fornecedor);
	}

	private void validarConsistenciaIdRequisicao(UUID pathId, UUID bodyId) {
		if (bodyId != null && !pathId.equals(bodyId)) {
			throw new FornecedorValidationException("O ID informado no corpo da requisição difere do ID da URL.");
		}
	}
}
