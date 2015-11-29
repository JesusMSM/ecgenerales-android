package com.elconfidencial.eceleccionesgenerales2015.viewholders;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.elconfidencial.eceleccionesgenerales2015.R;

/**
 * Created by Moonfish on 28/10/15.
 */
public class NoticiaViewHolder extends RecyclerView.ViewHolder{

    public TextView titulo,autor;
    public ImageView imagen;
    public FloatingActionButton botonCompartir;

    public NoticiaViewHolder(View v) {
        super(v);
        titulo = (TextView) v.findViewById(R.id.titulo);
        autor = (TextView) v.findViewById(R.id.autor);
        imagen = (ImageView) v.findViewById(R.id.imagen);
        botonCompartir = (FloatingActionButton) v.findViewById(R.id.fab);
    }
}
