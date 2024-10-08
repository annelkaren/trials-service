package mx.gob.pjpuebla.trials.workflow.carpeta;

import mx.gob.pjpuebla.trials.core.distritos.Distrito;
import mx.gob.pjpuebla.trials.core.distritos.DistritoRepository;
import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.domicilio.DomicilioSetUp;
import mx.gob.pjpuebla.trials.core.domicilios.Domicilio;
import mx.gob.pjpuebla.trials.core.domicilios.DomicilioRepository;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaRepository;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.sedes.Sede;
import mx.gob.pjpuebla.trials.core.sedes.SedeRepository;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartes;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesRepository;
import mx.gob.pjpuebla.trials.core.tipopartes.TipoPartesSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaRepository;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.workflow.anexos.AnexoBandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.ApelacionRecordResponse;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.BandejaRecepcionRecord;
import mx.gob.pjpuebla.trials.workflow.carpeta.records.CarpetaResponseRecord;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
import mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFolios;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRecord;
import mx.gob.pjpuebla.trials.workflow.personasdocumentos.PersonaDocumentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static mx.gob.pjpuebla.trials.workflow.folios.JuzgadoFoliosSetUp.createJuzgadoFolios;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CarpetaServiceTest {

    @Mock
    CarpetaRepository carpetaRepository;
    @Mock
    PersonaDocumentoRepository personaDocumentoRepository;
    @InjectMocks
    CarpetaService target;
    @Mock
    TipoPartesRepository tipoPartesRepository;
    @Mock
    private TipoSistemaRepository tipoSistemaRepository;
    @Mock
    private MateriaRepository materiaRepository;
    @Mock
    private DistritoRepository distritoRepository;
    @Mock
    private DomicilioRepository domicilioRepository;
    @Mock
    private SedeRepository sedeRepository;

    private Carpeta validCarpeta;
    private PersonaDocumentoRecord actor;
    private PersonaDocumentoRecord demandado;
    private ApelacionRecordResponse apelacionRecordResponse;
    private TipoJuicio tipoJuicio;
    private Juzgado juzgado;
    private JuzgadoFolios juzgadoFolios;
    private TipoPartes actorApelacion;
    private TipoPartes demandadoApelacion;


    @BeforeEach
    public void setUp() {
        validCarpeta = CarpetaSetUp.create(TipoJuicioSetUp.createTipoJuicio(), JuzgadoSetUp.createJuzgado());
        actor = new PersonaDocumentoRecord("Juan", "Perez", "", null, "fisica", "Actor", "", "2461740005", "a@a.com", "Actor", 1, validCarpeta.getId());
        demandado = new PersonaDocumentoRecord("Nauj", "Zerep", "", null, "fisica", "Demandado", "", "2461740005", "a@d.com", "Demandado", 2, validCarpeta.getId());
        apelacionRecordResponse = CarpetaSetUp.apelacionRecordResponse();
        actorApelacion = TipoPartesSetUp.createTipoPartes().setTipoJuicio(tipoJuicio);
        tipoPartesRepository.save(actorApelacion);
        demandadoApelacion = TipoPartesSetUp.createTipoPartes().setTipoJuicio(tipoJuicio).setNombre("Demandado");
        tipoPartesRepository.save(demandadoApelacion);

        Materia materia = materiaRepository.save(MateriaSetUp.createMateria());
        Distrito distrito = distritoRepository.save(DistritoSetUp.createDistrito());
        Domicilio domicilio = domicilioRepository.save(DomicilioSetUp.createDomicilio());

        Sede sede = SedeSetUp.createSede();
        sede.setDistrito(distrito);
        sede.setDomicilio(domicilio);
        sede = sedeRepository.save(sede);

        juzgado = JuzgadoSetUp.createJuzgado(materia, sede);
        juzgado.setContadorAsignaciones(0);
        juzgado.setMaxAsignacionesRonda(0);

        juzgadoFolios = createJuzgadoFolios();
        juzgadoFolios.setJuzgado(juzgado);
        TipoSistema tipoSistema = tipoSistemaRepository.save(TipoSistemaSetUp.createTipoSistema());
        tipoJuicio = TipoJuicioSetUp.createTipoJuicio(tipoSistema, materia);
    }

    @Test
    void getCarpetaResponseByNumExpYearJuzgado_return_CarpetaResponseRecord() {
        given(carpetaRepository.findByExpedienteAndJuzgadoId(any(), any()))
                .willReturn(Optional.ofNullable(validCarpeta));
        given(personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(any(), eq("Actor"), any()))
                .willReturn(actor);
        given(personaDocumentoRepository.findPersonaAndTipoParteByCarpetaId(any(), eq("Demandado"), any()))
                .willReturn(demandado);

        CarpetaResponseRecord carpetaResponseRecord = target.getCarpetaResponseByNumExpYearJuzgado(any(), any());

        assertThat(carpetaResponseRecord)
                .isOfAnyClassIn(CarpetaResponseRecord.class)
                .hasFieldOrPropertyWithValue("idCarpeta", validCarpeta.getId())
                .hasFieldOrPropertyWithValue("actor", actor.nombre() + " " + actor.apellidoPaterno())
                .hasFieldOrPropertyWithValue("demandado", demandado.nombre() + " " + demandado.apellidoPaterno());
    }

    @Test
    void getCarpetaResponseByNumExpYearJuzgado_return_not_found() {
        NotFoundException assertThrows = assertThrows(
                NotFoundException.class,
                () -> {
                    target.getCarpetaResponseByNumExpYearJuzgado("1", 1);
                }
        );
        assertThat(assertThrows.getMessage()).contains("Carpeta no encontrada");
    }

    @Test
    void getPersonaDocumentoById_return_carpetaId() {
        List<ApelacionRecordResponse> expectedResponses = Collections.singletonList(apelacionRecordResponse);

        given(personaDocumentoRepository.findPersonaDocumentoByCarpetaId(validCarpeta.getId()))
                .willReturn(expectedResponses);

        List<ApelacionRecordResponse> result = target.getPersonasDocumentoByCarpetaId(validCarpeta.getId());
        assertThat(result).isNotEmpty();
        ApelacionRecordResponse actualResponse = result.get(0);
        assertThat(actualResponse)
                .usingRecursiveComparison()
                .isEqualTo(apelacionRecordResponse);
    }

    @Test
    void getBandejaRecepcionByFolio_ReturnsBandejaRecepcion() {
        String folio = "1"; 

        BandejaRecepcionRecord bandejaRecepcion = DocumentoSetUp.createBandejaRecepcion();
        List<AnexoBandejaRecepcionRecord> anexosRecepcion = DocumentoSetUp.createAnexosDocumento();

        given(carpetaRepository.findByFolioAndJuzgado_Name(folio, "Oficialía Común de Partes"))
                .willReturn(bandejaRecepcion);
        given(carpetaRepository.findAnexosByDocumentoId(bandejaRecepcion.documentoId()))
            .willReturn(anexosRecepcion);
    
        BandejaRecepcionRecord result = target.getBandejaRecepcionByFolio(folio); 
    
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(bandejaRecepcion);
    }

    @Test
    void getBandejaRecepcionByFolio_ThrowsNotFoundException() {

        String folio = "999"; 
        given(carpetaRepository.findByFolioAndJuzgado_Name(folio, "Oficialía Común de Partes"))
            .willReturn(null); 
        

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                target.getBandejaRecepcionByFolio(folio); 
        });

        assertThat(exception.getMessage()).contains("No se encontró la carpeta con el folio: " + folio);
    }
}