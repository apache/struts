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
package com.opensymphony.xwork2.util.fs;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Represents file resource revision, used for file://* resources
 */
public class FileRevision extends Revision {

    private File file;
    private long lastModified;

    public static Revision build(URL fileUrl) {
        File file;
        try {
            if (fileUrl != null) {
                file = new File(fileUrl.toURI());
            } else {
                return null;
            }
        } catch (URISyntaxException e) {
            file = new File(fileUrl.getPath());
        }  catch (Throwable t) {
            return null;
        }
        if (file.exists() && file.canRead()) {
            long lastModified = file.lastModified();
            return new FileRevision(file, lastModified);
        }
        return null;
    }

    private FileRevision(File file, long lastUpdated) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        this.file = file;
        this.lastModified = lastUpdated;
    }

    public File getFile() {
        return file;
    }

    public boolean needsReloading() {
        return this.lastModified < this.file.lastModified();
    }

}
