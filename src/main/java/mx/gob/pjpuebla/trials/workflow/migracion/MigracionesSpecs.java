package mx.gob.pjpuebla.trials.workflow.migracion;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

import mx.gob.pjpuebla.trials.workflow.bandejas.records.BandejaMigracionFilter;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;

public final class MigracionesSpecs {
    
    private MigracionesSpecs() {}

        public static Specification<Migraciones> withFilters(BandejaMigracionFilter f) {
        return (root, query, cb) -> {
            if (f == null) return cb.conjunction();

            List<Predicate> preds = new ArrayList<>();

            if (f.estado() != null) {
                preds.add(cb.equal(root.get("estatus"), f.estado()));
            }
            if (f.juzgadoId() != null) {
                preds.add(cb.equal(root.get("juzgado").get("id"), f.juzgadoId()));
            }
            if (f.carpetaId() != null) {
                preds.add(cb.equal(root.get("carpeta").get("id"), f.carpetaId()));
            }

            // Joins perezosos: solo si hacen falta
            Join<Migraciones, Carpeta> carpetaJoin = null;
            boolean needsCarpeta = (f.expediente() != null && !f.expediente().isBlank())
                    || (f.q() != null && !f.q().isBlank());
            if (needsCarpeta) {
                carpetaJoin = root.join("carpeta", JoinType.LEFT);
            }

            if (f.expediente() != null && !f.expediente().isBlank()) {
                String like = "%" + f.expediente().toLowerCase() + "%";
                preds.add(cb.like(cb.lower(carpetaJoin.get("expediente")), like));
            }

            if (f.q() != null && !f.q().isBlank()) {
                String like = "%" + f.q().toLowerCase() + "%";
                List<Predicate> ors = new ArrayList<>();
                ors.add(cb.like(cb.lower(root.get("observaciones")), like));
                ors.add(cb.like(cb.lower(root.get("asignacionAnterior")), like));
                ors.add(cb.like(cb.lower(root.get("puestoAsignacionAnterior")), like));
                if (carpetaJoin != null) {
                    ors.add(cb.like(cb.lower(carpetaJoin.get("expediente")), like));
                }
                preds.add(cb.or(ors.toArray(new Predicate[0])));
            }

            return cb.and(preds.toArray(new Predicate[0]));
        };
    }


}
