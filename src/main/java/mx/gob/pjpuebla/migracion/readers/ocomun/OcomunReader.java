package mx.gob.pjpuebla.migracion.readers.ocomun;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OcomunReader {
    private final OcomunRepository ocomunRepository;


    public Ocomun findByOcomun(String cu){
        Optional<Ocomun> ocomun = ocomunRepository.findTopByCuAndEstatusOrderByIdDesc(cu, "A");
    
        if(ocomun.isPresent()){
            return ocomun.get();
        }

        return null;
    }
}
