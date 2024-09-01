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
package org.apache.struts2.config;

import org.apache.struts2.FileManager;
import org.apache.struts2.inject.ContainerBuilder;
import org.apache.struts2.inject.Scope;
import org.apache.struts2.util.location.LocatableProperties;

/**
 * Allows to specify custom {@link FileManager} by user
 */
public class FileManagerProvider implements ContainerProvider {

    private final Class<? extends FileManager> fileManagerClass;
    private final String name;

    public FileManagerProvider(Class<? extends FileManager> fileManagerClass, String name) {
        this.fileManagerClass = fileManagerClass;
        this.name = name;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(Configuration configuration) throws ConfigurationException {
    }

    @Override
    public boolean needsReload() {
        return false;
    }

    @Override
    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
        builder.factory(FileManager.class, name, fileManagerClass, Scope.SINGLETON);
    }

}
