/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on 1/10/2003
 *
 */
package com.opensymphony.webwork.views.freemarker;

import com.opensymphony.webwork.util.ListEntry;
import com.opensymphony.webwork.util.WebWorkUtil;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;
import freemarker.ext.beans.CollectionModel;
import freemarker.template.ObjectWrapper;
import junit.framework.TestCase;

import java.util.List;


/**
 * @author CameronBraid
 */
public class FreemarkerTest extends TestCase {

    TestAction testAction = null;


    /**
     *
     */
    public FreemarkerTest(String name) {
        super(name);
    }


    public void testSelectHelper() {
        WebWorkUtil wwUtil = new WebWorkUtil(ActionContext.getContext().getValueStack(), null, null);

        List selectList = null;

        selectList = wwUtil.makeSelectList("ignored", "stringList", null, null);
        assertEquals("one", ((ListEntry) selectList.get(0)).getKey());
        assertEquals("one", ((ListEntry) selectList.get(0)).getValue());

        selectList = wwUtil.makeSelectList("ignored", "beanList", "name", "value");
        assertEquals("one", ((ListEntry) selectList.get(0)).getKey());
        assertEquals("1", ((ListEntry) selectList.get(0)).getValue());
    }

    public void testValueStackMode() throws Exception {
        ScopesHashModel model = new ScopesHashModel(ObjectWrapper.BEANS_WRAPPER, null, null, ActionContext.getContext().getValueStack());

        CollectionModel stringList = null;

        stringList = (CollectionModel) model.get("stringList");
        assertEquals("one", stringList.get(0).toString());

        assertEquals("one", model.get("stringList[0]").toString());
        assertEquals("one", model.get("beanList[0].name").toString());
    }

    protected void setUp() throws Exception {
        super.setUp();

        OgnlValueStack stack = new OgnlValueStack();
        ActionContext.setContext(new ActionContext(stack.getContext()));

        testAction = new TestAction();
        ActionContext.getContext().getValueStack().push(testAction);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        ActionContext.setContext(null);
    }
}
