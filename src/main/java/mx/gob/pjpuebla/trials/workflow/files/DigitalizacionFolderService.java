package mx.gob.pjpuebla.trials.workflow.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.documentos.Documento;

@Slf4j
@Service
@RequiredArgsConstructor
public class DigitalizacionFolderService {
    private static final String ROOT_FOLDER = "digitalizacion";

    public Integer createFolderDigitalizacion(Documento doc) {
        /*
        String[] folderStruct = doc.getExpediente().split("/");

        if (folderStruct.length < 3) {
            log.error("Estructura del expediente incorrecta: {}", doc.getExpediente());
            return 0; // estructura incorrecta
        }
        
        String year = folderStruct[0];
        String juzgado = folderStruct[1];
        String expediente = folderStruct[2];        
        */
        String year = Integer.toString(LocalDate.now().getYear());
        String juzgado = doc.getJuzgado().getNombre();
        String expediente = doc.getExpediente();   

        Path rootPath = Paths.get(System.getProperty("user.dir"), ROOT_FOLDER, year, juzgado, expediente);

        try {
            // Crear las carpetas si no existen
            Files.createDirectories(rootPath);
            log.info("Carpeta creada exitosamente en: {}", rootPath.toString());
            return 1; // éxito
        } catch (IOException e) {
            log.error("Error al crear las carpetas de digitalización: {}", e.getMessage());
            return -1; // error al crear la carpeta
        }
    }
}
