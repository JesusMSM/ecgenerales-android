package com.elconfidencial.eceleccionesgenerales2015.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

    Context context;
    public static Resources resources;
    ViewPager pager;
    ViewPagerAdapter adapter;
    Toolbar toolbar;
    ActionBar actionBar;
    public TabLayout tabLayout;
    GlobalMethod globalMethod = new GlobalMethod(this);

    CoordinatorLayout activityLayout;
    public static ProgressDialog pd;

    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;

    boolean test = false;
    int count = 0;

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

        resources = getResources();

        setContentView(R.layout.activity_main);
        context = this;

        SharedPreferences prefs = context.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstTime", false);
        editor.apply();

        activityLayout = (CoordinatorLayout) findViewById(R.id.activityLayout);

        setupToolbar();
        setupViewPager();
        setupTabs();

        mHandler = new Handler();
        startRepeatingTask();



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

        if (ChooseActivity.SHOW_WIDGET_RESULTS) {
            pager.setCurrentItem(2);
        }
    }

    //----------------------------------------------------------------------
    //endregion




    @Override
    public void onBackPressed() {
       /** try{
            //comScore
            Log.i("Comscore", "Se sale de Comscore con onBackPressed");
            comScore.onUxInactive();
        }catch (Exception e){
            Log.i("Comscore", "Error Comscore");
            e.printStackTrace();
        }**/
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

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                if (globalMethod.haveNetworkConnection()){
                    new RefreshConfigAsyncTask().execute();
                }
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }


    @Override
    protected void onResume() {
        super.onResume();
        try{
            startRepeatingTask();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            stopRepeatingTask();
        }catch (Exception e){

            e.printStackTrace();
        }
    }
/**
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
    }**/



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


    private class RefreshConfigAsyncTask extends AsyncTask<String, String, JSONObject> {


        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParserObject jParser = new JSONParserObject();


            //Likes dislikes count
            //GlobalMethod.likesCount = GlobalMethod.getMyHashmap(getApplicationContext(),"likesCount");
            //GlobalMethod.dislikesCount = GlobalMethod.getMyHashmap(getApplicationContext(),"dislikesCount");


            // Getting JSON from URL
            if(globalMethod.haveNetworkConnection()){
                JSONObject json = jParser.getJSONFromUrl(ChooseActivity.config_url);
                return json;
            }
            return null;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            boolean changed = false;
            try {
                if(json != null && globalMethod.haveNetworkConnection()) {
                    // Getting JSON Array
                    if(ChooseActivity.DFP_CARD_EVERY_N != json.getInt(ChooseActivity.TAG_DFP_CARD_EVERY_N)) changed = true;
                    if(ChooseActivity.LAST_NEWS_COUNTER !=  json.getInt(ChooseActivity.TAG_LAST_NEWS_COUNTER)) changed = true;
                    if(!ChooseActivity.RESULTS_WEBVIEW_URL.equals(json.getString(ChooseActivity.TAG_RESULTS_WEBVIEW))) changed = true;
                    if(ChooseActivity.SHOW_SURVEYS != json.getBoolean(ChooseActivity.TAG_SHOW_SURVEYS)) changed = true;
                    if(ChooseActivity.SHOW_TIMER != json.getBoolean(ChooseActivity.TAG_SHOW_TIMER)) changed = true;
                    if(ChooseActivity.SHOW_WIDGET_RESULTS != json.getBoolean(ChooseActivity.TAG_SHOW_RESULTS)) changed = true;
                    if(!ChooseActivity.PRESINDER_SHARE_MESSAGE_ANDROID.equals(json.getString(ChooseActivity.TAG_PRESINDER_SHARE_MESSAGE_ANDROID))) changed = true;

                    ChooseActivity.DFP_CARD_EVERY_N = json.getInt(ChooseActivity.TAG_DFP_CARD_EVERY_N);
                    ChooseActivity.LAST_NEWS_COUNTER = json.getInt(ChooseActivity.TAG_LAST_NEWS_COUNTER);
                    ChooseActivity.RESULTS_WEBVIEW_URL = json.getString(ChooseActivity.TAG_RESULTS_WEBVIEW);
                    ChooseActivity.SHOW_SURVEYS = json.getBoolean(ChooseActivity.TAG_SHOW_SURVEYS);
                    ChooseActivity.SHOW_TIMER = json.getBoolean(ChooseActivity.TAG_SHOW_TIMER);
                    ChooseActivity.SHOW_WIDGET_RESULTS = json.getBoolean(ChooseActivity.TAG_SHOW_RESULTS);
                    ChooseActivity.PRESINDER_SHARE_MESSAGE_ANDROID = json.getString(ChooseActivity.TAG_PRESINDER_SHARE_MESSAGE_ANDROID);


                }

            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }
            if (changed) {
                pager.setOffscreenPageLimit(4);
                pager.getAdapter().notifyDataSetChanged();
            }

        }
    }

}
