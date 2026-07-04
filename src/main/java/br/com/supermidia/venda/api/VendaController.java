package br.com.supermidia.venda.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.supermidia.venda.api.dto.VendaCreateRequest;
import br.com.supermidia.venda.api.dto.VendaResponse;
import br.com.supermidia.venda.app.VendaService;
import br.com.supermidia.venda.domain.StatusVenda;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

	private final VendaService service;
	private final VendaMapper mapper;

	public VendaController(VendaService service, VendaMapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<VendaResponse> create(@Valid @RequestBody VendaCreateRequest request) {
		VendaResponse response = mapper.toResponse(service.criar(request));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<VendaResponse>> findAll(@RequestParam(required = false) StatusVenda status) {
		List<VendaResponse> out = (status == null ? service.findAll() : service.findByStatus(status)).stream()
				.map(mapper::toResponse).toList();
		return ResponseEntity.ok(out);
	}

	@GetMapping("/{id}")
	public ResponseEntity<VendaResponse> findById(@PathVariable UUID id) {
		return ResponseEntity.ok(mapper.toResponse(service.findById(id)));
	}

	@PostMapping("/{id}/ordem-servico")
	public ResponseEntity<VendaResponse> converterParaOrdemServico(@PathVariable UUID id) {
		return ResponseEntity.ok(mapper.toResponse(service.converterParaOrdemServico(id)));
	}

	@PostMapping("/{id}/cancelar")
	public ResponseEntity<VendaResponse> cancelar(@PathVariable UUID id) {
		return ResponseEntity.ok(mapper.toResponse(service.cancelar(id)));
	}

	@PostMapping("/{id}/recalcular")
	public ResponseEntity<VendaResponse> recalcular(@PathVariable UUID id) {
		return ResponseEntity.ok(mapper.toResponse(service.recalcular(id)));
	}
}
