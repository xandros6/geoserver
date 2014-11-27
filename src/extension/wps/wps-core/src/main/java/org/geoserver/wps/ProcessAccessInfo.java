package org.geoserver.wps;

import java.util.List;

import org.geoserver.catalog.Info;
import org.opengis.feature.type.Name;

public interface ProcessAccessInfo extends Info, Cloneable {

    Name getName();
    
    boolean isEnabled();

    List<String> getRoles();
    
    void setName(Name name);
    
    void setEnabled(Boolean enabled);

}
