package es.elconfidencial.eleccionesgenerales2015.model;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Afll on 01/11/2015.
 */
public class Persona {

    ParseObject personaPObj;
    String name,niceName,photoLink,partyColor,party ="";
    int agree,disagree=0;

    public Persona() {
    }

    public Persona(ParseObject personaPObj) {
        this.personaPObj = personaPObj;
        this.name = personaPObj.getString("name");
        this.niceName = personaPObj.getString("niceName");
        this.photoLink = personaPObj.getString("photoLink");
        this.party = personaPObj.getString("party");
        this.partyColor = personaPObj.getString("partyColor");
        this.agree = personaPObj.getInt("agree");
        this.disagree = personaPObj.getInt("disagree");
    }

    public int getDisagree() {
        return disagree;
    }

    public void setDisagree(int disagree) {
        this.disagree = disagree;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNiceName() {
        return niceName;
    }

    public void setNiceName(String niceName) {
        this.niceName = niceName;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public String getPartyColor() {
        return partyColor;
    }

    public void setPartyColor(String partyColor) {
        this.partyColor = partyColor;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public int getAgree() {
        return agree;
    }

    public void setAgree(int agree) {
        this.agree = agree;
    }

    public void increaseAgree(){
        agree++;

    }
    public void increaseDisagree(){
        disagree++;

    }
    public int getAgreeDisagreeDif(){
        return disagree - agree;
    }
    public void SaveLocallyInBG(){

    }
}
