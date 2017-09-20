package org.geoserver.nsg.pagination.random;

import java.io.Serializable;
import java.util.Map;

public class RequestData implements Serializable {

    private static final long serialVersionUID = 6687946816946977568L;

    private Map kvp;

    private Map rawKvp;

    public Map getKvp() {
        return kvp;
    }

    public void setKvp(Map kvp) {
        this.kvp = kvp;
    }

    public Map getRawKvp() {
        return rawKvp;
    }

    public void setRawKvp(Map rawKvp) {
        this.rawKvp = rawKvp;
    }

}
