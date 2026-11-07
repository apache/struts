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
package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.TestAction;
import org.apache.struts2.views.jsp.AbstractUITagTest;
import org.apache.struts2.views.jsp.ui.DoubleSelectTest.Country;
import org.apache.struts2.views.jsp.ui.DoubleSelectTest.Region;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DoubleSelectMultipleTest extends AbstractUITagTest {

    public static class SelectedRegions {
        private final String[] selectedRegions;

        public SelectedRegions(String... selectedRegions) {
            this.selectedRegions = selectedRegions;
        }

        public String[] getSelectedRegions() {
            return selectedRegions;
        }
    }

    private DoubleSelectTag tag;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        List<Region> belgiumRegions = new ArrayList<>();
        belgiumRegions.add(new Region("Antwerp", "AN"));
        belgiumRegions.add(new Region("Gent", "GN"));
        belgiumRegions.add(new Region("Brugge", "BRG"));
        List<Region> franceRegions = new ArrayList<>();
        franceRegions.add(new Region("Paris", "PA"));
        franceRegions.add(new Region("Bordeaux", "BOR"));
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("Belgium", "BE", belgiumRegions));
        countries.add(new Country("France", "FR", franceRegions));
        ((TestAction) action).setList2(countries);

        tag = new DoubleSelectTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setValue("'BE'");
        tag.setDoubleName("region");
        tag.setList("list2");
        tag.setDoubleList("regions");
        tag.setListKey("iso");
        tag.setDoubleListKey("key");
        tag.setListValue("name");
        tag.setDoubleListValue("name");
        tag.setFormName("inputForm");
    }

    private String render() throws Exception {
        tag.doStartTag();
        tag.doEndTag();
        return writer.toString();
    }

    public void testDoubleMultipleWithCollectionValue() throws Exception {
        tag.setDoubleMultiple("true");
        tag.setDoubleValue("{'AN', 'GN'}");

        render();

        verify(SelectTag.class.getResource("DoubleSelect-7.txt"));
    }

    public void testDoubleMultipleWithStringValue() throws Exception {
        tag.setDoubleMultiple("true");
        tag.setDoubleValue("'AN'");

        assertThat(render()).contains("var fooSelected = [\"AN\"];");
    }

    public void testDoubleMultipleWithArrayValue() throws Exception {
        stack.push(new SelectedRegions("AN", "BRG"));
        tag.setDoubleMultiple("true");
        tag.setDoubleValue("selectedRegions");

        assertThat(render()).contains("var fooSelected = [\"AN\", \"BRG\"];");
    }

    public void testDoubleMultipleValuesRenderAsStringLiterals() throws Exception {
        stack.push(new SelectedRegions("a'b\\c", "d\"e"));
        tag.setDoubleMultiple("true");
        tag.setDoubleValue("selectedRegions");

        assertThat(render()).contains("var fooSelected = [\"a\\&#39;b\\\\c\", \"d\\&quot;e\"];");
    }

    public void testDoubleMultipleValueCannotCloseTheScriptBlock() throws Exception {
        stack.push(new SelectedRegions("</script><b>"));
        tag.setDoubleMultiple("true");
        tag.setDoubleValue("selectedRegions");

        String output = render();

        assertThat(output).contains("var fooSelected = [\"&lt;\\/script&gt;&lt;b&gt;\"];");
        assertThat(output.indexOf("</script>")).isEqualTo(output.lastIndexOf("</script>"));
    }

    public void testDoubleMultipleFalseUsesTheSingleValuePath() throws Exception {
        tag.setDoubleMultiple("false");
        tag.setDoubleValue("'AN'");

        String output = render();

        assertThat(output).contains("fooTemp.options[i].value == 'AN'");
        assertThat(output).doesNotContain("fooSelected");
    }
}
