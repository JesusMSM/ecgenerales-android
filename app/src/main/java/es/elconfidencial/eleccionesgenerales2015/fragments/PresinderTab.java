package es.elconfidencial.eleccionesgenerales2015.fragments;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.WindowCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import es.elconfidencial.eleccionesgenerales2015.R;
import es.elconfidencial.eleccionesgenerales2015.listeners.OnDislikeClickListener;
import es.elconfidencial.eleccionesgenerales2015.listeners.OnLikeClickListener;
import es.elconfidencial.eleccionesgenerales2015.model.GlobalMethod;
import es.elconfidencial.eleccionesgenerales2015.model.Quote;
import es.elconfidencial.eleccionesgenerales2015.model.QuoteServer;

/**
 * A simple {@link Fragment} subclass.
 */
public class PresinderTab extends Fragment {

    TextView grupo,text,header1,header2;
    Button like,dislike,verResultados;
    QuoteServer qs = QuoteServer.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i("Presinder", "OnCreateView");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_presinder_tab, container, false);

        //Initiamos la instancia del QuoteServer
        qs.init(getContext());

        header1 = (TextView) v.findViewById(R.id.headerPresinder);
        header2 = (TextView) v.findViewById(R.id.headerPresinder2);
        text = (TextView) v.findViewById(R.id.questionQuote);
        grupo = (TextView) v.findViewById(R.id.groupQuote);

        like = (Button) v.findViewById(R.id.likeButton);
        dislike = (Button) v.findViewById(R.id.dislikeButton);
        verResultados = (Button) v.findViewById(R.id.verResultadosButton);

        like.setOnClickListener(new OnPresinderLikeClickListener(getContext()));
        dislike.setOnClickListener(new OnPresinderDislikeClickListener(getContext()));

        setFonts();
        setNextQuote();

        return v;
    }
    public void setFonts(){
        //Fonts
        header1.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Semibold.otf"));
        header2.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Semibold.otf"));
        text.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-BoldItalic.otf"));
        grupo.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));
        verResultados.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));
    }

    public void setNextQuote(){
        text.setText("\"" + qs.quotes.get(qs.getQuotesIndex()).getText() + "\"");
        grupo.setText(qs.quotes.get(qs.getQuotesIndex()).getGrupo());
    }

    public void resetPresinder(){
        GlobalMethod.likesCount = new HashMap<>();
        GlobalMethod.dislikesCount = new HashMap<>();
        qs.quotesIndex = 0;
        //Save
        GlobalMethod.saveIntPreference(getContext(),qs.quotesIndex,"quotesIndex");
        GlobalMethod.putMyHashmap(getContext(), "likesCount", GlobalMethod.likesCount);
        GlobalMethod.putMyHashmap(getContext(), "dislikesCount", GlobalMethod.dislikesCount);
    }
    /***********LISTENERS******************/

    //Like Listener
    public class OnPresinderLikeClickListener extends OnLikeClickListener {
        public OnPresinderLikeClickListener(Context context) {
            super(context);
        }

        @Override
        public void onClick(View v) {
            //Abrimos el dialog con la persona correspondiente
            Activity act = (Activity) v.getContext();
            final Dialog settingsDialog = new Dialog(act);
            settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater inflater = LayoutInflater.from(v.getContext());
            settingsDialog.setContentView(inflater.inflate(R.layout.image_popup_layout
                    , null));
            settingsDialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
            settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            //Indentificamos la quote actual
            Quote currentQuote = qs.quotes.get(qs.getQuotesIndex());
            //Agree con la quote actual
            qs.agreedWithQuote(currentQuote);

            /**LISTENERS**/
            //Contador de 3 segundos
            settingsDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    //Contador 3,2,1 dismiss.
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        public void run() {

                            // add your stuff here
                            settingsDialog.dismiss();
                        }
                    }, 1500, 1500);
                }
            });
            settingsDialog.findViewById(R.id.image_dialog_root).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    settingsDialog.dismiss();
                }
            });
            //Al tocarlo se cierra
            settingsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    setNextQuote();
                    settingsDialog.dismiss();
                }
            });
            settingsDialog.show();

            /*RELLENAR CAMPOS DEL POP UP**/
            //Set imagen correspondiente
            try {
                ImageView foto = (ImageView) settingsDialog.findViewById(R.id.foto);
                Glide.with(getContext()).load(R.drawable.nopicpersona).into(foto);
                //Fonts
            } catch (Exception e) {
                e.printStackTrace();
            }
            TextView title = (TextView) settingsDialog.findViewById(R.id.introText);
            TextView persona = (TextView) settingsDialog.findViewById(R.id.personaText);
            TextView party = (TextView) settingsDialog.findViewById(R.id.partyText);

            title.setText("Parece que estás de acuerdo con:");
            persona.setText(currentQuote.getPersona());
            party.setText(qs.getPersonFromName(currentQuote.getPersona()).getParty());

            //Fonts
            title.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Light.otf"));
            persona.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));
            party.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));
        }

    }

    //Dislike listener
    public class OnPresinderDislikeClickListener extends OnLikeClickListener {
        public OnPresinderDislikeClickListener(Context context) {
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
            settingsDialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
            settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            //Indentificamos la quote actual
            Quote currentQuote = qs.quotes.get(qs.getQuotesIndex());
            //Agree con la quote actual
            qs.disagreedWithQuote(currentQuote);

            /**LISTENERS**/
            //Contador de 3 segundos
            settingsDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    //Contador 3,2,1 dismiss.
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        public void run() {

                            // add your stuff here
                            settingsDialog.dismiss();
                        }
                    }, 1500, 1500);
                }
            });
            settingsDialog.findViewById(R.id.image_dialog_root).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    settingsDialog.dismiss();
                }
            });
            //Al tocarlo se cierra
            settingsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    setNextQuote();
                    settingsDialog.dismiss();
                }
            });
            settingsDialog.show();

            //Set imagen correspondiente
            try {
                ImageView foto = (ImageView) settingsDialog.findViewById(R.id.foto);
                Glide.with(getContext()).load(R.drawable.nopicpersona).into(foto);
                //Fonts
            } catch (Exception e) {
                e.printStackTrace();
            }
            TextView title = (TextView) settingsDialog.findViewById(R.id.introText);
            TextView persona = (TextView) settingsDialog.findViewById(R.id.personaText);
            TextView party = (TextView) settingsDialog.findViewById(R.id.partyText);

            title.setText("No estás de acuerdo con:");
            persona.setText(currentQuote.getPersona());
            party.setText(qs.getPersonFromName(currentQuote.getPersona()).getParty());
            //Fonts
            title.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Light.otf"));
            persona.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));
            party.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));
        }

    }
}
