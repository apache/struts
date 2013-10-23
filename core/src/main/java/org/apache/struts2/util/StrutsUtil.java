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

package org.apache.struts2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.views.jsp.ui.OgnlTool;
import org.apache.struts2.views.util.UrlHelper;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Struts base utility class, for use in Velocity and Freemarker templates
 */
public class StrutsUtil {

    protected static final Logger LOG = LoggerFactory.getLogger(StrutsUtil.class);

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected Map<String, Class> classes = new Hashtable<String, Class>();
    protected OgnlTool ognl;
    protected ValueStack stack;

    private UrlHelper urlHelper;
    private ObjectFactory objectFactory;

    public StrutsUtil(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        this.stack = stack;
        this.request = request;
        this.response = response;
        this.ognl = ((Container)stack.getContext().get(ActionContext.CONTAINER)).getInstance(OgnlTool.class);
        this.urlHelper = ((Container)stack.getContext().get(ActionContext.CONTAINER)).getInstance(UrlHelper.class);
        this.objectFactory = ((Container)stack.getContext().get(ActionContext.CONTAINER)).getInstance(ObjectFactory.class);
    }

    public Object bean(Object aName) throws Exception {
        String name = aName.toString();
        Class c = classes.get(name);

        if (c == null) {
            c = ClassLoaderUtil.loadClass(name, StrutsUtil.class);
            classes.put(name, c);
        }

        return objectFactory.buildBean(c, stack.getContext());
    }

    public boolean isTrue(String expression) {
        Boolean retVal = (Boolean) stack.findValue(expression, Boolean.class);
        return retVal != null && retVal;
    }

    public Object findString(String name) {
        return stack.findValue(name, String.class);
    }

    public String include(Object aName) throws Exception {
        return include(aName, request, response);
    }

    /**
     * @deprecated the request and response are stored in this util class, please use include(string)
     */
    public String include(Object aName, HttpServletRequest aRequest, HttpServletResponse aResponse) throws Exception {
        try {
            RequestDispatcher dispatcher = aRequest.getRequestDispatcher(aName.toString());

            if (dispatcher == null) {
                throw new IllegalArgumentException("Cannot find included file " + aName);
            }

            ResponseWrapper responseWrapper = new ResponseWrapper(aResponse);

            dispatcher.include(aRequest, responseWrapper);

            return responseWrapper.getData();
        }
        catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cannot include #0", e, aName.toString());
            }
            throw e;
        }
    }

    public String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cannot encode URL [#0]", e, s);
            }
            return s;
        }
    }

    public String buildUrl(String url) {
        return urlHelper.buildUrl(url, request, response, null);
    }

    public Object findValue(String expression, String className) throws ClassNotFoundException {
        return stack.findValue(expression, Class.forName(className));
    }

    public String getText(String text) {
        return (String) stack.findValue("getText('" + text + "')");
    }

    /*
     * @return the url ContextPath. An empty string if one does not exist.
     */
    public String getContext() {
        return (request == null)? "" : request.getContextPath();
    }

    public String translateVariables(String expression) {
        return TextParseUtil.translateVariables(expression, stack);
    }

    /**
     * the selectedList objects are matched to the list.listValue
     * <p/>
     * listKey and listValue are optional, and if not provided, the list item is used
     *
     * @param selectedList the name of the action property
     *                     that contains the list of selected items
     *                     or single item if its not an array or list
     * @param list         the name of the action property
     *                     that contains the list of selectable items
     * @param listKey      an ognl expression that is exaluated relative to the list item
     *                     to use as the key of the ListEntry
     * @param listValue    an ognl expression that is exaluated relative to the list item
     *                     to use as the value of the ListEntry
     * @return a List of ListEntry
     */
    public List makeSelectList(String selectedList, String list, String listKey, String listValue) {
        List selectList = new ArrayList();

        Collection selectedItems = null;

        Object i = stack.findValue(selectedList);

        if (i != null) {
            if (i.getClass().isArray()) {
                selectedItems = Arrays.asList((Object[]) i);
            } else if (i instanceof Collection) {
                selectedItems = (Collection) i;
            } else {
                // treat it is a single item
                selectedItems = new ArrayList();
                selectedItems.add(i);
            }
        }

        Collection items = (Collection) stack.findValue(list);

        if (items != null) {
            for (Object element : items) {
                Object key;

                if ((listKey == null) || (listKey.length() == 0)) {
                    key = element;
                } else {
                    key = ognl.findValue(listKey, element);
                }

                Object value = null;

                if ((listValue == null) || (listValue.length() == 0)) {
                    value = element;
                } else {
                    value = ognl.findValue(listValue, element);
                }

                boolean isSelected = false;

                if ((value != null) && (selectedItems != null) && selectedItems.contains(value)) {
                    isSelected = true;
                }

                selectList.add(new ListEntry(key, value, isSelected));
            }
        }

        return selectList;
    }

    public int toInt(long aLong) {
        return (int) aLong;
    }

    public long toLong(int anInt) {
        return (long) anInt;
    }

    public long toLong(String aLong) {
        if (aLong == null) {
            return 0;
        }

        return Long.parseLong(aLong);
    }

    public String toString(long aLong) {
        return Long.toString(aLong);
    }

    public String toString(int anInt) {
        return Integer.toString(anInt);
    }

    public String toStringSafe(Object obj) {
        try {
            if (obj != null) {
                return String.valueOf(obj);
            }
            return "";
        } catch (Exception e) {
            return "Exception thrown: " + e;
        }
    }

    static class ResponseWrapper extends HttpServletResponseWrapper {
        StringWriter strout;
        PrintWriter writer;
        ServletOutputStream sout;

        ResponseWrapper(HttpServletResponse aResponse) {
            super(aResponse);
            strout = new StringWriter();
            sout = new ServletOutputStreamWrapper(strout);
            writer = new PrintWriter(strout);
        }

        public String getData() {
            writer.flush();

            return strout.toString();
        }

        public ServletOutputStream getOutputStream() {
            return sout;
        }

        public PrintWriter getWriter() throws IOException {
            return writer;
        }
    }

    static class ServletOutputStreamWrapper extends ServletOutputStream {
        StringWriter writer;

        ServletOutputStreamWrapper(StringWriter aWriter) {
            writer = aWriter;
        }

        public void write(int aByte) {
            writer.write(aByte);
        }
    }

}
