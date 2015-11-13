package es.elconfidencial.eleccionesgenerales2015.model;

/**
 * Created by Moonfish on 9/11/15.
 */
public class Partido {
    private String id;
    private String nombre;
    private String color;
    private String siglas;
    private String tagNoticias;
    private String tagPush;

    public Partido(){
    }

    public Partido(String id, String nombre, String color, String siglas, String tagNoticias, String tagPush) {
        this.id = id;
        this.nombre = nombre;
        this.color = color;
        this.siglas = siglas;
        this.tagNoticias = tagNoticias;
        this.tagPush = tagPush;
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

    public String getTagNoticias() {
        return tagNoticias;
    }

    public void setTagNoticias(String tagNoticias) {
        this.tagNoticias = tagNoticias;
    }

    public String getTagPush() {
        return tagPush;
    }

    public void setTagPush(String tagPush) {
        this.tagPush = tagPush;
    }
}
