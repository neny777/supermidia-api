package br.com.supermidia.venda.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.supermidia.security.Permissoes;
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
		VendaResponse response = comVisibilidade(mapper.toResponse(service.criar(request)));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<VendaResponse>> findAll(@RequestParam(required = false) StatusVenda status) {
		List<VendaResponse> out = (status == null ? service.findAll() : service.findByStatus(status)).stream()
				.map(mapper::toResponse).map(this::comVisibilidade).toList();
		return ResponseEntity.ok(out);
	}

	@GetMapping("/{id}")
	public ResponseEntity<VendaResponse> findById(@PathVariable UUID id) {
		return ResponseEntity.ok(comVisibilidade(mapper.toResponse(service.findById(id))));
	}

	@PutMapping("/{id}")
	public ResponseEntity<VendaResponse> editar(@PathVariable UUID id, @Valid @RequestBody VendaCreateRequest request) {
		return ResponseEntity.ok(comVisibilidade(mapper.toResponse(service.editar(id, request))));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluir(@PathVariable UUID id) {
		service.excluir(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{id}/ordem-servico")
	public ResponseEntity<VendaResponse> converterParaOrdemServico(@PathVariable UUID id) {
		return ResponseEntity.ok(comVisibilidade(mapper.toResponse(service.converterParaOrdemServico(id))));
	}

	@PostMapping("/{id}/cancelar")
	public ResponseEntity<VendaResponse> cancelar(@PathVariable UUID id) {
		return ResponseEntity.ok(comVisibilidade(mapper.toResponse(service.cancelar(id))));
	}

	@PostMapping("/{id}/recalcular")
	public ResponseEntity<VendaResponse> recalcular(@PathVariable UUID id) {
		return ResponseEntity.ok(comVisibilidade(mapper.toResponse(service.recalcular(id))));
	}

	/**
	 * Visão em dois níveis: sem a permissão 'custos', a API OMITE custo, margem e
	 * o detalhamento de matérias/serviços (informação sensível). O preço final e o
	 * sugerido são visíveis a todos os operadores de vendas.
	 */
	private VendaResponse comVisibilidade(VendaResponse response) {
		if (!temPermissaoCustos()) {
			response.getItens().forEach(item -> {
				item.setCustoTotal(null);
				item.setMarkupAplicado(null);
				item.setDetalhes(List.of());
			});
		}
		return response;
	}

	private boolean temPermissaoCustos() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("ROLE_" + Permissoes.CUSTOS));
	}
}
