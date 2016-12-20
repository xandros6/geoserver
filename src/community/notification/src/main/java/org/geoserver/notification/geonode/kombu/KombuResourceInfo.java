package org.geoserver.notification.geonode.kombu;

import org.geotools.geometry.jts.ReferencedEnvelope;

public abstract class KombuResourceInfo extends KombuWorkspaceItemInfo {
    private String nativeName;

    private String store;

    private ReferencedEnvelope geographicBunds;

    private ReferencedEnvelope bounds;

    public String getNativeName() {
        return nativeName;
    }

    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public ReferencedEnvelope getGeographicBunds() {
        return geographicBunds;
    }

    public void setGeographicBunds(ReferencedEnvelope geographicBunds) {
        this.geographicBunds = geographicBunds;
    }

    public ReferencedEnvelope getBounds() {
        return bounds;
    }

    public void setBounds(ReferencedEnvelope bounds) {
        this.bounds = bounds;
    }

}
