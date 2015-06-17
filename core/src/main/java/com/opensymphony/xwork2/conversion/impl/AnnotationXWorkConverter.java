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
package com.opensymphony.xwork2.conversion.impl;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p/>
 * Type conversion is great for situations where you need to turn a String in to a more complex object. Because the web
 * is type-agnostic (everything is a string in HTTP), XWork's type conversion features are very useful. For instance,
 * if you were prompting a user to enter in coordinates in the form of a string (such as "3, 22"), you could have
 * XWork do the conversion both from String to Point and from Point to String.
 * <p/>
 * <p/> Using this "point" example, if your action (or another compound object in which you are setting properties on)
 * has a corresponding ClassName-conversion.properties file, XWork will use the configured type converters for
 * conversion to and from strings. So turning "3, 22" in to new Point(3, 22) is done by merely adding the following
 * entry to <b>ClassName-conversion.properties</b> (Note that the PointConverter should impl the TypeConverter
 * interface):
 * <p/>
 * <p/><b>point = com.acme.PointConverter</b>
 * <p/>
 * <p/> Your type converter should be sure to check what class type it is being requested to convert. Because it is used
 * for both to and from strings, you will need to split the conversion method in to two parts: one that turns Strings in
 * to Points, and one that turns Points in to Strings.
 * <p/>
 * <p/> After this is done, you can now reference your point (using &lt;ww:property value="post"/&gt; in JSP or ${point}
 * in FreeMarker) and it will be printed as "3, 22" again. As such, if you submit this back to an action, it will be
 * converted back to a Point once again.
 * <p/>
 * <p/> In some situations you may wish to apply a type converter globally. This can be done by editing the file
 * <b>xwork-conversion.properties</b> in the root of your class path (typically WEB-INF/classes) and providing a
 * property in the form of the class name of the object you wish to convert on the left hand side and the class name of
 * the type converter on the right hand side. For example, providing a type converter for all Point objects would mean
 * adding the following entry:
 * <p/>
 * <p/><b>com.acme.Point = com.acme.PointConverter</b>
 * <p/>
 * <!-- END SNIPPET: javadoc -->
 * <p/>
 * <p/>
 * <p/>
 * <!-- START SNIPPET: i18n-note -->
 * <p/>
 * Type conversion should not be used as a substitute for i18n. It is not recommended to use this feature to print out
 * properly formatted dates. Rather, you should use the i18n features of XWork (and consult the JavaDocs for JDK's
 * MessageFormat object) to see how a properly formatted date should be displayed.
 * <p/>
 * <!-- END SNIPPET: i18n-note -->
 * <p/>
 * <p/>
 * <p/>
 * <!-- START SNIPPET: error-reporting -->
 * <p/>
 * Any error that occurs during type conversion may or may not wish to be reported. For example, reporting that the
 * input "abc" could not be converted to a number might be important. On the other hand, reporting that an empty string,
 * "", cannot be converted to a number might not be important - especially in a web environment where it is hard to
 * distinguish between a user not entering a value vs. entering a blank value.
 * <p/>
 * <p/> By default, all conversion errors are reported using the generic i18n key <b>xwork.default.invalid.fieldvalue</b>,
 * which you can override (the default text is <i>Invalid field value for field "xxx"</i>, where xxx is the field name)
 * in your global i18n resource bundle.
 * <p/>
 * <p/>However, sometimes you may wish to override this message on a per-field basis. You can do this by adding an i18n
 * key associated with just your action (Action.properties) using the pattern <b>invalid.fieldvalue.xxx</b>, where xxx
 * is the field name.
 * <p/>
 * <p/>It is important to know that none of these errors are actually reported directly. Rather, they are added to a map
 * called <i>conversionErrors</i> in the ActionContext. There are several ways this map can then be accessed and the
 * errors can be reported accordingly.
 * <p/>
 * <!-- END SNIPPET: error-reporting -->
 *
 * @author <a href="mailto:plightbo@gmail.com">Pat Lightbody</a>
 * @author Rainer Hermanns
 * @see com.opensymphony.xwork2.conversion.impl.XWorkConverter
 * @deprecated Since XWork 2.0.4, the implementation of XWorkConverter handles the processing of annotations.
 */
@Deprecated public class AnnotationXWorkConverter extends XWorkConverter {
}
