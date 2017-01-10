package org.geoserver.notification.geonode.kombu;

public class KombuNamespaceInfo extends KombuSource {

    private String namespaceURI;

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

}