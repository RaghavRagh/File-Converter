package com.fileconvert.fileconvert.controller;

import com.fileconvert.fileconvert.ConvertedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("api/convert")
public class FileConverter {

    @PostMapping(value = "/convert-to-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> convertWordToPdf(@RequestParam("file") MultipartFile wordFile) {
        try {
            byte[] pdfData = convertWordToPdfBytes(wordFile.getInputStream());
            ConvertedFile convertedFile = new ConvertedFile("converted.pdf", "application/pdf", pdfData);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdfData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private byte[] convertWordToPdfBytes(InputStream inputStream) throws IOException {
        XWPFDocument document = new XWPFDocument(inputStream);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PDDocument pdfDocument = new PDDocument()) {
            PDPage page = new PDPage();
            pdfDocument.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);

                float y = page.getMediaBox().getHeight() - 50;

                for (XWPFParagraph paragraph : document.getParagraphs()) {
                    for (XWPFRun run : paragraph.getRuns()) {
                        String text = run.getText(0);
                        if (text != null) {
                            contentStream.newLineAtOffset(50, y);
                            contentStream.showText(text);
                            y -= 12; // Adjust for next line
                        }
                    }
                }

                contentStream.endText();
            }

            pdfDocument.save(out);
        }

        return out.toByteArray();
    }
}
