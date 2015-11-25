package es.elconfidencial.eleccionesgenerales2015.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

import org.json.JSONException;
import org.json.JSONObject;

import es.elconfidencial.eleccionesgenerales2015.R;

public class NoticiaContentActivity  extends ActionBarActivity {

    private String url = "";
    private String info = "";
    private String textSize= "";
    private WebView descripcion1, descripcion2;
    private TextView textoPrueba;
    private String head="";
    private String htmlString1,htmlString2, contenido1, contenido2 ="";
    private Intent intent;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //MultiDex.install(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticia_content);

        //comScore.setAppContext(this.getApplicationContext());

        //ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled( true );
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.custom_title_noticias, null);
        ((TextView)v.findViewById(R.id.actionBarTitle)).setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Light.otf"));
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Extraemos el intent para leer los par�metros y rellenar los campos
        intent = getIntent();

        //Datos para compartir
        url = intent.getStringExtra("link");
        info = Html.fromHtml(intent.getStringExtra("titulo")).toString();

        TextView titulo = (TextView) findViewById(R.id.titulo);
        ImageView imagen = (ImageView) findViewById(R.id.imagen);
        descripcion1 = (WebView)findViewById(R.id.descripcion1);
        descripcion2 = (WebView)findViewById(R.id.descripcion2);
        TextView autor = (TextView) findViewById(R.id.autor);
        TextView fecha = (TextView) findViewById(R.id.fecha);
        final ImageView imagenPubli = (ImageView) findViewById(R.id.imageView);
        final NativeContentAdView adView = (NativeContentAdView) findViewById(R.id.adView);


        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        titulo.setText(Html.fromHtml(intent.getStringExtra("titulo")));
        autor.setText(Html.fromHtml(intent.getStringExtra("autor")));
        fecha.setText(getTimeAgo(intent.getStringExtra("fecha")));

        //Obtenemos el tamaño de letra del contenido dependiendo del tamaño de pantalla

        //Comprueba si es la primera vez
        boolean firstTime = prefs.getBoolean("firstTimeNews", true); //Si no existe, devuelve el segundo parametro
        if(firstTime) {
            if (getSizeName().equals("xlarge")) {
                textSize = "28px";
            } else if (getSizeName().equals("large")) {
                textSize = "21px";
            } else if (getSizeName().equals("normal")) {
                textSize = "19px";
            } else {
                textSize = "17px";
            }
            editor.putString("textSize", textSize);
            editor.putBoolean("firstTimeNews", false);
            editor.apply();
            Log.d("Primera vez", "Ha entrado en primera vez");
            reloadDescription();
        }else{
            textSize = prefs.getString("textSize","");
            reloadDescription();
        }

        //Estilo
        titulo.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Milio-Heavy.ttf"));
        autor.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Regular.otf"));
        fecha.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Regular.otf"));
        Glide.with(getApplicationContext()).load(intent.getStringExtra("imagenUrl")).placeholder(R.mipmap.nopic).into(imagen);

        AdLoader builder = new AdLoader.Builder(this, getResources().getString(R.string.ad_unit))

                .forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                    @Override
                    public void onContentAdLoaded(NativeContentAd contentAd) {

                       imagenPubli.setImageDrawable(
                                contentAd.getImages().get(0).getDrawable());
                        adView.setImageView(imagenPubli);
                        adView.setNativeAd(contentAd);

                        
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        /*Toast.makeText(getApplicationContext(), "Failed to load native ad: "
                                + errorCode, Toast.LENGTH_SHORT).show();*/
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

        builder.loadAd(new PublisherAdRequest.Builder().build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_noticia_content, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void reduceTamaño(){
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
        reloadDescription();


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
        reloadDescription();


    }

    public void reloadDescription(){
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        if(item.getItemId() == R.id.share){
           shareAction(url, info);
        }
        if(item.getItemId() == R.id.aumenta){
            aumentarTamaño();
        }
        if(item.getItemId() == R.id.reduce){
            reduceTamaño();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //Amplitude
        Log.i("20D_AMPLITUDE", "ONTAP_FONT: "+ textSize);
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put("FONT SIZE", textSize);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent("ONTAP_FONT", eventProperties);

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
        Log.i("20D_AMPLITUDE", "ONSHARE: "+ url);
        JSONObject eventProperties = new JSONObject();
        try {
            eventProperties.put("NEWS URL", url);
        } catch (JSONException exception) {
        }
        Amplitude.getInstance().logEvent("ONSHARE", eventProperties);
    }

    //Formateadores para la fecha ( hace X minutos)
    public static String getTimeAgo(String timeCTE) {
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
        //Re-register receivers on resume
        //comScore.onEnterForeground();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Unregister receivers on pause
        //comScore.onExitForeground();
    }
}