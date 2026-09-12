/*
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
package org.apache.struts2.views.jasperreports7.tomcat;

import net.sf.jasperreports.engine.JasperCompileManager;
import org.apache.catalina.Context;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter;
import org.apache.struts2.util.ClassLoaderUtil;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Runs the plugin on a real servlet container: the mock response used by the other tests
 * keeps accepting writes after the stream is closed, a container does not.
 */
public class JasperReport7TomcatTest {

    private static Tomcat tomcat;
    private static String baseUrl;

    @BeforeClass
    public static void startTomcat() throws Exception {
        Path baseDir = Files.createTempDirectory(Files.createDirectories(Path.of("target")), "tomcat").toAbsolutePath();
        Path docBase = Files.createDirectories(baseDir.resolve("webapp"));
        Path reports = Files.createDirectories(docBase.resolve("reports"));
        URL jrxml = ClassLoaderUtil.getResource("org/apache/struts2/views/jasperreports7/simple.jrxml", JasperReport7TomcatTest.class);
        JasperCompileManager.compileReportToFile(Path.of(jrxml.toURI()).toString(), reports.resolve("simple.jasper").toString());

        tomcat = new Tomcat();
        tomcat.setBaseDir(baseDir.toString());
        tomcat.setPort(0);
        tomcat.getConnector().setProperty("address", "127.0.0.1");

        Context context = tomcat.addContext("", docBase.toString());
        Tomcat.addServlet(context, "default", new DefaultServlet());
        context.addServletMappingDecoded("/", "default");
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("struts");
        filterDef.setFilterClass(StrutsPrepareAndExecuteFilter.class.getName());
        filterDef.addInitParameter("config", "struts-default.xml,struts-plugin.xml,struts-tomcat.xml");
        context.addFilterDef(filterDef);
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("struts");
        filterMap.addURLPattern("/*");
        context.addFilterMap(filterMap);

        tomcat.start();
        baseUrl = "http://127.0.0.1:" + tomcat.getConnector().getLocalPort();
    }

    @AfterClass
    public static void stopTomcat() throws Exception {
        if (tomcat != null) {
            tomcat.stop();
            tomcat.destroy();
        }
    }

    @Test
    public void exportsCsv() throws Exception {
        HttpResponse<byte[]> response = report("csv");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).hasValueSatisfying(type -> assertThat(type).startsWith("text/csv"));
        assertThat(new String(response.body())).contains("Tomcat Report").contains("Hello Foo Bar!");
    }

    @Test
    public void exportsPdf() throws Exception {
        HttpResponse<byte[]> response = report("pdf");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).hasValueSatisfying(type -> assertThat(type).startsWith("application/pdf"));
        assertThat(response.body()).startsWith("%PDF".getBytes());
    }

    @Test
    public void exportsHtml() throws Exception {
        HttpResponse<byte[]> response = report("html");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).hasValueSatisfying(type -> assertThat(type).startsWith("text/html"));
        assertThat(new String(response.body())).contains("Tomcat Report").contains("Hello Foo Bar!");
    }

    @Test
    public void exportsXml() throws Exception {
        HttpResponse<byte[]> response = report("xml");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).hasValueSatisfying(type -> assertThat(type).startsWith("text/xml"));
        assertThat(new String(response.body())).contains("Tomcat Report").contains("Hello Foo Bar!");
    }

    @Test
    public void exportsRtf() throws Exception {
        HttpResponse<byte[]> response = report("rtf");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).hasValueSatisfying(type -> assertThat(type).startsWith("application/rtf"));
        assertThat(new String(response.body())).startsWith("{\\rtf").contains("Hello Foo Bar!");
    }

    @Test
    public void exportsXlsx() throws Exception {
        HttpResponse<byte[]> response = report("xlsx");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Content-Type")).hasValueSatisfying(type -> assertThat(type).startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        assertThat(response.body()).startsWith("PK".getBytes());
    }

    private static HttpResponse<byte[]> report(String format) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/report.action?format=" + format)).build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofByteArray());
    }
}
