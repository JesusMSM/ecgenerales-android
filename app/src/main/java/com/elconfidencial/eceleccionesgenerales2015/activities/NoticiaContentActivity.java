package com.elconfidencial.eceleccionesgenerales2015.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplitude.api.Amplitude;
import com.bumptech.glide.Glide;
import com.comscore.analytics.comScore;
import com.elconfidencial.eceleccionesgenerales2015.adapters.WebViewPagerAdapter;
import com.elconfidencial.eceleccionesgenerales2015.fragments.WebFragment;
import com.elconfidencial.eceleccionesgenerales2015.model.GlobalMethod;
import com.elconfidencial.eceleccionesgenerales2015.model.Noticia;
import com.elconfidencial.eceleccionesgenerales2015.text.TitilliumRegularTextView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

import org.json.JSONException;
import org.json.JSONObject;

import com.elconfidencial.eceleccionesgenerales2015.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NoticiaContentActivity  extends AppCompatActivity {
    private String url = "";
    private String info = "";
    private String textSize= "";
    private WebView webView;
    private Intent intent;
    Toolbar toolbar;
    ActionBar actionBar;
    Noticia noticia;

    ViewPager pager;
    ArrayList<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //MultiDex.install(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticia_content);

        //comScore.setAppContext(this.getApplicationContext());

        setupToolbar();

        //Lista noticias
        Gson gson = new Gson();
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        String json = prefs.getString("noticiasSpinner", "");
        Type type = new TypeToken<ArrayList<Noticia>>() {}.getType();
        final ArrayList<Noticia> noticias = gson.fromJson(json, type);

        intent = getIntent();
        //Get lista noticias y la actual
        noticia = intent.getExtras().getParcelable("currentNoticia");
        url=noticia.getLink();
        info=noticia.getTitulo();

        //Pager config
        pager = (ViewPager) findViewById(R.id.webPager);
        fragmentList = new ArrayList<>();
        int index = 0;

        for(int i = 0; i<noticias.size();i++) {
            fragmentList.add(WebFragment.newInstance(noticias.get(i)));
            if(noticias.get(i).getLink().equals(noticia.getLink())){
                index = i;
            }
        }
        WebViewPagerAdapter adapter = new WebViewPagerAdapter(getSupportFragmentManager(),fragmentList);

        //Set adapter
        pager.setAdapter(adapter);
        pager.setCurrentItem(index);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                url=noticias.get(i).getLink();
                info=noticias.get(i).getTitulo();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_noticia_content, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*public void reduceTamaño(){
        int textSizeInt = Integer.valueOf(textSize.substring(0, textSize.length() - 2));

        SharedPreferences.Editor editor = prefs.edit();

        if (getSizeName().equals("xlarge")) {
            textSizeInt=textSizeInt-3;
            if(textSizeInt<20) textSizeInt=20;
            textSize=textSizeInt+"px";

        }else{
            textSizeInt=textSizeInt-2;
            if(textSizeInt<10) textSizeInt=10;
            textSize=textSizeInt+"px";

        }
        editor.putString("textSize", textSize);
        editor.apply();
        //reloadDescription();


    }

    public void aumentarTamaño(){
        int textSizeInt = Integer.valueOf(textSize.substring(0, textSize.length() - 2));

        SharedPreferences.Editor editor = prefs.edit();

        if (getSizeName().equals("xlarge")) {
            textSizeInt=textSizeInt+3;
            if(textSizeInt>40) textSizeInt=40;
            textSize=textSizeInt+"px";

        }else{
            textSizeInt=textSizeInt+2;
            if(textSizeInt>25) textSizeInt=25;
            textSize=textSizeInt+"px";
        }
        editor.putString("textSize", textSize);
        editor.apply();
        //reloadDescription();


    }*/

    /*public void reloadDescription(){
        //Insertamos la cabecera al html con el estilo
        head = "<head><style>@font-face {font-family: MilioHeavy;src: url(\"file:///android_asset/Milio-Heavy.ttf\")}" +
                "@font-face {font-family: TitilliumLight;src: url(\"file:///android_asset/Titillium-Light.otf\")}" +
                "@font-face {font-family: TitilliumSemibold;src: url(\"file:///android_asset/Titillium-Semibold.otf\")}" +
                "h2{font-family: MilioHeavy;}" +
                "img{max-width: 100%; width:auto; height: auto;}" +
                "body{font-family:TitilliumLight;}" +

                "html { font-size: " + textSize + "}" +
                "strong{font-family:TitilliumSemibold;}</style></head>";

        String contenido = intent.getStringExtra("descripcion");
        splitContenido(contenido);


        htmlString1 ="<html>" + head + "<body><div>" + contenido1 + "</div></body></html>";

        //String htmlStringFormatted = htmlString.replace("<img src[^>]*>", "");
        //Quitar la imagen del final
        //Texto antes de anuncio

        descripcion1.getSettings().setJavaScriptEnabled(true);
        descripcion1.getSettings().setDefaultTextEncodingName("utf-8");
        descripcion1.loadDataWithBaseURL("", htmlString1, "text/html", "charset=UTF-8", null);
        descripcion1.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //Bloquear links
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
                return true;
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        // disable scroll on touch
        descripcion1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        //Texto después de anuncio

        htmlString2 ="<html>" + head + "<body><div>" + contenido2 + "</div></body></html>";

        descripcion2.getSettings().setJavaScriptEnabled(true);
        descripcion2.getSettings().setDefaultTextEncodingName("utf-8");
        descripcion2.loadDataWithBaseURL("", htmlString2, "text/html", "charset=UTF-8", null);
        descripcion2.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //Bloquear links
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
                return true;
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        // disable scroll on touch
        descripcion2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });


    }

    public void splitContenido(String contenido){

        String[] contenidoArray = contenido.split("</p>");
        contenido1 = contenidoArray[0] + "</p>";
        contenido2 = "";
        for (int i = 1; i < contenidoArray.length; i++) {
            if(i!=contenidoArray.length-1){
                contenido2 += contenidoArray[i] + "</p>";
            } else{
                contenido2 += contenidoArray[i];
            }

        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        if(item.getItemId() == R.id.share){
           shareAction(url, info);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        System.gc();
        finish();
        super.onBackPressed();
    }

    private String getSizeName() {
        int screenLayout = getResources().getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenLayout) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return "small";
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return "normal";
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return "large";
            case 4: // Configuration.SCREENLAYOUT_SIZE_XLARGE is API >= 9
                return "xlarge";
            default:
                return "undefined";
        }
    }

    public void shareAction(String url, String info){
        // Llama al sistema para que le muestre un diálogo al usuario con todas las aplicaciones que permitan compartir información
        Intent intent = new Intent();
        String textoCompartir = info + "\n\n" + url;

        intent.setAction( Intent.ACTION_SEND );
        intent.putExtra(Intent.EXTRA_TEXT, textoCompartir );
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType( "text/plain" );

        startActivity(Intent.createChooser(intent, getString(R.string.share)));

        //Amplitude
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put("page", "noticia_content");
            eventProperties.put("section", noticia.getTag());
            eventProperties.put("title", noticia.getTitulo());
            eventProperties.put("author", noticia.getAutor());
            eventProperties.put("url", noticia.getLink());
            eventProperties.put("action", "modal_inside");
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent("Share", eventProperties);
    }

    //Formateadores para la fecha ( hace X minutos)
    public static String getTimeAgo(String timeCTE) {
        Log.i("FECHA_TIME AGO", timeCTE);
        long time = 0;

        try{
            long epoch = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+02:00").parse(timeCTE).getTime();
            time=epoch;
        }catch(Exception e){
            e.printStackTrace();
        }
        final long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < C.MINUTE_MILLIS) {
            return "ahora";
        } else if (diff < 2 * C.MINUTE_MILLIS) {
            return "hace un minuto";
        } else if (diff < 50 * C.MINUTE_MILLIS) {
            return "hace "+diff / C.MINUTE_MILLIS + " minutos";
        } else if (diff < 90 * C.MINUTE_MILLIS) {
            return "hace una hora";
        } else if (diff < 24 * C.HOUR_MILLIS) {
            return "hace "+diff / C.HOUR_MILLIS + " horas";
        } else if (diff < 48 * C.HOUR_MILLIS) {
            return "ayer";
        } else {
            return "hace "+diff / C.DAY_MILLIS + " d\u00edas";
        }
    }
    public class C {
        /** One second (in milliseconds) */
        public static final int _A_SECOND = 1000;
        /** One minute (in milliseconds) */
        public static final int MINUTE_MILLIS = 60 * _A_SECOND;
        /** One hour (in milliseconds) */
        public static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        /** One day (in milliseconds) */
        public static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            //comScore
            Log.i("Comscore", "Dentro de on Resume");
            comScore.onEnterForeground();
        }catch (Exception e){
            Log.i("Comscore", "Error Comscore");
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            //comScore
            Log.i("Comscore", "Dentro de on Pause");
            comScore.onExitForeground();
        }catch (Exception e){
            Log.i("Comscore", "Error Comscore");
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            //comScore
            Log.i("Comscore", "Se sale de la app con onDestroy");
            comScore.onExitForeground();
        }catch (Exception e){
            Log.i("Comscore", "Error Comscore");
            e.printStackTrace();
        }
    }
    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(null);
            //actionBar.setHomeAsUpIndicator(R.drawable.elconfidencial_32dp_white);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_actionbar);
        }
    }

}