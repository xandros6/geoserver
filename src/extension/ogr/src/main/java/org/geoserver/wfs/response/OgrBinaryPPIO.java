/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.wfs.response;

import java.io.InputStream;
import java.io.OutputStream;

import net.opengis.wfs.FeatureCollectionType;

import org.geoserver.platform.Operation;
import org.geoserver.wps.ppio.BinaryPPIO;
import org.geotools.feature.FeatureCollection;

public class OgrBinaryPPIO extends BinaryPPIO {

    private Ogr2OgrOutputFormat ogr2OgrOutputFormat;

    private OgrFormat of;

    private Operation operation;

    public OgrBinaryPPIO(OgrFormat of, Ogr2OgrOutputFormat ogr2OgrOutputFormat, Operation operation) {
        super(FeatureCollectionType.class, FeatureCollection.class, of.mimeType);
        this.of = of;
        this.ogr2OgrOutputFormat = ogr2OgrOutputFormat;
        this.operation = operation;
    }

    @Override
    public void encode(Object value, OutputStream os) throws Exception {
        ogr2OgrOutputFormat.write(value, os, operation);
    }

    @Override
    public String getFileExtension() {
        return this.of.fileExtension;
    }

    @Override
    public PPIODirection getDirection() {
        return PPIODirection.ENCODING;
    }

    @Override
    public Object decode(InputStream input) throws Exception {
        return null;
    }

}
