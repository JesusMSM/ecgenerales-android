package es.elconfidencial.eleccionesgenerales2015.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Jesus on 07/11/2015.
 */
public class DatosEncuentas {

    private ArrayList<PartidoEncuesta> datosEncuesta;

    public ArrayList<PartidoEncuesta> getDatosEncuesta() {
        return datosEncuesta;
    }

    public void setDatosEncuesta(ArrayList<PartidoEncuesta> datosEncuesta) {
        this.datosEncuesta = datosEncuesta;
    }

    public DatosEncuentas(ArrayList<PartidoEncuesta> datosEncuesta) {

        this.datosEncuesta = datosEncuesta;
    }
}
