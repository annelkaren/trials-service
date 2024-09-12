package mx.gob.pjpuebla.trials.workflow.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.documentos.Documento;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.yaml")
public class DigitalizacionFolderService {
    
    @Value("${root-folder}")
    private String ROOT_FOLDER;
    

    public String createFolderDigitalizacion(Documento doc) {
        
        if(ROOT_FOLDER == null){
            ROOT_FOLDER = "/opt/pjp/files";
        }

        if (doc == null || doc.getExpediente() == null || doc.getJuzgado() == null) {
            throw new IllegalArgumentException("Documento o sus propiedades no pueden ser nulos");
        }

        String expedienteArray[] = doc.getExpediente().split("/");
       
        
        if (expedienteArray.length < 2) {
            throw new IllegalArgumentException("El expediente no tiene el formato esperado");
        }

        String expediente = expedienteArray[0].trim();
        String year = expedienteArray[1].trim();
        String juzgado = doc.getJuzgado().getNombre().trim();

        Path rootPath = Paths.get(ROOT_FOLDER, "digitalizacion", year, juzgado, expediente);
        
        
        try {
            // Crear las carpetas si no existen
            System.out.println("ENTRE A CREAR CARPETA : " + rootPath.toString());
            Files.createDirectories(rootPath);
            log.info("Carpeta creada exitosamente en: {}", rootPath.toString());
            return rootPath.toString();
        } catch (IOException e) {
            log.error("Error al crear las carpetas de digitalización: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear las carpetas de digitalización", e);
        }
    }
}