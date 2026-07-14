package br.com.supermidia.pessoa.cliente.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import br.com.supermidia.pessoa.cliente.api.ClienteMapper;
import br.com.supermidia.pessoa.cliente.api.dto.ClienteDTO;
import br.com.supermidia.pessoa.cliente.api.dto.ClienteFisicoDTO;
import br.com.supermidia.pessoa.cliente.api.dto.ClienteJuridicoDTO;
import br.com.supermidia.pessoa.cliente.domain.Cliente;
import br.com.supermidia.pessoa.cliente.infra.ClienteRepository;
import br.com.supermidia.pessoa.dominio.app.PessoaService;
import br.com.supermidia.pessoa.dominio.domain.Fisica;
import br.com.supermidia.pessoa.dominio.domain.Juridica;
import br.com.supermidia.pessoa.dominio.domain.Pessoa;
import br.com.supermidia.pessoa.dominio.infra.FisicaRepository;
import br.com.supermidia.pessoa.dominio.infra.JuridicaRepository;
import br.com.supermidia.pessoa.dominio.infra.PessoaRepository;
import br.com.supermidia.pessoa.shared.UniqueFieldRule;
import br.com.supermidia.pessoa.shared.UniqueRuleEngine;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Validated
@Service
public class ClienteService {
	private final PessoaService pessoaService;
	private final ClienteRepository clienteRepository;
	private final PessoaRepository pessoaRepository;
	private final FisicaRepository fisicaRepository;
	private final JuridicaRepository juridicaRepository;
	private final ClienteMapper clienteMapper;

	public ClienteService(PessoaService pessoaService, ClienteRepository clienteRepository, PessoaRepository pessoaRepository,
			FisicaRepository fisicaRepository, JuridicaRepository juridicaRepository, ClienteMapper clienteMapper) {
		this.pessoaService = pessoaService;
		this.clienteRepository = clienteRepository;
		this.pessoaRepository = pessoaRepository;
		this.fisicaRepository = fisicaRepository;
		this.juridicaRepository = juridicaRepository;
		this.clienteMapper = clienteMapper;
	}

	@Transactional
	public Cliente saveFisico(ClienteFisicoDTO dto) {
		Cliente cliente = clienteMapper.toClienteFisico(dto);
		if (dto.getId() != null) {
			Fisica fisica = fisicaRepository.findById(dto.getId())
					.orElseThrow(() -> new ClienteNotFoundException("Pessoa física não encontrada."));
			cliente.setPessoa(fisica);
			if (clienteRepository.existsById(dto.getId())) {
				cliente.setId(dto.getId());
			} else {
				cliente.setId(null);
			}
		}
		return clienteRepository.save(cliente);
	}

	@Transactional
	public Cliente createFisico(ClienteFisicoDTO dto) {
		List<String> erros = validateCadastroFisico(dto);
		if (!erros.isEmpty()) {
			throw new ClienteValidationException(String.join(", ", erros));
		}
		return saveFisico(dto);
	}

	@Transactional
	public Cliente saveJuridico(ClienteJuridicoDTO dto) {
		Cliente cliente = clienteMapper.toClienteJuridico(dto);
		if (dto.getId() != null) {
			Juridica juridica = juridicaRepository.findById(dto.getId())
					.orElseThrow(() -> new ClienteNotFoundException("Pessoa jurídica não encontrada."));
			cliente.setPessoa(juridica);
			if (clienteRepository.existsById(dto.getId())) {
				cliente.setId(dto.getId());
			} else {
				cliente.setId(null);
			}
		}
		return clienteRepository.save(cliente);
	}

	@Transactional
	public Cliente createJuridico(ClienteJuridicoDTO dto) {
		List<String> erros = validateCadastroJuridico(dto);
		if (!erros.isEmpty()) {
			throw new ClienteValidationException(String.join(", ", erros));
		}
		return saveJuridico(dto);
	}

	@Transactional
	public void updateFisico(UUID id, ClienteFisicoDTO clienteFisicoDTO) {
		validarConsistenciaIdRequisicao(id, clienteFisicoDTO.getId());
		// Busca o cliente existente
		Cliente cliente = clienteRepository.findByPessoaId(id)
				.orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado."));
		// Atualiza os dados do cliente usando o mapper
		clienteFisicoDTO.setId(id);
		clienteMapper.updateClienteFisicoFromDTO(clienteFisicoDTO, cliente);
		// Salva o cliente atualizado
		clienteRepository.save(cliente);
	}

	@Transactional
	public void updateFisicoComValidacao(UUID id, ClienteFisicoDTO clienteFisicoDTO) {
		List<String> erros = validateUniqueFisico(clienteFisicoDTO);
		if (!erros.isEmpty()) {
			throw new ClienteValidationException(String.join(", ", erros));
		}
		updateFisico(id, clienteFisicoDTO);
	}

	@Transactional
	public void updateJuridico(UUID id, ClienteJuridicoDTO clienteJuridicoDTO) {
		validarConsistenciaIdRequisicao(id, clienteJuridicoDTO.getId());
		// Busca o cliente existente
		Cliente cliente = clienteRepository.findByPessoaId(id)
				.orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado."));
		// Atualiza os dados do cliente usando o mapper
		clienteJuridicoDTO.setId(id);
		clienteMapper.updateClienteJuridicoFromDTO(clienteJuridicoDTO, cliente);
		// Salva o cliente atualizado
		clienteRepository.save(cliente);
	}

	@Transactional
	public void updateJuridicoComValidacao(UUID id, ClienteJuridicoDTO clienteJuridicoDTO) {
		List<String> erros = validateUniqueJuridico(clienteJuridicoDTO);
		if (!erros.isEmpty()) {
			throw new ClienteValidationException(String.join(", ", erros));
		}
		updateJuridico(id, clienteJuridicoDTO);
	}

	@Transactional
	public void deleteById(UUID clienteId) {
		Cliente cliente = clienteRepository.findById(clienteId)
				.orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado."));
		Fisica fisica;
		if ((cliente.getPessoa()).getClass() == Fisica.class) {
			fisica = fisicaRepository.findById(clienteId)
					.orElseThrow(() -> new ClienteNotFoundException("Pessoa física não encontrada."));
			if (pessoaService.clienteTemOutroPapel(fisica.getId())) {
				clienteRepository.deleteById(clienteId);
				return;
			} else {
				clienteRepository.deleteById(clienteId);
				fisicaRepository.deleteById(clienteId);
				pessoaRepository.deleteById(clienteId);
				return;
			}
		}
		Juridica juridica;
		if ((cliente.getPessoa()).getClass() == Juridica.class) {
			juridica = juridicaRepository.findById(clienteId)
					.orElseThrow(() -> new ClienteNotFoundException("Pessoa jurídica não encontrada."));
			if (pessoaService.clienteTemOutroPapel(juridica.getId())) {
				clienteRepository.deleteById(clienteId);

				return;
			} else {
				clienteRepository.deleteById(clienteId);
				juridicaRepository.deleteById(clienteId);
				pessoaRepository.deleteById(clienteId);
				return;
			}
		}
	}

	public List<ClienteDTO> findAll() {
		List<Cliente> clientes = clienteRepository.findAll();
		List<ClienteDTO> clientesDTO = new ArrayList<>();
		for (Cliente cliente : clientes) {
			ClienteDTO dto = new ClienteDTO();
			dto.setId(cliente.getId());
			dto.setNome(cliente.getPessoa().getNome());
			dto.setEmail(cliente.getPessoa().getEmail());
			dto.setTelefone(cliente.getPessoa().getTelefone());
			dto.setMunicipio(cliente.getPessoa().getMunicipio());
			dto.setUf(cliente.getPessoa().getUf());
			dto.setTipo(cliente.getPessoa().getTipo());
			dto.setCategoria(cliente.getCategoria());
			clientesDTO.add(dto);
		}
		return clientesDTO;
	}

	@Transactional(readOnly = true)
	public ClienteDTO findById(UUID id) {
		// Buscar na tabela de pessoas físicas
		Optional<Fisica> fisicaOptional = fisicaRepository.findById(id);
		if (fisicaOptional.isPresent()) {
			Fisica fisica = fisicaOptional.get();
			Cliente cliente = clienteRepository.findByPessoaId(fisica.getId())
					.orElseThrow(() -> new ClienteNotFoundException("Cliente associado não encontrado."));
			return clienteMapper.toClienteFisicoDTO(fisica, cliente);
		}
		// Buscar na tabela de pessoas jurídicas
		Optional<Juridica> juridicaOptional = juridicaRepository.findById(id);
		if (juridicaOptional.isPresent()) {
			Juridica juridica = juridicaOptional.get();
			Cliente cliente = clienteRepository.findByPessoaId(juridica.getId())
					.orElseThrow(() -> new ClienteNotFoundException("Cliente associado não encontrado."));
			return clienteMapper.toClienteJuridicoDTO(juridica, cliente);
		}
		// Se não encontrado em nenhum dos dois
		throw new ClienteNotFoundException("Cliente não encontrado.");
	}

	public ClienteFisicoDTO findFisicoById(UUID id) {
		// Buscar o cliente pelo ID
		Cliente cliente = clienteRepository.findById(id)
				.orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado com o ID: " + id));
		// Garantir que a pessoa associada é do tipo Fisica
		if (!(cliente.getPessoa() instanceof Fisica fisica)) {
			throw new IllegalStateException("A pessoa associada ao cliente não é do tipo Física.");
		}
		// Mapear o cliente e a entidade Fisica para ClienteFisicoDTO
		return clienteMapper.toClienteFisicoDTO(fisica, cliente);
	}

	public ClienteJuridicoDTO findJuridicoById(UUID id) {
		// Buscar o cliente pelo ID
		Cliente cliente = clienteRepository.findById(id)
				.orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado com o ID: " + id));
		// Garantir que a pessoa associada é do tipo Fisica
		if (!(cliente.getPessoa() instanceof Juridica juridica)) {
			throw new IllegalStateException("A pessoa associada ao cliente não é do tipo Jurídica.");
		}
		// Mapear o cliente e a entidade Fisica para ClienteFisicoDTO
		return clienteMapper.toClienteJuridicoDTO(juridica, cliente);
	}

	public List<String> validateUniqueFisico(ClienteFisicoDTO clienteFisicoDTO) {
		UUID id = clienteFisicoDTO.getId();
		List<UniqueFieldRule> rules = List.of(
				new UniqueFieldRule("nome", clienteFisicoDTO.getNome(), valor -> pessoaRepository.existsByNome(valor),
						valor -> pessoaRepository.existsByNomeAndIdNot(valor, id)),
				new UniqueFieldRule("email", clienteFisicoDTO.getEmail(), valor -> pessoaRepository.existsByEmail(valor),
						valor -> pessoaRepository.existsByEmailAndIdNot(valor, id)),
				new UniqueFieldRule("telefone", clienteFisicoDTO.getTelefone(),
						valor -> pessoaRepository.existsByTelefone(valor),
						valor -> pessoaRepository.existsByTelefoneAndIdNot(valor, id)),
				new UniqueFieldRule("rg", clienteFisicoDTO.getRg(), valor -> fisicaRepository.existsByRg(valor),
						valor -> fisicaRepository.existsByRgAndIdNot(valor, id)),
				new UniqueFieldRule("cpf", clienteFisicoDTO.getCpf(), valor -> fisicaRepository.existsByCpf(valor),
						valor -> fisicaRepository.existsByCpfAndIdNot(valor, id)));
		return UniqueRuleEngine.validate(id, rules);
	}

	public List<String> validateUniqueJuridico(@Valid ClienteJuridicoDTO clienteJuridicoDTO) {
		UUID id = clienteJuridicoDTO.getId();
		List<UniqueFieldRule> rules = List.of(
				new UniqueFieldRule("nome", clienteJuridicoDTO.getNome(), valor -> pessoaRepository.existsByNome(valor),
						valor -> pessoaRepository.existsByNomeAndIdNot(valor, id)),
				new UniqueFieldRule("email", clienteJuridicoDTO.getEmail(), valor -> pessoaRepository.existsByEmail(valor),
						valor -> pessoaRepository.existsByEmailAndIdNot(valor, id)),
				new UniqueFieldRule("telefone", clienteJuridicoDTO.getTelefone(),
						valor -> pessoaRepository.existsByTelefone(valor),
						valor -> pessoaRepository.existsByTelefoneAndIdNot(valor, id)),
				new UniqueFieldRule("ie", clienteJuridicoDTO.getIe(), valor -> juridicaRepository.existsByIe(valor),
						valor -> juridicaRepository.existsByIeAndIdNot(valor, id)),
				new UniqueFieldRule("cnpj", clienteJuridicoDTO.getCnpj(), valor -> juridicaRepository.existsByCnpj(valor),
						valor -> juridicaRepository.existsByCnpjAndIdNot(valor, id)));
		return UniqueRuleEngine.validate(id, rules);
	}

	public boolean existsById(UUID id) {
		return clienteRepository.existsById(id);
	}

	public List<String> validateCadastroFisico(ClienteFisicoDTO dto) {
		List<String> erros = validateUniqueFisico(dto);
		if (dto.getId() != null && existsById(dto.getId())) {
			erros.add("Cliente já está cadastrado");
		}
		return erros;
	}

	public List<String> validateCadastroJuridico(ClienteJuridicoDTO dto) {
		List<String> erros = validateUniqueJuridico(dto);
		if (dto.getId() != null && existsById(dto.getId())) {
			erros.add("Cliente já está cadastrado");
		}
		return erros;
	}

	public ClienteFisicoDTO findPessoaFisicaById(UUID id) {
		// Buscar a pessoa independente do tipo
		Pessoa pessoa = pessoaRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada."));
		// Verificar se a pessoa é do tipo correto
		if (!"FÍSICA".equals(pessoa.getTipo())) {
			throw new IllegalStateException("Pessoa selecionada não é Física.");
		}
			Fisica fisica = fisicaRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Pessoa física não encontrada."));
			Cliente cliente = new Cliente();
			cliente.setId(id);
			// Mapear para ClienteFisicoDTO
		return clienteMapper.toClienteFisicoDTO(fisica, cliente);
	}

	public ClienteJuridicoDTO findPessoaJuridicaById(UUID id) {
		// Buscar a pessoa independente do tipo
		Pessoa pessoa = pessoaRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada."));
		// Verificar se a pessoa é do tipo correto
		if (!"JURÍDICA".equals(pessoa.getTipo())) {
			throw new IllegalStateException("Pessoa selecionada não é Jurídica.");
		}
			Juridica juridica = juridicaRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Pessoa jurídica não encontrada."));
			Cliente cliente = new Cliente();
			cliente.setId(id);
			// Mapear para ClienteJuridicoDTO
			return clienteMapper.toClienteJuridicoDTO(juridica, cliente);
	}

	private void validarConsistenciaIdRequisicao(UUID pathId, UUID bodyId) {
		if (bodyId != null && !pathId.equals(bodyId)) {
			throw new ClienteValidationException("O ID informado no corpo da requisição difere do ID da URL.");
		}
	}
}
