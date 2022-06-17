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
package org.apache.struts2.tiles;

import org.apache.tiles.request.locale.PostfixedApplicationResource;
import org.apache.tiles.request.locale.URLApplicationResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class StrutsApplicationResource extends PostfixedApplicationResource {

    private final URL url;
    private final File file;

    /**
     * fixes WW-5011
     * @return file path for "file" protocol elsewhere url path as before to keep backward-compatibility
     * @see URLApplicationResource#getFile(URL)
     */
    private static String getFilePath(URL url) {
        String path = url.getPath();
        if (!"file".equals(url.getProtocol())) {
            return path;
        }
        try {
            // fixes WW-5011 because includes ref in path - like URLApplicationResource#getFile(URL)
            path = (new URI(url.toExternalForm())).getSchemeSpecificPart();
        } catch (Exception e) {
            // fallback solution
            if (url.getRef() != null && !new File(path).exists()) {
                // it's like WW-5011
                path += "#" + url.getRef();
            }
        }

        return path;
    }

    public StrutsApplicationResource(URL url) {
        super(getFilePath(url));
        this.url = url;
        this.file = new File(getFilePath(url));
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return url.openStream();
    }

    @Override
    public long getLastModified() throws IOException {
        if (file.exists()) {
            return file.lastModified();
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Resource " + getLocalePath() + " at " + url.toString();
    }
}
