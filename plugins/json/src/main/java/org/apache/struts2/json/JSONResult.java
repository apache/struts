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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.json.smd.SMDGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <!-- START SNIPPET: description --> <p/> This result serializes an action
 * into JSON. <p/> <!-- END SNIPPET: description --> <p/> <p/> <u>Result
 * parameters:</u> <p/> <!-- START SNIPPET: parameters --> <p/>
 * <ul>
 * <p/>
 * <li>excludeProperties - list of regular expressions matching the properties
 * to be excluded. The regular expressions are evaluated against the OGNL
 * expression representation of the properties. </li>
 * <p/>
 * </ul>
 * <p/> <!-- END SNIPPET: parameters --> <p/> <b>Example:</b> <p/>
 * <p/>
 * <pre>
 * &lt;!-- START SNIPPET: example --&gt;
 * &lt;result name=&quot;success&quot; type=&quot;json&quot; /&gt;
 * &lt;!-- END SNIPPET: example --&gt;
 * </pre>
 */
public class JSONResult implements Result {

    private static final long serialVersionUID = 8624350183189931165L;

    private static final Logger LOG = LoggerFactory.getLogger(JSONResult.class);

    private String defaultEncoding = "ISO-8859-1";
    private List<Pattern> includeProperties;
    private List<Pattern> excludeProperties;
    private String root;
    private boolean wrapWithComments;
    private boolean prefix;
    private boolean enableSMD = false;
    private boolean enableGZIP = false;
    private boolean ignoreHierarchy = true;
    private boolean ignoreInterfaces = true;
    private boolean enumAsBean = JSONWriter.ENUM_AS_BEAN_DEFAULT;
    private boolean noCache = false;
    private boolean excludeNullProperties = false;
    private int statusCode;
    private int errorCode;
    private String callbackParameter;
    private String contentType;
    private String wrapPrefix;
    private String wrapSuffix;

    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public void setDefaultEncoding(String val) {
        this.defaultEncoding = val;
    }

    /**
     * Gets a list of regular expressions of properties to exclude from the JSON
     * output.
     *
     * @return A list of compiled regular expression patterns
     */
    public List<Pattern> getExcludePropertiesList() {
        return this.excludeProperties;
    }

    /**
     * Sets a comma-delimited list of regular expressions to match properties
     * that should be excluded from the JSON output.
     *
     * @param commaDelim A comma-delimited list of regular expressions
     */
    public void setExcludeProperties(String commaDelim) {
        List<String> excludePatterns = JSONUtil.asList(commaDelim);
        if (excludePatterns != null) {
            this.excludeProperties = new ArrayList<Pattern>(excludePatterns.size());
            for (String pattern : excludePatterns) {
                this.excludeProperties.add(Pattern.compile(pattern));
            }
        }
    }

    /**
     * @return the includeProperties
     */
    public List<Pattern> getIncludePropertiesList() {
        return includeProperties;
    }

    /**
     * @param commaDelim comma delimited include string patterns
     */
    public void setIncludeProperties(String commaDelim) {
        List<String> includePatterns = JSONUtil.asList(commaDelim);
        if (includePatterns != null) {
            processIncludePatterns(includePatterns);
        }
    }

    private void processIncludePatterns(List<String> includePatterns) {
        includeProperties = new ArrayList<Pattern>(includePatterns.size());
        Map<String, String> existingPatterns = new HashMap<String, String>();
        for (String pattern : includePatterns) {
            processPattern(existingPatterns, pattern);
        }
    }

    private void processPattern(Map<String, String> existingPatterns, String pattern) {
        // Compile a pattern for each *unique* "level" of the object
        // hierarchy specified in the regex.
        String[] patternPieces = pattern.split("\\\\\\.");

        String patternExpr = "";
        for (String patternPiece : patternPieces) {
            patternExpr = processPatternPiece(existingPatterns, patternExpr, patternPiece);
        }
    }

    private String processPatternPiece(Map<String, String> existingPatterns, String patternExpr, String patternPiece) {
        if (patternExpr.length() > 0) {
            patternExpr += "\\.";
        }
        patternExpr += patternPiece;

        // Check for duplicate patterns so that there is no overlap.
        if (!existingPatterns.containsKey(patternExpr)) {
            existingPatterns.put(patternExpr, patternExpr);
            if (isIndexedProperty(patternPiece)) {
                addPattern(patternExpr.substring(0, patternExpr.lastIndexOf("\\[")));
            }
            addPattern(patternExpr);
        }
        return patternExpr;
    }

    /**
     * Add a pattern that does not have the indexed property matching (ie. list\[\d+\] becomes list).
     */
    private boolean isIndexedProperty(String patternPiece) {
        return patternPiece.endsWith("\\]");
    }

    private void addPattern(String pattern) {
        this.includeProperties.add(Pattern.compile(pattern));
        if (LOG.isDebugEnabled()) {
            LOG.debug("Adding include property expression:  " + pattern);
        }
    }

    public void execute(ActionInvocation invocation) throws Exception {
        ActionContext actionContext = invocation.getInvocationContext();
        HttpServletRequest request = (HttpServletRequest) actionContext.get(StrutsStatics.HTTP_REQUEST);
        HttpServletResponse response = (HttpServletResponse) actionContext.get(StrutsStatics.HTTP_RESPONSE);

        try {
            Object rootObject;
            rootObject = readRootObject(invocation);
            writeToResponse(response, createJSONString(request, rootObject), enableGzip(request));
        } catch (IOException exception) {
            LOG.error(exception.getMessage(), exception);
            throw exception;
        }
    }

    private Object readRootObject(ActionInvocation invocation) {
        Object root = findRootObject(invocation);
        if (enableSMD) {
            return new SMDGenerator(root, excludeProperties, ignoreInterfaces).generate(invocation);
        }
        return root;
    }

    private Object findRootObject(ActionInvocation invocation) {
        Object rootObject;
        if (this.root != null) {
            ValueStack stack = invocation.getStack();
            rootObject = stack.findValue(root);
        } else {
            rootObject = invocation.getAction();
        }
        return rootObject;
    }

    private String createJSONString(HttpServletRequest request, Object rootObject) throws JSONException {
        String json;
        json = JSONUtil.serialize(rootObject, excludeProperties, includeProperties, ignoreHierarchy,
                enumAsBean, excludeNullProperties);
        json = addCallbackIfApplicable(request, json);
        return json;
    }

    private boolean enableGzip(HttpServletRequest request) {
        return enableGZIP && JSONUtil.isGzipInRequest(request);
    }

    protected void writeToResponse(HttpServletResponse response, String json, boolean gzip) throws IOException {
        JSONUtil.writeJSONToResponse(new SerializationParams(response, getEncoding(), isWrapWithComments(),
                json, false, gzip, noCache, statusCode, errorCode, prefix, contentType, wrapPrefix,
                wrapSuffix));
    }

    @SuppressWarnings("unchecked")
    protected org.apache.struts2.json.smd.SMD buildSMDObject(ActionInvocation invocation) {
        return new SMDGenerator(readRootObject(invocation), excludeProperties, ignoreInterfaces).generate(invocation);
    }

    /**
     * Retrieve the encoding <p/>
     *
     * @return The encoding associated with this template (defaults to the value
     *         of 'struts.i18n.encoding' property)
     */
    protected String getEncoding() {
        String encoding = this.defaultEncoding;

        if (encoding == null) {
            encoding = System.getProperty("file.encoding");
        }

        if (encoding == null) {
            encoding = "UTF-8";
        }

        return encoding;
    }

    protected String addCallbackIfApplicable(HttpServletRequest request, String json) {
        if ((callbackParameter != null) && (callbackParameter.length() > 0)) {
            String callbackName = request.getParameter(callbackParameter);
            if ((callbackName != null) && (callbackName.length() > 0))
                json = callbackName + "(" + json + ")";
        }
        return json;
    }

    /**
     * @return OGNL expression of root object to be serialized
     */
    public String getRoot() {
        return this.root;
    }

    /**
     * Sets the root object to be serialized, defaults to the Action
     *
     * @param root OGNL expression of root object to be serialized
     */
    public void setRoot(String root) {
        this.root = root;
    }

    /**
     * @return Generated JSON must be enclosed in comments
     */
    public boolean isWrapWithComments() {
        return this.wrapWithComments;
    }

    /**
     * Wrap generated JSON with comments
     *
     * @param wrapWithComments
     */
    public void setWrapWithComments(boolean wrapWithComments) {
        this.wrapWithComments = wrapWithComments;
    }

    /**
     * @return Result has SMD generation enabled
     */
    public boolean isEnableSMD() {
        return this.enableSMD;
    }

    /**
     * Enable SMD generation for action, which can be used for JSON-RPC
     *
     * @param enableSMD
     */
    public void setEnableSMD(boolean enableSMD) {
        this.enableSMD = enableSMD;
    }

    public void setIgnoreHierarchy(boolean ignoreHierarchy) {
        this.ignoreHierarchy = ignoreHierarchy;
    }

    /**
     * Controls whether interfaces should be inspected for method annotations
     * You may need to set to this true if your action is a proxy as annotations
     * on methods are not inherited
     */
    public void setIgnoreInterfaces(boolean ignoreInterfaces) {
        this.ignoreInterfaces = ignoreInterfaces;
    }

    /**
     * Controls how Enum's are serialized : If true, an Enum is serialized as a
     * name=value pair (name=name()) (default) If false, an Enum is serialized
     * as a bean with a special property _name=name()
     *
     * @param enumAsBean
     */
    public void setEnumAsBean(boolean enumAsBean) {
        this.enumAsBean = enumAsBean;
    }

    public boolean isEnumAsBean() {
        return enumAsBean;
    }

    public boolean isEnableGZIP() {
        return enableGZIP;
    }

    public void setEnableGZIP(boolean enableGZIP) {
        this.enableGZIP = enableGZIP;
    }

    public boolean isNoCache() {
        return noCache;
    }

    /**
     * Add headers to response to prevent the browser from caching the response
     *
     * @param noCache
     */
    public void setNoCache(boolean noCache) {
        this.noCache = noCache;
    }

    public boolean isIgnoreHierarchy() {
        return ignoreHierarchy;
    }

    public boolean isExcludeNullProperties() {
        return excludeNullProperties;
    }

    /**
     * Do not serialize properties with a null value
     *
     * @param excludeNullProperties
     */
    public void setExcludeNullProperties(boolean excludeNullProperties) {
        this.excludeNullProperties = excludeNullProperties;
    }

    /**
     * Status code to be set in the response
     *
     * @param statusCode
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Error code to be set in the response
     *
     * @param errorCode
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setCallbackParameter(String callbackParameter) {
        this.callbackParameter = callbackParameter;
    }

    public String getCallbackParameter() {
        return callbackParameter;
    }

    /**
     * Prefix JSON with "{} &&"
     *
     * @param prefix
     */
    public void setPrefix(boolean prefix) {
        this.prefix = prefix;
    }

    /**
     * Content type to be set in the response
     *
     * @param contentType
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getWrapPrefix() {
        return wrapPrefix;
    }

    /**
     * Text to be inserted at the begining of the response
     */
    public void setWrapPrefix(String wrapPrefix) {
        this.wrapPrefix = wrapPrefix;
    }

    public String getWrapSuffix() {
        return wrapSuffix;
    }

    /**
     * Text to be inserted at the end of the response
     */
    public void setWrapSuffix(String wrapSuffix) {
        this.wrapSuffix = wrapSuffix;
    }
}
