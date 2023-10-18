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
package org.apache.struts2.util;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;
import org.apache.struts2.views.util.UrlHelper;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Struts base utility class, for use in Velocity and Freemarker templates
 */
public class StrutsUtil {

    protected static final Logger LOG = LogManager.getLogger(StrutsUtil.class);

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected Map<String, Class<?>> classes = new HashMap<>();
    protected ValueStack stack;

    private final UrlHelper urlHelper;
    private final ObjectFactory objectFactory;

    public StrutsUtil(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        this.stack = stack;
        this.request = request;
        this.response = response;
        this.urlHelper = stack.getActionContext().getContainer().getInstance(UrlHelper.class);
        this.objectFactory = stack.getActionContext().getContainer().getInstance(ObjectFactory.class);
    }

    public Object bean(Object name) throws Exception {
        String className = name.toString();
        Class<?> clazz = classes.get(className);
        if (clazz == null) {
            clazz = ClassLoaderUtil.loadClass(className, StrutsUtil.class);
            classes.put(className, clazz);
        }
        return objectFactory.buildBean(clazz, stack.getContext());
    }

    public boolean isTrue(String expression) {
        Boolean retVal = (Boolean) stack.findValue(expression, Boolean.class);
        return retVal != null && retVal;
    }

    public Object findString(String name) {
        return stack.findValue(name, String.class);
    }

    public String include(Object aName) throws Exception {
        RequestDispatcher dispatcher = request.getRequestDispatcher(aName.toString());
        if (dispatcher == null) {
            throw new IllegalArgumentException("Cannot find included file " + aName);
        }
        ResponseWrapper responseWrapper = new ResponseWrapper(response);
        dispatcher.include(request, responseWrapper);
        return responseWrapper.getData();
    }

    public String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.debug(format("Cannot encode URL [{0}]", s), e);
            return s;
        }
    }

    public String buildUrl(String url) {
        return urlHelper.buildUrl(url, request, response, null);
    }

    public Object findValue(String expression, String className) throws ClassNotFoundException {
        return stack.findValue(expression, Class.forName(className));
    }

    public Object findValue(String expr, Object context) {
        stack.push(context);
        try {
            return stack.findValue(expr, true);
        } finally {
            stack.pop();
        }
    }

    public String getText(String text) {
        return (String) stack.findValue("getText('" + text.replace('\'', '"') + "')");
    }

    /*
     * @return the url ContextPath. An empty string if one does not exist.
     */
    public String getContext() {
        return request == null ? "" : request.getContextPath();
    }

    public String translateVariables(String expression) {
        return TextParseUtil.translateVariables('%', expression, stack);
    }

    /**
     * the selectedList objects are matched to the list.listValue
     * <p>
     * listKey and listValue are optional, and if not provided, the list item is used
     * </p>
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
    public List<ListEntry> makeSelectList(String selectedList, String list, String listKey, String listValue) {
        List<ListEntry> selectList = new ArrayList<>();

        Collection items = (Collection) stack.findValue(list);
        if (items == null) {
            return selectList;
        }

        Collection selectedItems = getSelectedItems(selectedList);
        for (Object element : items) {
            Object key = computeKey(listKey, element);
            Object value = computeValue(listValue, element);
            boolean isSelected = value != null && selectedItems.contains(value);
            selectList.add(new ListEntry(key, value, isSelected));
        }

        return selectList;
    }

    private Collection getSelectedItems(String selectedListName) {
        Object i = stack.findValue(selectedListName);
        if (i == null) {
            return emptyList();
        }
        if (i.getClass().isArray()) {
            return Arrays.asList((Object[]) i);
        } else if (i instanceof Collection) {
            return (Collection) i;
        }
        return singletonList(i);
    }

    private Object computeKey(String listKey, Object element) {
        if (listKey == null || listKey.isEmpty()) {
            return element;
        }
        return findValue(listKey, element);
    }

    private Object computeValue(String listValue, Object element) {
        if (listValue == null || listValue.isEmpty()) {
            return element;
        }
        return findValue(listValue, element);
    }

    public int toInt(long aLong) {
        return (int) aLong;
    }

    public long toLong(int anInt) {
        return anInt;
    }

    public long toLong(String aLong) {
        if (aLong == null || aLong.isEmpty()) {
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
        return obj == null ? "" : obj.toString();
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

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            try {
                writeListener.onWritePossible();
            } catch (IOException e) {
                throw new StrutsException(e);
            }
        }
    }

}
