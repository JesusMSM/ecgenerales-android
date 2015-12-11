package com.elconfidencial.eceleccionesgenerales2015.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.elconfidencial.eceleccionesgenerales2015.R;

import java.util.List;

/**
 * Created by jorge_cmata on 11/12/15.
 */
public class EncuestaSpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> data;
    LayoutInflater inflater;

    public EncuestaSpinnerAdapter(Context context, int textViewResourceId, List<String> objects) {
        super(context, textViewResourceId, objects);

        this.context = context;
        data = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.row_custom_spinner_encuesta, parent, false);

        TextView nombre = (TextView) row.findViewById(R.id.nombreEncuesta);

        // Set values for spinner each row
        nombre.setText(data.get(position));

        nombre.setTypeface(Typeface.createFromAsset(context.getApplicationContext().getAssets(), "Titillium-Semibold.otf"));
        return row;
    }
}