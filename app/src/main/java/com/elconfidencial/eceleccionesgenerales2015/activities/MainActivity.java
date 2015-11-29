package com.elconfidencial.eceleccionesgenerales2015.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.parse.Parse;
import com.parse.ParseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.adapters.ViewPagerAdapter;
import com.elconfidencial.eceleccionesgenerales2015.json.JSONParserObject;
import com.elconfidencial.eceleccionesgenerales2015.model.GlobalMethod;
import com.elconfidencial.eceleccionesgenerales2015.model.Partido;
import com.elconfidencial.eceleccionesgenerales2015.model.QuoteServer;
import com.elconfidencial.eceleccionesgenerales2015.slidingtabfiles.SlidingTabLayout;

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
    public static String PRESINDER_SHARE_MESSAGE_ANDROID;

    public static String config_url = "http://datos.elconfidencial.com/app-elecciones-generales-2015-survey/config.json";

    //Parámetros config
    private String TAG_DFP_CARD_EVERY_N = "DFP_CARD_EVERY_N";
    private String TAG_LAST_NEWS_COUNTER = "LAST_NEWS_COUNTER";
    private String TAG_RESULTS_WEBVIEW = "RESULTS_WEBVIEW_URL";
    private String TAG_SHOW_SURVEYS= "SHOW_SURVEYS";
    private String TAG_SHOW_TIMER = "SHOW_TIMER";
    private String TAG_SHOW_RESULTS = "SHOW_WIDGET_RESULTS";
    private String TAG_PRESINDER_SHARE_MESSAGE_ANDROID = "PRESINDER_SHARE_MESSAGE_ANDROID";

    LinearLayout loadingLayout;
    RelativeLayout activityLayout;

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

        // Enable Local Datastore.
        try {
            Parse.enableLocalDatastore(this);

            Parse.initialize(this, "fFMHyON2OrC3F161LgiepetpuB3WTktLvS6gq6ZH", "jqiMfz2BVxn4JNFhbsvscaEDg6QPObKn1JvGr0Wa");

            ParseAnalytics.trackAppOpenedInBackground(getIntent());
        }catch (Exception e){
            e.printStackTrace();
        }

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
            if(partidosList.isEmpty()) {
                setPartidosListFromJSON(json);
            }
        } else{
            Log.i("PartidosJSON", "JSON no recuperado de assets");
        }


        loadingLayout = (LinearLayout) findViewById(R.id.loadingLayout);
        activityLayout = (RelativeLayout) findViewById(R.id.activityLayout);
        new JSONConfig().execute();
        /*ParseConfig.getInBackground(new ConfigCallback() {
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
        });*/
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

    public void refreshPresinder(){
        Log.i("PRESINDER", "Refreshing presinder...");
        //Set text from here
        pager.setAdapter(adapter);
        pager.setCurrentItem(3);

    }

    //Cambia de TAB pasándole el número correspondiente
    public static void switchFragment(int target){
        pager.setCurrentItem(target);
    }

    private class JSONConfig extends AsyncTask<String, String, JSONObject> {


        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParserObject jParser = new JSONParserObject();

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(config_url);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                // Getting JSON Array
                DFP_CARD_EVERY_N = json.getInt(TAG_DFP_CARD_EVERY_N);
                LAST_NEWS_COUNTER = json.getInt(TAG_LAST_NEWS_COUNTER);
                RESULTS_WEBVIEW_URL = json.getString(TAG_RESULTS_WEBVIEW);
                SHOW_SURVEYS = json.getBoolean(TAG_SHOW_SURVEYS);
                SHOW_TIMER = json.getBoolean(TAG_SHOW_TIMER);
                SHOW_WIDGET_RESULTS = json.getBoolean(TAG_SHOW_RESULTS);
                PRESINDER_SHARE_MESSAGE_ANDROID = json.getString(TAG_PRESINDER_SHARE_MESSAGE_ANDROID);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            loadingLayout.setVisibility(View.GONE);
            activityLayout.setVisibility(View.VISIBLE);
            if(SHOW_WIDGET_RESULTS){
                pager.setCurrentItem(2);
            }

        }
    }

}
