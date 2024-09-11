package mx.gob.pjpuebla.trials.core.documentos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento,Integer> {

    @Query(value = """ 
                select tj.* from tbl_juzgados tj 
                join tbl_materias tm on tm.pn_id = tj.fn_materia 
                where exists (select * from tbl_tipo_juicio ttj where ttj.pn_id=?11 and tm.pn_id=ttj.fn_materia)
                and exists 
                    (select * from tbl_documentos  td
                    where td.fn_juzgado = tj.pn_id
                        and exists 
                            (select * from tbl_personas_documentos tpd 
                            where tpd.fn_documento = td.pn_id and
                            concat(tpd.s_nombres,' ', tpd.s_apellido_paterno,' ',tpd.s_apellido_materno,' ',tpd.s_pseudonimo) = 
                            concat(?1, ' ', ?2, ' ', ?3, ' ', ?4 ) and tpd.fn_tipo_parte=?5)
                        and exists
                            (select * from tbl_personas_documentos tpd 
                            where tpd.fn_documento = td.pn_id and
                            concat(tpd.s_nombres,' ', tpd.s_apellido_paterno,' ',tpd.s_apellido_materno,' ',tpd.s_pseudonimo) = 
                            concat(?6, ' ', ?7, ' ', ?8, ' ', ?9 ) and tpd.fn_tipo_parte=?10)) """,
            nativeQuery=true)
    public Juzgado findConexidad(String nombreParte1, String aPaternoParte1, String aMaternoParte1, String pseudonimoParte1, Integer tipoParte1,
                                String nombreParte2, String aPaternoParte2, String aMaternoParte2, String pseudonimoParte2, Integer tipoParte2, Integer tipoJuicioId);
}
