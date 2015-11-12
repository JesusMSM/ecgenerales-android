package es.elconfidencial.eleccionesgenerales2015.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.parse.ParseAnalytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.elconfidencial.eleccionesgenerales2015.R;
import es.elconfidencial.eleccionesgenerales2015.activities.MainActivity;
import es.elconfidencial.eleccionesgenerales2015.activities.NoticiaContentActivity;
import es.elconfidencial.eleccionesgenerales2015.activities.PreferencesActivity;
import es.elconfidencial.eleccionesgenerales2015.fragments.HomeTab;
import es.elconfidencial.eleccionesgenerales2015.fragments.NoticiasTab;
import es.elconfidencial.eleccionesgenerales2015.listeners.OnDislikeClickListener;
import es.elconfidencial.eleccionesgenerales2015.listeners.OnLikeClickListener;
import es.elconfidencial.eleccionesgenerales2015.model.CardPubli;
import es.elconfidencial.eleccionesgenerales2015.model.Encuesta;
import es.elconfidencial.eleccionesgenerales2015.model.GlobalMethod;
import es.elconfidencial.eleccionesgenerales2015.model.Noticia;
import es.elconfidencial.eleccionesgenerales2015.model.Partido;
import es.elconfidencial.eleccionesgenerales2015.model.Persona;
import es.elconfidencial.eleccionesgenerales2015.model.Quote;
import es.elconfidencial.eleccionesgenerales2015.model.Titulo;
import es.elconfidencial.eleccionesgenerales2015.rss.RssNoticiasParser;
import es.elconfidencial.eleccionesgenerales2015.viewholders.CardPubliViewHolder;
import es.elconfidencial.eleccionesgenerales2015.viewholders.ContadorViewHolder;
import es.elconfidencial.eleccionesgenerales2015.viewholders.EncuestasViewHolder;
import es.elconfidencial.eleccionesgenerales2015.viewholders.NoticiaViewHolder;
import es.elconfidencial.eleccionesgenerales2015.viewholders.PoliticoViewHolder;
import es.elconfidencial.eleccionesgenerales2015.viewholders.PresinderViewHolder;
import es.elconfidencial.eleccionesgenerales2015.viewholders.SpinnerViewHolder;
import es.elconfidencial.eleccionesgenerales2015.viewholders.TituloViewHolder;

/**
 * Created by Moonfish on 28/10/15.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The items to display in your RecyclerView
    private List<Object> items;
    Context context;
    int encuestaSeleccionada = 0;
    BarChart grafico;

    private final int NOTICIA = 0,PRESINDER = 1, POLITICO = 2, SPINNER = 3, TITULO = 4, CONTADOR = 5, ENCUESTA=6, CARDPUBLI=7;


    // Provide a suitable constructor (depends on the kind of dataset)
    public MyRecyclerViewAdapter(Context context, List<Object> items) {
        this.context = context;
        this.items = items;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public int getItemViewType(int position) {
        //Depediendo del tipo de objeto (Noticia, Quiz, Integer...) devolvemos el tipo de celda del RecyclerView.
        if (items.get(position) instanceof Noticia) {
            return NOTICIA;
        }
        if (items.get(position) instanceof Quote) {
            return PRESINDER;
        }
        if (items.get(position) instanceof Persona) {
            return POLITICO;
        }
        if (items.get(position) instanceof Spinner) {
            return SPINNER;
        }
        if (items.get(position) instanceof Titulo) {
            return TITULO;
        }
        if (items.get(position).equals("contador")) {
            return CONTADOR;
        }
        if (items.get(position).equals("encuestas")) {
            return ENCUESTA;
        }
        else if (items.get(position) instanceof CardPubli) {
            return CARDPUBLI;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //Dependiendo del tipo de view, cargamos el archivo layout XML correspondiente
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case NOTICIA:
                View v1 = inflater.inflate(R.layout.recyclerview_item_noticia, viewGroup, false);
                viewHolder = new NoticiaViewHolder(v1);
                break;
            case PRESINDER:
                View v2 = inflater.inflate(R.layout.recyclerview_item_presinder, viewGroup, false);
                viewHolder = new PresinderViewHolder(v2);
                break;
            case POLITICO:
                View v3 = inflater.inflate(R.layout.recyclerview_item_politico, viewGroup, false);
                viewHolder = new PoliticoViewHolder(v3);
                break;
            case SPINNER:
                View v4 = inflater.inflate(R.layout.recyclerview_item_spinner, viewGroup, false);
                viewHolder = new SpinnerViewHolder(v4);
                break;
            case TITULO:
                View v5 = inflater.inflate(R.layout.recyclerview_item_titulo, viewGroup, false);
                viewHolder = new TituloViewHolder(v5);
                break;
            case CONTADOR:
                View v6 = inflater.inflate(R.layout.recyclerview_item_contador, viewGroup, false);
                viewHolder = new ContadorViewHolder(v6);
                break;
            case ENCUESTA:
                View v7 = inflater.inflate(R.layout.recyclerview_item_encuesta, viewGroup, false);
                viewHolder = new EncuestasViewHolder(v7);
                break;
            case CARDPUBLI:
                View v8 = inflater.inflate(R.layout.recyclerview_item_cardpubli, viewGroup, false);
                viewHolder = new CardPubliViewHolder(v8);
                break;
            default:
                viewHolder = null;
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        //Identificamos el tipo de ViewHolder y creamos el correspondiente para dicha posicion.
        switch (viewHolder.getItemViewType()) {
            case NOTICIA:
                NoticiaViewHolder vh1 = (NoticiaViewHolder) viewHolder;
                configureNoticiaViewHolder(vh1, position);
                break;
            case PRESINDER:
                PresinderViewHolder vh2 = (PresinderViewHolder) viewHolder;
                configurePresinderViewHolder(vh2, position);
                break;
            case POLITICO:
                PoliticoViewHolder vh3 = (PoliticoViewHolder) viewHolder;
                configurePoliticoViewHolder(vh3, position);
                break;
            case SPINNER:
                SpinnerViewHolder vh4 = (SpinnerViewHolder) viewHolder;
                configureSpinnerViewHolder(vh4, position);
                break;
            case TITULO:
                TituloViewHolder vh5 = (TituloViewHolder) viewHolder;
                configureTituloViewHolder(vh5, position);
                break;
            case CONTADOR:
                ContadorViewHolder vh6 = (ContadorViewHolder) viewHolder;
                configureContadorViewHolder(vh6, position);
                break;
            case ENCUESTA:
                EncuestasViewHolder vh7 = (EncuestasViewHolder) viewHolder;
                configureEncuestasViewHolder(vh7, position);
                break;
            case CARDPUBLI:
                CardPubliViewHolder vh8 = (CardPubliViewHolder) viewHolder;
                configureCardPubliViewHolder(vh8, position);
                break;
            default:
        }
    }

    /**
     * Funciones de configuracion de los ViewHolders **
     */
    private void configureNoticiaViewHolder(final NoticiaViewHolder vh, int position) {

        String url = "";
        String info = "";

        final Noticia noticia = (Noticia) items.get(position);
        if (noticia != null) {
            vh.titulo.setText(Html.fromHtml(noticia.getTitulo()));
            vh.autor.setText(noticia.getAutor());
            try {
                System.gc();
                //Glide.with(context).load(noticia.getImagenUrl()).placeholder(R.drawable.nopic).into(vh.imagen);
                Glide.with(context).load(noticia.getImagenUrl()).placeholder(R.mipmap.nopic).into(vh.imagen);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        vh.autor.setTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-Regular.otf"));
        vh.titulo.setTypeface(Typeface.createFromAsset(context.getAssets(), "Milio-Heavy-Italic.ttf"));

        //onClickListenerNoticia
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos un intent para llamar a NoticiasContentActivity con los extras de la noticia correspondiente
                Intent intent = new Intent(context, NoticiaContentActivity.class);
                intent.putExtra("titulo", noticia.getTitulo());
                System.out.print("DESC" + noticia.getDescripcion());
                intent.putExtra("descripcion", noticia.getDescripcion());
                intent.putExtra("autor", noticia.getAutor());
                intent.putExtra("fecha", noticia.getFecha());
                intent.putExtra("link", noticia.getLink());
                intent.putExtra("imagenUrl", noticia.getImagenUrl());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        vh.botonCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                String textoCompartir = noticia.getLink() + "\n\n" + noticia.getTitulo();

                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, textoCompartir);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setType("text/plain");

                v.getContext().startActivity(Intent.createChooser(intent, v.getContext().getResources().getString(R.string.share)));
            }
        });
    }

    private void configureCardPubliViewHolder(final CardPubliViewHolder vh, int position) {

        String url = "";
        String info = "";



        PublisherAdView mPublisherAdView = vh.mPublisherAdView;
        mPublisherAdView.setAdSizes(AdSize.BANNER);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);

       /* final Noticia noticia = (Noticia) items.get(position);
        if (noticia != null) {
            vh.titulo.setText(Html.fromHtml(noticia.getTitulo()));
            vh.autor.setText(noticia.getAutor());
            try {
                System.gc();
                //Glide.with(context).load(noticia.getImagenUrl()).placeholder(R.drawable.nopic).into(vh.imagen);
                Glide.with(context).load(noticia.getImagenUrl()).placeholder(R.mipmap.nopic).into(vh.imagen);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        vh.autor.setTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-Regular.otf"));
        vh.titulo.setTypeface(Typeface.createFromAsset(context.getAssets(), "Milio-Heavy-Italic.ttf"));

        //onClickListenerNoticia
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos un intent para llamar a NoticiasContentActivity con los extras de la noticia correspondiente
                Intent intent = new Intent(context, NoticiaContentActivity.class);
                intent.putExtra("titulo", noticia.getTitulo());
                System.out.print("DESC" + noticia.getDescripcion());
                intent.putExtra("descripcion", noticia.getDescripcion());
                intent.putExtra("autor", noticia.getAutor());
                intent.putExtra("fecha", noticia.getFecha());
                intent.putExtra("link", noticia.getLink());
                intent.putExtra("imagenUrl", noticia.getImagenUrl());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        vh.botonCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                String textoCompartir = noticia.getLink() + "\n\n" + noticia.getTitulo();

                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, textoCompartir);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setType("text/plain");

                v.getContext().startActivity(Intent.createChooser(intent, v.getContext().getResources().getString(R.string.share)));
            }
        });*/
    }

    private void configureContadorViewHolder(ContadorViewHolder vh3, int position) {
        vh3.showContador();

        vh3.label.setTypeface(Typeface.createFromAsset(context.getAssets(), "Milio-Bold.ttf"));
        vh3.barra.setTypeface(Typeface.createFromAsset(context.getAssets(), "Milio-Bold.ttf"));
        vh3.d_20.setTypeface(Typeface.createFromAsset(context.getAssets(), "Milio-Bold.ttf"));

        vh3.preferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PreferencesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    private void configureEncuestasViewHolder(EncuestasViewHolder vh3, int position) {
        //final BarChart grafico = (BarChart) items.get(position);

        if(HomeTab.encuestas.size()>0){


        this.grafico = vh3.grafico;
        Spinner spinner = vh3.spinner;

        grafico.setDrawBarShadow(false);
        grafico.setDrawValueAboveBar(true);

        grafico.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        grafico.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        grafico.setPinchZoom(false);

        grafico.setDrawGridBackground(false);
        grafico.setClickable(false);
        grafico.setPinchZoom(false);
        grafico.setDoubleTapToZoomEnabled(false);
        grafico.setTouchEnabled(false);

        // grafico.setDrawYLabels(false);

        //Typeface mTf = Typeface.createFromAsset(context.getAssets(), "Titilium-Regular.otf");

        XAxis xAxis = grafico.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-Regular.otf"));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setSpaceBetweenLabels(2);



        YAxis leftAxis = grafico.getAxisLeft();
        leftAxis.setTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-Regular.otf"));
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setEnabled(false);

        YAxis rightAxis = grafico.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-Regular.otf"));
        rightAxis.setLabelCount(8, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setEnabled(false);

        Legend l = grafico.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        l.setEnabled(false);
        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });


        List<String> encuestasTitulo = new ArrayList<String>();
        for(int i=0; i<HomeTab.encuestas.size(); i++){
            Log.d("ENCUESTAS", HomeTab.encuestas.get(i).getName());
            encuestasTitulo.add(HomeTab.encuestas.get(i).getName());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, R.layout.row_custom_spinner_encuesta, R.id.nombreEncuesta, encuestasTitulo);
        spinner.setAdapter(dataAdapter);




        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    encuestaSeleccionada = position;
                    Map<String, String> encuestas = new HashMap<>();
                    encuestas.put("encuesta", HomeTab.encuestas.get(position).getName());
                    //encuestas.put("persona", this.persona);
                    ParseAnalytics.trackEvent("ECL_SELECTOR_ENCUESTAS", encuestas);
                    setData(grafico, HomeTab.encuestas.get(position));
                }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        setData(grafico, HomeTab.encuestas.get(encuestaSeleccionada));

        }


    }

    private void setData(BarChart grafico, Encuesta e){

        grafico.animateY(2500);
        ArrayList<Integer> colores = new ArrayList<>();
        List<Partido> partidosJsonDatos = MainActivity.partidosList;
        for(int i=0; i<e.getPartidosEncuesta().size(); i++){
            colores.add(Color.parseColor(partidosJsonDatos.get(i).getColor()));
        }

        //Typeface mTf = Typeface.createFromAsset(context.getAssets(), "Titilium-Regular.otf");
        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < e.getPartidosEncuesta().size(); i++) {
           // Log.d("Encuestas", "contiente " + e.getPartidosEncuesta().get(i).getName() + " con porcentaje " + e.getPartidosEncuesta().get(i).getPorcentaje());
            xVals.add(e.getPartidosEncuesta().get(i).getName());

            for (int j=0; j<partidosJsonDatos.size(); j++){
                if(e.getPartidosEncuesta().get(i).getName().equalsIgnoreCase(partidosJsonDatos.get(j).getId())){
                    colores.set(i,Color.parseColor(partidosJsonDatos.get(j).getColor()));
                    xVals.set(i, partidosJsonDatos.get(j).getSiglas());
                }

            }
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < e.getPartidosEncuesta().size(); i++) {

            yVals1.add(new BarEntry((float)e.getPartidosEncuesta().get(i).getPorcentaje(), i));
        }


        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        set1.setBarSpacePercent(15f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        set1.setColors(colores);
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-Regular.otf"));;

        grafico.setData(data);

        grafico.invalidate();

    }

    private void configurePresinderViewHolder(final PresinderViewHolder vh, int position) {
        final Quote quote = (Quote) items.get(position);

        vh.question.setText(quote.getText());
        vh.group.setText(quote.getGrupo());

        try{
            Glide.with(context).load(R.drawable.caraok).into(vh.like);
            Glide.with(context).load(R.drawable.carano).into(vh.dislike);
        }catch (Exception e){
            e.printStackTrace();
        }

        //Like/Dislikes listeners
       // vh.like.setOnClickListener(new OnLikeClickListener(context));
       // vh.dislike.setOnClickListener(new OnDislikeClickListener(context));

        //Fonts
        vh.group.setTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-Regular.otf"));
        vh.question.setTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-BoldItalic.otf"));
        vh.likeText.setTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-Regular.otf"));
        vh.dislikeText.setTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-Regular.otf"));
    }


    private void configurePoliticoViewHolder(final PoliticoViewHolder vh, int position) {
        final Persona persona = (Persona) items.get(position);

        vh.posicion.setText(""+(position+1));
        vh.nombre.setText(persona.getNiceName());
        vh.partido.setText(persona.getParty());
        vh.nAgrees.setText("" + persona.getAgree());
        vh.nDisAgrees.setText("" + persona.getDisagree());
        try{
            Glide.with(context).load(context.getResources().getIdentifier(persona.getPhotoLink(),"drawable", context.getPackageName())).placeholder(R.mipmap.circlenopic).into(vh.fotoPolitico);
            Glide.with(context).load(R.drawable.caralittleok).into(vh.likesPolitico);
            Glide.with(context).load(R.drawable.caralittleno).into(vh.dislikesPolitico);
        }catch (Exception e){
            e.printStackTrace();
        }

        //Fonts
        vh.posicion.setTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-Semibold.otf"));
        vh.nombre.setTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-Semibold.otf"));
        vh.partido.setTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-Regular.otf"));
        vh.nAgrees.setTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-Regular.otf"));
        vh.nDisAgrees.setTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-Regular.otf"));

    }

    private void configureTituloViewHolder(TituloViewHolder vh6, int position) {
        final Titulo title = (Titulo) items.get(position);
        if (title != null) {
            vh6.title.setText(title.getTitle());
        }

        //Fonts
        vh6.title.setTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-Regular.otf"));


    }

    private void configureSpinnerViewHolder(final SpinnerViewHolder vh, int position) {


        String[] arrayPartidos = context.getResources().getStringArray(R.array.partidos);
        List<String> spinnerArray =  new ArrayList<String>();


        //Para que al recargar el recycler view, el valor que salga sea el que ha seleccionado
        switch(NoticiasTab.seleccion){
            case 0:
                spinnerArray.add(context.getResources().getString(R.string.elige_partido_politico));
                break;
            case 1:
                spinnerArray.add("General");
                break;
            case 2:
                spinnerArray.add(context.getResources().getString(R.string.pp));
                break;
            case 3:
                spinnerArray.add(context.getResources().getString(R.string.psoe));
                break;
            case 4:
                spinnerArray.add(context.getResources().getString(R.string.ciudadanos));
                break;
            case 5:
                spinnerArray.add(context.getResources().getString(R.string.podemos));
                break;
            case 6:
                spinnerArray.add(context.getResources().getString(R.string.iu));
                break;
            case 7:
                spinnerArray.add(context.getResources().getString(R.string.upyd));
                break;


        }
        //Default value
        //spinnerArray.add(context.getResources().getString(R.string.elige_partido_politico));

        for (int i = 0; i<arrayPartidos.length;i++){
            spinnerArray.add(arrayPartidos[i]);
        }

        PartidoSpinnerAdapter adapter = new PartidoSpinnerAdapter(
                context, R.layout.row_custom_spinner_partido, spinnerArray);

        vh.spinner.setAdapter(adapter);

        vh.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        NoticiasTab.seleccion = 0;
                        break;
                    case 1:
                        NoticiasTab.rss_url  = "http://rss.elconfidencial.com/tags/organismos/partido-popular-pp-3113/"; //general
                        NoticiasTab.seleccion = 1;
                        new CargarXmlTask().execute(NoticiasTab.rss_url);
                    case 2:
                        NoticiasTab.rss_url  = "http://rss.elconfidencial.com/tags/organismos/partido-popular-pp-3113/"; //PP
                        NoticiasTab.seleccion = 2;
                        new CargarXmlTask().execute(NoticiasTab.rss_url);
                        break;
                    case 3:
                        NoticiasTab.rss_url  = "http://rss.elconfidencial.com/tags/organismos/psoe-7017/"; //PSOE
                        NoticiasTab.seleccion = 3;
                        new CargarXmlTask().execute(NoticiasTab.rss_url);
                        break;
                    case 4:
                        NoticiasTab.rss_url  = "http://rss.elconfidencial.com/tags/organismos/ciudadanos-6359/";  //Ciudadanos
                        NoticiasTab.seleccion = 4;
                        new CargarXmlTask().execute(NoticiasTab.rss_url);
                        break;
                    case 5:
                        NoticiasTab.rss_url  = "http://rss.elconfidencial.com/tags/organismos/podemos-10616/";  //Podemos
                        NoticiasTab.seleccion = 5;
                        new CargarXmlTask().execute(NoticiasTab.rss_url);
                        break;
                    case 6:
                        NoticiasTab.rss_url  = "http://rss.elconfidencial.com/tags/organismos/izquierda-unida-2547/";   //IU
                        NoticiasTab.seleccion = 6;
                        new CargarXmlTask().execute(NoticiasTab.rss_url);
                        break;
                    case 7:
                        NoticiasTab.rss_url  = "http://rss.elconfidencial.com/tags/organismos/upyd-2430/";  //UPYD
                        NoticiasTab.seleccion = 7;
                        new CargarXmlTask().execute(NoticiasTab.rss_url);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    /*Permite gestionar de forma asincrona el RSS */
    private class CargarXmlTask extends AsyncTask<String,Integer,Boolean> {

        List<Object> items = new ArrayList<>();
        List<Noticia> noticias = new ArrayList<>();
        GlobalMethod globalMethod = new GlobalMethod(context);


        protected Boolean doInBackground(String... params) {
            try {
                if(globalMethod.haveNetworkConnection()) {
                    RssNoticiasParser saxparser =
                            new RssNoticiasParser(params[0]);

                    noticias = saxparser.parse();

                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }
        protected void onPostExecute(Boolean result) {

            addItems();

        }

        public void addItems() {

            Spinner spinner = new Spinner(context);
            items.add(spinner);

//            if(globalMethod.haveNetworkConnection()) {
            for (Noticia noticia : noticias){
                items.add(noticia);
            }
            /**    } else{
             //Mensaje de error
             Log.i("MyTag", "He pasado por el mensaje de error");
             }**/


            NoticiasTab.mAdapter = new MyRecyclerViewAdapter(MainActivity.context,items);
            NoticiasTab.mRecyclerView.setAdapter(NoticiasTab.mAdapter);
        }

    }
}
