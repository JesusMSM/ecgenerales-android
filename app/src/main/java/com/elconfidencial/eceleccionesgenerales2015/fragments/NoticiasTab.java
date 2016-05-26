package com.elconfidencial.eceleccionesgenerales2015.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.amplitude.api.Amplitude;
import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.activities.ChooseActivity;
import com.elconfidencial.eceleccionesgenerales2015.activities.MainActivity;
import com.elconfidencial.eceleccionesgenerales2015.activities.PreferencesActivity;
import com.elconfidencial.eceleccionesgenerales2015.adapters.MyRecyclerViewAdapter;
import com.elconfidencial.eceleccionesgenerales2015.model.CardPubli;
import com.elconfidencial.eceleccionesgenerales2015.model.GlobalMethod;
import com.elconfidencial.eceleccionesgenerales2015.model.Noticia;
import com.elconfidencial.eceleccionesgenerales2015.rss.RssNoticiasParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoticiasTab extends Fragment {

    public static String rss_url = "http://rss.elconfidencial.com/tags/temas/elecciones-generales-2015-20-d-15300/";
    public static int seleccion = 0;

    private TextView actionBarTitle;

    Context context;

    List<Object> items = new ArrayList<>();
    ArrayList<Noticia> noticias = new ArrayList<>();

    //RecyclerView atributtes
    public static RecyclerView mRecyclerView;
    public static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageView prefIcon;

    public NoticiasTab() {}

    public static NoticiasTab newInstance() {

        Bundle args = new Bundle();

        NoticiasTab fragment = new NoticiasTab();
        fragment.setArguments(args);


        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_noticias_tab, container, false);

        //RecyclerView
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rss_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        GlobalMethod globalMethod = new GlobalMethod(getContext());
        if(noticias.size()==0){
            if(globalMethod.haveNetworkConnection()) {
                new CargarXmlTask().execute(rss_url);
            }else{ //carga de cache
                Gson gson = new Gson();
                SharedPreferences prefs = getContext().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                String json = prefs.getString("noticias", "");
                Type type = new TypeToken<ArrayList<Noticia>>() {}.getType();
                noticias = gson.fromJson(json, type);
                addItems();
            }
        } else {
            addItems();
        }
    }

    /*Permite gestionar de forma asincrona el RSS */
    private class CargarXmlTask extends AsyncTask<String,Integer,Boolean> {


        GlobalMethod globalMethod = new GlobalMethod(getContext());
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getContext(), "Cargando", "Espere unos instantes");

        }

        protected Boolean doInBackground(String... params) {
            try {
                if(globalMethod.haveNetworkConnection()) {
                    RssNoticiasParser saxparser =
                            new RssNoticiasParser(params[0]);

                    SharedPreferences prefs = getContext().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                    noticias = (ArrayList<Noticia>) saxparser.parse();

                    //Guardamos las noticias
                    SharedPreferences.Editor prefsEditor = prefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(noticias);
                    prefsEditor.putString("noticias", json);
                    prefsEditor.apply();
                    //--------------------
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }
        protected void onPostExecute(Boolean result) {

            dismissProgressDialog();
            addItems();

            }

        public void dismissProgressDialog() {

            if (progressDialog!=null) {
                progressDialog.dismiss();
            }
        }

        }


    public void addItems() {

        //if(MainActivity.SHOW_TIMER){
        items.add("contador");
        //}

        Spinner spinner = new Spinner(context);
        items.add(spinner);

//            if(globalMethod.haveNetworkConnection()) {
        int i =0;
        for (Noticia noticia : noticias){
            if (i% ChooseActivity.DFP_CARD_EVERY_N==0&&i>0) items.add(new CardPubli());
            items.add(noticia);
            i++;
        }
        /**    } else{
         //Mensaje de error
         Log.i("MyTag", "He pasado por el mensaje de error");
         }**/


        mAdapter = new MyRecyclerViewAdapter(getContext(),items);
        mRecyclerView.setAdapter(mAdapter);
    }

    // region SAVE INSTANCE STATE
    //----------------------------------------------------------------------

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("noticias", noticias);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null){
            noticias = savedInstanceState.getParcelableArrayList("noticias");
        }

    }

    //----------------------------------------------------------------------
    //endregion

}
