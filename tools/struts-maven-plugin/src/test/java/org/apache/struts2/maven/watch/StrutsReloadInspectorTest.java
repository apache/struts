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
package org.apache.struts2.maven.watch;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StrutsReloadInspectorTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private void xml(String... constants) throws IOException {
        xmlWithDoctype("", constants);
    }

    private void xmlWithDoctype(String doctype, String... constants) throws IOException {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append(doctype).append("<struts>\n");
        for (int i = 0; i < constants.length; i += 2) {
            sb.append("<constant name=\"").append(constants[i]).append("\" value=\"")
                    .append(constants[i + 1]).append("\"/>\n");
        }
        sb.append("</struts>\n");
        write("struts.xml", sb.toString());
    }

    private void write(String name, String content) throws IOException {
        Files.write(tmp.getRoot().toPath().resolve(name), content.getBytes(StandardCharsets.UTF_8));
    }

    private List<String> warnings() {
        return new StrutsReloadInspector(tmp.getRoot().toPath()).warnings();
    }

    @Test
    public void devModeTrueNoWarnings() throws IOException {
        xml("struts.devMode", "true");

        assertThat(warnings()).isEmpty();
    }

    @Test
    public void devModeFalseWarnsForBothReloadSwitches() throws IOException {
        xml("struts.devMode", "false");

        List<String> warnings = warnings();

        assertThat(warnings).hasSize(2);
        assertThat(warnings.get(0)).contains("struts.configuration.xml.reload");
        assertThat(warnings.get(1)).contains("struts.i18n.reload");
    }

    @Test
    public void explicitFalseOverridesDevMode() throws IOException {
        xml("struts.devMode", "true", "struts.i18n.reload", "false");

        List<String> warnings = warnings();

        assertThat(warnings).hasSize(1);
        assertThat(warnings.get(0)).contains("struts.i18n.reload");
    }

    @Test
    public void propertiesFileOverridesXml() throws IOException {
        xml("struts.devMode", "false");
        write("struts.properties", "struts.devMode=true\n");

        assertThat(warnings()).isEmpty();
    }

    @Test
    public void devModeNotFoundGivesSoftWarning() {
        List<String> warnings = warnings();

        assertThat(warnings).hasSize(1);
        assertThat(warnings.get(0)).contains("struts.devMode");
    }

    @Test
    public void doesNotFetchExternalDtd() throws IOException {
        xmlWithDoctype("<!DOCTYPE struts PUBLIC \"x\" \"http://127.0.0.1:1/never.dtd\">\n",
                "struts.devMode", "true");

        assertThat(warnings()).isEmpty();
    }

    @Test
    public void malformedXmlProducesNoException() throws IOException {
        write("struts.xml", "<struts><constant name=");

        assertThat(warnings()).hasSize(1);
    }
}
