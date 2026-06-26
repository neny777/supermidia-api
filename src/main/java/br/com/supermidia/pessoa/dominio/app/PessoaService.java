package br.com.supermidia.pessoa.dominio.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.supermidia.pessoa.cliente.infra.ClienteRepository;
import br.com.supermidia.pessoa.colaborador.infra.ColaboradorRepository;
import br.com.supermidia.pessoa.dominio.domain.Pessoa;
import br.com.supermidia.pessoa.dominio.infra.PessoaRepository;
import br.com.supermidia.pessoa.fornecedor.infra.FornecedorRepository;
import br.com.supermidia.pessoa.parceiro.infra.ParceiroRepository;

@Service
public class PessoaService {
	private final PessoaRepository pessoaRepository;
	private final ClienteRepository clienteRepository;
	private final FornecedorRepository fornecedorRepository;
	private final ParceiroRepository parceiroRepository;
	private final ColaboradorRepository colaboradorRepository;

	public PessoaService(PessoaRepository pessoaRepository, ClienteRepository clienteRepository,
			FornecedorRepository fornecedorRepository, ParceiroRepository parceiroRepository,
			ColaboradorRepository colaboradorRepository) {
		this.pessoaRepository = pessoaRepository;
		this.clienteRepository = clienteRepository;
		this.fornecedorRepository = fornecedorRepository;
		this.parceiroRepository = parceiroRepository;
		this.colaboradorRepository = colaboradorRepository;
	}

	public List<Map<String, Object>> findPessoasByNome(String nome) {
		List<Pessoa> pessoas = pessoaRepository.findByNomeContainingIgnoreCase(nome);

		return pessoas.stream().map(pessoa -> {
			Map<String, Object> pessoaMap = new HashMap<>();
			pessoaMap.put("id", pessoa.getId());
			pessoaMap.put("nome", pessoa.getNome());
			return pessoaMap;
		}).collect(Collectors.toList());
	}
	
    public boolean clienteTemOutroPapel(UUID id) {
        return isColaborador(id) || isFornecedor(id) || isParceiro(id);
    }
    
	public boolean fornecedorTemOutroPapel(UUID id) {		
		return isCliente(id) || isColaborador(id) || isParceiro(id);
	}
	
	public boolean parceiroTemOutroPapel(UUID id) {		
		return isCliente(id) || isColaborador(id) || isFornecedor(id);
	}
    
    public boolean colaboradorTemOutroPapel(UUID id) {
        return isCliente(id) || isFornecedor(id) || isParceiro(id);
    }

    public boolean isCliente(UUID id) {
        return clienteRepository.existsByPessoaId(id);
    }
    
    public boolean isFornecedor(UUID id) {
        return fornecedorRepository.existsByPessoaId(id);
    }
    
    public boolean isParceiro(UUID id) {
        return parceiroRepository.existsByPessoaId(id);
    }

    public boolean isColaborador(UUID id) {
        return colaboradorRepository.existsById(id);
    }
}
