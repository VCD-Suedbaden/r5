package com.conveyal.file;

import java.io.File;

/**
 * Store (and maybe mirror) immutable files.
 * These are always seen as local files on the local filesystem,
 * but may be made more permanent (accessible to workers and future backends).
 * <p>
 * The add/remove/etc. methods are all blocking calls now for simplicity, i.e. if you add a file, all other components
 * of the system are known to be able to see it as soon as the method returns.
 * <p>
 * This does not handle storing file metadata in MongoDB. That is a separate concern.
 * Workers for example need to get files without looking into our database.
 * Our file metadata handling component could wrap this, so all backend file operations implicitly had metadata.
 * <p>
 * In the S3-based implementation we need to set content type and compression details on S3.
 * We plan to do that by inspecting the "magic number" bytes at the beginning of the file and auto-setting the
 * content type.
 */
public interface FileStorage {

    public interface Config {
        // The local directory where files will be stored, even if they are being mirrored to a remote storage service.
        String cacheDirectory();
        // This is actually only needed for one subclass, but leaving it alone because I expect to remove it entirely.
        String awsRegion();
    }

    /**
     * Takes an already existing file on the local filesystem and registers it as a permanent, immutable file to be
     * made available to all analysis components including workers and future backends.
     * <p>
     * If a file was uploaded in a form, we can call DiskFileItem.getStoreLocation to get the file, which according
     * to that method's Javadoc we are allowed to rename to our own location.
     * <p>
     * If the file was created by the backend, it should be created in a temp file. Once the file is completely
     * constructed / written out, it should be closed and then this method called on it.
     */
    void moveIntoStorage(FileStorageKey fileStorageKey, File file);

    /**
     * This should be treated as immutable - never write to a file returned from this method.
     * That could be enforced by making our own class with no write methods, that only allows reading the file.
     */
    File getFile(FileStorageKey fileStorageKey);

    /**
     * Get the URL for the File located at the FileStorageKey. This can be a file:// URL when running locally or a URL
     * available on the web generated by S3.
     */
    String getURL(FileStorageKey fileStorageKey);

    /**
     * Delete the File located at the FileStorageKey.
     */
    void delete(FileStorageKey fileStorageKey);

    /**
     * When a new server is spun up there will be no local files. In instances where we derive files from other files
     * (ex: creating Tiffs from Grids) and if they are already created we only need to return a download URL and therefore
     * not need to retrieve the file at all, it would be useful to check if the file exists in the FileStorage without
     * actually retrieving it.
     */
    boolean exists(FileStorageKey fileStorageKey);
}
