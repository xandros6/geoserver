/* (c) 2014-2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.restupload;

import org.geoserver.catalog.Catalog;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;

public class ResumableUploadCatalogResource extends Resource{

    public ResumableUploadCatalogResource(Context context, Request request, Response response, Catalog catalog) {
        super(context, request, response);
    }

    @Override
    public boolean allowPost() {
        return true;
    }

    @Override
    public boolean allowPut() {
        return super.allowPut();
    }

    @Override
    /*
     * First POST request returns upload URL with uploadId to call with successive PUT request
     */
    public void handlePost() {
        Representation output = new StringRepresentation("uploadLink", MediaType.TEXT_PLAIN);
        Response response = getResponse();
        response.setEntity(output);
        response.setStatus(Status.SUCCESS_ACCEPTED);
    }

    /*
     * PUT request is used to uploads file with uploadId generated by POST request
     */
    @Override
    public void handlePut(){

    }

    /*
     * GET request with uploadId is used to get the status of upload
     */
    @Override
    public void handleGet(){

    }

}
