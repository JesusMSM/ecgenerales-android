package es.elconfidencial.eleccionesgenerales2015.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import es.elconfidencial.eleccionesgenerales2015.R;
import es.elconfidencial.eleccionesgenerales2015.adapters.ViewPagerAdapter;
import es.elconfidencial.eleccionesgenerales2015.model.GlobalMethod;
import es.elconfidencial.eleccionesgenerales2015.model.Quote;
import es.elconfidencial.eleccionesgenerales2015.model.QuoteServer;
import es.elconfidencial.eleccionesgenerales2015.slidingtabfiles.SlidingTabLayout;

public class MainActivity extends AppCompatActivity {

    public static Context context;
    public static Resources resources;
    static ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    int numbOfTabs =4;
    CharSequence[] Titles = new CharSequence[numbOfTabs];
    GlobalMethod globalMethod = new GlobalMethod(this);

    //Quotes
    QuoteServer qs = QuoteServer.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resources = getResources();

        setContentView(R.layout.activity_main);
        this.context = getApplicationContext();
        qs.init(getApplicationContext());

        //Titulos, da igual lo que se ponga pero tienen que existir aunque no se vayan a ver despues
        Titles[0] = "HOME";
        Titles[1] = "NEWS";
        Titles[2] = "POLLS";
        Titles[3] = "PRESINDER";

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, numbOfTabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);
        pager.setOffscreenPageLimit(3);
        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.ColorAccent);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setCustomTabView(R.layout.custom_actionbar, 0);
        tabs.setViewPager(pager);

        //Download quotes
        qs.getQuotesFromParseOrLocal();
        //Likes dislikes count
        //GlobalMethod.likesCount = GlobalMethod.getMyHashmap(getApplicationContext(),"likesCount");
        //GlobalMethod.dislikesCount = GlobalMethod.getMyHashmap(getApplicationContext(),"dislikesCount");
    }



    @Override
    public void onBackPressed() {
        System.gc();
        finish();
        super.onBackPressed();
        System.exit(0);
    }





}
