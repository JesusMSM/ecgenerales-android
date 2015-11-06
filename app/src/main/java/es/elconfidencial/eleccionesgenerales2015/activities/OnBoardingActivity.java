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

public class OnBoardingActivity extends AppCompatActivity {

    private Button empezar;
    private AutoCompleteTextView searchMunicipio;

    private List<String> municipiosAutoComplete = new ArrayList<>();
    private List<String> municipiosList = new ArrayList<>();


    private String CCAAname;
    private String CCAAid;
    private String provinciaName;
    private String provinciaId;
    private String municipioName;
    private String municipioId;


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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
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
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
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
                json = new JSONObject(loadJSONFromAsset());
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

        private void getListMunicipios (JSONObject json){

            String provinciaNameAutoComplete;
            String municipioNameAutoComplete;

            Iterator iterIdCCAA = json.keys();
            while(iterIdCCAA.hasNext()){
                String idCCAA = (String)iterIdCCAA.next();
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

                        String idProvincia = (String)iterIdProvincia.next();
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
                                String idMunicipio = (String)iterIdMunicipio.next();
                                JSONObject jsonMunicipio = null;
                                try {
                                    jsonMunicipio = jsonIDMunicipio.getJSONObject(idMunicipio);

                                    //Por último, extraemos el nombre del municipio y lo guardamos en la lista de municipios
                                    municipioNameAutoComplete = jsonMunicipio.getString("Name") + ", " + provinciaNameAutoComplete;
                                    municipiosAutoComplete.add(municipioNameAutoComplete);
                                    municipiosList.add(jsonMunicipio.getString("Name"));
                                    municipioNameAutoComplete = "";

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }



                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

    }


    //Método que lee un fichero json almacenado en assets
    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("ccaa_provincia_municipio_ID.json");

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
