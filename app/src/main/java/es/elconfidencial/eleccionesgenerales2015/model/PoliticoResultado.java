package es.elconfidencial.eleccionesgenerales2015.model;

/**
 * Created by jorge_cmata on 31/10/15.
 */
public class PoliticoResultado {
    private String nombre;
    private String partido;
    private int nLikes;
    private int nDislikes;

    public PoliticoResultado(){
        super();
    }

    public PoliticoResultado(String nombre, String partido, int nLikes, int nDislikes) {
        this.nombre = nombre;
        this.partido = partido;
        this.nLikes = nLikes;
        this.nDislikes = nDislikes;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPartido() {
        return partido;
    }

    public void setPartido(String partido) {
        this.partido = partido;
    }

    public int getnLikes() {
        return nLikes;
    }

    public void setnLikes(int nLikes) {
        this.nLikes = nLikes;
    }

    public int getnDislikes() {
        return nDislikes;
    }

    public void setnDislikes(int nDislikes) {
        this.nDislikes = nDislikes;
    }
}
