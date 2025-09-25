package mx.gob.pjpuebla.migracion.readers.acuerdos;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad que representa los acuerdos migrados desde la base de datos secundaria.
 */
@Data
@Entity
@Table(name = "acuerdos")
public class AcuerdosMigracion {
    @Id
    private Integer clave;                // PK, autoincremental
    private Integer idAcuerdo;            // ID lógico del acuerdo
    private String cuEntradas;            // CU relacionado con entradas
    private String pieza;                 // Pieza del expediente
    private String ruta;                  // Ruta del archivo
    private String estatus;               // Estatus actual (1=activo, 0=inactivo)
    private String usuario;               // Usuario que generó o registró
    private String resumen;              // Resumen del acuerdo
    private LocalDate fecha;             // Fecha del acuerdo
    private String personal;             // Nombre del personal asociado
    private String visible;              // Flag de visibilidad (Y/N)
    private String juzgado;              // Juzgado origen
    private String destino;              // Destino del acuerdo
    private String mes;                  // Mes del acuerdo (texto)
    private String materia;              // Materia procesal (civil, penal, etc.)
    private String etapa;                // Etapa del juicio
    private String sentencia;            // Flag de si es sentencia (S/N)
    private String fojas;                // Número de fojas
    private Integer idPromocion;         // ID de la promoción relacionada
    
    @Column(name="fecha_resolucion")
    private LocalDate fechaResolucion;   // Fecha de resolución
    
    @Column(name = "juzgado_aux")
    private String juzgadoAux;           // Juzgado auxiliar si existe
}
