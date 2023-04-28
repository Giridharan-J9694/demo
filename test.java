import java.io.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.text.*;
import org.apache.poi.xwpf.usermodel.*;

public class PdfToWordConverter {
    public static void main(String[] args) throws Exception {
        // Load the PDF file into a PDDocument object
        PDDocument document = PDDocument.load(new File("example.pdf"));

        // Create a new Word document
        XWPFDocument docx = new XWPFDocument();

        // Iterate through each page of the PDF document
        int numPages = document.getNumberOfPages();
        for (int i = 0; i < numPages; i++) {
            // Get the content of the current page as text using PDFTextStripper
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(i + 1);
            stripper.setEndPage(i + 1);
            String pageContent = stripper.getText(document);

            // Create a new paragraph in the Word document and add the page content to it
            XWPFParagraph paragraph = docx.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(pageContent);

            // Copy the font style of each character from the PDF to the Word document
            PDPage page = document.getPage(i);
            PDResources resources = page.getResources();
            for (COSName fontName : resources.getFontNames()) {
                PDFont font = resources.getFont(fontName);
                String fontFamily = font.getName();
                int fontSize = font.getFontDescriptor().getFontSize();
                for (TextPosition tp : stripper.getTextForPage(page).getCharacterList()) {
                    if (font.getName().equals(tp.getFont().getName())) {
                        run.setFontSize(tp.getFontSize());
                        run.setFontFamily(tp.getFont().getName());
                        run.setBold(tp.getFont().isBold());
                        run.setItalic(tp.getFont().isItalic());
                    }
                }
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
