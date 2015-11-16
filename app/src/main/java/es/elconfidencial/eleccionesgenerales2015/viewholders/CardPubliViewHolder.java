package es.elconfidencial.eleccionesgenerales2015.viewholders;

import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.doubleclick.PublisherAdView;

import es.elconfidencial.eleccionesgenerales2015.R;

/**
 * Created by Jesus on 12/11/2015.
 */
public class CardPubliViewHolder extends RecyclerView.ViewHolder{

    public TextView empresa;
    public ImageView imagen;


    public CardPubliViewHolder(View v) {
        super(v);
        empresa = (TextView) v.findViewById(R.id.textViewEmpresa);
        imagen = (ImageView) v.findViewById(R.id.imageView);

    }
}
