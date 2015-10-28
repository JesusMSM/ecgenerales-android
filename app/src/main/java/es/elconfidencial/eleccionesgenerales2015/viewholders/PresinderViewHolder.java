package es.elconfidencial.eleccionesgenerales2015.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import es.elconfidencial.eleccionesgenerales2015.R;

/**
 * Created by Afll on 28/10/2015.
 */
public class PresinderViewHolder extends RecyclerView.ViewHolder {

    public TextView group,question;
    public Button like,dislike;

    public PresinderViewHolder(View v) {
        super(v);
        group = (TextView) v.findViewById(R.id.groupQuote);
        question = (TextView) v.findViewById(R.id.questionQuote);
        like = (Button) v.findViewById(R.id.likeButton);
        dislike = (Button) v.findViewById(R.id.dislikeButton);
    }
}
