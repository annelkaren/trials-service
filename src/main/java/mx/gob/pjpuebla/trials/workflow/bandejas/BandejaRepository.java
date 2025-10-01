package mx.gob.pjpuebla.trials.workflow.bandejas;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.gob.pjpuebla.trials.workflow.movimientos.Movimiento;

public interface BandejaRepository extends JpaRepository<Movimiento, Long>, BandejaRepositoryCustom {
}