package org.apache.struts2.dispatcher.multipart;

import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.struts2.StrutsConstants;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Multi-part form data request adapter for Jakarta Commons FileUpload package that
 * leverages the streaming API rather than the traditional non-streaming API.
 *
 * For more details see WW-3025
 *
 * @author Chris Cranford
 * @since 2.3.18
 */
public class JakartaStreamMultiPartRequest implements MultiPartRequest {

    static final Logger LOG = LoggerFactory.getLogger(JakartaStreamMultiPartRequest.class);

    /**
     * Defines the internal buffer size used during streaming operations.
     */
    private static final int BUFFER_SIZE = 10240;

    /**
     * Map between file fields and file data.
     */
    private Map<String, List<FileInfo>> fileInfos = new HashMap<String, List<FileInfo>>();

    /**
     * Map between non-file fields and values.
     */
    private Map<String, List<String>> parameters = new HashMap<String, List<String>>();

    /**
     * Internal list of raised errors to be passed to the the Struts2 framework.
     */
    private List<String> errors = new ArrayList<String>();

    /**
     * Internal list of non-critical messages to be passed to the Struts2 framework.
     */
    private List<String> messages = new ArrayList<String>();

    /**
     * Specifies the maximum size of the entire request.
     */
    private Long maxSize;

    /**
     * Specifies the buffer size to use during streaming.
     */
    private int bufferSize = BUFFER_SIZE;

    /**
     * Localization to be used regarding errors.
     */
    private Locale defaultLocale = Locale.ENGLISH;

    /**
     * Injects the Struts multiple part maximum size.
     *
     * @param maxSize
     */
    @Inject(StrutsConstants.STRUTS_MULTIPART_MAXSIZE)
    public void setMaxSize(String maxSize) {
        this.maxSize = Long.parseLong(maxSize);
    }

    /**
     * Sets the buffer size to be used.
     *
     * @param bufferSize
     */
    @Inject(value = StrutsConstants.STRUTS_MULTIPART_BUFFERSIZE, required = false)
    public void setBufferSize(String bufferSize) {
        this.bufferSize = Integer.parseInt(bufferSize);
    }

    /**
     * Injects the Struts locale provider.
     *
     * @param provider
     */
    @Inject
    public void setLocaleProvider(LocaleProvider provider) {
        defaultLocale = provider.getLocale();
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#cleanUp()
     */
    public void cleanUp() {
        LOG.debug("Performing File Upload temporary storage cleanup.");
        for (String fieldName : fileInfos.keySet()) {
            for (FileInfo fileInfo : fileInfos.get(fieldName)) {
                File file = fileInfo.getFile();
                LOG.debug("Deleting file '#0'.", file.getName());
                if (!file.delete())
                    LOG.warn("There was a problem attempting to delete file '#0'.", file.getName());
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getContentType(java.lang.String)
     */
    public String[] getContentType(String fieldName) {
        List<FileInfo> infos = fileInfos.get(fieldName);
        if (infos == null)
            return null;

        List<String> types = new ArrayList<String>(infos.size());
        for (FileInfo fileInfo : infos)
            types.add(fileInfo.getContentType());

        return types.toArray(new String[types.size()]);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getErrors()
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Allows interceptor to fetch non-critical messages that can be passed to the action.
     *
     * @return
     */
    public List<String> getMesssages() {
        return messages;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFile(java.lang.String)
     */
    public File[] getFile(String fieldName) {
        List<FileInfo> infos = fileInfos.get(fieldName);
        if (infos == null)
            return null;

        List<File> files = new ArrayList<File>(infos.size());
        for (FileInfo fileInfo : infos)
            files.add(fileInfo.getFile());

        return files.toArray(new File[files.size()]);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFileNames(java.lang.String)
     */
    public String[] getFileNames(String fieldName) {
        List<FileInfo> infos = fileInfos.get(fieldName);
        if (infos == null)
            return null;

        List<String> names = new ArrayList<String>(infos.size());
        for (FileInfo fileInfo : infos)
            names.add(getCanonicalName(fileInfo.getOriginalName()));

        return names.toArray(new String[names.size()]);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFileParameterNames()
     */
    public Enumeration<String> getFileParameterNames() {
        return Collections.enumeration(fileInfos.keySet());
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFilesystemName(java.lang.String)
     */
    public String[] getFilesystemName(String fieldName) {
        List<FileInfo> infos = fileInfos.get(fieldName);
        if (infos == null)
            return null;

        List<String> names = new ArrayList<String>(infos.size());
        for (FileInfo fileInfo : infos)
            names.add(fileInfo.getFile().getName());

        return names.toArray(new String[names.size()]);
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameter(java.lang.String)
     */
    public String getParameter(String name) {
        List<String> values = parameters.get(name);
        if (values != null && values.size() > 0)
            return values.get(0);
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameterNames()
     */
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameterValues(java.lang.String)
     */
    public String[] getParameterValues(String name) {
        List<String> values = parameters.get(name);
        if (values != null && values.size() > 0)
            return values.toArray(new String[values.size()]);
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#parse(javax.servlet.http.HttpServletRequest, java.lang.String)
     */
    public void parse(HttpServletRequest request, String saveDir)
            throws IOException {
        try {
            setLocale(request);
            processUpload(request, saveDir);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = buildErrorMessage(e, new Object[]{});
            if (!errors.contains(errorMessage))
                errors.add(errorMessage);
        }
    }

    /**
     * Inspect the servlet request and set the locale if one wasn't provided by
     * the Struts2 framework.
     *
     * @param request
     */
    protected void setLocale(HttpServletRequest request) {
        if (defaultLocale == null)
            defaultLocale = request.getLocale();
    }

    /**
     * Processes the upload.
     *
     * @param request
     * @param saveDir
     * @throws Exception
     */
    private void processUpload(HttpServletRequest request, String saveDir)
            throws Exception {

        // Sanity check that the request is a multi-part/form-data request.
        if (ServletFileUpload.isMultipartContent(request)) {

            // Sanity check on request size.
            boolean requestSizePermitted = isRequestSizePermitted(request);

            // Interface with Commons FileUpload API
            // Using the Streaming API
            ServletFileUpload servletFileUpload = new ServletFileUpload();
            FileItemIterator i = servletFileUpload.getItemIterator(request);

            // Iterate the file items
            while (i.hasNext()) {
                try {
                    FileItemStream itemStream = i.next();

                    // If the file item stream is a form field, delegate to the
                    // field item stream handler
                    if (itemStream.isFormField()) {
                        processFileItemStreamAsFormField(itemStream);
                    }

                    // Delegate the file item stream for a file field to the
                    // file item stream handler, but delegation is skipped
                    // if the requestSizePermitted check failed based on the
                    // complete content-size of the request.
                    else {

                        // prevent processing file field item if request size not allowed.
                        // also warn user in the logs.
                        if (!requestSizePermitted) {
                            addFileSkippedError(itemStream.getName(), request);
                            LOG.warn("Skipped stream '#0', request maximum size (#1) exceeded.", itemStream.getName(), maxSize);
                            continue;
                        }

                        processFileItemStreamAsFileField(itemStream, saveDir);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Defines whether the request allowed based on content length.
     *
     * @param request
     * @return
     */
    private boolean isRequestSizePermitted(HttpServletRequest request) {
        // if maxSize is specified as -1, there is no sanity check and it's
        // safe to return true for any request, delegating the failure
        // checks later in the upload process.
        if (maxSize == -1 || request == null)
            return true;

        return request.getContentLength() < maxSize;
    }

    /**
     * Get the request content length.
     *
     * @param request
     * @return
     */
    private long getRequestSize(HttpServletRequest request) {
        long requestSize = 0;
        if (request != null)
            requestSize = request.getContentLength();
        return requestSize;
    }

    /**
     * Add a file skipped message notification for action messages.
     *
     * @param fileName
     * @param request
     */
    private void addFileSkippedError(String fileName, HttpServletRequest request) {
        String exceptionMessage = "Skipped file " + fileName + "; request size limit exceeded.";
        FileSizeLimitExceededException exception = new FileUploadBase.FileSizeLimitExceededException(exceptionMessage, getRequestSize(request), maxSize);
        String message = buildErrorMessage(exception, new Object[]{fileName, getRequestSize(request), maxSize});
        if (!errors.contains(message))
            errors.add(message);
    }

    /**
     * Processes the FileItemStream as a Form Field.
     *
     * @param itemStream
     */
    private void processFileItemStreamAsFormField(FileItemStream itemStream) {
        String fieldName = itemStream.getFieldName();
        try {
            List<String> values = null;
            String fieldValue = Streams.asString(itemStream.openStream());
            if (!parameters.containsKey(fieldName)) {
                values = new ArrayList<String>();
                parameters.put(fieldName, values);
            } else {
                values = parameters.get(fieldName);
            }
            values.add(fieldValue);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.warn("Failed to handle form field '#0'.", fieldName);
        }
    }

    /**
     * Processes the FileItemStream as a file field.
     *
     * @param itemStream
     * @param location
     */
    private void processFileItemStreamAsFileField(FileItemStream itemStream, String location) {
        // Skip file uploads that don't have a file name - meaning that no file was selected.
        if (itemStream.getName() == null || itemStream.getName().trim().length() < 1) {
            LOG.debug("No file has been uploaded for the field: {}", itemStream.getFieldName());
            return;
        }

        File file = null;
        try {
            // Create the temporary upload file.
            file = createTemporaryFile(itemStream.getName(), location);

            if (streamFileToDisk(itemStream, file))
                createFileInfoFromItemStream(itemStream, file);
        } catch (IOException e) {
            if (file != null) {
                try {
                    file.delete();
                } catch (SecurityException se) {
                    se.printStackTrace();
                    LOG.warn("Failed to delete '#0' due to security exception above.", file.getName());
                }
            }
        }
    }

    /**
     * Creates a temporary file based on the given filename and location.
     *
     * @param fileName
     * @param location
     * @return
     * @throws IOException
     */
    private File createTemporaryFile(String fileName, String location)
            throws IOException {
        String name = fileName
                .substring(fileName.lastIndexOf('/') + 1)
                .substring(fileName.lastIndexOf('\\') + 1);

        String prefix = name;
        String suffix = "";

        if (name.contains(".")) {
            prefix = name.substring(0, name.lastIndexOf('.'));
            suffix = name.substring(name.lastIndexOf('.'));
        }

        if (prefix.length() < 3) {
            prefix = UUID.randomUUID().toString();
        }

        File file = File.createTempFile(prefix + "_", suffix, new File(location));
        LOG.debug("Creating temporary file '#0' (originally '#1').", file.getName(), fileName);
        return file;
    }

    /**
     * Streams the file upload stream to the specified file.
     *
     * @param itemStream
     * @param file
     * @return
     * @throws IOException
     */
    private boolean streamFileToDisk(FileItemStream itemStream, File file) throws IOException {
        boolean result = false;
        InputStream input = itemStream.openStream();
        OutputStream output = null;
        try {
            output = new BufferedOutputStream(new FileOutputStream(file), bufferSize);
            byte[] buffer = new byte[bufferSize];
            LOG.debug("Streaming file using buffer size #0.", bufferSize);
            for (int length = 0; ((length = input.read(buffer)) > 0); )
                output.write(buffer, 0, length);
            result = true;
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * Creates an internal <code>FileInfo</code> structure used to pass information
     * to the <code>FileUploadInterceptor</code> during the interceptor stack
     * invocation process.
     *
     * @param itemStream
     * @param file
     */
    private void createFileInfoFromItemStream(FileItemStream itemStream, File file) {
        // gather attributes from file upload stream.
        String fileName = itemStream.getName();
        String fieldName = itemStream.getFieldName();
        // create internal structure
        FileInfo fileInfo = new FileInfo(file, itemStream.getContentType(), fileName);
        // append or create new entry.
        if (!fileInfos.containsKey(fieldName)) {
            List<FileInfo> infos = new ArrayList<FileInfo>();
            infos.add(fileInfo);
            fileInfos.put(fieldName, infos);
        } else {
            fileInfos.get(fieldName).add(fileInfo);
        }
    }

    /**
     * Get the canonical name based on the supplied filename.
     *
     * @param fileName
     * @return
     */
    private String getCanonicalName(String fileName) {
        int forwardSlash = fileName.lastIndexOf("/");
        int backwardSlash = fileName.lastIndexOf("\\");
        if (forwardSlash != -1 && forwardSlash > backwardSlash) {
            fileName = fileName.substring(forwardSlash + 1, fileName.length());
        } else {
            fileName = fileName.substring(backwardSlash + 1, fileName.length());
        }
        return fileName;
    }

    /**
     * Build error message.
     *
     * @param e
     * @param args
     * @return
     */
    private String buildErrorMessage(Throwable e, Object[] args) {
        String errorKey = "struts.message.upload.error." + e.getClass().getSimpleName();
        if (LOG.isDebugEnabled())
            LOG.debug("Preparing error message for key: [#0]", errorKey);
        if (LocalizedTextUtil.findText(this.getClass(), errorKey, defaultLocale, null, new Object[0]) == null) {
            return LocalizedTextUtil.findText(this.getClass(), "struts.messages.error.uploading", defaultLocale, null, new Object[] { e.getMessage() });
        } else {
            return LocalizedTextUtil.findText(this.getClass(), errorKey, defaultLocale, null, args);
        }
    }

    /**
     * Build action message.
     *
     * @param e
     * @param args
     * @return
     */
    private String buildMessage(Throwable e, Object[] args) {
        String messageKey = "struts.message.upload.message." + e.getClass().getSimpleName();
        if (LOG.isDebugEnabled())
            LOG.debug("Preparing message for key: [#0]", messageKey);
        return LocalizedTextUtil.findText(this.getClass(), messageKey, defaultLocale, e.getMessage(), args);
    }

    /**
     * Internal data structure used to store a reference to information needed
     * to later pass post processing data to the <code>FileUploadInterceptor</code>.
     *
     * @version $Revision$
     * @since 7.0.0
     */
    private static class FileInfo implements Serializable {

        private static final long serialVersionUID = 1083158552766906037L;

        private File file;
        private String contentType;
        private String originalName;

        /**
         * Default constructor.
         *
         * @param file
         * @param contentType
         * @param originalName
         */
        public FileInfo(File file, String contentType, String originalName) {
            this.file = file;
            this.contentType = contentType;
            this.originalName = originalName;
        }

        /**
         * @return
         */
        public File getFile() {
            return file;
        }

        /**
         * @return
         */
        public String getContentType() {
            return contentType;
        }

        /**
         * @return
         */
        public String getOriginalName() {
            return originalName;
        }
    }

}
