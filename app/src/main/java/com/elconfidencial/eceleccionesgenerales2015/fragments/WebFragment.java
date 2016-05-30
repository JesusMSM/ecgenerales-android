package com.elconfidencial.eceleccionesgenerales2015.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.elconfidencial.eceleccionesgenerales2015.R;
import com.elconfidencial.eceleccionesgenerales2015.model.GlobalMethod;
import com.elconfidencial.eceleccionesgenerales2015.model.Noticia;
import com.elconfidencial.eceleccionesgenerales2015.text.TitilliumRegularTextView;

/**
 * Created by Afll on 30/05/2016.
 */
public class WebFragment extends Fragment {
    public Noticia noticia;

    public static WebFragment newInstance(Noticia noticia) {

        Bundle args = new Bundle();
        args.putParcelable("noticia", noticia);

        WebFragment fragment = new WebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        noticia = args.getParcelable("noticia");
    }

    View v;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_web, container, false);


        WebView webView = (WebView) v.findViewById(R.id.webView);

        GlobalMethod globalMethod = new GlobalMethod(v.getContext());
        TitilliumRegularTextView error = (TitilliumRegularTextView) v.findViewById(R.id.error);
        if(!globalMethod.haveNetworkConnection()){
            webView.setVisibility(View.GONE);
            error.setVisibility(View.VISIBLE);
        }else {

            //Datos para compartir
            String url = noticia.getLink();

            SharedPreferences prefs;
            prefs = PreferenceManager.getDefaultSharedPreferences(v.getContext());
            SharedPreferences.Editor editor = prefs.edit();

            url = url.replace("http://www.elconfidencial.com/", "http://www.elconfidencial.com/amp/");

            webView.getSettings().setJavaScriptEnabled(true);
            webView.setHorizontalScrollBarEnabled(false);
            webView.loadUrl(url);
        }

        return v;
    }
}
