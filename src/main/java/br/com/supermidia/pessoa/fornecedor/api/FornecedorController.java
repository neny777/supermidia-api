package br.com.supermidia.pessoa.fornecedor.api;

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

import br.com.supermidia.pessoa.fornecedor.api.dto.FornecedorDTO;
import br.com.supermidia.pessoa.fornecedor.api.dto.FornecedorFisicoDTO;
import br.com.supermidia.pessoa.fornecedor.api.dto.FornecedorJuridicoDTO;
import br.com.supermidia.pessoa.fornecedor.app.FornecedorValidationException;
import br.com.supermidia.pessoa.fornecedor.app.FornecedorService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/fornecedores")
public class FornecedorController {

	private final FornecedorService fornecedorService;

	public FornecedorController(FornecedorService fornecedorService) {
		this.fornecedorService = fornecedorService;
	}

	// Buscar pessoa física
	@GetMapping("/pessoa/fisica/{id}")
	public ResponseEntity<FornecedorFisicoDTO> findPessoaFisicaById(@PathVariable UUID id) {

		FornecedorFisicoDTO fornecedorFisicoDTO = fornecedorService.findPessoaFisicaById(id);

		return ResponseEntity.ok(fornecedorFisicoDTO);
	}

	// Buscar pessoa jurídica
	@GetMapping("/pessoa/juridica/{id}")
	public ResponseEntity<FornecedorJuridicoDTO> findPessoaJuridicaById(@PathVariable UUID id) {

		FornecedorJuridicoDTO fornecedorJuridicoDTO = fornecedorService.findPessoaJuridicaById(id);

		return ResponseEntity.ok(fornecedorJuridicoDTO);
	}

	// Buscar fornecedor físico
	@GetMapping("/fisico/{id}")
	public ResponseEntity<FornecedorFisicoDTO> findFornecedorFisicoById(@PathVariable UUID id) {

		FornecedorFisicoDTO fornecedorFisicoDTO = fornecedorService.findFisicoById(id);

		return ResponseEntity.ok(fornecedorFisicoDTO);
	}

	// Buscar fornecedor jurídico
	@GetMapping("/juridico/{id}")
	public ResponseEntity<FornecedorJuridicoDTO> findFornecedorJuridicoById(@PathVariable UUID id) {

		FornecedorJuridicoDTO fornecedorJuridicoDTO = fornecedorService.findJuridicoById(id);

		return ResponseEntity.ok(fornecedorJuridicoDTO);
	}

	// Cadastrar fornecedor físico
	@PostMapping("/fisico")
	public ResponseEntity<Void> createFornecedorFisico(@RequestBody @Valid FornecedorFisicoDTO dto) {
		fornecedorService.createFisico(dto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	// Cadastrar fornecedor jurídico
	@PostMapping("/juridico")
	public ResponseEntity<Void> createFornecedorJuridico(@RequestBody @Valid FornecedorJuridicoDTO dto) {
		fornecedorService.createJuridico(dto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	// Atualizar fornecedor físico
	@PutMapping("/fisico/{id}")
	public ResponseEntity<Void> updateFornecedorFisico(@PathVariable UUID id,
			@RequestBody @Valid FornecedorFisicoDTO dto) {
		fornecedorService.updateFisicoComValidacao(id, dto);
		return ResponseEntity.noContent().build();
	}

	// Atualizar fornecedor jurídico
	@PutMapping("/juridico/{id}")
	public ResponseEntity<Void> updateFornecedorJuridico(@PathVariable UUID id,
			@RequestBody @Valid FornecedorJuridicoDTO dto) {
		fornecedorService.updateJuridicoComValidacao(id, dto);
		return ResponseEntity.noContent().build();
	}

	// Excluir fornecedor
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteFornecedorById(@PathVariable UUID id) {
		fornecedorService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	// Listar todos os fornecedores
	@GetMapping
	public ResponseEntity<List<FornecedorDTO>> findAll() {

		List<FornecedorDTO> fornecedores = fornecedorService.findAll();
		return ResponseEntity.ok(fornecedores);
	}

	@GetMapping("/{id}")
	public ResponseEntity<FornecedorDTO> findById(@PathVariable UUID id) {
		FornecedorDTO fornecedorDTO = fornecedorService.findById(id);
		return ResponseEntity.ok(fornecedorDTO);
	}

	@PostMapping("/fisico/validar")
	public ResponseEntity<Void> validateFornecedorFisico(@RequestBody @Valid FornecedorFisicoDTO dto) {
		List<String> erros = fornecedorService.validateCadastroFisico(dto);
		if (!erros.isEmpty()) {
			throw new FornecedorValidationException(String.join(", ", erros));
		}
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/juridico/validar")
	public ResponseEntity<Void> validateFornecedorJuridico(@RequestBody @Valid FornecedorJuridicoDTO dto) {
		List<String> erros = fornecedorService.validateCadastroJuridico(dto);
		if (!erros.isEmpty()) {
			throw new FornecedorValidationException(String.join(", ", erros));
		}
		return ResponseEntity.noContent().build();
	}
}
