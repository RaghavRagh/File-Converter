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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("api/convert")
public class FileConverter {

    @PostMapping("/convert-to-pdf")
    public ResponseEntity<ConvertedFile> convertWordToPdf(@RequestParam("file") MultipartFile wordFile) {
        try {
            byte[] pdfData = convertWordToPdfBytes(wordFile.getInputStream());
            ConvertedFile convertedFile = new ConvertedFile("converted.pdf", "application/pdf", pdfData);
            return ResponseEntity.ok(convertedFile);
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

                for (XWPFParagraph paragraph : document.getParagraphs()) {
                    for (XWPFRun run : paragraph.getRuns()) {
                        contentStream.newLineAtOffset(100, 700); // Adjust position as needed
                        contentStream.showText(run.getText(0));
                    }
                }

                contentStream.endText();
            }

            pdfDocument.save(out);
        }

        return out.toByteArray();
    }
}
