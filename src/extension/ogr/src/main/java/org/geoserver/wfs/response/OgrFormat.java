/* (c) 2014 - 2015 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.response;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parameters defining an output format generated using ogr2ogr from
 * either a GML dump 
 * @author Andrea Aime - OpenGeo
 *
 */
public class OgrFormat {
    /**
     * The -f parameter
     */
    public String ogrFormat;
    
    /**
     * The GeoServer output format name
     */
    public String formatName;
    
    /**
     * The extension of the generated file, if any (shall include a dot, example, ".tab")
     */
    public String fileExtension;
    
    /**
     * The options that will be added to the command line
     */
    public List<String> options;
    
    /**
     * The type of format, used to instantiate the correct converter
     */
    public OgrType type;

    /**
     * If the output is a single file that can be streamed back. In that case we also need to know the mime type
     */
    public boolean singleFile;

    /**
     * The mime type of the single file output
     */
    public String mimeType;

    public OgrFormat(String ogrFormat, String formatName, String fileExtension, boolean singleFile,
            String mimeType, OgrType type, String... options) {
        this.ogrFormat = ogrFormat;
        this.formatName = formatName;
        this.fileExtension = fileExtension;
        this.singleFile = singleFile;
        this.mimeType = mimeType;
        this.type = type;
        if (options != null) {
            this.options = new ArrayList<String>(Arrays.asList(options));
        }
    }

    public OgrFormat(String ogrFormat, String formatName, String fileExtension, boolean singleFile,
            String mimeType, String... options) {
        this(ogrFormat, formatName, fileExtension, singleFile, mimeType, OgrType.BINARY, options);
    }

    /**
     * There is one major flaw in XStream. Unfortunately it has no way of telling if a field or attribute should get any default value if not present
     * in the xml file. Because constructor is not being invoked we cannot set the value there. Neither setting the value in field definition will
     * work. The resulting instance will always have zero or null values in the fields.
     *
     * The only way of setting the desired default value is using the following method. It is called during deserialization process and here we can
     * check if the field value is null. If yes it means that it's tag is not present and we can set the default value if needed.
     *
     * @return this
     * @throws ObjectStreamException
     */

    private Object readResolve() throws ObjectStreamException {
        if (this.type == null) {
            this.type = OgrType.BINARY;
        }
        return this;
    }

}
