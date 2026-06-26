package br.com.supermidia.pessoa.cliente.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.supermidia.pessoa.cliente.api.dto.ClienteDTO;
import br.com.supermidia.pessoa.cliente.api.dto.ClienteFisicoDTO;
import br.com.supermidia.pessoa.cliente.api.dto.ClienteJuridicoDTO;
import br.com.supermidia.pessoa.cliente.app.ClienteValidationException;
import br.com.supermidia.pessoa.cliente.app.ClienteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

	private final ClienteService clienteService;

	public ClienteController(ClienteService clienteService) {
		this.clienteService = clienteService;
	}

	// Buscar pessoa física
	@GetMapping("/pessoa/fisica/{id}")
	public ResponseEntity<ClienteFisicoDTO> findPessoaFisicaById(@PathVariable UUID id) {

		ClienteFisicoDTO clienteFisicoDTO = clienteService.findPessoaFisicaById(id);

		return ResponseEntity.ok(clienteFisicoDTO);
	}

	// Buscar pessoa jurídica
	@GetMapping("/pessoa/juridica/{id}")
	public ResponseEntity<ClienteJuridicoDTO> findPessoaJuridicaById(@PathVariable UUID id) {

		ClienteJuridicoDTO clienteJuridicoDTO = clienteService.findPessoaJuridicaById(id);

		return ResponseEntity.ok(clienteJuridicoDTO);
	}

	// Buscar cliente físico
	@GetMapping("/fisico/{id}")
	public ResponseEntity<ClienteFisicoDTO> findClienteFisicoById(@PathVariable UUID id) {

		ClienteFisicoDTO clienteFisicoDTO = clienteService.findFisicoById(id);

		return ResponseEntity.ok(clienteFisicoDTO);
	}

	// Buscar cliente jurídico
	@GetMapping("/juridico/{id}")
	public ResponseEntity<ClienteJuridicoDTO> findClienteJuridicoById(@PathVariable UUID id) {

		ClienteJuridicoDTO clienteJuridicoDTO = clienteService.findJuridicoById(id);

		return ResponseEntity.ok(clienteJuridicoDTO);
	}

	// Cadastrar cliente físico
	@PostMapping("/fisico")
	public ResponseEntity<Void> createClienteFisico(@RequestBody @Valid ClienteFisicoDTO dto) {
		clienteService.createFisico(dto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	// Cadastrar cliente jurídico
	@PostMapping("/juridico")
	public ResponseEntity<Void> createClienteJuridico(@RequestBody @Valid ClienteJuridicoDTO dto) {
		clienteService.createJuridico(dto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	// Atualizar cliente físico
	@PutMapping("/fisico/{id}")
	public ResponseEntity<Void> updateClienteFisico(@PathVariable UUID id, @RequestBody @Valid ClienteFisicoDTO dto) {
		clienteService.updateFisicoComValidacao(id, dto);
		return ResponseEntity.noContent().build();
	}

	// Atualizar cliente jurídico
	@PutMapping("/juridico/{id}")
	public ResponseEntity<Void> updateClienteJuridico(@PathVariable UUID id, @RequestBody @Valid ClienteJuridicoDTO dto) {
		clienteService.updateJuridicoComValidacao(id, dto);
		return ResponseEntity.noContent().build();
	}

	// Excluir cliente
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteClienteById(@PathVariable UUID id) {
		clienteService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	// Listar todos os clientes
	@GetMapping
	public ResponseEntity<List<ClienteDTO>> findAll() {

		List<ClienteDTO> clientes = clienteService.findAll();
		return ResponseEntity.ok(clientes);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ClienteDTO> findById(@PathVariable UUID id) {
		ClienteDTO clienteDTO = clienteService.findById(id);
		return ResponseEntity.ok(clienteDTO);
	}

	@PostMapping("/fisico/validar")
	public ResponseEntity<Void> validateClienteFisico(@RequestBody @Valid ClienteFisicoDTO dto) {
		List<String> erros = clienteService.validateCadastroFisico(dto);
		if (!erros.isEmpty()) {
			throw new ClienteValidationException(String.join(", ", erros));
		}
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/juridico/validar")
	public ResponseEntity<Void> validateClienteJuridico(@RequestBody @Valid ClienteJuridicoDTO dto) {
		List<String> erros = clienteService.validateCadastroJuridico(dto);
		if (!erros.isEmpty()) {
			throw new ClienteValidationException(String.join(", ", erros));
		}
		return ResponseEntity.noContent().build();
	}
}
