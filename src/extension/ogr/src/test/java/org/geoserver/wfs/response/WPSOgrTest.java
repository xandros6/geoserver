/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.response;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.wps.WPSTestSupport;
import org.junit.Test;
import org.w3c.dom.Document;

public class WPSOgrTest extends WPSTestSupport {

    protected static void setUp() throws Exception {
        OgrConfiguration.DEFAULT.ogr2ogrLocation = Ogr2OgrTestUtil.getOgr2Ogr();
        OgrConfiguration.DEFAULT.gdalData = Ogr2OgrTestUtil.getGdalData();
        Ogr2OgrConfigurator configurator = applicationContext.getBean(Ogr2OgrConfigurator.class);
        configurator.loadConfiguration();
    }

    @Test
    public void testConfigurationLoad() throws Exception {
        // Force resource
        GeoServerResourceLoader loader = GeoServerExtensions.bean(GeoServerResourceLoader.class);
        try {
            loader.createFile("ogr2ogr.xml");
            loader.copyFromClassPath("ogr2ogr.xml", "ogr2ogr.xml");
        } catch (IOException e) {
            LOGGER.log(Level.FINER, e.getMessage(), e);
        }
        Ogr2OgrConfigurator configurator = applicationContext.getBean(Ogr2OgrConfigurator.class);
        configurator.of.setOgrExecutable(Ogr2OgrTestUtil.getOgr2Ogr());
        configurator.of.setGdalData(Ogr2OgrTestUtil.getGdalData());
        configurator.loadConfiguration();
        List<String> formatNames = new ArrayList<>();
        for (OgrFormat f : configurator.of.getFormatsList()) {
            formatNames.add(f.formatName);
        }
        assertTrue(formatNames.contains("OGR-TAB"));
        assertTrue(formatNames.contains("OGR-MIF"));
        assertTrue(formatNames.contains("OGR-CSV"));
        assertTrue(formatNames.contains("OGR-KML"));
    }

    @Test
    public void testDescribeProcess() throws Exception {
        setUp();
        Document d = getAsDOM(root()
                + "service=wps&request=describeprocess&identifier=gs:BufferFeatureCollection");
        String base = "/wps:ProcessDescriptions/ProcessDescription/ProcessOutputs";
        for (OgrFormat f : OgrConfiguration.DEFAULT.formats) {
            assertXpathExists(base + "/Output[1]/ComplexOutput/Supported/Format[MimeType='"
                    + f.mimeType + "']", d);
        }
    }

}
