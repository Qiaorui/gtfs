package Service;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import xplanner.model.Place;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by qiaorui on 21/11/16.
 */
public class OTPService {
    private final static String BASE_URI = "http://localhost:8080/otp/routers/default/plan?";
    private final static String MODE = "TRANSIT,WALK";
    private final static String MAX_DISTANCE = "1250";



    private void clearPlace(JSONObject obj) {
        obj.remove("stopIndex");
        obj.remove("arrival");
        obj.remove("stopSequence");
        obj.remove("stopCode");
        obj.remove("departure");
        obj.remove("stopId");
        obj.remove("vertexType");
    }

    private void clearLeg(JSONObject obj) {
        if (obj.get("mode").equals("WALK")) {
            obj.clear();
            obj.put("mode", "WALK");
            return;
        } else if (obj.get("mode").equals("BUS")) {
            obj.put("waitingTime", 210);
        } else if (obj.get("mode").equals("SUBWAY")) {
            obj.put("waitingTime", 120);
        } else if (obj.get("mode").equals("FUNICULAR")) {
            obj.put("waitingTime", 300);
        }
        obj.remove("agencyName");
        obj.remove("routeShortName");
        obj.remove("routeType");
        obj.remove("agencyId");
        obj.remove("tripId");
        obj.remove("routeLongName");
        obj.remove("agencyUrl");
        obj.remove("routeId");
        obj.remove("headsign");
        //obj.remove("legGeometry");
        obj.remove("startTime");
        obj.remove("endTime");
        obj.remove("pathway");
        obj.remove("departureDelay");
        obj.remove("arrivalDelay");
        obj.remove("agencyTimeZoneOffset");
        obj.remove("transitLeg");
        obj.remove("interlineWithPreviousLeg");
        obj.remove("realTime");
        obj.remove("rentedBike");
        obj.remove("serviceDate");
        obj.remove("steps");
        clearPlace((JSONObject) obj.get("from"));
        clearPlace((JSONObject) obj.get("to"));
    }

    private void clearItinerary(JSONObject obj) {
        obj.put("duration", (long) obj.get("duration") - (long) obj.get("waitingTime") );
        obj.put("waitingTime", 0);
        JSONArray legs = (JSONArray) obj.get("legs");
        for (int i = 0; i < legs.size(); i++) {
            JSONObject leg = (JSONObject) legs.get(i);
            clearLeg(leg);
            if (!leg.get("mode").equals("WALK")) {
                obj.put("waitingTime", (int)obj.get("waitingTime") + (int)leg.get("waitingTime") );
            }
        }
        obj.put("duration", (long) obj.get("duration") + (int) obj.get("waitingTime") );

        obj.remove("startTime");
        obj.remove("endTime");
        //obj.remove("walkTime");
        //obj.remove("waitingTime");
        obj.remove("elevationLost");
        obj.remove("walkDistance");
        //obj.remove("transitTime");
        obj.remove("tooSloped");
        obj.remove("walkLimitExceeded");
        obj.remove("elevationGained");

    }

    private JSONArray sortJSONArray(JSONArray array) {
        JSONArray result = new JSONArray();
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = (JSONObject) array.get(i);
            clearItinerary(obj);
        }
        ArrayList<Integer> visited = new ArrayList<>(array.size());
        for (int i = 0; i < array.size(); i++) {
            int index = -1;
            long fastest = Long.MAX_VALUE;
            for (int j = 0; j < array.size(); j++) {
                if ( (long) ((JSONObject)array.get(j)).get("duration") < fastest && !visited.contains(j)) {
                    fastest = (long) ((JSONObject)array.get(j)).get("duration");
                    index = j;
                }
            }
            visited.add(index);
            result.add(array.get(index));
        }

        return result;
    }

    public JSONArray getItineraryJSON(String fromLat, String fromLon, String toLat, String toLon, String date, String time, String mode) {
        JSONArray result = null;
        try{
            String src = getJSON(fromLat, fromLon, toLat, toLon, date, time, mode);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(src);
            if (jsonObject.get("plan") == null) {
                JSONObject tmp = new JSONObject();
                tmp.put("duration", 30);
                tmp.put("walkTime", 30);
                JSONArray  array = new JSONArray();
                array.add(tmp);
                return array;
            }
            JSONArray jsonArray = (JSONArray) ((JSONObject)jsonObject.get("plan")).get("itineraries");
            result = jsonArray;
            //result = sortJSONArray(jsonArray);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public JSONArray getItineraryJSON(Double fromLat, Double fromLon, Double toLat, Double toLon, String date, String time, String mode) {
        return getItineraryJSON(
                fromLat.toString(),
                fromLon.toString(),
                toLat.toString(),
                toLon.toString(),
                date,
                time,
                mode
        );
    }

    private String getJSON(String fromLat, String fromLon, String toLat, String toLon, String date, String time, String mode) {
        StringBuilder buffer = new StringBuilder();

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet(BASE_URI + "fromPlace=" + fromLat + "," + fromLon + "&" + "toPlace=" + toLat + "," + toLon + "&" + "time=" + time + "&" + "date=" + date + "&" + "mode=" + mode + "&" + "maxWalkDistance=" + MAX_DISTANCE + "&" + "wheelchair=false&" + "locale=ca_ES");
            getRequest.addHeader("accept", "application/json");
            System.out.println(getRequest.getURI().normalize().toString());
            HttpResponse response = client.execute(getRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            String line;
            while ((line = br.readLine()) != null) {
                buffer.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer.toString();
    }



    /**
     * Calculates geodetic distance between two points specified by latitude/longitude using Vincenty inverse formula
     * for ellipsoids
     *
     * @param lat1 first point latitude in decimal degrees
     * @param lon1 first point longitude in decimal degrees
     * @param lat2 second point latitude in decimal degrees
     * @param lon2 second point longitude in decimal degrees
     * @returns distance in meters between points with 5.10<sup>-4</sup> precision
     * @see <a href="http://www.movable-type.co.uk/scripts/latlong-vincenty.html">Originally posted here</a>
     */
    private double distVincenty(double lat1, double lon1, double lat2, double lon2) {
        double a = 6378137, b = 6356752.314245, f = 1 / 298.257223563; // WGS-84 ellipsoid params
        double L = Math.toRadians(lon2 - lon1);
        double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
        double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));
        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

        double sinLambda, cosLambda, sinSigma, cosSigma, sigma, sinAlpha, cosSqAlpha, cos2SigmaM;
        double lambda = L, lambdaP, iterLimit = 100;
        do {
            sinLambda = Math.sin(lambda);
            cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda) + (cosU1 * sinU2 - sinU1 * cosU2 *
                    cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
            if (sinSigma == 0)
                return 0; // co-incident points
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            if (Double.isNaN(cos2SigmaM))
                cos2SigmaM = 0; // equatorial line: cosSqAlpha=0 (§6)
            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = L + (1 - C) * f * sinAlpha * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 *
                    cos2SigmaM * cos2SigmaM)));
        } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

        if (iterLimit == 0)
            return Double.NaN; // formula failed to converge

        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B /
                6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
        double dist = b * A * (sigma - deltaSigma);

        return dist;
    }

}
