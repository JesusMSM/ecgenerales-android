package es.elconfidencial.eleccionesgenerales2015.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.parse.ParseObject;

import org.json.JSONArray;
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

public class OnBoardingActivity extends AppCompatActivity {

    private Button empezar;
    private AutoCompleteTextView searchMunicipio;

    private List<String> municipiosAutoComplete = new ArrayList<>();
    private List<String> municipiosList = new ArrayList<>();
    private List<Integer> tagsList = new ArrayList<>();



    //private String pushwooshTag; // Ej:08.10.0000 (No ha marcado que desea recibir notificación a nivel de municipio)
    public static int realTag; // Ej:08.101.003

    private List<String> municipios = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        searchMunicipio = (AutoCompleteTextView) findViewById(R.id.searchMunicipio);
        empezar = (Button) findViewById(R.id.empezar);


       /** if(municipiosAutoComplete.get(0)!=null) {
            Log.i("Municipios", "Primer municipio de la lista " + municipiosAutoComplete.get(0));
        }**/
        //Adapter del buscador de municipio
        MyArrayAdapter<String> adapter = new MyArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, municipiosAutoComplete);

        // Numero de caracteres necesarios para que se empiece
        // a mostrar la lista
        searchMunicipio.setThreshold(3);

        // Se establece el Adapter
        searchMunicipio.setAdapter(adapter);


        //Listener del boton empezar
        empezar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getTagFromMunicipio(searchMunicipio.getText().toString())!=0){
                    //Alamacenamos el TAG completo del municipio seleccionado
                    realTag = getTagFromMunicipio(searchMunicipio.getText().toString());
                    Log.i("Municipios", "" + searchMunicipio.getText().toString());
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    startActivity(intent);
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

                                    //Guardamos el nombre del municipio en la lista del autoComplete y en la lista de nombres de municipios
                                    municipioNameAutoComplete = jsonMunicipio.getString("Name") + ", " + provinciaNameAutoComplete;
                                    municipiosAutoComplete.add(municipioNameAutoComplete);
                                    municipiosList.add(jsonMunicipio.getString("Name"));
                                    //Guardamos el tag correspondiente a la CCAA-provincia-municipio de dicho municipio
                                    tagsList.add(tag);

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


    //Método que extrae el tag CCAA-provincia-municipio a partir del nombre de un municipio
    //Devuelve 0 si no encuentra el municipio en la lista
    public int getTagFromMunicipio(String municipio){
        for(int i=0;i<municipiosAutoComplete.size();i++) {
            if(municipiosAutoComplete.get(i).equals(municipio)){
                return tagsList.get(i);
            }
        }
        return 0;
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
