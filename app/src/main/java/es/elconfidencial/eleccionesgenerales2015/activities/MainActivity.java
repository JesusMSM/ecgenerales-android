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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resources = getResources();

        setContentView(R.layout.activity_main);
        this.context = getApplicationContext();

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


        //Parse
        // Enable Local Datastore.

        Parse.enableLocalDatastore(this);

        ParseObject.registerSubclass(Quote.class);
        Parse.initialize(this, "fFMHyON2OrC3F161LgiepetpuB3WTktLvS6gq6ZH", "jqiMfz2BVxn4JNFhbsvscaEDg6QPObKn1JvGr0Wa");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("QUOTES");
        query.fromLocalDatastore();
        try {
            List<ParseObject> parseQuotes = query.find();
            if(parseQuotes.isEmpty()){
                Log.i("ParsePrueba", "Entramos en el null");
                //Nos bajamos la lista de quotes de la nube y lo almacenamos en local
                parseQuotes = getQuotesFromParse();

                for (ParseObject q : parseQuotes) {
                    GlobalMethod.quotes.add(new Quote(q.get("QUOTE").toString(), q.get("PERSONA").toString(), q.get("LABEL").toString()));
                }
                ParseObject.pinAll(parseQuotes);
                Log.i("ParsePrueba", "Quotes de Internet guardadas en local");
                Log.i("ParsePrueba", "Ejemplo quote" + GlobalMethod.quotes.get(0).getPersona());

                GlobalMethod.quotesIndex = GlobalMethod.getIntPreference(this,"quotesIndex",0);

            } else {
                Log.i("ParsePrueba", "Entramos en el else");
                for (ParseObject q : parseQuotes) {
                    GlobalMethod.quotes.add(new Quote(q.get("QUOTE").toString(), q.get("PERSONA").toString(), q.get("LABEL").toString()));
                }
                Log.i("ParsePrueba", "Ejemplo quote persona 0 " + GlobalMethod.quotes.get(0).getPersona());
                Log.i("ParsePrueba", "Ejemplo size " + GlobalMethod.quotes.size());
                GlobalMethod.quotesIndex = GlobalMethod.getIntPreference(this,"quotesIndex",0);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onBackPressed() {
        System.gc();
        finish();
        super.onBackPressed();
        System.exit(0);
    }


    private List<ParseObject> getQuotesFromParse() throws ParseException {
        //Parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("QUOTES");
        List <ParseObject> parseQuotes = query.find();

        return parseQuotes;
    }


}
