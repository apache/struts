/*
 * Copyright 1999-2004 The Apache Software Foundation.
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
package com.opensymphony.webwork.util.classloader;

import com.opensymphony.webwork.util.classloader.compilers.JavaCompiler;
import com.opensymphony.webwork.util.classloader.compilers.eclipse.EclipseJavaCompiler;
import com.opensymphony.webwork.util.classloader.listeners.CompilingListener;
import com.opensymphony.webwork.util.classloader.monitor.FilesystemAlterationMonitor;
import com.opensymphony.webwork.util.classloader.stores.MemoryResourceStore;
import com.opensymphony.webwork.util.classloader.stores.TransactionalResourceStore;
import com.uwyn.rife.continuations.ClassByteAware;

import java.io.File;

/**
 * @author tcurdt
 */
public class CompilingClassLoader extends ReloadingClassLoader implements ClassByteAware {

    private final TransactionalResourceStore transactionalStore;
    private final JavaCompiler compiler;

    public CompilingClassLoader(final ClassLoader pParent, final File pRepository) {
        this(pParent, pRepository, new TransactionalResourceStore(
                new MemoryResourceStore()) {
            public void onStart() {
            }

            ;

            public void onStop() {
            }

            ;
        }
        );
    }

    public CompilingClassLoader(final ClassLoader pParent, final File pRepository, final TransactionalResourceStore pStore) {
        this(pParent, pRepository, pStore, new EclipseJavaCompiler());
    }

    public CompilingClassLoader(final ClassLoader pParent, final File pRepository,
                                final TransactionalResourceStore pStore, final JavaCompiler pCompiler) {
        super(pParent, pRepository, pStore);
        transactionalStore = pStore;
        compiler = pCompiler;
    }

    public byte[] getClassBytes(String classname) throws ClassNotFoundException {
        return transactionalStore.read(classname);
    }

    public void start() {
        fam = new FilesystemAlterationMonitor();

        // FIXME keep reference for accessing errors/warnings
        listener = new CompilingListener(
                reader,
                compiler,
                transactionalStore
        ) {
            public void reload() {
                super.reload();
                CompilingClassLoader.this.reload();
            }
        };

        fam.addListener(listener, repository);
        thread = new Thread(fam);
        fam.doRun();
        thread.start();
    }
}
