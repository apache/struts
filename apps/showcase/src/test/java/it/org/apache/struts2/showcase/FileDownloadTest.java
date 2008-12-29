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
package it.org.apache.struts2.showcase;

import java.net.URL;
import java.net.MalformedURLException;

public class FileDownloadTest extends ITBaseTest {
    public void testImage() throws InterruptedException, MalformedURLException {
        beginAt("/filedownload/download.action");

        URL url = new URL("http://svn.apache.org/repos/asf/struts/struts2/trunk/apps/showcase/src/main/webapp/images/struts.gif");
        assertDownloadedFileEquals(url);
    }

     public void testZip() throws InterruptedException, MalformedURLException {
        beginAt("/filedownload/download2.action");

        URL url = new URL("http://svn.apache.org/repos/asf/struts/struts2/trunk/apps/showcase/src/main/webapp/images/struts-gif.zip");
        assertDownloadedFileEquals(url);
    }
}
