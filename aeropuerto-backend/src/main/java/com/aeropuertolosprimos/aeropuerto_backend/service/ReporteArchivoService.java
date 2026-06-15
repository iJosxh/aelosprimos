package com.aeropuertolosprimos.aeropuerto_backend.service;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReporteArchivoService {

    private static final Color COLOR_AZUL_OSCURO = new Color(15, 23, 42);
    private static final Color COLOR_AZUL = new Color(30, 64, 175);
    private static final Color COLOR_GRIS_CLARO = new Color(241, 245, 249);
    private static final Color COLOR_GRIS_BORDE = new Color(203, 213, 225);
    private static final Color COLOR_TEXTO = new Color(51, 65, 85);
    private static final Color COLOR_BLANCO = Color.WHITE;

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generarPdf(String titulo, List<String> encabezados, List<List<String>> filas) {
        try {
            ByteArrayOutputStream salida = new ByteArrayOutputStream();

            Rectangle pagina = encabezados.size() > 4 ? PageSize.LETTER.rotate() : PageSize.LETTER;
            Document documento = new Document(pagina, 36, 36, 36, 55);

            PdfWriter writer = PdfWriter.getInstance(documento, salida);
            writer.setPageEvent(new FooterPdf());

            documento.open();

            agregarEncabezadoPdf(documento);
            agregarTituloPdf(documento, titulo);
            agregarResumenPdf(documento);
            agregarTablaPdf(documento, encabezados, filas);
            agregarFirmaPdf(documento);

            documento.close();

            return salida.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("No se pudo generar el archivo PDF.");
        }
    }

    public byte[] generarExcel(String nombreHoja, List<String> encabezados, List<List<String>> filas) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

            String nombreSeguro = limpiarNombreHoja(nombreHoja);
            var sheet = workbook.createSheet(nombreSeguro);

            CellStyle estiloTitulo = crearEstiloTituloExcel(workbook);
            CellStyle estiloSubtitulo = crearEstiloSubtituloExcel(workbook);
            CellStyle estiloEncabezado = crearEstiloEncabezadoExcel(workbook);
            CellStyle estiloCelda = crearEstiloCeldaExcel(workbook);

            int totalColumnas = Math.max(encabezados.size(), 2);

            Row filaTitulo = sheet.createRow(0);
            filaTitulo.setHeightInPoints(28);

            Cell celdaTitulo = filaTitulo.createCell(0);
            celdaTitulo.setCellValue(nombreHoja);
            celdaTitulo.setCellStyle(estiloTitulo);

            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, totalColumnas - 1));

            Row filaFecha = sheet.createRow(1);
            Cell celdaFecha = filaFecha.createCell(0);
            celdaFecha.setCellValue("Generado: " + LocalDateTime.now().format(FORMATO_FECHA));
            celdaFecha.setCellStyle(estiloSubtitulo);

            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, totalColumnas - 1));

            Row filaEncabezados = sheet.createRow(3);

            for (int i = 0; i < encabezados.size(); i++) {
                Cell celda = filaEncabezados.createCell(i);
                celda.setCellValue(valor(encabezados.get(i)));
                celda.setCellStyle(estiloEncabezado);
            }

            int filaActual = 4;

            for (List<String> fila : filas) {
                Row row = sheet.createRow(filaActual++);

                for (int i = 0; i < fila.size(); i++) {
                    Cell celda = row.createCell(i);
                    celda.setCellValue(valor(fila.get(i)));
                    celda.setCellStyle(estiloCelda);
                }
            }

            for (int i = 0; i < totalColumnas; i++) {
                sheet.autoSizeColumn(i);
                int anchoActual = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, Math.min(anchoActual + 1200, 12000));
            }

            workbook.write(salida);

            return salida.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("No se pudo generar el archivo Excel.");
        }
    }

    private void agregarEncabezadoPdf(Document documento) throws Exception {
        PdfPTable tabla = new PdfPTable(new float[]{1.2f, 5.8f});
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(18);

        PdfPCell celdaLogo = new PdfPCell();
        celdaLogo.setBorder(Rectangle.NO_BORDER);
        celdaLogo.setBackgroundColor(COLOR_AZUL);
        celdaLogo.setPadding(12);

        Paragraph logo = new Paragraph("ALP", fuente(22, Font.BOLD, COLOR_BLANCO));
        logo.setAlignment(Element.ALIGN_CENTER);

        Paragraph logoTexto = new Paragraph("Aeropuerto", fuente(8, Font.NORMAL, COLOR_BLANCO));
        logoTexto.setAlignment(Element.ALIGN_CENTER);

        celdaLogo.addElement(logo);
        celdaLogo.addElement(logoTexto);

        PdfPCell celdaInfo = new PdfPCell();
        celdaInfo.setBorder(Rectangle.NO_BORDER);
        celdaInfo.setPaddingLeft(14);
        celdaInfo.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph nombre = new Paragraph("Aeropuerto Los Primos", fuente(20, Font.BOLD, COLOR_AZUL_OSCURO));
        nombre.setSpacingAfter(4);

        Paragraph descripcion = new Paragraph(
                "Sistema de gestión aeroportuaria | Consulta pública de vuelos",
                fuente(10, Font.NORMAL, COLOR_TEXTO)
        );

        Paragraph fecha = new Paragraph(
                "Fecha de generación: " + LocalDateTime.now().format(FORMATO_FECHA),
                fuente(9, Font.NORMAL, COLOR_TEXTO)
        );

        celdaInfo.addElement(nombre);
        celdaInfo.addElement(descripcion);
        celdaInfo.addElement(fecha);

        tabla.addCell(celdaLogo);
        tabla.addCell(celdaInfo);

        documento.add(tabla);
    }

    private void agregarTituloPdf(Document documento, String titulo) throws Exception {
        Paragraph tituloReporte = new Paragraph(titulo, fuente(17, Font.BOLD, COLOR_AZUL_OSCURO));
        tituloReporte.setAlignment(Element.ALIGN_LEFT);
        tituloReporte.setSpacingAfter(8);

        Paragraph subtitulo = new Paragraph(
                "Detalle generado a partir de la consulta realizada.",
                fuente(10, Font.NORMAL, COLOR_TEXTO)
        );
        subtitulo.setSpacingAfter(14);

        documento.add(tituloReporte);
        documento.add(subtitulo);
    }

    private void agregarResumenPdf(Document documento) throws Exception {
        PdfPTable tabla = new PdfPTable(1);
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(12);

        PdfPCell celda = new PdfPCell(new Phrase(
                "Documento informativo emitido por el Aeropuerto Los Primos. " +
                        "La información corresponde al estado registrado en el sistema al momento de la consulta.",
                fuente(9, Font.NORMAL, COLOR_TEXTO)
        ));

        celda.setPadding(10);
        celda.setBackgroundColor(COLOR_GRIS_CLARO);
        celda.setBorderColor(COLOR_GRIS_BORDE);

        tabla.addCell(celda);

        documento.add(tabla);
    }

    private void agregarTablaPdf(Document documento, List<String> encabezados, List<List<String>> filas) throws Exception {
        PdfPTable tabla = new PdfPTable(encabezados.size());
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(4);
        tabla.setSpacingAfter(20);

        if (encabezados.size() == 2) {
            tabla.setWidths(new float[]{2.2f, 5.8f});
        }

        for (String encabezado : encabezados) {
            PdfPCell celda = crearCeldaPdf(
                    encabezado,
                    fuente(10, Font.BOLD, COLOR_BLANCO),
                    COLOR_AZUL_OSCURO
            );

            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(celda);
        }

        int contador = 0;

        for (List<String> fila : filas) {
            Color fondo = contador % 2 == 0 ? Color.WHITE : COLOR_GRIS_CLARO;

            for (int i = 0; i < fila.size(); i++) {
                Font fuenteCelda = i == 0
                        ? fuente(9, Font.BOLD, COLOR_AZUL_OSCURO)
                        : fuente(9, Font.NORMAL, COLOR_TEXTO);

                PdfPCell celda = crearCeldaPdf(valor(fila.get(i)), fuenteCelda, fondo);
                tabla.addCell(celda);
            }

            contador++;
        }

        documento.add(tabla);
    }

    private PdfPCell crearCeldaPdf(String texto, Font fuente, Color fondo) {
        PdfPCell celda = new PdfPCell(new Phrase(valor(texto), fuente));
        celda.setPadding(8);
        celda.setBackgroundColor(fondo);
        celda.setBorderColor(COLOR_GRIS_BORDE);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return celda;
    }

    private void agregarFirmaPdf(Document documento) throws Exception {
        PdfPTable tabla = new PdfPTable(new float[]{3.5f, 2.5f});
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(18);

        PdfPCell vacia = new PdfPCell(new Phrase(""));
        vacia.setBorder(Rectangle.NO_BORDER);
        tabla.addCell(vacia);

        PdfPCell firma = new PdfPCell();
        firma.setBorder(Rectangle.NO_BORDER);
        firma.setPaddingTop(20);

        Paragraph linea = new Paragraph("______________________________", fuente(10, Font.NORMAL, COLOR_AZUL_OSCURO));
        linea.setAlignment(Element.ALIGN_CENTER);

        Paragraph texto = new Paragraph("Firma / Sello Aeropuerto Los Primos", fuente(9, Font.NORMAL, COLOR_TEXTO));
        texto.setAlignment(Element.ALIGN_CENTER);

        Paragraph nota = new Paragraph("Documento generado por el sistema", fuente(8, Font.ITALIC, COLOR_TEXTO));
        nota.setAlignment(Element.ALIGN_CENTER);

        firma.addElement(linea);
        firma.addElement(texto);
        firma.addElement(nota);

        tabla.addCell(firma);

        documento.add(tabla);
    }

    private Font fuente(float tamano, int estilo, Color color) {
        return FontFactory.getFont(
                FontFactory.HELVETICA,
                BaseFont.CP1252,
                false,
                tamano,
                estilo,
                color
        );
    }

    private static class FooterPdf extends PdfPageEventHelper {

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte contenido = writer.getDirectContent();

            Font fuenteFooter = FontFactory.getFont(
                    FontFactory.HELVETICA,
                    BaseFont.CP1252,
                    false,
                    8,
                    Font.NORMAL,
                    COLOR_TEXTO
            );

            Phrase footer = new Phrase(
                    "Aeropuerto Los Primos | Página " + writer.getPageNumber(),
                    fuenteFooter
            );

            ColumnText.showTextAligned(
                    contenido,
                    Element.ALIGN_CENTER,
                    footer,
                    (document.right() + document.left()) / 2,
                    document.bottom() - 22,
                    0
            );
        }
    }

    private CellStyle crearEstiloTituloExcel(XSSFWorkbook workbook) {
        XSSFCellStyle estilo = workbook.createCellStyle();
        estilo.setAlignment(HorizontalAlignment.CENTER);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
        estilo.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont fuente = workbook.createFont();
        fuente.setBold(true);
        fuente.setFontHeightInPoints((short) 16);
        fuente.setColor(IndexedColors.WHITE.getIndex());

        estilo.setFont(fuente);

        return estilo;
    }

    private CellStyle crearEstiloSubtituloExcel(XSSFWorkbook workbook) {
        XSSFCellStyle estilo = workbook.createCellStyle();
        estilo.setAlignment(HorizontalAlignment.LEFT);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFFont fuente = workbook.createFont();
        fuente.setItalic(true);
        fuente.setFontHeightInPoints((short) 10);
        fuente.setColor(IndexedColors.GREY_50_PERCENT.getIndex());

        estilo.setFont(fuente);

        return estilo;
    }

    private CellStyle crearEstiloEncabezadoExcel(XSSFWorkbook workbook) {
        XSSFCellStyle estilo = workbook.createCellStyle();
        estilo.setAlignment(HorizontalAlignment.CENTER);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
        estilo.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);

        XSSFFont fuente = workbook.createFont();
        fuente.setBold(true);
        fuente.setColor(IndexedColors.WHITE.getIndex());

        estilo.setFont(fuente);

        return estilo;
    }

    private CellStyle crearEstiloCeldaExcel(XSSFWorkbook workbook) {
        XSSFCellStyle estilo = workbook.createCellStyle();
        estilo.setAlignment(HorizontalAlignment.LEFT);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
        estilo.setWrapText(true);
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);

        XSSFFont fuente = workbook.createFont();
        fuente.setFontHeightInPoints((short) 10);

        estilo.setFont(fuente);

        return estilo;
    }

    private String limpiarNombreHoja(String nombreHoja) {
        if (nombreHoja == null || nombreHoja.isBlank()) {
            return "Reporte";
        }

        String limpio = nombreHoja
                .replace("[", "")
                .replace("]", "")
                .replace("*", "")
                .replace("/", "")
                .replace("\\", "")
                .replace("?", "")
                .replace(":", "");

        if (limpio.length() > 31) {
            return limpio.substring(0, 31);
        }

        return limpio;
    }

    private String valor(String texto) {
        return texto == null ? "" : texto;
    }
}
