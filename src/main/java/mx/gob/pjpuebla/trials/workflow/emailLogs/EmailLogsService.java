package mx.gob.pjpuebla.trials.workflow.emailLogs;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.util.enums.EstadoEnvioCorreo;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailLogsService {

    private final EmailLogsRepository emailLogsRepository;
    private static final int TAMANIO_LOTE = 200;
    private static final ZoneId ZONA_MEXICO = ZoneId.of("America/Mexico_City");

    public List<EmailLogs> getEmailsToVerify() {

        LocalDateTime ahoraMx = ZonedDateTime.now(ZONA_MEXICO).toLocalDateTime();

        return emailLogsRepository.findBatchToVerify(
                List.of(EstadoEnvioCorreo.PENDIENTE_ENVIO, EstadoEnvioCorreo.ENVIADO, EstadoEnvioCorreo.LEIDO),
                ahoraMx,
                PageRequest.of(0, TAMANIO_LOTE));
    }

}
