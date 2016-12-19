package org.geoserver.notification.geonode.kombu;

import java.io.Serializable;
import java.util.Date;

public class KombuMessage implements Serializable {

    private String id;

    private String type;

    private String action;

    private String generator = "GeoServer";

    private Date timestamp;

    private String user;

    private String originator;

    private KombuSource source;

    private KombuProperties properties;

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

    public void setSource(KombuSource source) {
        this.source = source;
    }

    public void setProperties(KombuProperties properties) {
        this.properties = properties;
    }

}
