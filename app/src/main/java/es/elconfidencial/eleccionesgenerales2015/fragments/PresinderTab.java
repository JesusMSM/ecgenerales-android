package es.elconfidencial.eleccionesgenerales2015.fragments;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amplitude.api.Amplitude;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.elconfidencial.eleccionesgenerales2015.R;
import es.elconfidencial.eleccionesgenerales2015.activities.ResultadosPresinderActivity;
import es.elconfidencial.eleccionesgenerales2015.listeners.OnDislikeClickListener;
import es.elconfidencial.eleccionesgenerales2015.listeners.OnLikeClickListener;
import es.elconfidencial.eleccionesgenerales2015.model.GlobalMethod;
import es.elconfidencial.eleccionesgenerales2015.model.Quote;
import es.elconfidencial.eleccionesgenerales2015.model.QuoteServer;
import es.elconfidencial.eleccionesgenerales2015.model.TypeWriter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PresinderTab extends Fragment {

    TextView grupo,header1,likeText,dislikeText, actionBarTitle;
    TextView text;
    ImageView like,dislike;
    Button verResultados;
    QuoteServer qs = QuoteServer.getInstance();
    public PresinderTab(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i("Presinder", "OnCreateView");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_presinder_tab, container, false);

        actionBarTitle = (TextView) v.findViewById(R.id.actionBarPresinder);

        //Initiamos la instancia del QuoteServer
        qs.init(getContext());

        header1 = (TextView) v.findViewById(R.id.headerPresinder);
        text = (TextView) v.findViewById(R.id.questionQuote);
        grupo = (TextView) v.findViewById(R.id.groupQuote);
        likeText =(TextView) v.findViewById(R.id.aFavorTab);
        dislikeText =(TextView) v.findViewById(R.id.enContraTab);

        like = (ImageView) v.findViewById(R.id.likeButtonTab);
        dislike = (ImageView) v.findViewById(R.id.dislikeButtonTab);
        verResultados = (Button) v.findViewById(R.id.verResultadosButton);

        //Listeners de los botones de agree y disagree
        like.setOnClickListener(new OnPresinderLikeClickListener(getContext()));
        dislike.setOnClickListener(new OnPresinderDislikeClickListener(getContext()));

        //Listener del boton resultados
        verResultados.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ResultadosPresinderActivity.class);
                startActivity(intent);
            }
        });

        setFonts();
        setNextQuote();

        return v;
    }
    public void setFonts(){
        //Fonts
        header1.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Semibold.otf"));
        text.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-BoldItalic.otf"));
        grupo.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));
        verResultados.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));
        likeText.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));
        dislikeText.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));
        actionBarTitle.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));
    }

    public void setNextQuote(){
        Log.i("PRESINDER", "CurrentQuote: " + qs.quotes.get(qs.getQuotesIndex()).getText());
        Log.i("PRESINDER", "CurrentIndex: " + String.valueOf(qs.quotesIndex));

        text.setText(qs.quotes.get(qs.getQuotesIndex()).getText());
        grupo.setText(qs.quotes.get(qs.getQuotesIndex()).getGrupo());
        text.setX(5000);
        grupo.setX(5000);
        text.animate().translationX(0).setDuration(450).start();
        grupo.animate().translationX(0).setDuration(450).start();
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
            View popUp = inflater.inflate(R.layout.image_popup_layout, null);
            settingsDialog.setContentView(popUp);
            //BG
            LinearLayout root = (LinearLayout) popUp.findViewById(R.id.image_dialog_root);
            root.setBackgroundColor(Color.parseColor("#80D5EEC8"));//Like Green

            settingsDialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
            settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            //Indentificamos la quote actual
            Quote currentQuote = qs.quotes.get(qs.getQuotesIndex());
            //Agree con la quote actual
            if(GlobalMethod.getIntPreference(getContext(),"NoMoreQuotes",0) == 0) {
                //Si siguen quedando quotes (Evita votar repetitivamente en la ultima quote)
                qs.agreedWithQuote(currentQuote);
            }

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
                    }, 3000, 3000);
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
            if(GlobalMethod.getIntPreference(getContext(),"NoMoreQuotes",0) == 0) {
                settingsDialog.show();
            }
            qs.incrementQuotesIndex();


            /*RELLENAR CAMPOS DEL POP UP**/
            //Set imagen correspondiente
            try {
                ImageView foto = (ImageView) settingsDialog.findViewById(R.id.foto);
                Glide.with(getContext()).load(getContext().getResources().getIdentifier(qs.getPersonFromName(currentQuote.getPersona()).getPhotoLink(), "drawable", getContext().getPackageName())).into(foto);
                //Fonts
            } catch (Exception e) {
                e.printStackTrace();
            }
            TextView title = (TextView) settingsDialog.findViewById(R.id.introText);
            TextView persona = (TextView) settingsDialog.findViewById(R.id.personaText);
            TextView party = (TextView) settingsDialog.findViewById(R.id.partyText);

            title.setText(getResources().getString(R.string.agree));
            persona.setText(qs.getPersonFromName(currentQuote.getPersona()).getNiceName());
            party.setText(qs.getPersonFromName(currentQuote.getPersona()).getParty());

            //Fonts
            title.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Light.otf"));
            persona.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));
            party.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));

            //Amplitude
            Log.i("20D_AMPLITUDE", "ONAGREE: "+ currentQuote.getText() + " " + currentQuote.getPersona().toString());
            JSONObject eventProperties = new JSONObject();
            try {
                eventProperties.put("QUOTE", currentQuote.getText());
                eventProperties.put("PERSONA", currentQuote.getPersona().toString());
            } catch (JSONException exception) {
            }
            Amplitude.getInstance().logEvent("ONAGREE", eventProperties);
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
            View popUp = inflater.inflate(R.layout.image_popup_layout, null);
            settingsDialog.setContentView(popUp);
            //BG
            LinearLayout root = (LinearLayout) popUp.findViewById(R.id.image_dialog_root);
            root.setBackgroundColor(Color.parseColor("#80FFCDBD"));//Dislike Red

            settingsDialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
            settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            //Indentificamos la quote actual
            Quote currentQuote = qs.quotes.get(qs.getQuotesIndex());
            //Agree con la quote actual
            if(GlobalMethod.getIntPreference(getContext(),"NoMoreQuotes",0) == 0) {
                //Si siguen quedando quotes (Evita votar repetitivamente en la ultima quote)
                qs.disagreedWithQuote(currentQuote);
            }
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
                    }, 3000, 3000);
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
            if(GlobalMethod.getIntPreference(getContext(),"NoMoreQuotes",0) == 0) {
                settingsDialog.show();
            }
            qs.incrementQuotesIndex();

            //Set imagen correspondiente
            try {
                ImageView foto = (ImageView) settingsDialog.findViewById(R.id.foto);
                Glide.with(getContext()).load(getContext().getResources().getIdentifier(qs.getPersonFromName(currentQuote.getPersona()).getPhotoLink(), "drawable", getContext().getPackageName())).into(foto);
                //Fonts
            } catch (Exception e) {
                e.printStackTrace();
            }
            TextView title = (TextView) settingsDialog.findViewById(R.id.introText);
            TextView persona = (TextView) settingsDialog.findViewById(R.id.personaText);
            TextView party = (TextView) settingsDialog.findViewById(R.id.partyText);

            title.setText(getResources().getString(R.string.disagree));
            persona.setText(qs.getPersonFromName(currentQuote.getPersona()).getNiceName());
            party.setText(qs.getPersonFromName(currentQuote.getPersona()).getParty());

            //Fonts
            title.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Light.otf"));
            persona.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));
            party.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));
        }

        public class OnPresinderVerResultadosClickListener extends OnLikeClickListener {
            public OnPresinderVerResultadosClickListener(Context context) {
                super(context);
            }

            @Override
            public void onClick(View v) {

            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("PRESINDER","onResume");
        qs.init(getContext());
        setNextQuote();
    }
}
