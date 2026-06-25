package mx.gob.pjpuebla.trials.core.materias;

import mx.gob.pjpuebla.trials.util.enums.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Integer> {

    Optional<Materia> findByIdAndEstado(Integer integer, Estado estado);

    List<Materia> findByNombreNotInOrderByNombre(List<String> excepcion);

    @Query("""
            SELECT new mx.gob.pjpuebla.trials.core.materias.SentenciasByMateriaRecord(
            ma.nombre,
            (SELECT COUNT(dd)
            FROM DocumentoDetalle dd
            JOIN dd.documento doc
            JOIN doc.carpeta ca
            JOIN ca.juzgado juz
            JOIN ca.tipoJuicio tj
            JOIN tj.materia mat
            WHERE doc.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.SENTENCIA
            AND mat.id = ma.id
            AND juz.instanciaJuzgado = mx.gob.pjpuebla.trials.util.enums.InstanciaJuzgado.PRIMERA_INSTANCIA)
            ,
            (SELECT COUNT(dd)
            FROM DocumentoDetalle dd
            JOIN dd.documento doc
            JOIN doc.carpeta ca
            JOIN ca.juzgado juz
            JOIN ca.tipoJuicio tj
            JOIN tj.materia mat
            WHERE doc.tipoDocumento = mx.gob.pjpuebla.trials.util.enums.TipoDocumento.SENTENCIA
            AND mat.id = ma.id
            AND juz.instanciaJuzgado = mx.gob.pjpuebla.trials.util.enums.InstanciaJuzgado.SEGUNDA_INSTANCIA))
            FROM Materia ma
            WHERE ma.nombre <> 'EXHORTO'
            ORDER BY ma.nombre
            """)
    List<SentenciasByMateriaRecord> getCountSentenciasByMateria();

    Optional<Materia> findByNombre(String nombre);

    @Query("""
        SELECT new mx.gob.pjpuebla.trials.core.materias.MateriaRecord(
            m.id,
            m.nombre
        )
        FROM Materia m
        WHERE m.estado = mx.gob.pjpuebla.trials.util.enums.Estado.ACTIVE
        ORDER BY m.nombre ASC
    """)
    List<MateriaRecord> findAllForSelect();
}
