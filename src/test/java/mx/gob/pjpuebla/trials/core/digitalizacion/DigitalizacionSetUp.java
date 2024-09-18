package mx.gob.pjpuebla.trials.core.digitalizacion;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class DigitalizacionSetUp {

    private DigitalizacionSetUp() {
    }

    public static MultipartFile generarArchivo(Integer tamanioMB, String nombreArchivo, String tipoContenido) {
        byte[] contenidoArchivo = new byte[tamanioMB * 1024 * 1024]; // Tamaño dinamico
        java.util.Arrays.fill(contenidoArchivo, (byte) 0x25);

        return new MockMultipartFile(
                nombreArchivo,
                nombreArchivo,
                tipoContenido,
                contenidoArchivo);

    }

}
