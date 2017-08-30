/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.nsg.pagination.random;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.geoserver.config.GeoServer;
import org.geoserver.platform.resource.Resource;
import org.geoserver.wfs.DefaultWebFeatureService20;
import org.geoserver.wfs.WFSException;
import org.geoserver.wfs.request.FeatureCollectionResponse;
import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.util.logging.Logging;
import org.opengis.filter.Filter;

import net.opengis.wfs20.GetFeatureType;
import net.opengis.wfs20.ResultTypeType;

/**
 * This service supports the PageResults operation and manage it
 * 
 * @author sandr
 *
 */

public class PageResultsWebFeatureService extends DefaultWebFeatureService20 {

    static Logger LOGGER = Logging.getLogger(PageResultsWebFeatureService.class);

    private static ResourceSet resSet;

    private static final String GML32_FORMAT = "application/gml+xml; version=3.2";

    private static final BigInteger DEFAULT_START = new BigInteger("0");

    private static final BigInteger DEFAULT_COUNT = new BigInteger("10");

    private String resultSetID;

    public PageResultsWebFeatureService(GeoServer geoServer) {
        super(geoServer);
        // Register XMI serializer
        resSet = new ResourceSetImpl();
        resSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("feature",
                new XMIResourceFactoryImpl());
    }

    /**
     * Recovers the stored request with associated {@link #resultSetID} and overrides the parameters
     * using the ones provided with current operation or the default values:
     * <ul>
     * <li>{@link net.opengis.wfs20.GetFeatureType#getStartIndex <em>StartIndex</em>}</li>
     * <li>{@link net.opengis.wfs20.GetFeatureType#getCount <em>Count</em>}</li>
     * <li>{@link net.opengis.wfs20.GetFeatureType#getOutputFormat <em>OutputFormat</em>}</li>
     * <li>{@link net.opengis.wfs20.GetFeatureType#getResultType <em>ResultType</em>}</li>
     * </ul>
     * Then executes the GetFeature operation using the WFS 2.0 service implementation and return is
     * result.
     *
     * @param request
     * @return
     * @throws WFSException
     * @throws IOException
     */
    public FeatureCollectionResponse pageResults(GetFeatureType request)
            throws WFSException, IOException {
        // Retrieve stored request
        GetFeatureType gft = getFeature(resultSetID);
        // Update with incoming parameters or defaults
        BigInteger startIndex = request.getStartIndex() != null ? request.getStartIndex()
                : DEFAULT_START;
        BigInteger count = request.getCount() != null ? request.getCount() : DEFAULT_COUNT;
        String outputFormat = request.getOutputFormat() != null ? request.getOutputFormat()
                : GML32_FORMAT;
        ResultTypeType resultType = request.getResultType() != null ? request.getResultType()
                : ResultTypeType.RESULTS;
        gft.setStartIndex(startIndex);
        gft.setCount(count);
        gft.setOutputFormat(outputFormat);
        gft.setResultType(resultType);
        // Execute as getFeature
        return super.getFeature(gft);
    }

    /**
     * Sets the resultSetID
     * 
     * @param resultSetID
     */
    public void setResultSetID(String resultSetID) {
        this.resultSetID = resultSetID;
    }

    /**
     * Helper method that deserializes GetFeature request and updates its last utilization
     * 
     * @param resultSetID
     * @return
     * @throws IOException
     * @throws Exception
     */
    private GetFeatureType getFeature(String resultSetID) throws IOException {
        GetFeatureType feature = null;
        Transaction transaction = new DefaultTransaction("Update");
        try {
            // Update GetFeature utilization
            DataStore currentDataStore = IndexConfiguration.getCurrentDataStore();
            SimpleFeatureStore store = (SimpleFeatureStore) currentDataStore
                    .getFeatureSource(IndexInitializer.STORE_SCHEMA_NAME);
            store.setTransaction(transaction);
            Filter filter = CQL.toFilter("ID = '" + resultSetID + "'");
            store.modifyFeatures("updated", new Date().getTime(), filter);
            // Retrieve GetFeature from file
            Resource storageResource = IndexConfiguration.getStorageResource();
            org.eclipse.emf.ecore.resource.Resource emfRes = resSet.getResource(URI.createFileURI(
                    storageResource.dir().getAbsolutePath() + "\\" + resultSetID + ".feature"),
                    true);
            feature = (GetFeatureType) emfRes.getContents().get(0);
        } catch (Exception t) {
            transaction.rollback();
            throw new RuntimeException("Error on retrive feature", t);
        } finally {
            transaction.close();
        }
        return feature;

    }

}
