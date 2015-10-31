package es.elconfidencial.eleccionesgenerales2015.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import es.elconfidencial.eleccionesgenerales2015.R;
import es.elconfidencial.eleccionesgenerales2015.listeners.OnDislikeClickListener;
import es.elconfidencial.eleccionesgenerales2015.listeners.OnLikeClickListener;
import es.elconfidencial.eleccionesgenerales2015.model.GlobalMethod;

/**
 * A simple {@link Fragment} subclass.
 */
public class PresinderTab extends Fragment {

    TextView grupo,text,persona;
    Button like,dislike;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i("Presinder", "OnCreateView");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_presinder_tab, container, false);

        text = (TextView) v.findViewById(R.id.questionQuote);
        persona = (TextView) v.findViewById(R.id.personaText);
        grupo = (TextView) v.findViewById(R.id.groupQuote);

        like = (Button) v.findViewById(R.id.likeButton);
        dislike = (Button) v.findViewById(R.id.dislikeButton);

        like.setOnClickListener(new OnPresinderLikeClickListener(getContext()));
        dislike.setOnClickListener(new OnDislikeClickListener(getContext()));

        return v;
    }

    public void nextQuote(){
        text.setText(GlobalMethod.quotes.get(GlobalMethod.quotesIndex).getText());
        persona.setText(GlobalMethod.quotes.get(GlobalMethod.quotesIndex).getPersona());
        grupo.setText(GlobalMethod.quotes.get(GlobalMethod.quotesIndex).getGrupo());
    }

    public class OnPresinderLikeClickListener extends OnLikeClickListener {
        public OnPresinderLikeClickListener(Context context) {
            super(context);
        }

        @Override
        public void onClick(View v) {
            Activity act = (Activity) v.getContext();
            final Dialog settingsDialog = new Dialog(act);
            settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater inflater = LayoutInflater.from(v.getContext());
            settingsDialog.setContentView(inflater.inflate(R.layout.image_popup_layout
                    , null));
            settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            //Contador de 3 segundos
            settingsDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    //Contador 3,2,1 dismiss.
                }
            });

            //Al tocarlo se cierra
            settingsDialog.findViewById(R.id.image_dialog_root).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    settingsDialog.dismiss();
                }
            });
            settingsDialog.show();

            //Set imagen
            try {
                ImageView foto = (ImageView) settingsDialog.findViewById(R.id.foto);
                Glide.with(getContext()).load(R.drawable.nopicpersona).into(foto);
                //Fonts
            } catch (Exception e) {
                e.printStackTrace();
            }
            TextView title = (TextView) settingsDialog.findViewById(R.id.introText);
            TextView persona = (TextView) settingsDialog.findViewById(R.id.personaText);

            title.setText("Estás de acuerdo con:");
            persona.setText("Mariano Rajoy");
            //Fonts
            title.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Light.otf"));
            persona.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));

            //Aumentamos index siguiente pregunta
            GlobalMethod.saveIntPreference(getContext(), GlobalMethod.quotesIndex, "quotesIndex");
            GlobalMethod.quotesIndex = GlobalMethod.getIntPreference(getContext(), "quotesIndex", 0);
            nextQuote();
        }

    }
}
