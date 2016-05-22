package com.elconfidencial.eceleccionesgenerales2015.fragments;

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

import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.activities.ChooseActivity;
import com.elconfidencial.eceleccionesgenerales2015.activities.MainActivity;
import com.elconfidencial.eceleccionesgenerales2015.adapters.MyRecyclerViewAdapter;
import com.elconfidencial.eceleccionesgenerales2015.json.JSONParser;
import com.elconfidencial.eceleccionesgenerales2015.model.CardPubli;
import com.elconfidencial.eceleccionesgenerales2015.model.DatosEncuentas;
import com.elconfidencial.eceleccionesgenerales2015.model.Encuesta;
import com.elconfidencial.eceleccionesgenerales2015.model.Noticia;
import com.elconfidencial.eceleccionesgenerales2015.model.PartidoEncuesta;
import com.elconfidencial.eceleccionesgenerales2015.model.TituloEncuesta;

import org.json.JSONArray;
import org.json.JSONObject;

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

        return v;
    }

    public void addItems() {

        if (items.size()>0) items.clear();

        //if(MainActivity.SHOW_TIMER){
            items.add("contador");
        //}

        for (Encuesta encuesta: ChooseActivity.encuestas){

            items.add(new TituloEncuesta(encuesta.getName()));
            Encuesta e = new Encuesta(encuesta.getName(),encuesta.getPartidosEncuesta());
            e.setFecha(encuesta.getFecha());
            e.setDescripcion(encuesta.getDescripcion());
            items.add(e);
        }

        mAdapter = new MyRecyclerViewAdapter(getContext(),items);
        mRecyclerView.setAdapter(mAdapter);
    }
}
