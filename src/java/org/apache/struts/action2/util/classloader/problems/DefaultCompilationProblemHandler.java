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
package org.apache.struts.action2.util.classloader.problems;

import java.util.ArrayList;
import java.util.Collection;


public class DefaultCompilationProblemHandler implements CompilationProblemHandler {

    final Collection errors = new ArrayList();
    final Collection warnings = new ArrayList();

    public void onStart() {
        errors.clear();
        warnings.clear();
    }

    public void handle(final CompilationProblem pProblem) {
        if (pProblem.isError()) {
            errors.add(pProblem);
        } else {
            warnings.add(pProblem);
        }
    }

    public void onStop() {
    }

    public CompilationProblem[] getErrors() {
        final CompilationProblem[] result = new CompilationProblem[errors.size()];
        errors.toArray(result);
        return result;
    }

    public CompilationProblem[] getWarnings() {
        final CompilationProblem[] result = new CompilationProblem[warnings.size()];
        warnings.toArray(result);
        return result;
    }
}
