package br.com.supermidia.pessoa.parceiro.api;

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

import br.com.supermidia.pessoa.parceiro.api.dto.ParceiroDTO;
import br.com.supermidia.pessoa.parceiro.api.dto.ParceiroFisicoDTO;
import br.com.supermidia.pessoa.parceiro.api.dto.ParceiroJuridicoDTO;
import br.com.supermidia.pessoa.parceiro.app.ParceiroValidationException;
import br.com.supermidia.pessoa.parceiro.app.ParceiroService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/parceiros")
public class ParceiroController {

	private final ParceiroService parceiroService;

	public ParceiroController(ParceiroService parceiroService) {
		this.parceiroService = parceiroService;
	}

	// Buscar pessoa física
	@GetMapping("/pessoa/fisica/{id}")
	public ResponseEntity<ParceiroFisicoDTO> findPessoaFisicaById(@PathVariable UUID id) {

		ParceiroFisicoDTO parceiroFisicoDTO = parceiroService.findPessoaFisicaById(id);

		return ResponseEntity.ok(parceiroFisicoDTO);
	}

	// Buscar pessoa jurídica
	@GetMapping("/pessoa/juridica/{id}")
	public ResponseEntity<ParceiroJuridicoDTO> findPessoaJuridicaById(@PathVariable UUID id) {

		ParceiroJuridicoDTO parceiroJuridicoDTO = parceiroService.findPessoaJuridicaById(id);

		return ResponseEntity.ok(parceiroJuridicoDTO);
	}

	// Buscar parceiro físico
	@GetMapping("/fisico/{id}")
	public ResponseEntity<ParceiroFisicoDTO> findParceiroFisicoById(@PathVariable UUID id) {

		ParceiroFisicoDTO parceiroFisicoDTO = parceiroService.findFisicoById(id);

		return ResponseEntity.ok(parceiroFisicoDTO);
	}

	// Buscar parceiro jurídico
	@GetMapping("/juridico/{id}")
	public ResponseEntity<ParceiroJuridicoDTO> findParceiroJuridicoById(@PathVariable UUID id) {

		ParceiroJuridicoDTO parceiroJuridicoDTO = parceiroService.findJuridicoById(id);

		return ResponseEntity.ok(parceiroJuridicoDTO);
	}

	// Cadastrar parceiro físico
	@PostMapping("/fisico")
	public ResponseEntity<Void> createParceiroFisico(@RequestBody @Valid ParceiroFisicoDTO dto) {
		parceiroService.createFisico(dto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	// Cadastrar parceiro jurídico
	@PostMapping("/juridico")
	public ResponseEntity<Void> createParceiroJuridico(@RequestBody @Valid ParceiroJuridicoDTO dto) {
		parceiroService.createJuridico(dto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	// Atualizar parceiro físico
	@PutMapping("/fisico/{id}")
	public ResponseEntity<Void> updateParceiroFisico(@PathVariable UUID id, @RequestBody @Valid ParceiroFisicoDTO dto) {
		parceiroService.updateFisicoComValidacao(id, dto);
		return ResponseEntity.noContent().build();
	}

	// Atualizar parceiro jurídico
	@PutMapping("/juridico/{id}")
	public ResponseEntity<Void> updateParceiroJuridico(@PathVariable UUID id,
			@RequestBody @Valid ParceiroJuridicoDTO dto) {
		parceiroService.updateJuridicoComValidacao(id, dto);
		return ResponseEntity.noContent().build();
	}

	// Excluir parceiro
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteParceiroById(@PathVariable UUID id) {
		parceiroService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	// Listar todos os parceiros
	@GetMapping
	public ResponseEntity<List<ParceiroDTO>> findAll() {

		List<ParceiroDTO> parceiros = parceiroService.findAll();
		return ResponseEntity.ok(parceiros);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ParceiroDTO> findById(@PathVariable UUID id) {
		ParceiroDTO parceiroDTO = parceiroService.findById(id);
		return ResponseEntity.ok(parceiroDTO);
	}

	@PostMapping("/fisico/validar")
	public ResponseEntity<Void> validateParceiroFisico(@RequestBody @Valid ParceiroFisicoDTO dto) {
		List<String> erros = parceiroService.validateCadastroFisico(dto);
		if (!erros.isEmpty()) {
			throw new ParceiroValidationException(String.join(", ", erros));
		}
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/juridico/validar")
	public ResponseEntity<Void> validateParceiroJuridico(@RequestBody @Valid ParceiroJuridicoDTO dto) {
		List<String> erros = parceiroService.validateCadastroJuridico(dto);
		if (!erros.isEmpty()) {
			throw new ParceiroValidationException(String.join(", ", erros));
		}
		return ResponseEntity.noContent().build();
	}
}
