/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.nsg.pagination.random;

import java.util.Arrays;
import java.util.logging.Logger;

import org.geoserver.config.GeoServer;
import org.geoserver.ows.AbstractDispatcherCallback;
import org.geoserver.ows.Request;
import org.geoserver.platform.Service;
import org.geoserver.platform.ServiceException;
import org.geotools.util.logging.Logging;

/**
 *
 * This dispatcher manages service of type {@link PageResultsWebFeatureService} and sets the
 * parameter ResultSetID present on KVP map.
 * <p>
 * Dummy featureId value is added to KVP map to allow dispatcher to manage it as usual WFS 2.0
 * request.
 *
 * @author sandr
 * 
 */

public class PageResultsDispatcherCallback extends AbstractDispatcherCallback {

    static Logger LOGGER = Logging.getLogger(PageResultsDispatcherCallback.class);

    private GeoServer gs;

    public PageResultsDispatcherCallback(GeoServer gs) {
        this.gs = gs;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Service serviceDispatched(Request request, Service service) throws ServiceException {
        if (service.getService() instanceof PageResultsWebFeatureService) {
            PageResultsWebFeatureService prService = (PageResultsWebFeatureService) service
                    .getService();
            prService.setResultSetID((String) request.getKvp().get("resultSetID"));
            request.getKvp().put("featureId", Arrays.asList("dummy"));
        }
        return super.serviceDispatched(request, service);
    }

}
