package org.geoserver.notification.geonode.kombu;

public abstract class KombuWorkspaceItemInfo extends KombuSource {
    private String workspace;

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

}
