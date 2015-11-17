package es.elconfidencial.eleccionesgenerales2015.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pushwoosh.PushManager;
import com.pushwoosh.SendPushTagsCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.elconfidencial.eleccionesgenerales2015.R;
import es.elconfidencial.eleccionesgenerales2015.adapters.MyArrayAdapter;
import es.elconfidencial.eleccionesgenerales2015.model.Municipio;

/**
 * Created by Afll on 11/11/2015.
 */
public class PreferencesActivity extends ActionBarActivity {
    public final String PWTAG = "MUNICIPIOS_TAGS";

    TextView generalesText,comunidadText,provinciaText,municipiosText;
    TextView ccaaNombre, provinciaNombre, municipioNombre;
    AutoCompleteTextView searchMunicipio;
    Switch generalesSwitch,comunidadSwitch,provinciaSwitch,municipioSwitch;
    Button reestablecer;

    SharedPreferences prefs;
    public int realTag;
    public String pushwooshTag;
    private PushManager pushManager ;


    private List<String> municipiosAutoComplete = new ArrayList<>();
    private List<Municipio> municipiosList = new ArrayList<>();

    Municipio municipioObj;
    //Valores locales nunca cambian. Seleccionados por el usuario en la aplicación.
    String comunidad,provincia,municipio = "";
    //Cambian dependiendo de los switches (default values).
    String comunidadPW = "00";
    String provinciaPW = "00";
    String municipioPW = "0000";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        //ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled( true );
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.custom_title_noticias, null);
        ((TextView)v.findViewById(R.id.actionBarTitle)).setText("PREFERENCIAS");
        ((TextView)v.findViewById(R.id.actionBarTitle)).setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Light.otf"));
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        pushManager = PushManager.getInstance(this);

        //AutoCompleteTextView
        searchMunicipio = (AutoCompleteTextView) findViewById(R.id.searchMunicipioPref);

        //Button
        reestablecer = (Button) findViewById(R.id.reestablecerPref);

        //Adapter del buscador de municipio
        MyArrayAdapter<String> adapter = new MyArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, municipiosAutoComplete);

        // Numero de caracteres necesarios para que se empiece
        // a mostrar la lista
        searchMunicipio.setThreshold(3);

        // Se establece el Adapter
        searchMunicipio.setAdapter(adapter);

        searchMunicipio.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(arg1.getApplicationWindowToken(), 0);

            }

        });

        //TextViews
        generalesText = (TextView) findViewById(R.id.generalesText);
        comunidadText = (TextView) findViewById(R.id.comunidadText);
        provinciaText = (TextView) findViewById(R.id.provinciaText);
        municipiosText = (TextView) findViewById(R.id.municipioText);
        ccaaNombre = (TextView) findViewById(R.id.ccaaNombre);
        provinciaNombre = (TextView) findViewById(R.id.provinciaNombre);
        municipioNombre = (TextView) findViewById(R.id.municipioNombre);

        ccaaNombre.setText(prefs.getString("CCAAName", "")); //Si no existe, devuelve el segundo parametro
        provinciaNombre.setText(prefs.getString("ProvinciaName", "")); //Si no existe, devuelve el segundo parametro
        municipioNombre.setText(prefs.getString("MunicipioName", "")); //Si no existe, devuelve el segundo parametro

        //Button
        reestablecer.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getMunicipioObject(searchMunicipio.getText().toString())!=null){

                    municipioObj = getMunicipioObject(searchMunicipio.getText().toString());
                    Log.i("Municipios", "" + searchMunicipio.getText().toString());

                    //Guardamos en preferences
                    SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("realTag", municipioObj.getTag());
                    editor.putString("MunicipioName", municipioObj.getMunicipioName());
                    editor.putString("ProvinciaName", municipioObj.getProvinciaName());
                    editor.putString("CCAAName", municipioObj.getCcaaaName());
                    editor.apply();

                    refreshValuesFromMunicipio(municipioObj);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.introduce_municipio), Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });


        //Switches
        generalesSwitch = (Switch) findViewById(R.id.generalesSwitch);
        comunidadSwitch = (Switch) findViewById(R.id.comunidadSwitch);
        provinciaSwitch = (Switch) findViewById(R.id.provinciaSwitch);
        municipioSwitch = (Switch) findViewById(R.id.municipioSwitch);

        //Set typefaces
        setTypefaces();
        //Set listeners
        generalesSwitch.setOnCheckedChangeListener(new GeneralesSwitchListener());
        comunidadSwitch.setOnCheckedChangeListener(new ComunidadSwitchListener());
        provinciaSwitch.setOnCheckedChangeListener(new ProvinciaSwitchListener());
        municipioSwitch.setOnCheckedChangeListener(new MunicipioSwitchListener());

        getUserTagValues();
        initializeSpinnersFromLocal();

        new JSONParse().execute();
    }

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
    }






    public void setTypefaces(){

    }
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


    public class GeneralesSwitchListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            //TODO
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
        finish();
        super.onBackPressed();
    }
}
