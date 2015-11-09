package es.elconfidencial.eleccionesgenerales2015.model;

/**
 * Created by Moonfish on 9/11/15.
 */
public class PartidoMegaencuesta {
    private String name;
    private int color;
    private int nVotos;
    private double porcentajeVotos;

    public PartidoMegaencuesta(){

    }

    public PartidoMegaencuesta(String name, int color, int nVotos, double porcentajeVotos) {
        this.name = name;
        this.color = color;
        this.nVotos = nVotos;
        this.porcentajeVotos = porcentajeVotos;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getnVotos() {
        return nVotos;
    }

    public void setnVotos(int nVotos) {
        this.nVotos = nVotos;
    }

    public double getPorcentajeVotos() {
        return porcentajeVotos;
    }

    public void setPorcentajeVotos(double porcentajeVotos) {
        this.porcentajeVotos = porcentajeVotos;
    }
}
