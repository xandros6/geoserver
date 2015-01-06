/* (c) 2014-2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.restupload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.geoserver.catalog.rest.CatalogRESTTestSupport;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.Status;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;

public class ResumableUploadTest extends CatalogRESTTestSupport{

    private Resource tmpUploadFolder;
    private String uploadId;
    private long partialSize = 50;

    @Before
    public void before() throws Exception {
        GeoServerResourceLoader loader = GeoServerExtensions.bean(GeoServerResourceLoader.class);
        tmpUploadFolder = loader.get("tmp/upload");
    }

    @Test
    public void testPostRequest() throws Exception {
        MockHttpServletResponse response = postAsServletResponse("/rest/resumableupload", "", "text/plain" );
        assertEquals( Status.SUCCESS_CREATED.getCode(), response.getStatusCode() );
        uploadId = response.getOutputStreamContent();
        assertNotNull(uploadId);
        response = postAsServletResponse("/rest/resumableupload", "", "text/plain" );
        assertEquals( Status.SUCCESS_CREATED.getCode(), response.getStatusCode() );
        String secondUploadId = response.getOutputStreamContent();
        assertNotNull(secondUploadId);
        assertNotEquals(uploadId, secondUploadId);
    }

    @Test
    public void testSuccessivePostRequest() throws Exception {
        testPostRequest();
        MockHttpServletResponse response = postAsServletResponse("/rest/resumableupload", "", "text/plain" );
        assertEquals( Status.SUCCESS_CREATED.getCode(), response.getStatusCode() );
        String secondUploadId = response.getOutputStreamContent();
        assertNotNull(secondUploadId);
        assertNotEquals(uploadId, secondUploadId);
    }

    @Test
    public void testUploadFull() throws Exception {
        testPostRequest();
        MockHttpServletRequest request = createRequest("/rest/resumableupload/"+uploadId);
        request.setMethod("PUT");
        request.setContentType("application/octet-stream");
        byte[] bigFile = bigShpAsBytes();
        request.setBodyContent(bigFile);
        request.setHeader( "Content-type", "application/octet-stream" );
        request.setHeader( "Content-Length", String.valueOf(bigFile.length));
        MockHttpServletResponse response = dispatch(request);
        assertEquals( Status.SUCCESS_OK.getCode(), response.getStatusCode() );
        File uploadedFile = new File(tmpUploadFolder.dir(), uploadId);
        assertTrue(uploadedFile.exists());
        assertEquals(bigFile.length,uploadedFile.length());
        //Check uploaded file byte by byte
        boolean checkBytes = Arrays.equals(
                bigShpAsBytes(),
                toBytes(new FileInputStream(uploadedFile))
                );
        assertTrue(checkBytes);
        //Check response content
        String restUrl = response.getOutputStreamContent();
        assertEquals("resturl", restUrl);
    }

    @Test
    public void testPartialUpload() throws Exception {
        testPostRequest();
        MockHttpServletRequest request = createRequest("/rest/resumableupload/"+uploadId);
        request.setMethod("PUT");
        request.setContentType("application/octet-stream");
        byte[] bigFile = bigShpAsBytes();
        byte[] partialFile = ArrayUtils.subarray(bigShpAsBytes(), 0, (int)partialSize);
        request.setBodyContent(partialFile);
        request.setHeader( "Content-type", "application/octet-stream" );
        request.setHeader( "Content-Length", String.valueOf(bigFile.length));
        MockHttpServletResponse response = dispatch(request);
        assertEquals(ResumableUploadCatalogResource.RESUME_INCOMPLETE.getCode(), response.getStatusCode() );
        assertEquals(null,response.getHeader("Content-Length"));
        assertEquals("0-"+(partialSize-1),response.getHeader("Range"));
        File uploadedFile = new File(tmpUploadFolder.dir(), uploadId);
        assertTrue(uploadedFile.exists());
        assertEquals(partialSize,uploadedFile.length());
    }

    @Test
    public void testUploadPartialResume() throws Exception {
        testPartialUpload();
        //Resume upload
        MockHttpServletRequest request = createRequest("/rest/resumableupload/"+uploadId);
        request.setMethod("PUT");
        request.setContentType("application/octet-stream");
        byte[] bigFile = bigShpAsBytes();
        byte[] partialFile = ArrayUtils.subarray(bigShpAsBytes(), (int)partialSize, (int)partialSize*2);
        request.setBodyContent(partialFile);
        request.setHeader( "Content-type", "application/octet-stream" );
        request.setHeader( "Content-Length", String.valueOf(partialFile.length));
        request.setHeader( "Content-Range", "bytes "+partialSize+"-"+partialSize*2+"/"+bigFile.length);
        MockHttpServletResponse response = dispatch(request);
        assertEquals(ResumableUploadCatalogResource.RESUME_INCOMPLETE.getCode(), response.getStatusCode() );
        assertEquals(null,response.getHeader("Content-Length"));
        assertEquals("0-"+(partialSize*2-1), response.getHeader("Range"));
        File uploadedFile = new File(tmpUploadFolder.dir(), uploadId);
        assertTrue(uploadedFile.exists());
        assertEquals(partialSize*2,uploadedFile.length());
        //Check uploaded file byte by byte
        boolean checkBytes = Arrays.equals(
                ArrayUtils.subarray(bigShpAsBytes(), 0, (int)partialSize*2),
                toBytes(new FileInputStream(uploadedFile))
                );
        assertTrue(checkBytes);
    }

    @Test
    public void testUploadFullResume() throws Exception {
        testPartialUpload();
        //Resume upload
        MockHttpServletRequest request = createRequest("/rest/resumableupload/"+uploadId);
        request.setMethod("PUT");
        request.setContentType("application/octet-stream");
        byte[] bigFile = bigShpAsBytes();
        byte[] partialFile = ArrayUtils.subarray(bigShpAsBytes(), (int)partialSize, bigFile.length);
        request.setBodyContent(partialFile);
        request.setHeader( "Content-type", "application/octet-stream" );
        request.setHeader( "Content-Length", String.valueOf(partialFile.length));
        request.setHeader( "Content-Range", "bytes "+partialSize+"-"+bigFile.length+"/"+bigFile.length);
        MockHttpServletResponse response = dispatch(request);
        assertEquals( Status.SUCCESS_OK.getCode(), response.getStatusCode() );
        File uploadedFile = new File(tmpUploadFolder.dir(), uploadId);
        assertTrue(uploadedFile.exists());
        assertEquals(bigFile.length,uploadedFile.length());
        //Check uploaded file byte by byte
        boolean checkBytes = Arrays.equals(
                bigShpAsBytes(),
                toBytes(new FileInputStream(uploadedFile))
                );
        assertTrue(checkBytes);
        //Check response content
        String restUrl = response.getOutputStreamContent();
        assertEquals("resturl", restUrl);
    }

    @Test
    public void testCleanup() throws Exception {
        //Change cleanup expirationDelay
        ResumableUploadResourceCleaner cleaner = (ResumableUploadResourceCleaner)applicationContext.getBean("resumableUploadStorageCleaner");
        cleaner.setExpirationDelay(1000);
        //Upload file
        testPartialUpload();

        File uploadedFile = new File(tmpUploadFolder.dir(), uploadId);
        //Wait to cleanup, max 2 minutes
        long startTime = new Date().getTime();
        while(uploadedFile.exists() && (new Date().getTime()-startTime) < 120000){
            Thread.sleep(1000);
        }
        assertTrue(!uploadedFile.exists());
        cleaner.setExpirationDelay(300000);
    }

    @Test
    public void testGetAfterPartial() throws Exception {
        testPartialUpload();
        MockHttpServletResponse response = getAsServletResponse("/rest/resumableupload/"+uploadId, "text/plain" );
        assertEquals(ResumableUploadCatalogResource.RESUME_INCOMPLETE.getCode(), response.getStatusCode() );
        assertEquals(null,response.getHeader("Content-Length"));
        assertEquals("0-"+(partialSize-1),response.getHeader("Range"));
    }

    @Test
    public void testGetAfterFull() throws Exception {
        testUploadFull();
        MockHttpServletResponse response = getAsServletResponse("/rest/resumableupload/"+uploadId, "text/plain" );
        assertEquals( Status.SUCCESS_OK.getCode(), response.getStatusCode() );
    }

    private byte[] bigShpAsBytes() throws IOException {
       return toBytes(getClass().getResourceAsStream( "bigFile.shp" ));
    }

    private byte[] toBytes(InputStream in) throws IOException {
        return IOUtils.toByteArray(in);
    }

}
