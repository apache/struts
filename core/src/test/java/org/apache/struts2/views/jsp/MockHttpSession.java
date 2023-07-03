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
package org.apache.struts2.views.jsp;

import com.mockobjects.ExpectationSet;
import com.mockobjects.MapEntry;
import com.mockobjects.MockObject;
import com.mockobjects.ReturnObjectBag;
import com.mockobjects.Verifiable;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import java.util.Enumeration;

public class MockHttpSession  extends MockObject implements HttpSession, Verifiable {
    private ExpectationSet myAttributes = new ExpectationSet("session attributes");
    private ExpectationSet myRemovedAttributes = new ExpectationSet("removed session attributes");
    private ReturnObjectBag myAttributeValues = new ReturnObjectBag("attributes");
    private Enumeration attributeNames;
    private ServletContext servletContext;

    public MockHttpSession() {
    }

    public Object getAttribute(String anAttributeName) {
        return this.myAttributeValues.getNextReturnObject(anAttributeName);
    }

    public void setupGetAttributeNames(Enumeration attributeNames) {
        this.attributeNames = attributeNames;
    }

    public Enumeration getAttributeNames() {
        return this.attributeNames;
    }

    public long getCreationTime() {
        this.notImplemented();
        return 0L;
    }

    public String getId() {
        this.notImplemented();
        return null;
    }

    public long getLastAccessedTime() {
        this.notImplemented();
        return 0L;
    }

    public int getMaxInactiveInterval() {
        this.notImplemented();
        return 0;
    }

    public void setupServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public Object getValue(String arg1) {
        this.notImplemented();
        return null;
    }

    public String[] getValueNames() {
        this.notImplemented();
        return null;
    }

    public void invalidate() {
        this.notImplemented();
    }

    public boolean isNew() {
        this.notImplemented();
        return false;
    }

    public void putValue(String arg1, Object arg2) {
        this.notImplemented();
    }

    public void setExpectedRemoveAttribute(String anAttributeName) {
        this.myRemovedAttributes.addExpected(anAttributeName);
    }

    public void removeAttribute(String anAttributeName) {
        this.myRemovedAttributes.addActual(anAttributeName);
    }

    public void removeValue(String arg1) {
        this.notImplemented();
    }

    public void setupGetAttribute(String key, Object value) {
        this.myAttributeValues.putObjectToReturn(key, value);
    }

    public void setAttribute(String aKey, Object aValue) {
        this.myAttributes.addActual(new MapEntry(aKey, aValue));
    }

    public void setExpectedAttribute(String aKey, Object aValue) {
        this.myAttributes.addExpected(new MapEntry(aKey, aValue));
    }

    public void setMaxInactiveInterval(int arg1) {
        this.notImplemented();
    }
}