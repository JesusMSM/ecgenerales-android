package com.elconfidencial.eceleccionesgenerales2015.viewholders;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.text.TitilliumLightTextView;

/**
 * Created by Moonfish on 28/10/15.
 */
public class NoticiaViewHolder extends RecyclerView.ViewHolder{

    public TextView titulo,tag,descripcion,timeAgo;
    public ImageView imagen;
    public ImageView botonCompartir;

    public NoticiaViewHolder(View v) {
        super(v);
        titulo = (TextView) v.findViewById(R.id.titulo);
        tag = (TextView) v.findViewById(R.id.tag);
        timeAgo = (TextView) v.findViewById(R.id.timeAgo);
        descripcion = (TextView) v.findViewById(R.id.descripcion);
        imagen = (ImageView) v.findViewById(R.id.imagen);
        botonCompartir = (ImageView) v.findViewById(R.id.compartir);
    }
}
