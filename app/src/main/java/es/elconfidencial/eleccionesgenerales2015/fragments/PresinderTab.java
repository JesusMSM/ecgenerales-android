package es.elconfidencial.eleccionesgenerales2015.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.elconfidencial.eleccionesgenerales2015.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PresinderTab extends Fragment {


    public PresinderTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_presinder_tab, container, false);
    }


}
