package mx.gob.pjpuebla.trials.core.salas;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.bloques.BloqueRepository;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.util.Estado;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class SalaService {

    private final SalaRepository salaRepository;
    private final JuzgadoRepository JuzgadoRepository;
    private final BloqueRepository BloqueRepository;


}
