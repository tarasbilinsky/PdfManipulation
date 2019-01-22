import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;


@SuppressWarnings("serial")
public class GetFile extends HttpServlet {



  private static String region="us-east-1";
  private static String bucket="ncs-uploads-main";

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
      String s3Path = "";
	  InputStream is = null;
      try{
    	  String path = request.getParameter("path");


          Path pathO = FileSystems.getDefault().getPath(path);
          if(!pathO.toFile().exists()){
              AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(region)).withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(keyId, key))).build();
              String tempPath = "D:\\VIPResources\\Temp\\"+ UUID.randomUUID().toString();
              File tempFile = new File(tempPath);

              s3Path = path
                      .replaceFirst("(?i)\\Q\\\\192.168.0.7\\E[4,5]\\Q\\\\EVIPResources(ARCHIVE)?\\Q\\DataUploads\\ByDate\\\\E", "")
              .replace('\\','/');

              ObjectMetadata s3Object = s3Client.getObject(new GetObjectRequest(bucket,s3Path),tempFile);
              pathO = tempFile.toPath();
          }
    	  
    	  is = Files.newInputStream(pathO , StandardOpenOption.READ);
  
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
          if(s3Path.length()>0) log(s3Path);
    	  log(e.getLocalizedMessage());
    	  if(is!=null) is.close();
      }
      
  }
}
