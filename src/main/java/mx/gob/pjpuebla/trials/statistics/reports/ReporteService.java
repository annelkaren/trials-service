package mx.gob.pjpuebla.trials.statistics.reports;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.gob.pjpuebla.trials.workflow.carpeta.CarpetaRepository;
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
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ReporteService {

    private static final Logger logger = LoggerFactory.getLogger(ReporteService.class);
    private final ReporteRepository reporteRepository;
    private final CarpetaRepository carpetaRepository;
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
}

