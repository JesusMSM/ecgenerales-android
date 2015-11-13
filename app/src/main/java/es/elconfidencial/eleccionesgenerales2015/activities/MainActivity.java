package es.elconfidencial.eleccionesgenerales2015.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.parse.ConfigCallback;
import com.parse.Parse;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.pushwoosh.PushManager;
import com.pushwoosh.SendPushTagsCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.elconfidencial.eleccionesgenerales2015.R;
import es.elconfidencial.eleccionesgenerales2015.adapters.ViewPagerAdapter;
import es.elconfidencial.eleccionesgenerales2015.model.GlobalMethod;
import es.elconfidencial.eleccionesgenerales2015.model.Partido;
import es.elconfidencial.eleccionesgenerales2015.model.Quote;
import es.elconfidencial.eleccionesgenerales2015.model.QuoteServer;
import es.elconfidencial.eleccionesgenerales2015.slidingtabfiles.SlidingTabLayout;

public class MainActivity extends AppCompatActivity {

    public static Context context;
    public static Resources resources;
    static ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    int numbOfTabs =4;
    CharSequence[] Titles = new CharSequence[numbOfTabs];
    GlobalMethod globalMethod = new GlobalMethod(this);

    //Parámetros de CONFIG
    public static int DFP_CARD_EVERY_N;
    public static int LAST_NEWS_COUNTER;
    public static String RESULTS_WEBVIEW_URL;
    public static boolean SHOW_SURVEYS;
    public static boolean SHOW_TIMER;
    public static boolean SHOW_WIDGET_RESULTS;


    //Quotes
    QuoteServer qs = QuoteServer.getInstance();

    //PartidosList
    public static List<Partido> partidosList = new ArrayList<>();

    public List<Partido> getPartidosList(){
        return partidosList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resources = getResources();

        setContentView(R.layout.activity_main);
        this.context = getApplicationContext();
        qs.init(getApplicationContext());

        SharedPreferences prefs = context.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstTime", false);
        editor.apply();

        //Titulos, da igual lo que se ponga pero tienen que existir aunque no se vayan a ver despues
        Titles[0] = "HOME";
        Titles[1] = "NEWS";
        Titles[2] = "POLLS";
        Titles[3] = "PRESINDER";

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, numbOfTabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);
        pager.setOffscreenPageLimit(3);
        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.ColorAccent);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout

        tabs.setCustomTabView(R.layout.custom_actionbar, 0);
        tabs.setViewPager(pager);

        //Download quotes
        qs.getQuotesFromParseOrLocal();
        //Likes dislikes count
        //GlobalMethod.likesCount = GlobalMethod.getMyHashmap(getApplicationContext(),"likesCount");
        //GlobalMethod.dislikesCount = GlobalMethod.getMyHashmap(getApplicationContext(),"dislikesCount");

        //Create Partidos variables
        // Getting JSON from asset
        String json = loadJSONFromAsset("PARTIDOS_TAGS.json");
        if(json!=null){
            Log.i("PartidosJSON", "JSON recuperado de assets");
            setPartidosListFromJSON(json);
        } else{
            Log.i("PartidosJSON", "JSON no recuperado de assets");
        }

        final RelativeLayout loadingLayout = (RelativeLayout) findViewById(R.id.loadingLayout);
        final RelativeLayout activityLayout = (RelativeLayout) findViewById(R.id.activityLayout);
        ParseConfig.getInBackground(new ConfigCallback() {
            @Override
            public void done(ParseConfig config, ParseException e) {
                DFP_CARD_EVERY_N = config.getInt("DFP_CARD_EVERY_N");
                LAST_NEWS_COUNTER = config.getInt("LAST_NEWS_COUNTER");
                RESULTS_WEBVIEW_URL = config.getString("RESULTS_WEBVIEW_URL");
                SHOW_SURVEYS = config.getBoolean("SHOW_SURVEYS");
                SHOW_TIMER = config.getBoolean("SHOW_TIMER");
                SHOW_WIDGET_RESULTS = config.getBoolean("SHOW_WIDGET_RESULTS");
                Log.d("TAG", "URL" + RESULTS_WEBVIEW_URL);
                loadingLayout.setVisibility(View.GONE);
                activityLayout.setVisibility(View.VISIBLE);

            }
        });
       /* Log.d("TAG", "URL"+RESULTS_WEBVIEW_URL);
        while(RESULTS_WEBVIEW_URL == null){
            Log.d("TAG", "URL"+RESULTS_WEBVIEW_URL);
        }

        ;*/
    }


    //Almacena los objetos Partidos dentro de la Lista Global partidosList a partir de un json
    public void setPartidosListFromJSON(String json){
        try {
            JSONArray jArray = new JSONArray(json);
            for (int i = 0; i < jArray.length(); i++) {
                try {
                    JSONObject partidoObject = jArray.getJSONObject(i);

                    // Creamos un objeto Partido, donde almacenaremos todos sus atributos
                    Partido partido = new Partido();
                    partido.setId(partidoObject.getString("ID"));
                    partido.setNombre(partidoObject.getString("Desc"));
                    partido.setColor(partidoObject.getString("Color"));
                    partido.setSiglas(partidoObject.getString("Short"));
                    partido.setTagNoticias(partidoObject.getString("News_TAG"));
                    partido.setTagPush(partidoObject.getString("Push_TAG"));
                    partidosList.add(partido);
                    Log.i("PartidosJSON", "Almacenado el partido " + partido.getId());
                    Log.i("PartidosJSON", "Con siglas " + partido.getSiglas());
                } catch (JSONException e) {
                    Log.i("PartidosJSON", "Error lectura de JSON");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    //Método que lee un fichero json almacenado en assets
    public String loadJSONFromAsset(String nameFile) {
        String json = null;
        try {

            InputStream is = getAssets().open(nameFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }


    @Override
    public void onBackPressed() {
        System.gc();
        finish();
        super.onBackPressed();
        System.exit(0);
    }



}
