package com.elconfidencial.eceleccionesgenerales2015.model;

import java.util.ArrayList;

/**
 * Created by Jesus on 07/11/2015.
 */
public class Encuesta {
    private String name;
    private ArrayList<PartidoEncuesta> partidosEncuesta;
    private String fecha;
    private String descripcion;

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<PartidoEncuesta> getPartidosEncuesta() {
        return partidosEncuesta;
    }

    public void setPartidosEncuesta(ArrayList<PartidoEncuesta> partidosEncuesta) {
        this.partidosEncuesta = partidosEncuesta;
    }

    public Encuesta(String name, ArrayList<PartidoEncuesta> partidosEncuesta) {

        this.name = name;
        this.partidosEncuesta = partidosEncuesta;
    }
}
