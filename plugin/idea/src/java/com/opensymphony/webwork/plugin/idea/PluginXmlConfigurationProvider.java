package com.opensymphony.webwork.plugin.idea;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Result;
import com.opensymphony.xwork.config.entities.InterceptorConfig;
import com.opensymphony.xwork.config.providers.XmlConfigurationProvider;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: patrick
 * Date: Oct 4, 2005
 * Time: 3:03:01 PM
 */
public class PluginXmlConfigurationProvider extends XmlConfigurationProvider {
    Project project;

    public PluginXmlConfigurationProvider(Project project) {
        this.project = project;
    }

    protected boolean verifyAction(String className, String name) {
        return true;
    }

    protected boolean verifyInterceptor(String className, String name, InterceptorConfig config) {
        return true;
    }

    protected Class verifyResultType(String className) {
        return ResultHolder.class;
    }

    public static class ResultHolder implements Result {
        public void execute(ActionInvocation invocation) throws Exception {
            // do nothing, this is just a holder
        }
    }

    protected InputStream getInputStream(final String fileName) {
        final InputStream[] is = new InputStream[]{null};
        ProjectRootManager.getInstance(project).getFileIndex().iterateContent(new ContentIterator() {
            public boolean processFile(VirtualFile fileOrDir) {
                if (fileName.equals(fileOrDir.getName())) {
                    try {
                        is[0] = fileOrDir.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return false;
                }
                return true;
            }
        });

        if (is[0] == null) {
            // ok, let's search the classpath then...
            return super.getInputStream(fileName);
        }

        return is[0];
    }
}
