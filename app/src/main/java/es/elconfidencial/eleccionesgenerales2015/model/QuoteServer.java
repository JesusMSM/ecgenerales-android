package es.elconfidencial.eleccionesgenerales2015.model;

import android.content.Context;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Afll on 01/11/2015.
 */
public class QuoteServer{
    private static QuoteServer mInstance = null;
    private Context context;

    public static List<Quote> quotes = new ArrayList();
    public static List<Persona> personas = new ArrayList<>();

    Persona [] personasByAgreed;
    public int countQuotes=0;
    public int quotesIndex=0;
    public int answeredQuotes=0;


    private QuoteServer() {
    }
    public static QuoteServer getInstance(){
        if (mInstance == null)
            mInstance = new QuoteServer();
        return mInstance;
    }
    public void init(Context context){
        this.context = context;
        //Obtener los objetos desde preferences si existen;
        quotesIndex = GlobalMethod.getIntPreference(context, "quotesIndex", 0);
        countQuotes = GlobalMethod.getIntPreference(context, "countQuotes", 0);
        answeredQuotes = GlobalMethod.getIntPreference(context, "answeredQuotes" ,0);
    }

    public void cleanData(){

    }

    /*Obtengo las quotes desde Parse o en local si hay**/
    public void getQuotesFromParseOrLocal(){
        //Parse
        // Enable Local Datastore.
        Parse.enableLocalDatastore(context);

        ParseObject.registerSubclass(Quote.class);
        Parse.initialize(context, "fFMHyON2OrC3F161LgiepetpuB3WTktLvS6gq6ZH", "jqiMfz2BVxn4JNFhbsvscaEDg6QPObKn1JvGr0Wa");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("QUOTES");
        query.fromLocalDatastore();
        try {
            List<ParseObject> parseQuotes = query.find();
            if(parseQuotes.isEmpty()){
                //Nos bajamos la lista de quotes de la nube y lo almacenamos en local
                ParseQuery<ParseObject> query2 = ParseQuery.getQuery("QUOTES");
                parseQuotes = query2.find();


                for (ParseObject q : parseQuotes) {
                    //Create new Quote from ParseObject
                    quotes.add(new Quote(q));
                    //Save persona for this quote
                    savePersonInLocalWithQuote(q);
                }

                Collections.shuffle(quotes); //Mezclamos aleatoriamente las quotes
                ParseObject.pinAll(parseQuotes);

                Log.i("ParsePrueba", "Quotes de Internet guardadas en local");
                Log.i("ParsePrueba", "Ejemplo quote" + quotes.get(0).getPersona());

            } else {
                Log.i("ParsePrueba", "Entramos en el else");
                //Obtenemos las quotes de local
                for (ParseObject q : parseQuotes) {
                    quotes.add(new Quote(q.get("QUOTE").toString(), q.get("PERSONA").toString(), q.get("LABEL").toString()));
                }
                //Obtenemos las personas de local
                getPersonsFromLocal();
                Log.i("ParsePrueba", "Ejemplo quote persona 0 " + quotes.get(0).getPersona());
                Log.i("ParsePrueba", "Ejemplo size " + quotes.size());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // Creamos en parse local un objeto persona con los datos que extraemos de la quote pasada por parámetro
    public void savePersonInLocalWithQuote(ParseObject quote){
        if(!personExists(quote.getString("PERSONA"))){
            ParseObject personaPObj = new ParseObject("Persona");
            personaPObj.put("name", quote.getString("PERSONA"));
            personaPObj.put("niceName", quote.getString("NICENAME"));
            personaPObj.put("photoLink", quote.getString("PERSONA").toLowerCase());
            personaPObj.put("agree", 0);
            personaPObj.put("disagree", 0);
            personaPObj.put("partyColor", "");
            personaPObj.put("party", quote.getString("PARTIDO"));

            //Objecto local
            Persona personaECL = new Persona(personaPObj);
            this.personas.add(personaECL);
            personaPObj.pinInBackground();
        }
    }

    public void getPersonsFromLocal(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Persona");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects,
                             ParseException e) {
                if (e == null) {
                    for(ParseObject obj :objects){
                        personas.add(new Persona(obj));
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }
    public boolean personExists(String name){
        boolean res = false;
        for(Persona p :personas){
            if(p.getName().equals(name)){
                res = true;
            };
        }
        return res;
    }

    public Persona getPersonFromName(String name){
        for (Persona p :personas){
            if(personExists(name) && p.getName().equals(name)){
                return p;
            }
        }
        return null;
    }

    public void agreedWithQuote(Quote quote) {
        incrementQuotesIndex();

        Log.i("QuoteServer: agree", quote.getPersona());
        Persona p = getPersonFromName(quote.getPersona());
        try {
            p.increaseAgree(); //incrementamos la variable local del contador
            Log.i("Quote", p.personaPObj.getString("name"));
            Log.i("Quote", p.getName());
            p.personaPObj.increment("agree"); //incrementamos la variable de parse local, para siguientes sesiones
            p.personaPObj.pin();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("Quote:", "Agrees:" + p.getAgree() + " Disagrees: " + p.getDisagree());

        //Parse Analytics - agree
        Map<String, String> agree = new HashMap<String, String>();
        agree.put("quote", quote.getText());
        agree.put("persona", quote.getPersona());
        ParseAnalytics.trackEvent("ECL_AGREE_EVENT", agree);
    }

    public void disagreedWithQuote(Quote quote) {
        incrementQuotesIndex();
        Log.i("QuoteServer: disagree", quote.getPersona());

        Persona p = getPersonFromName(quote.getPersona());
        try {
            p.increaseDisagree(); //incrementamos la variable local del contador
            Log.i("Quote", p.personaPObj.getString("name"));
            Log.i("Quote", p.getName());
            p.personaPObj.increment("disagree");//incrementamos la variable de parse local, para siguientes sesiones
            p.personaPObj.pin();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Parse Analytics - disagree
        Map<String, String> disagree = new HashMap<String, String>();
        disagree.put("quote", quote.getText());
        disagree.put("persona", quote.getPersona());
        ParseAnalytics.trackEvent("ECL_DISAGREE_EVENT", disagree);
    }
    public void incrementQuotesIndex(){
        //Aumentamos index siguiente pregunta
        if (quotesIndex == quotes.size() - 1) {//Ha llegado al final de las quotes
            //Reset del index
            GlobalMethod.saveIntPreference(context, 0, "quotesIndex");
            quotesIndex = GlobalMethod.getIntPreference(context, "quotesIndex", 0);
            Log.i("QuoteServer","quoteIndex: " + quotesIndex);
        } else {
            GlobalMethod.saveIntPreference(context, quotesIndex + 1, "quotesIndex");
            quotesIndex= GlobalMethod.getIntPreference(context, "quotesIndex", 0);
            Log.i("QuoteServer","quoteIndex: " + quotesIndex);
        }
    }
    public Quote nextQuote(){
        return quotes.get(quotesIndex);
    }

    /**GETTERS SETTERS*/
    public int getQuotesIndex() {
        return quotesIndex;
    }

    public void setQuotesIndex(int quotesIndex) {
        this.quotesIndex = quotesIndex;
    }

    public int getCountQuotes() {
        return countQuotes;
    }

    public void setCountQuotes(int countQuotes) {
        this.countQuotes = countQuotes;
    }

    public int getAnsweredQuotes() {
        return answeredQuotes;
    }

    public void setAnsweredQuotes(int answeredQuotes) {
        this.answeredQuotes = answeredQuotes;
    }

    public Persona[] getPersonasByAgreed() {
        return personasByAgreed;
    }

    public void setPersonasByAgreed(Persona[] personasByAgreed) {
        this.personasByAgreed = personasByAgreed;
    }
}
