import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

@SuppressWarnings("serial")
public class FillFormPdf extends HttpServlet {
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    response.setContentType("text/html");
      
	String inputFile = request.getParameter("input_file");
	String outputFile = request.getParameter("output_file");
	
	Map<String,String> map = new HashMap<String,String>();
	int i  = 1;
	String f = null;
	do {
		f = request.getParameter("f"+i);
		if(f!=null){
			map.put(f, request.getParameter("v"+i));
		}
	} while (f!=null);
	 
    PrintWriter out = response.getWriter();

	try {
		final PdfReader reader = new PdfReader(inputFile);
	    final PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(outputFile));
	    final AcroFields form = stamp.getAcroFields();
	    map.forEach((x,v) -> {try{form.setField(x, v);} catch(IOException | DocumentException e){log(e.getLocalizedMessage());}});
	    stamp.setFormFlattening(true);
	    stamp.close();
		out.println("1");
	} catch (Exception error) {
		log(error.getLocalizedMessage());
		out.println(error.getLocalizedMessage());
	}     
  }
  
}
