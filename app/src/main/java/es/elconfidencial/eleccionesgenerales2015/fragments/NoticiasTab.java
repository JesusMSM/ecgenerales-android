package es.elconfidencial.eleccionesgenerales2015.fragments;


import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import es.elconfidencial.eleccionesgenerales2015.R;
import es.elconfidencial.eleccionesgenerales2015.activities.MainActivity;
import es.elconfidencial.eleccionesgenerales2015.adapters.MyRecyclerViewAdapter;
import es.elconfidencial.eleccionesgenerales2015.model.CardPubli;
import es.elconfidencial.eleccionesgenerales2015.model.GlobalMethod;
import es.elconfidencial.eleccionesgenerales2015.model.Noticia;
import es.elconfidencial.eleccionesgenerales2015.model.Quote;
import es.elconfidencial.eleccionesgenerales2015.rss.RssNoticiasParser;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoticiasTab extends Fragment {

    public static String rss_url = "http://rss.elconfidencial.com/tags/organismos/partido-popular-pp-3113/";
    public static int seleccion = 0;

    private TextView actionBarTitle;

    //RecyclerView atributtes
    public static RecyclerView mRecyclerView;
    public static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_noticias_tab, container, false);

        actionBarTitle = (TextView) v.findViewById(R.id.actionBarNoticias);
        actionBarTitle.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Titillium-Regular.otf"));

        //RecyclerView
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rss_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(MainActivity.context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        new CargarXmlTask().execute(rss_url);


        return v;
    }



    /*Permite gestionar de forma asincrona el RSS */
    private class CargarXmlTask extends AsyncTask<String,Integer,Boolean> {

        List<Object> items = new ArrayList<>();
        List<Noticia> noticias = new ArrayList<>();
        GlobalMethod globalMethod = new GlobalMethod(getContext());


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
            
            Spinner spinner = new Spinner(getActivity());
            items.add(spinner);

//            if(globalMethod.haveNetworkConnection()) {
            int i =0;
                for (Noticia noticia : noticias){
                    if (i%MainActivity.DFP_CARD_EVERY_N==0&&i>0) items.add(new CardPubli());
                    items.add(noticia);
                    i++;
                }
        /**    } else{
                //Mensaje de error
                Log.i("MyTag", "He pasado por el mensaje de error");
            }**/


            mAdapter = new MyRecyclerViewAdapter(MainActivity.context,items);
            mRecyclerView.setAdapter(mAdapter);
        }

        }

}
