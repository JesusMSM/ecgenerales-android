package com.elconfidencial.eceleccionesgenerales2015.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.bumptech.glide.Glide;
import com.comscore.analytics.comScore;
import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.adapters.MyArrayAdapter;
import com.elconfidencial.eceleccionesgenerales2015.model.Municipio;
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

/**
 * Created by jorge_cmata on 14/5/16.
 */
public class SearchLocalityActivity extends AppCompatActivity {

    public final String PWTAG = "MUNICIPIOS_TAGS";

    private AutoCompleteTextView searchMunicipio;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private PushManager pushManager ;

    private List<String> municipiosAutoComplete = new ArrayList<>();
    private List<Municipio> municipiosList = new ArrayList<>();

    public Municipio municipioObj;
    Context context;

    AsyncTask<String, String, JSONObject> task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_locality);
        context = this;

        searchMunicipio = (AutoCompleteTextView) findViewById(R.id.searchMunicipio);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        pushManager = PushManager.getInstance(this);

        setupToolbar();
        setupAutoCompleteTextview();

        searchMunicipio.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(arg1.getApplicationWindowToken(), 0);

            }

        });


    }



    @Override
    protected void onResume() {
        if(task==null){
            task = new JSONParse();
            task.execute();
        }
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
        if(task!=null){
            task.cancel(true);
        }
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

    /**Rellenaremos 3 List:
     *  - municipiosAutoComplete: Con Strings de cada uno de los municipios encontrados en el json con la forma "Municipio, Provincia"
     *  - municipiosList: Con Strings de cada uno de los municipios
     *  - realTagList: Con ints de cada uno de los ids de los municipios, en el mismo orden que en las otras dos listas
     *
     * @param json ; json con la lisa de CCAA-Provincias-Municipios y sus tags
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


    //Método que extrae el objeto Municipio a partir del nombre del auto complete de un municipio
    //Devuelve 0 si no encuentra el municipio en la lista
    public Municipio getMunicipioObject(String municipio){
        for(int i=0;i<municipiosAutoComplete.size();i++) {
            if(municipiosAutoComplete.get(i).equalsIgnoreCase(municipio)){
                return municipiosList.get(i);
            }
        }
        return null;
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


    // region AutoCompleteTextView
    //----------------------------------------------------------------------

    private void setupAutoCompleteTextview(){

        searchMunicipio.setHint("Introduce su municipio");
        searchMunicipio.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        searchMunicipio.setTypeface((Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Light.otf")));

        //Adapter del buscador de municipio
        MyArrayAdapter<String> adapter = new MyArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, municipiosAutoComplete);

        // Numero de caracteres necesarios para que se empiece a mostrar la lista
        searchMunicipio.setThreshold(1);

        // Se establece el Adapter
        searchMunicipio.setAdapter(adapter);
    }

    public void readAutoCompleteTextView(){
        if(getMunicipioObject(searchMunicipio.getText().toString())!=null){

            //Almacenamos el TAG completo del municipio seleccionado
            municipioObj = getMunicipioObject(searchMunicipio.getText().toString());
            Log.i("Municipios", "" + searchMunicipio.getText().toString());

            //Guardamos en preferences
            SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("realTag", municipioObj.getTag());
            editor.putString("MunicipioName", municipioObj.getMunicipioName());
            editor.putString("MunicipioAutoComplete", municipioObj.getMunicipioAutoCompleteText());
            editor.putString("ProvinciaName", municipioObj.getProvinciaName());
            editor.putString("CCAAName", municipioObj.getCcaaaName());

            //logs
            Log.i("Municipios", "Guardamos en Preferences el tag " + municipioObj.getTag());
            Log.i("Municipios", "Guardamos en Preferences el AutomCompleteText " + municipioObj.getMunicipioAutoCompleteText());
            Log.i("Municipios", "Guardamos en Preferences el municipio " + municipioObj.getMunicipioName());
            Log.i("Municipios", "Guardamos en Preferences la provincia" + municipioObj.getProvinciaName());
            Log.i("Municipios", "Guardamos en Preferences la CCAA " + municipioObj.getCcaaaName());

            editor.apply();

            //Amplitude
            JSONObject eventProperties = new JSONObject();
            try {
                eventProperties.put("page", "");
                eventProperties.put("location", municipioObj.getMunicipioName());
            } catch (JSONException exception) {
                exception.printStackTrace();
            }
            Amplitude.getInstance().logEvent("Search_location", eventProperties);

            //Update PushWoosh
            saveTagInLocal(String.valueOf(municipioObj.getTag()));
            sendTagsToPushWoosh(municipioObj.getTag());

            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Introduzca un municipio válido", Toast.LENGTH_LONG);
            toast.show();
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

    // region Options Menu
    //----------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_locality, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.next:
                readAutoCompleteTextView();
                break;
        }

        return true;
    }
    //----------------------------------------------------------------------
    //endregion

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
    public void saveTagInLocal(String tag){
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("pushwooshTag", tag);
        editor.commit();
        Log.i(PWTAG, "PW tag saved in local: " + tag);
    }
}