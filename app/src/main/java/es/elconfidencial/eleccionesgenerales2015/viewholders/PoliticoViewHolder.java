package es.elconfidencial.eleccionesgenerales2015.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import es.elconfidencial.eleccionesgenerales2015.R;

/**
 * Created by Moonfish on 31/10/15.
 */
public class PoliticoViewHolder extends RecyclerView.ViewHolder {

    public TextView nombre,partido,nLikes,nDislikes;


    public PoliticoViewHolder(View v) {
        super(v);
        nombre = (TextView) v.findViewById(R.id.nombrePolitico);
        partido = (TextView) v.findViewById(R.id.partidoPolitico);
        nLikes = (TextView) v.findViewById(R.id.nLikes);
        nDislikes = (TextView) v.findViewById(R.id.nDislikes);
    }
}
