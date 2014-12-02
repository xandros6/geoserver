package org.geoserver.wps.security;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoserver.ows.Dispatcher;
import org.geoserver.ows.Request;
import org.geoserver.security.AccessLimits;
import org.geoserver.security.CatalogMode;
import org.geotools.util.logging.Logging;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ProcessAccessLimits extends AccessLimits {
    private static final Logger LOGGER = Logging.getLogger(ProcessAccessLimits.class);
    private static final long serialVersionUID = -3253977289877833644L;
    private boolean allowed;
    private String resource;

    public ProcessAccessLimits(CatalogMode mode, boolean allowed, String resource) {
        super(mode);
        this.resource = resource;
        this.allowed = allowed;
    }

    public boolean isAllowed(Boolean riseAuthException) {
        try{
            checkCatalogMode();
        }catch(InsufficientAuthenticationException ex){
            if(riseAuthException){
                if(LOGGER.isLoggable(Level.FINE)){
                    LOGGER.fine(ex.getMessage());
                }
                throw ex;
            }
        }catch(AccessDeniedException ex){
            if(riseAuthException){
                if(LOGGER.isLoggable(Level.FINE)){
                    LOGGER.fine(ex.getMessage());
                }
                throw ex;
            }
        }
        return this.allowed;
    }

    private void checkCatalogMode() {
        if (!this.allowed) {
            Request request = Dispatcher.REQUEST.get();
            //If in HIDE mode stay hidden
            if (getMode() == CatalogMode.HIDE) {

            } else if (getMode() == CatalogMode.MIXED) {
                //In MIXED mode the process stay hidden
                if(request != null && "GetCapabilities".equalsIgnoreCase(request.getRequest())){
                    //And throw unauthorized access in other case
                }else{
                    throw unauthorizedAccess(resource);
                }
            } else {
                //In CHALLENGE mode the process is always visible
                this.allowed = true;
                //But throw unauthorized access in Execute and Describe request
                if(request != null && 
                        !"GetCapabilities".equalsIgnoreCase(request.getRequest()) && 
                        ("Execute".equalsIgnoreCase(request.getRequest()) || "DescribeProcess".equalsIgnoreCase(request.getRequest())) ){
                    throw unauthorizedAccess(resource);
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
