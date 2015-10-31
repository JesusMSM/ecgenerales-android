package es.elconfidencial.eleccionesgenerales2015.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Moonfish on 28/10/15.
 */
public class GlobalMethod {
    Context mContext;
    public static List<Quote> quotes = new ArrayList();
    public static int quotesIndex = 0;
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
}
