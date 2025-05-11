package com.nihatkerembora.libraryapp.common.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;


public class PdfUtil {

    private static final float MARGIN_X = 50f;
    private static final float TITLE_Y_POSITION = 800f;
    private static final float HEADER_Y_POSITION = 770f;
    private static final float ROW_HEIGHT = 20f;
    private static final float PAGE_BOTTOM_MARGIN = 50f;
    private static final float CONTENT_WIDTH = 550f;

    private static final int FONT_TITLE_SIZE = 16;
    private static final int FONT_HEADER_SIZE = 12;
    private static final int FONT_ROW_SIZE = 11;

    private PdfUtil() {
    }

    public static <T> byte[] generatePdf(
            String title,
            List<ReportColumn<T>> columns,
            List<T> items
    ) {
        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            doc.addPage(page);

            // Başlık
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.setFont(PDType1Font.HELVETICA_BOLD, FONT_TITLE_SIZE);
                cs.beginText();
                cs.newLineAtOffset(MARGIN_X, TITLE_Y_POSITION);
                cs.showText(title);
                cs.endText();
            }

            float colWidth = CONTENT_WIDTH / columns.size();

            // Başlık satırları
            try (PDPageContentStream cs = new PDPageContentStream(doc, page,
                    PDPageContentStream.AppendMode.APPEND, true)) {
                cs.setFont(PDType1Font.HELVETICA_BOLD, FONT_HEADER_SIZE);
                float x = MARGIN_X;
                for (ReportColumn<T> col : columns) {
                    cs.beginText();
                    cs.newLineAtOffset(x, HEADER_Y_POSITION);
                    cs.showText(col.getHeader());
                    cs.endText();
                    x += colWidth;
                }
            }

            // İçerik
            float y = HEADER_Y_POSITION - ROW_HEIGHT;
            for (T item : items) {
                if (y < PAGE_BOTTOM_MARGIN) {
                    page = new PDPage();
                    doc.addPage(page);
                    y = TITLE_Y_POSITION - ROW_HEIGHT;
                }
                try (PDPageContentStream cs = new PDPageContentStream(doc, page,
                        PDPageContentStream.AppendMode.APPEND, true)) {
                    cs.setFont(PDType1Font.HELVETICA, FONT_ROW_SIZE);
                    float x = MARGIN_X;
                    for (ReportColumn<T> col : columns) {
                        cs.beginText();
                        cs.newLineAtOffset(x, y);
                        cs.showText(col.extract(item));
                        cs.endText();
                        x += colWidth;
                    }
                }
                y -= ROW_HEIGHT;
            }

            doc.save(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new UncheckedIOException("PDF oluşturulurken hata oluştu", new IOException(e));
        }
    }
}
