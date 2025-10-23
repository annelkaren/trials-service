package mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracionReader;
import mx.gob.pjpuebla.migracion.readers.entradas.EntradasMigracionRecord;
import mx.gob.pjpuebla.migracion.usecases.MigracionExpedienteResult;
import mx.gob.pjpuebla.migracion.usecases.MigrarExpedienteUseCase;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoService;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.ApiResponse;
import mx.gob.pjpuebla.trials.error.ApiResponseFactory;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.EstadoCarpeta;
import mx.gob.pjpuebla.trials.util.enums.PromocionSinExpedienteEnum;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.util.enums.TipoPromocion;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoService;
import mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records.PromocionSinExpedienteFiltrosRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records.PromocionSinExpedientePageRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records.PromocionSinExpedienteRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.promocionesSinExpediente.records.PromocionSinExpedienteSaveRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoData;
import mx.gob.pjpuebla.trials.workflow.documentos.records.DocumentoPromocionResponseRecord;
import mx.gob.pjpuebla.trials.workflow.movimientos.MovimientoService;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class PromocionSinExpedienteService {

    private final DocumentoService documentoService;
    private final JuzgadoService juzgadoService;
    private final PromocionSinExpedienteRepository promocionSinExpedienteRepository;
    private final EntradasMigracionReader entradasMigracionReader;
    private final MigrarExpedienteUseCase migrarExpedienteUseCase;
    private final MovimientoService movimientoService;
    private final PersonaService personaService;

    @Transactional
    public Page<PromocionSinExpedientePageRecord> getAll(
            PromocionSinExpedienteFiltrosRecord filtros,
            Pageable pageable) {
        return promocionSinExpedienteRepository.getAll(pageable);
    }

    @Transactional
    public ApiResponse<PromocionSinExpedienteSaveRecord> save(PromocionSinExpedienteRecord promocion) {

        Juzgado juzgado = juzgadoService.findJuzgadoById(promocion.juzgadoId());
        PromocionSinExpediente promocionSinExp = new PromocionSinExpediente()
                .setFolio(documentoService.getFolio("P"))
                .setExpediente(promocion.expediente() + "/" + promocion.year())
                .setJuzgado(juzgado)
                .setTipoJuicio(null)
                .setAnexos(String.join(", ", promocion.anexos()))
                .setEstado(PromocionSinExpedienteEnum.REGISTRADO)
                .setTipoRegistro(null)
                .setTipoPromocion(promocion.tipoPromocion());

        promocionSinExp = promocionSinExpedienteRepository.save(promocionSinExp);
        PromocionSinExpedienteSaveRecord saveRecord = new PromocionSinExpedienteSaveRecord(
                promocionSinExp.getId(),
                "La promoción ha sido registrada exitosamente");
        return ApiResponseFactory.success("La promoción ha sido registrada exitosamente", saveRecord);
    }

    public PromocionSinExpediente findById(Integer id) {
        return promocionSinExpedienteRepository.findById(id).orElse(null);
    }

    public DocumentoPromocionResponseRecord asociarExpediente(Integer idPromocion) {
        Persona persona = personaService.getAuditor();

        // Paso 1 - recuperamos registro de promocion sin expediente y verificamos el
        // estatus
        PromocionSinExpediente promocion = promocionSinExpedienteRepository.findById(idPromocion)
                .orElseThrow(
                        () -> new NotFoundException("No fue posible encontrar el registro de promoción sin expediente",
                                idPromocion.toString()));

        if (promocion.getEstado().equals(PromocionSinExpedienteEnum.EXPEDIENTE_MIGRADO)) {
            throw new IllegalStateException("La promoción ya cuenta con un expediente asociado.");
        }

        // Paso 2 buscamos el expediente en la base de datos del SECGJ PHP:
        String expediente = promocion.getExpediente().split("/")[0];
        Integer year = Integer.parseInt(promocion.getExpediente().split("/")[1]); // promocion.getExpediente().split("/")[1];
        Juzgado juzgado = promocion.getJuzgado();

        try {
            EntradasMigracionRecord entrada = entradasMigracionReader.buscarPorFiltros(
                    expediente, year, juzgado.getClaveJuzgado());

            if (entrada != null) {
                // SI se encuentra el expediente en el SECGJ PHP, lo migramos
                MigracionExpedienteResult expedienteMigrado = migrarExpedienteUseCase.migrarExpediente(expediente, year,
                        expediente);

                // Una vez que ya se ha creado el expediente debemos de crear un movimiento de
                // captura para que le aparezca el registro al capturista / digitalizador.
                movimientoService.createMovimento(expedienteMigrado.carpeta(), null, persona, null,
                        EstadoCarpeta.CAPTURA.name());

                // Actualizamos estatus de la promoción sin expediente
                promocion.setEstado(PromocionSinExpedienteEnum.EXPEDIENTE_MIGRADO);
                promocion.setTipoJuicio(expedienteMigrado.carpeta().getTipoJuicio());
                promocionSinExpedienteRepository.save(promocion);

                // Ahora una vez que el expediente principal esta agregado creamos el registro
                // de la promoción asociada al expediente principal:
                List<String> anexos = List.of(promocion.getAnexos().split(", "));
                return registraPromocion(promocion.getTipoPromocion(), expedienteMigrado.carpeta(), promocion.getFolio(), persona, anexos);

            }

        } catch (Exception e) {
            // Si no se encuentra el expediente en el SECGJ PHP, creamos el registro como 'expediente antiguo'
       
            //Creamos el expediente antiguo
            log.info("No se encontro en SECGJ PHP - INICIO DE REGISTRO POR METODO EXPEDIENTE ANTIGUO");

        }

        return null;
    }

    private DocumentoPromocionResponseRecord registraPromocion(TipoPromocion tipoPromocion, Carpeta carpeta, String folio, Persona persona, List<String> anexos) {
        DocumentoData docData = new DocumentoData().setTipoPromocion(tipoPromocion);

        Documento documento = new Documento()
                .setCarpeta(carpeta)
                .setTipoDocumento(TipoDocumento.PROMOCION)
                .setData(docData)
                .setFolio(folio)
                .setEstatus(EstadoCarpeta.CAPTURA)
                .setPersona(persona);

        documento = documentoService.save(documento);

        // Agregamos los anexos
        documentoService.addAnexos(anexos, documento);

        // Agregamos movimiento del documento para que aparezca en la bandeja de captura
        movimientoService.createMovimentoWithConcepto(null, documento, documento.getPersona(), null,
                documento.getEstatus().name(), documento.getConcepto());

        return new DocumentoPromocionResponseRecord(documento.getId(), documento.getFolio(), documento.getTipoDocumento());
    }
}
