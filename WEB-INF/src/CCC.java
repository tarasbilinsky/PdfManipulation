import com.itextpdf.text.pdf.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
import com.ctc.wstx.util.StringUtil;
import com.mycccportal.services.fnolrecommendation.PartyKindType;
import com.mycccportal.services.fnolrecommendation.StateType;
import com.mycccportal.services.fnolrecommendation.VehicleKindType;
import com.mycccportal.services.util.data.Request;
import org.apache.cxf.common.util.StringUtils;
*/
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class CCC extends HttpServlet {

    private Boolean  parseBool(String s){
        return s==null || s.length()==0?null:s.equals("1");

    }

    private String getParameterSpecial(HttpServletRequest request, String s){
        return request.getParameter(s.toLowerCase());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        String userName = getParameterSpecial(request,"UserName");
        String userPassword = getParameterSpecial(request,"UserPassword");
        String claimOfficeBranchCode = getParameterSpecial(request,"ClaimOfficeBranchCode");

        String claimNumber = getParameterSpecial(request,"ClaimNumber");

        String vIN = getParameterSpecial(request,"VIN");
        Integer vehicleYear = null;
        try{ vehicleYear = Integer.parseInt(getParameterSpecial(request,"VehicleYear"));} catch (NumberFormatException e){};
        String vehicleMakeCode = getParameterSpecial(request,"VehicleMakeCode");
        String vehicleModel = getParameterSpecial(request,"VehicleModel");
        /*VehicleKindType vehicleType = null;
        try{ vehicleType = VehicleKindType.fromValue(getParameterSpecial(request,"VehicleType"));} catch (IllegalArgumentException e){};
        StateType ownerStateCode = null;
        try{ ownerStateCode = StateType.fromValue(getParameterSpecial(request,"OwnerStateCode"));} catch (IllegalArgumentException e){};
        String zipCode = getParameterSpecial(request,"ZipCode");

        Integer primaryDamageCode = null;
        try{ primaryDamageCode = Integer.parseInt(getParameterSpecial(request,"PrimaryDamageCode"));} catch (NumberFormatException e){};
        Integer secondaryDamageCode = null;
        try{ secondaryDamageCode = Integer.parseInt(getParameterSpecial(request,"SecondaryDamageCode"));} catch (NumberFormatException e){};
        Boolean drivable = parseBool(getParameterSpecial(request,"Drivable"));
        Boolean airbagDeployed = parseBool(getParameterSpecial(request,"AirbagDeployed"));
        Integer maxOdometerReading = null;
        try{ maxOdometerReading = Integer.parseInt(getParameterSpecial(request,"MaxOdometerReading"));} catch (NumberFormatException e){};


        PartyKindType partyKind = null;
        try{ partyKind = PartyKindType.fromValue(getParameterSpecial(request,"PartyKind"));} catch (IllegalArgumentException e){};
        String liabilityConfirmed = getParameterSpecial(request,"LiabilityConfirmed");
        Integer severity = null;
        try{ severity = Integer.parseInt(getParameterSpecial(request,"Severity"));} catch (NumberFormatException e){};
        String intenttoRepair = getParameterSpecial(request,"IntenttoRepair");
        String rentalInUse = getParameterSpecial(request,"RentalInUse");
        String injuries  = getParameterSpecial(request,"Injuries");


        PrintWriter out = response.getWriter();

        try {
            for(String r: Request.run(
                     userName,
                     userPassword,
                     claimOfficeBranchCode,
                     claimNumber,
                     vIN,
                     vehicleYear,
                     vehicleMakeCode,
                     vehicleModel,
                     vehicleType,
                     ownerStateCode,
                    zipCode,
                     primaryDamageCode,
                     secondaryDamageCode,
                     drivable,
                     airbagDeployed,
                     maxOdometerReading,
                     partyKind,
                     liabilityConfirmed,
                     severity,
                     intenttoRepair,
                     rentalInUse,
                     injuries)
                    )
            out.print(r+'\n');
        } catch (Exception error) {
            log(error.getLocalizedMessage());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            error.printStackTrace(pw);
            out.print("Error: " + error.getLocalizedMessage()+"\n\n"+sw.toString());
        }
        */
    }
}
