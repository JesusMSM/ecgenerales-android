package es.elconfidencial.eleccionesgenerales2015.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
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

import com.bumptech.glide.Glide;

import es.elconfidencial.eleccionesgenerales2015.R;

public class NoticiaContentActivity  extends ActionBarActivity {

    private String url = "";
    private String info = "";
    private String textSize= "";
    private WebView descripcion;
    private Intent intent;

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
        descripcion = (WebView)findViewById(R.id.descripcion);
        TextView autor = (TextView) findViewById(R.id.autor);
        TextView fecha = (TextView) findViewById(R.id.fecha);

        titulo.setText(Html.fromHtml(intent.getStringExtra("titulo")));
        autor.setText(Html.fromHtml(intent.getStringExtra("autor")));
        fecha.setText(getTimeAgo(intent.getStringExtra("fecha")));

        //Obtenemos el tamaño de letra del contenido dependiendo del tamaño de pantalla

        if (getSizeName().equals("xlarge")) {
            textSize="25px";
        } else if (getSizeName().equals("large")) {
            textSize="18px";
        } else if (getSizeName().equals("normal")) {
            textSize="16px";
        }else {
            textSize="14px";
        }


        //Insertamos la cabecera al html con el estilo
        String head = "<head><style>@font-face {font-family: MilioHeavy;src: url(\"file:///android_asset/Milio-Heavy.ttf\")}" +
                "@font-face {font-family: TitilliumLight;src: url(\"file:///android_asset/Titillium-Light.otf\")}" +
                "@font-face {font-family: TitilliumSemibold;src: url(\"file:///android_asset/Titillium-Semibold.otf\")}" +
                "h2{font-family: MilioHeavy;}" +
                "img{max-width: 100%; width:auto; height: auto;}" +
                "body{font-family:TitilliumLight;text-align:justify}" +
                "a{text-decoration: none;color:black;} " +
                "html { font-size: " + textSize + "}" +
                "strong{font-family:TitilliumSemibold;}</style></head>";

        String htmlString ="<html>" + head + "<body><div>" + intent.getStringExtra("descripcion") + "</div></body></html>";

        //String htmlStringFormatted = htmlString.replace("<img src[^>]*>", "");
        //Quitar la imagen del final

        descripcion.getSettings().setJavaScriptEnabled(true);
        descripcion.getSettings().setDefaultTextEncodingName("utf-8");
        descripcion.loadDataWithBaseURL("", htmlString, "text/html", "charset=UTF-8", null);
        descripcion.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //Bloquear links
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        // disable scroll on touch
        descripcion.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        //Estilo
        titulo.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Milio-Heavy-Italic.ttf"));
        autor.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Regular.otf"));
        fecha.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Regular.otf"));
        Glide.with(getApplicationContext()).load(intent.getStringExtra("imagenUrl")).placeholder(R.drawable.nopic).into(imagen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_noticia_content, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void changeSize(){

        int textSizeInt = Integer.valueOf(textSize.substring(0, textSize.length() - 2));

        if (getSizeName().equals("xlarge")) {
            textSizeInt=textSizeInt+3;
            if(textSizeInt>40) textSizeInt=25;
            textSize=textSizeInt+"px";
        }else{
            textSizeInt=textSizeInt+2;
            if(textSizeInt>25) textSizeInt=14;
            textSize=textSizeInt+"px";
        }



        //Insertamos la cabecera al html con el estilo
        String head = "<head><style>@font-face {font-family: MilioHeavy;src: url(\"file:///android_asset/Milio-Heavy.ttf\")}" +
                "@font-face {font-family: TitilliumLight;src: url(\"file:///android_asset/Titillium-Light.otf\")}" +
                "@font-face {font-family: TitilliumSemibold;src: url(\"file:///android_asset/Titillium-Semibold.otf\")}" +
                "h2{font-family: MilioHeavy;}" +
                "img{max-width: 100%; width:auto; height: auto;}" +
                "body{font-family:TitilliumLight;text-align:justify}" +
                "a{text-decoration: none;color:black;} " +
                "html { font-size: " + textSize + "}" +
                "strong{font-family:TitilliumSemibold;}</style></head>";

        String htmlString ="<html>" + head + "<body><div>" + intent.getStringExtra("descripcion") + "</div></body></html>";

        //String htmlStringFormatted = htmlString.replace("<img src[^>]*>", "");
        //Quitar la imagen del final

        descripcion.getSettings().setJavaScriptEnabled(true);
        descripcion.getSettings().setDefaultTextEncodingName("utf-8");
        descripcion.loadDataWithBaseURL("", htmlString, "text/html", "charset=UTF-8", null);
        descripcion.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //Bloquear links
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        // disable scroll on touch
        descripcion.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        if(item.getItemId() == R.id.share){
            shareAction(url,info);
        }
        if(item.getItemId() == R.id.size){
            changeSize();
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

        startActivity(  Intent.createChooser( intent, getString(R.string.share) )  );
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