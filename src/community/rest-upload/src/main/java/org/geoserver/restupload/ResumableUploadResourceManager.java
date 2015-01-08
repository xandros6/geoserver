/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.restupload;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Resource;
import org.geoserver.rest.util.IOUtils;
import org.geotools.util.logging.Logging;
import org.restlet.resource.Representation;

public class ResumableUploadResourceManager {

    private static final Logger LOGGER = Logging.getLogger(ResumableUploadResourceManager.class);

    private static ConcurrentHashMap<String, ResumableUploadResource> resourceCache = new ConcurrentHashMap<String, ResumableUploadResource>();

    private static Resource tmpUploadFolder;

    public ResumableUploadResourceManager(String tmpFolder) {
        GeoServerResourceLoader loader = GeoServerExtensions.bean(GeoServerResourceLoader.class);
        tmpUploadFolder = loader.get(tmpFolder);
    }

    private static final class ResumableUploadResource {
        private Long totalFileSize = 0L;

        private File file;

        public ResumableUploadResource(String id) {
            this.file = new File(tmpUploadFolder.dir(), id);
        }

        public void setTotalFileSize(Long totalFileSize) {
            this.totalFileSize = totalFileSize;
        }

        public Long getTotalFileSize() {
            return totalFileSize;
        }

        public File getFile() {
            return file;
        }

        public long getLastModified() {
            return this.file.lastModified();
        }

        public void delete() {
            if (this.file.exists()) {
                this.file.delete();
            }
        }

    }

    /**
     * Create a new unique id for the resource.
     *
     * @return
     */
    private String getUploadId() {
        String id = UUID.randomUUID().toString();
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Associating resource with upload id: " + id);
        }
        return id;
    }

    public String createUploadResource() {
        String uploadId = getUploadId();
        ResumableUploadResource uploadResource = resourceCache.get(uploadId);
        if (uploadResource != null) {
            throw new IllegalStateException("The uploadId was already set!");
        } else {
            resourceCache.put(uploadId, new ResumableUploadResource(uploadId));
        }
        return uploadId;
    }

    public Boolean existsUploads() {
        return (resourceCache.size() != 0);
    }

    public boolean existsUpload(String uploadId) {
        return (resourceCache.get(uploadId) != null);
    }

    public Long handleUpload(String uploadId, Representation entity, Long startPosition) {
        Long writedBytes = 0L;
        try {
            final ReadableByteChannel source = entity.getChannel();
            RandomAccessFile raf = null;
            FileChannel outputChannel = null;
            try {
                raf = new RandomAccessFile(resourceCache.get(uploadId).getFile(), "rw");
                outputChannel = raf.getChannel();
                writedBytes = IOUtils.copyToFileChannel(256 * 1024, source, outputChannel,
                        startPosition);
            } finally {
                try {
                    if (raf != null) {
                        raf.close();
                    }
                } finally {
                    IOUtils.closeQuietly(source);
                    IOUtils.closeQuietly(outputChannel);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {

        }

        return resourceCache.get(uploadId).getFile().length();
    }

    public Boolean validateUpload(String uploadId, Long totalByteToUpload, Long startPosition,
            Long endPosition, Long totalFileSize) {
        Boolean validated = false;
        ResumableUploadResource uploadResource = resourceCache.get(uploadId);
        if (uploadResource.getFile().exists()) {
            if (uploadResource.getFile().length() == startPosition
                    && uploadResource.getTotalFileSize().longValue() == totalFileSize.longValue()) {
                validated = true;
            }
        }
        return validated;
    }

    public void setFileSize(String uploadId, Long totalFileSize) {
        ResumableUploadResource uploadResource = resourceCache.get(uploadId);
        uploadResource.setTotalFileSize(totalFileSize);
    }

    public Long getFileSize(String uploadId) {
        ResumableUploadResource uploadResource = resourceCache.get(uploadId);
        return uploadResource.getTotalFileSize();
    }

    public void clearUpload(String uploadId) {
        ResumableUploadResource resource = resourceCache.get(uploadId);
        resource.delete();
    }

    public void uploadDone(String uploadId) {
        // TODO MAPPING FILE
    }

    public void cleanExpiredResources(long expirationThreshold) {
        for (String uploadId : resourceCache.keySet()) {
            ResumableUploadResource resource = resourceCache.get(uploadId);
            if (resource.getLastModified() < expirationThreshold) {
                resource.delete();
                resourceCache.remove(uploadId);
            }
        }
    }

    public Long getWritedBytes(String uploadId) {
        return resourceCache.get(uploadId).getFile().length();
    }

}
