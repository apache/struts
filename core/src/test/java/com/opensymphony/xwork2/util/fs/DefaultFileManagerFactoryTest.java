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

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Scope;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DefaultFileManagerFactoryTest extends XWorkTestCase {

    static FileManager fileManager;

    public void testCreateDefaultFileManager() throws Exception {
        // given
        fileManager = null;
        DefaultFileManagerFactory factory = new DefaultFileManagerFactory();
        factory.setFileManager(new DefaultFileManager());
        factory.setContainer(new DummyContainer());

        // when
        FileManager fm = factory.getFileManager();

        // then
        assertTrue(fm instanceof DefaultFileManager);
    }

    public void testCreateDummyFileManager() throws Exception {
        // given
        fileManager = new DummyFileManager();
        DefaultFileManagerFactory factory = new DefaultFileManagerFactory();
        factory.setFileManager(new DefaultFileManager());
        factory.setContainer(new DummyContainer());

        // when
        FileManager fm = factory.getFileManager();

        // then
        assertTrue(fm instanceof DummyFileManager);
    }

    public void testFileManagerFactoryWithRealConfig() throws Exception {
        // given
        DefaultFileManagerFactory factory = new DefaultFileManagerFactory();
        container.inject(factory);

        // when
        FileManager fm = factory.getFileManager();

        // then
        assertTrue(fm instanceof DefaultFileManager);
    }
}

class DummyContainer implements Container {
    public void inject(Object o) {
    }

    public <T> T inject(Class<T> implementation) {
        return null;
    }

    public <T> T getInstance(Class<T> type, String name) {
        if ("dummy".equals(name)) {
            return (T) DefaultFileManagerFactoryTest.fileManager;
        }
        return null;
    }

    public <T> T getInstance(Class<T> type) {
        return null;
    }

    public Set<String> getInstanceNames(Class<?> type) {
        if (DefaultFileManagerFactoryTest.fileManager != null) {
            return new HashSet<String>() {
                {
                    add("dummy");
                }
            };
        }
        return Collections.emptySet();
    }

    public void setScopeStrategy(Scope.Strategy scopeStrategy) {
    }

    public void removeScopeStrategy() {
    }

}

class DummyFileManager implements FileManager {

    public void setReloadingConfigs(boolean reloadingConfigs) {
    }

    public boolean fileNeedsReloading(String fileName) {
        return false;
    }

    public boolean fileNeedsReloading(URL fileUrl) {
        return false;
    }

    public InputStream loadFile(URL fileUrl) {
        return null;
    }

    public void monitorFile(URL fileUrl) {
    }

    public URL normalizeToFileProtocol(URL url) {
        return null;
    }

    public boolean support() {
        return true;
    }

    public boolean internal() {
        return true;
    }

    public Collection<? extends URL> getAllPhysicalUrls(URL url) throws IOException {
        return null;
    }
}

