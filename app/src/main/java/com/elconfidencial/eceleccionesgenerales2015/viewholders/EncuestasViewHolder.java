package com.elconfidencial.eceleccionesgenerales2015.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.charts.BarChartEC;

/**
 * Created by Jesus on 05/11/2015.
 */
public class EncuestasViewHolder extends RecyclerView.ViewHolder {
    public BarChartEC grafico;
    public TextView fecha,descripcion,estimacion_voto,fuente;


    public EncuestasViewHolder(View v) {
        super(v);
        grafico = (BarChartEC) v.findViewById(R.id.chartEncuestas);
        fecha = (TextView) v.findViewById(R.id.textView_fecha);
        descripcion = (TextView) v.findViewById(R.id.textView_descripcion);
        estimacion_voto = (TextView) v.findViewById(R.id.textView_estimacion_voto);
        fuente = (TextView) v.findViewById(R.id.textView_fuente);


    }
}