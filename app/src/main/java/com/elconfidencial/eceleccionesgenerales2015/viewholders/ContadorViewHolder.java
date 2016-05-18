package com.elconfidencial.eceleccionesgenerales2015.viewholders;

import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.views.DonutProgress;

/**
 * Created by Jesus on 03/11/2015.
 */
public class ContadorViewHolder extends RecyclerView.ViewHolder{

    public TextView contador, textViewDias,textViewHoras,textViewMinutos,textViewColegiosElectorales;


    public ContadorViewHolder(View v) {
        super(v);

        textViewColegiosElectorales = (TextView) v.findViewById(R.id.textViewColegiosElectorales);
        textViewDias = (TextView) v.findViewById(R.id.textViewDias);
        textViewHoras = (TextView) v.findViewById(R.id.textViewHoras);
        textViewMinutos = (TextView) v.findViewById(R.id.textViewMinutos);

        //textViewDias.setTypeface(Typeface.createFromAsset(v.getContext().getAssets(), "Titillium-Light.otf"));
        //textViewHoras.setTypeface(Typeface.createFromAsset(v.getContext().getAssets(), "Titillium-Light.otf"));
        //textViewMinutos.setTypeface(Typeface.createFromAsset(v.getContext().getAssets(), "Titillium-Light.otf"));
        //textViewColegiosElectorales.setTypeface(Typeface.createFromAsset(v.getContext().getAssets(), "Titillium-Regular.otf"));
    }

    public void showContador(){

        long tiempoRestante = 0;

        //Calculamos el tiempo (milisegundos) que quedan para las elecciones catalanas
        try {
            long today = new Date().getTime();
            String fechaElecciones = "26/06/2016 09:00";
            Date elecciones = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(fechaElecciones);

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

                textViewDias.setText(""+days);
                textViewHoras.setText("" + hours);
                textViewMinutos.setText("" + minutes);
//                contador.setText( days + " " + MainActivity.resources.getString(R.string.dias) + "  " + hours + " h  \n"+ minutes +" mins  " + seconds + " segs ");
            }

            public void onFinish() {
                contador.setText("");//Texto al llegar a 0;
            }
        }.start();
    }

}
