package es.elconfidencial.eleccionesgenerales2015.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.elconfidencial.eleccionesgenerales2015.R;
import es.elconfidencial.eleccionesgenerales2015.activities.MainActivity;
import es.elconfidencial.eleccionesgenerales2015.model.GlobalMethod;
import es.elconfidencial.eleccionesgenerales2015.model.Partido;
import es.elconfidencial.eleccionesgenerales2015.model.PartidoMegaencuesta;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraficosTab extends Fragment {


    private LinearLayout gridMegaencuesta, graficoMegaencuesta;
    private TextView actionBarTitle, headerEncuesta, graciasPorParticipar;
    HorizontalBarChart grafico;
    Context context;
    private View v;
    private int partidoMarcado = -1;

    private ImageView pp, cs, psoe, podemos, iu, pnv, convergencia, upyd, otros;
    private Button vota;


    //Lista de partidos de la megaencuesta
    ArrayList<PartidoMegaencuesta> partidoMegaencuestaList = new ArrayList<PartidoMegaencuesta>();


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
        boolean hasVoted = prefs.getBoolean("hasVoted", false); //Si no existe, devuelve el segundo parametro
        if (hasVoted) {
            //Cargamos la vista del gráfico
            gridMegaencuesta.setVisibility(View.GONE);
            graficoMegaencuesta.setVisibility(View.VISIBLE);
            setGraficoMegaencuesta();
        } else {
            //Cargamos la vista del Grid
            gridMegaencuesta.setVisibility(View.VISIBLE);
            graficoMegaencuesta.setVisibility(View.GONE);
            setGridMegaencuestaLayout();
        }


        return v;
    }

    /**
     * Este método carga en pantalla el layout correspondiente a:
     * - El usuario aún no ha votado en la encuesta
     * - No es el día de las elecciones, por lo que el webview permanece oculto
     */
    public void setGridMegaencuestaLayout() {

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
                } else {
                    Toast toast = Toast.makeText(getContext(), getResources().getString(R.string.selecciona_partido), Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

    }


    /**
     * Este método carga en pantalla el layout correspondiente a:
     * - El usuario SÍ ha votado en la encuesta
     * - No es el día de las elecciones, por lo que el webview permanece oculto
     */
    public void setGraficoMegaencuesta() {

        graciasPorParticipar = (TextView) v.findViewById(R.id.gracias);
        graciasPorParticipar.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-BoldItalic.otf"));
        grafico = (HorizontalBarChart) v.findViewById(R.id.horizontalBarChart);

        //Nos bajamos los resultados de Parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("PARTY_SURVEY");
        try {
            List<ParseObject> pList = query.find();
            for (ParseObject partidoPObj : pList) {
                Log.i("Megaencuesta", "" + partidoPObj.getString("PARTIDO") + " : " + partidoPObj.getInt("COUNT"));
                for (Partido partidoObject : ((MainActivity) getActivity()).getPartidosList()) {
                    //Si coinciden las ids de los partidos de la lista recibida de Parse y la local global
                    //Rellenamos las 3 necesarias para pintar nuestro gráfico
                    if (partidoObject.getId().equals(partidoPObj.getString("PARTIDO"))) {
                        //Rellenamos un nuevo objeto PartidoMegaencuesta
                        PartidoMegaencuesta partidoMegaencuesta = new PartidoMegaencuesta();
                        partidoMegaencuesta.setName(partidoObject.getSiglas());
                        partidoMegaencuesta.setnVotos(partidoPObj.getInt("COUNT"));
                        partidoMegaencuesta.setColor(Color.parseColor(partidoObject.getColor()));
                        //Lo añadimos a la lista
                        partidoMegaencuestaList.add(partidoMegaencuesta);
                    }
                }
            }
            //Con las listas completadas, añadimos a cada partido su atributo de porcentaje de votos
            setPorcentajes();
            sortPartiesByPercentage();
            //Con los partidos ordenador, rellenamos el gráfico
            generateDataBar();

        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("Megaencuesta", "Error: " + e.getMessage());
        }
    }

    public void sortPartiesByPercentage(){
        Collections.sort(partidoMegaencuestaList, new Comparator<PartidoMegaencuesta>(){
            public int compare(PartidoMegaencuesta partido1, PartidoMegaencuesta partido2) {
                return Double.compare(partido1.getPorcentajeVotos(),partido2.getPorcentajeVotos());
            }
        });
    }

    public void setPorcentajes(){

        double tamañoMuestra = getTamañoMuestra(partidoMegaencuestaList);
        Log.i("Grafico", "Tamaño de la muestra: " + tamañoMuestra);

        for(PartidoMegaencuesta partidoMegaencuesta : partidoMegaencuestaList) {
            Log.i("Grafico", "" + partidoMegaencuesta.getnVotos()/tamañoMuestra);
            partidoMegaencuesta.setPorcentajeVotos((partidoMegaencuesta.getnVotos() / tamañoMuestra) * 100);
           // Log.i("Grafico", "" + partidoMegaencuesta.getPorcentajeVotos());
        }

    }

    public int getTamañoMuestra(List<PartidoMegaencuesta> partidosList){
        int tamañoMuestra = 0;
        for(PartidoMegaencuesta partido : partidosList){
            tamañoMuestra += partido.getnVotos();
        }
        return tamañoMuestra;
    }

    private void generateDataBar(){

        ArrayList<BarEntry> porcentajesPartidos = new ArrayList<BarEntry>();
        ArrayList<Integer> coloresList = new ArrayList<>();
        ArrayList<String> partidosStringList = new ArrayList<>();

        int i = 0;
        for (PartidoMegaencuesta partidoMegaencuesta: partidoMegaencuestaList) {
            porcentajesPartidos.add(new BarEntry((float)partidoMegaencuesta.getPorcentajeVotos(), i));
            coloresList.add(partidoMegaencuesta.getColor());
            partidosStringList.add(partidoMegaencuesta.getName());
            i++;
        }

        BarDataSet set1 = new BarDataSet(porcentajesPartidos, "% de votos");

        set1.setColors(coloresList);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(partidosStringList, dataSets);



        //Estilo del gráfico

        //Tamaño de letra
        GlobalMethod globalMethod = new GlobalMethod(getContext());
        if (GlobalMethod.getSizeName(getContext()).equals("xlarge")) {
            data.setValueTextSize(25f);
        } else if (GlobalMethod.getSizeName(getContext()).equals("large")) {
            data.setValueTextSize(17f);
        } else if (GlobalMethod.getSizeName(getContext()).equals("normal")) {
            data.setValueTextSize(13f);
        }else {
            data.setValueTextSize(11f);
        }
        if (grafico != null) {
            grafico.setDrawBarShadow(false);
            grafico.setTouchEnabled(false);
            grafico.setDrawValueAboveBar(false);
            grafico.setBackgroundColor(Color.TRANSPARENT);
            grafico.setGridBackgroundColor(Color.TRANSPARENT);
            grafico.setDescription(getTamañoMuestra(partidoMegaencuestaList) + " votos");
            if (GlobalMethod.getSizeName(getContext()).equals("xlarge")) {
                grafico.setDescriptionTextSize(25f);
            } else if (GlobalMethod.getSizeName(getContext()).equals("large")) {
                grafico.setDescriptionTextSize(17f);
            } else if (GlobalMethod.getSizeName(getContext()).equals("normal")) {
                grafico.setDescriptionTextSize(13f);
            }else {
                grafico.setDescriptionTextSize(13f);
            }
            grafico.setDescriptionTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));

            // if more than 60 entries are displayed in the chart, no values will be
            // drawn
            grafico.setMaxVisibleValueCount(60);

            // scaling can now only be done on x- and y-axis separately
            grafico.setPinchZoom(false);

            // draw shadows for each bar that show the maximum value
            // mChart.setDrawBarShadow(true);

            // mChart.setDrawXLabels(false);

            grafico.setDrawGridBackground(false);

            // mChart.setDrawYLabels(false);

            data.setValueTextColor(Color.WHITE);


            XAxis xl = grafico.getXAxis();
            xl.setPosition(XAxis.XAxisPosition.BOTTOM);
            xl.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Semibold.otf"));
            xl.setDrawAxisLine(false);
            xl.setDrawGridLines(false);
            xl.setGridLineWidth(0.3f);
            if (GlobalMethod.getSizeName(getContext()).equals("xlarge")) {
                xl.setTextSize(27f);
            } else if (GlobalMethod.getSizeName(getContext()).equals("large")) {
                xl.setTextSize(16f);
            } else if (GlobalMethod.getSizeName(getContext()).equals("normal")) {
                xl.setTextSize(13f);
            }else {
                xl.setTextSize(9f);
            }


            YAxis yl = grafico.getAxisLeft();
            yl.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Semibold.otf"));
            yl.setDrawAxisLine(false);
            yl.setDrawGridLines(false);
            yl.setTextColor(Color.TRANSPARENT);
            yl.setGridLineWidth(0.3f);

            //  yl.setInverted(true);

            YAxis yr = grafico.getAxisRight();
            yr.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Semibold.otf"));
            yr.setDrawAxisLine(false);
            yr.setDrawGridLines(false);
            yr.setTextColor(Color.TRANSPARENT);
            //            yr.setInverted(true);

            grafico.animateY(2500);

            //Desactivamos la leyenda
            Legend l = grafico.getLegend();
            l.setEnabled(false);


            //Pasamos los datos al gráfico
            grafico.setData((BarData) data);
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
