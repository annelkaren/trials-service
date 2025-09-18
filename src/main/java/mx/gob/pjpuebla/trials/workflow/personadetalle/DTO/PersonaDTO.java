package mx.gob.pjpuebla.trials.workflow.personadetalle.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonaDTO {

    @JsonProperty("datosContacto")
    private DatosContacto datosContacto;

    @JsonProperty("datosEstadistica")
    private DatosEstadistica datosEstadistica;

    @JsonProperty("datosGenerales")
    private DatosGenerales datosGenerales;

    @Data
    public static class DatosContacto {

        @JsonProperty("calle")
        private String calle;

        @JsonProperty("codigoPostal")
        private String codigoPostal;

        @JsonProperty("colonia")
        private String colonia;

        @JsonProperty("correoElectronico")
        private String correoElectronico;

        @JsonProperty("estado")
        private String estado;

        @JsonProperty("municipio")
        private String municipio;

        @JsonProperty("numeroExterior")
        private String numeroExterior;

        @JsonProperty("numeroInterior")
        private String numeroInterior;

        @JsonProperty("pais")
        private Integer pais;

        @JsonProperty("telefono")
        private String telefono;

        @JsonProperty("tipoDomicilio")
        private String tipoDomicilio;
    }

    @Data
    public static class DatosEstadistica {

        @JsonProperty("posicionTrabajo")
        private Integer posicionTrabajo;

        @JsonProperty("cantidadBienes")
        private Integer cantidadBienes;

        @JsonProperty("cantidadDependientes")
        private Integer cantidadDependientes;

        @JsonProperty("condicionMigratoria")
        private String condicionMigratoria;

        @JsonProperty("datosPrivados")
        private Boolean datosPrivados;

        @JsonProperty("detallesDependientes")
        private String detallesDependientes;

        @JsonProperty("discapacidad")
        private String discapacidad;

        @JsonProperty("documentoIdentificacion")
        private Integer documentoIdentificacion;

        @JsonProperty("entidadNacimiento")
        private String entidadNacimiento;

        @JsonProperty("escolaridad")
        private Integer escolaridad;

        @JsonProperty("frecuenciaIngreso")
        private String frecuenciaIngreso;

        @JsonProperty("grupoEtnico")
        private String grupoEtnico;

        @JsonProperty("grupoVulnerable")
        private String grupoVulnerable;

        @JsonProperty("hablaEspanol")
        private Boolean hablaEspanol;

        @JsonProperty("ingresosMensuales")
        private String ingresosMensuales;

        @JsonProperty("lenguaExtranjera")
        private String lenguaExtranjera;

        @JsonProperty("lenguaIndigena")
        private Integer lenguaIndigena;

        @JsonProperty("lugarTrabajo")
        private String lugarTrabajo;

        @JsonProperty("municipioNacimiento")
        private String municipioNacimiento;

        @JsonProperty("paisNacimiento")
        private Integer paisNacimiento;

        @JsonProperty("profesion")
        private String profesion;

        @JsonProperty("recibePercepciones")
        private Boolean recibePercepciones;

        @JsonProperty("religion")
        private String religion;

        @JsonProperty("sabeLeer")
        private Boolean sabeLeer;

        @JsonProperty("senias")
        private String senias;

        @JsonProperty("hablaLenguaIndigena")
        private Boolean hablaLenguaIndigena;

        @JsonProperty("perteneceGrupoVulnerable")
        private Boolean perteneceGrupoVulnerable;

        @JsonProperty("tieneBienes")
        private Boolean tieneBienes;

        @JsonProperty("tieneSeniasParticulares")
        private Boolean tieneSeniasParticulares;

        @JsonProperty("perteneceGrupoEtnico")
        private Boolean perteneceGrupoEtnico;

        @JsonProperty("tieneReligion")
        private Boolean tieneReligion;

        @JsonProperty("tieneDependientes")
        private Boolean tieneDependientes;

        @JsonProperty("hablaLenguaExtranjera")
        private Boolean hablaLenguaExtranjera;

    }

    @Data
    public static class DatosGenerales {

        @JsonProperty("adscripcion")
        private String adscripcion;

        @JsonProperty("apellidoMaterno")
        private String apellidoMaterno;

        @JsonProperty("apellidoPaterno")
        private String apellidoPaterno;

        @JsonProperty("cedula")
        private String cedula;

        @JsonProperty("curp")
        private String curp;

        @JsonProperty("edad")
        private Integer edad;

        @JsonProperty("enRepresentacion")
        private Integer enRepresentacion;

        @JsonProperty("estadoCivil")
        private String estadoCivil;

        @JsonProperty("fechaNacimiento")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate fechaNacimiento;

        @JsonProperty("idCarpeta")
        private Integer idCarpeta;

        @JsonProperty("nacionalidad")
        private Integer nacionalidad;

        @JsonProperty("nombre")
        private String nombres;

        @JsonProperty("pseudonimo")
        private String pseudonimo;

        @JsonProperty("razonSocial")
        private String razonSocial;

        @JsonProperty("rfc")
        private String rfc;

        @JsonProperty("sexo")
        private String sexo;

        @JsonProperty("tipo")
        private Integer tipo;

        @JsonProperty("tipoDefensor")
        private String tipoDefensor;

        @JsonProperty("tipoPersona")
        private String tipoPersona;
    }

}
