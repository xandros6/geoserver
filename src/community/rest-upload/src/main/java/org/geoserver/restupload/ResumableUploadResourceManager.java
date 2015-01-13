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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Resource;
import org.geoserver.rest.util.IOUtils;
import org.geoserver.rest.util.RESTUtils;
import org.geotools.util.logging.Logging;
import org.restlet.resource.Representation;

public class ResumableUploadResourceManager {

    private static final Logger LOGGER = Logging.getLogger(ResumableUploadResourceManager.class);

    private static Resource tmpUploadFolder;

    public ResumableUploadResourceManager(String tmpFolder) {
        GeoServerResourceLoader loader = GeoServerExtensions.bean(GeoServerResourceLoader.class);
        tmpUploadFolder = loader.get(tmpFolder);
    }

    private static final class ResumableUploadResource {
        private String id;

        private File file;

        public ResumableUploadResource(String id, File file) {
            this.id = id;
            this.file = file;
        }

        public File getFile() {
            return file;
        }

        public void delete() {
            if (this.file.exists()) {
                this.file.delete();
            }
        }

        public void clear() {
            if (this.file.exists()) {
                this.file.delete();
            }
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
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

    public String createUploadResource(String filePath) throws IllegalStateException, IOException {
        String uploadId = getUploadId();
        ResumableUploadResource uploadResource = getResource(uploadId);
        if (uploadResource != null) {
            throw new IllegalStateException("The uploadId was already set!");
        } else {
            createUploadResource(filePath, uploadId);
        }
        return uploadId;
    }

    // Return a FILE by append uploadId to filePath with "_" separator
    private void createUploadResource(String filePath, String uploadId) throws IOException {
        String tempPath = FilenameUtils.removeExtension(filePath) + "_" + uploadId + "."
                + FilenameUtils.getExtension(filePath);
        tempPath = tempPath.replaceAll("^/", "");
        tempPath = FilenameUtils.concat(tmpUploadFolder.dir().getCanonicalPath(), tempPath);
        try {
            new File(tempPath).getParentFile().mkdirs();
            new File(tempPath).createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create upload resource");
        }
    }

    // Find resource with specific uploadId
    private ResumableUploadResource getResource(String uploadId) throws IllegalStateException {
        Collection<File> files = FileUtils.listFiles(tmpUploadFolder.dir(), new WildcardFileFilter(
                "*_" + uploadId + ".*"), TrueFileFilter.INSTANCE);
        if (files.size() == 1) {
            return new ResumableUploadResource(uploadId, files.iterator().next());
        }
        if (files.size() > 1) {
            throw new IllegalStateException("Found multiple files with same uploadId");
        }
        return null;

    }

    public Boolean hasAnyResource() {
        Collection<File> files = FileUtils.listFiles(tmpUploadFolder.dir(), new WildcardFileFilter(
                "*.*"), TrueFileFilter.INSTANCE);
        return (files.size() != 0);
    }

    public boolean resourceExists(String uploadId) {
        return getResource(uploadId) != null;
    }

    public Long handleUpload(String uploadId, Representation entity, Long startPosition) {
        ResumableUploadResource resource = getResource(uploadId);
        Long writedBytes = 0L;
        try {
            final ReadableByteChannel source = entity.getChannel();
            RandomAccessFile raf = null;
            FileChannel outputChannel = null;
            try {
                raf = new RandomAccessFile(resource.getFile(), "rw");
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

        return resource.getFile().length();
    }

    public Boolean validateUpload(String uploadId, Long totalByteToUpload, Long startPosition,
            Long endPosition, Long totalFileSize) {
        Boolean validated = false;
        ResumableUploadResource uploadResource = getResource(uploadId);
        if (uploadResource != null && uploadResource.getFile().exists()) {
            if (uploadResource.getFile().length() == startPosition) {
                validated = true;
            }
        }
        return validated;
    }

    public void clearUpload(String uploadId) {
        ResumableUploadResource resource = getResource(uploadId);
        if (resource != null) {
            resource.clear();
        }
    }

    public void cleanExpiredResources(long expirationThreshold) {
        Collection<File> files = FileUtils.listFiles(tmpUploadFolder.dir(), new WildcardFileFilter(
                "*.*"), TrueFileFilter.INSTANCE);
        for (Iterator<File> i = files.iterator(); i.hasNext();) {
            File file = i.next();
            if (file.lastModified() < expirationThreshold) {
                file.delete();
            }
        }
    }

    public Long getWritedBytes(String uploadId) {
        return getResource(uploadId).getFile().length();
    }

    public String uploadDone(String uploadId) throws IOException {
        ResumableUploadResource resource = getResource(uploadId);
        Map<String, String> storeParams = new HashMap<String, String>();
        StringBuilder destinationPath = new StringBuilder(getDestinationPath(uploadId));
        String tempFile = resource.getFile().getCanonicalPath();
        RESTUtils.remapping(null, FilenameUtils.getBaseName(destinationPath.toString()),
                destinationPath, tempFile, storeParams);
        resource.delete();
        // Add temporary sidecar file to mark upload completion, it will be cleared after expirationThreshold
        getSideCarFile(uploadId).createNewFile();
        return destinationPath.toString();
    }

    private File getSideCarFile(String uploadId) throws IOException {
        String sidecarPath = FilenameUtils.concat(tmpUploadFolder.dir().getCanonicalPath(),
                uploadId + ".sidecar");
        return new File(sidecarPath);
    }

    public Boolean isUploadDone(String uploadId) throws IOException, IllegalStateException {
        ResumableUploadResource resource = getResource(uploadId);
        if (resource != null) {
            return false;
        } else {
            if (getSideCarFile(uploadId).exists()) {
                return true;
            } else {
                throw new IllegalStateException("Resource uploaded not found");
            }
        }
    }

    private String getDestinationPath(String uploadId) throws IOException {
        ResumableUploadResource resource = getResource(uploadId);
        String fileName = resource.getFile().getCanonicalPath()
                .replaceAll(tmpUploadFolder.dir().getCanonicalPath(), "");
        fileName = fileName.replaceAll("_" + uploadId, "");
        fileName = fileName.replaceAll("^/", "");
        return fileName;
    }

}
