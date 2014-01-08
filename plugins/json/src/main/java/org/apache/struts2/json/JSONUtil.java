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

import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.WildcardUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.SMDMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

/**
 * Wrapper for JSONWriter with some utility methods.
 */
public class JSONUtil {

    public final static String RFC3339_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private static final Logger LOG = LoggerFactory.getLogger(JSONUtil.class);

    /**
     * Serializes an object into JSON.
     *
     * @param object
     *            to be serialized
     * @return JSON string
     * @throws JSONException
     */
    public static String serialize(Object object) throws JSONException {
        JSONWriter writer = new JSONWriter();
        return writer.write(object);
    }

    /**
     * Serializes an object into JSON, excluding any properties matching any of
     * the regular expressions in the given collection.
     *
     * @param object
     *            to be serialized
     * @param excludeProperties
     *            Patterns matching properties to exclude
     * @param ignoreHierarchy
     *            whether to ignore properties defined on base classes of the
     *            root object
     * @return JSON string
     * @throws JSONException
     */
    public static String serialize(Object object, Collection<Pattern> excludeProperties,
            Collection<Pattern> includeProperties, boolean ignoreHierarchy, boolean excludeNullProperties)
            throws JSONException {
        JSONWriter writer = new JSONWriter();
        writer.setIgnoreHierarchy(ignoreHierarchy);
        return writer.write(object, excludeProperties, includeProperties, excludeNullProperties);
    }

    /**
     * Serializes an object into JSON, excluding any properties matching any of
     * the regular expressions in the given collection.
     *
     * @param object
     *            to be serialized
     * @param excludeProperties
     *            Patterns matching properties to exclude
     * @param ignoreHierarchy
     *            whether to ignore properties defined on base classes of the
     *            root object
     * @param enumAsBean
     *            whether to serialized enums a Bean or name=value pair
     * @param defaultDateFormat
     *            date format used to serialize dates
     * @return JSON string
     * @throws JSONException
     */
    public static String serialize(Object object, Collection<Pattern> excludeProperties,
                                   Collection<Pattern> includeProperties, boolean ignoreHierarchy, boolean enumAsBean,
                                   boolean excludeNullProperties, String defaultDateFormat) throws JSONException {
        JSONWriter writer = new JSONWriter();
        writer.setIgnoreHierarchy(ignoreHierarchy);
        writer.setEnumAsBean(enumAsBean);
        writer.setDateFormatter(defaultDateFormat);
        return writer.write(object, excludeProperties, includeProperties, excludeNullProperties);
    }

    /**
     * Serializes an object into JSON to the given writer.
     *
     * @param writer
     *            Writer to serialize the object to
     * @param object
     *            object to be serialized
     * @throws IOException
     * @throws JSONException
     */
    public static void serialize(Writer writer, Object object) throws IOException, JSONException {
        writer.write(serialize(object));
    }

    /**
     * Serializes an object into JSON to the given writer, excluding any
     * properties matching any of the regular expressions in the given
     * collection.
     *
     * @param writer
     *            Writer to serialize the object to
     * @param object
     *            object to be serialized
     * @param excludeProperties
     *            Patterns matching properties to ignore
     * @throws IOException
     * @throws JSONException
     */
    public static void serialize(Writer writer, Object object, Collection<Pattern> excludeProperties,
            Collection<Pattern> includeProperties, boolean excludeNullProperties) throws IOException,
            JSONException {
        writer.write(serialize(object, excludeProperties, includeProperties, true, excludeNullProperties));
    }

    /**
     * Deserializes a object from JSON
     *
     * @param json
     *            string in JSON
     * @return desrialized object
     * @throws JSONException
     */
    public static Object deserialize(String json) throws JSONException {
        JSONReader reader = new JSONReader();
        return reader.read(json);
    }

    /**
     * Deserializes a object from JSON
     *
     * @param reader
     *            Reader to read a JSON string from
     * @return deserialized object
     * @throws JSONException
     *             when IOException happens
     */
    public static Object deserialize(Reader reader) throws JSONException {
        // read content
        BufferedReader bufferReader = new BufferedReader(reader);
        String line;
        StringBuilder buffer = new StringBuilder();

        try {
            while ((line = bufferReader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException e) {
            throw new JSONException(e);
        }

        return deserialize(buffer.toString());
    }

    public static void writeJSONToResponse(SerializationParams serializationParams) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(serializationParams.getSerializedJSON()))
            stringBuilder.append(serializationParams.getSerializedJSON());

        if (StringUtils.isNotBlank(serializationParams.getWrapPrefix()))
            stringBuilder.insert(0, serializationParams.getWrapPrefix());
        else if (serializationParams.isWrapWithComments()) {
            stringBuilder.insert(0, "/* ");
            stringBuilder.append(" */");
        } else if (serializationParams.isPrefix())
            stringBuilder.insert(0, "{}&& ");

        if (StringUtils.isNotBlank(serializationParams.getWrapSuffix()))
            stringBuilder.append(serializationParams.getWrapSuffix());

        String json = stringBuilder.toString();

        if (LOG.isDebugEnabled()) {
            LOG.debug("[JSON]" + json);
        }

        HttpServletResponse response = serializationParams.getResponse();

        // status or error code
        if (serializationParams.getStatusCode() > 0)
            response.setStatus(serializationParams.getStatusCode());
        else if (serializationParams.getErrorCode() > 0)
            response.sendError(serializationParams.getErrorCode());

        // content type
        response.setContentType(serializationParams.getContentType() + ";charset="
                + serializationParams.getEncoding());

        if (serializationParams.isNoCache()) {
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Expires", "0");
            response.setHeader("Pragma", "No-cache");
        }

        if (serializationParams.isGzip()) {
            response.addHeader("Content-Encoding", "gzip");
            GZIPOutputStream out = null;
            InputStream in = null;
            try {
                out = new GZIPOutputStream(response.getOutputStream());
                in = new ByteArrayInputStream(json.getBytes(serializationParams.getEncoding()));
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                if (in != null)
                    in.close();
                if (out != null) {
                    out.finish();
                    out.close();
                }
            }

        } else {
            response.setContentLength(json.getBytes(serializationParams.getEncoding()).length);
            PrintWriter out = response.getWriter();
            out.print(json);
        }
    }

    public static Set<String> asSet(String commaDelim) {
        if ((commaDelim == null) || (commaDelim.trim().length() == 0))
            return null;
        return TextParseUtil.commaDelimitedStringToSet(commaDelim);
    }

    /**
     * List visible methods carrying the
     *
     * @SMDMethod annotation
     *
     * @param ignoreInterfaces
     *            if true, only the methods of the class are examined. If false,
     *            annotations on every interfaces' methods are examined.
     */
    @SuppressWarnings("unchecked")
    public static Method[] listSMDMethods(Class clazz, boolean ignoreInterfaces) {
        final List<Method> methods = new LinkedList<Method>();
        if (ignoreInterfaces) {
            for (Method method : clazz.getMethods()) {
                SMDMethod smdMethodAnnotation = method.getAnnotation(SMDMethod.class);
                if (smdMethodAnnotation != null) {
                    methods.add(method);
                }
            }
        } else {
            // recurse the entire superclass/interface hierarchy and add in
            // order encountered
            JSONUtil.visitInterfaces(clazz, new JSONUtil.ClassVisitor() {
                public boolean visit(Class aClass) {
                    for (Method method : aClass.getMethods()) {
                        SMDMethod smdMethodAnnotation = method.getAnnotation(SMDMethod.class);
                        if ((smdMethodAnnotation != null) && !methods.contains(method)) {
                            methods.add(method);
                        }
                    }
                    return true;
                }
            });
        }

        Method[] methodResult = new Method[methods.size()];
        return methods.toArray(methodResult);
    }

    /**
     * Realizes the visit(Class) method called by vistInterfaces for all
     * encountered classes/interfaces
     */
    public static interface ClassVisitor {

        /**
         * Called when a new interface/class is encountered
         *
         * @param aClass
         *            the encountered class/interface
         * @return true if the recursion should continue, false to stop
         *         recursion immediately
         */
        @SuppressWarnings("unchecked")
        boolean visit(Class aClass);
    }

    /**
     * Visit all the interfaces realized by the specified object, its
     * superclasses and its interfaces <p/> Visitation is performed in the
     * following order: aClass aClass' interfaces the interface's superclasses
     * (interfaces) aClass' superclass superclass' interfaces superclass'
     * interface's superclasses (interfaces) super-superclass and so on <p/> The
     * Object base class is base excluded. Classes/interfaces are only visited
     * once each
     *
     * @param aClass
     *            the class to start recursing upwards from
     * @param visitor
     *            this vistor is called for each class/interface encountered
     * @return true if all classes/interfaces were visited, false if it was
     *         exited early as specified by a ClassVisitor result
     */
    @SuppressWarnings("unchecked")
    public static boolean visitInterfaces(Class aClass, ClassVisitor visitor) {
        List<Class> classesVisited = new LinkedList<Class>();
        return visitUniqueInterfaces(aClass, visitor, classesVisited);
    }

    /**
     * Recursive method to visit all the interfaces of a class (and its
     * superclasses and super-interfaces) if they haven't already been visited.
     * <p/> Always visits itself if it hasn't already been visited
     *
     * @param thisClass
     *            the current class to visit (if not already done so)
     * @param classesVisited
     *            classes already visited
     * @param visitor
     *            this vistor is called for each class/interface encountered
     * @return true if recursion can continue, false if it should be aborted
     */
    private static boolean visitUniqueInterfaces(Class thisClass, ClassVisitor visitor,
            List<Class> classesVisited) {
        boolean okayToContinue = true;

        if (!classesVisited.contains(thisClass)) {
            classesVisited.add(thisClass);
            okayToContinue = visitor.visit(thisClass);

            if (okayToContinue) {
                Class[] interfaces = thisClass.getInterfaces();
                int index = 0;
                while ((index < interfaces.length) && (okayToContinue)) {
                    okayToContinue = visitUniqueInterfaces(interfaces[index++], visitor, classesVisited);
                }

                if (okayToContinue) {
                    Class superClass = thisClass.getSuperclass();
                    if ((superClass != null) && (!Object.class.equals(superClass))) {
                        okayToContinue = visitUniqueInterfaces(superClass, visitor, classesVisited);
                    }
                }
            }
        }
        return okayToContinue;
    }

    public static boolean isGzipInRequest(HttpServletRequest request) {
        String header = request.getHeader("Accept-Encoding");
        return (header != null) && (header.indexOf("gzip") >= 0);
    }

    public static final String REGEXP_PATTERN = "regexp";
    public static final String WILDCARD_PATTERN = "wildcard";
    /* package */ static final String SPLIT_PATTERN = "split";
    /* package */ static final String JOIN_STRING = "join";
    /* package */ static final String ARRAY_BEGIN_STRING = "array-begin";
    /* package */ static final String ARRAY_END_STRING = "array-end";

    /* package */ static Map<String, Map<String, String>> getIncludePatternData()
    {
        Map<String, Map<String, String>> includePatternData = new HashMap<String, Map<String, String>>();

        Map<String, String> data = new HashMap<String, String>();
        data.put(REGEXP_PATTERN, "\\\\\\.");
        data.put(WILDCARD_PATTERN, "\\.");
        includePatternData.put(SPLIT_PATTERN, data);

        data = new HashMap<String, String>();
        data.put(REGEXP_PATTERN, "\\.");
        data.put(WILDCARD_PATTERN, ".");
        includePatternData.put(JOIN_STRING, data);

        data = new HashMap<String, String>();
        data.put(REGEXP_PATTERN, "\\[");
        data.put(WILDCARD_PATTERN, "[");
        includePatternData.put(ARRAY_BEGIN_STRING, data);

        data = new HashMap<String, String>();
        data.put(REGEXP_PATTERN, "\\]");
        data.put(WILDCARD_PATTERN, "]");
        includePatternData.put(ARRAY_END_STRING, data);

        return includePatternData;
    }

    private static final Map<String, Map<String, String>> defaultIncludePatternData = getIncludePatternData();

    public static List<Pattern> processIncludePatterns(Set<String> includePatterns, String type) {
        return processIncludePatterns(includePatterns, type, defaultIncludePatternData);
    }

    /* package */ static List<Pattern> processIncludePatterns(Set<String> includePatterns, String type, Map<String, Map<String, String>> includePatternData) {
        if (includePatterns != null) {
            List<Pattern> results = new ArrayList<Pattern>(includePatterns.size());
            Map<String, String> existingPatterns = new HashMap<String, String>();
            for (String pattern : includePatterns) {
                processPattern(results, existingPatterns, pattern, type, includePatternData);
            }
            return results;
        } else {
            return null;
        }
    }

    private static void processPattern(List<Pattern> results, Map<String, String> existingPatterns, String pattern, String type, Map<String, Map<String, String>> includePatternData) {
        // Compile a pattern for each *unique* "level" of the object
        // hierarchy specified in the regex.
        String[] patternPieces = pattern.split(includePatternData.get(SPLIT_PATTERN).get(type));

        String patternExpr = "";
        for (String patternPiece : patternPieces) {
            patternExpr = processPatternPiece(results, existingPatterns, patternExpr, patternPiece, type, includePatternData);
        }
    }

    private static String processPatternPiece(List<Pattern> results, Map<String, String> existingPatterns, String patternExpr, String patternPiece, String type, Map<String, Map<String, String>> includePatternData) {
        if (patternExpr.length() > 0) {
            patternExpr += includePatternData.get(JOIN_STRING).get(type);
        }
        patternExpr += patternPiece;

        // Check for duplicate patterns so that there is no overlap.
        if (!existingPatterns.containsKey(patternExpr)) {
            existingPatterns.put(patternExpr, patternExpr);
            if (isIndexedProperty(patternPiece, type, includePatternData)) {
                addPattern(results, patternExpr.substring(0, patternExpr.lastIndexOf(includePatternData.get(ARRAY_BEGIN_STRING).get(type))), type);
            }
            addPattern(results, patternExpr, type);
        }
        return patternExpr;
    }

    /**
     * Add a pattern that does not have the indexed property matching (ie. list\[\d+\] becomes list).
     */
    private static boolean isIndexedProperty(String patternPiece, String type, Map<String, Map<String, String>> includePatternData) {
        return patternPiece.endsWith(includePatternData.get(ARRAY_END_STRING).get(type));
    }

    private static void addPattern(List<Pattern> results, String pattern, String type) {
        results.add(REGEXP_PATTERN.equals(type) ? Pattern.compile(pattern) : WildcardUtil.compileWildcardPattern(pattern));
        if (LOG.isTraceEnabled()) {
            LOG.trace("Adding include " + (REGEXP_PATTERN.equals(type) ? "property" : "wildcard") + " expression:  " + pattern);
        }
    }

}
