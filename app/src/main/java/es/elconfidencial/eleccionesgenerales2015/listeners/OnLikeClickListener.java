package es.elconfidencial.eleccionesgenerales2015.listeners;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import es.elconfidencial.eleccionesgenerales2015.R;
import es.elconfidencial.eleccionesgenerales2015.model.GlobalMethod;

/**
 * Created by Afll on 29/10/2015.
 */
public class OnLikeClickListener implements View.OnClickListener{

    Context context;
    public OnLikeClickListener(Context context){
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        Activity act = (Activity) v.getContext();
        final Dialog settingsDialog = new Dialog(act);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(v.getContext());
        settingsDialog.setContentView(inflater.inflate(R.layout.image_popup_layout
                , null));
        settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        //Contador de 3 segundos
        settingsDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                //Contador 3,2,1 dismiss.
            }
        });

        //Al tocarlo se cierra
        settingsDialog.findViewById(R.id.image_dialog_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsDialog.dismiss();
            }
        });
        settingsDialog.show();

        //Set imagen
        try {
            ImageView foto = (ImageView) settingsDialog.findViewById(R.id.foto);
            Glide.with(context).load(R.drawable.nopicpersona).into(foto);
            //Fonts
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView title = (TextView) settingsDialog.findViewById(R.id.introText);
        TextView persona = (TextView) settingsDialog.findViewById(R.id.personaText);

        title.setText("Estás de acuerdo con:");
        persona.setText("Mariano Rajoy");
        //Fonts
        title.setTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-Light.otf"));
        persona.setTypeface(Typeface.createFromAsset(context.getAssets(), "Titillium-Regular.otf"));

        //Aumentamos index siguiente pregunta
        GlobalMethod.saveIntPreference(context,GlobalMethod.quotesIndex,"quotesIndex");
        GlobalMethod.quotesIndex = GlobalMethod.getIntPreference(context,"quotesIndex",0);
    }

    public void rellenarCampos(){

    }
}
