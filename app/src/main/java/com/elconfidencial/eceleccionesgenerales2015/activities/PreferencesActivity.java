package com.elconfidencial.eceleccionesgenerales2015.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.comscore.analytics.comScore;
import com.pushwoosh.PushManager;
import com.pushwoosh.SendPushTagsCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.adapters.MyArrayAdapter;
import com.elconfidencial.eceleccionesgenerales2015.model.Municipio;

/**
 * Created by Afll on 11/11/2015.
 */
public class PreferencesActivity extends ActionBarActivity {
    public final String PWTAG = "MUNICIPIOS_TAGS";

    Toolbar toolbar;
    ActionBar actionBar;

    TextView generalesText,comunidadText,provinciaText,municipiosText, localityName;
    TextView ccaaNombre, provinciaNombre, municipioNombre,editarText,editarDescr, versionText;

    SwitchCompat generalesSwitch,comunidadSwitch,provinciaSwitch,municipioSwitch;

    //Partidos
    TextView ppText,psoeText,csText,podemosText,iuText,upydText;
    SwitchCompat ppSwitch,psoeSwitch,csSwitch,podemosSwitch,iuSwitch,upydSwitch;

    ImageView positionIcon;

    TextView editButton;
    Button creditosButton;

    SharedPreferences prefs;
    public int realTag;
    public String pushwooshTag;
    private PushManager pushManager ;


    private List<String> municipiosAutoComplete = new ArrayList<>();
    private List<Municipio> municipiosList = new ArrayList<>();

    Context context;
    Municipio municipioObj;
    //Valores locales nunca cambian. Seleccionados por el usuario en la aplicación.
    String comunidad,provincia,municipio = "";
    //Cambian dependiendo de los switches (default values).
    String comunidadPW = "00";
    String provinciaPW = "00";
    String municipioPW = "0000";

    List<String> partidosPW = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        context = this;

        //No abrir automaticsmente el teclado
       // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        pushManager = PushManager.getInstance(this);

        bindViews();
        setTypefaces();

        setupToolbar();

        //setListenerCreditos();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SearchLocalityActivity.class);
                startActivity(intent);
            }
        });

        ccaaNombre.setText(prefs.getString("CCAAName", "")); //Si no existe, devuelve el segundo parametro
        provinciaNombre.setText(prefs.getString("ProvinciaName", "")); //Si no existe, devuelve el segundo parametro
        municipioNombre.setText(prefs.getString("MunicipioName", "")); //Si no existe, devuelve el segundo parametro
        localityName.setText(prefs.getString("MunicipioName", "Ninguno")); //Si no existe, devuelve el segundo parametro

    }

    public void bindViews(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);

         positionIcon= (ImageView) findViewById(R.id.positionIcon);
        Glide.with(context).load(R.drawable.ic_location_settings).into(positionIcon);

        //creditosButton = (Button) findViewById(R.id.acercaDeButton);

        //TextViews
        localityName = (TextView) findViewById(R.id.locality_name_text);
        generalesText = (TextView) findViewById(R.id.generalesText);
        comunidadText = (TextView) findViewById(R.id.comunidadText);
        provinciaText = (TextView) findViewById(R.id.provinciaText);
        municipiosText = (TextView) findViewById(R.id.municipioText);
        ccaaNombre = (TextView) findViewById(R.id.ccaaNombre);
        provinciaNombre = (TextView) findViewById(R.id.provinciaNombre);
        municipioNombre = (TextView) findViewById(R.id.municipioNombre);
        ppText = (TextView) findViewById(R.id.ppText);
        psoeText = (TextView) findViewById(R.id.psoeText);
        csText = (TextView) findViewById(R.id.csText);
        podemosText = (TextView) findViewById(R.id.podemosText);
        iuText = (TextView) findViewById(R.id.iuText);
        upydText = (TextView) findViewById(R.id.upydText);
        //acercaDe = (TextView) findViewById(R.id.acercaDe);


        //Buttons
        editButton = (TextView) findViewById(R.id.edit_locality_button);

        //Switches
        generalesSwitch = (SwitchCompat) findViewById(R.id.generalesSwitch);
        comunidadSwitch = (SwitchCompat) findViewById(R.id.comunidadSwitch);
        provinciaSwitch = (SwitchCompat) findViewById(R.id.provinciaSwitch);
        municipioSwitch = (SwitchCompat) findViewById(R.id.municipioSwitch);

        //Partidos Switch
        ppSwitch = (SwitchCompat) findViewById(R.id.ppSwitch);
        psoeSwitch = (SwitchCompat) findViewById(R.id.psoeSwitch);
        csSwitch = (SwitchCompat) findViewById(R.id.csSwitch);
        podemosSwitch = (SwitchCompat) findViewById(R.id.podemosSwitch);
        iuSwitch = (SwitchCompat) findViewById(R.id.iuSwitch);
        upydSwitch = (SwitchCompat) findViewById(R.id.upydSwitch);

        //Version
        versionText = (TextView) findViewById(R.id.version);
    }

    public void setListenerCreditos(){
        creditosButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                new MaterialDialog.Builder(v.getContext())
                        .title(R.string.titulo_creditos)
                        .content(R.string.contenido_creditos)
                        .positiveText(R.string.atras_creditos)
                        .negativeText(R.string.contacto)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                try {
                                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                    sendIntent.setType("plain/text");
                                    sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                                    sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"moonfishteam@gmail.com", "laboratorio@elconfidencial.com"});
                                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Contacto Elecciones Generales 20-D");

                                    startActivity(sendIntent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .typeface(Typeface.createFromAsset(getApplicationContext().getAssets(),
                                "Titillium-Regular.otf"), Typeface.createFromAsset(getApplicationContext().getAssets(),
                                "Titillium-Light.otf"))
                        .show();

            }
        });
    }

    public void setVersion(){
        PackageInfo pInfo = null;
        try {
            pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String versionString = pInfo.versionName;
        versionText.setText("Versión de la aplicación: " + versionString);

        getUserTagValues();
        initializeSpinnersFromLocal();
    }

    public void setSwitchListeners(){

        generalesSwitch.setOnCheckedChangeListener(new GeneralesSwitchListener());
        comunidadSwitch.setOnCheckedChangeListener(new ComunidadSwitchListener());
        provinciaSwitch.setOnCheckedChangeListener(new ProvinciaSwitchListener());
        municipioSwitch.setOnCheckedChangeListener(new MunicipioSwitchListener());
        ppSwitch.setOnCheckedChangeListener(new PartidoSwitchListener());
        psoeSwitch.setOnCheckedChangeListener(new PartidoSwitchListener());
        csSwitch.setOnCheckedChangeListener(new PartidoSwitchListener());
        podemosSwitch.setOnCheckedChangeListener(new PartidoSwitchListener());
        iuSwitch.setOnCheckedChangeListener(new PartidoSwitchListener());
        upydSwitch.setOnCheckedChangeListener(new PartidoSwitchListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**try{
            //comScore
            Log.i("Comscore", "Dentro de on Resume");
            comScore.onEnterForeground();
        }catch (Exception e){
            Log.i("Comscore", "Error Comscore");
            e.printStackTrace();
        }**/
    }

    @Override
    protected void onPause() {
        super.onPause();
       /** try{
            //comScore
            Log.i("Comscore", "Dentro de on Pause");
            comScore.onExitForeground();
        }catch (Exception e){
            Log.i("Comscore", "Error Comscore");
            e.printStackTrace();
        }**/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
        try{
            //comScore
            Log.i("Comscore", "Se sale de la app con onDestroy");
            comScore.onExitForeground();
        }catch (Exception e){
            Log.i("Comscore", "Error Comscore");
            e.printStackTrace();
        }**/
    }


    // region Asyntask
    //----------------------------------------------------------------------

    private class JSONParse extends AsyncTask<String, String, JSONObject> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /**  uid = (TextView)findViewById(R.id.uid);
             name1 = (TextView)findViewById(R.id.name);
             email1 = (TextView)findViewById(R.id.email);
             pDialog = new ProgressDialog(MainActivity.this);
             pDialog.setMessage("Getting Data ...");
             pDialog.setIndeterminate(false);
             pDialog.setCancelable(true);
             pDialog.show();**/

        }

        @Override
        protected JSONObject doInBackground(String... args) {

            // Getting JSON from asset
            JSONObject json = null;
            try {
                json = new JSONObject(loadJSONFromAsset("ccaa_provincia_municipio_ID.json"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (json != null) {
                Log.i("Municipios", "JSON recuperado de assets");
            } else {
                Log.i("Municipios", "JSON no recuperado de assets");
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            getListMunicipios(json);


        }

    }

    //----------------------------------------------------------------------
    //endregion

    // region JsonParser
    //----------------------------------------------------------------------

    /**
     * En este método por un lado rellenaremos una lista que va a permitir al AutoComplete Text dar las opciones a mostrar
     * Pero también vamos a crear una lista de objetos municipios para poder acceder despueés a sus propiedades
     * @param json
     */

    private void getListMunicipios (JSONObject json){

        int tag;

        String provinciaNameAutoComplete;
        String municipioNameAutoComplete;

        Iterator iterIdCCAA = json.keys();
        while(iterIdCCAA.hasNext()){
            //Guardamos la ID en la variable tag. Ej: ID = 8 --> tag = 8.000.000
            String idCCAA = (String)iterIdCCAA.next();
            tag = Integer.parseInt(idCCAA) * 1000000;
            JSONObject jsonCCAA = null;
            try {
                jsonCCAA = json.getJSONObject(idCCAA);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Extraemos el jsonObject del campo "Provincia"
            try {
                assert jsonCCAA != null;
                JSONObject jsonIDProvincia = jsonCCAA.getJSONObject("Provincias");
                String CCAAname = jsonCCAA.getString("Name");
                Log.i("Municipios", "Entramos en  " + CCAAname);

                Iterator iterIdProvincia = jsonIDProvincia.keys();
                while(iterIdProvincia.hasNext()){

                    //Guardamos la ID en la variable tag. Ej: ID = 11 --> tag = 110.000
                    String idProvincia = (String)iterIdProvincia.next();
                    tag += Integer.parseInt(idProvincia) * 10000;
                    JSONObject jsonProvincia = null;
                    try {
                        jsonProvincia = jsonIDProvincia.getJSONObject(idProvincia);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Extraemos el jsonObject del campo "Municipios"
                    try {
                        assert jsonProvincia != null;
                        JSONObject jsonIDMunicipio = jsonProvincia.getJSONObject("Municipios");
                        provinciaNameAutoComplete = jsonProvincia.getString("Name");

                        Iterator iterIdMunicipio = jsonIDMunicipio.keys();
                        while(iterIdMunicipio.hasNext()){
                            //Guardamos la ID en la variable tag. Ej: ID = 1024 --> tag = 1.0024
                            String idMunicipio = (String)iterIdMunicipio.next();
                            tag += Integer.parseInt(idMunicipio);
                            JSONObject jsonMunicipio = null;
                            try {
                                jsonMunicipio = jsonIDMunicipio.getJSONObject(idMunicipio);

                                //Guardamos el nombre del municipio en la lista del autoComplete
                                municipioNameAutoComplete = jsonMunicipio.getString("Name") + ", " + provinciaNameAutoComplete;
                                municipiosAutoComplete.add(municipioNameAutoComplete);

                                //Creamos un objeto municipio y lo rellenamos. Lo añadimos a la lista de municipios
                                Municipio municipioObj = new Municipio();
                                municipioObj.setMunicipioAutoCompleteText(municipioNameAutoComplete);
                                municipioObj.setTag(tag);
                                municipioObj.setCcaaaName(CCAAname);
                                municipioObj.setCcaaTag(Integer.parseInt(idCCAA));
                                municipioObj.setProvinciaName(jsonProvincia.getString("Name"));
                                municipioObj.setProvinciaTag(Integer.parseInt(idProvincia));
                                municipioObj.setMunicipioName(jsonMunicipio.getString("Name"));
                                municipioObj.setMunicipioTag(Integer.parseInt(idMunicipio));
                                municipiosList.add(municipioObj);

                                //Reseteamos los valores para la próxima iteración
                                municipioNameAutoComplete = "";
                                tag -= Integer.parseInt(idMunicipio);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Reseteamos la variable tag para la siguiente iteración
                    tag -= Integer.parseInt(idProvincia);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Reseteamos la variable tag para la siguiente iteración
            tag -= Integer.parseInt(idCCAA);

        }

    }

    //----------------------------------------------------------------------
    //endregion

    // region Toolbar
    //----------------------------------------------------------------------

    private void setupToolbar() {
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(null);
            //actionBar.setHomeAsUpIndicator(R.drawable.elconfidencial_32dp_white);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //----------------------------------------------------------------------
    //endregion

    // region Pushwoosh
    //----------------------------------------------------------------------

    /*********CCAA, PROVINCIA, MUNICIPIO***********/
    public void getUserTagValues(){
        realTag = prefs.getInt("realTag", 00000000);
        String tag = String.valueOf(realTag);
        //Add 0 from comunidad
        if (tag.length()==7){
            tag = "0"+tag;
        }
        comunidad = tag.substring(0,2);
        provincia = tag.substring(2,4);
        municipio = tag.substring(4,8);
    }

    public void initializeSpinnersFromLocal(){
        //General
        String wantPW = prefs.getString("wantPW","ON");
        if(wantPW.equals("ON")){generalesSwitch.setChecked(true);}
        if(wantPW.equals("OFF")){generalesSwitch.setChecked(false);}
        //CCAA,Prov,Munic
        pushwooshTag = prefs.getString("pushwooshTag", "00000000");
        String tag = pushwooshTag;
        //Add 0 from comunidad
        if (tag.length()==7){
            tag = "0"+tag;
        }
        comunidadPW = tag.substring(0,2);
        provinciaPW = tag.substring(2,4);
        municipioPW = tag.substring(4,8);

        if(!comunidadPW.equals("00")){//Active
            comunidadSwitch.setChecked(true);
            Log.i(PWTAG,"Comunidad: "+ comunidadPW);
        }
        if(!provinciaPW.equals("00")){//Active
            provinciaSwitch.setChecked(true);
            Log.i(PWTAG, "Provincia: " + provinciaPW);
        }
        if(!municipioPW.equals("0000")){//Active
            municipioSwitch.setChecked(true);
            Log.i(PWTAG, "Municipio: " + municipioPW);
        }

        //Partidos tags init
        partidosPW = new ArrayList<>(prefs.getStringSet("partidosPW", new HashSet<String>()));

        //Recorremos la lista para ver que tags estan activos y marcamos el switch
        for(int i = 0;i<partidosPW.size();i++){
            switch (partidosPW.get(i)) {
                case "PP":
                    ppSwitch.setChecked(true);break;
                case "PSOE":
                    psoeSwitch.setChecked(true);break;
                case "CIUDADANOS":
                    csSwitch.setChecked(true);break;
                case "PODEMOS":
                    podemosSwitch.setChecked(true);break;
                case "IU":
                    iuSwitch.setChecked(true);break;
                case "UPYD":
                    upydSwitch.setChecked(true);break;
            }
        }
    }

    public void refreshComunidadTag(boolean isOn){
        String tag = "";
        if(isOn){
            comunidadPW = comunidad;
        }else{
            comunidadPW = "00";
        }
        tag = comunidadPW + provinciaPW + municipioPW;
        saveTagInLocal(tag);
        sendTagsToPushWoosh(Integer.parseInt(tag));
    }

    public void refreshProvinciaTag(boolean isOn){
        String tag = "";
        if(isOn){
            provinciaPW = provincia;
        }else{
            provinciaPW = "00";
        }
        tag = comunidadPW + provinciaPW + municipioPW;
        saveTagInLocal(tag);
        sendTagsToPushWoosh(Integer.parseInt(tag));
    }

    public void refreshMunicipioTag(boolean isOn){
        String tag = "";
        if(isOn){
            municipioPW = municipio;
        }else{
            municipioPW = "0000";
        }
        tag = comunidadPW + provinciaPW + municipioPW;
        saveTagInLocal(tag);
        sendTagsToPushWoosh(Integer.parseInt(tag));
    }

    //Método que extrae el objeto Municipio a partir del nombre del auto complete de un municipio
    //Devuelve 0 si no encuentra el municipio en la lista
    public Municipio getMunicipioObject(String municipio){
        for(int i=0;i<municipiosAutoComplete.size();i++) {
            if(municipiosAutoComplete.get(i).equals(municipio)){
                return municipiosList.get(i);
            }
        }
        return null;
    }

    public void refreshValuesFromMunicipio(Municipio municipioNuevo){

        //refresh TextViews
        ccaaNombre.setText(municipioNuevo.getCcaaaName());
        provinciaNombre.setText(municipioNuevo.getProvinciaName());
        municipioNombre.setText(municipioNuevo.getMunicipioName());

        //refresh variables
        comunidad = municipioNuevo.getCcaaaName();
        provincia = municipioNuevo.getProvinciaName();
        municipio = municipioNuevo.getMunicipioName();
    }

    public void saveTagInLocal(String tag){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("pushwooshTag", tag);
        editor.commit();
        Log.i(PWTAG, "PW tag saved in local: " + tag);
    }

    /*** Envia un numero al tasg de PW *****/
    public void sendTagsToPushWoosh(final int number){
        Map<String,Object> tags = new HashMap<>();
        tags.put(PWTAG, number);
        pushManager.sendTags(getApplicationContext(), tags, new SendPushTagsCallBack() {
            @Override
            public void taskStarted() {
                //Task Start
                Log.i("PushWoosh: ", "Sending tags to PW...  " + String.valueOf(number));
            }

            @Override
            public void onSentTagsSuccess(Map<String, String> map) {
                //Task end
                Log.i("PushWoosh: ", "Sent Success");
            }

            @Override
            public void onSentTagsError(Exception e) {
                Log.i("PushWoosh: ", "Sent Error");
            }
        });
    }

    //Partidos

    public void refreshPartidoTag(boolean isChecked,String tagChecked){
        if(isChecked){
            if(!partidosPW.contains(tagChecked)){
                partidosPW.add(tagChecked);
            }
        }else{
            partidosPW.remove(tagChecked);
        }
        savePartidosInLocal();
        sendPartidosToPushWoosh();
    }

    public void savePartidosInLocal(){
        Log.i("Pref Partidos", "Local saved partidos tags...");
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("partidosPW", new HashSet<String>(partidosPW));
        editor.commit();
    }

    //Comunicacion con PushWoosh SEND
    private void sendPartidosToPushWoosh(){
        Map<String,Object> tags = new HashMap<>();
        tags.put("PARTIDOS_TAGS", partidosPW.toArray());
        pushManager.sendTags(getApplicationContext(), tags, new SendPushTagsCallBack() {
            @Override
            public void taskStarted() {
                //Task Start
                Log.i("Pref Partidos", "Sending partidos to PW...");
            }

            @Override
            public void onSentTagsSuccess(Map<String, String> map) {
                //Task end
            }

            @Override
            public void onSentTagsError(Exception e) {

            }
        });
    }

    //----------------------------------------------------------------------
    //endregion

    // region Listeners
    //----------------------------------------------------------------------

    public class GeneralesSwitchListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){//Register for push
                pushManager.registerForPushNotifications();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("wantPW","ON");
                editor.commit();
            }else{//Unregister for push
                pushManager.unregisterForPushNotifications();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("wantPW","OFF");
                editor.commit();
            }

        }
    }
    public class ComunidadSwitchListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            refreshComunidadTag(isChecked);
        }
    }
    public class ProvinciaSwitchListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            refreshProvinciaTag(isChecked);
        }
    }
    public class MunicipioSwitchListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            refreshMunicipioTag(isChecked);
        }
    }
    public class PartidoSwitchListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String result = "";
            String idSwitch = getResources().getResourceEntryName(buttonView.getId());
            switch (idSwitch){
                case "ppSwitch":
                    refreshPartidoTag(isChecked,"PP");break;
                case "psoeSwitch":
                    refreshPartidoTag(isChecked,"PSOE");break;
                case "csSwitch":
                    refreshPartidoTag(isChecked,"CIUDADANOS");break;
                case "podemosSwitch":
                    refreshPartidoTag(isChecked,"PODEMOS");break;
                case "iuSwitch":
                    refreshPartidoTag(isChecked,"IU");break;
                case "upydSwitch":
                    refreshPartidoTag(isChecked,"UPYD");break;

            }
        }
    }

    //----------------------------------------------------------------------
    //endregion

    // region OptionsItemSelected
    //----------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        System.gc();
        super.onBackPressed();
    }

    //----------------------------------------------------------------------
    //endregion

    // region Typefaces
    //----------------------------------------------------------------------

    public void setTypefaces(){
        //Nombres
        ccaaNombre.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Regular.otf"));
        provinciaNombre.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Regular.otf"));
        municipioNombre.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Regular.otf"));
        //Municipio
        generalesText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Semibold.otf"));
        comunidadText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Semibold.otf"));
        provinciaText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Semibold.otf"));
        municipiosText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Semibold.otf"));
        //Partidos
        ppText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Semibold.otf"));
        psoeText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Semibold.otf"));
        csText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Semibold.otf"));
        podemosText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Semibold.otf"));
        iuText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Semibold.otf"));
        upydText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Semibold.otf"));
        //Version
        versionText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Regular.otf"));
    }

    //----------------------------------------------------------------------
    //endregion

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









}
