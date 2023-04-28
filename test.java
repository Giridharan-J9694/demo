import java.io.*;
import java.util.List;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.text.*;
import org.apache.poi.xwpf.usermodel.*;

public class PdfToWordConverter {
    public static void main(String[] args) throws Exception {
        // Load the PDF file into a PDDocument object
        PDDocument document = PDDocument.load(new File("example.pdf"));

        // Create a new Word document
        XWPFDocument docx = new XWPFDocument();

        // Create a PDFTextStripper object and set the start and end page numbers
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(1);
        stripper.setEndPage(document.getNumberOfPages());

        // Get the text content of the PDF document
        String text = stripper.getText(document);

        // Create a list of paragraphs based on the PDF text content
        List<String> paragraphs = List.of(text.split("\\r?\\n\\r?\\n"));

        // Iterate through each paragraph and add it to the Word document
        for (String paragraphText : paragraphs) {
            // Create a new paragraph in the Word document and add the paragraph text to it
            XWPFParagraph paragraph = docx.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(paragraphText);

            // Set the font style of the paragraph based on the PDF text style
            List<TextPosition> textPositions = stripper.getCharactersByArticle();
            if (textPositions != null && textPositions.size() > 0) {
                TextPosition firstChar = textPositions.get(0);
                run.setFontFamily(firstChar.getFont().getName());
                run.setFontSize((int) firstChar.getFontSizeInPt());
                run.setBold(firstChar.getFont().isBold());
                run.setItalic(firstChar.getFont().isItalic());
            }
        }

        // Save the Word document
        FileOutputStream out = new FileOutputStream("example.docx");
        docx.write(out);
        out.close();

        // Close the PDF document
        document.close();

        System.out.println("PDF converted to Word successfully.");
    }
}
