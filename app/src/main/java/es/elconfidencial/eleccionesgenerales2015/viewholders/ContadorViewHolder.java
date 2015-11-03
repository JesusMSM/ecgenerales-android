package es.elconfidencial.eleccionesgenerales2015.viewholders;

import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.elconfidencial.eleccionesgenerales2015.R;
import es.elconfidencial.eleccionesgenerales2015.activities.MainActivity;

/**
 * Created by Jesus on 03/11/2015.
 */
public class ContadorViewHolder extends RecyclerView.ViewHolder{

    public TextView contador,label,barra,d_20;

    public ContadorViewHolder(View v) {
        super(v);
        contador = (TextView) v.findViewById(R.id.contador);
        label = (TextView) v.findViewById(R.id.label);
        barra = (TextView) v.findViewById(R.id.barra);
        d_20 = (TextView) v.findViewById(R.id.d_20);
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


                contador.setText( days + " " + MainActivity.resources.getString(R.string.dias) + "  " + hours + " h  \n"+ minutes +" mins  " + seconds + " segs ");
            }

            public void onFinish() {
                contador.setText("");//Texto al llegar a 0;
            }
        }.start();
    }

}
