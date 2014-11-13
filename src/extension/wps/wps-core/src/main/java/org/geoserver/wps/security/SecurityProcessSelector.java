package org.geoserver.wps.security;

import org.opengis.feature.type.Name;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityProcessSelector extends SecurityProcessFilter{

    public SecurityProcessSelector() {
        super();
    }
    
    @Override
    protected boolean allowProcess(Name processName) {
        Authentication user = SecurityContextHolder.getContext().getAuthentication();
        return manager.getAccessLimits(user, processName).isAllowed();
    }

}
