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

public class WrapperClassBean {

    private String stringField;
    private Integer intField;
    private int nullIntField;
    private Boolean booleanField;
    private boolean primitiveBooleanField1;
    private boolean primitiveBooleanField2;
    private boolean primitiveBooleanField3;
    private Character charField;
    private Long longField;
    private Float floatField;
    private Double doubleField;
    private Object objectField;
    private Byte byteField;
    private List<SimpleValue> listField;
    private List<Map<String, Long>> listMapField;
    private Map<String, List<Long>> mapListField;
    private Map<String, Long>[] arrayMapField;

    public List<SimpleValue> getListField() {
        return listField;
    }

    public void setListField(List<SimpleValue> listField) {
        this.listField = listField;
    }

    public List<Map<String, Long>> getListMapField() {
        return listMapField;
    }

    public void setListMapField(List<Map<String, Long>> listMapField) {
        this.listMapField = listMapField;
    }

    public Map<String, List<Long>> getMapListField() {
        return mapListField;
    }

    public void setMapListField(Map<String, List<Long>> mapListField) {
        this.mapListField = mapListField;
    }

    public Map<String, Long>[] getArrayMapField() {
        return arrayMapField;
    }

    public void setArrayMapField(Map<String, Long>[] arrayMapField) {
        this.arrayMapField = arrayMapField;
    }

    public Boolean getBooleanField() {
        return booleanField;
    }

    public void setBooleanField(Boolean booleanField) {
        this.booleanField = booleanField;
    }

    public boolean isPrimitiveBooleanField1() {
        return primitiveBooleanField1;
    }

    public void setPrimitiveBooleanField1(boolean primitiveBooleanField1) {
        this.primitiveBooleanField1 = primitiveBooleanField1;
    }

    public boolean isPrimitiveBooleanField2() {
        return primitiveBooleanField2;
    }

    public void setPrimitiveBooleanField2(boolean primitiveBooleanField2) {
        this.primitiveBooleanField2 = primitiveBooleanField2;
    }

    public boolean isPrimitiveBooleanField3() {
        return primitiveBooleanField3;
    }

    public void setPrimitiveBooleanField3(boolean primitiveBooleanField3) {
        this.primitiveBooleanField3 = primitiveBooleanField3;
    }

    public Byte getByteField() {
        return byteField;
    }

    public void setByteField(Byte byteField) {
        this.byteField = byteField;
    }

    public Character getCharField() {
        return charField;
    }

    public void setCharField(Character charField) {
        this.charField = charField;
    }

    public Double getDoubleField() {
        return doubleField;
    }

    public void setDoubleField(Double doubleField) {
        this.doubleField = doubleField;
    }

    public Float getFloatField() {
        return floatField;
    }

    public void setFloatField(Float floatField) {
        this.floatField = floatField;
    }

    public Integer getIntField() {
        return intField;
    }

    public void setIntField(Integer intField) {
        this.intField = intField;
    }

    public int getNullIntField() {
        return nullIntField;
    }

    public void setNullIntField(int nullIntField) {
        this.nullIntField = nullIntField;
    }

    public Long getLongField() {
        return longField;
    }

    public void setLongField(Long longField) {
        this.longField = longField;
    }

    public Object getObjectField() {
        return objectField;
    }

    public void setObjectField(Object objectField) {
        this.objectField = objectField;
    }

    public String getStringField() {
        return stringField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }
}
