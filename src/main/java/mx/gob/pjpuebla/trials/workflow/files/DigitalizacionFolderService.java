package mx.gob.pjpuebla.trials.workflow.files;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        if (doc == null || doc.getCarpeta().getExpediente() == null || doc.getCarpeta().getJuzgado() == null) {
            throw new IllegalArgumentException("Documento o sus propiedades no pueden ser nulos");
        }

        String[] expedienteArray = doc.getCarpeta().getExpediente().split("/");


        if (expedienteArray.length < 2) {
            throw new IllegalArgumentException("El expediente no tiene el formato esperado");
        }

        String expediente = expedienteArray[0].trim();
        String year = expedienteArray[1].trim();
        String juzgado = doc.getCarpeta().getJuzgado().getNombre().trim();


        Path rootPath = Paths.get(rootFolder, "digitalizacion", year, juzgado, expediente);

        try {
            // Crear las carpetas si no existen
            Files.createDirectories(rootPath);
            log.info("Carpeta creada exitosamente en: {}", rootPath);
            return rootPath.toString();
        } catch (IOException e) {
            log.error("Error al crear las carpetas de digitalización: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear las carpetas de digitalización", e);
        }
    }
}