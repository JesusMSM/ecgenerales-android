package com.elconfidencial.eceleccionesgenerales2015.model;

/**
 * Created by Moonfish on 28/10/15.
 */
public class Noticia {
    private String titulo;
    private String descripcion;
    private String autor;
    private String imagenUrl;
    private String link;
    private String fecha;

    public Noticia(){
        super();
    }

    public Noticia(String titulo, String descripcion, String autor, String imagenUrl, String link, String fecha){
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.autor = autor;
        this.imagenUrl = imagenUrl;
        this.link = link;
        this.fecha = fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
