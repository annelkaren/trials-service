package mx.gob.pjpuebla.migracion.acl.mapper;

import org.springframework.stereotype.Component;

@Component
public class TipoPiezaMapper {
    public String getTipoPieza(String cu) {
        if (cu == null) {
            throw new IllegalArgumentException("cu no puede ser null");
        }
        cu = cu.trim();

        int len = cu.length();
        if (len != 16) {
            throw new IllegalArgumentException(
                    "cu debe tener exactamente 16 caracteres; recibido " + len + ": '" + cu + "'");
        }

        // (Opcional) Validación de formato: 12 dígitos + 2 letras + 2 dígitos
        // Ajusta el regex si tu sufijo puede variar.
        if (!cu.matches("\\d{12}[A-Za-z]{2}\\d{2}")) {
            throw new IllegalArgumentException(
                    "Formato de pieza (cu) inválido. Se esperaba 12 dígitos, 2 letras, 2 dígitos.");
        }

        // Si ya validaste longitud==16, es equivalente usar (12,16) o solo (12)
        return cu.substring(12); // "AC01" por ejemplo
    }
}
