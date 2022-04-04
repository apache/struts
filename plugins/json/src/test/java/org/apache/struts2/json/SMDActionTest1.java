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
package org.apache.struts2.json;

import java.util.List;
import java.util.Map;

import org.apache.struts2.json.annotations.SMDMethod;

public class SMDActionTest1 {
    private boolean addWasCalled;
    @SuppressWarnings("unchecked")
    private List listParam;
    @SuppressWarnings("unchecked")
    private Map mapParam;
    private Bean beanParam;
    private String stringParam;
    private int intParam;
    private boolean booleanParam;
    private char charParam;
    private long longParam;
    private float floatParam;
    private double doubleParam;
    private short shortParam;
    private Object objectParam;
    private byte byteParam;

    @SMDMethod
    public void doSomethingPrimitives(String stringParam, int intParam, boolean booleanParam, char charParam,
            long longParam, float floatParam, double doubleParam, short shortParam, byte byteParam) {
        this.stringParam = stringParam;
        this.intParam = intParam;
        this.booleanParam = booleanParam;
        this.charParam = charParam;
        this.longParam = longParam;
        this.floatParam = floatParam;
        this.doubleParam = doubleParam;
        this.byteParam = byteParam;
        this.shortParam = shortParam;
    }

    @SuppressWarnings("unchecked")
    @SMDMethod
    public void doSomethingObjects(Bean beanParam, Map mapParam, List listParam) {
        this.beanParam = beanParam;
        this.mapParam = mapParam;
        this.listParam = listParam;
    }

    @SMDMethod
    public void add(int a, int b) {
        this.addWasCalled = true;
    }

    @SMDMethod
    public void doSomething() {

    }

    public void methodWithoutAnnotation() {

    }

    public boolean isAddWasCalled() {
        return this.addWasCalled;
    }

    public void setAddWasCalled(boolean addWasCalled) {
        this.addWasCalled = addWasCalled;
    }

    @SuppressWarnings("unchecked")
    public List getListParam() {
        return this.listParam;
    }

    @SuppressWarnings("unchecked")
    public void setListParam(List listParam) {
        this.listParam = listParam;
    }

    @SuppressWarnings("unchecked")
    public Map getMapParam() {
        return this.mapParam;
    }

    @SuppressWarnings("unchecked")
    public void setMapParam(Map mapParam) {
        this.mapParam = mapParam;
    }

    public Bean getBeanParam() {
        return this.beanParam;
    }

    public void setBeanParam(Bean beanParam) {
        this.beanParam = beanParam;
    }

    public String getStringParam() {
        return this.stringParam;
    }

    public void setStringParam(String stringParam) {
        this.stringParam = stringParam;
    }

    public int getIntParam() {
        return this.intParam;
    }

    public void setIntParam(int intParam) {
        this.intParam = intParam;
    }

    public boolean isBooleanParam() {
        return this.booleanParam;
    }

    public void setBooleanParam(boolean booleanParam) {
        this.booleanParam = booleanParam;
    }

    public char getCharParam() {
        return this.charParam;
    }

    public void setCharParam(char charParam) {
        this.charParam = charParam;
    }

    public long getLongParam() {
        return this.longParam;
    }

    public void setLongParam(long longParam) {
        this.longParam = longParam;
    }

    public float getFloatParam() {
        return this.floatParam;
    }

    public void setFloatParam(float floatParam) {
        this.floatParam = floatParam;
    }

    public double getDoubleParam() {
        return this.doubleParam;
    }

    public void setDoubleParam(double doubleParam) {
        this.doubleParam = doubleParam;
    }

    public Object getObjectParam() {
        return this.objectParam;
    }

    public void setObjectParam(Object objectParam) {
        this.objectParam = objectParam;
    }

    public byte getByteParam() {
        return this.byteParam;
    }

    public void setByteParam(byte byteParam) {
        this.byteParam = byteParam;
    }

    public short getShortParam() {
        return this.shortParam;
    }

    public void setShortParam(short shortParam) {
        this.shortParam = shortParam;
    }
}
