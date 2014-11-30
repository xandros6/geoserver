package org.geoserver.wps.security;

import java.util.logging.Logger;

import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.wps.process.ProcessFilter;
import org.geotools.process.ProcessFactory;
import org.geotools.util.logging.Logging;
import org.opengis.feature.type.Name;

public abstract class SecurityProcessFilter implements ProcessFilter{

    private static final Logger LOGGER = Logging.getLogger(SecurityProcessFilter.class);
    protected ProcessAccessManager manager;

    @Override
    public ProcessFactory filterFactory(ProcessFactory pf) {
        if(manager == null){
            manager = GeoServerExtensions.bean(ProcessAccessManager.class);
            if (manager == null) {
                manager = new DefaultProcessAccessManager(GeoServerExtensions.bean(WpsAccessRuleDAO.class));
            } 
        }
        return new SecurityProcessFactory(pf, this);
    }

    protected abstract boolean allowProcess(Name processName);

}
