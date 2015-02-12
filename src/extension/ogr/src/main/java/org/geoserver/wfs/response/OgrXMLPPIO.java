/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.wfs.response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.opengis.wfs.FeatureCollectionType;
import net.opengis.wfs.WfsFactory;

import org.geoserver.platform.Operation;
import org.geoserver.wps.ppio.XMLPPIO;
import org.geotools.feature.FeatureCollection;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * Process XML output parameter using ogr2ogr process
 */

public class OgrXMLPPIO extends XMLPPIO {

    private Ogr2OgrOutputFormat ogr2OgrOutputFormat;

    private OgrFormat of;

    private Operation operation;

    public OgrXMLPPIO(OgrFormat of, Ogr2OgrOutputFormat ogr2OgrOutputFormat, Operation operation) {
        super(FeatureCollectionType.class, FeatureCollection.class, of.mimeType,
                org.geoserver.wfs.xml.v1_1_0.WFS.FEATURECOLLECTION);
        this.of = of;
        this.ogr2OgrOutputFormat = ogr2OgrOutputFormat;
        this.operation = operation;
    }

    @Override
    public void encode(Object value, OutputStream os) throws Exception {
        ogr2OgrOutputFormat.write(value, os, this.operation);
    }

    @Override
    public PPIODirection getDirection() {
        return PPIODirection.ENCODING;
    }

    @Override
    public void encode(Object value, ContentHandler handler) throws Exception {
        FeatureCollection<?, ?> features = (FeatureCollection<?, ?>) value;
        FeatureCollectionType fc = WfsFactory.eINSTANCE.createFeatureCollectionType();
        fc.getFeature().add(features);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        encode(fc, os);

        InputStream bis = new ByteArrayInputStream(os.toByteArray());
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        XMLReader parser = saxParser.getXMLReader();
        parser.setContentHandler(handler);
        parser.parse(new InputSource(bis));

    }

    @Override
    public Object decode(InputStream input) throws Exception {
        return null;
    }

}
