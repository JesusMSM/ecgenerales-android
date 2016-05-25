package com.elconfidencial.eceleccionesgenerales2015.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Afll on 28/10/2015.
 */
@ParseClassName("Quote")
public class Quote extends ParseObject{
    String text;
    String persona;
    String grupo;
    ParseObject quotePObj;
    boolean answered;

    public Quote(){
    }
    public Quote(String text,String persona,String grupo, boolean answered){
        this.text = text;
        this.persona = persona;
        this.grupo = grupo;
        this.answered = answered;
    }
    public Quote (ParseObject quotePObj){
        this.quotePObj = quotePObj;
        if (quotePObj != null) {
            this.text = quotePObj.getString("QUOTE");
            this.grupo = quotePObj.getString("LABEL");
            this.persona = quotePObj.getString("PERSONA");
            this.answered = quotePObj.getBoolean("answered");
        }
    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public void agree(){
        //Parse agree
    }
    public void disagree(){

    }
    public boolean IsEqualToQuote(Quote quote){
        return true;
    }

    public void SaveRemotelyInBG() {

    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }
}
