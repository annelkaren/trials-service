package mx.gob.pjpuebla.migracion.readers.ocomun;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ocomun")
public class Ocomun {

    @Id
    @Column(name = "id")
    private Integer id;

    private Integer folio;
    private String expediente;

    @Column(name = "ayo")
    private String year;

    @Column(name = "juzgado")
    private String claveJuzgado;

    private String cu;
    private LocalDate fecha;
    private String hora;
    private String estatus;
    private String anexos;
    private String juicio;
    
    @Column(name = "status_enviado")
    private String statusEnviado;
    private String digitalizado;
    
    @Column(name = "fecha_env")
    private LocalDate fechaDigitalizacion;

    @Column(name = "hora_env") //fecha y hora de digitalización.
    private String horaDigitalizacion;

    @Column(name = "fecha_juz") // fecha en ña que se recepciono en el juzgado. 
    private LocalDate fechaRecepcionJuzgado;
    
    @Column(name = "hora_juz")
    private String horaRecepcionJuzgado;

    @Column(name = "archivo")
    private String rutaDigitalizacion;

    private String actor;
    private String demandado;
    private String correo;

    // en donse se esta capturando O apelacion o auxiliar cuando son de segunda instancia. 
    private String distrito;

    //se setean cuando es una apelación:
    @Column(name = "proce")
    private String juzgadoProcedenciaApelacion;

    @Column(name = "pexp")
    private String expedienteProcedenciaApelacion;

    @Column(name = "payo")
    private String yearExpedienteProcedenciaApelacion;

    @Column(name = "q_registro")
    private String personaQuienRegistro;

    @Column(name = "q_digitalizo")
    private String personaQuienDigitalizo;

    @Column(name = "oficialia")
    private String claveOficialia; // se une con tabla de juzgados es la clave.

    @Column(name = "salida") // salida: por defecto esta vacio, S -> da salida, J -> cuando ya esta en el juzgado.
    private String salida;

    @Column(name = "fecha_hora_sal")
    private String fechaHoraSalida;

    @Column(name = "dio_salida") // Quien dio salida al expediente
    private String dioSalida;

    @Column(name = "nombre_carrito") // quien se llevo los documentos del expediente. 
    private String nombreCarrito;
    
    @Column(name = "id_carrito")
    private String idPersonaCarrito; // id que vincula con tabla accesos para saber la persona que se lleva el documento

    // datos de procedencia por si existe una segunda procedencia de una apelacion.
    @Column(name = "proce2")
    private String segundoJuzgadoProcedenciaApelacion;

    @Column(name = "pexp2")
    private String segundoExpedienteProcedenciaApelacion;

    @Column(name = "payo2")
    private String segundoYearExpedienteProcedenciaApelacion;

    @Column(name = "jorigen")
    private String claveJuzgadoOrigen;

    @Column(name = "cuorigenJA")
    private String cuOrigenJA;

    @Column(name = "piezasJA") // super numerario cuantas piezas lleva. 
    private String piezasJA;

    @Column(name="anexosJA")
    private String anexosJA;

    private String diasentenciaJA;
    
    @Column(name = "numero_medida")
    private String numeroMedida;

    // columnas aplicables solo para sistemas de oralidad:

    @Column(name = "oral") //  clave juzgado del juicio de oralidad fam y mercantil
    private String claveJuzgadoJuicioOralidad;

    @Column(name = "nombre_juicio_jof")
    private String nombreJuicioOralidad;

    @Column(name = "nombre_juez_jof")
    private String nombreJuezOralidad;

    @Column(name = "celular_actor_jof")
    private String celularActorOralidad;

    @Column(name = "correo_actor_jof")
    private String correoActorOralidad;

    @Column(name = "sala_jof")
    private String salaOralidad;

    @Column(name = "fecha_aud_jof")
    private LocalDate fechaAudienciaOralidad;

    @Column(name = "hora_aud_jof")
    private LocalDateTime horaAudienciaOralidad;

    @Column(name = "abogado_jof")
    private String hasAbogadoOralidad; // S - SI N - NO , Null - NO.

    @Column(name = "udf_jof") // domicilio familiar si es un tipo de juicio de alimentos. 
    private String domicilioFamJuicioAlimentos;

    @Column(name = "dda_jof")
    private String domicioFamiliarAcredorJuicioAlimentos;

    @Column(name = "dpp_jof")
    private String domicilioFamiliarJuicioGuardaCustodia;

    @Column(name = "df_jof")
    private String domicilioFamiliarDivorcioIncausadoBilateral;

    @Column(name = "dd_jof")
    private String domicilioFamiliarDivorcioIncausadoUnilateral;

    @Column(name = "dnna_jof")
    private String domicilioVisitaYConvivencia; // juicio de niños, niñas y adolescentes.


    @Column(name = "tipoDivorcio")
    private String tipoDivorcio; // U : unilateral - B: Bilateral

    @Column(name = "curp_actor")
    private String curpActor;

    @Column(name = "cedula_profesional")
    private String cedulaProfesionalAgobado;

}
