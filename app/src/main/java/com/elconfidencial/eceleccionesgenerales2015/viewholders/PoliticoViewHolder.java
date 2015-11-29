package com.elconfidencial.eceleccionesgenerales2015.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elconfidencial.eceleccionesgenerales2015.R;

/**
 * Created by Moonfish on 31/10/15.
 */
public class PoliticoViewHolder extends RecyclerView.ViewHolder {

    public TextView nombre,partido,nAgrees,nDisAgrees,posicion;
    public ImageView fotoPolitico, likesPolitico, dislikesPolitico;
    public LinearLayout rowPolitico;

    public PoliticoViewHolder(View v) {
        super(v);
        posicion = (TextView) v.findViewById(R.id.posicion);
        nombre = (TextView) v.findViewById(R.id.nombrePolitico);
        partido = (TextView) v.findViewById(R.id.partidoPolitico);
        nAgrees = (TextView) v.findViewById(R.id.nAgrees);
        nDisAgrees = (TextView) v.findViewById(R.id.nDisAgrees);
        fotoPolitico = (ImageView) v.findViewById(R.id.fotoPolitico);
        likesPolitico = (ImageView) v.findViewById(R.id.likesPolitico);
        dislikesPolitico = (ImageView) v.findViewById(R.id.dislikesPolitico);
        rowPolitico = (LinearLayout) v.findViewById(R.id.rowPolitico);
    }
}
