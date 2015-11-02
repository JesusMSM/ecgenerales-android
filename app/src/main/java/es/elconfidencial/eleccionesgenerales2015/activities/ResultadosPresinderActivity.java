package es.elconfidencial.eleccionesgenerales2015.activities;

import android.content.Intent;
import android.graphics.Typeface;
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        System.gc();
        finish();
        super.onBackPressed();
    }
}
