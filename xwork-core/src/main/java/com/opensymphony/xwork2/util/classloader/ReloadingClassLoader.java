/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.util.classloader;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.lang3.ObjectUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The ReloadingClassLoader uses a delegation mechanism to allow
 * classes to be reloaded. That means that loadClass calls may
 * return different results if the class was changed in the underlying
 * ResourceStore.
 * <p/>
 * class taken from Apache JCI
 */
public class ReloadingClassLoader extends ClassLoader {
    private static final Logger LOG = LoggerFactory.getLogger(ReloadingClassLoader.class);
    private final ClassLoader parent;
    private ResourceStore[] stores;
    private ClassLoader delegate;

    private Set<Pattern> acceptClasses = Collections.emptySet();

    public ReloadingClassLoader(final ClassLoader pParent) {
        super(pParent);
        parent = pParent;
        URL parentRoot = pParent.getResource("");
        FileManager fileManager = ActionContext.getContext().getInstance(FileManagerFactory.class).getFileManager();
        URL root = fileManager.normalizeToFileProtocol(parentRoot);
        root = ObjectUtils.defaultIfNull(root, parentRoot);
        try {
            if (root != null) {
                stores = new ResourceStore[]{new FileResourceStore(new File(root.toURI()))};
            } else {
                throw new XWorkException("Unable to start the reloadable class loader, consider setting 'struts.convention.classes.reload' to false");
            }
        } catch (URISyntaxException e) {
            throw new XWorkException("Unable to start the reloadable class loader, consider setting 'struts.convention.classes.reload' to false", e);
        } catch (RuntimeException e) {
            // see WW-3121
            // TODO: Fix this for a reloading mechanism to be marked as stable
            if (root != null)
                LOG.error("Exception while trying to build the ResourceStore for URL [#0]", e, root.toString());
            else
                LOG.error("Exception while trying to get root resource from class loader", e);
            LOG.error("Consider setting struts.convention.classes.reload=false");
            throw e;
        }

        delegate = new ResourceStoreClassLoader(parent, stores);
    }

    public boolean addResourceStore(final ResourceStore pStore) {
        try {
            final int n = stores.length;
            final ResourceStore[] newStores = new ResourceStore[n + 1];
            System.arraycopy(stores, 0, newStores, 1, n);
            newStores[0] = pStore;
            stores = newStores;
            delegate = new ResourceStoreClassLoader(parent, stores);
            return true;
        } catch (final RuntimeException e) {
            LOG.error("Could not add resource store", e);
        }
        return false;
    }

    public boolean removeResourceStore(final ResourceStore pStore) {

        final int n = stores.length;
        int i = 0;

        // FIXME: this should be improved with a Map
        // find the pStore and index position with var i
        while ((i < n) && (stores[i] != pStore)) {
            i++;
        }

        // pStore was not found
        if (i == n) {
            return false;
        }

        // if stores length > 1 then array copy old values, else create new empty store
        final ResourceStore[] newStores = new ResourceStore[n - 1];
        if (i > 0) {
            System.arraycopy(stores, 0, newStores, 0, i);
        }
        if (i < n - 1) {
            System.arraycopy(stores, i + 1, newStores, i, (n - i - 1));
        }

        stores = newStores;
        delegate = new ResourceStoreClassLoader(parent, stores);
        return true;
    }

    public void reload() {
        if (LOG.isTraceEnabled())
            LOG.trace("Reloading class loader");
        delegate = new ResourceStoreClassLoader(parent, stores);
    }

    public void clearAssertionStatus() {
        delegate.clearAssertionStatus();
    }

    public URL getResource(String name) {
        return delegate.getResource(name);
    }

    public InputStream getResourceAsStream(String name) {
        return delegate.getResourceAsStream(name);
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        return isAccepted(name) ? delegate.loadClass(name) : parent.loadClass(name);
    }

    public void setClassAssertionStatus(String className, boolean enabled) {
        delegate.setClassAssertionStatus(className, enabled);
    }

    public void setDefaultAssertionStatus(boolean enabled) {
        delegate.setDefaultAssertionStatus(enabled);
    }

    public void setPackageAssertionStatus(String packageName, boolean enabled) {
        delegate.setPackageAssertionStatus(packageName, enabled);
    }

    public void setAccepClasses(Set<Pattern> acceptClasses) {
        this.acceptClasses = acceptClasses;
    }

    protected boolean isAccepted(String className) {
        if (!this.acceptClasses.isEmpty()) {
            for (Pattern pattern : acceptClasses) {
                Matcher matcher = pattern.matcher(className);
                if (matcher.matches()) {
                    return true;
                }
            }
            return false;
        } else
            return true;
    }
}

