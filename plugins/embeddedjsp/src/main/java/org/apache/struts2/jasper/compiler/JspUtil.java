/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.struts2.jasper.compiler;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.struts2.el.ExpressionFactoryImpl;
import org.apache.struts2.jasper.Constants;
import org.apache.struts2.jasper.JasperException;
import org.apache.struts2.jasper.JspCompilationContext;
import org.apache.struts2.jasper.el.ExpressionEvaluatorImpl;
import org.xml.sax.Attributes;

import javax.el.FunctionMapper;
import javax.servlet.jsp.el.ExpressionEvaluator;
import java.io.*;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * This class has all the utility method(s).
 * Ideally should move all the bean containers here.
 *
 * @author Mandar Raje.
 * @author Rajiv Mordani.
 * @author Danno Ferrin
 * @author Pierre Delisle
 * @author Shawn Bayern
 * @author Mark Roth
 */
public class JspUtil {

    private static final String WEB_INF_TAGS = "/WEB-INF/tags/";
    private static final String META_INF_TAGS = "/META-INF/tags/";

    // Delimiters for request-time expressions (JSP and XML syntax)
    private static final String OPEN_EXPR = "<%=";
    private static final String CLOSE_EXPR = "%>";
    private static final String OPEN_EXPR_XML = "%=";
    private static final String CLOSE_EXPR_XML = "%";

    private static int tempSequenceNumber = 0;

    //private static ExpressionEvaluatorImpl expressionEvaluator
    //= new ExpressionEvaluatorImpl();

    //tc6
    private final static ExpressionEvaluator expressionEvaluator =
            new ExpressionEvaluatorImpl(new ExpressionFactoryImpl());

    private static final String javaKeywords[] = {
            "abstract", "assert", "boolean", "break", "byte", "case",
            "catch", "char", "class", "const", "continue",
            "default", "do", "double", "else", "enum", "extends",
            "final", "finally", "float", "for", "goto",
            "if", "implements", "import", "instanceof", "int",
            "interface", "long", "native", "new", "package",
            "private", "protected", "public", "return", "short",
            "static", "strictfp", "super", "switch", "synchronized",
            "this", "throws", "transient", "try", "void",
            "volatile", "while"};

    public static final int CHUNKSIZE = 1024;

    public static char[] removeQuotes(char[] chars) {
        CharArrayWriter caw = new CharArrayWriter();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '%' && chars[i + 1] == '\\' &&
                    chars[i + 2] == '>') {
                caw.write('%');
                caw.write('>');
                i = i + 2;
            } else {
                caw.write(chars[i]);
            }
        }
        return caw.toCharArray();
    }

    public static char[] escapeQuotes(char[] chars) {
        // Prescan to convert %\> to %>
        String s = new String(chars);
        while (true) {
            int n = s.indexOf("%\\>");
            if (n < 0)
                break;
            StringBuffer sb = new StringBuffer(s.substring(0, n));
            sb.append("%>");
            sb.append(s.substring(n + 3));
            s = sb.toString();
        }
        chars = s.toCharArray();
        return (chars);


        // Escape all backslashes not inside a Java string literal
        /*
        CharArrayWriter caw = new CharArrayWriter();
        boolean inJavaString = false;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '"') inJavaString = !inJavaString;
            // escape out the escape character
            if (!inJavaString && (chars[i] == '\\')) caw.write('\\');
            caw.write(chars[i]);
        }
        return caw.toCharArray();
        */
    }

    /**
     * Checks if the token is a runtime expression.
     * In standard JSP syntax, a runtime expression starts with '&lt;%' and
     * ends with '%&gt;'. When the JSP document is in XML syntax, a runtime
     * expression starts with '%=' and ends with '%'.
     *
     * @param token The token to be checked
     * @param isXml is xml syntax
     * @return whether the token is a runtime expression or not.
     */
    public static boolean isExpression(String token, boolean isXml) {
        String openExpr;
        String closeExpr;
        if (isXml) {
            openExpr = OPEN_EXPR_XML;
            closeExpr = CLOSE_EXPR_XML;
        } else {
            openExpr = OPEN_EXPR;
            closeExpr = CLOSE_EXPR;
        }
        if (token.startsWith(openExpr) && token.endsWith(closeExpr)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param expression expression string
     * @param isXml      is xml
     * @return the "expression" part of a runtime expression,
     * taking the delimiters out.
     */
    public static String getExpr(String expression, boolean isXml) {
        String returnString;
        String openExpr;
        String closeExpr;
        if (isXml) {
            openExpr = OPEN_EXPR_XML;
            closeExpr = CLOSE_EXPR_XML;
        } else {
            openExpr = OPEN_EXPR;
            closeExpr = CLOSE_EXPR;
        }
        int length = expression.length();
        if (expression.startsWith(openExpr) &&
                expression.endsWith(closeExpr)) {
            returnString = expression.substring(
                    openExpr.length(), length - closeExpr.length());
        } else {
            returnString = "";
        }
        return returnString;
    }

    /**
     * Takes a potential expression and converts it into XML form
     *
     * @param expression expression string
     * @return expressions as xml
     */
    public static String getExprInXml(String expression) {
        String returnString;
        int length = expression.length();

        if (expression.startsWith(OPEN_EXPR)
                && expression.endsWith(CLOSE_EXPR)) {
            returnString = expression.substring(1, length - 1);
        } else {
            returnString = expression;
        }

        return escapeXml(returnString.replace(Constants.ESC, '$'));
    }

    /**
     * Checks to see if the given scope is valid.
     *
     * @param scope The scope to be checked
     * @param n     The Node containing the 'scope' attribute whose value is to be
     *              checked
     * @param err   error dispatcher
     * @throws JasperException if scope is not null and different from
     *                         &quot;page&quot;, &quot;request&quot;, &quot;session&quot;, and
     *                         &quot;application&quot;
     */
    public static void checkScope(String scope, Node n, ErrorDispatcher err)
            throws JasperException {
        if (scope != null && !scope.equals("page") && !scope.equals("request")
                && !scope.equals("session") && !scope.equals("application")) {
            err.jspError(n, "jsp.error.invalid.scope", scope);
        }
    }

    /**
     * Checks if all mandatory attributes are present and if all attributes
     * present have valid names.  Checks attributes specified as XML-style
     * attributes as well as attributes specified using the jsp:attribute
     * standard action.
     *
     * @param typeOfTag       type of tag
     * @param n               node
     * @param validAttributes valid attributes
     * @param err             error dispatcher
     * @throws JasperException in case of Jasper errors
     */
    public static void checkAttributes(String typeOfTag,
                                       Node n,
                                       ValidAttribute[] validAttributes,
                                       ErrorDispatcher err)
            throws JasperException {
        Attributes attrs = n.getAttributes();
        Mark start = n.getStart();
        boolean valid = true;

        // AttributesImpl.removeAttribute is broken, so we do this...
        int tempLength = (attrs == null) ? 0 : attrs.getLength();
        Vector temp = new Vector(tempLength, 1);
        for (int i = 0; i < tempLength; i++) {
            String qName = attrs.getQName(i);
            if ((!qName.equals("xmlns")) && (!qName.startsWith("xmlns:")))
                temp.addElement(qName);
        }

        // Add names of attributes specified using jsp:attribute
        Node.Nodes tagBody = n.getBody();
        if (tagBody != null) {
            int numSubElements = tagBody.size();
            for (int i = 0; i < numSubElements; i++) {
                Node node = tagBody.getNode(i);
                if (node instanceof Node.NamedAttribute) {
                    String attrName = node.getAttributeValue("name");
                    temp.addElement(attrName);
                    // Check if this value appear in the attribute of the node
                    if (n.getAttributeValue(attrName) != null) {
                        err.jspError(n, "jsp.error.duplicate.name.jspattribute",
                                attrName);
                    }
                } else {
                    // Nothing can come before jsp:attribute, and only
                    // jsp:body can come after it.
                    break;
                }
            }
        }

    /*
     * First check to see if all the mandatory attributes are present.
     * If so only then proceed to see if the other attributes are valid
     * for the particular tag.
     */
        String missingAttribute = null;

        for (ValidAttribute validAttribute : validAttributes) {
            int attrPos;
            if (validAttribute.mandatory) {
                attrPos = temp.indexOf(validAttribute.name);
                if (attrPos != -1) {
                    temp.remove(attrPos);
                    valid = true;
                } else {
                    valid = false;
                    missingAttribute = validAttribute.name;
                    break;
                }
            }
        }

        // If mandatory attribute is missing then the exception is thrown
        if (!valid)
            err.jspError(start, "jsp.error.mandatory.attribute", typeOfTag,
                    missingAttribute);

        // Check to see if there are any more attributes for the specified tag.
        int attrLeftLength = temp.size();
        if (attrLeftLength == 0)
            return;

        // Now check to see if the rest of the attributes are valid too.
        String attribute = null;

        for (int j = 0; j < attrLeftLength; j++) {
            valid = false;
            attribute = (String) temp.elementAt(j);
            for (ValidAttribute validAttribute : validAttributes) {
                if (attribute.equals(validAttribute.name)) {
                    valid = true;
                    break;
                }
            }
            if (!valid)
                err.jspError(start, "jsp.error.invalid.attribute", typeOfTag,
                        attribute);
        }
        // XXX *could* move EL-syntax validation here... (sb)
    }

    public static String escapeQueryString(String unescString) {
        if (unescString == null)
            return null;

        String escString = "";
        String shellSpChars = "\\\"";

        for (int index = 0; index < unescString.length(); index++) {
            char nextChar = unescString.charAt(index);

            if (shellSpChars.indexOf(nextChar) != -1)
                escString += "\\";

            escString += nextChar;
        }
        return escString;
    }

    /**
     * Escape the 5 entities defined by XML.
     *
     * @param s xml string to escape
     * @return escaped xml string
     */
    public static String escapeXml(String s) {
        if (s == null) return null;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '<') {
                sb.append("&lt;");
            } else if (c == '>') {
                sb.append("&gt;");
            } else if (c == '\'') {
                sb.append("&apos;");
            } else if (c == '&') {
                sb.append("&amp;");
            } else if (c == '"') {
                sb.append("&quot;");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Replaces any occurrences of the character <tt>replace</tt> with the
     * string <tt>with</tt>.
     *
     * @param name  string
     * @param replace char to replace
     * @param with replace with
     *
     * @return replaced string
     */
    public static String replace(String name, char replace, String with) {
        StringBuilder buf = new StringBuilder();
        int begin = 0;
        int end;
        int last = name.length();

        while (true) {
            end = name.indexOf(replace, begin);
            if (end < 0) {
                end = last;
            }
            buf.append(name.substring(begin, end));
            if (end == last) {
                break;
            }
            buf.append(with);
            begin = end + 1;
        }

        return buf.toString();
    }

    public static class ValidAttribute {
        String name;
        boolean mandatory;
        boolean rtexprvalue;    // not used now

        public ValidAttribute(String name, boolean mandatory,
                              boolean rtexprvalue) {
            this.name = name;
            this.mandatory = mandatory;
            this.rtexprvalue = rtexprvalue;
        }

        public ValidAttribute(String name, boolean mandatory) {
            this(name, mandatory, false);
        }

        public ValidAttribute(String name) {
            this(name, false);
        }
    }

    /**
     * Convert a String value to 'boolean'.
     * Besides the standard conversions done by
     * Boolean.valueOf(s).booleanValue(), the value "yes"
     * (ignore case) is also converted to 'true'.
     * If 's' is null, then 'false' is returned.
     *
     * @param s the string to be converted
     * @return the boolean value associated with the string s
     */
    public static boolean booleanValue(String s) {
        return BooleanUtils.toBoolean(s);
    }

    /**
     * <p> The <tt>Class</tt> object is determined by passing the given string
     * name to the <tt>Class.forName()</tt> method, unless the given string
     * name represents a primitive type, in which case it is converted to a
     * <tt>Class</tt> object by appending ".class" to it (e.g., "int.class").
     *
     * @param type   type
     * @param loader class loader
     *
     * @return the <tt>Class</tt> object associated with the class or
     * interface with the given string name.
     *
     * @throws ClassNotFoundException if call was not found
     */
    public static Class toClass(String type, ClassLoader loader)
            throws ClassNotFoundException {

        Class c = null;
        int i0 = type.indexOf('[');
        int dims = 0;
        if (i0 > 0) {
            // This is an array.  Count the dimensions
            for (int i = 0; i < type.length(); i++) {
                if (type.charAt(i) == '[')
                    dims++;
            }
            type = type.substring(0, i0);
        }

        if ("boolean".equals(type))
            c = boolean.class;
        else if ("char".equals(type))
            c = char.class;
        else if ("byte".equals(type))
            c = byte.class;
        else if ("short".equals(type))
            c = short.class;
        else if ("int".equals(type))
            c = int.class;
        else if ("long".equals(type))
            c = long.class;
        else if ("float".equals(type))
            c = float.class;
        else if ("double".equals(type))
            c = double.class;
        else if (type.indexOf('[') < 0)
            c = loader.loadClass(type);

        if (dims == 0)
            return c;

        if (dims == 1)
            return java.lang.reflect.Array.newInstance(c, 1).getClass();

        // Array of more than i dimension
        return java.lang.reflect.Array.newInstance(c, new int[dims]).getClass();
    }

    /**
     * Produces a String representing a call to the EL interpreter.
     *
     * @param isTagFile    is a tag file
     * @param expression   a String containing zero or more "${}" expressions
     * @param expectedType the expected type of the interpreted result
     * @param fnmapvar     Variable pointing to a function map.
     * @param XmlEscape    True if the result should do XML escaping
     * @return a String representing a call to the EL interpreter.
     */
    public static String interpreterCall(boolean isTagFile,
                                         String expression,
                                         Class expectedType,
                                         String fnmapvar,
                                         boolean XmlEscape) {
        /*
         * Determine which context object to use.
         */
        String jspCtxt = null;
        if (isTagFile)
            jspCtxt = "this.getJspContext()";
        else
            jspCtxt = "_jspx_page_context";

    /*
         * Determine whether to use the expected type's textual name
     * or, if it's a primitive, the name of its correspondent boxed
     * type.
         */
        String targetType = expectedType.getName();
        String primitiveConverterMethod = null;
        if (expectedType.isPrimitive()) {
            if (expectedType.equals(Boolean.TYPE)) {
                targetType = Boolean.class.getName();
                primitiveConverterMethod = "booleanValue";
            } else if (expectedType.equals(Byte.TYPE)) {
                targetType = Byte.class.getName();
                primitiveConverterMethod = "byteValue";
            } else if (expectedType.equals(Character.TYPE)) {
                targetType = Character.class.getName();
                primitiveConverterMethod = "charValue";
            } else if (expectedType.equals(Short.TYPE)) {
                targetType = Short.class.getName();
                primitiveConverterMethod = "shortValue";
            } else if (expectedType.equals(Integer.TYPE)) {
                targetType = Integer.class.getName();
                primitiveConverterMethod = "intValue";
            } else if (expectedType.equals(Long.TYPE)) {
                targetType = Long.class.getName();
                primitiveConverterMethod = "longValue";
            } else if (expectedType.equals(Float.TYPE)) {
                targetType = Float.class.getName();
                primitiveConverterMethod = "floatValue";
            } else if (expectedType.equals(Double.TYPE)) {
                targetType = Double.class.getName();
                primitiveConverterMethod = "doubleValue";
            }
        }

        if (primitiveConverterMethod != null) {
            XmlEscape = false;
        }

    /*
         * Build up the base call to the interpreter.
         */
        // XXX - We use a proprietary call to the interpreter for now
        // as the current standard machinery is inefficient and requires
        // lots of wrappers and adapters.  This should all clear up once
        // the EL interpreter moves out of JSTL and into its own project.
        // In the future, this should be replaced by code that calls
        // ExpressionEvaluator.parseExpression() and then cache the resulting
        // expression objects.  The interpreterCall would simply select
        // one of the pre-cached expressions and evaluate it.
        // Note that PageContextImpl implements VariableResolver and
        // the generated Servlet/SimpleTag implements FunctionMapper, so
        // that machinery is already in place (mroth).
        targetType = toJavaSourceType(targetType);
        StringBuilder call = new StringBuilder(
                "(" + targetType + ") "
                        + "org.apache.struts2.jasper.runtime.PageContextImpl.proprietaryEvaluate"
                        + "(" + Generator.quote(expression) + ", "
                        + targetType + ".class, "
                        + "(PageContext)" + jspCtxt
                        + ", " + fnmapvar
                        + ", " + XmlEscape
                        + ")");
 
    /*
         * Add the primitive converter method if we need to.
         */
        if (primitiveConverterMethod != null) {
            call.insert(0, "(");
            call.append(")." + primitiveConverterMethod + "()");
        }

        return call.toString();
    }

    /**
     * Validates the syntax of all ${} expressions within the given string.
     *
     * @param where       the approximate location of the expressions in the JSP page
     * @param expressions a string containing zero or more "${}" expressions
     * @param expectedType expected class type
     * @param functionMapper function mapper
     * @param err         an error dispatcher to use
     * @throws JasperException in case of Jasper errors
     * @deprecated now delegated to the org.apache.el Package
     */
    public static void validateExpressions(Mark where,
                                           String expressions,
                                           Class expectedType,
                                           FunctionMapper functionMapper,
                                           ErrorDispatcher err)
            throws JasperException {

    }

    /**
     * Resets the temporary variable name.
     * (not thread-safe)
     *
     * @deprecated
     */
    public static void resetTemporaryVariableName() {
        tempSequenceNumber = 0;
    }

    /**
     * @return Generates a new temporary variable name.
     * (not thread-safe)
     * @deprecated
     */
    public static String nextTemporaryVariableName() {
        return Constants.TEMP_VARIABLE_NAME_PREFIX + (tempSequenceNumber++);
    }

    public static String coerceToPrimitiveBoolean(String s,
                                                  boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.struts2.jasper.runtime.JspRuntimeLibrary.coerceToBoolean(" + s + ")";
        } else {
            if (s == null || s.length() == 0)
                return "false";
            else
                return Boolean.valueOf(s).toString();
        }
    }

    public static String coerceToBoolean(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Boolean) org.apache.struts2.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Boolean.class)";
        } else {
            if (s == null || s.length() == 0) {
                return "new Boolean(false)";
            } else {
                // Detect format error at translation time
                return "new Boolean(" + Boolean.valueOf(s).toString() + ")";
            }
        }
    }

    public static String coerceToPrimitiveByte(String s,
                                               boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.struts2.jasper.runtime.JspRuntimeLibrary.coerceToByte(" + s + ")";
        } else {
            if (s == null || s.length() == 0)
                return "(byte) 0";
            else
                return "((byte)" + Byte.valueOf(s).toString() + ")";
        }
    }

    public static String coerceToByte(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Byte) org.apache.struts2.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Byte.class)";
        } else {
            if (s == null || s.length() == 0) {
                return "new Byte((byte) 0)";
            } else {
                // Detect format error at translation time
                return "new Byte((byte)" + Byte.valueOf(s).toString() + ")";
            }
        }
    }

    public static String coerceToChar(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.struts2.jasper.runtime.JspRuntimeLibrary.coerceToChar(" + s + ")";
        } else {
            if (s == null || s.length() == 0) {
                return "(char) 0";
            } else {
                char ch = s.charAt(0);
                // this trick avoids escaping issues
                return "((char) " + (int) ch + ")";
            }
        }
    }

    public static String coerceToCharacter(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Character) org.apache.struts2.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Character.class)";
        } else {
            if (s == null || s.length() == 0) {
                return "new Character((char) 0)";
            } else {
                char ch = s.charAt(0);
                // this trick avoids escaping issues
                return "new Character((char) " + (int) ch + ")";
            }
        }
    }

    public static String coerceToPrimitiveDouble(String s,
                                                 boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.struts2.jasper.runtime.JspRuntimeLibrary.coerceToDouble(" + s + ")";
        } else {
            if (s == null || s.length() == 0)
                return "(double) 0";
            else
                return Double.valueOf(s).toString();
        }
    }

    public static String coerceToDouble(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Double) org.apache.struts2.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Double.class)";
        } else {
            if (s == null || s.length() == 0) {
                return "new Double(0)";
            } else {
                // Detect format error at translation time
                return "new Double(" + Double.valueOf(s).toString() + ")";
            }
        }
    }

    public static String coerceToPrimitiveFloat(String s,
                                                boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.struts2.jasper.runtime.JspRuntimeLibrary.coerceToFloat(" + s + ")";
        } else {
            if (s == null || s.length() == 0)
                return "(float) 0";
            else
                return Float.valueOf(s).toString() + "f";
        }
    }

    public static String coerceToFloat(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Float) org.apache.struts2.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Float.class)";
        } else {
            if (s == null || s.length() == 0) {
                return "new Float(0)";
            } else {
                // Detect format error at translation time
                return "new Float(" + Float.valueOf(s).toString() + "f)";
            }
        }
    }

    public static String coerceToInt(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.struts2.jasper.runtime.JspRuntimeLibrary.coerceToInt(" + s + ")";
        } else {
            if (s == null || s.length() == 0)
                return "0";
            else
                return Integer.valueOf(s).toString();
        }
    }

    public static String coerceToInteger(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Integer) org.apache.struts2.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Integer.class)";
        } else {
            if (s == null || s.length() == 0) {
                return "new Integer(0)";
            } else {
                // Detect format error at translation time
                return "new Integer(" + Integer.valueOf(s).toString() + ")";
            }
        }
    }

    public static String coerceToPrimitiveShort(String s,
                                                boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.struts2.jasper.runtime.JspRuntimeLibrary.coerceToShort(" + s + ")";
        } else {
            if (s == null || s.length() == 0)
                return "(short) 0";
            else
                return "((short) " + Short.valueOf(s).toString() + ")";
        }
    }

    public static String coerceToShort(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Short) org.apache.struts2.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Short.class)";
        } else {
            if (s == null || s.length() == 0) {
                return "new Short((short) 0)";
            } else {
                // Detect format error at translation time
                return "new Short(\"" + Short.valueOf(s).toString() + "\")";
            }
        }
    }

    public static String coerceToPrimitiveLong(String s,
                                               boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.struts2.jasper.runtime.JspRuntimeLibrary.coerceToLong(" + s + ")";
        } else {
            if (s == null || s.length() == 0)
                return "(long) 0";
            else
                return Long.valueOf(s).toString() + "l";
        }
    }

    public static String coerceToLong(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Long) org.apache.struts2.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Long.class)";
        } else {
            if (s == null || s.length() == 0) {
                return "new Long(0)";
            } else {
                // Detect format error at translation time
                return "new Long(" + Long.valueOf(s).toString() + "l)";
            }
        }
    }

    public static InputStream getInputStream(String fname, JarFile jarFile,
                                             JspCompilationContext ctxt,
                                             ErrorDispatcher err)
            throws JasperException, IOException {

        InputStream in = null;


        if (jarFile != null) {
            String jarEntryName = fname.substring(1, fname.length());
            ZipEntry jarEntry = jarFile.getEntry(jarEntryName);
            if (jarEntry == null) {
                err.jspError("jsp.error.file.not.found", fname);
            }
            in = jarFile.getInputStream(jarEntry);
        } else {
            in = ctxt.getResourceAsStream(fname);
        }

        if (in == null) {
            err.jspError("jsp.error.file.not.found", fname);
        }

        return in;
    }

    /**
     * Gets the fully-qualified class name of the tag handler corresponding to
     * the given tag file path.
     *
     * @param path Tag file path
     * @param err  Error dispatcher
     * @return Fully-qualified class name of the tag handler corresponding to
     * the given tag file path
     * @throws JasperException in case of Jasper errors
     * @deprecated Use {@link #getTagHandlerClassName(String, String, ErrorDispatcher)}
     * See https://issues.apache.org/bugzilla/show_bug.cgi?id=46471
     */
    public static String getTagHandlerClassName(String path,
                                                ErrorDispatcher err)
            throws JasperException {
        return getTagHandlerClassName(path, null, err);
    }

    /**
     * Gets the fully-qualified class name of the tag handler corresponding to
     * the given tag file path.
     *
     * @param path Tag file path
     * @param urn urn
     * @param err  Error dispatcher
     * @return Fully-qualified class name of the tag handler corresponding to
     * the given tag file path
     * @throws JasperException in case of Jasper errors
     */
    public static String getTagHandlerClassName(String path, String urn,
                                                ErrorDispatcher err) throws JasperException {

        String className = null;
        int begin = 0;
        int index;

        index = path.lastIndexOf(".tag");
        if (index == -1) {
            err.jspError("jsp.error.tagfile.badSuffix", path);
        }

        //It's tempting to remove the ".tag" suffix here, but we can't.
        //If we remove it, the fully-qualified class name of this tag
        //could conflict with the package name of other tags.
        //For instance, the tag file
        //    /WEB-INF/tags/foo.tag
        //would have fully-qualified class name
        //    org.apache.jsp.tag.web.foo
        //which would conflict with the package name of the tag file
        //    /WEB-INF/tags/foo/bar.tag

        index = path.indexOf(WEB_INF_TAGS);
        if (index != -1) {
            className = "org.apache.jsp.tag.web.";
            begin = index + WEB_INF_TAGS.length();
        } else {
            index = path.indexOf(META_INF_TAGS);
            if (index != -1) {
                className = getClassNameBase(urn);
                begin = index + META_INF_TAGS.length();
            } else {
                err.jspError("jsp.error.tagfile.illegalPath", path);
            }
        }

        className += makeJavaPackage(path.substring(begin));

        return className;
    }

    private static String getClassNameBase(String urn) {
        StringBuffer base = new StringBuffer("org.apache.jsp.tag.meta.");
        if (urn != null) {
            base.append(makeJavaPackage(urn));
            base.append('.');
        }
        return base.toString();
    }

    /**
     * Converts the given path to a Java package or fully-qualified class name
     *
     * @param path Path to convert
     * @return Java package corresponding to the given path
     */
    public static final String makeJavaPackage(String path) {
        String classNameComponents[] = split(path, "/");
        StringBuffer legalClassNames = new StringBuffer();
        for (int i = 0; i < classNameComponents.length; i++) {
            legalClassNames.append(makeJavaIdentifier(classNameComponents[i]));
            if (i < classNameComponents.length - 1) {
                legalClassNames.append('.');
            }
        }
        return legalClassNames.toString();
    }

    /**
     * Splits a string into it's components.
     *
     * @param path String to split
     * @param pat  Pattern to split at
     * @return the components of the path
     */
    private static final String[] split(String path, String pat) {
        Vector comps = new Vector();
        int pos = path.indexOf(pat);
        int start = 0;
        while (pos >= 0) {
            if (pos > start) {
                String comp = path.substring(start, pos);
                comps.add(comp);
            }
            start = pos + pat.length();
            pos = path.indexOf(pat, start);
        }
        if (start < path.length()) {
            comps.add(path.substring(start));
        }
        String[] result = new String[comps.size()];
        for (int i = 0; i < comps.size(); i++) {
            result[i] = (String) comps.elementAt(i);
        }
        return result;
    }

    /**
     * Converts the given identifier to a legal Java identifier
     *
     * @param identifier Identifier to convert
     * @return Legal Java identifier corresponding to the given identifier
     */
    public static final String makeJavaIdentifier(String identifier) {
        StringBuffer modifiedIdentifier =
                new StringBuffer(identifier.length());
        if (!Character.isJavaIdentifierStart(identifier.charAt(0))) {
            modifiedIdentifier.append('_');
        }
        for (int i = 0; i < identifier.length(); i++) {
            char ch = identifier.charAt(i);
            if (Character.isJavaIdentifierPart(ch) && ch != '_') {
                modifiedIdentifier.append(ch);
            } else if (ch == '.') {
                modifiedIdentifier.append('_');
            } else {
                modifiedIdentifier.append(mangleChar(ch));
            }
        }
        if (isJavaKeyword(modifiedIdentifier.toString())) {
            modifiedIdentifier.append('_');
        }
        return modifiedIdentifier.toString();
    }

    /**
     * Mangle the specified character to create a legal Java class name.
     * @param ch character
     * @return new string
     */
    public static final String mangleChar(char ch) {
        char[] result = new char[5];
        result[0] = '_';
        result[1] = Character.forDigit((ch >> 12) & 0xf, 16);
        result[2] = Character.forDigit((ch >> 8) & 0xf, 16);
        result[3] = Character.forDigit((ch >> 4) & 0xf, 16);
        result[4] = Character.forDigit(ch & 0xf, 16);
        return new String(result);
    }

    /**
     * @param key string to check
     *
     * @return Test whether the argument is a Java keyword
     */
    public static boolean isJavaKeyword(String key) {
        int i = 0;
        int j = javaKeywords.length;
        while (i < j) {
            int k = (i + j) / 2;
            int result = javaKeywords[k].compareTo(key);
            if (result == 0) {
                return true;
            }
            if (result < 0) {
                i = k + 1;
            } else {
                j = k;
            }
        }
        return false;
    }

    /**
     * Converts the given Xml name to a legal Java identifier.  This is
     * slightly more efficient than makeJavaIdentifier in that we only need
     * to worry about '.', '-', and ':' in the string.  We also assume that
     * the resultant string is further concatenated with some prefix string
     * so that we don't have to worry about it being a Java key word.
     *
     * @param name Identifier to convert
     * @return Legal Java identifier corresponding to the given identifier
     */
    public static final String makeXmlJavaIdentifier(String name) {
        if (name.indexOf('-') >= 0)
            name = replace(name, '-', "$1");
        if (name.indexOf('.') >= 0)
            name = replace(name, '.', "$2");
        if (name.indexOf(':') >= 0)
            name = replace(name, ':', "$3");
        return name;
    }

    static InputStreamReader getReader(String fname, String encoding,
                                       JarFile jarFile,
                                       JspCompilationContext ctxt,
                                       ErrorDispatcher err)
            throws JasperException, IOException {

        return getReader(fname, encoding, jarFile, ctxt, err, 0);
    }

    static InputStreamReader getReader(String fname, String encoding,
                                       JarFile jarFile,
                                       JspCompilationContext ctxt,
                                       ErrorDispatcher err, int skip)
            throws JasperException, IOException {

        InputStreamReader reader = null;
        InputStream in = getInputStream(fname, jarFile, ctxt, err);
        for (int i = 0; i < skip; i++) {
            in.read();
        }
        try {
            reader = new InputStreamReader(in, encoding);
        } catch (UnsupportedEncodingException ex) {
            err.jspError("jsp.error.unsupported.encoding", encoding);
        }

        return reader;
    }

    /**
     * Handles taking input from TLDs
     * 'java.lang.Object' -&gt; 'java.lang.Object.class'
     * 'int' -&gt; 'int.class'
     * 'void' -&gt; 'Void.TYPE'
     * 'int[]' -&gt; 'int[].class'
     *
     * @param type java source type
     * @return type
     */
    public static String toJavaSourceTypeFromTld(String type) {
        if (type == null || "void".equals(type)) {
            return "Void.TYPE";
        }
        return type + ".class";
    }

    /**
     * Class.getName() return arrays in the form "[[[&lt;et&gt;", where et,
     * the element type can be one of ZBCDFIJS or L&lt;classname&gt;;
     * It is converted into forms that can be understood by javac.
     *
     * @param type source type
     * @return java source type
     */
    public static String toJavaSourceType(String type) {

        if (type.charAt(0) != '[') {
            return type;
        }

        int dims = 1;
        String t = null;
        for (int i = 1; i < type.length(); i++) {
            if (type.charAt(i) == '[') {
                dims++;
            } else {
                switch (type.charAt(i)) {
                    case 'Z':
                        t = "boolean";
                        break;
                    case 'B':
                        t = "byte";
                        break;
                    case 'C':
                        t = "char";
                        break;
                    case 'D':
                        t = "double";
                        break;
                    case 'F':
                        t = "float";
                        break;
                    case 'I':
                        t = "int";
                        break;
                    case 'J':
                        t = "long";
                        break;
                    case 'S':
                        t = "short";
                        break;
                    case 'L':
                        t = type.substring(i + 1, type.indexOf(';'));
                        break;
                }
                break;
            }
        }
        StringBuilder resultType = new StringBuilder(t);
        for (; dims > 0; dims--) {
            resultType.append("[]");
        }
        return resultType.toString();
    }

    /**
     * Compute the canonical name from a Class instance.  Note that a
     * simple replacement of '$' with '.' of a binary name would not work,
     * as '$' is a legal Java Identifier character.
     *
     * @param c A instance of java.lang.Class
     * @return The canonical name of c.
     */
    public static String getCanonicalName(Class c) {

        String binaryName = c.getName();
        c = c.getDeclaringClass();

        if (c == null) {
            return binaryName;
        }

        StringBuilder buf = new StringBuilder(binaryName);
        do {
            buf.setCharAt(c.getName().length(), '.');
            c = c.getDeclaringClass();
        } while (c != null);

        return buf.toString();
    }
}
