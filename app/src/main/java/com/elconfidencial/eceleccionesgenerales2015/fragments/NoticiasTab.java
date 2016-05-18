package com.elconfidencial.eceleccionesgenerales2015.fragments;


import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import com.amplitude.api.Amplitude;
import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.activities.MainActivity;
import com.elconfidencial.eceleccionesgenerales2015.activities.PreferencesActivity;
import com.elconfidencial.eceleccionesgenerales2015.adapters.MyRecyclerViewAdapter;
import com.elconfidencial.eceleccionesgenerales2015.model.CardPubli;
import com.elconfidencial.eceleccionesgenerales2015.model.GlobalMethod;
import com.elconfidencial.eceleccionesgenerales2015.model.Noticia;
import com.elconfidencial.eceleccionesgenerales2015.rss.RssNoticiasParser;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoticiasTab extends Fragment {

    public static String rss_url = "http://rss.elconfidencial.com/tags/temas/elecciones-generales-2015-20-d-15300/";
    public static int seleccion = 0;

    private TextView actionBarTitle;

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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_noticias_tab, container, false);

        //RecyclerView
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rss_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(MainActivity.context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        new CargarXmlTask().execute(rss_url);


        return v;
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

            //if(MainActivity.SHOW_TIMER){
                items.add("contador");
            //}

            Spinner spinner = new Spinner(getActivity());
            items.add(spinner);

//            if(globalMethod.haveNetworkConnection()) {
            int i =0;
                for (Noticia noticia : noticias){
                    if (i%MainActivity.DFP_CARD_EVERY_N==0&&i>0) items.add(new CardPubli());
                    items.add(noticia);
                    i++;
                }
        /**    } else{
                //Mensaje de error
                Log.i("MyTag", "He pasado por el mensaje de error");
            }**/


            mAdapter = new MyRecyclerViewAdapter(MainActivity.context,items);
            mRecyclerView.setAdapter(mAdapter);
        }

        }

}
