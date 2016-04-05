import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.*;
import javax.servlet.http.*;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.exceptions.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

@SuppressWarnings("serial")
public class MergePdf extends HttpServlet {
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    response.setContentType("text/html");
      
	String outputFile = request.getParameter("output_file");
	int rotatePDFDocNum = 0;
	try{
		rotatePDFDocNum = Integer.parseInt(request.getParameter("rotate_pdf_doc_num"));
	} catch(NumberFormatException e){}

	String pdfs_list = request.getParameter("pdfs_list");
	StringTokenizer st = new StringTokenizer(pdfs_list,",");
	ArrayList<String> pdfs_arr = new ArrayList<String>();

    while (st.hasMoreTokens()){
    	pdfs_arr.add(st.nextToken());
    }
	 
    PrintWriter out = response.getWriter();

	try {
		processPDFs(pdfs_arr.toArray(new String[pdfs_arr.size()]), outputFile, false, rotatePDFDocNum);
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
  
  public void processPDFs(String[] input_files, String output_file, boolean paginate, int rotatePDFDocNum) throws IOException, DocumentException{
 	OutputStream output = new FileOutputStream(output_file);

		List<ConvertStruct> convList = new ArrayList<ConvertStruct>();
		for (int i=0; i<input_files.length; i++){
			convList.add(this.new ConvertStruct(new File(input_files[i])));
		}
		
		convert(convList);
		List<InputStream> inputStreams = makeStreamsList (convList);
		
		concatPDFs(inputStreams, output, paginate, rotatePDFDocNum);
		
		deleteTempFiles(convList);

	 }

	 public void concatPDFs(List<InputStream> streamOfPDFFiles, OutputStream outputStream, boolean paginate, int rotatePDFDocNum) {
		    Document document = new Document();
		    try {
		      List<InputStream> pdfs = streamOfPDFFiles;
		      List<PdfReader> readers = new ArrayList<PdfReader>();
		      int totalPages = 0;
		      Iterator<InputStream> iteratorPDFs = pdfs.iterator();

		      // Create Readers for the pdfs.
		      while (iteratorPDFs.hasNext()) {
		        InputStream pdf = iteratorPDFs.next();
		        PdfReader pdfReader = new PdfReader(pdf);
		        readers.add(pdfReader);
		        totalPages += pdfReader.getNumberOfPages();
		      }
		      // Create a writer for the outputstream
		      PdfWriter writer = PdfWriter.getInstance(document, outputStream);

		      //set size of first page; must be before document.open 
		      document.setPageSize(readers.get(0).getPageSize(1));

		      document.open();
		      BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		      PdfContentByte cb = writer.getDirectContent(); // Holds the PDF
		      // data

		      PdfImportedPage page;
		      int currentPageNumber = 0;
		      int pageOfCurrentReaderPDF = 0;
		      int pdfNum = 0;
		      Iterator<PdfReader> iteratorPDFReader = readers.iterator();

		      // Loop through the PDF files and add to the output.
		      while (iteratorPDFReader.hasNext()) {
		        pdfNum++;
		        PdfReader pdfReader = iteratorPDFReader.next();

		        // Create a new page in the target for each source page.
		        while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {
			      com.itextpdf.text.Rectangle page_size = pdfReader.getPageSize(pageOfCurrentReaderPDF+1);
		          if (rotatePDFDocNum !=0 && pdfNum == rotatePDFDocNum){
		        	  document.setPageSize(page_size.rotate());
		          }
		          else{
		        	  document.setPageSize(page_size);
		          }
		          document.newPage();
		          pageOfCurrentReaderPDF++;
		          currentPageNumber++;
		          page = writer.getImportedPage(pdfReader, pageOfCurrentReaderPDF);
		          if (rotatePDFDocNum !=0 && pdfNum == rotatePDFDocNum){
		        	  cb.addTemplate(page, 0, -1, 1, 0, 0, page_size.getWidth());
		          }
		          else{
		        	  cb.addTemplate(page, 0, 0);
		          }

		          // Code for pagination.
		          if (paginate) {
		            cb.beginText();
		            cb.setFontAndSize(bf, 9);
		            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "" + currentPageNumber + " of " + totalPages, 520, 5, 0);
		            cb.endText();
		          }
		        }
		        pageOfCurrentReaderPDF = 0;
		      }
		      outputStream.flush();
		      document.close();
		      outputStream.close();
		    } catch (Exception e) {
		    	log(e.getLocalizedMessage());
		    } finally {
		      if (document.isOpen())
		        document.close();
		      try {
		        if (outputStream != null)
		          outputStream.close();
		      } catch (IOException ioe) {
		        log(ioe.getLocalizedMessage());
		      }
		    }
	 }		 

	 public class ConvertStruct{
		public File sourceFile;
		public File convertedFile;
		public boolean isTempFile = false;
        public boolean skip = false;

	 	public ConvertStruct(File sourceFile){
	 		this.sourceFile=sourceFile;
	 	}		 
	
		public void convert() throws IOException, DocumentException{
			InputStream in = new FileInputStream (sourceFile);
            PdfReader formReader = null;
            try {
                formReader = new PdfReader(in);
            } catch (Exception e) {
                log(e.getLocalizedMessage());
                this.skip = true;
                return;
            }
            try{
                if (formReader.getAcroForm() != null){
                    this.convertedFile = File.createTempFile("pdfs_merge","");
                    this.isTempFile = true;
                    try {
                        PdfStamper stamp = new PdfStamper(formReader, new FileOutputStream(this.convertedFile));
                        stamp.setFormFlattening(true);
                        stamp.close();
                    } catch (Exception e){
                        this.convertedFile = this.sourceFile;
                        log(e.getLocalizedMessage());
                    }
                }
                else{
                    this.convertedFile = this.sourceFile;
                }
            } catch (UnsupportedPdfException e) {
                this.skip = true;
            } catch (InvalidPdfException e) {
                this.skip = true;
            } catch (InvalidImageException e) {
                this.skip = true;
            } catch (BadPasswordException e) {
                this.skip = true;
            } catch (IllegalPdfSyntaxException e){
                this.skip = true;
            }

        }
	 }
		 
	public void convert(List<ConvertStruct> convList) throws IOException, DocumentException{
	    Iterator<ConvertStruct> it = convList.iterator();
	    while (it.hasNext()) {
	    	ConvertStruct conv = it.next();
	    	conv.convert();
	    }
	}
		 
	public List<InputStream> makeStreamsList(List<ConvertStruct> convList) throws FileNotFoundException{
	    Iterator<ConvertStruct> it = convList.iterator();
	    List<InputStream> streams = new ArrayList<InputStream>();
	    while (it.hasNext()) {
	    	ConvertStruct conv = it.next();
	    	if(!conv.skip) streams.add(new FileInputStream(conv.convertedFile));
	    }
	    return streams;
	}
	
	public void deleteTempFiles(List<ConvertStruct> convList){
	    Iterator<ConvertStruct> it = convList.iterator();
	    while (it.hasNext()) {
	    	ConvertStruct conv = it.next();
	    	if (conv.isTempFile){
	    		conv.convertedFile.delete();
	    	}
	    }
	}
  
}
