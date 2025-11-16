package mx.gob.pjpuebla.trials.statistics.reports;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.core.juzgados.JuzgadoRepository;
import mx.gob.pjpuebla.trials.core.personas.PersonaService;
import mx.gob.pjpuebla.trials.core.salas.SalaRepository;
import mx.gob.pjpuebla.trials.core.sedes.records.SedeDomiciliosRecord;
import mx.gob.pjpuebla.trials.workflow.audiencias.AudienciaRepository;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import mx.gob.pjpuebla.trials.workflow.documentos.DocumentoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ReporteService {

    private static final Logger logger = LoggerFactory.getLogger(ReporteService.class);
    private final ReporteRepository reporteRepository;
    private final CarpetaRepository carpetaRepository;
    private final JuzgadoRepository juzgadoRepository;
    private final SalaRepository salaRepository;
    private final PersonaService personaService;
    private final AudienciaRepository audienciaRepository;
    private final DocumentoRepository documentoRepository;
    private final DataSource dataSource;

    public List<ReporteRecord> getAll() {
        Sort sortBy = Sort.by(Sort.Direction.ASC, "order");
        List<ReporteRecord> response = new ArrayList<>();
        List<Reporte> list = reporteRepository.findAll(sortBy);
        for (Reporte reportRecord : list) {
            if (reportRecord.getExtraData().getMaterias() != null && !reportRecord.getExtraData().getMaterias().isEmpty()) {
                if (reportRecord.getExtraData().getJuiciosExcluidos() == null || reportRecord.getExtraData().getJuiciosExcluidos().isEmpty()) {
                    reportRecord.getExtraData().setJuiciosExcluidos(Collections.singletonList(0));
                }
                LocalDateTime date = carpetaRepository.getDatesByMateria(reportRecord.getExtraData().getMaterias(), reportRecord.getExtraData().getJuiciosExcluidos());
                ReporteRecord result = new ReporteRecord(reportRecord.getKey(), reportRecord.getName(), reportRecord.getDescription(),
                        (date != null) ? date.toLocalDate() : null);
                response.add(result);
            } else if (reportRecord.getExtraData().getTipoJuicios() != null && !reportRecord.getExtraData().getTipoJuicios().isEmpty()) {
                LocalDateTime date = carpetaRepository.getDatesByTipoJuicio(reportRecord.getExtraData().getTipoJuicios());
                ReporteRecord result = new ReporteRecord(reportRecord.getKey(), reportRecord.getName(), reportRecord.getDescription(),
                        (date != null) ? date.toLocalDate() : null);
                response.add(result);
            } else {
                ReporteRecord result = new ReporteRecord(reportRecord.getKey(), reportRecord.getName(), reportRecord.getDescription(), null);
                response.add(result);
            }
        }
        return response;
    }

    public byte[] generateReport(String key, LocalDate startDate, LocalDate endDate) {
        try (QueryResult queryResult = getQueryByKey(key, startDate, endDate);
             SXSSFWorkbook wb = new SXSSFWorkbook(1_000);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ResultSet rs = queryResult.rs();

            wb.setCompressTempFiles(true);
            SXSSFSheet sheet = wb.createSheet(key + "_" + startDate.toString().replace("-", "") + "_" + endDate.toString().replace("-", ""));

            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();

            //headers
            generateHeaders(wb, sheet, md);
            //data
            setValues(wb, sheet, md, rs);

            // —— Autosize
            for (int colIdx = 0; colIdx < cols; colIdx++) {
                sheet.trackColumnForAutoSizing(colIdx);
            }

            for (int colIdx = 0; colIdx < cols; colIdx++) {
                sheet.autoSizeColumn(colIdx);
                int w = sheet.getColumnWidth(colIdx);
                int paddingChars = 2;
                sheet.setColumnWidth(colIdx, w + paddingChars * 256);
            }

            wb.write(bos);
            wb.dispose();
            return bos.toByteArray();
        } catch (Exception ex) {
            return new byte[0];
        }
    }

    private QueryResult getQueryByKey(String key, LocalDate startDate, LocalDate endDate) throws SQLException {

        String sql = "SELECT * FROM trials.reporte_" + key + """
                 WHERE make_date("AÑO_DEM"::int, "MES_DEM"::int, "DIA_DEM"::int) BETWEEN ? AND ?
                ORDER BY make_date("AÑO_DEM"::int, "MES_DEM"::int, "DIA_DEM"::int)
                """;

        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(false);

        PreparedStatement ps = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ps.setDate(1, java.sql.Date.valueOf(startDate));
        ps.setDate(2, java.sql.Date.valueOf(endDate));
        ps.setFetchSize(1_000);

        ResultSet rs = ps.executeQuery();
        return new QueryResult(conn, ps, rs);

    }

    private void generateHeaders(SXSSFWorkbook wb, Sheet sheet, ResultSetMetaData md) throws SQLException {
        // —— Estilo de encabezado ——
        CellStyle headerStyle = wb.createCellStyle();

        // Color de fondo personalizado #8C92BC
        XSSFColor customColor = new XSSFColor(new java.awt.Color(0x8C, 0x92, 0xBC), null);

        // === Estilo del header ===
        ((org.apache.poi.xssf.usermodel.XSSFCellStyle) headerStyle)
                .setFillForegroundColor(customColor);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Texto centrado horizontal y vertical
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Bordes
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        // Fuente en negrita y blanca
        XSSFFont headerFont = (XSSFFont) wb.createFont();
        headerFont.setBold(true);
        headerFont.getCTFont().addNewB();
        headerStyle.setFont(headerFont);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        // —— Header ——
        Row header = sheet.createRow(0);
        header.setHeightInPoints(43);
        for (int c = 1; c <= md.getColumnCount(); c++) {
            Cell cell = header.createCell(c - 1);
            cell.setCellValue(md.getColumnLabel(c));
            cell.setCellStyle(headerStyle);
        }

        // Freeze
        sheet.createFreezePane(0, 1);
    }

    private void setValues(SXSSFWorkbook wb, Sheet sheet, ResultSetMetaData md, ResultSet rs) throws SQLException {
        // antes de recorrer el ResultSet
        CellStyle dataStyle = wb.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // Bordes
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // —— Datos ——
        int r = 1;
        while (rs.next()) {
            Row row = sheet.createRow(r++);
            for (int c = 1; c <= md.getColumnCount(); c++) {
                Cell cell = row.createCell(c - 1);
                Object v = rs.getObject(c);
                if (v == null) cell.setBlank();
                else if (v instanceof Number n) cell.setCellValue(n.doubleValue());
                else cell.setCellValue(String.valueOf(v));

                cell.setCellStyle(dataStyle);
            }
        }
    }

    public byte[] generateCNPPReport(LocalDate startDate, LocalDate endDate) {
        try (SXSSFWorkbook wb = new SXSSFWorkbook(500);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            wb.setCompressTempFiles(true);

            CellStyle title = titleStyle(wb);
            CellStyle header = headerStyleGrey(wb);
            CellStyle normal = normalStyle(wb, false);
            normal.setAlignment(HorizontalAlignment.CENTER);
            CellStyle wrap = normalStyle(wb, true);

            // ================= Hoja 1: Capítulo 1 =================
            SXSSFSheet s1 = wb.createSheet("Capítulo 1 - Recursos");
            int r = 0;

            r = addTitleRow(s1, r, "Capítulo 1. Recursos materiales u Operaciones jurídicos", title, 4);

            r = addBlankRow(s1, r);
            r = addHeaderRow(s1, r, header, "Rubro", "Número", "Ubicación");

            // Lista de ubicaciones
            List<SedeDomiciliosRecord> originalList = juzgadoRepository.getAllUbications(Arrays.asList("PENAL"));
            String ubicaciones = getUbications(originalList).stream()
                    .map(s -> "• " + s)
                    .collect(java.util.stream.Collectors.joining("\n"));
            Row rowCJ = s1.createRow(r++);
            setCell(rowCJ, 0, "Centros de Justicia Penal (instalaciones donde se desarrolla la administración de justicia por Magistrados y Jueces)", wrap);
            setCell(rowCJ, 1, originalList.size(), normal);
            setCell(rowCJ, 2, ubicaciones, wrap);
            rowCJ.setHeightInPoints((originalList.size() * s1.getDefaultRowHeightInPoints()));
            applyBordersRange(s1, rowCJ.getRowNum(), rowCJ.getRowNum(), 0, 2);

            //Salas de audiencias
            List<SedeDomiciliosRecord> originalList2 = salaRepository.getAllUbications(Arrays.asList("PENAL"));
            String ubicacionesSala = getUbications(originalList2).stream()
                    .map(s -> "• " + s)
                    .collect(java.util.stream.Collectors.joining("\n"));
            r = addBlankRow(s1, r);
            r = addHeaderRow(s1, r, header, "Rubro", "Número", "Ubicación");
            Row rowSA = s1.createRow(r++);
            setCell(rowSA, 0, "Salas de Audiencias", normal);
            setCell(rowSA, 1, originalList2.size(), normal);
            setCell(rowSA, 2, ubicacionesSala, wrap);
            rowSA.setHeightInPoints((originalList2.size() * s1.getDefaultRowHeightInPoints()));
            applyBordersRange(s1, rowSA.getRowNum(), rowSA.getRowNum(), 0, 2);

            //Jueces
            Map<String, Integer> values = personaService.findAllJuecesPenales();
            r = addBlankRow(s1, r);
            r = addHeaderRow(s1, r, header, "Rubro", "Total", "Mujeres", "Hombres");
            List<Object[]> jueces = List.of(
                    new Object[]{"Total de Jueces Penales", values.get("total"), values.get("mujeres"), values.get("hombres")},
                    new Object[]{"Jueces con Función Administrativa", 0, 0, 0},
                    new Object[]{"Jueces con Función de Control", 0, 0, 0},
                    new Object[]{"Jueces con Función de Enjuiciamiento", 0, 0, 0},
                    new Object[]{"Jueces con Función de Ejecucion", 0, 0, 0}
            );
            r = addTableRows(s1, r, normal, jueces);
            autosizeAllColumns(s1, 6);

            // ================= Hoja 2: Capítulo 2 =================
            SXSSFSheet s2 = wb.createSheet("Capítulo 2 - Acciones");
            r = 0;
            r = addTitleRow(s2, r, "Capítulo 2. Acciones sustantivas frente a la consolidación del sistema penal adversarial", title, 4);

            r = addHeaderRow(s2, r, header, "Rubro", "Número");
            List<Object[]> indicadores = List.of(
                    new Object[]{"Audiencias Celebradas", audienciaRepository.findAllPenales(Arrays.asList("PENAL"), startDate.atTime(0, 0, 0), endDate.atTime(23, 59, 59))},
                    new Object[]{"Audiencias celebradas por videoconferencia", 0},
                    new Object[]{"Personas con prisión preventiva oficiosa", 0},
                    new Object[]{"Personas con prisión preventiva justificada", 0},
                    new Object[]{"Personas con medida cautelar diversa", 0},
                    new Object[]{"Sustitución de medidas cautelares por prisión preventiva", 0},
                    new Object[]{"Audiencias de autorización de actos de investigación (art. 252 CNPP)", 0}
            );
            r = addTableRows(s2, r, normal, indicadores);

            r = addBlankRow(s2, r);
            r = addHeaderRow(s2, r, header, "Tipo de Autorización", "Número");
            List<Object[]> tipos = List.of(
                    new Object[]{"I. Exhumación de cadáveres", 0},
                    new Object[]{"II. Órdenes de cateo", 0},
                    new Object[]{"III. Intervención de comunicaciones privadas y correspondencia", 0},
                    new Object[]{"IV. Toma de muestras / extracciones de sangre u otros", 0},
                    new Object[]{"V. Reconocimiento o examen físico (con negativa)", 0},
                    new Object[]{"VI. Las demás que señalen las leyes aplicables", 0}
            );
            r = addTableRows(s2, r, normal, tipos);

            r = addBlankRow(s2, r);
            r = addHeaderRow(s2, r, header, "Implementación de Acciones Tendientes a la Justicia Digital", "Número");
            r = addTableRows(s2, r, normal,
                    java.util.Collections.singletonList(new Object[]{"Implementación de Acciones Tendientes a la Justicia Digital", 0})
            );
            autosizeAllColumns(s2, 4);

            // ================= Hoja 3: Capítulo 3 =================
            SXSSFSheet s3 = wb.createSheet("Capítulo 3 - Operación");
            r = 0;
            r = addTitleRow(s3, r, "Capítulo 3. Operación de Centros y Personas Imputadas", title, 5);

            r = addHeaderRow(s3, r, header, "Rubro", "Número");
            List<Object[]> op = List.of(
                    new Object[]{"Causas Penales Judicializadas", 0},
                    new Object[]{"Audiencias de Ejecución", 0},
                    new Object[]{"Apelaciones", carpetaRepository.countCarpetasPenales(Arrays.asList("PENAL"))}
            );
            r = addTableRows(s3, r, normal, op);

            r = addBlankRow(s3, r);
            r = addHeaderRow(s3, r, header, "Rubro", "Total", "Mujeres", "Hombres", "Indefinido");
            List<Object[]> sentencias = List.of(
                    new Object[]{"Sentencias dictadas (incl. procedimiento abreviado)", 0, 0, 0, 0},
                    new Object[]{"Sentencias Condenatorias", documentoRepository.countDocumentosPorTipoYMaterias(Arrays.asList("PENAL")), 0, 0, 0},
                    new Object[]{"Sentencias Absolutorias", 0, 0, 0, 0},
                    new Object[]{"Sentencias Mixtas", 0, 0, 0, 0}
            );
            r = addTableRows(s3, r, normal, sentencias);
            autosizeAllColumns(s3, 6);

            wb.write(bos);
            wb.dispose();
            return bos.toByteArray();

        } catch (Exception ex) {
            logger.error("Error in generateCNPPReport: {}", ex.getMessage(), ex);
            return new byte[0];
        }
    }

    private CellStyle titleStyle(SXSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short) 14);
        s.setFont(f);
        s.setAlignment(HorizontalAlignment.LEFT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        return s;
    }

    private CellStyle headerStyleGrey(SXSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        s.setFont(f);
        s.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        setThinBorders(s);
        return s;
    }

    private CellStyle normalStyle(SXSSFWorkbook wb, boolean wrap) {
        CellStyle s = wb.createCellStyle();
        s.setVerticalAlignment(VerticalAlignment.TOP);
        s.setAlignment(HorizontalAlignment.LEFT);
        s.setWrapText(wrap);
        setThinBorders(s);
        return s;
    }

    private void setThinBorders(CellStyle s) {
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
    }

    private int addTitleRow(Sheet sh, int r, String text, CellStyle style, int mergeCols) {
        Row row = sh.createRow(r++);
        Cell cell = row.createCell(0);
        cell.setCellValue(text);
        cell.setCellStyle(style);
        sh.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, Math.max(0, mergeCols - 1)));
        return r;
    }

    private int addHeaderRow(Sheet sh, int r, CellStyle header, String... cols) {
        Row row = sh.createRow(r++);
        for (int i = 0; i < cols.length; i++) {
            Cell c = row.createCell(i);
            c.setCellValue(cols[i]);
            c.setCellStyle(header);
        }
        return r;
    }

    private int addBlankRow(Sheet sh, int r) {
        sh.createRow(r++);
        return r;
    }

    private int addTableRows(Sheet sh, int r, CellStyle body, List<Object[]> rows) {
        for (Object[] arr : rows) {
            Row row = sh.createRow(r++);
            for (int c = 0; c < arr.length; c++) {
                setCell(row, c, arr[c], body);
            }
            applyBordersRange(sh, row.getRowNum(), row.getRowNum(), 0, arr.length - 1);
        }
        return r;
    }

    private Cell setCell(Row row, int col, Object val, CellStyle style) {
        Cell cell = row.createCell(col);
        if (val == null) cell.setBlank();
        else if (val instanceof Number n) cell.setCellValue(n.doubleValue());
        else cell.setCellValue(String.valueOf(val));
        cell.setCellStyle(style);
        return cell;
    }

    private void applyBordersRange(Sheet sh, int r1, int r2, int c1, int c2) {
        for (int r = r1; r <= r2; r++) {
            Row row = sh.getRow(r);
            if (row == null) continue;
            for (int c = c1; c <= c2; c++) {
                Cell cell = row.getCell(c);
                if (cell == null) cell = row.createCell(c);
                CellStyle cs = sh.getWorkbook().createCellStyle();
                cs.cloneStyleFrom(cell.getCellStyle());
                setThinBorders(cs);
                cell.setCellStyle(cs);
            }
        }
    }

    private void autosizeAllColumns(SXSSFSheet sh, int maxCols) {
        int cols = Math.min(maxCols, 50);
        for (int c = 0; c < cols; c++) {
            sh.trackColumnForAutoSizing(c);
        }
        for (int c = 0; c < cols; c++) {
            sh.autoSizeColumn(c);
            int w = sh.getColumnWidth(c);
            sh.setColumnWidth(c, w + 2 * 256);
        }
    }

    private List<String> getUbications(List<SedeDomiciliosRecord> sedes) {
        List<String> list = new ArrayList<>();
        for (SedeDomiciliosRecord sede : sedes) {
            String ubication = sede.sedeNombre() + ", " + sede.calle();
            ubication += ", no. Exterior " + sede.exterior();
            if (sede.interior() != null && !sede.interior().isEmpty())
                ubication += ", no. Interior " + sede.interior();
            if (sede.localidad() != null && !sede.localidad().isEmpty())
                ubication += ", localidad " + sede.localidad();
            if (sede.colonia() != null && !sede.colonia().isEmpty())
                ubication += ", colonia " + sede.colonia();
            if (sede.codigoPostal() != null && !sede.codigoPostal().trim().isEmpty())
                ubication += ", CP. " + sede.codigoPostal();
            ubication += ", " + sede.municipio() + ", " + sede.estadoRepublica();
            list.add(ubication);
        }
        return list.stream().distinct().toList();
    }
}

