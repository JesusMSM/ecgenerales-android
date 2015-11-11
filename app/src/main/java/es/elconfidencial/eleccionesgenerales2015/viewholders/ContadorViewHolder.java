package es.elconfidencial.eleccionesgenerales2015.viewholders;

import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pushwoosh.thirdparty.radiusnetworks.ibeacon.IBeaconManager;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.elconfidencial.eleccionesgenerales2015.R;
import es.elconfidencial.eleccionesgenerales2015.activities.MainActivity;
import es.elconfidencial.eleccionesgenerales2015.views.DonutProgress;

/**
 * Created by Jesus on 03/11/2015.
 */
public class ContadorViewHolder extends RecyclerView.ViewHolder{

    public TextView contador,label,barra,d_20, textViewDias,textViewHoras,textViewMinutos,textViewFaltan,textViewColegiosElectorales;
    public DonutProgress donut_dias,donut_horas,donut_minutos;

    public ImageView preferences;

    public ContadorViewHolder(View v) {
        super(v);

        label = (TextView) v.findViewById(R.id.label);
        barra = (TextView) v.findViewById(R.id.barra);
        d_20 = (TextView) v.findViewById(R.id.d_20);
        textViewFaltan = (TextView) v.findViewById(R.id.textViewFaltan);
        textViewColegiosElectorales = (TextView) v.findViewById(R.id.textViewColegiosElectorales);
        donut_dias = (DonutProgress) v.findViewById(R.id.donut_dias);
        textViewDias = (TextView) v.findViewById(R.id.textViewDias);
        donut_horas = (DonutProgress) v.findViewById(R.id.donut_horas);
        textViewHoras = (TextView) v.findViewById(R.id.textViewHoras);
        donut_minutos = (DonutProgress) v.findViewById(R.id.donut_minutos);
        textViewMinutos = (TextView) v.findViewById(R.id.textViewMinutos);
        preferences = (ImageView) v.findViewById(R.id.preferencesIcon);

        donut_dias.setStartingDegree(270);
        donut_dias.setTextColor(v.getResources().getColor(R.color.white));
        donut_dias.setTextSize(50f);
        donut_dias.setMax(50);


        donut_horas.setStartingDegree(270);
        donut_horas.setTextColor(v.getResources().getColor(R.color.white));
        donut_horas.setTextSize(50f);
        donut_horas.setMax(24);

        donut_minutos.setStartingDegree(270);
        donut_minutos.setTextColor(v.getResources().getColor(R.color.white));
        donut_minutos.setTextSize(50f);
        donut_minutos.setMax(60);

        textViewDias.setTypeface(Typeface.createFromAsset(v.getContext().getAssets(), "Titillium-Light.otf"));
        textViewHoras.setTypeface(Typeface.createFromAsset(v.getContext().getAssets(), "Titillium-Light.otf"));
        textViewMinutos.setTypeface(Typeface.createFromAsset(v.getContext().getAssets(), "Titillium-Light.otf"));
        textViewFaltan.setTypeface(Typeface.createFromAsset(v.getContext().getAssets(), "Titillium-Regular.otf"));
        textViewColegiosElectorales.setTypeface(Typeface.createFromAsset(v.getContext().getAssets(), "Titillium-Regular.otf"));
    }

    public void showContador(){

        long tiempoRestante = 0;

        //Calculamos el tiempo (milisegundos) que quedan para las elecciones catalanas
        try {
            long today = new Date().getTime();
            String fechaEleccionesCatalanas = "20/12/2015";
            Date elecciones = new SimpleDateFormat("dd/MM/yyyy").parse(fechaEleccionesCatalanas);

            tiempoRestante = elecciones.getTime()- today;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Iniciamos una cuenta atras, empezando por los milisegundos que quedan (tiempoRestante) hasta alcanzar 1 segundo.
        //Mostramos los milisegundos en formato: D dias H h M mins S segs.
        new CountDownTimer(tiempoRestante, 1000) {

            StringBuilder time = new StringBuilder();

            public void onTick(long millisUntilFinished) {
                //tiempo.setText("seconds remaining: " + millisUntilFinished / 1000);
                long days = (millisUntilFinished / (1000 * 60 * 60 * 24)); //for counting days
                long hours = (millisUntilFinished - days*(1000*60*60*24)) / (1000 * 60 * 60); //for counting hours
                long minutes = (millisUntilFinished - days*(1000*60*60*24) - hours*(1000*60*60))/ (1000 * 60); //for counting minutes
                long seconds = (millisUntilFinished - days*(1000*60*60*24) - hours*(1000*60*60) - minutes*(1000*60)) / (1000); //for counting seconds

                donut_dias.setText(""+days);
                donut_dias.setProgress((int) days);
                donut_horas.setText("" + hours);
                donut_horas.setProgress((int) hours);
                donut_minutos.setText("" + minutes);
                donut_minutos.setProgress((int) minutes);
//                contador.setText( days + " " + MainActivity.resources.getString(R.string.dias) + "  " + hours + " h  \n"+ minutes +" mins  " + seconds + " segs ");
            }

            public void onFinish() {
                contador.setText("");//Texto al llegar a 0;
            }
        }.start();
    }

}
