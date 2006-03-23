package com.opensymphony.webwork.plugin.idea;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * User: patrick
 * Date: Oct 4, 2005
 * Time: 11:28:50 AM
 */
public class FreeMarkerFileType extends LanguageFileType {
    protected FreeMarkerFileType() {
        super(new FreeMarkerLanguage());
    }

    @NotNull
    public String getName() {
        return "FreeMarker";
    }

    @NotNull
    public String getDescription() {
        return "FreeMarker file type";
    }

    @NotNull
    public String getDefaultExtension() {
        return "ftl";
    }

    @Nullable
    public Icon getIcon() {
        return null;
    }
}
