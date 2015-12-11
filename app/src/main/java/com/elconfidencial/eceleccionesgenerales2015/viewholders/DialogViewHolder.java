package com.elconfidencial.eceleccionesgenerales2015.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.elconfidencial.eceleccionesgenerales2015.R;

/**
 * Created by jorge_cmata on 11/12/15.
 */
public class DialogViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progress;
    public DialogViewHolder(View v) {
        super(v);
        progress = (ProgressBar) v.findViewById(R.id.progressBar);

    }
}
