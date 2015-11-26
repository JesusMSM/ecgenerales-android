package es.elconfidencial.eleccionesgenerales2015.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amplitude.api.Amplitude;
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

import org.json.JSONException;
import org.json.JSONObject;

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


    private LinearLayout gridMegaencuesta, graficoMegaencuesta, webviewLayout;
    private TextView actionBarTitle, headerEncuesta, graciasPorParticipar, mensajeAviso;
    HorizontalBarChart grafico;
    private WebView webviewResultados;
    Context context;
    private View v;
    private int partidoMarcado = -1;
    private final int MINIMO_TAMANO_MUESTRA=50;

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


        //Inicializamos los Layout correspondientes a cada una de las screens
        gridMegaencuesta = (LinearLayout) v.findViewById(R.id.gridMegaencuestaScreen);
        graficoMegaencuesta = (LinearLayout) v.findViewById(R.id.graficoMegaencuestaScreen);
        webviewLayout = (LinearLayout) v.findViewById(R.id.webviewScreen);

        //Comprobamos que layout debemos mostrar y cuales deben aparecer ocultos
        //if(MainActivity.SHOW_WIDGET_RESULTS){
        if(false){
            //Cargamos el WebView
            gridMegaencuesta.setVisibility(View.GONE);
            graficoMegaencuesta.setVisibility(View.GONE);
            webviewLayout.setVisibility(View.VISIBLE);
            setWebViewLayout();
        } else{
            SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
            boolean hasVoted = prefs.getBoolean("hasVoted", false); //Si no existe, devuelve el segundo parametro
            if (hasVoted) {
                //Cargamos la vista del gráfico
                gridMegaencuesta.setVisibility(View.GONE);
                graficoMegaencuesta.setVisibility(View.VISIBLE);
                webviewLayout.setVisibility(View.GONE);
                setGraficoMegaencuesta();
            } else {
                //Cargamos la vista del Grid
                gridMegaencuesta.setVisibility(View.VISIBLE);
                graficoMegaencuesta.setVisibility(View.GONE);
                webviewLayout.setVisibility(View.GONE);
                setGridMegaencuestaLayout();
            }
        }



        return v;
    }


    /**
     * Este método carga en pantalla el layout correspondiente al día de las elecciones
     */
    public void setWebViewLayout(){
        webviewResultados = (WebView) v.findViewById(R.id.resultadosWebview);

        //Preparamos el Webview
        webviewResultados.getSettings().setJavaScriptEnabled(true);
        webviewResultados.getSettings().setDomStorageEnabled(true);
        webviewResultados.getSettings().setAppCacheEnabled(true);
        webviewResultados.getSettings().setDefaultTextEncodingName("utf-8");
        //webviewResultados.getSettings().setSupportZoom(true);
        webviewResultados.setVerticalScrollBarEnabled(true);

        GlobalMethod globalMethod = new GlobalMethod(getContext());

        //WebChromeClient
        webviewResultados.setWebChromeClient(new WebChromeClient());

        //WebClient
        webviewResultados.setWebViewClient(
                new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if (url.equals(MainActivity.RESULTS_WEBVIEW_URL + "/")) {
                            view.loadUrl(url);
                            Log.i("Resultados", "dentro del if con " + url);
                            return true;
                        } else {
                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(i);
                            Log.i("Resultados", "dentro del else con " + url);
                            return true;
                        }
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                    }
                });

        //Comprobamos si tiene conexi�n a Internet
        //Si tiene conexi�n cargamos la url, si no tiene mostramos el mensaje de alerta
        if(globalMethod.haveNetworkConnection() && MainActivity.RESULTS_WEBVIEW_URL !=null){
            webviewResultados.loadUrl(MainActivity.RESULTS_WEBVIEW_URL + "/");
            Log.i("Resultados", "RESULTS_WEBVIEW_URL = " + MainActivity.RESULTS_WEBVIEW_URL);
        } else {
            //Compramos el tipo de dispositivo y calculamos el tama�o de letra.
            String textSize= "";
            if (globalMethod.getSizeName(getContext()).equals("xlarge")) {
                textSize="25px";
            } else if (globalMethod.getSizeName(getContext()).equals("large")) {
                textSize="18px";
            } else if (globalMethod.getSizeName(getContext()).equals("normal")) {
                textSize="16px";
            }else {
                textSize="14px";
            }

            String head = "<head><style>@font-face {font-family: MilioHeavy;src: url(\"file:///android_asset/Milio-Heavy.ttf\")}" +
                    "@font-face {font-family: TitilliumLight;src: url(\"file:///android_asset/Titillium-Light.otf\")}" +
                    "@font-face {font-family: TitilliumSemibold;src: url(\"file:///android_asset/Titillium-Semibold.otf\")}" +
                    "h2{font-family: MilioHeavy;}" +
                    "body{font-family:TitilliumLight;text-align:justify}" +
                    "a{text-decoration: none;color:black;} " +
                    "html { font-size: " + textSize + "}" +
                    "strong{font-family:TitilliumSemibold;}</style></head>";

            String htmlSinConexion ="<html>" + head + "<body><div>" + getResources().getString(R.string.alerta_conexion_webview) + "</div></body></html>";
            webviewResultados.loadDataWithBaseURL("", htmlSinConexion, "text/html", "charset=UTF-8", null);
        }



        // disable scroll on touch
     /**   webviewResultados.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });**/
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

                    //Amplitude
                    Log.i("20D_AMPLITUDE", "ONSELECT_PARTY: "+ getNamePartidoMarcado());
                    JSONObject eventProperties = new JSONObject();
                    try {
                        eventProperties.put("PARTY", getNamePartidoMarcado());
                    } catch (JSONException exception) {
                    }
                    Amplitude.getInstance().logEvent("ONSELECT_PARTY", eventProperties);
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

        mensajeAviso = (TextView) v.findViewById(R.id.avisoResultados);
        mensajeAviso.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));
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
        Collections.sort(partidoMegaencuestaList, new Comparator<PartidoMegaencuesta>() {
            public int compare(PartidoMegaencuesta partido1, PartidoMegaencuesta partido2) {
                return Double.compare(partido1.getPorcentajeVotos(), partido2.getPorcentajeVotos());
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
            //Si el tamaño de la muestra es mayor de MINIMO_TAMAÑO_MUESTRA se muestra la descripción
            if(getTamañoMuestra(partidoMegaencuestaList)>=MINIMO_TAMANO_MUESTRA){
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
            }

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
        Glide.with(getContext()).load(R.drawable.pp_off).placeholder(R.drawable.nopicpersona).into(pp);
        Glide.with(getContext()).load(R.drawable.cs_off).placeholder(R.drawable.nopicpersona).into(cs);
        Glide.with(getContext()).load(R.drawable.psoe_off).placeholder(R.drawable.nopicpersona).into(psoe);
        Glide.with(getContext()).load(R.drawable.pdms_off).placeholder(R.drawable.nopicpersona).into(podemos);
        Glide.with(getContext()).load(R.drawable.iu_off).placeholder(R.drawable.nopicpersona).into(iu);
        Glide.with(getContext()).load(R.drawable.pnv_off).placeholder(R.drawable.nopicpersona).into(pnv);
        Glide.with(getContext()).load(R.drawable.convergencia_off).placeholder(R.drawable.nopicpersona).into(convergencia);
        Glide.with(getContext()).load(R.drawable.upyd_off).placeholder(R.drawable.nopicpersona).into(upyd);
        Glide.with(getContext()).load(R.drawable.otros_off).placeholder(R.drawable.nopicpersona).into(otros);
    }

    public void setListenersImgPartidos(){

        pp.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(partidoMarcado!=0){
                    checkPartido(0);
                } else {
                    setNotPressedImages();
                    partidoMarcado = -1;
                }

            }
        });
        cs.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(partidoMarcado!=1){
                    checkPartido(1);
                } else {
                    setNotPressedImages();
                    partidoMarcado = -1;
                }
            }
        });
        psoe.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(partidoMarcado!=2){
                    checkPartido(2);
                } else {
                    setNotPressedImages();
                    partidoMarcado = -1;
                }
            }
        });
        podemos.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(partidoMarcado!=3){
                    checkPartido(3);
                } else {
                    setNotPressedImages();
                    partidoMarcado = -1;
                }
            }
        });
        iu.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(partidoMarcado!=4){
                    checkPartido(4);
                } else {
                    setNotPressedImages();
                    partidoMarcado = -1;
                }
            }
        });
        pnv.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(partidoMarcado!=5){
                    checkPartido(5);
                } else {
                    setNotPressedImages();
                    partidoMarcado = -1;
                }
            }
        });
        convergencia.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(partidoMarcado!=6){
                    checkPartido(6);
                } else {
                    setNotPressedImages();
                    partidoMarcado = -1;
                }
            }
        });
        upyd.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(partidoMarcado!=7){
                    checkPartido(7);
                } else {
                    setNotPressedImages();
                    partidoMarcado = -1;
                }
            }
        });
        otros.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(partidoMarcado!=8){
                    checkPartido(8);
                } else {
                    setNotPressedImages();
                    partidoMarcado = -1;
                }
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
                Glide.with(getContext()).load(R.drawable.pp_on).into(pp);
                break;
            case 1: //CS
                partidoMarcado= 1;
                Glide.with(getContext()).load(R.drawable.cs_on).into(cs);
                break;
            case 2: //PSOE
                partidoMarcado= 2;
                Glide.with(getContext()).load(R.drawable.psoe_on).into(psoe);
                break;
            case 3: //PODEMOS
                partidoMarcado= 3;
                Glide.with(getContext()).load(R.drawable.pdms_on).into(podemos);
                break;
            case 4: //IU
                partidoMarcado= 4;
                Glide.with(getContext()).load(R.drawable.iu_on).into(iu);
                break;
            case 5: //PNV
                partidoMarcado= 5;
                Glide.with(getContext()).load(R.drawable.pnv_on).into(pnv);
                break;
            case 6: //CONVERGENCIA
                partidoMarcado= 6;
                Glide.with(getContext()).load(R.drawable.convergencia_on).into(convergencia);
                break;
            case 7: //UPYD
                partidoMarcado= 7;
                Glide.with(getContext()).load(R.drawable.upyd_on).into(upyd);
                break;
            case 8: //OTROS
                partidoMarcado= 8;
                Glide.with(getContext()).load(R.drawable.otros_on).into(otros);
                break;
            default: partidoMarcado = -1;break;
        }
    }


}
