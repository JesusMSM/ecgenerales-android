package com.elconfidencial.eceleccionesgenerales2015.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Moonfish on 28/10/15.
 */
public class Noticia implements Parcelable{
    private String titulo;
    private String descripcion;
    private String autor;
    private String imagenUrl;
    private String link;
    private String fecha;
    private String tag;

    public Noticia(){
        super();
    }

    protected Noticia(Parcel in) {
        titulo = in.readString();
        descripcion = in.readString();
        autor = in.readString();
        imagenUrl = in.readString();
        link = in.readString();
        fecha = in.readString();
        tag = in.readString();
    }

    public static final Creator<Noticia> CREATOR = new Creator<Noticia>() {
        @Override
        public Noticia createFromParcel(Parcel in) {
            return new Noticia(in);
        }

        @Override
        public Noticia[] newArray(int size) {
            return new Noticia[size];
        }
    };

    public Noticia(String titulo, String descripcion, String autor, String imagenUrl, String link, String fecha, String tag){
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.autor = autor;
        this.imagenUrl = imagenUrl;
        this.link = link;
        this.fecha = fecha;
        this.tag = tag;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(titulo);
        dest.writeString(descripcion);
        dest.writeString(autor);
        dest.writeString(imagenUrl);
        dest.writeString(link);
        dest.writeString(fecha);
        dest.writeString(tag);
    }
}
