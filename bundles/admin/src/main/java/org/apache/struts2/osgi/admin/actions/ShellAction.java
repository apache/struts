/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.osgi.admin.actions;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.felix.shell.ShellService;
import org.apache.struts2.osgi.DefaultBundleAccessor;
import org.apache.struts2.osgi.interceptor.BundleContextAware;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * This action executes commands on the Felix Shell.
 * 
 * The action is BundleContextAware so that if the OSGi interceptor is used the BundleContext
 * can be provided for configurations where the DefaultBundleAccessor is insufficient.
 * 
 */
public class ShellAction extends ActionSupport implements BundleContextAware {
    private String command;
    private String output;
    private BundleContext bundleContext;

    @Override
    public String execute() {
        // get service
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errByteStream = new ByteArrayOutputStream();
        PrintStream outStream = new PrintStream(outByteStream);
        PrintStream errStream = new PrintStream(errByteStream);

        String outString = null;
        String errString = null;
        try {
            executeCommand(command, outStream, errStream);
            outString = outByteStream.toString().trim();
            errString = errByteStream.toString().trim();
        } catch (Exception e) {
            outString = outByteStream.toString().trim();
            errString = "Exception: " + e.toString() + " (" + e.getMessage() + ").  Output:" + outString +
                        ".  Error: " + errByteStream.toString().trim();  // Full details for troubleshooting.
        } finally {
            outStream.close();
            errStream.close();
        }

        output = errString != null && errString.length() > 0 ? errString : outString;
        return Action.SUCCESS;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getOutput() {
        return output;
    }

    public void executeCommand(String commandLine, PrintStream out, PrintStream err) throws Exception {
        ShellService shellService = getShellService(out);
        if (shellService != null) {
            out.println("Attempting to execute command: " + commandLine);
            shellService.executeCommand(commandLine, out, err);
        }
        else {
            err.println("Apache Felix Shell service is not installed");
        }
    }

    private ShellService getShellService(PrintStream out) {
        //bundle can be de-activated, so keeping a reference aorund is not a good idea
        final DefaultBundleAccessor bundleAccessor = DefaultBundleAccessor.getInstance();
        ServiceReference ref = (bundleAccessor != null ? bundleAccessor.getServiceReference(ShellService.class.getName()) : null);
        //out.println("DefaultBundleAccessor: "  + bundleAcessor + ", ServiceReference [" + ShellService.class.getName() + "]: " + ref);  // No logger, for debugging only.

        if (ref == null && this.bundleContext != null) {
            // Depending on OSGi and Felix bundle configurations, the DefaultBundleAccessor may not be able to locate the ShellService.
            // In such cases, use the bundleContext (if available) to locate the ShellService in a "brute-force" manner.
            final Bundle[] bundles = this.bundleContext.getBundles();
            if (bundles != null && bundles.length > 0) {
                for (Bundle currentBundle : bundles) {
                    if (currentBundle != null) {
                        //out.println("Bundle [" + index + "], SymbolicName: " + currentBundle.getSymbolicName() + ", Location: " + currentBundle.getLocation() + ", BundleID: " + currentBundle.getBundleId());  // No logger, for debugging only.
                        if (currentBundle.getSymbolicName().startsWith("org.apache.felix.shell")) {
                            BundleContext currentBundleContext = currentBundle.getBundleContext();
                            Object directShellServiceByClass = (currentBundleContext != null ? currentBundleContext.getServiceReference(org.apache.felix.shell.ShellService.class) : null);
                            Object directShellServiceByName = (currentBundleContext != null ? currentBundleContext.getServiceReference("org.apache.felix.shell.ShellService") : null);
                            //out.println("    ShellService reference (via bundle's context) by class: " + directShellServiceByClass);  // No logger, for debugging only.
                            //out.println("    ShellService reference (via bundle's context) by name: " + directShellServiceByName);    // No logger, for debugging only.
                            if (ref == null) {
                                ref = (directShellServiceByClass != null ? (ServiceReference) directShellServiceByClass : (ServiceReference) directShellServiceByName);
                            }
                        }
                    } else {
                        //out.println("Bundle [" + index + "] is null");  // No logger, for debugging only.
                    }
                }
            } else {
                //out.println("OSGi Interceptor-provided BundleContext bundle array is null or empty");  // No logger, for debugging only.
            }
        }

        if (ref == null) {
            out.println("ShellService reference cannot be found (null), service lookup will fail.");
        }

        return (ShellService) (bundleAccessor != null ? bundleAccessor.getService(ref) : null);
    }

    @Override
    public void setBundleContext(BundleContext bundleContext) {
        //System.out.println("ShellAction - setBundleContext called.  BundleContext: " + bundleContext);  // No logger, for debugging only.
        this.bundleContext = bundleContext;
    }
}
