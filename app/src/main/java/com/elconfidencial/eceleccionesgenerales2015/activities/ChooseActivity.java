package com.elconfidencial.eceleccionesgenerales2015.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.amplitude.api.Amplitude;
import com.bumptech.glide.Glide;
import com.comscore.analytics.comScore;
import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.json.JSONParser;
import com.elconfidencial.eceleccionesgenerales2015.json.JSONParserObject;
import com.elconfidencial.eceleccionesgenerales2015.model.DatosEncuentas;
import com.elconfidencial.eceleccionesgenerales2015.model.Encuesta;
import com.elconfidencial.eceleccionesgenerales2015.model.GlobalMethod;
import com.elconfidencial.eceleccionesgenerales2015.model.Partido;
import com.elconfidencial.eceleccionesgenerales2015.model.PartidoEncuesta;
import com.elconfidencial.eceleccionesgenerales2015.model.QuoteServer;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.pushwoosh.BasePushMessageReceiver;
import com.pushwoosh.BaseRegistrationReceiver;
import com.pushwoosh.PushManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChooseActivity extends AppCompatActivity {

    private String apiKeyAmplitude = "47a789399b21bcd069f97c9d6afddd97";
    GlobalMethod globalMethod = new GlobalMethod(this);


    //Parámetros de CONFIG
    public static int DFP_CARD_EVERY_N;
    public static int LAST_NEWS_COUNTER;
    public static String RESULTS_WEBVIEW_URL;
    public static boolean SHOW_SURVEYS;
    public static boolean SHOW_TIMER;
    public static boolean SHOW_WIDGET_RESULTS;
    public static String PRESINDER_SHARE_MESSAGE_ANDROID;

    public static String config_url = "http://datos.elconfidencial.com/app-elecciones-generales-2015-survey/config.json";
    public static String encuestas_url = "http://datos.elconfidencial.com/app-elecciones-generales-2015-survey/survey.json";

    //Parámetros config
    private String TAG_DFP_CARD_EVERY_N = "DFP_CARD_EVERY_N";
    private String TAG_LAST_NEWS_COUNTER = "LAST_NEWS_COUNTER";
    private String TAG_RESULTS_WEBVIEW = "RESULTS_WEBVIEW_URL";
    private String TAG_SHOW_SURVEYS= "SHOW_SURVEYS";
    private String TAG_SHOW_TIMER = "SHOW_TIMER";
    private String TAG_SHOW_RESULTS = "SHOW_WIDGET_RESULTS";
    private String TAG_PRESINDER_SHARE_MESSAGE_ANDROID = "PRESINDER_SHARE_MESSAGE_ANDROID";

    //PartidosList
    public static List<Partido> partidosList = new ArrayList<>();

    public List<Partido> getPartidosList(){
        return partidosList;
    }

    //Encuestas
    public static ArrayList<Encuesta> encuestas = new ArrayList<>();

    //Quotes
    QuoteServer qs = QuoteServer.getInstance();
    Context context;
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        bindView();
        MultiDex.install(this);
        context = this;
        qs.init(this);

        // Enable Local Datastore.
        try {
            Parse.enableLocalDatastore(this);

            Parse.initialize(this, "fFMHyON2OrC3F161LgiepetpuB3WTktLvS6gq6ZH", "jqiMfz2BVxn4JNFhbsvscaEDg6QPObKn1JvGr0Wa");

            ParseAnalytics.trackAppOpenedInBackground(getIntent());
        }catch (Exception e){
            e.printStackTrace();
        }


        try{
            //Comscore
            comScore.setAppContext(this.getApplicationContext());
            comScore.setCustomerC2("elo_mtereisa");
            comScore.setPublisherSecret("c703dc81c1024d5172e35a58f86e2e9b");
            comScore.setAppName(getResources().getString(R.string.app_name));
        } catch (Exception e){
            Log.i("Comscore", "Error en Choose Activity en Comscore");
            e.printStackTrace();
        }



        //Register receivers for push notifications
        registerReceivers();



        //Create and start push manager
        PushManager pushManager = PushManager.getInstance(this);

        //Start push manager, this will count app open for Pushwoosh stats as well
        try {
            pushManager.onStartup(this);
        }
        catch(Exception e)
        {
            //push notifications are not available or AndroidManifest.xml is not configured properly
        }

        //Register for push!
        prefs = getApplicationContext().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        if (prefs.getString("wantPW","ON").equals("ON")){
            pushManager.registerForPushNotifications();
        }else{
            pushManager.unregisterForPushNotifications();
        }

        checkMessage(getIntent());


        if(globalMethod.haveNetworkConnection()) {
            //new LaunchActivityAsyntask().execute();
            new DownloadEncuestas().execute(encuestas_url);
        }



        //Inicializar Amplitude
        Log.i("20D_AMPLITUDE", "Inicializacion de Amplitude");
        Amplitude.getInstance().initialize(this, apiKeyAmplitude).enableForegroundTracking(getApplication());
    }

    /**PW**/
    //Registration receiver
    BroadcastReceiver mBroadcastReceiver = new BaseRegistrationReceiver()
    {
        @Override
        public void onRegisterActionReceive(Context context, Intent intent)
        {
            checkMessage(intent);
        }
    };

    private void bindView(){
        ImageView launchBackground = (ImageView) findViewById(R.id.loading_screen);
        ImageView launchLogo = (ImageView) findViewById(R.id.confi_logo);
        Glide.with(this).load(R.drawable.img_background_launch).into(launchBackground);
        Glide.with(this).load(R.drawable.img_icon_launch).into(launchLogo);
        Log.i("Prueba", "bind launch");
    }

    //Push message receiver
    private BroadcastReceiver mReceiver = new BasePushMessageReceiver()
    {
        @Override
        protected void onMessageReceive(Intent intent)
        {
            //JSON_DATA_KEY contains JSON payload of push notification.
            showMessage("push message is " + intent.getExtras().getString(JSON_DATA_KEY));
        }
    };

    //Registration of the receivers
    public void registerReceivers()
    {
        IntentFilter intentFilter = new IntentFilter(getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");

        registerReceiver(mReceiver, intentFilter, getPackageName() +".permission.C2D_MESSAGE", null);

        registerReceiver(mBroadcastReceiver, new IntentFilter(getPackageName() + "." + PushManager.REGISTER_BROAD_CAST_ACTION));
    }

    public void unregisterReceivers()
    {
        //Unregister receivers on pause
        try
        {
            unregisterReceiver(mReceiver);
        }
        catch (Exception e)
        {
            // pass.
        }

        try
        {
            unregisterReceiver(mBroadcastReceiver);
        }
        catch (Exception e)
        {
            //pass through
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //Re-register receivers on resume
        registerReceivers();

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
    public void onPause()
    {
        super.onPause();

        //Unregister receivers on pause
        unregisterReceivers();
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

    private void checkMessage(Intent intent)
    {
        if (null != intent)
        {
            if (intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
            {
                //showMessage("push message is " + intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT));
            }
            else if (intent.hasExtra(PushManager.REGISTER_EVENT))
            {
                //showMessage("register");
            }
            else if (intent.hasExtra(PushManager.UNREGISTER_EVENT))
            {
                //showMessage("unregister");
            }
            else if (intent.hasExtra(PushManager.REGISTER_ERROR_EVENT))
            {
                //showMessage("register error");
            }
            else if (intent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT))
            {
                //showMessage("unregister error");
            }

            resetIntentValues();
        }
    }

    /**
     * Will check main Activity intent and if it contains any PushWoosh data, will clear it
     */
    private void resetIntentValues()
    {
        Intent mainAppIntent = getIntent();

        if (mainAppIntent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.PUSH_RECEIVE_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.REGISTER_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.REGISTER_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.REGISTER_ERROR_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.REGISTER_ERROR_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_ERROR_EVENT);
        }

        setIntent(mainAppIntent);
    }

    private void showMessage(String message)
    {
        //Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);

        checkMessage(intent);
    }



    private class LaunchActivityAsyntask extends AsyncTask<String, String, JSONObject> {


        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParserObject jParser = new JSONParserObject();

            //Download quotes
           qs.getQuotesFromParseOrLocal();

            //Likes dislikes count
            //GlobalMethod.likesCount = GlobalMethod.getMyHashmap(getApplicationContext(),"likesCount");
            //GlobalMethod.dislikesCount = GlobalMethod.getMyHashmap(getApplicationContext(),"dislikesCount");

            //Create Partidos variables
            // Getting JSON from asset
            String jsonAsset = loadJSONFromAsset("PARTIDOS_TAGS.json");
            if(jsonAsset!=null){
                Log.i("PartidosJSON", "JSON recuperado de assets");
                if(partidosList.isEmpty()) {
                    setPartidosListFromJSON(jsonAsset);
                }
            } else{
                Log.i("PartidosJSON", "JSON no recuperado de assets");
            }

            // Getting JSON from URL
            if(globalMethod.haveNetworkConnection()){
                JSONObject json = jParser.getJSONFromUrl(config_url);
                return json;
            }
            return null;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if(json != null && globalMethod.haveNetworkConnection()) {
                    // Getting JSON Array
                    DFP_CARD_EVERY_N = json.getInt(TAG_DFP_CARD_EVERY_N);
                    LAST_NEWS_COUNTER = json.getInt(TAG_LAST_NEWS_COUNTER);
                    RESULTS_WEBVIEW_URL = json.getString(TAG_RESULTS_WEBVIEW);
                    SHOW_SURVEYS = json.getBoolean(TAG_SHOW_SURVEYS);
                    SHOW_TIMER = json.getBoolean(TAG_SHOW_TIMER);
                    SHOW_WIDGET_RESULTS = json.getBoolean(TAG_SHOW_RESULTS);
                    PRESINDER_SHARE_MESSAGE_ANDROID = json.getString(TAG_PRESINDER_SHARE_MESSAGE_ANDROID);
                }

            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }

            //Comprueba si es la primera vez
            boolean firstTime = prefs.getBoolean("firstTime", true); //Si no existe, devuelve el segundo parametro

            if(firstTime){
                Intent intent = new Intent(context, OnBoardingActivity.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();
            }


        }
    }

    private class DownloadEncuestas extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONArray doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONArray json = jParser.getJSONFromUrl(encuestas_url);

            return json;
        }
        @Override
        protected void onPostExecute(JSONArray json) {
            encuestas.clear();

            ArrayList<String> titulos = new ArrayList<>();
            ArrayList<String> fechas = new ArrayList<>();
            ArrayList<String> descripciones = new ArrayList<>();

            ArrayList<DatosEncuentas> datosEncuestas = new ArrayList<>();

            if(json!=null) {
                for (int i = 0; i < json.length(); i++) {

                    try {
                        ArrayList<PartidoEncuesta> partidoEncuestas = new ArrayList<>();
                        JSONObject encuestaGlobal = json.getJSONObject(i);
                        titulos.add(encuestaGlobal.getString("Source"));
                        fechas.add(encuestaGlobal.getString("Date"));
                        descripciones.add(encuestaGlobal.getString("Description"));
                        JSONArray datos = encuestaGlobal.getJSONArray("Data");
                        for (int j = 0; j < datos.length(); j++) {
                            JSONObject duplaPartido = datos.getJSONObject(j);
                            JSONArray partido = duplaPartido.names();
                            String nombre = "";
                            double porcentaje = 0;
                            for (int k = 0; k < partido.length(); k++) {
                                nombre = partido.getString(k);
                                //Log.d("Encuestas", nombre);
                                porcentaje = duplaPartido.getDouble(nombre);
                                //Log.d("Encuestas", ""+porcentaje);


                            }
                            PartidoEncuesta partidoEncuesta = new PartidoEncuesta(nombre, porcentaje);
                            partidoEncuestas.add(partidoEncuesta);

                        }
                        Collections.sort(partidoEncuestas, new Comparator<PartidoEncuesta>() {
                            public int compare(PartidoEncuesta partido1, PartidoEncuesta partido2) {
                                return Double.compare(partido2.getPorcentaje(), partido1.getPorcentaje());
                            }
                        });
                        datosEncuestas.add(new DatosEncuentas(partidoEncuestas));


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Encuesta e = new Encuesta(titulos.get(i), datosEncuestas.get(i).getDatosEncuesta());
                    e.setFecha(fechas.get(i));
                    e.setDescripcion(descripciones.get(i));

                    encuestas.add(e);
                }

                for (int n = 0; n < encuestas.size(); n++) {
                    Log.d("Encuestas", "El contenido de encuestas es: ");
                    Log.d("Encuestas", encuestas.get(n).getName());

                    for (int m = 0; m < encuestas.get(n).getPartidosEncuesta().size(); m++) {
                        Log.d("Encuestas", "Con el partido " + encuestas.get(n).getPartidosEncuesta().get(m).getName() + " y porcentaje " + encuestas.get(n).getPartidosEncuesta().get(m).getPorcentaje());
                    }
                }
                new LaunchActivityAsyntask().execute();

            }
        }
    }

    // region JSON Partidos local
    //----------------------------------------------------------------------

    //Almacena los objetos Partidos dentro de la Lista Global partidosList a partir de un json
    public void setPartidosListFromJSON(String json){
        try {
            JSONArray jArray = new JSONArray(json);
            for (int i = 0; i < jArray.length(); i++) {
                try {
                    JSONObject partidoObject = jArray.getJSONObject(i);
                    // Creamos un objeto Partido, donde almacenaremos todos sus atributos
                    Partido partido = new Partido();
                    partido.setId(partidoObject.getString("ID"));
                    partido.setNombre(partidoObject.getString("Desc"));
                    partido.setColor(partidoObject.getString("Color"));
                    partido.setSiglas(partidoObject.getString("Short"));
                    partido.setTagNoticias(partidoObject.getString("News_TAG"));
                    partido.setTagPush(partidoObject.getString("Push_TAG"));
                    partidosList.add(partido);
                    Log.i("PartidosJSON", "Almacenado el partido " + partido.getId());
                    Log.i("PartidosJSON", "Con siglas " + partido.getSiglas());
                } catch (JSONException e) {
                    Log.i("PartidosJSON", "Error lectura de JSON");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    //Método que lee un fichero json almacenado en assets
    public String loadJSONFromAsset(String nameFile) {
        String json = null;
        try {

            InputStream is = getAssets().open(nameFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    //----------------------------------------------------------------------
    //endregion
}
