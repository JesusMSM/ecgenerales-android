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

    public TextView group,question,likeText,dislikeText,header1,header2;
    public ImageView like,dislike;

    public PresinderViewHolder(View v) {
        super(v);
        header1 = (TextView) v.findViewById(R.id.headerPresinder);
        header2 = (TextView) v.findViewById(R.id.headerPresinder2);
        group = (TextView) v.findViewById(R.id.groupQuote);
        question = (TextView) v.findViewById(R.id.questionQuote);
        like = (ImageView) v.findViewById(R.id.likeButton);
        dislike = (ImageView) v.findViewById(R.id.dislikeButton);
        likeText = (TextView) v.findViewById(R.id.aFavor);
        dislikeText = (TextView) v.findViewById(R.id.enContra);
    }
}
