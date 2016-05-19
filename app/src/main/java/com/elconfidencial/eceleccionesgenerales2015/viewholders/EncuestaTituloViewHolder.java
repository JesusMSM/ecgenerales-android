package com.elconfidencial.eceleccionesgenerales2015.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elconfidencial.eceleccionesgenerales2015.R;

/**
 * Created by Jesus on 19/05/2016.
 */
public class EncuestaTituloViewHolder extends RecyclerView.ViewHolder{
    public TextView nombre_encuesta,titulo;

    public EncuestaTituloViewHolder(View v) {
        super(v);
        nombre_encuesta = (TextView) v.findViewById(R.id.titulo);
        titulo = (TextView) v.findViewById(R.id.encuesta_del);




    }

}