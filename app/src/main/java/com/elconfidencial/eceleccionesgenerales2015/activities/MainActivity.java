package com.elconfidencial.eceleccionesgenerales2015.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comscore.analytics.comScore;
import com.elconfidencial.eceleccionesgenerales2015.fragments.EncuestasTab;
import com.elconfidencial.eceleccionesgenerales2015.fragments.ResultadosTab;
import com.elconfidencial.eceleccionesgenerales2015.fragments.NoticiasTab;
import com.elconfidencial.eceleccionesgenerales2015.fragments.PresinderTab;
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
    ViewPager pager;
    ViewPagerAdapter adapter;
    Toolbar toolbar;
    ActionBar actionBar;
    public TabLayout tabLayout;
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
    CoordinatorLayout activityLayout;
    public static ProgressDialog pd;

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


        //COMSCORE
        /**
        try{
            //comScore
            comScore.onUxActive();
        }catch (Exception e){
            Log.i("Comscore", "Error Comscore");
            e.printStackTrace();
        }**/


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

        setupToolbar();
        setupViewPager();
        setupTabs();


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
        activityLayout = (CoordinatorLayout) findViewById(R.id.activityLayout);
        if(globalMethod.haveNetworkConnection()) {
            new JSONConfig().execute();
        }
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
        pd = new ProgressDialog(getApplicationContext());
    }

    // region Toolbar
    //----------------------------------------------------------------------

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(null);
            //actionBar.setHomeAsUpIndicator(R.drawable.elconfidencial_32dp_white);
            //actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //----------------------------------------------------------------------
    //endregion


    // region TabLayout
    //----------------------------------------------------------------------

    private void setupTabs() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, android.R.color.black));

        //Configura las tabs
        setupTabAt(0, "Noticias", true);
        setupTabAt(1, "Encuestas", false);
        setupTabAt(2, "Resultados", false);
        setupTabAt(3, "Test", false);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int tabPosition = tab.getPosition();
                pager.setCurrentItem(tabPosition);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void setupTabAt(int position, String title, boolean selected) {
        TabLayout.Tab tab = tabLayout.getTabAt(position);

        if (tab!=null){
            tab.setCustomView(R.layout.tab_custom);
            TextView textView = (TextView) tab.getCustomView();
            if (textView!= null) {
                textView.setText(title);
                textView.setTextColor(ContextCompat.getColorStateList(this, R.color.black));
                textView.setSelected(selected);
            }
        }

    }


    //----------------------------------------------------------------------
    //endregion




    // region VIEWPAGER
    //----------------------------------------------------------------------

    public void setupViewPager(){

        pager = (ViewPager) findViewById(R.id.pager);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(NoticiasTab.newInstance(), "Portada");
        adapter.addFragment(EncuestasTab.newInstance(), "Favoritos");
        adapter.addFragment(ResultadosTab.newInstance(), "Esencial");
        adapter.addFragment(PresinderTab.newInstance(), "Esencial");

        pager.setAdapter(adapter);
    }

    //----------------------------------------------------------------------
    //endregion




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
        try{
            //comScore
            Log.i("Comscore", "Se sale de Comscore con onBackPressed");
            comScore.onUxInactive();
        }catch (Exception e){
            Log.i("Comscore", "Error Comscore");
            e.printStackTrace();
        }
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



    @Override
    protected void onResume() {
        super.onResume();
        try{
            //comScore
            Log.i("Comscore", "Dentro de on Resume");
            comScore.onEnterForeground();
        }catch (Exception e){
            Log.i("Comscore", "Error Comscore");
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            //comScore
            Log.i("Comscore", "Dentro de on Pause");
            comScore.onExitForeground();
        }catch (Exception e){
            Log.i("Comscore", "Error Comscore");
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            //comScore
            Log.i("Comscore", "Se sale de la app con onDestroy");
            comScore.onExitForeground();
        }catch (Exception e){
            Log.i("Comscore", "Error Comscore");
            e.printStackTrace();
        }
    }



    // region Options Menu
    //----------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:

                Intent intent = new Intent(this, PreferencesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                //Amplitude
                /**Log.i("20D_AMPLITUDE", "ONTAP_SETTINGS");
                 Amplitude.getInstance().logEvent("ONTAP_SETTINGS");**/
        }
        return super.onOptionsItemSelected(item);
    }

    //----------------------------------------------------------------------
    //endregion




    private class JSONConfig extends AsyncTask<String, String, JSONObject> {


        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParserObject jParser = new JSONParserObject();

            // Getting JSON from URL
            if(globalMethod.haveNetworkConnection()){
                JSONObject json = jParser.getJSONFromUrl(config_url);
                return json;
            }
            return null;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if(json != null && globalMethod.haveNetworkConnection()) {
                    // Getting JSON Array
                    DFP_CARD_EVERY_N = json.getInt(TAG_DFP_CARD_EVERY_N);
                    LAST_NEWS_COUNTER = json.getInt(TAG_LAST_NEWS_COUNTER);
                    RESULTS_WEBVIEW_URL = json.getString(TAG_RESULTS_WEBVIEW);
                    SHOW_SURVEYS = json.getBoolean(TAG_SHOW_SURVEYS);
                    SHOW_TIMER = json.getBoolean(TAG_SHOW_TIMER);
                    SHOW_WIDGET_RESULTS = json.getBoolean(TAG_SHOW_RESULTS);
                    PRESINDER_SHARE_MESSAGE_ANDROID = json.getString(TAG_PRESINDER_SHARE_MESSAGE_ANDROID);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }
            if(json!= null) {
                loadingLayout.setVisibility(View.GONE);
                activityLayout.setVisibility(View.VISIBLE);
                if (SHOW_WIDGET_RESULTS) {
                    pager.setCurrentItem(2);
                }
            }

        }
    }
}
