package mx.gob.pjpuebla.trials.workflow.bandejas.criteria;

import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Convierte PageRequest.getSort() en CriteriaBuilder.orderBy(...),
 * centralizando el catálogo de campos ordenables.
 */
public final class OrderMapper {

    private OrderMapper() {}

    /**
     * Mapea Sort → List<Order>. Si no hay sort o no reconoce las propiedades,
     * devuelve lista vacía (para que el caller aplique su fallback).
     */
    public static List<Order> mapSort(Sort sort, CriteriaBuilder cb, BandejaCriteriaSupport.Joins j) {
        List<Order> orders = new ArrayList<>();
        if (sort == null || sort.isUnsorted()) return orders;

        for (Sort.Order s : sort) {
            String prop = s.getProperty().toLowerCase(Locale.ROOT);
            boolean asc = s.isAscending();

            switch (prop) {
                case "folio" -> {
                    Expression<String> folio = cb.<String>coalesce()
                            .value(j.cMov().get("folio"))
                            .value(j.doc().get("folio"));
                    orders.add(asc ? cb.asc(folio) : cb.desc(folio));
                }
                case "expediente" -> {
                    Expression<String> exp = cb.<String>coalesce()
                            .value(j.cDoc().get("expediente"))
                            .value(j.cMov().get("expediente"));
                    orders.add(asc ? cb.asc(exp) : cb.desc(exp));
                }
                case "materia" -> {
                    Expression<String> mat = cb.<String>coalesce()
                            .value(j.matDoc().get("nombre"))
                            .value(j.matMov().get("nombre"));
                    // ordenar por lower(...) para estabilidad
                    var matLower = cb.lower(mat);
                    orders.add(asc ? cb.asc(matLower) : cb.desc(matLower));
                }
                case "organojurisdiccional", "organo" -> {
                    Expression<String> org = cb.<String>coalesce()
                            .value(j.jcDoc().get("nombre"))
                            .value(j.jcMov().get("nombre"));
                    var orgLower = cb.lower(org);
                    orders.add(asc ? cb.asc(orgLower) : cb.desc(orgLower));
                }
                case "fecharegistro" -> {
                    // Usamos fechaAsignacion del movimiento
                    Expression<?> fecha = j.m().get("fechaAsignacion");
                    orders.add(asc ? cb.asc(fecha) : cb.desc(fecha));
                }
                case "tipoentrada" -> {
                    // Orden compuesto: primero “si tiene doc.tipoDocumento” (nulls last),
                    // luego doc.tipoDocumento, y si no hay doc, por cMov.tipoCarpeta.
                    Expression<Integer> rank = cb.<Integer>selectCase()
                            .when(cb.isNotNull(j.doc().get("tipoDocumento")), 0)
                            .otherwise(1);
                    orders.add(asc ? cb.asc(rank) : cb.desc(rank));
                    orders.add(asc ? cb.asc(j.doc().get("tipoDocumento")) : cb.desc(j.doc().get("tipoDocumento")));
                    orders.add(asc ? cb.asc(j.cMov().get("tipoCarpeta"))  : cb.desc(j.cMov().get("tipoCarpeta")));
                }
                case "movimientoid", "id" -> {
                    Expression<Integer> id = j.m().get("id");
                    orders.add(asc ? cb.asc(id) : cb.desc(id));
                }
                default -> {
                    // propiedad desconocida → la ignoramos (no reventamos el orden)
                }
            }
        }
        return orders;
    }
}
