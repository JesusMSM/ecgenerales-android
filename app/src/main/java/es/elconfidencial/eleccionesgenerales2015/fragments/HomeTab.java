package es.elconfidencial.eleccionesgenerales2015.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import es.elconfidencial.eleccionesgenerales2015.R;
import es.elconfidencial.eleccionesgenerales2015.activities.MainActivity;
import es.elconfidencial.eleccionesgenerales2015.adapters.MyRecyclerViewAdapter;
import es.elconfidencial.eleccionesgenerales2015.model.Quote;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeTab extends Fragment {

    List<Object> items = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_tab, container, false);


        //RecyclerView
        mRecyclerView = (RecyclerView) v.findViewById(R.id.home_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(MainActivity.context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        addItems();

        return v;
    }

    private void addItems() {

        if (items.size() > 0) items.clear(); //Evitar duplicados

        items.add(new Quote("\"Las repeticiones son el instrumento principal por el que se canaliza el fracaso escolar y acaban siendo unos causantes del abandono prematuro del sistema educativo\"","Mariano Rajoy","Educación"));

        mAdapter = new MyRecyclerViewAdapter(MainActivity.context,items);
        mRecyclerView.setAdapter(mAdapter);
    }
}
