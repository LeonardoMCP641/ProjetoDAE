package pt.ipleiria.estg.dei.ei.dae.publications.ai;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;

@ApplicationScoped
public class PdfTextExtractor {

    public String extractText(String pdfPath) {
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao extrair texto do PDF", e);
        }
    }
}
