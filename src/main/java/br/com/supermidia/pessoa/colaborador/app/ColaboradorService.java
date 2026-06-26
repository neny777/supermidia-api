package br.com.supermidia.pessoa.colaborador.app;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.supermidia.pessoa.colaborador.api.ColaboradorMapper;
import br.com.supermidia.pessoa.colaborador.api.dto.ColaboradorDTO;
import br.com.supermidia.pessoa.colaborador.domain.Colaborador;
import br.com.supermidia.pessoa.colaborador.infra.ColaboradorRepository;
import br.com.supermidia.pessoa.dominio.domain.Fisica;
import br.com.supermidia.pessoa.dominio.infra.FisicaRepository;
import br.com.supermidia.pessoa.dominio.domain.Pessoa;
import br.com.supermidia.pessoa.dominio.infra.PessoaRepository;
import br.com.supermidia.pessoa.dominio.app.PessoaService;
import br.com.supermidia.pessoa.shared.UniqueFieldRule;
import br.com.supermidia.pessoa.shared.UniqueRuleEngine;

@Service
public class ColaboradorService {
	private final PessoaService pessoaService;
	private final ColaboradorRepository colaboradorRepository;
	private final PessoaRepository pessoaRepository;
	private final FisicaRepository fisicaRepository;
	private final ColaboradorMapper colaboradorMapper;

	public ColaboradorService(PessoaService pessoaService, ColaboradorRepository colaboradorRepository,
			PessoaRepository pessoaRepository, FisicaRepository fisicaRepository, ColaboradorMapper colaboradorMapper) {
		this.pessoaService = pessoaService;
		this.colaboradorRepository = colaboradorRepository;
		this.pessoaRepository = pessoaRepository;
		this.fisicaRepository = fisicaRepository;
		this.colaboradorMapper = colaboradorMapper;
	}

	@Transactional
	public Colaborador save(ColaboradorDTO dto) {
		Colaborador colaborador = colaboradorMapper.toColaborador(dto);
		if (dto.getId() != null) {
			Fisica fisica = fisicaRepository.findById(dto.getId())
					.orElseThrow(() -> new ColaboradorNotFoundException("Pessoa física não encontrada."));
			colaborador.setFisica(fisica);
			if (colaboradorRepository.existsById(dto.getId())) {
				colaborador.setId(dto.getId());
			} else {
				colaborador.setId(null);
			}
		}
		return colaboradorRepository.save(colaborador);
	}

	@Transactional
	public Colaborador create(ColaboradorDTO dto) {
		List<String> erros = validateCadastroFisico(dto);
		if (!erros.isEmpty()) {
			throw new ColaboradorValidationException(String.join(", ", erros));
		}
		return save(dto);
	}

	@Transactional
	public void update(UUID id, ColaboradorDTO colaboradorDTO) {
		validarConsistenciaIdRequisicao(id, colaboradorDTO.getId());
		// Busca o colaborador existente
		Colaborador colaborador = colaboradorRepository.findById(id)
				.orElseThrow(() -> new ColaboradorNotFoundException("Colaborador não encontrado."));
		// Atualiza os dados do colaborador usando o mapper
		colaboradorDTO.setId(id);
		colaboradorMapper.updateColaboradorFromDTO(colaboradorDTO, colaborador);
		// Salva o colaborador atualizado
		colaboradorRepository.save(colaborador);
	}

	@Transactional
	public void updateComValidacao(UUID id, ColaboradorDTO colaboradorDTO) {
		validarConsistenciaIdRequisicao(id, colaboradorDTO.getId());
		colaboradorDTO.setId(id);
		List<String> erros = validateUniqueFisico(colaboradorDTO);
		if (!erros.isEmpty()) {
			throw new ColaboradorValidationException(String.join(", ", erros));
		}
		update(id, colaboradorDTO);
	}

	@Transactional
	public void deleteById(UUID colaboradorId) {
		Colaborador colaborador = colaboradorRepository.findById(colaboradorId)
				.orElseThrow(() -> new ColaboradorNotFoundException("Colaborador não encontrado."));
		Fisica fisica = fisicaRepository.findById(colaborador.getId())
				.orElseThrow(() -> new ColaboradorNotFoundException("Pessoa física não encontrada."));
		if (pessoaService.colaboradorTemOutroPapel(fisica.getId())) {
			colaboradorRepository.deleteById(colaborador.getId());
			return;
		} else {
			colaboradorRepository.deleteById(colaborador.getId());
			fisicaRepository.deleteById(colaborador.getId());
			pessoaRepository.deleteById(colaborador.getId());
			return;
		}
	}

	public List<ColaboradorDTO> findAll() {
		List<Colaborador> colaboradores = colaboradorRepository.findAll();
		List<ColaboradorDTO> colaboradoresDTO = new ArrayList<>();
		for (Colaborador colaborador : colaboradores) {
			ColaboradorDTO dto = new ColaboradorDTO();
			dto.setId(colaborador.getId());
			dto.setNome(colaborador.getFisica().getNome());
			dto.setEmail(colaborador.getFisica().getEmail());
			dto.setTelefone(colaborador.getFisica().getTelefone());
			dto.setMunicipio(colaborador.getFisica().getMunicipio());
			dto.setUf(colaborador.getFisica().getUf());
			colaboradoresDTO.add(dto);
		}
		return colaboradoresDTO;
	}

	public ColaboradorDTO findById(UUID id) {
		// Buscar o colaborador pelo ID
		Colaborador colaborador = colaboradorRepository.findById(id)
				.orElseThrow(() -> new ColaboradorNotFoundException("Colaborador não encontrado com o ID: " + id));
		// Mapear o colaborador e a entidade Fisica para ColaboradorDTO
		return colaboradorMapper.toColaboradorDTO(colaborador.getFisica(), colaborador);
	}

	public List<String> validateUniqueFisico(ColaboradorDTO colaboradorDTO) {
		UUID id = colaboradorDTO.getId();
		List<UniqueFieldRule> rules = List.of(
				new UniqueFieldRule("nome", colaboradorDTO.getNome(), valor -> pessoaRepository.existsByNome(valor),
						valor -> pessoaRepository.existsByNomeAndIdNot(valor, id)),
				new UniqueFieldRule("email", colaboradorDTO.getEmail(), valor -> pessoaRepository.existsByEmail(valor),
						valor -> pessoaRepository.existsByEmailAndIdNot(valor, id)),
				new UniqueFieldRule("telefone", colaboradorDTO.getTelefone(),
						valor -> pessoaRepository.existsByTelefone(valor),
						valor -> pessoaRepository.existsByTelefoneAndIdNot(valor, id)),
				new UniqueFieldRule("rg", colaboradorDTO.getRg(), valor -> fisicaRepository.existsByRg(valor),
						valor -> fisicaRepository.existsByRgAndIdNot(valor, id)),
				new UniqueFieldRule("cpf", colaboradorDTO.getCpf(), valor -> fisicaRepository.existsByCpf(valor),
						valor -> fisicaRepository.existsByCpfAndIdNot(valor, id)));
		return UniqueRuleEngine.validate(id, rules);
	}

	public boolean existsById(UUID id) {
		return colaboradorRepository.existsById(id);
	}

	public ColaboradorDTO findPessoaFisicaById(UUID id) {
		// Buscar a pessoa independente do tipo
		Pessoa pessoa = pessoaRepository.findById(id)
				.orElseThrow(() -> new ColaboradorNotFoundException("Pessoa não encontrada."));
		// Verificar se a pessoa é do tipo correto
		if (!"FÍSICA".equals(pessoa.getTipo())) {
			throw new ColaboradorValidationException("Pessoa selecionada não é Física.");
		}
		Fisica fisica = fisicaRepository.findById(id)
				.orElseThrow(() -> new ColaboradorNotFoundException("Pessoa física não encontrada."));
		Colaborador colaborador = new Colaborador();
		colaborador.setId(id);
		// Mapear para ColaboradorDTO
		return colaboradorMapper.toColaboradorDTO(fisica, colaborador);
	}

	public List<String> validateCadastroFisico(ColaboradorDTO dto) {
		List<String> erros = validateUniqueFisico(dto);
		if (dto.getId() != null && existsById(dto.getId())) {
			erros.add("Colaborador já está cadastrado");
		}
		return erros;
	}

	private void validarConsistenciaIdRequisicao(UUID pathId, UUID bodyId) {
		if (bodyId != null && !pathId.equals(bodyId)) {
			throw new ColaboradorValidationException("O ID informado no corpo da requisição difere do ID da URL.");
		}
	}
}
