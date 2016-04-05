import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;


@SuppressWarnings("serial")
public class ImagesPdf extends HttpServlet {
	
  enum Positioning{
	  Plain,Four,Six;
	  static Positioning create(int i){
		  switch(i){
		  case 1: return Plain;
		  case 4: return Four;
		  case 6: return Six;
		  }
		  return null;
	  }

	public int imagesMarginN() {
		  switch(this){
		  case Four: return 1;
		  case Six: return 1;
		default:
			break;
		  }
		  return 0;
	}

	public float columns() {
		  switch(this){
		  case Four: return 2;
		  case Six: return 2;
		default:
			break;
		  }		return 1;
	}

	public float rows() {
		  switch(this){
		  case Four: return 2;
		  case Six: return 3;
		default:
			break;
		  }		return 1;
	}

	public void scale(Image jpg, Rectangle page_size, int margin, int images_margin, int pos) {

	      float image_fit_width = (page_size.getWidth() - 2*margin - this.imagesMarginN()*images_margin)/columns();
	      float image_fit_height = (page_size.getHeight() - 2*margin - this.imagesMarginN()*images_margin)/rows();
	      
	      switch(this){
	      	case Plain: break;
	      	case Four:
	      		switch (pos){
				case 0:
					jpg.setAbsolutePosition(margin, margin+image_fit_height+images_margin);
					break;
				case 1:
					jpg.setAbsolutePosition(margin+image_fit_width+images_margin, margin+image_fit_height+images_margin);
					break;
				case 2:
					jpg.setAbsolutePosition(margin, margin);
					break;
				case 3:
					jpg.setAbsolutePosition(margin+image_fit_width+images_margin, margin);
					break;
	      		}
	      	case Six:
	      		switch (pos){
				case 0:
					jpg.setAbsolutePosition(margin, margin+image_fit_height*2+images_margin*2);
					break;
				case 1:
					jpg.setAbsolutePosition(margin+image_fit_width+images_margin, margin+image_fit_height*2+images_margin*2);
					break;
				case 2:
					jpg.setAbsolutePosition(margin, margin+image_fit_height+images_margin);
					break;
				case 3:
					jpg.setAbsolutePosition(margin+image_fit_width+images_margin, margin+image_fit_height+images_margin);
					break;
				case 4:
					jpg.setAbsolutePosition(margin, margin);
					break;
				case 5:
					jpg.setAbsolutePosition(margin+image_fit_width+images_margin, margin);
					break;
	      		}	
	      }
	      jpg.scaleToFit(image_fit_width,image_fit_height);

		
	}
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();

	  String page_size_as_string;
	  Rectangle page_size;
	  page_size_as_string = request.getParameter("page_size");
      if (page_size_as_string==null) page_size_as_string = "letter";

      if (page_size_as_string.equalsIgnoreCase("letter"))
      	page_size=PageSize.LETTER;
      else if (page_size_as_string.equalsIgnoreCase("halfletter"))
      	page_size=PageSize.HALFLETTER;
      else if (page_size_as_string.equalsIgnoreCase("A4"))
      	page_size=PageSize.A4;
      else if (page_size_as_string.equalsIgnoreCase("A3"))
      	page_size=PageSize.A3;
      else if (page_size_as_string.equalsIgnoreCase("A2"))
      	page_size=PageSize.A2;
      else if (page_size_as_string.equalsIgnoreCase("A1"))
      	page_size=PageSize.A1;
      else if (page_size_as_string.equalsIgnoreCase("A0"))
      	page_size=PageSize.A0;
      else{
    	  out.println("Invalid/unknown page size format: "+page_size_as_string);
    	  return;
      }
      
      if (request.getParameter("landscape")!=null)
      	page_size = page_size.rotate();

      int margin = 18;
      try{
    	  margin = Integer.parseInt(request.getParameter("margin"));
      } catch(NumberFormatException e){}
      
      int imagesPerPage = 1;
  	  try{
  		imagesPerPage = Integer.parseInt(request.getParameter("images_per_page"));
  	  } catch(NumberFormatException e){}
  	  
  	  int images_margin = 10;
      try{
    	images_margin = Integer.parseInt(request.getParameter("images_margin"));
      } catch(NumberFormatException e){}
  	  
  	  Positioning positioning = Positioning.create(imagesPerPage);
  	  
  	  if(positioning==null){
  		  out.println("Number of images per page "+imagesPerPage+" not supported");
  		  return;
  	  }

      String outputFile = request.getParameter("output_file");
      
          
      String images_list = request.getParameter("images_list");
      StringTokenizer st = new StringTokenizer(images_list,",");

      Document document = new Document(page_size,margin,margin,margin,margin);
      try{
    	    PdfWriter.getInstance(document, new FileOutputStream(outputFile));
    	    document.open();
	        String image_filename;
	        int pos = 1;
	        while (st.hasMoreTokens())
	        {
	        	image_filename = st.nextToken();
	        	try{
	        		Image jpg = Image.getInstance(image_filename);
	        		

	            	if (imagesPerPage>1 && pos >= imagesPerPage && pos % imagesPerPage  == 1){
	                    document.add(Chunk.NEXTPAGE);
	            	}
	            	
	            	positioning.scale(jpg,page_size,margin,images_margin,pos % imagesPerPage);
	            	document.add(jpg);
	            	pos++;

	        	}
	        	catch (java.io.IOException ex){
	        		if (ex.getMessage().indexOf("is not a recognized imageformat") > 0)
	        			continue;
	        		else
	            		throw new Exception(ex);
	        	}
	        }
  		document.close();
  	  } catch (Exception ex){
          try {
              document.close();
          } catch (Exception e){
              //ignore
          }
  		log(ex.getLocalizedMessage());
  		out.println(ex.getLocalizedMessage());
  		return;
  	  }      
      
      out.println("1");
  }
  
}
