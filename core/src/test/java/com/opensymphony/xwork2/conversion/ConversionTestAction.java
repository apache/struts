/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.conversion;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.conversion.annotations.Conversion;
import com.opensymphony.xwork2.conversion.annotations.ConversionRule;
import com.opensymphony.xwork2.conversion.annotations.ConversionType;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

import java.util.HashMap;
import java.util.List;

/**
 * <code>ConversionTestAction</code>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
@Conversion()
public class ConversionTestAction implements Action {



    private String convertInt;

    private String convertDouble;

    private List users = null;


    private HashMap keyValues = null;


    public String getConvertInt() {
        return convertInt;
    }

    @TypeConversion(type = ConversionType.APPLICATION, converter = "com.opensymphony.xwork2.util.XWorkBasicConverter")
    public void setConvertInt( String convertInt ) {
        this.convertInt = convertInt;
    }

    public String getConvertDouble() {
        return convertDouble;
    }

    @TypeConversion(converter = "com.opensymphony.xwork2.util.XWorkBasicConverter")
    public void setConvertDouble( String convertDouble ) {
        this.convertDouble = convertDouble;
    }

    public List getUsers() {
        return users;
    }

    @TypeConversion(rule = ConversionRule.COLLECTION, converter = "java.lang.String")
    public void setUsers( List users ) {
        this.users = users;
    }

    public HashMap getKeyValues() {
        return keyValues;
    }

    @TypeConversion(rule = ConversionRule.MAP, converter = "java.math.BigInteger")
    public void setKeyValues( HashMap keyValues ) {
        this.keyValues = keyValues;
    }

    /**
     * Where the logic of the action is executed.
     *
     * @return a string representing the logical result of the execution.
     *         See constants in this interface for a list of standard result values.
     * @throws Exception thrown if a system level exception occurs.
     *                   Application level exceptions should be handled by returning
     *                   an error value, such as Action.ERROR.
     */
    @TypeConversion(type = ConversionType.APPLICATION, key = "java.util.Date", converter = "com.opensymphony.xwork2.util.XWorkBasicConverter")
    public String execute() throws Exception {
        return SUCCESS;
    }
}
