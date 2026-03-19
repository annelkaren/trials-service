package mx.gob.pjpuebla.trials.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import mx.gob.pjpuebla.trials.util.enums.EmailErrorType;
import mx.gob.pjpuebla.trials.workflow.emailLogs.EmailError;

public class SmtpResponseMapper {

    private final Map<String, EmailError> catalog;

    public SmtpResponseMapper() {
        this.catalog = loadCatalog();
    }

    public EmailError map(String code, String subcode) {

        EmailError base = catalog.get(subcode);

        if (base != null) {
            return EmailError.withSmtp(base, code, subcode);
        }

        return fallback(code, subcode);
    }

    private Map<String, EmailError> loadCatalog() {

        try {
            ObjectMapper mapper = new ObjectMapper();

            InputStream is = getClass()
                    .getClassLoader()
                    .getResourceAsStream("smtp-errors.json");

            JsonNode root = mapper.readTree(is);

            Map<String, EmailError> map = new HashMap<>();

            root.fields().forEachRemaining(entry -> {

                String subcode = entry.getKey();
                JsonNode node = entry.getValue();

                EmailError error = EmailError.of(
                        EmailErrorType.valueOf(node.get("type").asText()),
                        node.get("message").asText(),
                        node.get("retryable").asBoolean());

                map.put(subcode, error);
            });

            return map;

        } catch (Exception e) {
            throw new RuntimeException("Error cargando smtp-errors.json", e);
        }
    }

    private EmailError fallback(String code, String subcode) {

        if (code != null && code.startsWith("2")) {
            return new EmailError(
                    EmailErrorType.SUCCESS,
                    "Correo enviado correctamente",
                    false,
                    code,
                    subcode);
        }

        if (code != null && code.startsWith("4")) {
            return new EmailError(
                    EmailErrorType.TEMPORARY_FAILURE,
                    "Error temporal, se reintentará",
                    true,
                    code,
                    subcode);
        }

        if (code != null && code.startsWith("5")) {
            return new EmailError(
                    EmailErrorType.SERVER_ERROR,
                    "Error permanente en el envío",
                    false,
                    code,
                    subcode);
        }

        return new EmailError(
                EmailErrorType.UNKNOWN,
                "Error desconocido",
                false,
                code,
                subcode);
    }
}