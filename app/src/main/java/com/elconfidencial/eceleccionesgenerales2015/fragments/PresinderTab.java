package com.elconfidencial.eceleccionesgenerales2015.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.activities.ResultadosPresinderActivity;
import com.elconfidencial.eceleccionesgenerales2015.listeners.OnLikeClickListener;
import com.elconfidencial.eceleccionesgenerales2015.model.GlobalMethod;
import com.elconfidencial.eceleccionesgenerales2015.model.Quote;
import com.elconfidencial.eceleccionesgenerales2015.model.QuoteServer;

/**
 * A simple {@link Fragment} subclass.
 */
public class PresinderTab extends Fragment {

    TextView grupo,header1,testAfinidad,likeText,dislikeText, actionBarTitle;
    TextView text;
    ImageView like,dislike;
    Button verResultados,reiniciar;
    QuoteServer qs = QuoteServer.getInstance();

    public PresinderTab(){}

    public static PresinderTab newInstance() {

        Bundle args = new Bundle();

        PresinderTab fragment = new PresinderTab();
        fragment.setArguments(args);


        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i("Presinder", "OnCreateView");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_presinder_tab, container, false);

        //Initiamos la instancia del QuoteServer
        qs.init(getContext());

        header1 = (TextView) v.findViewById(R.id.headerPresinder);
        testAfinidad = (TextView) v.findViewById(R.id.testAfinidad);
        text = (TextView) v.findViewById(R.id.questionQuote);
        grupo = (TextView) v.findViewById(R.id.groupQuote);
        likeText =(TextView) v.findViewById(R.id.aFavorTab);
        dislikeText =(TextView) v.findViewById(R.id.enContraTab);

        like = (ImageView) v.findViewById(R.id.likeButtonTab);
        dislike = (ImageView) v.findViewById(R.id.dislikeButtonTab);
        verResultados = (Button) v.findViewById(R.id.verResultadosButton);
        reiniciar = (Button) v.findViewById(R.id.reiniciarButton);

        //Listeners de los botones de agree y disagree
        like.setOnClickListener(new OnPresinderLikeClickListener(getContext()));
        dislike.setOnClickListener(new OnPresinderDislikeClickListener(getContext()));

        //Listener del boton resultados
        verResultados.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ResultadosPresinderActivity.class);
                startActivity(intent);
                Amplitude.getInstance().logEvent("Tap_view_result");
            }
        });
        reiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Reiniciar")
                        .setMessage("Si reinicia el test se borrarán todos los resultados que tenga acumulados. ¿Está seguro?")
                        .setPositiveButton("REINICIAR", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Amplitude.getInstance().logEvent("Tap_reset");
                                qs.reset();
                                setNextQuote();
                            }
                        })
                        .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        text.setMovementMethod(new ScrollingMovementMethod());

        setFonts();
        GlobalMethod globalMethod = new GlobalMethod(getContext());
        setNextQuote();


        return v;
    }
    public void setFonts(){
        //Fonts
        header1.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Roboto-Black.ttf"));
        text.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Milio-Demibold-Italic.ttf"));
        grupo.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf"));
        testAfinidad.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf"));
        verResultados.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf"));
        reiniciar.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf"));
        likeText.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf"));
        dislikeText.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf"));
    }

    public void setNextQuote(){
        if(qs.quotes.size()>0) {
            Log.i("PRESINDER", "CurrentQuote: " + qs.quotes.get(qs.getQuotesIndex()).getText());
            Log.i("PRESINDER", "CurrentIndex: " + String.valueOf(qs.quotesIndex));
            Log.i("PRESINDER", "Count Quotes: " + String.valueOf(qs.quotes.size()));
            text.setText(qs.quotes.get(qs.getQuotesIndex()).getText());
            grupo.setText(qs.quotes.get(qs.getQuotesIndex()).getGrupo());
            text.setX(5000);
            grupo.setX(5000);
            text.animate().translationX(0).setDuration(450).start();
            grupo.animate().translationX(0).setDuration(450).start();
            like.setClickable(true);
            dislike.setClickable(true);
        }else{
            text.setText("Reinicia el test para empezarlo de nuevo.");
            grupo.setText("TEST FINALIZADO");
            like.setClickable(false);
            dislike.setClickable(false);
        }
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
            Amplitude.getInstance().logEvent("Tap_agree");

            Activity act = (Activity) v.getContext();
            final Dialog settingsDialog = new Dialog(act);
            settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater inflater = LayoutInflater.from(v.getContext());
            View popUp = inflater.inflate(R.layout.image_popup_layout, null);
            settingsDialog.setContentView(popUp);
            //BG
            LinearLayout root = (LinearLayout) popUp.findViewById(R.id.image_dialog_root);
            root.setBackgroundColor(Color.parseColor("#80277200"));//Like Green

            settingsDialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
            settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            //Indentificamos la quote actual
            Quote currentQuote = qs.quotes.get(qs.getQuotesIndex());
            //Agree con la quote actual
            //if(GlobalMethod.getIntPreference(getContext(),"NoMoreQuotes",0) == 0) {
                //Si siguen quedando quotes (Evita votar repetitivamente en la ultima quote)
                qs.agreedWithQuote(currentQuote);
            //}

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
                if(getContext().getResources().getIdentifier(qs.getPersonFromName(currentQuote.getPersona()).getPhotoLink(), "drawable", getContext().getPackageName()) != 0) {
                    //Glide.with(getContext()).load(getContext().getResources().getIdentifier(qs.getPersonFromName(currentQuote.getPersona()).getPhotoLink(), "drawable", getContext().getPackageName())).into(foto);
                    foto.setImageResource(getContext().getResources().getIdentifier(qs.getPersonFromName(currentQuote.getPersona()).getPhotoLink(), "drawable", getContext().getPackageName()));

                } else {
                    Glide.with(getContext()).load(R.drawable.default_img).into(foto);
                }
                ImageView icon = (ImageView) settingsDialog.findViewById(R.id.iconAnswer);
                icon.setImageResource(R.drawable.ic_agree_big);

                LinearLayout fondoColor = (LinearLayout) settingsDialog.findViewById(R.id.fondoColor);
                fondoColor.setBackgroundColor(getResources().getColor(R.color.color_agree));
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

            /**
            //Amplitude
            Log.i("20D_AMPLITUDE", "ONAGREE: "+ currentQuote.getText() + " " + currentQuote.getPersona().toString());
            JSONObject eventProperties = new JSONObject();
            try {
                eventProperties.put("QUOTE", currentQuote.getText());
                eventProperties.put("PERSONA", currentQuote.getPersona().toString());
            } catch (JSONException exception) {
            }
            //Amplitude.getInstance().logEvent("ONAGREE", eventProperties);**/
        }

    }

    //Dislike listener
    public class OnPresinderDislikeClickListener extends OnLikeClickListener {
        public OnPresinderDislikeClickListener(Context context) {
            super(context);
        }

        @Override
        public void onClick(View v) {
            Amplitude.getInstance().logEvent("Tap_disagree");

            Activity act = (Activity) v.getContext();
            final Dialog settingsDialog = new Dialog(act);
            settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater inflater = LayoutInflater.from(v.getContext());
            View popUp = inflater.inflate(R.layout.image_popup_layout, null);
            settingsDialog.setContentView(popUp);
            //BG
            LinearLayout root = (LinearLayout) popUp.findViewById(R.id.image_dialog_root);
            root.setBackgroundColor(Color.parseColor("#80D71400"));//Dislike Red

            settingsDialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
            settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            //Indentificamos la quote actual
            Quote currentQuote = qs.quotes.get(qs.getQuotesIndex());
            //Agree con la quote actual
            //if(GlobalMethod.getIntPreference(getContext(),"NoMoreQuotes",0) == 0) {
                //Si siguen quedando quotes (Evita votar repetitivamente en la ultima quote)
                qs.disagreedWithQuote(currentQuote);
            //}
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
                if(getContext().getResources().getIdentifier(qs.getPersonFromName(currentQuote.getPersona()).getPhotoLink(), "drawable", getContext().getPackageName()) != 0) {
                    //Glide.with(getContext()).load(getContext().getResources().getIdentifier(qs.getPersonFromName(currentQuote.getPersona()).getPhotoLink(), "drawable", getContext().getPackageName())).into(foto);
                    foto.setImageResource(getContext().getResources().getIdentifier(qs.getPersonFromName(currentQuote.getPersona()).getPhotoLink(), "drawable", getContext().getPackageName()));
                } else {
                    Glide.with(getContext()).load(R.drawable.default_img).into(foto);
                }
                ImageView icon = (ImageView) settingsDialog.findViewById(R.id.iconAnswer);
                icon.setImageResource(R.drawable.ic_disagree_big);
                LinearLayout fondoColor = (LinearLayout) settingsDialog.findViewById(R.id.fondoColor);
                fondoColor.setBackgroundColor(getResources().getColor(R.color.color_disagree));
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

            /**
            //Amplitude
            Log.i("20D_AMPLITUDE", "ONDISAGREE: "+ currentQuote.getText() + " " + currentQuote.getPersona().toString());
            JSONObject eventProperties = new JSONObject();
            try {
                eventProperties.put("QUOTE", currentQuote.getText());
                eventProperties.put("PERSONA", currentQuote.getPersona().toString());
            } catch (JSONException exception) {
            }
            //Amplitude.getInstance().logEvent("ONDISAGREE", eventProperties);**/
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

        //Amplitude
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put("page", "test");
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        Amplitude.getInstance().logEvent("page_view", eventProperties);


        //Amplitude
        JSONObject eventProperties1 = new JSONObject();
        try {
            eventProperties1.put("segment", "test");
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        Amplitude.getInstance().logEvent("Tap_menu", eventProperties1);

        //qs.init(getContext());
        //setNextQuote();
    }
}
