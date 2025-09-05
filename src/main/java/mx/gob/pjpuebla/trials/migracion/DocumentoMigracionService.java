package mx.gob.pjpuebla.trials.migracion;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

// ACL mappers:
import mx.gob.pjpuebla.migracion.acl.mapper.RubrosMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.SentenciaMapper;
import mx.gob.pjpuebla.migracion.acl.mapper.ResolucionMapper;

// Legacy models:
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracion; 
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallesProm;
import mx.gob.pjpuebla.migracion.readers.ocomun.Ocomun;
import mx.gob.pjpuebla.migracion.readers.acuerdos.AcuerdosMigracionSaveRecord;     
import mx.gob.pjpuebla.migracion.readers.acuerdos.SentenciaMigracionSaveRecord;    
import mx.gob.pjpuebla.migracion.readers.detallesProm.DetallePromSaveRecord;

// Core:
import mx.gob.pjpuebla.trials.core.conceptos.Concepto;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.TipoPromocion;
import mx.gob.pjpuebla.trials.util.enums.TipoSentencia;
import mx.gob.pjpuebla.trials.util.enums.TipoResolucion;
import mx.gob.pjpuebla.trials.workflow.anexos.Anexo;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoService;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.core.instituciones.Institucion;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;

@Service
@RequiredArgsConstructor
public class DocumentoMigracionService {

    private final DocumentoRepository documentoRepository;
    private final AnexoRepository anexoRepository;
    private final DocumentoService documentoService;

    private final SentenciaMapper sentenciaMapper;     // ACL
    private final ResolucionMapper resolucionMapper;   // ACL

    /* ------------------------------------------------------------------------------------------------
     *  Acuerdos
     * ----------------------------------------------------------------------------------------------*/
    @Transactional
    public List<Documento> createAcuerdosFromLegacy(List<AcuerdosMigracion> acuerdos,
                                                    Carpeta carpeta,
                                                    RubrosMapper rubrosMapper) {
        if (acuerdos == null || acuerdos.isEmpty()) return List.of();

        List<Documento> documentos = new ArrayList<>();
        for (AcuerdosMigracion a : acuerdos) {
            List<String> rubros = rubrosMapper.mapRubros(a.getResumen());
            String rubroPrincipal = rubros.isEmpty() ? "" : rubros.get(0);

            AcuerdosMigracionSaveRecord data = new AcuerdosMigracionSaveRecord(
                    carpeta,
                    rubroPrincipal,
                    a.getFechaResolucion(),
                    rubros,
                    a.getClave().toString(),
                    a.getRuta(),
                    a.getFecha()
            );

            Documento created = documentoService.createAcuerdoMigracion(data);
            documentos.add(created);
        }
        return documentos;
    }

    /* ------------------------------------------------------------------------------------------------
     *  Sentencias
     * ----------------------------------------------------------------------------------------------*/
    @Transactional
    public List<Documento> createSentenciasFromLegacy(List<AcuerdosMigracion> sentencias,
                                                      Carpeta carpeta) {
        if (sentencias == null || sentencias.isEmpty()) return List.of();

        List<Documento> documentos = new ArrayList<>();
        for (AcuerdosMigracion s : sentencias) {
            // Mapear con ACL (no hardcodear enums)
            TipoSentencia tipoSent = sentenciaMapper.mapTipoSentencia(s.getResumen());
            TipoResolucion tipoRes = resolucionMapper.mapTipoResolucionSentencia(s.getSentencia());

            SentenciaMigracionSaveRecord data = new SentenciaMigracionSaveRecord(
                    carpeta,
                    s.getFechaResolucion(),
                    tipoSent,
                    tipoRes,
                    s.getClave().toString(),
                    s.getRuta(),
                    s.getFecha()
            );

            Documento created = documentoService.createSentenciaMigracion(data);
            documentos.add(created);
        }
        return documentos;
    }

    /* ------------------------------------------------------------------------------------------------
     *  Promociones (por si lo necesitas aquí también)
     * ----------------------------------------------------------------------------------------------*/
    @Transactional
    public List<Documento> createPromocionesFromLegacy(List<DetallesProm> promociones,
                                                       Carpeta carpeta,
                                                       TipoPromocion defaultTipo) {
        if (promociones == null || promociones.isEmpty()) return List.of();

        List<Documento> documentos = new ArrayList<>();
        for (DetallesProm p : promociones) {
            DetallePromSaveRecord save = new DetallePromSaveRecord(
                    carpeta,
                    defaultTipo,                 // o mapea con tu PromocionMapper en el usecase
                    p.getId().toString(),
                    p.getArchivo(),
                    p.getAcuerdo()
            );
            Documento doc = documentoService.createPromocionMigracion(save);
            createAnexosDePromocion(p.getAnexos(), doc);
            documentos.add(doc);
        }
        return documentos;
    }

    /* ------------------------------------------------------------------------------------------------
     *  Demanda inicial y Anexos (sin cambios, te los dejo por completitud)
     * ----------------------------------------------------------------------------------------------*/
    @Transactional
    public Documento createDemandaInicial(Ocomun ocomun,
                                          Carpeta carpeta,
                                          Concepto concepto) {
        DocumentoData data = new DocumentoData(); // ajusta si necesitas payload
        return createDocumento(null, carpeta, data,
                ocomun != null ? ocomun.getRutaDigitalizacion() : "",
                null, concepto, null, null);
    }

    @Transactional
    public Documento createDocumento(TipoDocumento tipoDocumento,
                                     Carpeta carpeta,
                                     DocumentoData data,
                                     String ruta,
                                     String folio,
                                     Concepto concepto,
                                     Institucion institucion,
                                     Documento documentoRelacionado) {
        Documento doc = new Documento()
                .setVersion(0)
                .setTipoDocumento(tipoDocumento)
                .setData(data)
                .setRuta(ruta)
                .setCarpeta(carpeta)
                .setPersona(null)
                .setFechaAsignacion(java.time.LocalDateTime.now())
                .setEstatus(EstadoCarpeta.MIGRADO)
                .setFolio(folio)
                .setConcepto(concepto)
                .setInstitucion(institucion)
                .setAcuerdoRespuesta(documentoRelacionado);

        return documentoRepository.save(doc);
    }

    @Transactional
    public List<Anexo> createAnexos(String anexos, Documento documento) {
        if (documento == null) throw new IllegalArgumentException("Documento obligatorio.");
        if (anexos == null || anexos.isBlank()) return List.of();

        List<Anexo> toSave = Pattern.compile("\\s*,\\s*")
                .splitAsStream(anexos)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .map(nombre -> new Anexo().setNombre(nombre).setDocumento(documento))
                .toList();

        return toSave.isEmpty() ? List.of() : anexoRepository.saveAll(toSave);
    }

    @Transactional
    public List<Anexo> createAnexosDePromocion(String anexos, Documento documento) {
        if (anexos == null || anexos.isBlank() || "Sin Anexos".equalsIgnoreCase(anexos)) {
            return List.of();
        }
        List<Anexo> toSave = Pattern.compile("\\s*,\\s*")
                .splitAsStream(anexos)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .map(nombre -> new Anexo().setNombre(nombre).setDocumento(documento))
                .toList();

        return toSave.isEmpty() ? List.of() : anexoRepository.saveAll(toSave);
    }
}
