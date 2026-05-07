package mx.gob.pjpuebla.trials.core.oficialias.records;

import com.fasterxml.jackson.annotation.JsonInclude;

import mx.gob.pjpuebla.trials.core.sedes.records.SedeRecordResponse;
import mx.gob.pjpuebla.trials.core.tipooficialias.TipoOficialiaRecord;
import mx.gob.pjpuebla.trials.core.materias.MateriaRecord;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaCatalogoRecord;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OficialiaRecord(
                Integer id,
                Integer version,
                String nombre,
                String responsable,
                Estado estado,
                TipoOficialiaRecord tipo,
                SedeRecordResponse sede,
                List<CarpetaCatalogoRecord> tipoDocumentos,
                List<MateriaRecord> materias) implements Serializable {

        public OficialiaRecord(Integer id, Integer version, String nombre, String responsable, Estado estado,
                        TipoOficialiaRecord tipo, SedeRecordResponse sede, String tipoDocumentos) {
                this(
                                id,
                                version,
                                nombre,
                                responsable,
                                estado,
                                tipo,
                                sede,
                                tipoDocumentos != null && !tipoDocumentos.isBlank()
                                                ? Arrays.stream(tipoDocumentos.split(","))
                                                                .map(String::trim)
                                                                .map(clave -> {
                                                                        if (clave.equals("PROMOCION")) {
                                                                                TipoDocumento tipoDocumento = TipoDocumento
                                                                                                .valueOf(clave);
                                                                                return new CarpetaCatalogoRecord(clave,
                                                                                                tipoDocumento.getEtiqueta());
                                                                        } else {
                                                                                TipoCarpeta tipoCarpeta = TipoCarpeta
                                                                                                .valueOf(clave);
                                                                                return new CarpetaCatalogoRecord(clave,
                                                                                                tipoCarpeta.getEtiqueta());
                                                                        }
                                                                })
                                                                .collect(Collectors.toList())
                                                : null,
                                null);
        }
}
