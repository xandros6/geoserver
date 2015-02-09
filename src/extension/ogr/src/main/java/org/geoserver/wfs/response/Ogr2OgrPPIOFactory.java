/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.wfs.response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.opengis.wfs.GetFeatureType;
import net.opengis.wfs.WfsFactory;

import org.geoserver.platform.Operation;
import org.geoserver.platform.Service;
import org.geoserver.wps.ppio.PPIOFactory;
import org.geoserver.wps.ppio.ProcessParameterIO;
import org.geotools.util.Version;

public class Ogr2OgrPPIOFactory implements PPIOFactory {

    private Ogr2OgrOutputFormat ogr2OgrOutputFormat;

    public Ogr2OgrPPIOFactory(Ogr2OgrOutputFormat ogr2OgrOutputFormat) {
        this.ogr2OgrOutputFormat = ogr2OgrOutputFormat;
    }

    @Override
    public List<ProcessParameterIO> getProcessParameterIO() {
        List<ProcessParameterIO> ogrParams = new ArrayList<ProcessParameterIO>();
        for (OgrFormat of : this.ogr2OgrOutputFormat.getFormatsList()) {
            ProcessParameterIO ppio = null;
            GetFeatureType gft = WfsFactory.eINSTANCE.createGetFeatureType();
            gft.setOutputFormat(of.formatName);
            Operation operation = new Operation("GetFeature", new Service("WFS", null, new Version(
                    "1.0.0"), Arrays.asList("GetFeature")), null, new Object[] { gft });
            String mimeType = of.mimeType;
            if(mimeType == null || mimeType.isEmpty()){
                of.mimeType = ogr2OgrOutputFormat.getMimeType(null, operation);
            }
            switch (of.type) {
            case BINARY:
                ppio = new OgrBinaryPPIO(of, ogr2OgrOutputFormat, operation);
                break;
            case TEXT:
                ppio = new OgrCDataPPIO(of, ogr2OgrOutputFormat, operation);
                break;
            case XML:
                ppio = new OgrXMLPPIO(of, ogr2OgrOutputFormat, operation);
                break;
            default:
                break;
            }
            if (ppio != null) {
                ogrParams.add(ppio);
            }
        }
        return ogrParams;
    }
}
