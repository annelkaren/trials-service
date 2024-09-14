package mx.gob.pjpuebla.trials.core.digitalizacion;

import java.util.Arrays;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class DigitalizacionSetUp {

    private DigitalizacionSetUp() {
    }

    public static MultipartFile generarFile() {
        // Crear un archivo de 50 MB
        byte[] fileContent = new byte[50 * 1024 * 1024]; // 50 MB
        Arrays.fill(fileContent, (byte) 0x25); // Llenar con el valor hexadecimal 0x25 (que es '%', usado en PDF)

        return new MockMultipartFile(
                "file",
                "testfile.pdf", // Nombre del archivo simulado como PDF
                "application/pdf", // MIME type para PDF
                fileContent // Contenido binario simulado
        );
    }

}
