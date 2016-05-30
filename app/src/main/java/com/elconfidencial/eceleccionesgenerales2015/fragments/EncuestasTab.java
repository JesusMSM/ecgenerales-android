package com.elconfidencial.eceleccionesgenerales2015.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.baoyz.widget.PullRefreshLayout;
import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.activities.ChooseActivity;
import com.elconfidencial.eceleccionesgenerales2015.activities.MainActivity;
import com.elconfidencial.eceleccionesgenerales2015.adapters.MyRecyclerViewAdapter;
import com.elconfidencial.eceleccionesgenerales2015.json.JSONParser;
import com.elconfidencial.eceleccionesgenerales2015.model.CardPubli;
import com.elconfidencial.eceleccionesgenerales2015.model.DatosEncuentas;
import com.elconfidencial.eceleccionesgenerales2015.model.Encuesta;
import com.elconfidencial.eceleccionesgenerales2015.model.GlobalMethod;
import com.elconfidencial.eceleccionesgenerales2015.model.Noticia;
import com.elconfidencial.eceleccionesgenerales2015.model.PartidoEncuesta;
import com.elconfidencial.eceleccionesgenerales2015.model.TituloEncuesta;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jorge_cmata on 12/5/16.
 */


public class EncuestasTab extends Fragment {

    //RecyclerView atributtes
    public static RecyclerView mRecyclerView;
    public static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<Object> items = new ArrayList<>();

    PullRefreshLayout layout;



    public EncuestasTab() {}

    public static EncuestasTab newInstance() {

        Bundle args = new Bundle();

        EncuestasTab fragment = new EncuestasTab();
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_encuestas_tab, container, false);
        //RecyclerView
        mRecyclerView = (RecyclerView) v.findViewById(R.id.encuestas_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        addItems();

        layout = (PullRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);

        // listen refresh event
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GlobalMethod globalMethod = new GlobalMethod(getContext());
                if (globalMethod.haveNetworkConnection()) {

                    new DownloadEncuestas().execute(ChooseActivity.encuestas_url);
                }

            }
        });

        return v;
    }

    public void addItems() {

        if (items.size()>0) items.clear();
        GlobalMethod globalMethod = new GlobalMethod(getContext());
        if (!globalMethod.haveNetworkConnection()){
            Gson gson = new Gson();
            SharedPreferences prefs = getContext().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
            String json = prefs.getString("encuestas", "");
            Type type = new TypeToken<ArrayList<Encuesta>>() {}.getType();
            ChooseActivity.encuestas = gson.fromJson(json, type);
        }

        if(ChooseActivity.SHOW_TIMER){
            items.add("contador");
        }

        if(ChooseActivity.encuestas!=null) {
            for (Encuesta encuesta : ChooseActivity.encuestas) {

                items.add(new TituloEncuesta(encuesta.getName()));
                Encuesta e = new Encuesta(encuesta.getName(), encuesta.getPartidosEncuesta());
                e.setFecha(encuesta.getFecha());
                e.setDescripcion(encuesta.getDescripcion());
                items.add(e);
            }
        }

        mAdapter = new MyRecyclerViewAdapter(getContext(),items);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        addItems();
    }

    private class DownloadEncuestas extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONArray doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONArray json = jParser.getJSONFromUrl(ChooseActivity.encuestas_url);

            return json;
        }
        @Override
        protected void onPostExecute(JSONArray json) {
            ChooseActivity.encuestas.clear();

            ArrayList<String> titulos = new ArrayList<>();
            ArrayList<String> fechas = new ArrayList<>();
            ArrayList<String> descripciones = new ArrayList<>();

            ArrayList<DatosEncuentas> datosEncuestas = new ArrayList<>();

            if(json!=null) {
                for (int i = 0; i < json.length(); i++) {

                    try {
                        ArrayList<PartidoEncuesta> partidoEncuestas = new ArrayList<>();
                        JSONObject encuestaGlobal = json.getJSONObject(i);
                        titulos.add(encuestaGlobal.getString("Source"));
                        fechas.add(encuestaGlobal.getString("Date"));
                        descripciones.add(encuestaGlobal.getString("Description"));
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

                    Encuesta e = new Encuesta(titulos.get(i), datosEncuestas.get(i).getDatosEncuesta());
                    e.setFecha(fechas.get(i));
                    e.setDescripcion(descripciones.get(i));

                    ChooseActivity.encuestas.add(e);
                }

                for (int n = 0; n < ChooseActivity.encuestas.size(); n++) {
                    Log.d("Encuestas", "El contenido de encuestas es: ");
                    Log.d("Encuestas", ChooseActivity.encuestas.get(n).getName());

                    for (int m = 0; m < ChooseActivity.encuestas.get(n).getPartidosEncuesta().size(); m++) {
                        Log.d("Encuestas", "Con el partido " + ChooseActivity.encuestas.get(n).getPartidosEncuesta().get(m).getName() + " y porcentaje " + ChooseActivity.encuestas.get(n).getPartidosEncuesta().get(m).getPorcentaje());
                    }
                }
                //Guardamos las encuestas
                SharedPreferences prefs = getContext().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = prefs.edit();
                Gson gson = new Gson();
                String json2 = gson.toJson(ChooseActivity.encuestas);
                prefsEditor.putString("encuestas", json2);
                prefsEditor.apply();
                //--------------------
                addItems();
                if(layout!=null) layout.setRefreshing(false);

            }
        }
    }
}
