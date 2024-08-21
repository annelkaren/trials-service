package mx.gob.pjpuebla.trials.core.listavalor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListaValorRepository extends JpaRepository<ListaValor, Integer> {

}
