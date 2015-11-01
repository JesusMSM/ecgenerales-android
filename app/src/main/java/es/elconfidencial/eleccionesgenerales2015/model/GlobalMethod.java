package es.elconfidencial.eleccionesgenerales2015.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Moonfish on 28/10/15.
 */
public class GlobalMethod {
    Context mContext;
    public static List<Quote> quotes = new ArrayList();

    public static HashMap<String,Integer> likesCount = new HashMap<>();
    public static HashMap<String,Integer> dislikesCount = new HashMap<>();

    // constructor
    public GlobalMethod(Context context){
        this.mContext = context;
    }

    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static void saveIntPreference(Context context, int value, String key){
        //Indexer Quotes
        SharedPreferences sp = context.getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    public static int getIntPreference(Context context, String key, int defaultValue){
        SharedPreferences sp = context.getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);

    }
    public static void saveStringPreference(Context context, String value, String key){
        //Indexer Quotes
        SharedPreferences sp = context.getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static String getIntPreference(Context context, String key, String defaultValue){
        SharedPreferences sp = context.getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        return sp.getString(key, defaultValue);

    }

    public static void putMyHashmap(Context context, String key , HashMap map) {
        SharedPreferences sp = context.getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(map);
        editor.putString(key, json);
        editor.apply();
    }

    public static HashMap<String,Integer> getMyHashmap(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString(key, "");
        Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
        HashMap obj = gson.fromJson(json, type);
        if (obj== null){return new HashMap<> ();}
        return obj;

    }
}
