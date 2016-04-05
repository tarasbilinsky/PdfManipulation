import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;


public class LockEms extends HttpServlet {

    public static void main (String[] args){
        boolean res = lockIfNotLocked("/Users/taras/Downloads/FZK0OLD/FZK0OLD.env");
        java.lang.System.out.println(res);
    }

    public static boolean lockIfNotLocked(String filename) {
        final String findName = "STATUS";

        Path path = Paths.get(filename);
        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        short headerLength = getShort(data,8);

        int findLen = headerLength+1;

        for(int i=32;i<headerLength;i+=32){
            String name = getString(data,i,11);
            if(name.equals(findName)) break;
            byte fLen = data[i+16];
            findLen+=fLen;
        }

        boolean wasLocked = false;
        char locked = (char) data[findLen];
        if(locked=='Y' || locked=='y' || locked=='T' || locked=='t') wasLocked = true;
        else if(locked=='N') data[findLen]='Y';
        else if(locked=='n') data[findLen]='y';
        else if(locked=='F') data[findLen]='T';
        else if(locked=='f') data[findLen]='t';
        else data[findLen]='Y';

        if(wasLocked);

        else{
            try {
                Files.write(path, data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        return wasLocked;
    }

    private static short getShort(final byte[] data, final int index){
        short[] shorts = new short[1];
        byte[] b = Arrays.copyOfRange(data,index,index+2);
        ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
        return shorts[0];
    }
    private static String getString(final byte[] data, final int index, final int len){
        StringBuilder r = new StringBuilder();
        for(int i=index;i<index+len && data[i]!=0;i++)
            r.append((char)data[i]);
        return r.toString();
    }


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Exception error = null;
        boolean res = false;
        try{
            String fileLocation = request.getParameter("fileLocation");
            res = lockIfNotLocked(fileLocation);
        } catch (Exception e){
            error = e;
        }
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(error==null?(res?"1":"0"):error.getLocalizedMessage());
    }
}