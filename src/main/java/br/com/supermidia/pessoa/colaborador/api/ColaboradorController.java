package br.com.supermidia.pessoa.colaborador.api;

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

import br.com.supermidia.pessoa.colaborador.api.dto.ColaboradorDTO;
import br.com.supermidia.pessoa.colaborador.app.ColaboradorValidationException;
import br.com.supermidia.pessoa.colaborador.app.ColaboradorService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/colaboradores")
public class ColaboradorController {
	private final ColaboradorService colaboradorService;

	public ColaboradorController(ColaboradorService colaboradorService) {
		this.colaboradorService = colaboradorService;
	}

	// Colaboradores que ainda não são usuários (para o cadastro de novo usuário)
	@GetMapping("/nao-usuario")
	public ResponseEntity<List<ColaboradorDTO>> findNaoUsuarios() {
		return ResponseEntity.ok(colaboradorService.findNaoUsuarios());
	}

	// Buscar pessoa física
	@GetMapping("/fisica/{id}")
	public ResponseEntity<ColaboradorDTO> findPessoaFisicaById(@PathVariable UUID id) {
		
		ColaboradorDTO colaboradorDTO = colaboradorService.findPessoaFisicaById(id);

		return ResponseEntity.ok(colaboradorDTO);
	}
	// Buscar colaborador físico
	@GetMapping("/{id}")
	public ResponseEntity<ColaboradorDTO> findById(@PathVariable UUID id) {
		ColaboradorDTO colaboradorDTO = colaboradorService.findById(id);
		return ResponseEntity.ok(colaboradorDTO);
	}
	
	
	// Cadastrar colaborador físico
	@PostMapping
	public ResponseEntity<Void> createColaborador(@RequestBody @Valid ColaboradorDTO dto) {
		colaboradorService.create(dto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	// Atualizar colaborador físico
	@PutMapping("/{id}")
	public ResponseEntity<Void> updateColaborador(@PathVariable UUID id, @RequestBody @Valid ColaboradorDTO dto) {
		colaboradorService.updateComValidacao(id, dto);
		return ResponseEntity.noContent().build();
	}
	// Excluir colaborador
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteColaboradorById(@PathVariable UUID id) {		
		colaboradorService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	// Listar todos os colaboradores
	@GetMapping
	public ResponseEntity<List<ColaboradorDTO>> findAll() {

		List<ColaboradorDTO> colaboradores = colaboradorService.findAll();
		return ResponseEntity.ok(colaboradores);
	}

	@PostMapping("/validar")
	public ResponseEntity<Void> validateColaborador(@RequestBody @Valid ColaboradorDTO dto) {
		List<String> erros = colaboradorService.validateCadastroFisico(dto);
		if (!erros.isEmpty()) {
			throw new ColaboradorValidationException(String.join(", ", erros));
		}
		return ResponseEntity.noContent().build();
	}
}
