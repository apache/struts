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
     * @return original file name from upload source
     */
    String getOriginalName();

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

}
