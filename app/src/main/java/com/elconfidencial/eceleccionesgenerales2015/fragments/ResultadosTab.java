package com.elconfidencial.eceleccionesgenerales2015.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.bumptech.glide.Glide;
import com.elconfidencial.eceleccionesgenerales2015.activities.ChooseActivity;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.activities.MainActivity;
import com.elconfidencial.eceleccionesgenerales2015.charts.HorizontalBarChartEC;
import com.elconfidencial.eceleccionesgenerales2015.model.GlobalMethod;
import com.elconfidencial.eceleccionesgenerales2015.model.Partido;
import com.elconfidencial.eceleccionesgenerales2015.model.PartidoMegaencuesta;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultadosTab extends Fragment {

    
    //Etiquetas partidos Parse megaencuesta
    private static final String PP_TAG= "PP";
    private static final String PSOE_TAG= "PSOE";
    private static final String CS_TAG= "CIUDADANOS";
    private static final String UNIDOS_PODEMOS_TAG= "UNIDOS-PODEMOS";
    private static final String PNV_TAG= "PNV";
    private static final String OTROS_TAG= "OTROS";
    private static final String BLANCO_TAG= "BLANCO";
    private static final String CONVERGENCIA_TAG= "CONVERGENCIA";
    private static final String ERC_TAG= "ERC";

    private LinearLayout gridMegaencuesta, graficoMegaencuesta, webviewLayout;
    private FrameLayout contadorLayout;
    private TextView headerEncuesta, graciasPorParticipar, mensajeAviso;
    private ImageView refreshIcon;
    HorizontalBarChartEC grafico;
    private WebView webviewResultados;
    RelativeLayout partySelectedLayout;
    TableLayout tablaPartidosLayout;
    TextView cancelButton;
    FrameLayout voteButton;
    Context context;
    private View v;
    private int partidoMarcado = -1;
    private final int MINIMO_TAMANO_MUESTRA=50;

    private ImageView pp, cs, psoe, podemos, erc, pnv, convergencia, blanco, otros;
    private ImageView imagenBig;
    public TextView contador, textViewDias,textViewHoras,textViewMinutos,textViewColegiosElectorales;
    private FrameLayout vota;


    //Lista de partidos de la megaencuesta
    ArrayList<PartidoMegaencuesta> partidoMegaencuestaList = new ArrayList<PartidoMegaencuesta>();


    public ResultadosTab() {}

    public static ResultadosTab newInstance() {

        Bundle args = new Bundle();

        ResultadosTab fragment = new ResultadosTab();
        fragment.setArguments(args);


        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Amplitude
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put("page", "resultados");
            if(ChooseActivity.SHOW_WIDGET_RESULTS){
                eventProperties.put("segment", "resultados España");
            } else{
                eventProperties.put("segment", "votos lectores");
            }
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        Amplitude.getInstance().logEvent("page_view", eventProperties);


        //Amplitude
        JSONObject eventProperties1 = new JSONObject();
        try {
            eventProperties1.put("segment", "resultados");
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        Amplitude.getInstance().logEvent("Tap_menu", eventProperties1);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_resultados_tab, container, false);
        context = getContext();



        //Inicializamos los Layout correspondientes a cada una de las screens
        gridMegaencuesta = (LinearLayout) v.findViewById(R.id.gridMegaencuestaScreen);
        graficoMegaencuesta = (LinearLayout) v.findViewById(R.id.graficoMegaencuestaScreen);
        webviewLayout = (LinearLayout) v.findViewById(R.id.webviewScreen);
        contadorLayout = (FrameLayout) v.findViewById(R.id.contador_layout);


        //Comprobamos que layout debemos mostrar y cuales deben aparecer ocultos
        if(ChooseActivity.SHOW_WIDGET_RESULTS){
            //Cargamos el WebView
            contadorLayout.setVisibility(View.GONE);
            gridMegaencuesta.setVisibility(View.GONE);
            graficoMegaencuesta.setVisibility(View.GONE);
            webviewLayout.setVisibility(View.VISIBLE);
            setWebViewLayout();
        } else{
            SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
            boolean hasVoted = prefs.getBoolean("hasVoted", false); //Si no existe, devuelve el segundo parametro

            //Inicializamos el contador
            bindContador();
            if(ChooseActivity.SHOW_TIMER){
                long tiempoRestanteInicio = 0;
                long tiempoRestanteFin = 0;
                long today = new Date().getTime();
                String fechaElecciones = "26/06/2016 09:00";
                String fechaFinElecciones = "26/06/2016 20:00";
                try{
                    Date elecciones = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(fechaElecciones);
                    Date cierre = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(fechaFinElecciones);
                    tiempoRestanteInicio = elecciones.getTime()- today;
                    tiempoRestanteFin = cierre.getTime()- today;
                    if (tiempoRestanteInicio>0){
                        showContador();
                        contadorLayout.setVisibility(View.VISIBLE);
                    }else if(tiempoRestanteFin>0){
                        //METER EL OTRO CONTADOR
                        showContadorCierre();
                        contadorLayout.setVisibility(View.VISIBLE);
                    }else{
                        //DEJAR VACIO
                        contadorLayout.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                contadorLayout.setVisibility(View.GONE);
            }



            if (hasVoted) {
                //Cargamos la vista del gráfico
                Log.i("Megaencuesta", "Cargamos la vista del gráfico");
                gridMegaencuesta.setVisibility(View.GONE);
                graficoMegaencuesta.setVisibility(View.VISIBLE);
                webviewLayout.setVisibility(View.GONE);
                setGraficoMegaencuesta();
            } else {
                //Cargamos la vista del Grid
                Log.i("Megaencuesta", "Cargamos la vista del gráfico");
                gridMegaencuesta.setVisibility(View.VISIBLE);
                graficoMegaencuesta.setVisibility(View.GONE);
                webviewLayout.setVisibility(View.GONE);
                setGridMegaencuestaLayout();
            }
        }



        return v;
    }


    // region LAYOUT WEBVIEW
    //----------------------------------------------------------------------

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
                        if (url.equals(ChooseActivity.RESULTS_WEBVIEW_URL)) {
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
        if(globalMethod.haveNetworkConnection() && ChooseActivity.RESULTS_WEBVIEW_URL !=null){
            webviewResultados.loadUrl(ChooseActivity.RESULTS_WEBVIEW_URL);
            Log.i("Resultados", "RESULTS_WEBVIEW_URL = " + ChooseActivity.RESULTS_WEBVIEW_URL);
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


        /**Añadimos funcionalidad refresh
         refreshIcon.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        webviewResultados.reload();
        }
        });**/


        // disable scroll on touch
        /**   webviewResultados.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
        return (event.getAction() == MotionEvent.ACTION_MOVE);
        }
        });**/
    }


    //----------------------------------------------------------------------
    //endregion

    // region GRID MEGAENCUESTA
    //----------------------------------------------------------------------

    /**
     * Este método carga en pantalla el layout correspondiente a:
     * - El usuario aún no ha votado en la encuesta
     * - No es el día de las elecciones, por lo que el webview permanece oculto
     */
    public void setGridMegaencuestaLayout() {

        bindViewsMegaencuesta();

        //Cargamos todas las imagenes grises (not pressed)
        setNotPressedImages();

        //Listener de las imagenes de los partidos
        setupListenersPartidos();


        //Listener del botón votar
     /**   vota.setOnClickListener(new View.OnClickListener() {
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
        });**/

    }

    private void bindViewsMegaencuesta(){

        partySelectedLayout = (RelativeLayout) v.findViewById(R.id.party_selected_layout);
        tablaPartidosLayout = (TableLayout) v.findViewById(R.id.tabla_partidos);

        cancelButton = (TextView) v.findViewById(R.id.cancelButton);
        voteButton = (FrameLayout) v.findViewById(R.id.votaButton);
        imagenBig = (ImageView) v.findViewById(R.id.image_megaencuesta_big);

        pp = (ImageView) v.findViewById(R.id.ppLogo);
        cs = (ImageView) v.findViewById(R.id.cslogo);
        psoe = (ImageView) v.findViewById(R.id.psoelogo);
        podemos = (ImageView) v.findViewById(R.id.podemosLogo);
        erc = (ImageView) v.findViewById(R.id.iulogo);
        pnv = (ImageView) v.findViewById(R.id.pnvlogo);
        convergencia = (ImageView) v.findViewById(R.id.convergenciaLogo);
        blanco = (ImageView) v.findViewById(R.id.blancologo);
        otros = (ImageView) v.findViewById(R.id.otroslogo);

    }

    private void setupListenersPartidos(){
        pp.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                partySelectedLayout.setVisibility(View.VISIBLE);
                partySelectedLayout.setX(5000);
                partySelectedLayout.animate().translationX(0).setDuration(450).start();
                tablaPartidosLayout.setVisibility(View.GONE);
                Glide.with(context).load(R.drawable.pp_color).into(imagenBig);
                partySelectedAmplitude(PP_TAG);
                setupListenersPartidoDetail(PP_TAG);
            }
        });

        psoe.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                partySelectedLayout.setVisibility(View.VISIBLE);
                partySelectedLayout.setX(5000);
                partySelectedLayout.animate().translationX(0).setDuration(450).start();
                tablaPartidosLayout.setVisibility(View.GONE);
                Glide.with(context).load(R.drawable.psoe_color).into(imagenBig);
                partySelectedAmplitude(PSOE_TAG);
                setupListenersPartidoDetail(PSOE_TAG);
            }
        });

        cs.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                partySelectedLayout.setVisibility(View.VISIBLE);
                partySelectedLayout.setX(5000);
                partySelectedLayout.animate().translationX(0).setDuration(450).start();
                tablaPartidosLayout.setVisibility(View.GONE);
                Glide.with(context).load(R.drawable.ciudadanos_color).into(imagenBig);
                partySelectedAmplitude(CS_TAG);
                setupListenersPartidoDetail(CS_TAG);
            }
        });

        podemos.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                partySelectedLayout.setVisibility(View.VISIBLE);
                partySelectedLayout.setX(5000);
                partySelectedLayout.animate().translationX(0).setDuration(450).start();
                tablaPartidosLayout.setVisibility(View.GONE);
                Glide.with(context).load(R.drawable.unidospodemos_color).into(imagenBig);
                partySelectedAmplitude(UNIDOS_PODEMOS_TAG);
                setupListenersPartidoDetail(UNIDOS_PODEMOS_TAG);
            }
        });

        pnv.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                partySelectedLayout.setVisibility(View.VISIBLE);
                partySelectedLayout.setX(5000);
                partySelectedLayout.animate().translationX(0).setDuration(450).start();
                tablaPartidosLayout.setVisibility(View.GONE);
                Glide.with(context).load(R.drawable.pnv_color).into(imagenBig);
                partySelectedAmplitude(PNV_TAG);
                setupListenersPartidoDetail(PNV_TAG);
            }
        });

        erc.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                partySelectedLayout.setVisibility(View.VISIBLE);
                partySelectedLayout.setX(5000);
                partySelectedLayout.animate().translationX(0).setDuration(450).start();
                tablaPartidosLayout.setVisibility(View.GONE);
                Glide.with(context).load(R.drawable.esquerra_color).into(imagenBig);
                partySelectedAmplitude(ERC_TAG);
                setupListenersPartidoDetail(ERC_TAG);
            }
        });

        convergencia.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                partySelectedLayout.setVisibility(View.VISIBLE);
                partySelectedLayout.setX(5000);
                partySelectedLayout.animate().translationX(0).setDuration(450).start();
                tablaPartidosLayout.setVisibility(View.GONE);
                Glide.with(context).load(R.drawable.convergencia_color).into(imagenBig);
                partySelectedAmplitude(CONVERGENCIA_TAG);
                setupListenersPartidoDetail(CONVERGENCIA_TAG);
            }
        });

        otros.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                partySelectedLayout.setVisibility(View.VISIBLE);
                partySelectedLayout.setX(5000);
                partySelectedLayout.animate().translationX(0).setDuration(450).start();
                tablaPartidosLayout.setVisibility(View.GONE);
                Glide.with(context).load(R.drawable.otros_color).into(imagenBig);
                partySelectedAmplitude(OTROS_TAG);
                setupListenersPartidoDetail(OTROS_TAG);
            }
        });

        blanco.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                partySelectedLayout.setVisibility(View.VISIBLE);
                partySelectedLayout.setX(5000);
                partySelectedLayout.animate().translationX(0).setDuration(450).start();
                tablaPartidosLayout.setVisibility(View.GONE);
                Glide.with(context).load(R.drawable.blanco_color).into(imagenBig);
                partySelectedAmplitude(BLANCO_TAG);
                setupListenersPartidoDetail(BLANCO_TAG);
            }
        });
    }

    private void partySelectedAmplitude(String partido){
        //Amplitude
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put("tag", partido);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        Amplitude.getInstance().logEvent("Select political party", eventProperties);
    }

    private void setupListenersPartidoDetail(final String nombrePartido){
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partySelectedLayout.setVisibility(View.GONE);
                tablaPartidosLayout.setVisibility(View.VISIBLE);
            }
        });

        voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Marcar como que ha votado
                //Cargamos las preferencias compartidas, es como la base de datos para guardarlas y que se recuerden mas tarde
                SharedPreferences prefs = getActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("hasVoted", true); //Lo guardamos para recordarlo
                editor.apply(); //Guardamos las SharedPreferences

                //Comunicacion con Parse.com
                ParseQuery<ParseObject> query = ParseQuery.getQuery("PARTY_SURVEY");
                Log.i("Megaencuesta", nombrePartido);
                query.whereEqualTo("PARTIDO", nombrePartido); //Averiguamos el partido que se encuentra marcado
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
                JSONObject eventProperties1 = new JSONObject();
                try {
                    eventProperties1.put("tag", nombrePartido);
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
                Amplitude.getInstance().logEvent("Vote", eventProperties1);

            }
        });
    }

    public void setNotPressedImages(){
        //Cargamos las imagenes not pressed
        Glide.with(context).load(R.drawable.pp).into(pp);
        Glide.with(context).load(R.drawable.ciudadanos).into(cs);
        Glide.with(context).load(R.drawable.psoe).into(psoe);
        Glide.with(context).load(R.drawable.unidospodemos).into(podemos);
        Glide.with(context).load(R.drawable.esquerra).into(erc);
        Glide.with(context).load(R.drawable.pnv).into(pnv);
        Glide.with(context).load(R.drawable.convergencia).into(convergencia);
        Glide.with(context).load(R.drawable.otros).into(otros);
        Glide.with(context).load(R.drawable.blanco).into(blanco);
    }



    //----------------------------------------------------------------------
    //endregion

    // region ENCUESTA EL CONFIDENCIAL
    //----------------------------------------------------------------------

    /**
     * Este método carga en pantalla el layout correspondiente a:
     * - El usuario SÍ ha votado en la encuesta
     * - No es el día de las elecciones, por lo que el webview permanece oculto
     */
    public void setGraficoMegaencuesta() {

        grafico = (HorizontalBarChartEC) v.findViewById(R.id.horizontalBarChart);
        partidoMegaencuestaList.clear();

        //Nos bajamos los resultados de Parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("PARTY_SURVEY");
        try {
            List<ParseObject> pList = query.find();
            for (ParseObject partidoPObj : pList) {
                Log.i("Megaencuesta", "" + partidoPObj.getString("PARTIDO") + " : " + partidoPObj.getInt("COUNT"));
                for (Partido partidoObject : ChooseActivity.partidosList) {
                    //Si coinciden las ids de los partidos de la lista recibida de Parse y la local global
                    //Rellenamos las 3 necesarias para pintar nuestro gráfico
                    if (partidoObject.getId().equals(partidoPObj.getString("PARTIDO"))) {
                        Log.i("Megaencuesta", partidoObject.getSiglas() + " lo metemos para procesar");
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
        data.setValueTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));
        data.setValueTextColor(Color.parseColor("#333333"));
        if (grafico != null) {
            if (GlobalMethod.getSizeName(getContext()).equals("xlarge")) {
                grafico.setExtraRightOffset(70f);
            }else {
                grafico.setExtraRightOffset(50f);
            }
            grafico.setDrawBarShadow(false);
            grafico.setTouchEnabled(false);
            grafico.setDrawValueAboveBar(false);
            grafico.setBackgroundColor(Color.TRANSPARENT);
            grafico.setGridBackgroundColor(Color.TRANSPARENT);
            //Si el tamaño de la muestra es mayor de MINIMO_TAMAÑO_MUESTRA se muestra la descripción
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

    //----------------------------------------------------------------------
    //endregion


    // region CONTADOR
    //----------------------------------------------------------------------

    public void bindContador(){
        textViewColegiosElectorales = (TextView) v.findViewById(R.id.textViewColegiosElectorales);
        textViewDias = (TextView) v.findViewById(R.id.textViewDias);
        textViewHoras = (TextView) v.findViewById(R.id.textViewHoras);
        textViewMinutos = (TextView) v.findViewById(R.id.textViewMinutos);
    }
    public void showContador(){

        long tiempoRestante = 0;

        //Calculamos el tiempo (milisegundos) que quedan para las elecciones catalanas
        try {
            long today = new Date().getTime();
            String fechaElecciones = "26/06/2016 09:00";
            Date elecciones = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(fechaElecciones);

            tiempoRestante = elecciones.getTime()- today;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        //Iniciamos una cuenta atras, empezando por los milisegundos que quedan (tiempoRestante) hasta alcanzar 1 segundo.
        //Mostramos los milisegundos en formato: D dias H h M mins S segs.
        new CountDownTimer(tiempoRestante, 1000) {

            StringBuilder time = new StringBuilder();

            public void onTick(long millisUntilFinished) {
                //tiempo.setText("seconds remaining: " + millisUntilFinished / 1000);
                long days = (millisUntilFinished / (1000 * 60 * 60 * 24)); //for counting days
                long hours = (millisUntilFinished - days*(1000*60*60*24)) / (1000 * 60 * 60); //for counting hours
                long minutes = (millisUntilFinished - days*(1000*60*60*24) - hours*(1000*60*60))/ (1000 * 60); //for counting minutes
                long seconds = (millisUntilFinished - days*(1000*60*60*24) - hours*(1000*60*60) - minutes*(1000*60)) / (1000); //for counting seconds

                textViewDias.setText(""+days);
                textViewHoras.setText("" + hours);
                textViewMinutos.setText("" + minutes);
                textViewColegiosElectorales.setText("para abrir los colegios electorales");

//                contador.setText( days + " " + MainActivity.resources.getString(R.string.dias) + "  " + hours + " h  \n"+ minutes +" mins  " + seconds + " segs ");
            }

            public void onFinish() {
                textViewDias.setText("0");
                textViewHoras.setText("0");
                textViewMinutos.setText("0");//Texto al llegar a 0;
            }
        }.start();
    }

    public void showContadorCierre(){

        long tiempoRestante = 0;

        //Calculamos el tiempo (milisegundos) que quedan para las elecciones catalanas
        try {
            long today = new Date().getTime();
            String fechaElecciones = "26/06/2016 20:00";
            Date elecciones = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(fechaElecciones);

            tiempoRestante = elecciones.getTime()- today;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        //Iniciamos una cuenta atras, empezando por los milisegundos que quedan (tiempoRestante) hasta alcanzar 1 segundo.
        //Mostramos los milisegundos en formato: D dias H h M mins S segs.
        new CountDownTimer(tiempoRestante, 1000) {

            StringBuilder time = new StringBuilder();

            public void onTick(long millisUntilFinished) {
                //tiempo.setText("seconds remaining: " + millisUntilFinished / 1000);
                long days = (millisUntilFinished / (1000 * 60 * 60 * 24)); //for counting days
                long hours = (millisUntilFinished - days*(1000*60*60*24)) / (1000 * 60 * 60); //for counting hours
                long minutes = (millisUntilFinished - days*(1000*60*60*24) - hours*(1000*60*60))/ (1000 * 60); //for counting minutes
                long seconds = (millisUntilFinished - days*(1000*60*60*24) - hours*(1000*60*60) - minutes*(1000*60)) / (1000); //for counting seconds

                textViewDias.setText(""+days);
                textViewHoras.setText("" + hours);
                textViewMinutos.setText("" + minutes);
                textViewColegiosElectorales.setText("para cerrar los colegios electorales");
//                contador.setText( days + " " + MainActivity.resources.getString(R.string.dias) + "  " + hours + " h  \n"+ minutes +" mins  " + seconds + " segs ");
            }

            public void onFinish() {
                textViewDias.setText("0");
                textViewHoras.setText("0");
                textViewMinutos.setText("0");//Texto al llegar a 0;
            }
        }.start();
    }

    //----------------------------------------------------------------------
    //endregion


}
