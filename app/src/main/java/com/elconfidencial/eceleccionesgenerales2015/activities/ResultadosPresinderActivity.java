package com.elconfidencial.eceleccionesgenerales2015.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.amplitude.api.Amplitude;
import com.comscore.analytics.comScore;
import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.adapters.MyRecyclerViewAdapter;
import com.elconfidencial.eceleccionesgenerales2015.model.Persona;
import com.elconfidencial.eceleccionesgenerales2015.model.QuoteServer;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultadosPresinderActivity extends AppCompatActivity {

    //RecyclerView atributtes
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    QuoteServer qs = QuoteServer.getInstance();
    List<Object> items = new ArrayList<>();

    //Compartir
    private File picFile;

    Toolbar toolbar;
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados_presinder);

        setupToolbar();

        //ActionBar
        /*getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.custom_title_resultados_presinder, null);
        ((TextView) v.findViewById(R.id.actionBarTitle)).setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Light.otf"));
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.resultados_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Initiamos la instancia del QuoteServer
        qs.init(this);

        //Ordenamos los politicos de la base de datos local
        Collections.sort(qs.personas, new PersonComparator());

        //Mostramos la lista
        for (Persona persona : qs.personas) {
            items.add(persona);
        }

        //Add footer
        //items.add("footerpresinder");

        mAdapter = new MyRecyclerViewAdapter(this, items);
        mRecyclerView.setAdapter(mAdapter);


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

    //Ordenamos la lista de politicos según el siguiente criterio:
    //Primero se tendrá en cuenta la diferencia entre Likes y Dislikes
    //En caso de empate, se tendrá en cuenta el número de Likes

    private class PersonComparator implements Comparator<Persona> {

        public int compare(Persona p1, Persona p2) {
            if(p1.getAgreeDisagreeDif() - p2.getAgreeDisagreeDif()!=0) {
                return p1.getAgreeDisagreeDif() - p2.getAgreeDisagreeDif();
            } else {
                return p2.getAgree() - p1.getAgree();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_resultados_presinder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            finish();
        }
        if(id == R.id.share_result){
            shareit();
            //Amplitude
            Log.i("20D_AMPLITUDE", "ONSHARE_PRESINDER");
            Amplitude.getInstance().logEvent("ONSHARE_PRESINDER");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        System.gc();
        finish();
        super.onBackPressed();
    }

    //Compartir
    public void shareit()
    {
        //View view =  findViewById(R.id.result);//your layout idr
        View view = getWindow().getDecorView();
        view.getRootView();
        String state = Environment.getExternalStorageState();
    try {
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File picDir = new File(Environment.getExternalStorageDirectory() + "/EG2015-EC");
            if (!picDir.exists()) {
                picDir.mkdir();
            }
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache(true);
            Bitmap bitmap = view.getDrawingCache();

            /**Funciones para detectar el tema y colores/imagenes de fondo**/
            final Canvas canvas = new Canvas(bitmap);

            // Get current theme to know which background to use
            final Resources.Theme theme = ResultadosPresinderActivity.this.getTheme();
            final TypedArray ta = theme
                    .obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
            final int res = ta.getResourceId(0, 0);
            final Drawable background = ResultadosPresinderActivity.this.getResources().getDrawable(res);

            // Draw background
            background.draw(canvas);

            // Draw views
            view.draw(canvas);

            String fileName = "resultPicture" + ".png";
            picFile = new File(picDir + "/" + fileName);
            try {
                picFile.createNewFile();
                FileOutputStream picOut = new FileOutputStream(picFile);

                //Toño
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(picFile.getPath(), options);

                bitmap.setDensity(view.getResources().getDisplayMetrics().densityDpi);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), (int) (bitmap.getHeight()));

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, picOut);

                picOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e2) {
                e2.printStackTrace();
                Toast.makeText(getApplicationContext(), "Out of memory", Toast.LENGTH_LONG).show();
            }
            view.destroyDrawingCache();
        } else {
            //Error

        }

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/png");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, ChooseActivity.PRESINDER_SHARE_MESSAGE_ANDROID);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(picFile.getAbsolutePath()));
        startActivity(Intent.createChooser(sharingIntent, "Compartir"));
    }catch (Exception e){

    }
    }

    private void setupToolbar() {
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(null);
            //actionBar.setHomeAsUpIndicator(R.drawable.elconfidencial_32dp_white);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_actionbar);
        }
    }
}
