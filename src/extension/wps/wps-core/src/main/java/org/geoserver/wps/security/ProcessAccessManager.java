package org.geoserver.wps.security;

import org.opengis.feature.type.Name;
import org.springframework.security.core.Authentication;

public interface ProcessAccessManager {

    /**
     * Returns the access limits for a whole process namespace
     */
    ProcessAccessLimits getAccessLimits(Authentication user, String namespace);

    /**
     * Returns the access limits for a single process
     */
    ProcessAccessLimits getAccessLimits(Authentication user, Name process);
}
