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
package it.org.apache.struts2.showcase;

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class StrutsParametersTest {

    private WebClient webClient;

    @Before
    public void setUp() throws Exception {
        webClient = new WebClient();
    }

    @After
    public void tearDown() throws Exception {
        webClient.close();
    }

    @Test
    public void public_StringField_WithoutGetterSetter_FieldNotAnnotated() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("publicField", "yes");
        params.put("varToPrint", "publicField");
        assertText(params, "publicField{no}");
    }

    @Test
    public void public_StringField_WithoutGetterSetter_FieldAnnotated() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("publicFieldAnnotated", "yes");
        params.put("varToPrint", "publicFieldAnnotated");
        assertText(params, "publicFieldAnnotated{yes}");
    }

    @Test
    public void private_StringField_WithSetter_MethodNotAnnotated() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("privateFieldMethod", "yes");
        params.put("varToPrint", "privateField");
        assertText(params, "privateField{no}");
    }

    @Test
    public void private_StringField_WithSetter_MethodAnnotated() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("privateFieldMethodAnnotated", "yes");
        params.put("varToPrint", "privateField");
        assertText(params, "privateField{yes}");
    }

    @Test
    public void public_ArrayField_WithoutGetterSetter_FieldNotAnnotated() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("publicArray[0]", "1");
        params.put("varToPrint", "publicArray");
        assertText(params, "publicArray{[0]}");
    }

    @Test
    public void public_ArrayField_WithoutGetterSetter_FieldAnnotated() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("publicArrayAnnotated[0]", "1");
        params.put("varToPrint", "publicArrayAnnotated");
        assertText(params, "publicArrayAnnotated{[1]}");
    }

    @Test
    public void public_ListField_WithoutGetterSetter_FieldNotAnnotated() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("publicList[0]", "yes");
        params.put("varToPrint", "publicList");
        assertText(params, "publicList{[no]}");
    }

    @Test
    public void public_ListField_WithoutGetterSetter_FieldAnnotated() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("publicListAnnotated[0]", "yes");
        params.put("varToPrint", "publicListAnnotated");
        assertText(params, "publicListAnnotated{[yes]}");
    }

    @Test
    public void private_ListField_WithGetterNoSetter_MethodNotAnnotated() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("privateListMethod[0]", "yes");
        params.put("varToPrint", "privateList");
        assertText(params, "privateList{[no]}");
    }

    @Test
    public void private_ListField_WithGetterNoSetter_MethodAnnotated() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("privateListMethodAnnotated[0]", "yes");
        params.put("varToPrint", "privateList");
        assertText(params, "privateList{[yes]}");
    }

    @Test
    public void public_MapField_WithoutGetterSetter_FieldNotAnnotated() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("publicMap['key']", "yes");
        params.put("varToPrint", "publicMap");
        assertText(params, "publicMap{{key=no}}");
    }

    @Test
    public void public_MapField_WithoutGetterSetter_FieldAnnotated() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("publicMapAnnotated['key']", "yes");
        params.put("varToPrint", "publicMapAnnotated");
        assertText(params, "publicMapAnnotated{{key=yes}}");
    }

    @Test
    public void public_MapField_Insert_WithoutGetterSetter_FieldNotAnnotated() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("publicMap[999]", "yes");
        params.put("varToPrint", "publicMap");
        assertText(params, "publicMap{{key=no}}");
    }

    @Test
    public void public_MapField_Insert_WithoutGetterSetter_FieldAnnotated() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("publicMapAnnotated[999]", "yes");
        params.put("varToPrint", "publicMapAnnotated");
        assertText(params, "publicMapAnnotated{{999=yes, key=no}}");
    }

    @Test
    public void public_MyDtoField_WithoutGetter_FieldNotAnnotated() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("publicMyDto.str", "yes");
        params.put("publicMyDto.map['key']", "yes");
        params.put("publicMyDto.array[0]", "1");
        params.put("varToPrint", "publicMyDto");
        assertText(params, "publicMyDto{str=no, map={key=no}, array=[0]}");
    }

    @Test
    public void public_MyDtoField_WithoutGetter_FieldAnnotated() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("publicMyDtoAnnotated.str", "yes");
        params.put("publicMyDtoAnnotated.map['key']", "yes");
        params.put("publicMyDtoAnnotated.array[0]", "1");
        params.put("varToPrint", "publicMyDtoAnnotated");
        assertText(params, "publicMyDtoAnnotated{str=yes, map={key=yes}, array=[1]}");
    }

    @Test
    public void public_MyDtoField_WithoutGetter_FieldAnnotatedDepthOne() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("publicMyDtoAnnotatedDepthOne.str", "yes");
        params.put("publicMyDtoAnnotatedDepthOne.map['key']", "yes");
        params.put("publicMyDtoAnnotatedDepthOne.array[0]", "1");
        params.put("varToPrint", "publicMyDtoAnnotatedDepthOne");
        assertText(params, "publicMyDtoAnnotatedDepthOne{str=yes, map={key=no}, array=[0]}");
    }

    @Test
    public void private_MyDtoField_WithGetter_MethodNotAnnotated() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("unsafeMethodMyDto.str", "yes");
        params.put("unsafeMethodMyDto.map['key']", "yes");
        params.put("unsafeMethodMyDto.array[0]", "1");
        params.put("varToPrint", "privateMyDto");
        assertText(params, "privateMyDto{str=no, map={key=no}, array=[0]}");
    }

    @Test
    public void private_MyDtoField_WithGetter_MethodNotAnnotated_Alternate() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("unsafeMethodMyDto['str']", "yes");
        params.put("unsafeMethodMyDto['map']['key']", "yes");
        params.put("unsafeMethodMyDto['map'][999]", "yes");
        params.put("unsafeMethodMyDto['array'][0]", "1");
        params.put("varToPrint", "privateMyDto");
        assertText(params, "privateMyDto{str=no, map={key=no}, array=[0]}");
    }

    @Test
    public void private_MyDtoField_WithGetter_MethodAnnotated() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("safeMethodMyDto.str", "yes");
        params.put("safeMethodMyDto.map['key']", "yes");
        params.put("safeMethodMyDto.array[0]", "1");
        params.put("varToPrint", "privateMyDto");
        assertText(params, "privateMyDto{str=yes, map={key=yes}, array=[1]}");
    }

    @Test
    public void private_MyDtoField_WithGetter_MethodAnnotatedDepthOne() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("safeMethodMyDtoDepthOne.str", "yes");
        params.put("safeMethodMyDtoDepthOne.map['key']", "yes");
        params.put("safeMethodMyDtoDepthOne.array[0]", "1");
        params.put("varToPrint", "privateMyDto");
        assertText(params, "privateMyDto{str=yes, map={key=no}, array=[0]}");
    }

    private void assertText(Map<String, String> params, String text) throws IOException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(ParameterUtils.getBaseUrl())
                .path("/paramsannotation/test.action");
        params.forEach(builder::queryParam);
        String url = builder.toUriString();
        HtmlPage page = webClient.getPage(url);
        String output = page.getElementById("output").asNormalizedText();
        assertEquals(text, output);
    }
}
