package mx.gob.pjpuebla.trials.workflow.documentos.records;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

class DocumentoDataTest {

    @Test
    void debeDeserializarCamposLegacyDeDomicilios() throws Exception {
        String json = """
                {
                  "domicilio": "domicilio principal",
                  "ultimoDomicilioFamiliar": "ultimo domicilio",
                  "domicilioFamiliar": "domicilio familiar",
                  "domicilioAcreedor": "domicilio acreedor",
                  "domicilioDemandado": "domicilio demandado",
                  "campoDesconocido": "se ignora"
                }
                """;

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        DocumentoData data = objectMapper.readValue(json, DocumentoData.class);

        assertEquals("domicilio principal", data.getDomicilio());
        assertEquals("ultimo domicilio", data.getUltimoDomicilioFamiliar());
        assertEquals("domicilio familiar", data.getDomicilioFamiliar());
        assertEquals("domicilio acreedor", data.getDomicilioAcreedor());
        assertEquals("domicilio demandado", data.getDomicilioDemandado());
    }
}
