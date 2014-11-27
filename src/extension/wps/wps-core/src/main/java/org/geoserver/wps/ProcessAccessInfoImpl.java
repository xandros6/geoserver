package org.geoserver.wps;

import java.util.ArrayList;
import java.util.List;

import org.opengis.feature.type.Name;

public class ProcessAccessInfoImpl implements ProcessAccessInfo {

    private static final long serialVersionUID = -8791361642137777632L;
    
    private Boolean enabled;
    private List<String> roles = new ArrayList<String>();
    private Name name;
    private String id;
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public List<String> getRoles() {
        return roles;
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setName(Name name) {
       this.name = name;        
    }

    @Override
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;        
    }

}
