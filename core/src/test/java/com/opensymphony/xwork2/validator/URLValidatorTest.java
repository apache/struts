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
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.validators.URLValidator;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Test case for URLValidator
 *
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class URLValidatorTest extends XWorkTestCase {


    ValueStack stack;
    ActionContext actionContext;
    private TextProviderFactory tpf;

    public void testAcceptNullValueForMutualExclusionOfValidators() throws Exception {

        URLValidator validator = new URLValidator();
        validator.setValidatorContext(new DummyValidatorContext(new Object(), tpf));
        validator.setFieldName("testingUrl1");
        validator.setValueStack(ActionContext.getContext().getValueStack());
        validator.validate(new MyObject());

        assertFalse(validator.getValidatorContext().hasErrors());
        assertFalse(validator.getValidatorContext().hasActionErrors());
        assertFalse(validator.getValidatorContext().hasActionMessages());
        assertFalse(validator.getValidatorContext().hasFieldErrors());
    }

    public void testInvalidEmptyValue() throws Exception {

        URLValidator validator = new URLValidator();
        validator.setValidatorContext(new DummyValidatorContext(new Object(), tpf));
        validator.setFieldName("testingUrl2");
        validator.setValueStack(ActionContext.getContext().getValueStack());
        validator.validate(new MyObject());

        assertFalse(validator.getValidatorContext().hasErrors());
        assertFalse(validator.getValidatorContext().hasActionErrors());
        assertFalse(validator.getValidatorContext().hasActionMessages());
        assertFalse(validator.getValidatorContext().hasFieldErrors());
    }

    public void testInvalidValue() throws Exception {

        URLValidator validator = new URLValidator();
        validator.setValidatorContext(new DummyValidatorContext(new Object(), tpf));
        validator.setFieldName("testingUrl3");
        validator.setValueStack(ActionContext.getContext().getValueStack());
        validator.validate(new MyObject());

        assertTrue(validator.getValidatorContext().hasErrors());
        assertFalse(validator.getValidatorContext().hasActionErrors());
        assertFalse(validator.getValidatorContext().hasActionMessages());
        assertTrue(validator.getValidatorContext().hasFieldErrors());
    }


    public void testValidUrl1() throws Exception {

        URLValidator validator = new URLValidator();
        validator.setValidatorContext(new DummyValidatorContext(new Object(), tpf));
        validator.setFieldName("testingUrl4");
        validator.setValueStack(ActionContext.getContext().getValueStack());
        validator.validate(new MyObject());

        assertFalse(validator.getValidatorContext().hasErrors());
        assertFalse(validator.getValidatorContext().hasActionErrors());
        assertFalse(validator.getValidatorContext().hasActionMessages());
        assertFalse(validator.getValidatorContext().hasFieldErrors());
    }

    public void testValidUrl2() throws Exception {

        URLValidator validator = new URLValidator();
        validator.setValidatorContext(new DummyValidatorContext(new Object(), tpf));
        validator.setFieldName("testingUrl5");
        validator.setValueStack(ActionContext.getContext().getValueStack());
        validator.validate(new MyObject());

        assertFalse(validator.getValidatorContext().hasErrors());
        assertFalse(validator.getValidatorContext().hasActionErrors());
        assertFalse(validator.getValidatorContext().hasActionMessages());
        assertFalse(validator.getValidatorContext().hasFieldErrors());
    }

    public void testValidUrlWithRegex() throws Exception {
        URLValidator validator = new URLValidator();

        validator.setUrlRegex("^myapp:\\/\\/[a-z]*\\.com$");

        Pattern pattern = Pattern.compile(validator.getUrlRegex());

        assertTrue(pattern.matcher("myapp://test.com").matches());
        assertFalse(pattern.matcher("myap://test.com").matches());
    }

    public void testValidUrlWithRegexExpression() throws Exception {
        URLValidator validator = new URLValidator();
        ActionContext.getContext().getValueStack().push(new MyAction());
        validator.setValueStack(ActionContext.getContext().getValueStack());
        validator.setUrlRegexExpression("${urlRegex}");

        Pattern pattern = Pattern.compile(validator.getUrlRegex());

        assertTrue(pattern.matcher("myapp://test.com").matches());
        assertFalse(pattern.matcher("myap://test.com").matches());
    }

    public void testValidUrlWithDefaultRegex() throws Exception {
        URLValidator validator = new URLValidator();

        Pattern pattern = Pattern.compile(validator.getUrlRegex(), Pattern.CASE_INSENSITIVE);

        assertFalse(pattern.matcher("myapp://test.com").matches());
        assertFalse(pattern.matcher("myap://test.com").matches());
        assertFalse(pattern.matcher("").matches());
        assertFalse(pattern.matcher("   ").matches());
        assertFalse(pattern.matcher("no url").matches());
        assertFalse(pattern.matcher("http://example.com////////////////////////////////////////////////////////////////////////////////////??").matches());

        assertTrue(pattern.matcher("http://www.opensymphony.com").matches());
        assertTrue(pattern.matcher("https://www.opensymphony.com").matches());
        assertTrue(pattern.matcher("https://www.opensymphony.com:443/login").matches());
        assertTrue(pattern.matcher("http://localhost:8080/myapp").matches());

        assertTrue(pattern.matcher("http://www.legalspace.com/__media__/js/netsoltrademark.php?d=www.a-vos-travaux.fr%2Facheter-un-aspirateur-sans-sac-pas-cher%2F").matches());
        assertTrue(UrlValidator.getInstance().isValid("http://www.legalspace.com/__media__/js/netsoltrademark.php?d=www.a-vos-travaux.fr%2Facheter-un-aspirateur-sans-sac-pas-cher%2F"));

        assertTrue(pattern.matcher("http://www.duadmin.isaev.Infoduadmin.Isaev.info/?a%5B%5D=%3Ca%20href%3Dhttp%3A%2F%2Fwww.aspert.fr%2Fun-seche-cheveux-lisseur-est-il-vraiment-utile%2F%3Eseche%20cheveux%20dyson%20test%3C%2Fa").matches());
        assertTrue(UrlValidator.getInstance().isValid("http://www.duadmin.isaev.Infoduadmin.Isaev.info/?a%5B%5D=%3Ca%20href%3Dhttp%3A%2F%2Fwww.aspert.fr%2Fun-seche-cheveux-lisseur-est-il-vraiment-utile%2F%3Eseche%20cheveux%20dyson%20test%3C%2Fa"));

        assertTrue(pattern.matcher("http://netsol-underconstruction-page-monitor-1.com/__media__/js/netsoltrademark.php?d=www.le-soutien-scolaire.fr%2Favis-et-test-comparatifs-des-robots-multifonctions%2F").matches());
        assertTrue(UrlValidator.getInstance().isValid("http://netsol-underconstruction-page-monitor-1.com/__media__/js/netsoltrademark.php?d=www.le-soutien-scolaire.fr%2Favis-et-test-comparatifs-des-robots-multifonctions%2F"));

        //this will cause test to hang indefinitely using JDK 1.8.0_121, Struts 2.5.10.1 and JUnit 4.5
        assertTrue(pattern.matcher("http://www.javaroad.jp/news/redirect.jsp?link=http://www.forum-course-de-cote.com/que-penser-dune-trottinette-electrique/").matches());
        assertTrue(UrlValidator.getInstance().isValid("http://www.javaroad.jp/news/redirect.jsp?link=http://www.forum-course-de-cote.com/que-penser-dune-trottinette-electrique/"));

        //this will cause test to hang indefinitely using JDK 1.8.0_121, Struts 2.5.10.1 and JUnit 4.5
        assertTrue(pattern.matcher("http://wargame.ch/wc/acw/sub/aotm/guestbook/index.php?page3D183EClearwater20Roofing20Contractors3C/a3E3Ekaldu20non20msg3C/a3E").matches());
        assertTrue(UrlValidator.getInstance().isValid("http://wargame.ch/wc/acw/sub/aotm/guestbook/index.php?page3D183EClearwater20Roofing20Contractors3C/a3E3Ekaldu20non20msg3C/a3E"));

        assertTrue(pattern.matcher("http://253.254.255.1").matches());
        assertTrue(UrlValidator.getInstance().isValid("http://253.254.255.1"));

        assertTrue(pattern.matcher("http://253.254.255.12").matches());
        assertTrue(UrlValidator.getInstance().isValid("http://253.254.255.12"));

        assertTrue(pattern.matcher("http://1.2.3.100").matches());
        assertTrue(UrlValidator.getInstance().isValid("http://1.2.3.100"));

        assertTrue(pattern.matcher("http://1.2.3.255").matches());
        assertTrue(UrlValidator.getInstance().isValid("http://1.2.3.255"));

    }

    public void testLongRunningValidations() throws Exception {
        URLValidator validator = new URLValidator();

        Pattern pattern = Pattern.compile(validator.getUrlRegex(), Pattern.CASE_INSENSITIVE);

        long time = System.currentTimeMillis();
        assertFalse(pattern.matcher("ftp://aaaaaaaaaaaaaaaaaaaaaaaa|").matches());
        assertTrue("Validation did not complete in half a second", System.currentTimeMillis() - time < 500);

        time = System.currentTimeMillis();
        assertFalse(pattern.matcher("ftp://bbbbbbbbbbbbbbbbbbbbbbbb}").matches());
        assertTrue("Validation did not complete in half a second", System.currentTimeMillis() - time < 500);

        time = System.currentTimeMillis();
        assertFalse(pattern.matcher("ftp://cccccccccccccccccccccccc{").matches());
        assertTrue("Validation did not complete in half a second", System.currentTimeMillis() - time < 500);
    }

    public void testValidUrlCaseInsensitive() throws Exception {
        // given
        final Map<String, Object> fieldErrors = new HashMap<>();

        URLValidator validator = new URLValidator() {
            @Override
            public String getFieldName() {
                return "url";
            }

            @Override
            protected Object getFieldValue(String name, Object object) throws ValidationException {
                return object;
            }

            @Override
            protected void addFieldError(String propertyName, Object object) {
                fieldErrors.put(propertyName, object);
            }
        };

        // when
        validator.validate("http://localhost:8080/myapp");

        // then
        assertTrue(fieldErrors.isEmpty());

        // when
        validator.validate("http://LOCALHOST:8080/MYAPP");

        // then
        assertTrue(fieldErrors.isEmpty());

        // when
        validator.validate("http://www.appache.org/TEST");

        // then
        assertTrue(fieldErrors.isEmpty());
    }

    public void testArrayOfUrls() throws Exception {
        URLValidator validator = new URLValidator();
        validator.setValidatorContext(new DummyValidatorContext(new Object(), tpf));
        validator.setFieldName("urls");
        validator.setValueStack(ActionContext.getContext().getValueStack());
        validator.validate(new MyObject());

        assertTrue(validator.getValidatorContext().hasErrors());
        assertFalse(validator.getValidatorContext().hasActionErrors());
        assertFalse(validator.getValidatorContext().hasActionMessages());
        assertTrue(validator.getValidatorContext().hasFieldErrors());
        assertEquals(1, validator.getValidatorContext().getFieldErrors().get("urls").size());
    }

    public void testCollectionOfUrls() throws Exception {
        URLValidator validator = new URLValidator();
        validator.setValidatorContext(new DummyValidatorContext(new Object(), tpf));
        validator.setFieldName("urlCollection");
        validator.setValueStack(ActionContext.getContext().getValueStack());
        validator.setDefaultMessage("Wrong URL provided: ${currentValue}");
        validator.validate(new MyObject());

        assertTrue(validator.getValidatorContext().hasErrors());
        assertFalse(validator.getValidatorContext().hasActionErrors());
        assertFalse(validator.getValidatorContext().hasActionMessages());
        assertTrue(validator.getValidatorContext().hasFieldErrors());
        assertEquals(1, validator.getValidatorContext().getFieldErrors().get("urlCollection").size());
        assertEquals("Wrong URL provided: htps://wrong.side.com", validator.getValidatorContext().getFieldErrors().get("urlCollection").get(0));
    }

    public void testCollectionOfUrlsSafness() throws Exception {
        URLValidator validator = new URLValidator();
        validator.setValidatorContext(new DummyValidatorContext(new Object(), tpf));
        validator.setFieldName("urlSafeness");
        validator.setValueStack(ActionContext.getContext().getValueStack());
        validator.setDefaultMessage("Wrong URL provided: ${currentValue}");
        validator.validate(new MyObject());

        assertTrue(validator.getValidatorContext().hasErrors());
        assertFalse(validator.getValidatorContext().hasActionErrors());
        assertFalse(validator.getValidatorContext().hasActionMessages());
        assertTrue(validator.getValidatorContext().hasFieldErrors());
        assertEquals(2, validator.getValidatorContext().getFieldErrors().get("urlSafeness").size());
        assertEquals("Wrong URL provided: ${1+2}", validator.getValidatorContext().getFieldErrors().get("urlSafeness").get(0));
        assertEquals("Wrong URL provided: %{2+3}", validator.getValidatorContext().getFieldErrors().get("urlSafeness").get(1));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        stack = ActionContext.getContext().getValueStack();
        actionContext = ActionContext.getContext();
        tpf = container.getInstance(TextProviderFactory.class);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        stack = null;
        actionContext = null;
    }


    class MyObject {
        public String getTestingUrl1() {
            return null;
        }

        public String getTestingUrl2() {
            return "";
        }

        public String getTestingUrl3() {
            return "sasdasd@asddd";
        }

        public String getTestingUrl4() {
            //return "http://yahoo.com/";
            return "http://www.jroller.com1?qwe=qwe";
        }

        public String getTestingUrl5() {
            return "http://yahoo.com/articles?id=123\n";
        }

        public String[] getUrls() {
            return new String[]{
                    "https://struts.apache.org",
                    "htps://wrong.side.com"
            };
        }

        public Collection<String> getUrlCollection() {
            return Arrays.asList("https://struts.apache.org","htps://wrong.side.com");
        }
        public Collection<String> getUrlSafeness() {
            return Arrays.asList("https://struts.apache.org","${1+2}", "%{2+3}");
        }
    }

    class MyAction {

        public String getUrlRegex() {
            return "myapp:\\/\\/[a-z]*\\.com";
        }
    }
}
