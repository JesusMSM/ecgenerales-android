package es.elconfidencial.eleccionesgenerales2015.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import es.elconfidencial.eleccionesgenerales2015.R;
import es.elconfidencial.eleccionesgenerales2015.activities.MainActivity;
import es.elconfidencial.eleccionesgenerales2015.activities.NoticiaContentActivity;
import es.elconfidencial.eleccionesgenerales2015.fragments.NoticiasTab;
import es.elconfidencial.eleccionesgenerales2015.listeners.OnDislikeClickListener;
import es.elconfidencial.eleccionesgenerales2015.listeners.OnLikeClickListener;
import es.elconfidencial.eleccionesgenerales2015.model.GlobalMethod;
import es.elconfidencial.eleccionesgenerales2015.model.Noticia;
import es.elconfidencial.eleccionesgenerales2015.model.Persona;
import es.elconfidencial.eleccionesgenerales2015.model.Quote;
import es.elconfidencial.eleccionesgenerales2015.rss.RssNoticiasParser;
import es.elconfidencial.eleccionesgenerales2015.viewholders.NoticiaViewHolder;
import es.elconfidencial.eleccionesgenerales2015.viewholders.PoliticoViewHolder;
import es.elconfidencial.eleccionesgenerales2015.viewholders.PresinderViewHolder;
import es.elconfidencial.eleccionesgenerales2015.viewholders.SpinnerViewHolder;

/**
 * Created by Moonfish on 28/10/15.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The items to display in your RecyclerView
    private List<Object> items;
    Context context;

    private final int NOTICIA = 0,PRESINDER = 1, POLITICO = 2, SPINNER = 3;


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
        else if (items.get(position) instanceof Spinner) {
            return SPINNER;
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


    private void configurePresinderViewHolder(final PresinderViewHolder vh, int position) {
        final Quote quote = (Quote) items.get(position);

        vh.question.setText(quote.getText());
        vh.group.setText(quote.getGrupo());

        //Like/Dislikes listeners
        vh.like.setOnClickListener(new OnLikeClickListener(context));
        vh.dislike.setOnClickListener(new OnDislikeClickListener(context));

        //Fonts
        vh.group.setTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-Regular.otf"));
        vh.question.setTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-BoldItalic.otf"));
    }


    private void configurePoliticoViewHolder(final PoliticoViewHolder vh, int position) {
        final Persona persona = (Persona) items.get(position);

        vh.posicion.setText(""+(position+1));
        vh.nombre.setText(persona.getName());
        vh.partido.setText(persona.getParty());
        vh.nAgrees.setText("" + persona.getAgree());
        vh.nDisAgrees.setText("" + persona.getDisagree());
        try{
            Glide.with(context).load(R.drawable.nopicpersona).into(vh.fotoPolitico);
        }catch (Exception e){
            e.printStackTrace();
        }

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
                spinnerArray.add(context.getResources().getString(R.string.pp));
                break;
            case 2:
                spinnerArray.add(context.getResources().getString(R.string.psoe));
                break;
            case 3:
                spinnerArray.add(context.getResources().getString(R.string.ciudadanos));
                break;
            case 4:
                spinnerArray.add(context.getResources().getString(R.string.podemos));
                break;
            case 5:
                spinnerArray.add(context.getResources().getString(R.string.iu));
                break;
            case 6:
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
                        NoticiasTab.rss_url  = "http://rss.elconfidencial.com/tags/organismos/partido-popular-pp-3113/"; //PP
                        NoticiasTab.seleccion = 1;
                        new CargarXmlTask().execute(NoticiasTab.rss_url);
                        break;
                    case 2:
                        NoticiasTab.rss_url  = "http://rss.elconfidencial.com/tags/organismos/cup-15022/"; //PSOE
                        NoticiasTab.seleccion = 2;
                        new CargarXmlTask().execute(NoticiasTab.rss_url);
                        break;
                    case 3:
                        NoticiasTab.rss_url  = "http://rss.elconfidencial.com/tags/personajes/catalunya-si-que-es-pot-15843/";  //Ciudadanos
                        NoticiasTab.seleccion = 3;
                        new CargarXmlTask().execute(NoticiasTab.rss_url);
                        break;
                    case 4:
                        NoticiasTab.rss_url  = "http://rss.elconfidencial.com/tags/organismos/partido-popular-pp-3113/";  //Podemos
                        NoticiasTab.seleccion = 4;
                        new CargarXmlTask().execute(NoticiasTab.rss_url);
                        break;
                    case 5:
                        NoticiasTab.rss_url  = "http://rss.elconfidencial.com/tags/organismos/partido-popular-pp-3113/";   //IU
                        NoticiasTab.seleccion = 5;
                        new CargarXmlTask().execute(NoticiasTab.rss_url);
                        break;
                    case 6:
                        NoticiasTab.rss_url  = "http://rss.elconfidencial.com/tags/organismos/partido-popular-pp-3113/";  //UPYD
                        NoticiasTab.seleccion = 6;
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
