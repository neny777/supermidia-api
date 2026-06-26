package br.com.supermidia.pessoa.dominio.api;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.supermidia.pessoa.dominio.app.PessoaService;

@RestController
@RequestMapping("/api/pessoas")
public class PessoaController {

    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @GetMapping("/nome")
    public ResponseEntity<List<Map<String, Object>>> findPessoasByNome(@RequestParam String nome) {    	
        List<Map<String, Object>> pessoas = pessoaService.findPessoasByNome(nome);
        return ResponseEntity.ok(pessoas);
    }
}
