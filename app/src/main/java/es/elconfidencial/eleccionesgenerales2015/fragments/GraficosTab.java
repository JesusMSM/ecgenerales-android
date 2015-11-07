package es.elconfidencial.eleccionesgenerales2015.fragments;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import es.elconfidencial.eleccionesgenerales2015.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraficosTab extends Fragment {


    private TextView actionBarTitle;
    Context context;

    private ImageView pp,cs,psoe,podemos,iu,pnv,convergencia,upyd,otros;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_graficos_tab, container, false);

        //actionBar
        actionBarTitle = (TextView) v.findViewById(R.id.actionBarResultados);
        actionBarTitle.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));

        //Inicializamos layout: NO día de las elecciones - No se ha votado

        //Imagenes
        pp = (ImageView) v.findViewById(R.id.ppLogo);
        cs = (ImageView) v.findViewById(R.id.cslogo);
        psoe = (ImageView) v.findViewById(R.id.psoelogo);
        podemos = (ImageView) v.findViewById(R.id.podemosLogo);
        iu = (ImageView) v.findViewById(R.id.iulogo);
        pnv = (ImageView) v.findViewById(R.id.pnvlogo);
        convergencia = (ImageView) v.findViewById(R.id.convergenciaLogo);
        upyd = (ImageView) v.findViewById(R.id.upydlogo);
        otros = (ImageView) v.findViewById(R.id.otroslogo);



        return v;
    }



}
