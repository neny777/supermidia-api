package br.com.supermidia.servico.api;

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

import br.com.supermidia.servico.api.dto.ServicoCreateRequest;
import br.com.supermidia.servico.api.dto.ServicoResponse;
import br.com.supermidia.servico.api.dto.ServicoUpdateRequest;
import br.com.supermidia.servico.app.ServicoService;
import br.com.supermidia.servico.domain.Servico;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

	private final ServicoService service;
	private final ServicoMapper mapper;

	public ServicoController(ServicoService service, ServicoMapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<ServicoResponse> create(@Valid @RequestBody ServicoCreateRequest request) {
		Servico saved = service.create(mapper.toEntity(request));
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(saved));
	}

	@GetMapping
	public ResponseEntity<List<ServicoResponse>> findAll() {
		List<ServicoResponse> out = service.findAll().stream().map(mapper::toResponse).toList();
		return ResponseEntity.ok(out);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ServicoResponse> findById(@PathVariable UUID id) {
		return ResponseEntity.ok(mapper.toResponse(service.findById(id)));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ServicoResponse> update(@PathVariable UUID id,
			@Valid @RequestBody ServicoUpdateRequest request) {
		Servico updated = service.update(id, entity -> mapper.updateEntity(request, entity));
		return ResponseEntity.ok(mapper.toResponse(updated));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
