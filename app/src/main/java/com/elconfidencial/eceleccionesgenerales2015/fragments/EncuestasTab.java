package com.elconfidencial.eceleccionesgenerales2015.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elconfidencial.eceleccionesgenerales2015.R;

/**
 * Created by jorge_cmata on 12/5/16.
 */
public class EncuestasTab extends Fragment {

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

        return v;
    }
}
