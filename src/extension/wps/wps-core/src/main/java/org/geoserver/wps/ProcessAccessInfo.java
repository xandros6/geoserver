package org.geoserver.wps;

import java.util.List;

import org.geoserver.catalog.Info;

public interface ProcessAccessInfo extends Info, Cloneable {

    boolean isEnabled();

    List<String> getRoles();

}
