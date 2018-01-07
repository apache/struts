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
package com.opensymphony.xwork2.conversion.annotations;

import com.opensymphony.xwork2.conversion.impl.XWorkBasicConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <!-- START SNIPPET: description -->
 * <p>This annotation is used for class and application wide conversion rules.</p>
 *
 * <p>
 * Class wide conversion:<br>
 * The conversion rules will be assembled in a file called <code>XXXAction-conversion.properties</code>
 * within the same package as the related action class.
 * Set type to: <code>type = ConversionType.CLASS</code>
 * </p>
 *
 * <p>
 * Application wide conversion:<br>
 * The conversion rules will be assembled within the <code>xwork-conversion.properties</code> file within the classpath root.
 * Set type to: <code>type = ConversionType.APPLICATION</code>
 * </p>
 * <!-- END SNIPPET: description -->
 *
 * <p><u>Annotation usage:</u></p>
 *
 * <!-- START SNIPPET: usage -->
 * <p>The TypeConversion annotation can be applied at property and method level.</p>
 * <!-- END SNIPPET: usage -->
 *
 * <p><u>Annotation parameters:</u></p>
 *
 * <!-- START SNIPPET: parameters -->
 * <table summary="">
 * <thead>
 * <tr>
 * <th>Parameter</th>
 * <th>Required</th>
 * <th>Default</th>
 * <th>Description</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>key</td>
 * <td>no</td>
 * <td>The annotated property/key name</td>
 * <td>The optional property name mostly used within TYPE level annotations.</td>
 * </tr>
 * <tr>
 * <td>type</td>
 * <td>no</td>
 * <td>ConversionType.CLASS</td>
 * <td>Enum value of ConversionType.  Determines whether the conversion should be applied at application or class level.</td>
 * </tr>
 * <tr>
 * <td>rule</td>
 * <td>no</td>
 * <td>ConversionRule.PROPERTY</td>
 * <td>Enum value of ConversionRule. The ConversionRule can be a property, a Collection or a Map.</td>
 * </tr>
 * <tr>
 * <td>converter</td>
 * <td>either this or value</td>
 * <td>&nbsp;</td>
 * <td>The class or bean name of the TypeConverter to be used as converter.</td>
 * </tr>
 * <tr>
 * <td>converterClass</td>
 * <td>either this or value</td>
 * <td>XWorkBasicConverter</td>
 * <td>The class of the TypeConverter to be used as converter.</td>
 * </tr>
 * <tr>
 * <td>value</td>
 * <td>either converter or this</td>
 * <td>&nbsp;</td>
 * <td>The value to set for ConversionRule.KEY_PROPERTY.</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p> <u>Example code:</u></p>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &#64;Conversion()
 * public class ConversionAction implements Action {
 *
 *   private String convertInt;
 *
 *   private String convertDouble;
 *   private List users = null;
 *
 *   private HashMap keyValues = null;
 *
 *   &#64;TypeConversion(type = ConversionType.APPLICATION)
 *   public void setConvertInt( String convertInt ) {
 *       this.convertInt = convertInt;
 *   }
 *
 *   &#64;TypeConversion(converterClass = XWorkBasicConverter.class)
 *   public void setConvertDouble( String convertDouble ) {
 *       this.convertDouble = convertDouble;
 *   }
 *
 *   &#64;TypeConversion(rule = ConversionRule.COLLECTION, converterClass = String.class)
 *   public void setUsers( List users ) {
 *       this.users = users;
 *   }
 *
 *   &#64;TypeConversion(rule = ConversionRule.MAP, converterClass = BigInteger.class)
 *   public void setKeyValues( HashMap keyValues ) {
 *       this.keyValues = keyValues;
 *   }
 *
 *   &#64;TypeConversion(type = ConversionType.APPLICATION, property = "java.util.Date", converterClass = XWorkBasicConverter.class)
 *   public String execute() throws Exception {
 *       return SUCCESS;
 *   }
 * }
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeConversion {

    /**
     * The optional key name used within TYPE level annotations.
     * Defaults to the property name.
     *
     * @return key
     */
    String key() default "";

    /**
     * The ConversionType can be either APPLICATION or CLASS.
     * Defaults to CLASS.
     *
     * Note: If you use ConversionType.APPLICATION, you can not set a value!
     *
     * @return the conversion type
     */
    ConversionType type() default ConversionType.CLASS;

    /**
     * The ConversionRule can be a PROPERTY, KEY, KEY_PROPERTY, ELEMENT, COLLECTION (deprecated) or a MAP.
     * Note: Collection and Map conversion rules can be determined via com.opensymphony.xwork2.util.DefaultObjectTypeDeterminer.
     *
     * @see com.opensymphony.xwork2.conversion.impl.DefaultObjectTypeDeterminer
     *
     * @return the conversion rule
     */
    ConversionRule rule() default ConversionRule.PROPERTY;

    /**
     * The class or bean name of the TypeConverter to be used as converter.
     *
     * Note: This can not be used with ConversionRule.KEY_PROPERTY!
     *
     * @return class or bean name of the TypeConverter to be used as converter
     * @see {@link #converterClass()}
     */
    String converter() default "";

    /**
     * The class of the TypeConverter to be used as converter.
     *
     * Note: This can not be used with ConversionRule.KEY_PROPERTY!
     *
     * @return class of the TypeConverter to be used as converter
     */
    Class<?> converterClass() default XWorkBasicConverter.class;

    /**
     * If used with ConversionRule.KEY_PROPERTY specify a value here!
     *
     * Note: If you use ConversionType.APPLICATION, you can not set a value!
     *
     * @return value
     */
    String value() default "";

}
