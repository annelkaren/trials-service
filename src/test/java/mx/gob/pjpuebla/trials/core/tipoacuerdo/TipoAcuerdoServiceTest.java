package mx.gob.pjpuebla.trials.core.tipoacuerdo;


import mx.gob.pjpuebla.trials.core.materias.Materia;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicio;
import mx.gob.pjpuebla.trials.core.tipojuicio.TipoJuicioSetUp;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistema;
import mx.gob.pjpuebla.trials.core.tiposistema.TipoSistemaSetUp;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaSetUp;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoSetUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import mx.gob.pjpuebla.trials.error.NotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoAcuerdoServiceTest {

    @Mock
    private DocumentoRepository documentoRepository;

    @Mock
    private TipoAcuerdoRepository tipoAcuerdoRepository;

    @InjectMocks
    private TipoAcuerdoService tipoAcuerdoService;

    private TipoAcuerdo tipoAcuerdo;
    private Documento documento;
    private TipoJuicio tipoJuicio;
    private Carpeta carpeta;
    private TipoSistema tipoSistema;
    private Materia materia;

    @BeforeEach
    public void setUp() {
        tipoAcuerdo = TipoAcuerdoSetUp.createTipoAcuerdo();
        materia = MateriaSetUp.createMateria();
        tipoJuicio = TipoJuicioSetUp.createTipoJuicio();
        carpeta = CarpetaSetUp.create();
        documento = DocumentoSetUp.create(tipoJuicio);
        tipoSistema = TipoSistemaSetUp.createTipoSistema();

    }

    @Test
    void testFindByDocumentoId_DocumentoNotFound() {
        Integer documentoId = 1;
        when(documentoRepository.findById(documentoId)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                tipoAcuerdoService.findByDocumentoId(documentoId)
        );
        assertTrue(exception.getMessage().contains("Documento no encontrado"));
    }

    @Test
    void testFindByDocumentoId_FamiliarMateria() {
        documento.setId(51);
        tipoSistema.setId(100);
        tipoSistema.setNombre("Tradicional");
        tipoJuicio.setTipoSistema(tipoSistema);
        carpeta.setTipoJuicio(tipoJuicio);
        documento.setCarpeta(carpeta);
        materia.setId(250);
        materia.setNombre("FAMILIAR");
        documento.getCarpeta().getTipoJuicio().setMateria(materia);
        tipoAcuerdo.setId(50);
        tipoAcuerdo.setNombre("PROYECTO DE SENTENCIA");
        tipoAcuerdo.setMateria(materia);
        tipoAcuerdo.setTipoSistema(tipoSistema);

        when(documentoRepository.findById(51)).thenReturn(Optional.of(documento));
        when(tipoAcuerdoRepository.findByMateriaIdAndTipoSistemaId(materia.getId(), tipoSistema.getId()))
                .thenReturn(Collections.singletonList(tipoAcuerdo));

        List<TipoAcuerdoRecord> result = tipoAcuerdoService.findByDocumentoId(51);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(tipoAcuerdo.getId(), result.get(0).id());
        assertEquals(tipoAcuerdo.getNombre(), result.get(0).nombreAcuerdo());
    }

    @Test
    void testFindByDocumentoId_FamiliarMateriaOral() {
        documento.setId(51);
        tipoSistema.setId(101);
        tipoSistema.setNombre("Oral");
        tipoJuicio.setTipoSistema(tipoSistema);
        carpeta.setTipoJuicio(tipoJuicio);
        documento.setCarpeta(carpeta);
        materia.setId(250);
        materia.setNombre("FAMILIAR");
        documento.getCarpeta().getTipoJuicio().setMateria(materia);

        TipoAcuerdo tipoAcuerdo1 = new TipoAcuerdo();
        tipoAcuerdo1.setId(52);
        tipoAcuerdo1.setNombre("ENTREGA DE DISCO DVD");
        tipoAcuerdo1.setMateria(materia);
        tipoAcuerdo1.setTipoSistema(tipoSistema);

        TipoAcuerdo tipoAcuerdo2 = new TipoAcuerdo();
        tipoAcuerdo2.setId(53);
        tipoAcuerdo2.setNombre("ENTREGA DE OFICIO");
        tipoAcuerdo2.setMateria(materia);
        tipoAcuerdo2.setTipoSistema(tipoSistema);


        when(documentoRepository.findById(51)).thenReturn(Optional.of(documento));
        when(tipoAcuerdoRepository.findByMateriaIdAndTipoSistemaId(materia.getId(), tipoSistema.getId()))
                .thenReturn(Arrays.asList(tipoAcuerdo1, tipoAcuerdo2));

        List<TipoAcuerdoRecord> result = tipoAcuerdoService.findByDocumentoId(51);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(tipoAcuerdo1.getId(), result.get(0).id());
        assertEquals(tipoAcuerdo1.getNombre(), result.get(0).nombreAcuerdo());
        assertEquals(tipoAcuerdo2.getId(), result.get(1).id());
        assertEquals(tipoAcuerdo2.getNombre(), result.get(1).nombreAcuerdo());
    }

    @Test
    void testFindByDocumentoId_TipoSistemaNull() {
        documento.setId(51);
        tipoJuicio.setTipoSistema(null);
        carpeta.setTipoJuicio(tipoJuicio);
        documento.setCarpeta(carpeta);
        materia.setId(250);
        materia.setNombre("FAMILIAR");
        documento.getCarpeta().getTipoJuicio().setMateria(materia);

        when(documentoRepository.findById(51)).thenReturn(Optional.of(documento));
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                tipoAcuerdoService.findByDocumentoId(51)
        );

        assertTrue(exception.getMessage().contains("Tipo de sistema no encontrado para materia 'FAMILIAR'"));
    }


    @Test
    void testFindByDocumentoId_NonFamiliarMateria_PenalSinTipoSistema() {
        documento.setId(51);
        tipoSistema = null;
        tipoJuicio.setTipoSistema(tipoSistema);
        carpeta.setTipoJuicio(tipoJuicio);
        documento.setCarpeta(carpeta);
        materia.setId(150);
        materia.setNombre("LABORAL");
        documento.getCarpeta().getTipoJuicio().setMateria(materia);

        TipoAcuerdo tipoAcuerdo1 = new TipoAcuerdo();
        tipoAcuerdo1.setId(55);
        tipoAcuerdo1.setNombre("AUDIENCIA ASUNTOS COLECTIVOS");
        tipoAcuerdo1.setMateria(materia);
        tipoAcuerdo1.setTipoSistema(null);

        TipoAcuerdo tipoAcuerdo2 = new TipoAcuerdo();
        tipoAcuerdo2.setId(56);
        tipoAcuerdo2.setNombre("DEVOLUCION DE DOCUMENTOS");
        tipoAcuerdo2.setMateria(materia);
        tipoAcuerdo2.setTipoSistema(null);

        when(documentoRepository.findById(51)).thenReturn(Optional.of(documento));
        when(tipoAcuerdoRepository.findByMateriaId(materia.getId()))
                .thenReturn(Arrays.asList(tipoAcuerdo1, tipoAcuerdo2));

        List<TipoAcuerdoRecord> result = tipoAcuerdoService.findByDocumentoId(51);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(tipoAcuerdo1.getId(), result.get(0).id());
        assertEquals(tipoAcuerdo1.getNombre(), result.get(0).nombreAcuerdo());
        assertEquals(tipoAcuerdo2.getId(), result.get(1).id());
        assertEquals(tipoAcuerdo2.getNombre(), result.get(1).nombreAcuerdo());
    }
}
