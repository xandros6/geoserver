package org.geoserver.wps.security;

import org.geoserver.security.AccessLimits;
import org.geoserver.security.CatalogMode;

public class ProcessAccessLimits extends AccessLimits {
    private static final long serialVersionUID = -3253977289877833644L;
    private boolean allowed;

    public ProcessAccessLimits(CatalogMode mode, boolean allowed) {
        super(mode);
        this.allowed = allowed;
    }

    public boolean isAllowed() {
        return allowed;
    }
}
