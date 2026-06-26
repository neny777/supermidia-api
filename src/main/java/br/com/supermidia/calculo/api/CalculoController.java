package br.com.supermidia.calculo.api;

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

import br.com.supermidia.calculo.api.dto.CalculoCreateRequest;
import br.com.supermidia.calculo.api.dto.CalculoResponse;
import br.com.supermidia.calculo.api.dto.CalculoUpdateRequest;
import br.com.supermidia.calculo.app.CalculoService;
import br.com.supermidia.calculo.domain.Calculo;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/calculos")
public class CalculoController {

	private final CalculoService service;
	private final CalculoMapper mapper;

	public CalculoController(CalculoService service, CalculoMapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<CalculoResponse> create(@Valid @RequestBody CalculoCreateRequest request) {
		Calculo entity = mapper.toEntity(request);
		Calculo saved = service.create(entity);
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(saved));
	}

	@GetMapping
	public ResponseEntity<List<CalculoResponse>> findAll() {
		List<CalculoResponse> out = service.findAll().stream().map(mapper::toResponse).toList();
		return ResponseEntity.ok(out);
	}

	@GetMapping("/{id}")
	public ResponseEntity<CalculoResponse> findById(@PathVariable UUID id) {
		return ResponseEntity.ok(mapper.toResponse(service.findById(id)));
	}

	@PutMapping("/{id}")
	public ResponseEntity<CalculoResponse> update(@PathVariable UUID id, @Valid @RequestBody CalculoUpdateRequest request) {
		Calculo updated = service.update(id, entity -> mapper.updateEntity(request, entity));
		return ResponseEntity.ok(mapper.toResponse(updated));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
