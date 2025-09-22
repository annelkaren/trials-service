package mx.gob.pjpuebla.migracion.readers.ocomun;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OcomunReader {
    private final OcomunRepository ocomunRepository;


    public Optional<Ocomun> findByOcomun(String cu){
        return ocomunRepository.findTopByCuAndEstatusOrderByIdDesc(cu, "A");
    }
}
