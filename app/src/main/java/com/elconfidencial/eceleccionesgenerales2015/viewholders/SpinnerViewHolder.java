package com.elconfidencial.eceleccionesgenerales2015.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.text.TitilliumSemiBoldTextView;

/**
 * Created by Jesus on 01/11/2015.
 */
public class SpinnerViewHolder extends RecyclerView.ViewHolder{

    public TextView selected;
    public LinearLayout layoutSpinner;
    public SpinnerViewHolder(View v) {
        super(v);
        selected = (TextView) v.findViewById(R.id.selectedPartido);
        layoutSpinner = (LinearLayout) v.findViewById(R.id.layoutSpinner);
    }
}
