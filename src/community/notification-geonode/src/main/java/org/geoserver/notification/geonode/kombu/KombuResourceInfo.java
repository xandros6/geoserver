package org.geoserver.notification.geonode.kombu;

import org.geotools.geometry.jts.ReferencedEnvelope;

public abstract class KombuResourceInfo extends KombuWorkspaceItemInfo {
    private String nativeName;

    private String store;

    private KobuBounds geographicBunds;

    private KobuBounds bounds;

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

    public KobuBounds getGeographicBunds() {
        return geographicBunds;
    }

    public void setGeographicBunds(KobuBounds geographicBunds) {
        this.geographicBunds = geographicBunds;
    }

    public KobuBounds getBounds() {
        return bounds;
    }

    public void setBounds(KobuBounds bounds) {
        this.bounds = bounds;
    }

}
