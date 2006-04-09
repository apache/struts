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
package org.apache.struts.action2.util.classloader.monitor;

import java.io.File;

/**
 * @author tcurdt
 */
public interface FilesystemAlterationListener {
    void onStart(final File repository);

    void onCreateFile(final File file);

    void onChangeFile(final File file);

    void onDeleteFile(final File file);

    void onCreateDirectory(final File dir);

    void onChangeDirectory(final File dir);

    void onDeleteDirectory(final File dir);

    void onStop(final File repository);
}
