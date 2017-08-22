/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.nsg.pagination.random;

import org.geoserver.config.GeoServer;
import org.geoserver.ows.AbstractDispatcherCallback;
import org.geoserver.ows.Request;
import org.geoserver.ows.Response;
import org.geoserver.platform.Operation;

import net.opengis.wfs20.ResultTypeType;

/**
 * <p>
 * When a request that contains the "resultType" parameter arrives, if the parameter value is
 * "index" it is substituted by "hits".
 * </p>
 * <p>
 * A new entry named RESULT_TYPE_INDEX specifying that the original result type was "index" is added
 * to KVP maps
 * </p>
 */

public class IndexResultTypeDisapatcherCallback extends AbstractDispatcherCallback {

    private GeoServer gs;

    private static final String RESULT_TYPE_PARAMETER = "resultType";

    private static final String RESULT_TYPE_INDEX = "index";

    static final String RESULT_TYPE_INDEX_PARAMETER = "RESULT_TYPE_INDEX";

    public IndexResultTypeDisapatcherCallback(GeoServer gs) {
        this.gs = gs;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Request init(Request request) {
        Object resultType = request.getKvp().get(RESULT_TYPE_PARAMETER);
        if (resultType != null && resultType.toString().equals(RESULT_TYPE_INDEX)) {
            request.getKvp().put(RESULT_TYPE_PARAMETER, ResultTypeType.HITS);
            request.getKvp().put(RESULT_TYPE_INDEX_PARAMETER, true);
        }
        return super.init(request);
    }

    @Override
    public Response responseDispatched(Request request, Operation operation, Object result,
            Response response) {
        Response newResponse = response;
        if (request.getKvp().get(RESULT_TYPE_INDEX_PARAMETER) != null
                && (Boolean) request.getKvp().get(RESULT_TYPE_INDEX_PARAMETER)) {
            newResponse = new IndexOutputFormat(this.gs);
        }
        return super.responseDispatched(request, operation, result, newResponse);
    }

}
