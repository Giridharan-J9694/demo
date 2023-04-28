import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1CFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class PdfToWordConverter {
    public static void main(String[] args) throws IOException {
        String pdfFilePath = "input.pdf";
        String docxFilePath = "output.docx";

        try (PDDocument pdf = PDDocument.load(new File(pdfFilePath))) {
            XWPFDocument docx = new XWPFDocument();
            for (PDPage page : pdf.getPages()) {
                String text = getText(page);
                XWPFParagraph paragraph = docx.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(text);
            }
            FileOutputStream out = new FileOutputStream(docxFilePath);
            docx.write(out);
            out.close();
        }
    }

    private static String getText(PDPage page) throws IOException {
        StringBuilder sb = new StringBuilder();
        PDResources resources = page.getResources();
        Iterable<COSName> fontNames = resources.getFontNames();

        for (COSName fontName : fontNames) {
            PDFont font = resources.getFont(fontName);
            if (font == null) {
                continue;
            }
            FontDescriptor fontDescriptor = font.getFontDescriptor();
            if (fontDescriptor == null) {
                continue;
            }
            float fontSize = fontDescriptor.getFontBoundingBox().getHeight() / 1000;
            for (TextPosition text : new PDFTextStripper().getTextPositions(page)) {
                if (font.equals(text.getFont())) {
                    String unicode = text.getUnicode();
                    sb.append(unicode);
                    if (fontDescriptor.isBold() && !unicode.contains("<b>")) {
                        sb.append("<b>");
                    }
                    if (fontDescriptor.isItalic() && !unicode.contains("<i>")) {
                        sb.append("<i>");
                    }
                    if ((!fontDescriptor.isBold()) && unicode.contains("<b>")) {
                        sb.append("</b>");
                    }
                    if ((!fontDescriptor.isItalic()) && unicode.contains("<i>")) {
                        sb.append("</i>");
                    }
                }
            }
        }
        return sb.toString();
    }
}
