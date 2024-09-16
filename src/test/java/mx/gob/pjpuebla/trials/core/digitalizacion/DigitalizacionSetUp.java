package mx.gob.pjpuebla.trials.core.digitalizacion;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class DigitalizacionSetUp {

    private DigitalizacionSetUp() {
    }

    public static MultipartFile generarArchivoPDF() {
        // Crear un archivo de 50 MB
        byte[] fileContent = new byte[50 * 1024 * 1024]; // 50 MB
        // Llenar con el valor hexadecimal 0x25 (que es '%', usado en PDF)
        java.util.Arrays.fill(fileContent, (byte) 0x25);

        return new MockMultipartFile(
                "file",
                "testfile.pdf",
                "application/pdf",
                fileContent
        );
    }

    public static MultipartFile generarArchivoNoPDF() {
        return new MockMultipartFile(
                "file",
                "textfile.txt",
                "text/plain",
                "This is not a PDF".getBytes()
        );
    }

    public static MultipartFile generarArchivoGrande() {
        return new MockMultipartFile(
                "file",
                "largefile.pdf",
                "application/pdf",
                new byte[51 * 1024 * 1024] // 51 MB
        );
    }

    public static MultipartFile generarArchivoPDFMenorMaximo() {

        byte[] fileContent = new byte[5 * 1024 * 1024]; // 50 MB
        java.util.Arrays.fill(fileContent, (byte) 0x25);

        return new MockMultipartFile(
                "file",
                "testfile.pdf",
                "application/pdf",
                fileContent
        );
    }
}
