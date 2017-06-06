package service;

import org.apache.commons.logging.Log;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qiaoruixiang on 02/06/2017.
 */
public class RestClient {

    private JSONObject data;
    private String url;
    private Map<String, Object> queryParams;
    //private String headerName;
    //private String headerValue;

    public RestClient(String s){
        url = s;
        queryParams = new HashMap<>();
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public void setQueryParams(Map<String, Object> qp) {
        queryParams = queryParams;
    }

    /*
    public void addHeader(String name, String value){

        headerName = name;
        headerValue = value;

    }

    public void addParam(String key, String value){

        try {
            data.put(key, value);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    */

    public String executePost(){  // If you want to use post method to hit server
        String result = null;

        HttpURLConnection urlConnection = null;
        DataOutputStream output;
        BufferedReader reader = null;

        try {
        URIBuilder buildUri = new URIBuilder(url);
        //Uri.Builder buildUri = Uri.parse(url).buildUpon();
        for (Map.Entry<String, Object> pair : queryParams.entrySet()) {
            buildUri.addParameter(pair.getKey(), pair.getValue().toString());
        }

            URL url = new URL(buildUri.build().toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            output = new DataOutputStream(urlConnection.getOutputStream());
            output.writeBytes(URLEncoder.encode(data.toString(), "UTF-8"));
            output.flush();
            output.close();


            InputStream is = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = reader.readLine()) != null) {
                response.append(line);
                response.append('\n');
            }
            result = response.toString();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public String executeGet(){ //If you want to use get method to hit server
        String result = null;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URIBuilder buildUri = new URIBuilder(url);
            //Uri.Builder buildUri = Uri.parse(url).buildUpon();
            for (Map.Entry<String, Object> pair : queryParams.entrySet()) {
                buildUri.addParameter(pair.getKey(), pair.getValue().toString());
            }

            URL url = new URL(buildUri.build().toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {

                return "";
            }
            result = buffer.toString();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    System.out.println("REST: " + "Error closing stream");
                    e.printStackTrace();
                }
            }
        }

        // TODO web service bug, have to remove in the future
        Pattern pattern = Pattern.compile("(\\{.*\\})");
        Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return result;
    }



}
