package mx.gob.pjpuebla.trials.util.ftp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FtpDownloader {

    private final FtpProps props;

    /** Busca un server config por host/IP exacto */
    private FtpProps.FtpServerProps findByHost(String hostIp) {
        for (Map.Entry<String, FtpProps.FtpServerProps> e : props.getServers().entrySet()) {
            if (hostIp.equals(e.getValue().getHost()))
                return e.getValue();
        }
        throw new IllegalArgumentException("No hay configuración FTP para host: " + hostIp);
    }

    /** Descarga un archivo binario por FTP y regresa byte[] */
    public byte[] download(FtpProps.FtpServerProps cfg, String remotePath) {
        FTPClient ftp = new FTPClient();
        try {
            ftp.setConnectTimeout(cfg.getConnectTimeoutMs());
            ftp.connect(cfg.getHost(), cfg.getPort());
            if (!ftp.login(cfg.getUsername(), cfg.getPassword())) {
                throw new RuntimeException("Login FTP falló para " + cfg.getHost());
            }
            if (cfg.isPassiveMode())
                ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.setDataTimeout(cfg.getDataTimeoutMs());

            try (InputStream in = ftp.retrieveFileStream(remotePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                if (in == null) {
                    throw new RuntimeException("No se pudo abrir stream del archivo: " + remotePath);
                }
                in.transferTo(baos);
                if (!ftp.completePendingCommand()) {
                    throw new RuntimeException("Error al completar la descarga FTP");
                }
                return baos.toByteArray();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error FTP (" + cfg.getHost() + "): " + ex.getMessage(), ex);
        } finally {
            try {
                if (ftp.isConnected()) {
                    ftp.logout();
                    ftp.disconnect();
                }
            } catch (Exception ignore) {
            }
        }
    }

    /** Variante A: si la ruta viene como URL completa ftp://IP/dir/archivo.ext */
    public byte[] downloadFromFullUrl(String ftpUrl) {
        try {
            URI uri = URI.create(ftpUrl); // ej. ftp://172.16.6.22/carpeta/archivo.pdf
            String host = uri.getHost();
            String path = uri.getPath(); // /carpeta/archivo.pdf
            FtpProps.FtpServerProps cfg = findByHost(host);
            return download(cfg, path);
        } catch (IllegalArgumentException iae) {
            throw iae;
        } catch (Exception e) {
            throw new RuntimeException("Ruta FTP inválida: " + ftpUrl, e);
        }
    }

    /** Variante B: si solo tienes la IP en 'ruta' y el path remoto por separado */
    public byte[] downloadFromHostAndPath(String hostIp, String remotePath) {
        FtpProps.FtpServerProps cfg = findByHost(hostIp);
        return download(cfg, remotePath);
    }

}
