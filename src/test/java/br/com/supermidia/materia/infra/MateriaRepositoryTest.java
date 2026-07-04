package br.com.supermidia.materia.infra;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import br.com.supermidia.materia.domain.Materia;
import br.com.supermidia.materia.domain.UnidadeMateria;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class MateriaRepositoryTest {

	@Autowired
	private MateriaRepository materiaRepository;

	@Test
	void findGruposDeveListarDistintosSemNulosEmOrdem() {
		materiaRepository.saveAll(List.of(
				materia("LONA BRILHO", "LONAS"),
				materia("LONA FOSCA", "LONAS"),
				materia("ADESIVO VINIL", "ADESIVOS"),
				materia("GRAMPO", null)));

		assertThat(materiaRepository.findGrupos()).containsExactly("ADESIVOS", "LONAS");
	}

	@Test
	void findByGrupoDeveTrazerSoAsMateriasDoGrupo() {
		materiaRepository.saveAll(List.of(
				materia("LONA BRILHO", "LONAS"),
				materia("ADESIVO VINIL", "ADESIVOS")));

		assertThat(materiaRepository.findByGrupoOrderByNome("LONAS"))
				.extracting(Materia::getNome).containsExactly("LONA BRILHO");
	}

	private Materia materia(String nome, String grupo) {
		Materia materia = new Materia();
		materia.setNome(nome);
		materia.setGrupo(grupo);
		materia.setUnidade(UnidadeMateria.M2);
		materia.setPreco(new BigDecimal("8.50"));
		return materia;
	}
}
