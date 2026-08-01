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
package org.apache.struts2.conversion.annotations;

import org.apache.struts2.conversion.impl.XWorkBasicConverter;
import org.apache.struts2.conversion.impl.DefaultObjectTypeDeterminer;

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
 * The conversion rules will be assembled within the <code>struts-conversion.properties</code> or
 * <code>xwork-conversion.properties</code> (deprecated) file within the classpath root.
 * Set type to: <code>type = ConversionType.APPLICATION</code>
 * </p>
 * <!-- END SNIPPET: description -->
 *
 * <p><u>Annotation usage:</u></p>
 *
 * <!-- START SNIPPET: usage -->
 * <p>The TypeConversion annotation can be applied at field and method level.</p>
 * <!-- END SNIPPET: usage -->
 *
 * <p>The {@code org.apache.struts2.util} package also has dedicated {@code @Key},
 * {@code @Element}, {@code @KeyProperty} and {@code @CreateIfNull} annotations that {@link
 * org.apache.struts2.conversion.impl.DefaultObjectTypeDeterminer} consults, on the field then its
 * setter then its getter, <em>before</em> falling back to the converter mapping this annotation
 * populates. If both a dedicated annotation and an equivalent {@code @TypeConversion} are declared
 * for the same property, the dedicated annotation wins silently.</p>
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
 * <td>The resolved property name on a method; the field's own name on a field</td>
 * <td>The property name the rule applies to. The matching prefix for the given rule
 * (<code>Key_</code>, <code>Element_</code>, <code>KeyProperty_</code>, <code>CreateIfNull_</code>, or the deprecated
 * <code>Collection_</code>) is prepended automatically unless the key already carries it. Required on TYPE level annotations,
 * where there is no member name to derive it from.</td>
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
 *
 *   private HashMap keyValues = null;
 *
 *   &#64;TypeConversion()
 *   public void setConvertInt( String convertInt ) {
 *       this.convertInt = convertInt;
 *   }
 *
 *   &#64;TypeConversion(converterClass = XWorkBasicConverter.class)
 *   public void setConvertDouble( String convertDouble ) {
 *       this.convertDouble = convertDouble;
 *   }
 *
 *   &#64;TypeConversion(rule = ConversionRule.CREATE_IF_NULL, value = "true")
 *   private List users = null;
 *
 *   &#64;TypeConversion(rule = ConversionRule.ELEMENT, converterClass = String.class)
 *   public void setUsers( List users ) {
 *       this.users = users;
 *   }
 *
 *   &#64;TypeConversion(rule = ConversionRule.MAP, converterClass = BigInteger.class)
 *   public void setKeyValues( HashMap keyValues ) {
 *       this.keyValues = keyValues;
 *   }
 *
 *   &#64;TypeConversion(type = ConversionType.APPLICATION, key = "java.util.Date", converterClass = XWorkBasicConverter.class)
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
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeConversion {

    /**
     * The property name this conversion applies to. Optional on a method, where it defaults to the
     * resolved JavaBean property name; optional on a field, where it defaults to the <em>field's own
     * name</em> instead - not necessarily the same thing. Required on TYPE level annotations.
     *
     * <p>The prefix matching the declared {@link ConversionRule} is prepended automatically, so
     * {@code @TypeConversion(key = "users", rule = ConversionRule.CREATE_IF_NULL, value = "true")}
     * and {@code @TypeConversion(key = "CreateIfNull_users", ...)} are equivalent.</p>
     *
     * <p>If a field's name does not match the property it backs (for example a field {@code _users}
     * exposed as property {@code users}), give an explicit {@code key} of {@code "users"} - a derived
     * key of {@code CreateIfNull__users} is never looked up, since conversion metadata is read by
     * property name, not field name.</p>
     *
     * @return key
     * @since 7.3.0 the rule prefix is derived; previously the full key had to be spelled out
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
     * The ConversionRule can be a PROPERTY, KEY, KEY_PROPERTY, ELEMENT, CREATE_IF_NULL or a MAP.
     * {@link ConversionRule#COLLECTION} is also accepted, but deprecated - use ELEMENT instead.
     * Note: Collection and Map conversion rules can be determined via org.apache.struts2.conversion.impl.DefaultObjectTypeDeterminer.
     *
     * @see DefaultObjectTypeDeterminer
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
