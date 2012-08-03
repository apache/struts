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

package org.apache.struts2.views.jasperreports;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

/**
 * Ported to Struts:
 *
 */
public class CompileReport {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please supply the name of the report(s) source to compile.");
            System.exit(-1);
        }

        try {
            for (int i = 0; i < args.length; i++) {
                System.out.println("JasperReports Compiling: " + args[i]);
                JasperCompileManager.compileReportToFile(args[i]);
            }
        } catch (JRException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        System.exit(0);
    }
}
