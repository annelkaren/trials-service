package mx.gob.pjpuebla.trials.workflow.transferencias;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import mx.gob.pjpuebla.trials.core.distritos.DistritoSetUp;
import mx.gob.pjpuebla.trials.core.juzgados.Juzgado;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoSetUp;
import mx.gob.pjpuebla.trials.core.materias.MateriaSetUp;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.personas.PersonaSetUp;
import mx.gob.pjpuebla.trials.core.roles.RoleRecord;
import mx.gob.pjpuebla.trials.core.roles.RoleService;
import mx.gob.pjpuebla.trials.core.sedes.SedeSetUp;
import mx.gob.pjpuebla.trials.util.enums.EstadoTransferencia;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoService;
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;
import mx.gob.pjpuebla.trials.workflow.transferencias.records.TransferenciaRecord;
import mx.gob.pjpuebla.trials.workflow.transferencias.records.TransferenciaRecordResponse;

@ExtendWith(MockitoExtension.class)
class TransferenciaServiceTest {

    @InjectMocks
    TransferenciaService transferenciaService;

    @Mock
    PersonaRepository personaRepository;

    @Mock
    PersonaService personaService;

    @Mock 
    DocumentoService documentoService;

    @Mock
    DocumentoRepository documentoRepository;

    @Mock
    TransferenciaRepository transferenciaRepository;

    @Mock
    CarpetaRepository carpetaRepository;

    @Mock
    RoleService roleService;

    private Juzgado juzgado;
    private Transferencia transferencia;

    @BeforeEach
    public void SetUp(){
        juzgado = JuzgadoSetUp.createJuzgado(MateriaSetUp.createMateria(), SedeSetUp.createSede().setDistrito(DistritoSetUp.createDistrito()));

        transferencia = new Transferencia()
            .setId(1)
            .setJuzgado(juzgado)
            .setUuid(UUID.randomUUID());
    }

    @Test
    void updateTest(){
        Persona personaEntrega = PersonaSetUp.createPersona().setId(1L).setJuzgado(juzgado);
        Persona personaRecibe = PersonaSetUp.createPersona().setId(2L).setJuzgado(juzgado);

        TransferenciaRecord request = new TransferenciaRecord(personaEntrega.getId().intValue(), personaRecibe.getId().intValue(), juzgado.getId(), "");

        transferencia.setEntregaId(personaEntrega.getId());

        Transferencia result = transferencia;
        result.setEstatus(EstadoTransferencia.CONCLUIDO);
        result.setRecibeId(personaRecibe.getId());

        RoleRecord rol = new RoleRecord("1","TEST");

        Page<Movimiento> page = new PageImpl<>(Collections.emptyList());

        given(transferenciaRepository.findById(transferencia.getId())).willReturn(Optional.of(transferencia));
        given(personaRepository.findById(1L)).willReturn(Optional.of(personaEntrega));
        given(personaRepository.findById(2L)).willReturn(Optional.of(personaRecibe));
        given(documentoRepository.findByPersonaAsignada("", juzgado.getId(), personaEntrega, true, Pageable.unpaged())).willReturn(page);
        given(transferenciaRepository.save(any())).willReturn(result);
        given(roleService.getRolesByUserId(any())).willReturn(List.of(rol));

        TransferenciaRecordResponse response = transferenciaService.update(request, transferencia.getId());

        assertThat(response).isNotNull()
        .hasFieldOrPropertyWithValue("uuid", transferencia.getUuid().toString())
        .hasFieldOrPropertyWithValue("estatus", EstadoTransferencia.CONCLUIDO.name());
    }

    @Test
    void getTransferenciaTest(){
        RoleRecord rol = new RoleRecord("1","TEST");
        Persona personaEntrega = PersonaSetUp.createPersona().setId(1L).setJuzgado(juzgado);
        Persona personaRecibe = PersonaSetUp.createPersona().setId(2L).setJuzgado(juzgado);

        transferencia
            .setEntregaId(personaEntrega.getId())
            .setRecibeId(personaRecibe.getId())
            .setEstatus(EstadoTransferencia.CONCLUIDO);
            
        given(transferenciaRepository.findByUuid(any())).willReturn(Optional.of(transferencia));
        given(personaRepository.findById(1L)).willReturn(Optional.of(personaEntrega));
        given(personaRepository.findById(2L)).willReturn(Optional.of(personaRecibe));
        given(roleService.getRolesByUserId(any())).willReturn(List.of(rol));

        TransferenciaRecordResponse response = transferenciaService.getTransferencia(transferencia.getUuid().toString()); 

        assertThat(response).isNotNull()
        .hasFieldOrPropertyWithValue("uuid", transferencia.getUuid().toString())
        .hasFieldOrPropertyWithValue("estatus", EstadoTransferencia.CONCLUIDO.name());
    }

    @Test
    void getTransferenciaByPersonaEntrega(){
        RoleRecord rol = new RoleRecord("1","TEST");
        Persona personaEntrega = PersonaSetUp.createPersona().setId(1L).setJuzgado(juzgado);
        Persona personaRecibe = PersonaSetUp.createPersona().setId(2L).setJuzgado(juzgado);

        transferencia
            .setEntregaId(personaEntrega.getId())
            .setRecibeId(personaRecibe.getId())
            .setEstatus(EstadoTransferencia.AUTORIZADO);
            
        given(transferenciaRepository.findByEntregaIdAndEstatus(1, EstadoTransferencia.AUTORIZADO)).willReturn(Optional.of(transferencia));
        given(personaService.getAuditor()).willReturn(personaEntrega);
        given(personaRepository.findById(2L)).willReturn(Optional.of(personaRecibe));
        given(roleService.getRolesByUserId(any())).willReturn(List.of(rol));

        TransferenciaRecordResponse response = transferenciaService.getTransferenciaByPersonaEntrega(); 

        assertThat(response).isNotNull().hasFieldOrPropertyWithValue("estatus", EstadoTransferencia.AUTORIZADO.name());
    }
}
