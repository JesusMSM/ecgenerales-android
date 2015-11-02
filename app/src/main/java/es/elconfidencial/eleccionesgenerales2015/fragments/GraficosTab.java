package es.elconfidencial.eleccionesgenerales2015.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import es.elconfidencial.eleccionesgenerales2015.R;
import es.elconfidencial.eleccionesgenerales2015.activities.MainActivity;
import es.elconfidencial.eleccionesgenerales2015.adapters.MyRecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraficosTab extends Fragment {


    //RecyclerView atributtes
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_graficos_tab, container, false);

        //RecyclerView
        mRecyclerView = (RecyclerView) v.findViewById(R.id.resultados_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(MainActivity.context);
        mRecyclerView.setLayoutManager(mLayoutManager);
       /** mAdapter = new MyRecyclerViewAdapter(MainActivity.context, getSampleArrayList());
        mRecyclerView.setAdapter(mAdapter);**/


        return v;
    }



}
