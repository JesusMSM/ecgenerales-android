package es.elconfidencial.eleccionesgenerales2015.activities;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.elconfidencial.eleccionesgenerales2015.R;
import es.elconfidencial.eleccionesgenerales2015.adapters.MyRecyclerViewAdapter;
import es.elconfidencial.eleccionesgenerales2015.model.Persona;
import es.elconfidencial.eleccionesgenerales2015.model.QuoteServer;

public class ResultadosPresinderActivity extends AppCompatActivity {

    //RecyclerView atributtes
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    QuoteServer qs = QuoteServer.getInstance();
    List<Object> items = new ArrayList<>();

    Button volver;

    //Compartir
    private File picFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados_presinder);

        //ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.custom_title_resultados_presinder, null);
        ((TextView) v.findViewById(R.id.actionBarTitle)).setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Light.otf"));
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.resultados_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(MainActivity.context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Initiamos la instancia del QuoteServer
        qs.init(this);

        //Listener del boton de retroceder
        volver = (Button) findViewById(R.id.volverAJugar);
        volver.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Regular.otf"));
        volver.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        //Ordenamos los politicos de la base de datos local
        Collections.sort(qs.personas, new PersonComparator());

        //Mostramos la lista
        for (Persona persona : qs.personas) {
            items.add(persona);
        }

        mAdapter = new MyRecyclerViewAdapter(MainActivity.context, items);
        mRecyclerView.setAdapter(mAdapter);


    }

    //Ordenamos la lista de politicos según el siguiente criterio:
    //Primero se tendrá en cuenta la diferencia entre Likes y Dislikes
    //En caso de empate, se tendrá en cuenta el número de Likes

    private class PersonComparator implements Comparator<Persona> {

        public int compare(Persona p1, Persona p2) {
            if(p1.getAgreeDisagreeDif() - p2.getAgreeDisagreeDif()!=0) {
                return p1.getAgreeDisagreeDif() - p2.getAgreeDisagreeDif();
            } else {
                return p1.getAgree() - p2.getDisagree();
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
        }
        if(id == R.id.share_result){
            shareit();
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

        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            File picDir  = new File(Environment.getExternalStorageDirectory()+ "/EG2015-EC");
            if (!picDir.exists())
            {
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
                    .obtainStyledAttributes(new int[] { android.R.attr.windowBackground });
            final int res = ta.getResourceId(0, 0);
            final Drawable background= ResultadosPresinderActivity.this.getResources().getDrawable(res);

            // Draw background
            background.draw(canvas);

            // Draw views
            view.draw(canvas);

            String fileName = "resultPicture" + ".jpg";
            picFile = new File(picDir + "/" + fileName);
            try
            {
                picFile.createNewFile();
                FileOutputStream picOut = new FileOutputStream(picFile);
                //bitmap.setDensity(view.getResources().getDisplayMetrics().densityDpi);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), (int)(bitmap.getHeight()));

                boolean saved = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, picOut);

                picOut.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            catch (OutOfMemoryError e2){
                e2.printStackTrace();
                Toast.makeText(getApplicationContext(),"Su dispositivo no puede compartir los resultados porque que dispone de poca memoria RAM. Cierre aplicaciones y vuelva a intentarlo",Toast.LENGTH_LONG).show();
            }
            view.destroyDrawingCache();
        } else {
            //Error

        }

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/jpeg");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Estos han sido mis resultados en Presinder EC. Descarga la app en: https://www.elconfidencial.com");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(picFile.getAbsolutePath()));
        startActivity(Intent.createChooser(sharingIntent, "Compartir"));
    }
}
