package es.elconfidencial.eleccionesgenerales2015.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.Parse;
import com.parse.ParseAnalytics;

import es.elconfidencial.eleccionesgenerales2015.R;

public class ChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "fFMHyON2OrC3F161LgiepetpuB3WTktLvS6gq6ZH", "jqiMfz2BVxn4JNFhbsvscaEDg6QPObKn1JvGr0Wa");

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        //Comprueba si es la primera vez
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        boolean firstTime = prefs.getBoolean("firstTime", true); //Si no existe, devuelve el segundo parametro

        if(firstTime){
            Intent intent = new Intent(this, OnBoardingActivity.class);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
