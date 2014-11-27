/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geoserver.catalog.MetadataMap;
import org.geotools.process.ProcessFactory;
import org.opengis.feature.type.Name;

public class ProcessGroupInfoImpl implements ProcessGroupInfo {

    private static final long serialVersionUID = 4850653421657310854L;

    Class<? extends ProcessFactory> factoryClass;

    boolean enabled;

    List<String> roles;

    List<ProcessAccessInfo> filteredProcesses = new ArrayList<ProcessAccessInfo>();

    MetadataMap metadataMap = new MetadataMap();

    @Override
    public String getId() {
        return "wpsProcessFactory-" + factoryClass.getName();
    }

    public Class<? extends ProcessFactory> getFactoryClass() {
        return factoryClass;
    }

    public void setFactoryClass(Class<? extends ProcessFactory> factoryClass) {
        this.factoryClass = factoryClass;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<ProcessAccessInfo> getFilteredProcesses() {
        return filteredProcesses;
    }

    public void setFilteredProcesses(List<ProcessAccessInfo> filteredProcesses) {
        this.filteredProcesses = filteredProcesses;
    }

    @Override
    public MetadataMap getMetadata() {
        return metadataMap;
    }

    @Override
    public ProcessGroupInfo clone() {
        ProcessGroupInfoImpl clone = new ProcessGroupInfoImpl();
        clone.setEnabled(enabled);
        clone.setFactoryClass(factoryClass);
        clone.setRoles(roles);
        if(filteredProcesses != null) {
            clone.setFilteredProcesses(new ArrayList<ProcessAccessInfo>(filteredProcesses));
        } 
        if(metadataMap != null) {
            clone.metadataMap = new MetadataMap(new HashMap<String, Serializable>(metadataMap));
        }

        return clone;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (enabled ? 1231 : 1237);
        result = prime * result + ((factoryClass == null) ? 0 : factoryClass.hashCode());
        result = prime * result + ((filteredProcesses == null) ? 0 : filteredProcesses.hashCode());
        result = prime * result + ((metadataMap == null) ? 0 : metadataMap.hashCode());
        result = prime * result + ((roles == null) ? 0 : roles.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProcessGroupInfoImpl other = (ProcessGroupInfoImpl) obj;
        if (enabled != other.enabled)
            return false;
        if (roles != other.roles)
            return false;
        if (factoryClass == null) {
            if (other.factoryClass != null)
                return false;
        } else if (!factoryClass.equals(other.factoryClass))
            return false;
        if (filteredProcesses == null) {
            if (other.filteredProcesses != null)
                return false;
        } else if (!filteredProcesses.equals(other.filteredProcesses))
            return false;
        if (metadataMap == null) {
            if (other.metadataMap != null)
                return false;
        } else if (!metadataMap.equals(other.metadataMap))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ProcessFactoryInfoImpl [factoryClass=" + factoryClass + ", enabled=" + enabled
                + ", filteredProcesses=" + filteredProcesses + ", metadataMap=" + metadataMap +  ", roles=" + roles +"]";
    }

    @Override
    public List<String> getRoles() {
        return roles;
    }

    @Override
    public void setRoles(List<String> roles) {
        this.roles = roles;        
    }


}
