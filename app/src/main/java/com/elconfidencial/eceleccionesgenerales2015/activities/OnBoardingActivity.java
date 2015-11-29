package com.elconfidencial.eceleccionesgenerales2015.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.adapters.MyArrayAdapter;
import com.elconfidencial.eceleccionesgenerales2015.model.Municipio;

public class OnBoardingActivity extends AppCompatActivity {

    private Button empezar;
    private AutoCompleteTextView searchMunicipio;
    private TextView introduceMunicipio;
    private ImageView fondo;
    private LinearLayout linear;

    private List<String> municipiosAutoComplete = new ArrayList<>();
    private List<Municipio> municipiosList = new ArrayList<>();

    public Municipio municipioObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        fondo = (ImageView) findViewById(R.id.logoOnboarding);
        Glide.with(getApplicationContext()).load(R.drawable.logo_onboarding).into(fondo);
        linear = (LinearLayout) findViewById(R.id.linearOnboarding);
        linear.setBackgroundResource(R.drawable.background_onboarding);
        searchMunicipio = (AutoCompleteTextView) findViewById(R.id.searchMunicipio);
        searchMunicipio.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        searchMunicipio.setTypeface((Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Light.otf")));
        empezar = (Button) findViewById(R.id.empezar);
        empezar.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Regular.otf"));
        //empezar.setBackgroundResource(R.drawable.button_resultados);

        introduceMunicipio = (TextView) findViewById(R.id.introduceMunicipio);
        introduceMunicipio.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "Titillium-Light.otf"));

       /** if(municipiosAutoComplete.get(0)!=null) {
            Log.i("Municipios", "Primer municipio de la lista " + municipiosAutoComplete.get(0));
        }**/
        //Adapter del buscador de municipio
        MyArrayAdapter<String> adapter = new MyArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, municipiosAutoComplete);


        // Numero de caracteres necesarios para que se empiece
        // a mostrar la lista
        searchMunicipio.setThreshold(2);

        // Se establece el Adapter
        searchMunicipio.setAdapter(adapter);

        searchMunicipio.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(arg1.getApplicationWindowToken(), 0);

            }

        });


        //Listener del boton empezar
        empezar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getMunicipioObject(searchMunicipio.getText().toString())!=null){
                    //Alamacenamos el TAG completo del municipio seleccionado
                    municipioObj = getMunicipioObject(searchMunicipio.getText().toString());
                    Log.i("Municipios", "" + searchMunicipio.getText().toString());

                    //Guardamos en preferences
                    SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("realTag", municipioObj.getTag());
                    editor.putString("MunicipioName", municipioObj.getMunicipioName());
                    editor.putString("ProvinciaName", municipioObj.getProvinciaName());
                    editor.putString("CCAAName", municipioObj.getCcaaaName());
                    Log.i("Municipios", "Guardamos en Preferences el tag " + municipioObj.getTag());
                    Log.i("Municipios", "Guardamos en Preferences el municipio " + municipioObj.getMunicipioName());
                    Log.i("Municipios", "Guardamos en Preferences la provincia" + municipioObj.getProvinciaName());
                    Log.i("Municipios", "Guardamos en Preferences la CCAA " + municipioObj.getCcaaaName());
                    editor.apply();

                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.introduce_municipio), Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });


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
            if(json!=null){
                Log.i("Municipios", "JSON recuperado de assets");
            } else{
                Log.i("Municipios", "JSON no recuperado de assets");
            }
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {

            getListMunicipios(json);



        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_on_boarding, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
