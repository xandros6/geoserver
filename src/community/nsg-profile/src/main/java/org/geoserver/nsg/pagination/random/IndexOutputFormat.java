/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.nsg.pagination.random;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.geoserver.config.GeoServer;
import org.geoserver.ows.util.OwsUtils;
import org.geoserver.ows.util.ResponseUtils;
import org.geoserver.platform.Operation;
import org.geoserver.platform.ServiceException;
import org.geoserver.wfs.WFSInfo;
import org.geoserver.wfs.request.FeatureCollectionResponse;
import org.geoserver.wfs.response.v2_0.HitsOutputFormat;
import org.geotools.util.logging.Logging;
import org.geotools.wfs.v2_0.WFS;
import org.geotools.wfs.v2_0.WFSConfiguration;
import org.geotools.xml.Encoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.opengis.wfs20.GetFeatureType;

/**
 * This output format handles requests if the original requested result type was "index" </br>
 * See {@link IndexResultTypeDisapatcherCallback}
 * 
 * @author sandr
 *
 */
public class IndexOutputFormat extends HitsOutputFormat {

    static Logger LOGGER = Logging.getLogger(IndexOutputFormat.class);
    GetFeatureType request;
    String resultSetId;

    public IndexOutputFormat(GeoServer gs) {
        super(gs);
    }
    
    @Override
    public void write(Object value, OutputStream output, Operation operation)
            throws IOException, ServiceException {
        // extract GetFeature request
        request = (GetFeatureType) OwsUtils.parameter(operation.getParameters(),
                GetFeatureType.class);
        // generate an UUID (resultSetID) for this request
        resultSetId = UUID.randomUUID().toString();
        super.write(value, output, operation);
    }

    @Override
    protected void encode(FeatureCollectionResponse hits, OutputStream output, WFSInfo wfs)
            throws IOException {
        
        hits.setNumberOfFeatures(BigInteger.ZERO);
        // instantiate the XML encoder
        Encoder encoder = new Encoder(new WFSConfiguration());
        encoder.setEncoding(Charset.forName(wfs.getGeoServer().getSettings().getCharset()));
        encoder.setSchemaLocation(WFS.NAMESPACE,
                ResponseUtils.appendPath(wfs.getSchemaBaseURL(), "wfs/2.0/wfs.xsd"));
        Document document;
        try {
            // encode the HITS result using FeatureCollection as the root XML element
            document = encoder.encodeAsDOM(hits.getAdaptee(), WFS.FeatureCollection);
        } catch (Exception exception) {
            throw new RuntimeException("Error encoding INDEX result.", exception);
        }
        // add the resultSetID attribute to the result
        addResultSetIdElement(document, resultSetId);
        // write the XML document to response output stream
        writeDocument(document, output);
    }
    
    /**
     * Helper method that serialize GetFeature request, store it in the file system and associate it with resultSetId
     */
    protected void storeGetFeature(){
        
    }

    /**
     * Helper method that adds the resultSetID attribute to XML result. If no FeatureCollection
     * element can be found nothing will be done.
     */
    private static void addResultSetIdElement(Document document, String resultSetId) {
        // search FeatureCollection XML nodes
        NodeList nodes = document.getElementsByTagName("wfs:FeatureCollection");
        if (nodes.getLength() != 1) {
            // only one node should exists, let's log an warning an move on
            LOGGER.warning(
                    "No feature collection element could be found, resultSetID attribute will not be added.");
            return;
        }
        // get the FeatureCollection node
        Node node = nodes.item(0);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            // the found node is a XML element so let's add the resultSetID attribute
            Element element = (Element) node;
            element.setAttribute("resultSetID", resultSetId);
        } else {
            // unlikely but we got a XML node that is not a XML element
            LOGGER.warning(
                    "Feature collection node is not a XML element, resultSetID attribute will not be added.");
        }
    }

    /**
     * Helper method that just writes a XML document to a given output stream.
     */
    private static void writeDocument(Document document, OutputStream output) {
        // instantiate a new XML transformer
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (Exception exception) {
            throw new RuntimeException("Error creating XML transformer.", exception);
        }
        // write the XML document to the provided output stream
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(output);
        try {
            transformer.transform(source, result);
        } catch (Exception exception) {
            throw new RuntimeException("Error writing INDEX result to the output stream.",
                    exception);
        }
    }

}
