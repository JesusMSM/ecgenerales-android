package com.elconfidencial.eceleccionesgenerales2015.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Spinner;

import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.charts.BarChartEC;

/**
 * Created by Jesus on 05/11/2015.
 */
public class EncuestasViewHolder extends RecyclerView.ViewHolder {
    public BarChartEC grafico;
    public Spinner spinner;

    public EncuestasViewHolder(View v) {
        super(v);
        grafico = (BarChartEC) v.findViewById(R.id.chartEncuestas);
        spinner = (Spinner) v.findViewById(R.id.spinnerEncuesta);
    }
}