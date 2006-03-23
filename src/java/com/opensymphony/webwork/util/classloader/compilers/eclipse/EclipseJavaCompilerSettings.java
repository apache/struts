package com.opensymphony.webwork.util.classloader.compilers.eclipse;

import com.opensymphony.webwork.util.classloader.compilers.JavaCompilerSettings;
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
