package mx.gob.pjpuebla.trials.statistics.reports;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final CarpetaRepository carpetaRepository;
    private final DataSource dataSource;


    public List<ReporteDatesRecord> getDatesByMateria() {
        List<ReporteDatesRecord> response = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        List<ReporteDateRecord> list = reporteRepository.getAllKeys();
        for (ReporteDateRecord record : list) {
            ExtraData data = mapper.convertValue(record.extraData(), ExtraData.class);
            if (data.getMaterias() != null && !data.getMaterias().isEmpty()) {
                if (data.getJuiciosExcluidos() == null || data.getJuiciosExcluidos().isEmpty()) {
                    data.setJuiciosExcluidos(Arrays.asList(0));
                }
                ReporteDatesRecord result = new ReporteDatesRecord(record.reporte(), carpetaRepository.getDatesByMateria(data.getMaterias(), data.getJuiciosExcluidos()));
                response.add(result);
            } else if (data.getTipoJuicios() != null && !data.getTipoJuicios().isEmpty()) {
                ReporteDatesRecord result = new ReporteDatesRecord(record.reporte(), carpetaRepository.getDatesByTipoJuicio(data.getTipoJuicios()).toLocalDate());
                response.add(result);
            }
        }
        return response;
    }

    public byte[] exportarBytes() throws Exception {
        String sql = """
                SELECT *
                FROM trials.reporte_divorcios
                WHERE make_date("AÑO_DEM"::int, "MES_DEM"::int, "DIA_DEM"::int)
                      BETWEEN ? AND ?
                ORDER BY make_date("AÑO_MAT"::int, "MES_MAT"::int, "DIA_MAT"::int)
                """;

        try (Connection conn = dataSource.getConnection()) {
            // Para que fetchSize funcione en PostgreSQL (cursor en servidor):
            conn.setAutoCommit(false);
            LocalDate inicio = LocalDate.of(2025, 8, 01);
            LocalDate fin = LocalDate.of(2025, 8, 12);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDate(1, java.sql.Date.valueOf(inicio));
                ps.setDate(2, java.sql.Date.valueOf(fin));
                ps.setFetchSize(1_000); // ajusta según volumen

                try (ResultSet rs = ps.executeQuery();
                     SXSSFWorkbook wb = new SXSSFWorkbook(1_000);
                     ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

                    wb.setCompressTempFiles(true);
                    Sheet sheet = wb.createSheet("DIVORCIOS_"+inicio.toString().replace("-","") +"_"+fin.toString().replace("-", ""));

                    ResultSetMetaData md = rs.getMetaData();
                    int cols = md.getColumnCount();

                    // —— Estilo de encabezado ——
                    CellStyle headerStyle = wb.createCellStyle();

                    // Color de fondo personalizado #8C92BC
                    XSSFColor customColor = new XSSFColor(new java.awt.Color(0x8C, 0x92, 0xBC), null);

                    // === Estilo del header ===
                    // Aplicar color de fondo sólido
                    ((org.apache.poi.xssf.usermodel.XSSFCellStyle) headerStyle)
                            .setFillForegroundColor(customColor);
                    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                    // Texto centrado horizontal y vertical
                    headerStyle.setAlignment(HorizontalAlignment.CENTER);
                    headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                    // Bordes opcionales
                    headerStyle.setBorderBottom(BorderStyle.THIN);
                    headerStyle.setBorderTop(BorderStyle.THIN);
                    headerStyle.setBorderLeft(BorderStyle.THIN);
                    headerStyle.setBorderRight(BorderStyle.THIN);
                    // Fuente en negrita y blanca (para que contraste con el fondo)
                    XSSFFont headerFont = (XSSFFont) wb.createFont();
                    headerFont.setBold(true); // estándar POI
                    headerFont.getCTFont().addNewB();  // <b/> en OOXML
                    headerStyle.setFont(headerFont);
                    headerStyle.setBorderBottom(BorderStyle.THIN);
                    headerFont.setColor(IndexedColors.WHITE.getIndex()); // cambia a WHITE si el fondo es oscuro


                    // —— Header ——
                    Row header = sheet.createRow(0);
                    header.setHeightInPoints(43);
                    for (int c = 1; c <= cols; c++) {
                        Cell cell = header.createCell(c - 1);
                        cell.setCellValue(md.getColumnLabel(c));
                        cell.setCellStyle(headerStyle);
                    }

                    // Freeze
                    sheet.createFreezePane(0, 1);

                    // —— Tracking parcial para autosize ——
                    // Ajusta este arreglo con los índices de columnas que quieres ajustar automáticamente (0-based).
                    int[] colsParaAutoSize = new int[]{
                            1, 2, 3, 4,   //  MPIO_REG, TIPO_JUZGADO, NuM_JUZGADO, NuM_EXPDIV
                            5, 6, 7, 8, 9    // CVE_EST, NuM_ACTAMAT, ENT_REGMAT, MPIO_REGMAT, LOC_REGMAT
                    };
                    org.apache.poi.xssf.streaming.SXSSFSheet sxssfSheet =
                            (org.apache.poi.xssf.streaming.SXSSFSheet) sheet;
                    for (int colIdx : colsParaAutoSize) {
                        sxssfSheet.trackColumnForAutoSizing(colIdx);
                    }

                    // antes de recorrer el ResultSet
                    CellStyle dataStyle = wb.createCellStyle();
                    dataStyle.setAlignment(HorizontalAlignment.CENTER);
                    dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    // Bordes opcionales
                    dataStyle.setBorderBottom(BorderStyle.THIN);
                    dataStyle.setBorderTop(BorderStyle.THIN);
                    dataStyle.setBorderLeft(BorderStyle.THIN);
                    dataStyle.setBorderRight(BorderStyle.THIN);

                    // —— Datos ——
                    int r = 1;
                    while (rs.next()) {
                        Row row = sheet.createRow(r++);
                        for (int c = 1; c <= cols; c++) {
                            Cell cell = row.createCell(c - 1);
                            Object v = rs.getObject(c);
                            if (v == null) cell.setBlank();
                            else if (v instanceof Number n) cell.setCellValue(n.doubleValue());
                            else cell.setCellValue(String.valueOf(v));

                            cell.setCellStyle(dataStyle); // <- centra toda la hoja
                        }
                    }
                    //sheet.setDefaultColumnWidth(10);
                    // —— Autosize solo en las columnas rastreadas ——
                    for (int colIdx : colsParaAutoSize) {
                        // Evita excepción si el índice está fuera de rango
                        if (colIdx >= 0 && colIdx < cols) {
                            sheet.autoSizeColumn(colIdx);
                            int w = sheet.getColumnWidth(colIdx);      // unidades de 1/256 de carácter
                            int paddingChars = 2;                       // “2 caracteres” extra
                            sheet.setColumnWidth(colIdx, w + paddingChars * 256);
                        }
                    }

                    wb.write(bos);
                    wb.dispose(); // limpia temporales de SXSSF
                    return bos.toByteArray();
                }
            }
        }
    }
}

