package es.elconfidencial.eleccionesgenerales2015.model;

/**
 * Created by Moonfish on 9/11/15.
 */
public class Partido {
    private String id;
    private String nombre;
    private String color;
    private String siglas;

    public Partido(){
    }

    public Partido(String id, String color, String nombre, String siglas) {
        this.id = id;
        this.color = color;
        this.nombre = nombre;
        this.siglas = siglas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSiglas() {
        return siglas;
    }

    public void setSiglas(String siglas) {
        this.siglas = siglas;
    }
}
