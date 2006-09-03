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
package org.apache.struts2.util.classloader.listeners;

import org.apache.struts2.util.classloader.ReloadingClassLoader;
import org.apache.struts2.util.classloader.compilers.JavaCompiler;
import org.apache.struts2.util.classloader.monitor.FilesystemAlterationListener;
import org.apache.struts2.util.classloader.problems.CompilationProblem;
import org.apache.struts2.util.classloader.problems.DefaultCompilationProblemHandler;
import org.apache.struts2.util.classloader.problems.ConsoleCompilationProblemHandler;
import org.apache.struts2.util.classloader.readers.ResourceReader;
import org.apache.struts2.util.classloader.stores.TransactionalResourceStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


public class CompilingListener implements FilesystemAlterationListener {

    private final static Log log = LogFactory.getLog(CompilingListener.class);

    private final Collection created = new ArrayList();
    private final Collection changed = new ArrayList();
    private final Collection deleted = new ArrayList();
    File pRepository;

    private final JavaCompiler compiler;
    private final ResourceReader reader;
    private final TransactionalResourceStore transactionalStore;
    private final DefaultCompilationProblemHandler problemHandler = new ConsoleCompilationProblemHandler();

    public CompilingListener(
            final ResourceReader pReader,
            final JavaCompiler pCompiler,
            final TransactionalResourceStore pTransactionalStore
    ) {
        compiler = pCompiler;
        reader = pReader;
        transactionalStore = pTransactionalStore;
    }

    public DefaultCompilationProblemHandler getCompilationProblemHandler() {
        return problemHandler;
    }

    public void onStart(final File pRepository) {
        this.pRepository = pRepository;
        created.clear();
        changed.clear();
        deleted.clear();
        transactionalStore.onStart();
    }

    public void onStop(final File pRepository) {
        this.pRepository = pRepository;
        log.debug("resources " +
                created.size() + " created, " +
                changed.size() + " changed, " +
                deleted.size() + " deleted");

        boolean reload = false;

        if (deleted.size() > 0) {
            for (Iterator it = deleted.iterator(); it.hasNext();) {
                final File file = (File) it.next();
                transactionalStore.remove(ReloadingClassLoader.clazzName(pRepository, file));
            }
            reload = true;
        }

        final Collection compileables = new ArrayList();
        compileables.addAll(created);
        compileables.addAll(changed);

        final String[] clazzes = new String[compileables.size()];

        if (compileables.size() > 0) {

            int i = 0;
            for (Iterator it = compileables.iterator(); it.hasNext();) {
                final File file = (File) it.next();
                clazzes[i] = ReloadingClassLoader.clazzName(pRepository, file);
                //log.debug(clazzes[i]);
                i++;
            }

            compiler.compile(
                    clazzes,
                    reader,
                    transactionalStore,
                    problemHandler
            );

            final CompilationProblem[] errors = problemHandler.getErrors();
            final CompilationProblem[] warnings = problemHandler.getWarnings();

            log.debug(
                    errors.length + " errors, " +
                            warnings.length + " warnings"
            );

            if (errors.length > 0) {
                for (int j = 0; j < clazzes.length; j++) {
                    transactionalStore.remove(clazzes[j]);
                }
            }

            reload = true;

        }

        transactionalStore.onStop();

        if (reload) {
            reload();
        }
    }

    public void onCreateFile(final File file) {
        if (file.getName().endsWith(".java")) {
            created.add(file);
        }
    }

    public void onChangeFile(final File file) {
        if (file.getName().endsWith(".java")) {
            changed.add(file);
        }
    }

    public void onDeleteFile(final File file) {
        if (file.getName().endsWith(".java")) {
            deleted.add(file);
        }
    }

    public void onCreateDirectory(final File file) {
    }

    public void onChangeDirectory(final File file) {
    }

    public void onDeleteDirectory(final File file) {
    }

    protected void reload() {
        log.debug("reload");
    }
}
