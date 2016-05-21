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


/**
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


}
