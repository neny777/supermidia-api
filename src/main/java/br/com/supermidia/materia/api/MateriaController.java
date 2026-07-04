package br.com.supermidia.materia.api;

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

import br.com.supermidia.materia.api.dto.MateriaCreateRequest;
import br.com.supermidia.materia.api.dto.MateriaResponse;
import br.com.supermidia.materia.api.dto.MateriaUpdateRequest;
import br.com.supermidia.materia.app.MateriaService;
import br.com.supermidia.materia.domain.Materia;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/materias")
public class MateriaController {

	private final MateriaService service;
	private final MateriaMapper mapper;

	public MateriaController(MateriaService service, MateriaMapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<MateriaResponse> create(@Valid @RequestBody MateriaCreateRequest request) {
		Materia saved = service.create(mapper.toEntity(request));
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(saved));
	}

	@GetMapping
	public ResponseEntity<List<MateriaResponse>> findAll() {
		List<MateriaResponse> out = service.findAll().stream().map(mapper::toResponse).toList();
		return ResponseEntity.ok(out);
	}

	@GetMapping("/grupos")
	public ResponseEntity<List<String>> findGrupos() {
		return ResponseEntity.ok(service.findGrupos());
	}

	@GetMapping("/{id}")
	public ResponseEntity<MateriaResponse> findById(@PathVariable UUID id) {
		return ResponseEntity.ok(mapper.toResponse(service.findById(id)));
	}

	@PutMapping("/{id}")
	public ResponseEntity<MateriaResponse> update(@PathVariable UUID id,
			@Valid @RequestBody MateriaUpdateRequest request) {
		Materia updated = service.update(id, entity -> mapper.updateEntity(request, entity));
		return ResponseEntity.ok(mapper.toResponse(updated));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
