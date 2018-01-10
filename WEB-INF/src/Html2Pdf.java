import com.itextpdf.text.*;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.TextMarginFinder;
//import com.itextpdf.tool.xml.XMLWorkerHelper;


public class Html2Pdf extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        String outputFile = request.getParameter("output_file");
        String html = request.getParameter("html");
        String headerpdf = request.getParameter("headerpdf");

        PrintWriter out = response.getWriter();

        try {
            html2Pdf(headerpdf,html,outputFile);
            out.println("1");
        } catch (Exception error) {
            log(error.getLocalizedMessage());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            error.printStackTrace(pw);
            out.println(error.getClass().getName()+": "+sw.toString());
            log(error.getLocalizedMessage()+"\n\n"+sw.toString());
        }
    }

    private void html2Pdf(String headerpdf, String html, String outputFile) throws DocumentException, IOException{


        InputStream page1 = new FileInputStream(headerpdf);


        class PdfCreatorInner extends PdfPageEventHelper {
            float marginTop, marginBottom,marginLeft,marginRight;
            PdfImportedPage page1=null;
            PdfTemplate total, page1Footer;

            PdfCreatorInner(InputStream page1){
                try{
                    OutputStream resultOs = new FileOutputStream(new File(outputFile));
                    PdfReader reader = new PdfReader(page1);
                    Rectangle pageSize = reader.getPageSize(1);
                    PdfReaderContentParser parser = new PdfReaderContentParser(reader);
                    TextMarginFinder finder = parser.processContent(1, new TextMarginFinder());
                    marginLeft = 30;
                    marginRight = 30;
                    float tY = pageSize.getTop();
                    float dY = tY-70;
                    try{
                        dY = finder.getUry()-finder.getHeight();
                    } catch (Exception e) {//ignore
                    }
                    marginTop = tY-dY+10;
                    marginBottom = 30;

                    Document document = new Document(pageSize,marginLeft,marginRight,marginTop,marginBottom);
                    PdfWriter writer = PdfWriter.getInstance(document, resultOs);
                    writer.setPageEvent(this);
                    this.page1 = writer.getImportedPage(reader, 1);




                    marginTop = 50;


                    document.open();

                    total = writer.getDirectContent().createTemplate(10, 12);
                    page1Footer = writer.getDirectContent().createTemplate(55,12);

                    writer.setMargins(0, 0, 0, 0);

                    InputStream is = new ByteArrayInputStream(html.getBytes());
                    //XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
                    document.close();

                    reader.close();

                    resultOs.close();

                }
                catch (DocumentException e) {throw new RuntimeException(e);}
                catch (IOException e) 		{throw new RuntimeException(e);}
            }

            public void onStartPage(PdfWriter writer, Document document) {
                int pageNumber = writer.getPageNumber();
                if(pageNumber==1){
                    writer.getDirectContentUnder().addTemplate(page1, 0, 0);
                } else {

                }
            }
            public void onEndPage(PdfWriter writer, Document document){
                int pageNumber = writer.getPageNumber();

                PdfTemplate tmp = null;
                PdfPTable table = new PdfPTable(new float[]{24,1});
                float r = document.right()-document.rightMargin();
                table.setTotalWidth(r);
                table.setLockedWidth(true);
                table.getDefaultCell().setFixedHeight(10);
                PdfPCell c;


                if(pageNumber==1){
                    document.setMargins(marginLeft, marginRight, marginTop, marginBottom);
                    try {
                        c = new PdfPCell(Image.getInstance(page1Footer));
                    } catch (BadElementException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    tmp = writer.getDirectContent().createTemplate(55, 12);
                    try {
                        c = new PdfPCell(Image.getInstance(tmp));
                    } catch (BadElementException e) {
                        throw new RuntimeException(e);
                    }
                }

                c.setHorizontalAlignment(Element.ALIGN_RIGHT);
                c.setPaddingLeft(0);
                c.setPaddingRight(0);
                c.setPaddingTop(5);
                c.setBorder(Rectangle.TOP);
                table.addCell(c);

                try {
                    c = new PdfPCell(Image.getInstance(total));
                } catch (BadElementException e) {
                    throw new RuntimeException(e);
                }
                c.setHorizontalAlignment(Element.ALIGN_LEFT);
                c.setPaddingLeft(0);
                c.setPaddingRight(0);
                c.setPaddingTop(5);
                c.setBorder(Rectangle.TOP);
                table.addCell(c);


                table.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin()+5, writer.getDirectContent());
                if(pageNumber>1){
                    ColumnText.showTextAligned(tmp, Element.ALIGN_LEFT, new Phrase(String.format("Page %s of",pageNumber)),0,3,0);
                }
            }

            public void onCloseDocument(PdfWriter writer, Document document){
                int totalPageNumbers = writer.getPageNumber()-1;
                if(totalPageNumbers>1){
                    ColumnText.showTextAligned(total, Element.ALIGN_LEFT, new Phrase(""+totalPageNumbers), 0, 3, 0);
                    ColumnText.showTextAligned(page1Footer, Element.ALIGN_LEFT, new Phrase("Page 1 of"), 0, 3, 0);
                }
            }


        }


        new PdfCreatorInner(page1);






    }




}
