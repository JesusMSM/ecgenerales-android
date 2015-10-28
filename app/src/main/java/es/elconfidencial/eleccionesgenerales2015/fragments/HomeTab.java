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
public class HomeTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home_tab, container, false);
        return v;
    }


}
