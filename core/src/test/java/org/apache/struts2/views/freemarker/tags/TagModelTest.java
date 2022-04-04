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

package org.apache.struts2.views.freemarker.tags;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.components.Component;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ognl.OgnlValueStack;

import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;

public class TagModelTest extends StrutsInternalTestCase {

    final Object ADAPTER_TEMPLATE_MODEL_CONTAINED_OBJECT = new Object();
    final Object WRAPPING_TEMPLATE_MODEL_CONTAINED_OBJECT = new Object();

    public void testUnwrapMap() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        OgnlValueStack stack = (OgnlValueStack)ActionContext.getContext().getValueStack();

        Map params = new LinkedHashMap();

        // Try to test out the commons Freemarker's Template Model

        // TemplateBooleanModel
        params.put("property1", new TemplateBooleanModel() {
            public boolean getAsBoolean() throws TemplateModelException {
                return true;
            }
        });
        params.put("property2", new TemplateBooleanModel() {
            public boolean getAsBoolean() throws TemplateModelException {
                return false;
            }
        });

        // TemplateScalarModel
        params.put("property3", new TemplateScalarModel() {
            public String getAsString() throws TemplateModelException {
                return "toby";
            }
        });
        params.put("property4", new TemplateScalarModel() {
            public String getAsString() throws TemplateModelException {
                return "phil";
            }
        });

        // TemplateNumberModel
        params.put("property5", new TemplateNumberModel() {
            public Number getAsNumber() throws TemplateModelException {
                return new Integer("10");
            }
        });
        params.put("property6", new TemplateNumberModel() {
            public Number getAsNumber() throws TemplateModelException {
                return new Float("1.1");
            }
        });

        // TemplateHashModel
        params.put("property7", new TemplateHashModel() {
            public TemplateModel get(String arg0) throws TemplateModelException {
                return null;
            }

            public boolean isEmpty() throws TemplateModelException {
                return true;
            }
        });

        // TemplateSequenceModel
        params.put("property8", new TemplateSequenceModel() {
            public TemplateModel get(int arg0) throws TemplateModelException {
                return null;
            }

            public int size() throws TemplateModelException {
                return 0;
            }
        });

        // TemplateCollectionModel
        params.put("property9", new TemplateCollectionModel() {
            public TemplateModelIterator iterator() throws TemplateModelException {
                return new TemplateModelIterator() {
                    private Iterator i;
                    {
                        i = Collections.EMPTY_LIST.iterator();
                    }

                    public boolean hasNext() throws TemplateModelException {
                        return i.hasNext();
                    }

                    public TemplateModel next() throws TemplateModelException {
                        return (TemplateModel) i.next();
                    }
                };
            }
        });

        // AdapterTemplateModel
        params.put("property10", new AdapterTemplateModel() {
            public Object getAdaptedObject(Class arg0) {
                return ADAPTER_TEMPLATE_MODEL_CONTAINED_OBJECT;
            }
        });

        // WrapperTemplateModel
        params.put("property11", new WrapperTemplateModel() {
            public Object getWrappedObject() {
                return WRAPPING_TEMPLATE_MODEL_CONTAINED_OBJECT;
            }
        });

        TagModel tagModel = new TagModel(stack, request, response) {
            protected Component getBean() {
                return null;
            }
        };

        Map result = tagModel.unwrapParameters(params);

        assertNotNull(result);
        assertEquals(result.size(), 11);
        assertEquals(result.get("property1"), Boolean.TRUE);
        assertEquals(result.get("property2"), Boolean.FALSE);
        assertEquals(result.get("property3"), "toby");
        assertEquals(result.get("property4"), "phil");
        assertEquals(result.get("property5"), new Integer(10));
        assertEquals(result.get("property6"), new Float(1.1));
        assertNotNull(result.get("property7"));
        assertTrue(result.get("property7") instanceof Map);
        assertNotNull(result.get("property8"));
        assertTrue(result.get("property8") instanceof Collection);
        assertNotNull(result.get("property9"));
        assertTrue(result.get("property9") instanceof Collection);
        assertEquals(result.get("property10"),
            ADAPTER_TEMPLATE_MODEL_CONTAINED_OBJECT);
        assertEquals(result.get("property11"),
            WRAPPING_TEMPLATE_MODEL_CONTAINED_OBJECT);
    }

    public void testGetWriter() throws Exception {

        OgnlValueStack stack = (OgnlValueStack)ActionContext.getContext().getValueStack();
        
        final InternalBean bean = new InternalBean(stack);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        Map params = new LinkedHashMap();

        // Try to test out the commons Freemarker's Template Model

        // TemplateBooleanModel
        params.put("property1", new TemplateBooleanModel() {
            public boolean getAsBoolean() throws TemplateModelException {
                return true;
            }
        });
        params.put("property2", new TemplateBooleanModel() {
            public boolean getAsBoolean() throws TemplateModelException {
                return false;
            }
        });

        // TemplateScalarModel
        params.put("property3", new TemplateScalarModel() {
            public String getAsString() throws TemplateModelException {
                return "toby";
            }
        });
        params.put("property4", new TemplateScalarModel() {
            public String getAsString() throws TemplateModelException {
                return "phil";
            }
        });

        // TemplateNumberModel
        params.put("property5", new TemplateNumberModel() {
            public Number getAsNumber() throws TemplateModelException {
                return new Integer("10");
            }
        });
        params.put("property6", new TemplateNumberModel() {
            public Number getAsNumber() throws TemplateModelException {
                return new Float("1.1");
            }
        });

        // TemplateHashModel
        params.put("property7", new TemplateHashModel() {
            public TemplateModel get(String arg0) throws TemplateModelException {
                return null;
            }

            public boolean isEmpty() throws TemplateModelException {
                return true;
            }
        });

        // TemplateSequenceModel
        params.put("property8", new TemplateSequenceModel() {
            public TemplateModel get(int arg0) throws TemplateModelException {
                return null;
            }

            public int size() throws TemplateModelException {
                return 0;
            }
        });

        // TemplateCollectionModel
        params.put("property9", new TemplateCollectionModel() {
            public TemplateModelIterator iterator() throws TemplateModelException {
                return new TemplateModelIterator() {
                    private Iterator i;
                    {
                        i = Collections.EMPTY_LIST.iterator();
                    }

                    public boolean hasNext() throws TemplateModelException {
                        return i.hasNext();
                    }

                    public TemplateModel next() throws TemplateModelException {
                        return (TemplateModel) i.next();
                    }
                };
            }
        });

        // AdapterTemplateModel
        params.put("property10", new AdapterTemplateModel() {
            public Object getAdaptedObject(Class arg0) {
                return ADAPTER_TEMPLATE_MODEL_CONTAINED_OBJECT;
            }
        });

        // WrapperTemplateModel
        params.put("property11", new WrapperTemplateModel() {
            public Object getWrappedObject() {
                return WRAPPING_TEMPLATE_MODEL_CONTAINED_OBJECT;
            }
        });

        TagModel tagModel = new TagModel(stack, request, response) {
            protected Component getBean() {
                return bean;
            }
        };

        tagModel.getWriter(new StringWriter(), params);

        assertNotNull(bean);
        assertEquals(bean.getProperty1(), true);
        assertEquals(bean.getProperty2(), false);
        assertEquals(bean.getProperty3(), "toby");
        assertEquals(bean.getProperty4(), "phil");
        assertEquals(bean.getProperty5(), new Integer(10));
        assertEquals(bean.getProperty6(), new Float(1.1));
        assertNotNull(bean.getProperty7());
        assertTrue(bean.getProperty7() instanceof Map);
        assertNotNull(bean.getProperty8());
        assertTrue(bean.getProperty8() instanceof Collection);
        assertNotNull(bean.getProperty9());
        assertTrue(bean.getProperty9() instanceof Collection);
        assertEquals(bean.getProperty10(), ADAPTER_TEMPLATE_MODEL_CONTAINED_OBJECT);
        assertEquals(bean.getProperty11(), WRAPPING_TEMPLATE_MODEL_CONTAINED_OBJECT);
    }

    public static class InternalBean extends Component {

        public InternalBean(OgnlValueStack stack) {
            super(stack);
        }

        private boolean property1 = false; // inverse of the expected result, so we could test that it works
        private boolean property2 = true; // inverse of the expected result, so we could test that it works

        private String property3;
        private String property4;

        private Integer property5;
        private Float property6;

        private Map property7;
        private Collection property8;
        private Collection property9;

        private Object property10;
        private Object property11;

        public void setProperty1(boolean property1) {
            this.property1 = property1;
        }

        public boolean getProperty1() {
            return property1;
        }

        public void setProperty2(boolean property2) {
            this.property2 = property2;
        }

        public boolean getProperty2() {
            return property2;
        }

        public void setProperty3(String property3) {
            this.property3 = property3;
        }

        public String getProperty3() {
            return this.property3;
        }

        public void setProperty4(String property4) {
            this.property4 = property4;
        }

        public String getProperty4() {
            return this.property4;
        }

        public void setProperty5(Integer property5) {
            this.property5 = property5;
        }

        public Integer getProperty5() {
            return this.property5;
        }

        public void setProperty6(Float property6) {
            this.property6 = property6;
        }

        public Float getProperty6() {
            return this.property6;
        }

        public void setProperty7(Map property7) {
            this.property7 = property7;
        }

        public Map getProperty7() {
            return property7;
        }

        public void setProperty8(Collection property8) {
            this.property8 = property8;
        }

        public Collection getProperty8() {
            return property8;
        }

        public void setProperty9(Collection property9) {
            this.property9 = property9;
        }

        public Collection getProperty9() {
            return this.property9;
        }

        public void setProperty10(Object property10) {
            this.property10 = property10;
        }

        public Object getProperty10() {
            return this.property10;
        }

        public void setProperty11(Object property11) {
            this.property11 = property11;
        }

        public Object getProperty11() {
            return this.property11;
        }

    }
}
