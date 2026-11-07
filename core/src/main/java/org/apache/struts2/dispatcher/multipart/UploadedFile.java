/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.dispatcher.multipart;

import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Virtual representation of an uploaded file used by {@link MultiPartRequest}
 */
public interface UploadedFile extends Serializable {

    /**
     * @return size of the content of file/stream/array
     */
    Long length();

    /**
     * @return a local name of the file
     */
    String getName();

    /**
     * The raw file name submitted by the client in the multipart {@code Content-Disposition}
     * header -- <strong>untrusted input</strong>. It is not sanitised or path-stripped by the
     * framework, may contain directory-traversal sequences ({@code ../}), drive/UNC prefixes,
     * or other path metacharacters, and must not be used directly to build a filesystem path.
     * The framework itself never does so: the local, on-disk copy is named from
     * {@link #getName()}, an internally generated (UUID-based) name. Use
     * {@link #getSanitizedOriginalName()} for a basename-only variant suitable for display or
     * for building a path under an application-controlled directory.
     *
     * @return original file name from upload source, exactly as the client supplied it
     */
    String getOriginalName();

    /**
     * {@link #getOriginalName()} with any directory/drive component stripped, leaving only the
     * final path segment (equivalent to {@code FilenameUtils.getName(getOriginalName())}). Still
     * client-controlled and not otherwise validated -- it may still contain characters unsafe
     * for a particular filesystem, or be empty -- but it cannot carry a {@code ../} or an
     * absolute/UNC path component into a caller that naively concatenates it onto a directory.
     * Restores, as a default method available to every {@link UploadedFile} implementation, the
     * path-stripping behaviour the pre-7.0 {@code getFileNames()}/{@code getCanonicalName()}
     * accessors on {@code MultiPartRequest} provided; that pair has no caller in the current
     * action-based upload flow ({@code ActionFileUploadInterceptor} hands actions
     * {@link #getOriginalName()} directly, unsanitised).
     *
     * @return the last path segment of {@link #getOriginalName()}
     * @since 7.5.0
     */
    default String getSanitizedOriginalName() {
        return FilenameUtils.getName(getOriginalName());
    }

    /**
     * @return indicates if this is a real file or maybe just in-memory stream
     */
    boolean isFile();

    /**
     * @return removes a local copy of the uploaded file/stream
     */
    boolean delete();

    /**
     * @return an absolute path of the file if possible
     */
    String getAbsolutePath();

    /**
     * @return content of the upload file
     */
    Object getContent();

    /**
     * @return content type of the uploaded file
     */
    String getContentType();

    /**
     * Represents a name of the input file, eg.:
     * "myFile" in case of <input type="file" name="myFile">
     *
     * @return name of the input file field
     * @since 6.7.0
     */
    String getInputName();

    /**
     * Streams the uploaded content without forcing it to disk. Implementations backed by
     * in-memory bytes can return the bytes directly; file-backed implementations stream the
     * file. The default reads whatever {@link #getContent()} exposes.
     *
     * @return an input stream over the uploaded content
     * @throws IOException if the content cannot be read
     * @since 7.3.0
     */
    default InputStream getInputStream() throws IOException {
        Object content = getContent();
        if (content instanceof File file) {
            return new FileInputStream(file);
        }
        if (content instanceof byte[] bytes) {
            return new ByteArrayInputStream(bytes);
        }
        throw new IOException("No content stream available for " + getName());
    }

    /**
     * Indicates whether this upload has no content available (for example, the upload failed).
     * Implementations that hold their content in memory should override this to answer WITHOUT
     * materializing the content to disk. The default reports missing when {@link #getContent()}
     * is {@code null}.
     *
     * @return true if there is no content backing this upload
     * @since 7.3.0
     */
    default boolean isMissing() {
        return getContent() == null;
    }

}
