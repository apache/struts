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
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;

import java.util.HashSet;
import java.util.Set;

/**
 * Default implementation
 */
public class DefaultFileManagerFactory implements FileManagerFactory {

    private static final Logger LOG = LogManager.getLogger(DefaultFileManagerFactory.class);

    private boolean reloadingConfigs;
    private FileManagerHolder fileManagerHolder;
    private FileManager systemFileManager;
    private Container container;

    @Inject(value = "system")
    public void setFileManager(FileManager fileManager) {
        this.systemFileManager = fileManager;
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject(value = StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, required = false)
    public void setReloadingConfigs(String reloadingConfigs) {
        this.reloadingConfigs = Boolean.parseBoolean(reloadingConfigs);
    }

    public FileManager getFileManager() {
        if (fileManagerHolder != null) {
            return fileManagerHolder.getFileManager();
        }

        FileManager fileManager = lookupFileManager();
        if (fileManager != null) {
            LOG.debug("Using FileManager implementation [{}]", fileManager.getClass().getSimpleName());
            fileManager.setReloadingConfigs(reloadingConfigs);
            fileManagerHolder = new FileManagerHolder(fileManager);
            return fileManager;
        }

        LOG.debug("Using default implementation of FileManager provided under name [system]: {}", systemFileManager.getClass().getSimpleName());
        systemFileManager.setReloadingConfigs(reloadingConfigs);
        fileManagerHolder = new FileManagerHolder(systemFileManager);
        return systemFileManager;
    }

    private FileManager lookupFileManager() {
        Set<String> names = container.getInstanceNames(FileManager.class);
        LOG.debug("Found following implementations of FileManager interface: {}", names);
        Set<FileManager> internals = new HashSet<>();
        Set<FileManager> users = new HashSet<>();
        for (String fmName : names) {
            FileManager fm = container.getInstance(FileManager.class, fmName);
            if (fm.internal()) {
                internals.add(fm);
            } else {
                users.add(fm);
            }
        }
        for (FileManager fm : users) {
            if (fm.support()) {
                LOG.debug("Using FileManager implementation [{}]", fm.getClass().getSimpleName());
                return fm;
            }
        }
        LOG.debug("No user defined FileManager, looking up for internal implementations!");
        for (FileManager fm : internals) {
            if (fm.support()) {
                return fm;
            }
        }
        return null;
    }

    private static class FileManagerHolder {

        private final FileManager fileManager;

        public FileManagerHolder(FileManager fileManager) {
            this.fileManager = fileManager;
        }

        public FileManager getFileManager() {
            return fileManager;
        }
    }

}
