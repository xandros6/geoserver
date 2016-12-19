package org.geoserver.notification.geonode.kombu;

import java.io.Serializable;

public class KombuSource implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = -8997506545819486027L;

    private String id;

    private String type;

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
