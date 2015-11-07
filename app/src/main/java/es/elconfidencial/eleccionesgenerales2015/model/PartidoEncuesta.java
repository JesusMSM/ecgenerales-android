package es.elconfidencial.eleccionesgenerales2015.model;

/**
 * Created by Jesus on 07/11/2015.
 */
public class PartidoEncuesta {

    private String name;
    private double porcentaje;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public PartidoEncuesta(String name, double porcentaje) {

        this.name = name;
        this.porcentaje = porcentaje;
    }
}
