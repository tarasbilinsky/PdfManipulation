import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class GetFile extends HttpServlet {
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
	  InputStream is = null;
      try{
    	  String path = request.getParameter("path");
    	  
    	  is = Files.newInputStream(FileSystems.getDefault().getPath(path) , StandardOpenOption.READ);
  
    	  response.setContentType("application/octet-stream");
    	  ServletOutputStream out = response.getOutputStream();
    	  byte[] buffer = new byte[1024000];

    	  int bytesRead;
    	    while ((bytesRead = is.read(buffer)) != -1)
    	    {
    	        out.write(buffer, 0, bytesRead);
    	    }

    	    is.close();
    	    
      } catch (Exception e){
    	  log(e.getLocalizedMessage());
    	  if(is!=null) is.close();
      }
      
  }
}
