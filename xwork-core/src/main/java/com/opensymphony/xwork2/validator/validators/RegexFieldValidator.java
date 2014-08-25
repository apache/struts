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

package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.validator.ValidationException;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <!-- START SNIPPET: javadoc -->
 * Validates a string field using a regular expression.
 * <!-- END SNIPPET: javadoc -->
 *
 * 
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * 	  <li>fieldName - The field name this validator is validating. Required if using Plain-Validator Syntax otherwise not required</li>
 *    <li>regexp - The RegExp expression</li>
 *    <li>caseSensitive - Boolean (Optional). Sets whether the expression should be matched against in a case-sensitive way. Default is <code>true</code>.</li>
 *    <li>trim - Boolean (Optional). Sets whether the expression should be trimmed before matching. Default is <code>true</code>.</li>
 *    <li>regexExpression - String (Optional). Defines regExp expression as an OGNL expression - will be evaluated to String</li>
 *    <li>caseSensitiveExpression - String (Optional). Defines caseSensitive param as an OGNL expression - will be evaluated to Boolean.</li>
 *    <li>trimExpression - String (Optional). Defines trim param as an OGNL expression - will be evaluated to Boolean</li>
 * </ul>
 * You can mix normal params with expression aware params but thus was not tested
 * <!-- END SNIPPET: parameters -->
 *
 * <!-- START SNIPPET: parameters-warning -->
 * Do not use ${regexExpression}, ${caseSensitiveExpression} and ${trimExpression} as an expression as this will turn into infinitive loop!
 * <!-- END SNIPPET: parameters-warning -->
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;validators&gt;
 *     &lt;!-- Plain Validator Syntax --&gt;
 *     &lt;validator type="regex"&gt;
 *         &lt;param name="fieldName"&gt;myStrangePostcode&lt;/param&gt;
 *         &lt;param name="regex"&gt;&lt;![CDATA[([aAbBcCdD][123][eEfFgG][456])]]&gt;&lt;/param&gt;
 *     &lt;/validator&gt;
 *
 *     &lt;!-- Field Validator Syntax --&gt;
 *     &lt;field name="myStrangePostcode"&gt;
 *         &lt;field-validator type="regex"&gt;
 *             &lt;param name="regex"&gt;&lt;![CDATA[([aAbBcCdD][123][eEfFgG][456])]]&gt;&lt;/param&gt;
 *         &lt;/field-validator&gt;
 *     &lt;/field&gt;
 *
 *     &lt;!-- Field Validator Syntax with expressions --&gt;
 *     &lt;field name="myStrangePostcode"&gt;
 *         &lt;field-validator type="regex"&gt;
 *             &lt;param name="regexExpression"&gt;${regexValue}&lt;/param&gt; &lt;!-- will be evaluated as: String getRegexValue() --&gt;
 *             &lt;param name="caseSensitiveExpression"&gt;${caseSensitiveValue}&lt;/param&gt; &lt;!-- will be evaluated as: boolean getCaseSensitiveValue() --&gt;
 *             &lt;param name="trimExpression"&gt;${trimValue}&lt;/param&gt; &lt;!-- will be evaluated as: boolean getTrimValue() --&gt;
 *         &lt;/field-validator&gt;
 *     &lt;/field&gt;
 * &lt;/validators&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Quake Wang
 * @version $Date$ $Revision$
 */
public class RegexFieldValidator extends FieldValidatorSupport {

    private static final Logger LOG = LoggerFactory.getLogger(RegexFieldValidator.class);

    private String regex;
    private String regexExpression;
    private Boolean caseSensitive = true;
    private String caseSensitiveExpression = "";
    private Boolean trim = true;
    private String trimExpression = "";

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);
        // if there is no value - don't do comparison
        // if a value is required, a required validator should be added to the field
        String regexToUse = getRegex();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Defined regexp as [#0]", regexToUse);
        }
        if (value == null || regexToUse == null) {
            return;
        }

        // XW-375 - must be a string
        if (!(value instanceof String)) {
            return;
        }

        // string must not be empty
        String str = ((String) value).trim();
        if (str.length() == 0) {
            return;
        }

        // match against expression
        Pattern pattern;
        if (isCaseSensitive()) {
            pattern = Pattern.compile(regexToUse);
        } else {
            pattern = Pattern.compile(regexToUse, Pattern.CASE_INSENSITIVE);
        }

        String compare = (String) value;
        if ( isTrimed() ) {
            compare = compare.trim();
        }
        Matcher matcher = pattern.matcher( compare );

        if (!matcher.matches()) {
            addFieldError(fieldName, object);
        }
    }

    /**
     * @return Returns the regular expression to be matched.
     */
    public String getRegex() {
        if (StringUtils.isNotEmpty(regex)) {
            return regex;
        } else if (StringUtils.isNotEmpty(regexExpression)) {
            return (String) parse(regexExpression, String.class);
        } else {
            return null;
        }
    }

    /**
     * Sets the regular expression to be matched
     */
    public void setRegex(String regex) {
        this.regex = regex;
    }

    /**
     * Sets the regular expression as an OGNL expression to be matched
     */
    public void setRegexExpression(String regexExpression) {
        this.regexExpression = regexExpression;
    }

    /**
     * @return Returns whether the expression should be matched against in
     *         a case-sensitive way.  Default is <code>true</code>.
     */
    public boolean isCaseSensitive() {
        if (StringUtils.isNotEmpty(caseSensitiveExpression)) {
            return (Boolean) parse(caseSensitiveExpression, Boolean.class);
        }
        return caseSensitive;
    }

    /**
     * Sets whether the expression should be matched against in
     * a case-sensitive way.  Default is <code>true</code>.
     */
    public void setCaseSensitive(Boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * Allows specify caseSensitive param as an OGNL expression
     */
    public void setCaseSensitiveExpression(String caseSensitiveExpression) {
        this.caseSensitiveExpression = caseSensitiveExpression;
    }

    /**
     * @return Returns whether the expression should be trimed before matching.
     * Default is <code>true</code>.
     */
    public boolean isTrimed() {
        if (StringUtils.isNotEmpty(trimExpression)) {
            return (Boolean) parse(trimExpression, Boolean.class);
        }
        return trim;
    }

    /**
     * Sets whether the expression should be trimed before matching.
     * Default is <code>true</code>.
     */
    public void setTrim(Boolean trim) {
        this.trim = trim;
    }

    /**
     * Allows specify trim param as an OGNL expression
     */
    public void setTrimExpression(String trimExpression) {
        this.trimExpression = trimExpression;
    }

}
