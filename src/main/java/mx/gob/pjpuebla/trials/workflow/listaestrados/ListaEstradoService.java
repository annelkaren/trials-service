package mx.gob.pjpuebla.trials.workflow.listaestrados;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListaEstradoService {

    private final ListaEstradoRepository listaEstradoRepository;

    @Transactional(readOnly = true)
    public Page<ListaEstrado> findAllByListaEstradoId(Integer listaEstrado, String searchQuery, Pageable pageable) {

        return listaEstradoRepository.findAllListaEstradoIdAndSearch(
                searchQuery == null ? "" : searchQuery,
                listaEstrado,
                pageable);

    }

}
