package es.elconfidencial.eleccionesgenerales2015.model;

/**
 * Created by Afll on 28/10/2015.
 */
public class Quote {
    String text;
    String persona;
    String grupo;

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

    public Quote(String text,String persona,String grupo){
        this.text = text;
        this.persona = persona;
        this.grupo = grupo;

    }
}
