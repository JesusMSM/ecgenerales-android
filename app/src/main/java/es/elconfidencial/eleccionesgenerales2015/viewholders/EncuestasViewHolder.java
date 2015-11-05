package es.elconfidencial.eleccionesgenerales2015.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;

import es.elconfidencial.eleccionesgenerales2015.R;

/**
 * Created by Jesus on 05/11/2015.
 */
public class EncuestasViewHolder extends RecyclerView.ViewHolder {
    public BarChart grafico;


    public EncuestasViewHolder(View v) {
        super(v);
        grafico = (BarChart) v.findViewById(R.id.chartEncuestas);
    }
}