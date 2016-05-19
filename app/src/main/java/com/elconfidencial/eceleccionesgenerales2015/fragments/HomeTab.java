package com.elconfidencial.eceleccionesgenerales2015.fragments;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.amplitude.api.Amplitude;
import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.activities.MainActivity;
import com.elconfidencial.eceleccionesgenerales2015.activities.PreferencesActivity;
import com.elconfidencial.eceleccionesgenerales2015.adapters.MyRecyclerViewAdapter;
import com.elconfidencial.eceleccionesgenerales2015.json.JSONParser;
import com.elconfidencial.eceleccionesgenerales2015.model.CardPubli;
import com.elconfidencial.eceleccionesgenerales2015.model.DatosEncuentas;
import com.elconfidencial.eceleccionesgenerales2015.model.Encuesta;
import com.elconfidencial.eceleccionesgenerales2015.model.GlobalMethod;
import com.elconfidencial.eceleccionesgenerales2015.model.Noticia;
import com.elconfidencial.eceleccionesgenerales2015.model.PartidoEncuesta;
import com.elconfidencial.eceleccionesgenerales2015.model.QuoteServer;
import com.elconfidencial.eceleccionesgenerales2015.model.Titulo;
import com.elconfidencial.eceleccionesgenerales2015.rss.RssNoticiasParser;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeTab extends Fragment {

    List<Object> items = new ArrayList<>();
    QuoteServer qs = QuoteServer.getInstance();

    public static String rss_url = "http://rss.elconfidencial.com/tags/temas/elecciones-generales-2015-20-d-15300/";
    public static String encuestas_url = "http://datos.elconfidencial.com/app-elecciones-generales-2015-survey/survey.json";


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static ArrayList<Encuesta> encuestas = new ArrayList<>();
    public LinearLayout actionbar;
    ImageView preferences;
    TextView titleActionBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_tab, container, false);

        //Comprobamos si el contador se está mostrando, en caso negativo, no mostraremos la action bar
        if(!MainActivity.SHOW_TIMER){
            //Preferences
            actionbar = (LinearLayout) v.findViewById(R.id.actionbar_home);
            titleActionBar = (TextView) v.findViewById(R.id.actionBarHome);
            titleActionBar.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));
            preferences = (ImageView) v.findViewById(R.id.preferencesIcon);
            preferences.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), PreferencesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    //Amplitude
                    Log.i("20D_AMPLITUDE", "ONTAP_SETTINGS");
                    Amplitude.getInstance().logEvent("ONTAP_SETTINGS");
                }
            });
        }
        //RecyclerView
        mRecyclerView = (RecyclerView) v.findViewById(R.id.home_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        new JSONParse().execute();
        //new CargarXmlTask().execute(rss_url);
        return v;
    }



    private class JSONParse extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected JSONArray doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONArray json = jParser.getJSONFromUrl(encuestas_url);

            return json;
        }
        @Override
        protected void onPostExecute(JSONArray json) {
            encuestas.clear();

            ArrayList<String> titulos = new ArrayList<>();
            ArrayList<DatosEncuentas> datosEncuestas = new ArrayList<>();

            if(json!=null) {
                for (int i = 0; i < json.length(); i++) {

                    try {
                        ArrayList<PartidoEncuesta> partidoEncuestas = new ArrayList<>();
                        JSONObject encuestaGlobal = json.getJSONObject(i);
                        titulos.add(encuestaGlobal.getString("Name"));
                        JSONArray datos = encuestaGlobal.getJSONArray("Data");
                        for (int j = 0; j < datos.length(); j++) {
                            JSONObject duplaPartido = datos.getJSONObject(j);
                            JSONArray partido = duplaPartido.names();
                            String nombre = "";
                            double porcentaje = 0;
                            for (int k = 0; k < partido.length(); k++) {
                                nombre = partido.getString(k);
                                //Log.d("Encuestas", nombre);
                                porcentaje = duplaPartido.getDouble(nombre);
                                //Log.d("Encuestas", ""+porcentaje);


                            }
                            PartidoEncuesta partidoEncuesta = new PartidoEncuesta(nombre, porcentaje);
                            partidoEncuestas.add(partidoEncuesta);

                        }
                        Collections.sort(partidoEncuestas, new Comparator<PartidoEncuesta>() {
                            public int compare(PartidoEncuesta partido1, PartidoEncuesta partido2) {
                                return Double.compare(partido2.getPorcentaje(), partido1.getPorcentaje());
                            }
                        });
                        datosEncuestas.add(new DatosEncuentas(partidoEncuestas));


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    encuestas.add(new Encuesta(titulos.get(i), datosEncuestas.get(i).getDatosEncuesta()));
                }



                for (int n = 0; n < encuestas.size(); n++) {
                    Log.d("Encuestas", "El contenido de encuestas es: ");
                    Log.d("Encuestas", encuestas.get(n).getName());

                    for (int m = 0; m < encuestas.get(n).getPartidosEncuesta().size(); m++) {
                        Log.d("Encuestas", "Con el partido " + encuestas.get(n).getPartidosEncuesta().get(m).getName() + " y porcentaje " + encuestas.get(n).getPartidosEncuesta().get(m).getPorcentaje());
                    }
                }

            }
        }
    }

    /*Permite gestionar de forma asincrona el RSS */
    private class CargarXmlTask extends AsyncTask<String,Integer,Boolean> {

        List<Object> items = new ArrayList<>();
        List<Noticia> noticias = new ArrayList<>();
        GlobalMethod globalMethod = new GlobalMethod(getContext());


        protected Boolean doInBackground(String... params) {
            try {
                if(globalMethod.haveNetworkConnection()) {
                    RssNoticiasParser saxparser =
                            new RssNoticiasParser(params[0]);

                    noticias = saxparser.parse();

                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }
        protected void onPostExecute(Boolean result) {


            addItems();

        }

        public void addItems() {



            if (items.size() > 0) items.clear(); //Evitar duplicados

            if(MainActivity.SHOW_TIMER){
                items.add("contador");
            }else{
                actionbar.setVisibility(View.VISIBLE);
            }

            if(MainActivity.SHOW_SURVEYS){
                items.add(new Titulo(getString(R.string.titulo_encuestas)));
                items.add("encuestas");
            }

            items.add(new Titulo(getString(R.string.titulo_ultimas_noticias)));

            int listLength = MainActivity.LAST_NEWS_COUNTER; //número de noticias que deben aparecer
            for (int i = 0; i < listLength; i++) {
                if (i%MainActivity.DFP_CARD_EVERY_N==0&&i>0) items.add(new CardPubli());
                items.add(noticias.get(i));
            }

            items.add(qs.quotes.get(qs.getQuotesIndex()));

            /**    } else{
             //Mensaje de error
             Log.i("MyTag", "He pasado por el mensaje de error");
             }**/


            mAdapter = new MyRecyclerViewAdapter(getContext(),items);
            mRecyclerView.setAdapter(mAdapter);
        }

    }
}
