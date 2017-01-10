package org.geoserver.notification.geonode.kombu;

public class KombuStoreInfo extends KombuSource {

    private String workspace;

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

}
