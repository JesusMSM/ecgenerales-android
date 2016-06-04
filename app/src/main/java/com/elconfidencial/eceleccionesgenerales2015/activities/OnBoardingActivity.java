package com.elconfidencial.eceleccionesgenerales2015.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.comscore.analytics.comScore;
import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.adapters.MyArrayAdapter;
import com.elconfidencial.eceleccionesgenerales2015.model.Municipio;

public class OnBoardingActivity extends AppCompatActivity {

    TextView  saltarButton;
    ImageView positionIcon, searchIcon;
    Toolbar toolbar;
    ActionBar actionBar;
    RelativeLayout searchLayout;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
        context = this;

        //Amplitude
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put("page", "escoge_municipio");
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        Amplitude.getInstance().logEvent("page_view", eventProperties);


        //binding
        positionIcon = (ImageView) findViewById(R.id.positionIcon);
        Glide.with(context).load(R.drawable.ic_location_selection).into(positionIcon);
        searchIcon = (ImageView) findViewById(R.id.search_icon);
        Glide.with(context).load(R.drawable.ic_search).into(searchIcon);
        saltarButton = (TextView) findViewById(R.id.skip_button);
        searchLayout = (RelativeLayout) findViewById(R.id.search_layout);

        setupToolbar();


        //listeners
        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SearchLocalityActivity.class);
                startActivity(intent);
            }
        });

        saltarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = ProgressDialog.show(context, "Cargando", "Espere unos instantes");

                //Amplitude
                Amplitude.getInstance().logEvent("Skip");

                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        /**try{
            //comScore
            Log.i("Comscore", "Dentro de on Resume");
            comScore.onEnterForeground();
        }catch (Exception e){
            Log.i("Comscore", "Error Comscore");
            e.printStackTrace();
        }**/
    }

    @Override
    protected void onPause() {
        super.onPause();
       /** try{
            //comScore
            Log.i("Comscore", "Dentro de on Pause");
            comScore.onExitForeground();
        }catch (Exception e){
            Log.i("Comscore", "Error Comscore");
            e.printStackTrace();
        }**/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       /** try{
            //comScore
            Log.i("Comscore", "Se sale de la app con onDestroy");
            comScore.onExitForeground();
        }catch (Exception e){
            Log.i("Comscore", "Error Comscore");
            e.printStackTrace();
        }**/
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
}
