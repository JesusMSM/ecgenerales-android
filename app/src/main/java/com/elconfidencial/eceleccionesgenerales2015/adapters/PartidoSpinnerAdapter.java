package com.elconfidencial.eceleccionesgenerales2015.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import com.elconfidencial.eceleccionesgenerales2015.R;

/**
 * Created by Jesus on 01/11/2015.
 */
public class PartidoSpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> data;
    LayoutInflater inflater;

    public PartidoSpinnerAdapter(Context context, int textViewResourceId,List<String> objects) {
        super(context, textViewResourceId, objects);

        this.context=context;
        data     = objects;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.row_custom_spinner_partido, parent, false);

        TextView nombre = (TextView)row.findViewById(R.id.nombrePartido);

        // Set values for spinner each row
        nombre.setText(data.get(position));

        nombre.setTypeface(Typeface.createFromAsset(context.getApplicationContext().getAssets(), "Titillium-Semibold.otf"));
        return row;
    }
}