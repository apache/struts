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

import org.apache.struts2.osgi.DefaultBundleAccessor;
import org.apache.felix.shell.ShellService;
import org.osgi.framework.ServiceReference;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * This action executes commands on the Felix Shell
 */
public class ShellAction extends ActionSupport {
    private String command;
    private String output;

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
            errString = e.getMessage();
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
        ShellService shellService = getShellService();
        if (shellService != null)
            shellService.executeCommand(commandLine, out, err);
        else
            err.println("Apache Felix Shell service is not installed");
    }

    private ShellService getShellService() {
        //bundle can be de-activated, so keeping a reference aorund is not a good idea
        DefaultBundleAccessor bundleAcessor = DefaultBundleAccessor.getInstance();
        ServiceReference ref = bundleAcessor.getServiceReference(ShellService.class.getName());
        return (ShellService) bundleAcessor.getService(ref);
    }
}
