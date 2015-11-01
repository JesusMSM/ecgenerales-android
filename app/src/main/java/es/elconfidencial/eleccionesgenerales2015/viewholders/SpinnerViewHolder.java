package es.elconfidencial.eleccionesgenerales2015.viewholders;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import es.elconfidencial.eleccionesgenerales2015.R;

/**
 * Created by Jesus on 01/11/2015.
 */
public class SpinnerViewHolder extends RecyclerView.ViewHolder{

    public Spinner spinner;

    public SpinnerViewHolder(View v) {
        super(v);
        spinner = (Spinner) v.findViewById(R.id.spinnerPartido);

    }
}
