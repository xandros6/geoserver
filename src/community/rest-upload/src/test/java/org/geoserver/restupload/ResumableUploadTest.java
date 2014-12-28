/* (c) 2014-2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.restupload;

import static org.junit.Assert.assertEquals;

import org.geoserver.catalog.rest.CatalogRESTTestSupport;
import org.junit.Test;

import com.mockrunner.mock.web.MockHttpServletResponse;

public class ResumableUploadTest extends CatalogRESTTestSupport{

    @Test
    public void testPostRequest() throws Exception {
        MockHttpServletResponse response = postAsServletResponse("/rest/resumableupload", "", "text/plain" );
        assertEquals( 201, response.getStatusCode() );
        assertEquals("uploadLink",response.getOutputStreamContent());
    }

}
