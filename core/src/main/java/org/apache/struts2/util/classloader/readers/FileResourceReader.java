/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.util.classloader.readers;

import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 */
public final class FileResourceReader implements ResourceReader {

    private final File base;

    public FileResourceReader(final File pBase) {
        base = pBase;
    }

    public boolean isAvailable(String filename) {
        return new File(base, filename).exists();
    }

    public char[] getContent(final String fileName) {
        try {
            return FileUtils.readFileToString(
                    new File(base, fileName), "UTF-8").toCharArray();
        } catch (Exception e) {
        }
        return null;
    }
}
