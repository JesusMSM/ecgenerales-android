package es.elconfidencial.eleccionesgenerales2015.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.pushwoosh.PushManager;
import com.pushwoosh.SendPushTagsCallBack;

import java.util.HashMap;
import java.util.Map;

import es.elconfidencial.eleccionesgenerales2015.R;

/**
 * Created by Afll on 11/11/2015.
 */
public class PreferencesActivity extends ActionBarActivity {
    public final String PWTAG = "MUNICIPIOS_TAGS";

    TextView generalesText,comunidadText,provinciaText,municipiosText;
    Switch generalesSwitch,comunidadSwitch,provinciaSwitch,municipioSwitch;

    SharedPreferences prefs;
    public int realTag;
    public String pushwooshTag;
    private PushManager pushManager ;

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

        //TextViews
        generalesText = (TextView) findViewById(R.id.generalesText);
        comunidadText = (TextView) findViewById(R.id.comunidadText);
        provinciaText = (TextView) findViewById(R.id.provinciaText);
        municipiosText = (TextView) findViewById(R.id.municipioText);

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
    }
    public void setTypefaces(){

    }
    public void getUserTagValues(){
        realTag = prefs.getInt("realTag",00000000);
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
        pushwooshTag = prefs.getString("pushwooshTag","00000000");
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
}
