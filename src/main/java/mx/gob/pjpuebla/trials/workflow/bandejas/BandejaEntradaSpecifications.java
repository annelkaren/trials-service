package mx.gob.pjpuebla.trials.workflow.bandejas;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import mx.gob.pjpuebla.trials.workflow.bandejas.records.entrada.BandejaEntradaFilter;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;

public final class BandejaEntradaSpecifications {
    private BandejaEntradaSpecifications() {
    }

    public static Specification<Movimiento> withFilters(BandejaEntradaFilter f) {
        return (root, query, cb) -> {
            if (f == null)
                return cb.conjunction();
            List<Predicate> preds = new ArrayList<>();

            // JOIN CON CARPETA Para obtener expediente:
            Join<Movimiento, Carpeta> carpetaJoin = null;
            Join<Movimiento, Documento> documentoJoin = null;
            Join<Documento, Carpeta> carpetaDeDocumentos = null;

            boolean needsCarpeta = (f.expediente() != null && !f.expediente().isBlank())
                    || (f.key() != null && !f.key().isBlank());

            if (needsCarpeta) {
                if (root.get("carpeta") != null) {
                    carpetaJoin = root.join("carpeta", JoinType.LEFT);
                } else {
                    documentoJoin = root.join("documento", JoinType.LEFT);
                    carpetaDeDocumentos = documentoJoin.join("carpeta", JoinType.INNER);
                }
            }
            if (f.expediente() != null && !f.expediente().isBlank()) {
                String like = "%" + f.expediente().toLowerCase() + "%";
                if (root.get("carpeta") != null) {
                    preds.add(cb.like(cb.lower(carpetaJoin.get("expediente")), like));
                } else {
                    preds.add(cb.like(cb.lower(carpetaDeDocumentos.get("expediente")), like));
                }
            }

            if (f.key() != null && !f.key().isBlank()) {
                String like = "%" + f.key().toLowerCase() + "%";
                List<Predicate> ors = new ArrayList<>();

                if (root.get("carpeta") != null) {
                    ors.add(cb.like(cb.lower(carpetaJoin.get("expediente")), like));
                } else {
                    ors.add(cb.like(cb.lower(carpetaDeDocumentos.get("expediente")), like));
                }

                preds.add(cb.or(ors.toArray(new Predicate[0])));
            }

            return cb.and(preds.toArray(new Predicate[0]));
        };
    }
}
