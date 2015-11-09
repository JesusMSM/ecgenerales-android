package es.elconfidencial.eleccionesgenerales2015.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import es.elconfidencial.eleccionesgenerales2015.R;
import es.elconfidencial.eleccionesgenerales2015.activities.MainActivity;
import es.elconfidencial.eleccionesgenerales2015.model.Partido;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraficosTab extends Fragment {


    private LinearLayout gridMegaencuesta, graficoMegaencuesta;
    private TextView actionBarTitle, headerEncuesta, graciasPorParticipar;
    HorizontalBarChart grafico;
    Context context;
    private View v;
    private int partidoMarcado;

    private ImageView pp,cs,psoe,podemos,iu,pnv,convergencia,upyd,otros;
    private Button vota;


    //Variables globales para gráfico
    ArrayList<Integer> nVotosList = new ArrayList<Integer>();
    ArrayList<String> partidosStringList = new ArrayList<String>();
    ArrayList<String> coloresList = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_graficos_tab, container, false);

        //actionBar
        actionBarTitle = (TextView) v.findViewById(R.id.actionBarResultados);
        actionBarTitle.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));

        //Inicializamos parse
        try {
            Parse.enableLocalDatastore(v.getContext());
            //Autenticacion con Parse
            Parse.initialize(context, "fFMHyON2OrC3F161LgiepetpuB3WTktLvS6gq6ZH", "jqiMfz2BVxn4JNFhbsvscaEDg6QPObKn1JvGr0Wa");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Inicializamos los Layout correspondientes a cada una de las screens
        gridMegaencuesta = (LinearLayout) v.findViewById(R.id.gridMegaencuestaScreen);
        graficoMegaencuesta = (LinearLayout) v.findViewById(R.id.graficoMegaencuestaScreen);

        //Comprobamos que layout debemos mostrar y cuales deben aparecer ocultos
        SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        boolean hasVoted = prefs.getBoolean("hasVoted", false ); //Si no existe, devuelve el segundo parametro
        if(hasVoted){
            //Cargamos la vista del gráfico
            gridMegaencuesta.setVisibility(View.GONE);
            graficoMegaencuesta.setVisibility(View.VISIBLE);
            setGraficoMegaencuesta();
        }else{
            //Cargamos la vista del Grid
            gridMegaencuesta.setVisibility(View.VISIBLE);
            graficoMegaencuesta.setVisibility(View.GONE);
            setGridMegaencuestaLayout();
        }



        return v;
    }

    /**
     * Este método carga en pantalla el layout correspondiente a:
     *  - El usuario aún no ha votado en la encuesta
     *  - No es el día de las elecciones, por lo que el webview permanece oculto
     */
    public void setGridMegaencuestaLayout(){

        headerEncuesta = (TextView) v.findViewById(R.id.headerEncuesta);
        headerEncuesta.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));

        pp = (ImageView) v.findViewById(R.id.ppLogo);
        cs = (ImageView) v.findViewById(R.id.cslogo);
        psoe = (ImageView) v.findViewById(R.id.psoelogo);
        podemos = (ImageView) v.findViewById(R.id.podemosLogo);
        iu = (ImageView) v.findViewById(R.id.iulogo);
        pnv = (ImageView) v.findViewById(R.id.pnvlogo);
        convergencia = (ImageView) v.findViewById(R.id.convergenciaLogo);
        upyd = (ImageView) v.findViewById(R.id.upydlogo);
        otros = (ImageView) v.findViewById(R.id.otroslogo);

        vota = (Button) v.findViewById(R.id.votaButton);
        vota.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));

        //Cargamos todas las imagenes grises (not pressed)
        setNotPressedImages();

        //Listener de las imagenes de los partidos
        setListenersImgPartidos();


        //Listener del botón votar
        vota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (partidoMarcado != -1) {
                    //Marcar como que ha votado
                    //Cargamos las preferencias compartidas, es como la base de datos para guardarlas y que se recuerden mas tarde
                    SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("hasVoted", true); //Lo guardamos para recordarlo
                    editor.apply(); //Guardamos las SharedPreferences

                    //Comunicacion con Parse.com
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("PARTY_SURVEY");
                    Log.i("Megaencuesta", getNamePartidoMarcado());
                    query.whereEqualTo("PARTIDO", getNamePartidoMarcado()); //Averiguamos el partido que se encuentra marcado
                    // Retrieve the object by id
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject partyResult, ParseException e) {
                            if (e == null) {
                                try {
                                    partyResult.increment("COUNT");
                                    partyResult.save();

                                    //Cargamos la vista del gráfico
                                    gridMegaencuesta.setVisibility(View.GONE);
                                    graficoMegaencuesta.setVisibility(View.VISIBLE);
                                    setGraficoMegaencuesta();

                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        });

    }



    /**
     * Este método carga en pantalla el layout correspondiente a:
     *  - El usuario SÍ ha votado en la encuesta
     *  - No es el día de las elecciones, por lo que el webview permanece oculto
     */
    public void setGraficoMegaencuesta(){

        graciasPorParticipar = (TextView) v.findViewById(R.id.gracias);
        grafico = (HorizontalBarChart) v.findViewById(R.id.horizontalBarChart);

        //Nos bajamos los resultados de Parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("PARTY_SURVEY");
        try {
            List<ParseObject> pList = query.find();
            for (ParseObject partidoPObj : pList) {
                Log.i("Megaencuesta", "" + partidoPObj.getString("PARTIDO") + " : " + partidoPObj.getInt("COUNT"));
                for(Partido partidoObject: ((MainActivity)getActivity()).getPartidosList()){
                    //Si coinciden las ids de los partidos de la lista recibida de Parse y la local global
                    //Rellenamos las 3 necesarias para pintar nuestro gráfico
                    if(partidoObject.getId().equals(partidoPObj.getString("PARTIDO"))){
                        partidosStringList.add(partidoObject.getSiglas());
                        nVotosList.add(partidoPObj.getInt("COUNT"));
                        coloresList.add(partidoObject.getColor());
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("Megaencuesta", "Error: " + e.getMessage());
        }
    }



    public void setNotPressedImages(){
        //Cargamos las imagenes not pressed
        Glide.with(getContext()).load(R.drawable.nopicpersona).placeholder(R.drawable.nopicpersona).into(pp);
        Glide.with(getContext()).load(R.drawable.nopicpersona).placeholder(R.drawable.nopicpersona).into(cs);
        Glide.with(getContext()).load(R.drawable.nopicpersona).placeholder(R.drawable.nopicpersona).into(psoe);
        Glide.with(getContext()).load(R.drawable.nopicpersona).placeholder(R.drawable.nopicpersona).into(podemos);
        Glide.with(getContext()).load(R.drawable.nopicpersona).placeholder(R.drawable.nopicpersona).into(iu);
        Glide.with(getContext()).load(R.drawable.nopicpersona).placeholder(R.drawable.nopicpersona).into(pnv);
        Glide.with(getContext()).load(R.drawable.nopicpersona).placeholder(R.drawable.nopicpersona).into(convergencia);
        Glide.with(getContext()).load(R.drawable.nopicpersona).placeholder(R.drawable.nopicpersona).into(upyd);
        Glide.with(getContext()).load(R.drawable.nopicpersona).placeholder(R.drawable.nopicpersona).into(otros);
    }

    public void setListenersImgPartidos(){

        pp.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPartido(0);
            }
        });
        cs.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPartido(1);
            }
        });
        psoe.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPartido(2);
            }
        });
        podemos.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPartido(3);
            }
        });
        iu.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPartido(4);
            }
        });
        pnv.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPartido(5);
            }
        });
        convergencia.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPartido(6);
            }
        });
        upyd.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPartido(7);
            }
        });
        otros.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPartido(8);
            }
        });
    }

    public String getNamePartidoMarcado (){
        String partidoMarcadoName = "";
        switch (partidoMarcado) {
            case 0: //PP
                partidoMarcadoName = "PP";
                break;
            case 1: //Ciudadanos
                partidoMarcadoName = "CIUDADANOS";
                break;
            case 2: //PSOE
                partidoMarcadoName = "PSOE";
                break;
            case 3: //PODEMOS
                partidoMarcadoName = "PODEMOS";
                break;
            case 4: //IU
                partidoMarcadoName = "IU";
                break;
            case 5: //PNV
                partidoMarcadoName = "PNV";
                break;
            case 6: //CONVERGENCIA
                partidoMarcadoName = "PACMA";
                break;
            case 7: //UPYD
                partidoMarcadoName = "UPYD";
                break;
           /** case 8: //CONVERGENCIA
                partidoMarcadoName = "";
                break;**/
        }

        return partidoMarcadoName;
    }

    public void checkPartido( int partidoPosition){

        setNotPressedImages();

        switch (partidoPosition){
            case 0: //PP
                partidoMarcado= 0;
                Glide.with(getContext()).load(R.drawable.mariano_rajoy).into(pp);
                break;
            case 1: //CS
                partidoMarcado= 1;
                Glide.with(getContext()).load(R.drawable.albert_rivera).into(cs);
                break;
            case 2: //PSOE
                partidoMarcado= 2;
                Glide.with(getContext()).load(R.drawable.pedro_sanchez).into(psoe);
                break;
            case 3: //PODEMOS
                partidoMarcado= 3;
                Glide.with(getContext()).load(R.drawable.pablo_iglesias).into(podemos);
                break;
            case 4: //IU
                partidoMarcado= 4;
                Glide.with(getContext()).load(R.drawable.pablo_iglesias).into(podemos);
                break;
            case 5: //PNV
                partidoMarcado= 5;
                Glide.with(getContext()).load(R.drawable.pablo_iglesias).into(podemos);
                break;
            case 6: //PP
                partidoMarcado= 6;
                Glide.with(getContext()).load(R.drawable.pablo_iglesias).into(podemos);
                break;
            case 7: //PP
                partidoMarcado= 7;
                Glide.with(getContext()).load(R.drawable.pablo_iglesias).into(podemos);
                break;
            case 8: //PP
                partidoMarcado= 8;
                Glide.with(getContext()).load(R.drawable.pablo_iglesias).into(podemos);
                break;
            case 9: //PP
                partidoMarcado= 9;
                Glide.with(getContext()).load(R.drawable.pablo_iglesias).into(podemos);
                break;
            default: partidoMarcado = -1;break;
        }
    }


}
