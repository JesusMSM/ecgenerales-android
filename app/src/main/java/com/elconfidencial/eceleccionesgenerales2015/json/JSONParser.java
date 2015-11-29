package com.elconfidencial.eceleccionesgenerales2015.json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by Jesus on 07/11/2015.
 */
public class JSONParser {

    final String TAG = "JsonParser.java";

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public JSONParser() {

    }

    public JSONArray getJSONFromUrl(String url) {

        Log.v(TAG, "Final Requsting URL is : :" + url);

        String line = "";
        String responseJsonData = null;
        JSONArray json = null;

        try {
            StringBuilder sb = new StringBuilder();
            String x = "";
            URL httpurl = new URL(url);
            HttpURLConnection tc= (HttpURLConnection) httpurl.openConnection();
            InputStreamReader is =new InputStreamReader(tc.getInputStream());
            BufferedReader in = new BufferedReader(is);

            if(in !=null){
                while ((line = in.readLine()) != null) {
                    sb.append(line + "\n");
                    x = sb.toString();
                }
                responseJsonData = new String(x);

                in.close();
                is.close();
                if(tc!=null)
                    tc.disconnect();
                // Log.e(TAG, responseJsonData);

            }
        }
        catch (UnknownHostException uh){
            Log.v("NewWebHelper", "Unknown host :");
            uh.printStackTrace();
        }
        catch (FileNotFoundException e) {
            Log.v("NewWebHelper", "FileNotFoundException :");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.v("NewWebHelper", "IOException :");
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.v("NewWebHelper", "Exception :");
            e.printStackTrace();
        }
        try{
            json = new JSONArray(responseJsonData);
        }catch (Exception e){
            e.printStackTrace();
        }
        return json;
    }

}