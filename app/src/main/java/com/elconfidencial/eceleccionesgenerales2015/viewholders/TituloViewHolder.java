package com.elconfidencial.eceleccionesgenerales2015.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elconfidencial.eceleccionesgenerales2015.R;

/**
 * Created by Jesus on 03/11/2015.
 */
public class TituloViewHolder extends RecyclerView.ViewHolder{
    public TextView title;
    public LinearLayout link;
    public TituloViewHolder(View v) {
        super(v);
        title = (TextView) v.findViewById(R.id.title);
        link = (LinearLayout) v.findViewById(R.id.title_layout);

    }

}