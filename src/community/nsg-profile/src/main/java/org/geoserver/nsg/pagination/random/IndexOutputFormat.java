/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.nsg.pagination.random;

import java.io.IOException;
import java.io.OutputStream;

import org.geoserver.config.GeoServer;
import org.geoserver.platform.Operation;
import org.geoserver.platform.ServiceException;
import org.geoserver.wfs.response.v2_0.HitsOutputFormat;

import net.opengis.wfs20.BaseRequestType;

/**
 * This output format handles requests if the original requested result type was "index" </br>
 * It checks {@link BaseRequestType#getExtendedProperties()} for
 * {@link IndexResultTypeDisapatcherCallback#RESULT_TYPE_INDEX_PARAMETER} valued as true
 * 
 * @author sandr
 *
 */
public class IndexOutputFormat extends HitsOutputFormat {

    public IndexOutputFormat(GeoServer gs) {
        super(gs);
    }

    @Override
    public void write(Object value, OutputStream output, Operation operation)
            throws IOException, ServiceException {
        super.write(value, output, operation);
    }

}
