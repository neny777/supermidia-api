package br.com.supermidia.configuracao.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.supermidia.configuracao.api.dto.ConfiguracaoDTO;
import br.com.supermidia.configuracao.api.dto.VendaPadroesDTO;
import br.com.supermidia.configuracao.app.ConfiguracaoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/configuracoes")
public class ConfiguracaoController {

	private final ConfiguracaoService service;

	public ConfiguracaoController(ConfiguracaoService service) {
		this.service = service;
	}

	@GetMapping
	public ResponseEntity<ConfiguracaoDTO> obter() {
		return ResponseEntity.ok(ConfiguracaoDTO.de(service.obter()));
	}

	/** Padrões do formulário da venda — acessível ao papel 'vendas'. */
	@GetMapping("/venda-padroes")
	public ResponseEntity<VendaPadroesDTO> vendaPadroes() {
		return ResponseEntity.ok(VendaPadroesDTO.de(service.obter()));
	}

	@PutMapping
	public ResponseEntity<ConfiguracaoDTO> atualizar(@Valid @RequestBody ConfiguracaoDTO dto) {
		return ResponseEntity.ok(ConfiguracaoDTO.de(service.atualizar(dto)));
	}
}
