package mx.gob.pjpuebla.trials.workflow.files;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.util.enums.TipoCarpeta;
import mx.gob.pjpuebla.trials.workflow.carpeta.Carpeta;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class DigitalizacionFolderService {

    @Value("${app.root-folder}")
    private String rootFolder;

    public String createFolderDigitalizacion(Documento doc) {
        if (doc == null) {
            throw new IllegalArgumentException("El documento no puede ser nulo");
        }

        Carpeta carpeta = doc.getCarpeta();
        if (carpeta == null) {
            throw new IllegalArgumentException("La carpeta no puede ser nula");
        }

        if (carpeta.getExpediente() == null || carpeta.getJuzgado() == null || carpeta.getTipoCarpeta() == null) {
            throw new IllegalArgumentException("Algunas propiedades de la carpeta no pueden ser nulas");
        }

        String[] expedienteArray = doc.getCarpeta().getExpediente().split("/");

        if (expedienteArray.length < 2) {
            throw new IllegalArgumentException("El expediente no tiene el formato esperado");
        }

        String expediente = expedienteArray[0].trim();
        String year = expedienteArray[1].trim();
        String juzgado = (doc.getCarpeta().getJuzgado().getNombre().trim()).replaceAll("\\s+", "");

        String nombreCarpeta;
        Path rootPath;
        if (juzgado.isEmpty()) {
            throw new IllegalArgumentException("El juzgado no puede ser nulo o vacío");
        }

        Path basePath = Paths.get(rootFolder, "digitalizacion", year, juzgado);

        if (TipoCarpeta.EXHORTO.equals(doc.getCarpeta().getTipoCarpeta())) {
            nombreCarpeta = expediente;
            rootPath = basePath.resolve(Paths.get("entrada", nombreCarpeta));
        } else {
            nombreCarpeta = String.format("%06d", Integer.parseInt(expediente));
            rootPath = basePath.resolve(nombreCarpeta);
        }

        try {
            Files.createDirectories(rootPath); // Crear las carpetas si no existen
            log.info("Carpeta creada exitosamente en: {}", rootPath);
            return rootPath.toString();
        } catch (IOException e) {
            log.error("Error al crear las carpetas de digitalización: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear las carpetas de digitalización", e);
        }
    }
}