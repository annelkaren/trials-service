package mx.gob.pjpuebla.trials.workflow.listaestrados;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ListaEstradoRepository extends JpaRepository<ListaEstrado, Integer> {

    @Query("""
    SELECT l FROM ListaEstrado l
    WHERE (
        :searchTerm IS NULL OR
        lower(TRANSLATE(l.usuarioAlta, 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN')) LIKE LOWER(TRANSLATE(CONCAT('%', :searchTerm, '%'), 'áéíóúüñÁÉÍÓÚÜÑ', 'aeiounAEIOUN'))
    )
    AND (
        CASE
            WHEN :listaestradoId IS NOT NULL AND l.id = :listaestradoId THEN true
            ELSE true
        END
    )
""")
    Page<ListaEstrado> findAllListaEstradoIdAndSearch(
            @Param("searchTerm") String searchTerm,
            @Param("listaestradoId") Integer listaestradoId,
            Pageable pageable);
}
