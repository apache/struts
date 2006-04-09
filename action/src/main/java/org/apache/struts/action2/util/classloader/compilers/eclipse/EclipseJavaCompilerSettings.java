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
package org.apache.struts.action2.util.classloader.compilers.eclipse;

import org.apache.struts.action2.util.classloader.compilers.JavaCompilerSettings;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

import java.util.HashMap;
import java.util.Map;


public class EclipseJavaCompilerSettings implements JavaCompilerSettings {
    private final Map map = new HashMap();

    public EclipseJavaCompilerSettings() {
        map.put(CompilerOptions.OPTION_ReportMissingSerialVersion, CompilerOptions.IGNORE);
        map.put(CompilerOptions.OPTION_LineNumberAttribute, CompilerOptions.GENERATE);
        map.put(CompilerOptions.OPTION_SourceFileAttribute, CompilerOptions.GENERATE);
        map.put(CompilerOptions.OPTION_ReportDeprecation, CompilerOptions.IGNORE);
        map.put(CompilerOptions.OPTION_ReportUnusedImport, CompilerOptions.IGNORE);
        map.put(CompilerOptions.OPTION_Encoding, "UTF-8");
        map.put(CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.GENERATE);
        map.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_5);
        map.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_5); 
    }

    public Map getMap() {
        return map;
    }
}
