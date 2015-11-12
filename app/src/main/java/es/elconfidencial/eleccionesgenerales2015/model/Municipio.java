package es.elconfidencial.eleccionesgenerales2015.model;

/**
 * Created by Moonfish on 12/11/15.
 */
public class Municipio {
    private int tag;
    private String municipioName;
    private String municipioAutoCompleteText;
    private int municipioTag;
    private String provinciaName;
    private int provinciaTag;
    private String ccaaaName;
    private int ccaaTag;

     public Municipio(){
     }

    public Municipio(int tag, String municipioName, String municipioAutoCompleteText, int municipioTag, String provinciaName, int provinciaTag, String ccaaaName, int ccaaTag) {
        this.tag = tag;
        this.municipioName = municipioName;
        this.municipioAutoCompleteText = municipioAutoCompleteText;
        this.municipioTag = municipioTag;
        this.provinciaName = provinciaName;
        this.provinciaTag = provinciaTag;
        this.ccaaaName = ccaaaName;
        this.ccaaTag = ccaaTag;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getMunicipioName() {
        return municipioName;
    }

    public void setMunicipioName(String municipioName) {
        this.municipioName = municipioName;
    }

    public String getMunicipioAutoCompleteText() {
        return municipioAutoCompleteText;
    }

    public void setMunicipioAutoCompleteText(String municipioAutoCompleteText) {
        this.municipioAutoCompleteText = municipioAutoCompleteText;
    }

    public int getMunicipioTag() {
        return municipioTag;
    }

    public void setMunicipioTag(int municipioTag) {
        this.municipioTag = municipioTag;
    }

    public String getProvinciaName() {
        return provinciaName;
    }

    public void setProvinciaName(String provinciaName) {
        this.provinciaName = provinciaName;
    }

    public int getProvinciaTag() {
        return provinciaTag;
    }

    public void setProvinciaTag(int provinciaTag) {
        this.provinciaTag = provinciaTag;
    }

    public String getCcaaaName() {
        return ccaaaName;
    }

    public void setCcaaaName(String ccaaaName) {
        this.ccaaaName = ccaaaName;
    }

    public int getCcaaTag() {
        return ccaaTag;
    }

    public void setCcaaTag(int ccaaTag) {
        this.ccaaTag = ccaaTag;
    }
}
