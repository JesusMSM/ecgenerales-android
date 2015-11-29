package com.elconfidencial.eceleccionesgenerales2015.rss;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

import com.elconfidencial.eceleccionesgenerales2015.model.Noticia;

/**
 * Created by Moonfish on 28/10/15.
 */
public class RssNoticiasHandler extends DefaultHandler {
    private List<Noticia> noticias;
    private Noticia noticiaActual;
    private StringBuilder sbTexto;
    private boolean firstTime;

    public List<Noticia> getNoticias(){
        return noticias;
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        super.characters(ch, start, length);

        if (this.noticiaActual != null)
            sbTexto.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {

        super.endElement(uri, localName, name);

        if (this.noticiaActual != null) {
            if (localName.equals("title") && (firstTime)) {
                noticiaActual.setTitulo(sbTexto.toString());
                firstTime = false;
            } else if(localName.equals("content")){
                //Comprobamos que no esta vacio
                if(!sbTexto.toString().isEmpty()){
                    noticiaActual.setDescripcion(sbTexto.toString().replaceAll("<img src[^>]*>", ""));
                }
            } else if (localName.equals("id")) {
                noticiaActual.setLink(sbTexto.toString());
            } else if (localName.equals("published")) {
                noticiaActual.setFecha(sbTexto.toString());
            } else if (localName.equals("name")) {
                noticiaActual.setAutor(sbTexto.toString());
            } else if(localName.equals("entry")){
                noticias.add(noticiaActual);}

            sbTexto.setLength(0);
        }
    }

    @Override
    public void startDocument() throws SAXException {

        super.startDocument();

        noticias = new ArrayList<Noticia>();
        sbTexto = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName,
                             String name, Attributes attributes) throws SAXException {

        super.startElement(uri, localName, name, attributes);

        if (localName.equals("entry")) {
            firstTime=true;
            noticiaActual = new Noticia();

        }
        if (localName.equals("link")) {
            if (attributes.getValue("rel").toString().equals("enclosure")) {
                noticiaActual.setImagenUrl(attributes.getValue("href").toString());
            }
        }
    }

}