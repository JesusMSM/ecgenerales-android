package com.elconfidencial.eceleccionesgenerales2015.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.elconfidencial.eceleccionesgenerales2015.R;

/**
 * Created by Afll on 24/11/2015.
 */
public class FooterPresinderViewHolder extends RecyclerView.ViewHolder{
    public Button volverAJugar,reset;
    public FooterPresinderViewHolder(View itemView) {
        super(itemView);
        volverAJugar = (Button) itemView.findViewById(R.id.volverAJugar);
        reset = (Button) itemView.findViewById(R.id.resetPresinder);
    }
}
