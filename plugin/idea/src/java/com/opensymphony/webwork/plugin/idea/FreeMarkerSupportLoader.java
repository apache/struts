package com.opensymphony.webwork.plugin.idea;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NonNls;

/**
 * User: patrick
 * Date: Oct 4, 2005
 * Time: 1:00:14 PM
 */
public class FreeMarkerSupportLoader implements ApplicationComponent {
    public FreeMarkerSupportLoader() {
        System.out.println("Hi!");
    }

    @NonNls
    public String getComponentName() {
        return "FreeMarker Support";
    }

    public void initComponent() {
        ApplicationManager.getApplication().runWriteAction(
          new Runnable() {
            public void run() {
              FileTypeManager.getInstance().registerFileType(new FreeMarkerFileType(), new String[] {"ftl"});
            }
          }
        );
    }

    public void disposeComponent() {
    }
}
