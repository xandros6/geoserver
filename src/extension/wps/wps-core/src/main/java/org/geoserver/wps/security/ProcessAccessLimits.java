package org.geoserver.wps.security;

import org.geoserver.ows.Dispatcher;
import org.geoserver.ows.Request;
import org.geoserver.security.AccessLimits;
import org.geoserver.security.CatalogMode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ProcessAccessLimits extends AccessLimits {
    private static final long serialVersionUID = -3253977289877833644L;
    private boolean allowed;
    private String resource;

    public ProcessAccessLimits(CatalogMode mode, boolean allowed, String resource) {
        super(mode);
        this.resource = resource;
        this.allowed = allowed;
        checkCatalogMode();
    }

    public boolean isAllowed() {
        return allowed;
    }

    private void checkCatalogMode(){
        if (!this.allowed) {
            Request request = Dispatcher.REQUEST.get();
            // if in hide mode, we just hide the resource
            if (getMode() == CatalogMode.HIDE) {
                //not change allowed mode
            } else if (getMode() == CatalogMode.MIXED) {
                // if request is a get capabilities and mixed, we hide again               
                if(request != null && "GetCapabilities".equalsIgnoreCase(request.getRequest())){
                    this.allowed = false;
                    // otherwise challenge the user for credentials
                }else{
                    //Internal call
                    if(request == null){
                        this.allowed = true;
                    }else{
                        throw unauthorizedAccess(resource);
                    }
                }
            } else {
                // for challenge mode we agree to show freely only the metadata, every
                // other access will trigger a security exception
                if(request != null && "Execute".equalsIgnoreCase(request.getRequest())){
                    throw unauthorizedAccess(resource);
                }else{
                    this.allowed = true;
                }
            }
        }
    }

    private static RuntimeException unauthorizedAccess(String resourceName) {
        // not hide, and not filtering out a list, this
        // is an unauthorized direct resource access, complain
        Authentication user =  SecurityContextHolder.getContext().getAuthentication();
        if (user == null || user.getAuthorities().size() == 0)
            return new InsufficientAuthenticationException("Cannot access "
                    + resourceName + " as anonymous");
        else
            return new AccessDeniedException("Cannot access "
                    + resourceName + " with the current privileges");
    }
}
