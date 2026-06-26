package br.com.supermidia.produto.api;

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

import br.com.supermidia.produto.api.dto.ProdutoCreateRequest;
import br.com.supermidia.produto.api.dto.ProdutoCalculoRequest;
import br.com.supermidia.produto.api.dto.ProdutoCalculoResponse;
import br.com.supermidia.produto.api.dto.ProdutoResponse;
import br.com.supermidia.produto.api.dto.ProdutoUpdateRequest;
import br.com.supermidia.produto.app.ProdutoCalculoService;
import br.com.supermidia.produto.app.ProdutoService;
import br.com.supermidia.produto.domain.Produto;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

	private final ProdutoService service;
	private final ProdutoCalculoService calculoService;
	private final ProdutoMapper mapper;

	public ProdutoController(ProdutoService service, ProdutoCalculoService calculoService, ProdutoMapper mapper) {
		this.service = service;
		this.calculoService = calculoService;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<ProdutoResponse> create(@Valid @RequestBody ProdutoCreateRequest request) {
		Produto entity = mapper.toEntity(request);
		entity.setMateriasCalculo(mapper.toMateriaCalculoEntities(request.getMateriasCalculo()));
		entity.setServicosCalculo(mapper.toServicoCalculoEntities(request.getServicosCalculo()));
		aplicarParametros(request, entity);
		Produto saved = service.create(entity);
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(saved));
	}

	@GetMapping
	public ResponseEntity<List<ProdutoResponse>> findAll() {
		List<ProdutoResponse> out = service.findAll().stream().map(mapper::toResponse).toList();
		return ResponseEntity.ok(out);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProdutoResponse> findById(@PathVariable UUID id) {
		return ResponseEntity.ok(mapper.toResponse(service.findById(id)));
	}

	@PostMapping("/{id}/calcular")
	public ResponseEntity<ProdutoCalculoResponse> calcular(@PathVariable UUID id,
			@Valid @RequestBody ProdutoCalculoRequest request) {
		return ResponseEntity.ok(calculoService.calcular(id, request));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProdutoResponse> update(@PathVariable UUID id, @Valid @RequestBody ProdutoUpdateRequest request) {
		Produto updated = service.update(id, entity -> {
			mapper.updateEntity(request, entity);
			entity.setMateriasCalculo(mapper.toMateriaCalculoEntities(request.getMateriasCalculo()));
			entity.setServicosCalculo(mapper.toServicoCalculoEntities(request.getServicosCalculo()));
			aplicarParametros(request, entity);
		});
		return ResponseEntity.ok(mapper.toResponse(updated));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	private void aplicarParametros(ProdutoCreateRequest request, Produto entity) {
		for (int i = 0; i < entity.getMateriasCalculo().size(); i++) {
			entity.getMateriasCalculo().get(i)
					.setParametros(mapper.toMateriaParametroEntities(request.getMateriasCalculo().get(i).getParametros()));
		}
		for (int i = 0; i < entity.getServicosCalculo().size(); i++) {
			entity.getServicosCalculo().get(i)
					.setParametros(mapper.toServicoParametroEntities(request.getServicosCalculo().get(i).getParametros()));
		}
	}

	private void aplicarParametros(ProdutoUpdateRequest request, Produto entity) {
		for (int i = 0; i < entity.getMateriasCalculo().size(); i++) {
			entity.getMateriasCalculo().get(i)
					.setParametros(mapper.toMateriaParametroEntities(request.getMateriasCalculo().get(i).getParametros()));
		}
		for (int i = 0; i < entity.getServicosCalculo().size(); i++) {
			entity.getServicosCalculo().get(i)
					.setParametros(mapper.toServicoParametroEntities(request.getServicosCalculo().get(i).getParametros()));
		}
	}
}
