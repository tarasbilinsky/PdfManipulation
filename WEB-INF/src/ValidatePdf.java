import java.io.*;
import java.lang.instrument.ClassDefinition;

import javax.servlet.*;
import javax.servlet.http.*;

import com.itextpdf.text.pdf.PdfReader;

@SuppressWarnings("serial")
public class ValidatePdf extends HttpServlet {
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{	  
	  response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      String inputFile = request.getParameter("inputFile");
      boolean res = false;
      Exception error = null;
      try {
			@SuppressWarnings("unused")
			PdfReader reader = new PdfReader(inputFile);
			res = true;
	  } catch (java.io.IOException e) {
 			if (
 					e.getMessage().equals("PDF header signature not found.")
 				||	e.getMessage().startsWith("Rebuild failed: trailer not found.")	
 			){
				res = false;
 			}
 			else{
 				error = e;
 			}
	  } catch (Exception e){
	   	error = e;
	  }
      
      if(error!=null){
    	  log(error.getLocalizedMessage());
      }
      out.println(error==null?(res?"1":"0"):error.getLocalizedMessage());
  }
}
