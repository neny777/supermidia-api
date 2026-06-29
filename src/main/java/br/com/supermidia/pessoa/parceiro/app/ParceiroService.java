package br.com.supermidia.pessoa.parceiro.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import br.com.supermidia.pessoa.dominio.app.PessoaService;
import br.com.supermidia.pessoa.dominio.domain.Fisica;
import br.com.supermidia.pessoa.dominio.domain.Juridica;
import br.com.supermidia.pessoa.dominio.domain.Pessoa;
import br.com.supermidia.pessoa.dominio.infra.FisicaRepository;
import br.com.supermidia.pessoa.dominio.infra.JuridicaRepository;
import br.com.supermidia.pessoa.dominio.infra.PessoaRepository;
import br.com.supermidia.pessoa.parceiro.api.ParceiroMapper;
import br.com.supermidia.pessoa.parceiro.api.dto.ParceiroDTO;
import br.com.supermidia.pessoa.parceiro.api.dto.ParceiroFisicoDTO;
import br.com.supermidia.pessoa.parceiro.api.dto.ParceiroJuridicoDTO;
import br.com.supermidia.pessoa.parceiro.domain.Parceiro;
import br.com.supermidia.pessoa.parceiro.infra.ParceiroRepository;
import br.com.supermidia.pessoa.shared.UniqueFieldRule;
import br.com.supermidia.pessoa.shared.UniqueRuleEngine;
import jakarta.validation.Valid;

@Validated
@Service
public class ParceiroService {
	private final PessoaService pessoaService;
	private final ParceiroRepository parceiroRepository;
	private final PessoaRepository pessoaRepository;
	private final FisicaRepository fisicaRepository;
	private final JuridicaRepository juridicaRepository;
	private final ParceiroMapper parceiroMapper;

	public ParceiroService(PessoaService pessoaService, ParceiroRepository parceiroRepository,
			PessoaRepository pessoaRepository, FisicaRepository fisicaRepository, JuridicaRepository juridicaRepository,
			ParceiroMapper parceiroMapper) {
		this.pessoaService = pessoaService;
		this.parceiroRepository = parceiroRepository;
		this.pessoaRepository = pessoaRepository;
		this.fisicaRepository = fisicaRepository;
		this.juridicaRepository = juridicaRepository;
		this.parceiroMapper = parceiroMapper;
	}

	@Transactional
	public Parceiro saveFisico(ParceiroFisicoDTO dto) {
		Parceiro parceiro = parceiroMapper.toParceiroFisico(dto);
		if (dto.getId() != null) {
			Fisica fisica = fisicaRepository.findById(dto.getId())
					.orElseThrow(() -> new ParceiroNotFoundException("Pessoa física não encontrada."));
			parceiro.setPessoa(fisica);
			if (parceiroRepository.existsById(dto.getId())) {
				parceiro.setId(dto.getId());
			} else {
				parceiro.setId(null);
			}
		}
		return parceiroRepository.save(parceiro);
	}

	@Transactional
	public Parceiro createFisico(ParceiroFisicoDTO dto) {
		List<String> erros = validateCadastroFisico(dto);
		if (!erros.isEmpty()) {
			throw new ParceiroValidationException(String.join(", ", erros));
		}
		return saveFisico(dto);
	}

	@Transactional
	public Parceiro saveJuridico(ParceiroJuridicoDTO dto) {
		Parceiro parceiro = parceiroMapper.toParceiroJuridico(dto);
		if (dto.getId() != null) {
			Juridica juridica = juridicaRepository.findById(dto.getId())
					.orElseThrow(() -> new ParceiroNotFoundException("Pessoa jurídica não encontrada."));
			parceiro.setPessoa(juridica);
			if (parceiroRepository.existsById(dto.getId())) {
				parceiro.setId(dto.getId());
			} else {
				parceiro.setId(null);
			}
		}
		return parceiroRepository.save(parceiro);
	}

	@Transactional
	public Parceiro createJuridico(ParceiroJuridicoDTO dto) {
		List<String> erros = validateCadastroJuridico(dto);
		if (!erros.isEmpty()) {
			throw new ParceiroValidationException(String.join(", ", erros));
		}
		return saveJuridico(dto);
	}

	@Transactional
	public void updateFisico(UUID id, ParceiroFisicoDTO parceiroFisicoDTO) {
		validarConsistenciaIdRequisicao(id, parceiroFisicoDTO.getId());
		// Busca o parceiro existente
		Parceiro parceiro = parceiroRepository.findByPessoaId(id)
				.orElseThrow(() -> new ParceiroNotFoundException("Parceiro não encontrado."));
		// Atualiza os dados do parceiro usando o mapper
		parceiroFisicoDTO.setId(id);
		parceiroMapper.updateParceiroFisicoFromDTO(parceiroFisicoDTO, parceiro);
		// Salva o parceiro atualizado
		parceiroRepository.save(parceiro);
	}

	@Transactional
	public void updateFisicoComValidacao(UUID id, ParceiroFisicoDTO parceiroFisicoDTO) {
		List<String> erros = validateUniqueFisico(parceiroFisicoDTO);
		if (!erros.isEmpty()) {
			throw new ParceiroValidationException(String.join(", ", erros));
		}
		updateFisico(id, parceiroFisicoDTO);
	}

	@Transactional
	public void updateJuridico(UUID id, ParceiroJuridicoDTO parceiroJuridicoDTO) {
		validarConsistenciaIdRequisicao(id, parceiroJuridicoDTO.getId());
		// Busca o parceiro existente
		Parceiro parceiro = parceiroRepository.findByPessoaId(id)
				.orElseThrow(() -> new ParceiroNotFoundException("Parceiro não encontrado."));
		// Atualiza os dados do parceiro usando o mapper
		parceiroJuridicoDTO.setId(id);
		parceiroMapper.updateParceiroJuridicoFromDTO(parceiroJuridicoDTO, parceiro);
		// Salva o parceiro atualizado
		parceiroRepository.save(parceiro);
	}

	@Transactional
	public void updateJuridicoComValidacao(UUID id, ParceiroJuridicoDTO parceiroJuridicoDTO) {
		List<String> erros = validateUniqueJuridico(parceiroJuridicoDTO);
		if (!erros.isEmpty()) {
			throw new ParceiroValidationException(String.join(", ", erros));
		}
		updateJuridico(id, parceiroJuridicoDTO);
	}

	@Transactional
	public void deleteById(UUID parceiroId) {
		Parceiro parceiro = parceiroRepository.findById(parceiroId)
				.orElseThrow(() -> new ParceiroNotFoundException("Parceiro não encontrado."));
		Fisica fisica;
		if ((parceiro.getPessoa()).getClass() == Fisica.class) {
			fisica = fisicaRepository.findById(parceiroId)
					.orElseThrow(() -> new ParceiroNotFoundException("Pessoa física não encontrada."));
			if (pessoaService.parceiroTemOutroPapel(fisica.getId())) {
				parceiroRepository.deleteById(parceiroId);
				return;
			} else {
				parceiroRepository.deleteById(parceiroId);
				fisicaRepository.deleteById(parceiroId);
				pessoaRepository.deleteById(parceiroId);
				return;
			}
		}
		Juridica juridica;
		if ((parceiro.getPessoa()).getClass() == Juridica.class) {
			juridica = juridicaRepository.findById(parceiroId)
					.orElseThrow(() -> new ParceiroNotFoundException("Pessoa jurídica não encontrada."));
			if (pessoaService.parceiroTemOutroPapel(juridica.getId())) {
				parceiroRepository.deleteById(parceiroId);

				return;
			} else {
				parceiroRepository.deleteById(parceiroId);
				juridicaRepository.deleteById(parceiroId);
				pessoaRepository.deleteById(parceiroId);
				return;
			}
		}
	}

	public List<ParceiroDTO> findAll() {
		List<Parceiro> parceiros = parceiroRepository.findAll();
		List<ParceiroDTO> parceirosDTO = new ArrayList<>();
		for (Parceiro parceiro : parceiros) {
			ParceiroDTO dto = new ParceiroDTO();
			dto.setId(parceiro.getId());
			dto.setNome(parceiro.getPessoa().getNome());
			dto.setEmail(parceiro.getPessoa().getEmail());
			dto.setTelefone(parceiro.getPessoa().getTelefone());
			dto.setMunicipio(parceiro.getPessoa().getMunicipio());
			dto.setUf(parceiro.getPessoa().getUf());
			dto.setTipo(parceiro.getPessoa().getTipo());
			parceirosDTO.add(dto);
		}
		return parceirosDTO;
	}

	@Transactional(readOnly = true)
	public ParceiroDTO findById(UUID id) {
		// Buscar na tabela de pessoas físicas
		Optional<Fisica> fisicaOptional = fisicaRepository.findById(id);
		if (fisicaOptional.isPresent()) {
			Fisica fisica = fisicaOptional.get();
			Parceiro parceiro = parceiroRepository.findByPessoaId(fisica.getId())
					.orElseThrow(() -> new ParceiroNotFoundException("Parceiro associado não encontrado."));
			return parceiroMapper.toParceiroFisicoDTO(fisica, parceiro);
		}
		// Buscar na tabela de pessoas jurídicas
		Optional<Juridica> juridicaOptional = juridicaRepository.findById(id);
		if (juridicaOptional.isPresent()) {
			Juridica juridica = juridicaOptional.get();
			Parceiro parceiro = parceiroRepository.findByPessoaId(juridica.getId())
					.orElseThrow(() -> new ParceiroNotFoundException("Parceiro associado não encontrado."));
			return parceiroMapper.toParceiroJuridicoDTO(juridica, parceiro);
		}
		// Se não encontrado em nenhum dos dois
		throw new ParceiroNotFoundException("Parceiro não encontrado.");
	}

	public ParceiroFisicoDTO findFisicoById(UUID id) {
		// Buscar o parceiro pelo ID
		Parceiro parceiro = parceiroRepository.findById(id)
				.orElseThrow(() -> new ParceiroNotFoundException("Parceiro não encontrado com o ID: " + id));
		// Garantir que a pessoa associada é do tipo Fisica
		if (!(parceiro.getPessoa() instanceof Fisica fisica)) {
			throw new IllegalStateException("A pessoa associada ao parceiro não é do tipo Física.");
		}
		// Mapear o parceiro e a entidade Fisica para ParceiroFisicoDTO
		return parceiroMapper.toParceiroFisicoDTO(fisica, parceiro);
	}

	public ParceiroJuridicoDTO findJuridicoById(UUID id) {
		// Buscar o parceiro pelo ID
		Parceiro parceiro = parceiroRepository.findById(id)
				.orElseThrow(() -> new ParceiroNotFoundException("Parceiro não encontrado com o ID: " + id));
		// Garantir que a pessoa associada é do tipo Fisica
		if (!(parceiro.getPessoa() instanceof Juridica juridica)) {
			throw new IllegalStateException("A pessoa associada ao parceiro não é do tipo Jurídica.");
		}
		// Mapear o parceiro e a entidade Fisica para ParceiroFisicoDTO
		return parceiroMapper.toParceiroJuridicoDTO(juridica, parceiro);
	}

	public List<String> validateUniqueFisico(ParceiroFisicoDTO parceiroFisicoDTO) {
		UUID id = parceiroFisicoDTO.getId();
		List<UniqueFieldRule> rules = List.of(
				new UniqueFieldRule("nome", parceiroFisicoDTO.getNome(), valor -> pessoaRepository.existsByNome(valor),
						valor -> pessoaRepository.existsByNomeAndIdNot(valor, id)),
				new UniqueFieldRule("email", parceiroFisicoDTO.getEmail(), valor -> pessoaRepository.existsByEmail(valor),
						valor -> pessoaRepository.existsByEmailAndIdNot(valor, id)),
				new UniqueFieldRule("telefone", parceiroFisicoDTO.getTelefone(),
						valor -> pessoaRepository.existsByTelefone(valor),
						valor -> pessoaRepository.existsByTelefoneAndIdNot(valor, id)),
				new UniqueFieldRule("rg", parceiroFisicoDTO.getRg(), valor -> fisicaRepository.existsByRg(valor),
						valor -> fisicaRepository.existsByRgAndIdNot(valor, id)),
				new UniqueFieldRule("cpf", parceiroFisicoDTO.getCpf(), valor -> fisicaRepository.existsByCpf(valor),
						valor -> fisicaRepository.existsByCpfAndIdNot(valor, id)));
		return UniqueRuleEngine.validate(id, rules);
	}

	public List<String> validateUniqueJuridico(@Valid ParceiroJuridicoDTO parceiroJuridicoDTO) {
		UUID id = parceiroJuridicoDTO.getId();
		List<UniqueFieldRule> rules = List.of(
				new UniqueFieldRule("nome", parceiroJuridicoDTO.getNome(), valor -> pessoaRepository.existsByNome(valor),
						valor -> pessoaRepository.existsByNomeAndIdNot(valor, id)),
				new UniqueFieldRule("email", parceiroJuridicoDTO.getEmail(), valor -> pessoaRepository.existsByEmail(valor),
						valor -> pessoaRepository.existsByEmailAndIdNot(valor, id)),
				new UniqueFieldRule("telefone", parceiroJuridicoDTO.getTelefone(),
						valor -> pessoaRepository.existsByTelefone(valor),
						valor -> pessoaRepository.existsByTelefoneAndIdNot(valor, id)),
				new UniqueFieldRule("ie", parceiroJuridicoDTO.getIe(), valor -> juridicaRepository.existsByIe(valor),
						valor -> juridicaRepository.existsByIeAndIdNot(valor, id)),
				new UniqueFieldRule("cnpj", parceiroJuridicoDTO.getCnpj(), valor -> juridicaRepository.existsByCnpj(valor),
						valor -> juridicaRepository.existsByCnpjAndIdNot(valor, id)));
		return UniqueRuleEngine.validate(id, rules);
	}

	public boolean existsById(UUID id) {
		return parceiroRepository.existsById(id);
	}

	public List<String> validateCadastroFisico(ParceiroFisicoDTO dto) {
		List<String> erros = validateUniqueFisico(dto);
		if (dto.getId() != null && existsById(dto.getId())) {
			erros.add("Parceiro já está cadastrado");
		}
		return erros;
	}

	public List<String> validateCadastroJuridico(ParceiroJuridicoDTO dto) {
		List<String> erros = validateUniqueJuridico(dto);
		if (dto.getId() != null && existsById(dto.getId())) {
			erros.add("Parceiro já está cadastrado");
		}
		return erros;
	}

	public ParceiroFisicoDTO findPessoaFisicaById(UUID id) {
		// Buscar a pessoa independente do tipo
		Pessoa pessoa = pessoaRepository.findById(id)
				.orElseThrow(() -> new ParceiroNotFoundException("Pessoa não encontrada."));
		// Verificar se a pessoa é do tipo correto
		if (!"FÍSICA".equals(pessoa.getTipo())) {
			throw new IllegalStateException("Pessoa selecionada não é Física.");
		}
			Fisica fisica = fisicaRepository.findById(id)
					.orElseThrow(() -> new ParceiroNotFoundException("Pessoa física não encontrada."));
			Parceiro parceiro = new Parceiro();
			parceiro.setId(id);
			// Mapear para ParceiroFisicoDTO
		return parceiroMapper.toParceiroFisicoDTO(fisica, parceiro);
	}

	public ParceiroJuridicoDTO findPessoaJuridicaById(UUID id) {
		// Buscar a pessoa independente do tipo
		Pessoa pessoa = pessoaRepository.findById(id)
				.orElseThrow(() -> new ParceiroNotFoundException("Pessoa não encontrada."));
		// Verificar se a pessoa é do tipo correto
		if (!"JURÍDICA".equals(pessoa.getTipo())) {
			throw new IllegalStateException("Pessoa selecionada não é Jurídica.");
		}
			Juridica juridica = juridicaRepository.findById(id)
					.orElseThrow(() -> new ParceiroNotFoundException("Pessoa jurídica não encontrada."));
			Parceiro parceiro = new Parceiro();
			parceiro.setId(id);
			// Mapear para ParceiroJuridicoDTO
			return parceiroMapper.toParceiroJuridicoDTO(juridica, parceiro);
	}

	private void validarConsistenciaIdRequisicao(UUID pathId, UUID bodyId) {
		if (bodyId != null && !pathId.equals(bodyId)) {
			throw new ParceiroValidationException("O ID informado no corpo da requisição difere do ID da URL.");
		}
	}
}
