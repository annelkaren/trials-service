package mx.gob.pjpuebla.trials.workflow.documentos.anexosoficios;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.personas.Persona;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.error.NotFoundException;
import mx.gob.pjpuebla.trials.util.enums.Estado;
import mx.gob.pjpuebla.trials.util.enums.TipoDocumento;
import mx.gob.pjpuebla.trials.workflow.documentos.Documento;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.anexosoficios.records.AnexoOficioRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnexoOficioService {

    @Value("${app.root-folder}")
    private String rootFolder;

    private final AnexoOficioRepository anexoOficioRepository;
    private final DocumentoRepository documentoRepository;
    private final PersonaService personaService;

    private static final long MAX_FILE_SIZE = 50L * 1024L * 1024L;

    @Transactional
    public List<AnexoOficioRecord> guardarAnexos(Integer documentoId, List<MultipartFile> files) {
        Documento documento = getOficio(documentoId);

        if (files == null || files.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes enviar al menos un archivo.");
        }

        Persona auditor = personaService.getAuditor();
        Path directorio = resolveAnexosPath(documento, auditor);
        createDirectories(directorio);

        List<AnexoOficioRecord> result = new ArrayList<>();
        for (MultipartFile file : files) {
            validarArchivo(file);
            String nombreArchivo = "anexo_oficio_" + UUID.randomUUID() + ".pdf";
            writeFile(directorio.resolve(nombreArchivo), file);

            AnexoOficio anexoOficio = new AnexoOficio();
            anexoOficio.setDocumento(documento);
            anexoOficio.setNombreArchivo(nombreArchivo);
            anexoOficio.setEstado(Estado.ACTIVE);
            anexoOficio = anexoOficioRepository.save(anexoOficio);

            result.add(new AnexoOficioRecord(anexoOficio.getId(), anexoOficio.getNombreArchivo()));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<AnexoOficioRecord> getAnexosByOficio(Integer documentoId) {
        getOficio(documentoId);
        return anexoOficioRepository.findAllByDocumentoIdAndEstadoOrderByIdAsc(documentoId, Estado.ACTIVE)
                .stream()
                .map(anexo -> new AnexoOficioRecord(anexo.getId(), anexo.getNombreArchivo()))
                .toList();
    }

    @Transactional(readOnly = true)
    public byte[] getAnexoFile(Integer anexoId) throws IOException {
        AnexoOficio anexo = anexoOficioRepository.findByIdAndEstado(anexoId, Estado.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Anexo de oficio no encontrado", "anexoId: " + anexoId));

        Persona auditor = personaService.getAuditor();
        Path ruta = resolveAnexosPath(anexo.getDocumento(), auditor).resolve(anexo.getNombreArchivo());

        if (!Files.exists(ruta)) {
            throw new NotFoundException("Archivo de anexo de oficio no encontrado", ruta.toString());
        }

        return Files.readAllBytes(ruta);
    }

    @Transactional(readOnly = true)
    public byte[] mergeOficioConAnexos(Integer documentoId, byte[] oficioBytes) {
        List<AnexoOficio> anexos = anexoOficioRepository
                .findAllByDocumentoIdAndEstadoOrderByIdAsc(documentoId, Estado.ACTIVE);

        if (anexos.isEmpty()) {
            return oficioBytes;
        }

        Persona auditor = personaService.getAuditor();
        Documento documento = getOficio(documentoId);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document outputDocument = new Document();
            PdfCopy pdfCopy = new PdfCopy(outputDocument, outputStream);
            outputDocument.open();

            appendPdf(pdfCopy, new ByteArrayInputStream(oficioBytes));

            for (AnexoOficio anexo : anexos) {
                Path rutaAnexo = resolveAnexosPath(documento, auditor).resolve(anexo.getNombreArchivo());
                if (!Files.exists(rutaAnexo)) {
                    throw new NotFoundException("Archivo de anexo de oficio no encontrado", rutaAnexo.toString());
                }
                try (InputStream inputStream = Files.newInputStream(rutaAnexo)) {
                    appendPdf(pdfCopy, inputStream);
                }
            }

            outputDocument.close();
            pdfCopy.close();
            return outputStream.toByteArray();
        } catch (IOException | DocumentException e) {
            log.error("Error al unir oficio con anexos", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "No fue posible unir el oficio con sus anexos.");
        }
    }

    private void appendPdf(PdfCopy pdfCopy, InputStream inputStream) throws IOException {
        PdfReader reader = new PdfReader(inputStream);
        int totalPages = reader.getNumberOfPages();
        for (int page = 1; page <= totalPages; page++) {
            pdfCopy.addPage(pdfCopy.getImportedPage(reader, page));
        }
        pdfCopy.freeReader(reader);
        reader.close();
    }

    private Documento getOficio(Integer documentoId) {
        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new NotFoundException("Documento no encontrado", "documentoId: " + documentoId));

        if (!TipoDocumento.OFICIO.equals(documento.getTipoDocumento())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El documento no es un oficio.");
        }

        return documento;
    }

    private void validarArchivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo no puede estar vacío.");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se permiten archivos PDF.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo no puede superar los 50 MB.");
        }
    }

    private void writeFile(Path filePath, MultipartFile file) {
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "No fue posible guardar el archivo de anexo.", e);
        }
    }

    private void createDirectories(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "No fue posible crear la carpeta de anexos de oficio.", e);
        }
    }

    private Path resolveAnexosPath(Documento documento, Persona auditor) {
        String year = resolveYear(documento);
        String centroTrabajo = resolveCentroTrabajo(documento, auditor);
        String tipoOficioDir = "Administrativo".equalsIgnoreCase(documento.getData().getTipoOficio())
                ? "oficioAdministrativo"
                : "oficioJurisdiccional";

        return Paths.get(rootFolder, "digitalizacion", year, centroTrabajo, tipoOficioDir, "anexos_oficios");
    }

    private String resolveYear(Documento documento) {
        if (documento.getCarpeta() != null && documento.getCarpeta().getExpediente() != null) {
            String[] expedienteParts = documento.getCarpeta().getExpediente().split("/");
            if (expedienteParts.length > 1) {
                return expedienteParts[1].trim();
            }
        }
        return String.valueOf(LocalDate.now().getYear());
    }

    private String resolveCentroTrabajo(Documento documento, Persona auditor) {
        String value;
        if (documento.getCarpeta() != null && documento.getCarpeta().getJuzgado() != null) {
            value = documento.getCarpeta().getJuzgado().getNombre();
        } else if (auditor.getJuzgado() != null) {
            value = auditor.getJuzgado().getNombre();
        } else if (auditor.getOficialia() != null) {
            value = auditor.getOficialia().getNombre();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La persona no está asociada a juzgado ni oficialía.");
        }
        return value.trim().replaceAll("\\s+", "");
    }
}
