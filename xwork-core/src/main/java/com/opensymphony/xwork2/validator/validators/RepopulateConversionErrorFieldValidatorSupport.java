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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.validator.ValidationException;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 *
 * An abstract base class that adds in the capability to populate the stack with
 * a fake parameter map when a conversion error has occurred and the 'repopulateField'
 * property is set to "true".
 *
 * <p/>
 *
 *
 * <!-- START SNIPPET: javadoc -->
 *
 * The capability of auto-repopulating the stack with a fake parameter map when
 * a conversion error has occurred can be done with 'repopulateField' property
 * set to "true".
 *
 * <p/>
 *
 * This is typically usefull when one wants to repopulate the field with the original value
 * when a conversion error occurred. Eg. with a textfield that only allows an Integer
 * (the action class have an Integer field declared), upon conversion error, the incorrectly
 * entered integer (maybe a text 'one') will not appear when dispatched back. With 'repopulateField'
 * porperty set to true, it will, meaning the textfield will have 'one' as its value
 * upon conversion error.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/>
 *
 * <pre>
 * <!-- START SNIPPET: exampleJspPage -->
 *
 * &lt;!-- myJspPage.jsp --&gt;
 * &lt;ww:form action="someAction" method="POST"&gt;
 *   ....
 *   &lt;ww:textfield
 *       label="My Integer Field"
 *       name="myIntegerField" /&gt;
 *   ....
 *   &lt;ww:submit /&gt;
 * &lt;/ww:form&gt;
 *
 * <!-- END SNIPPET: exampleJspPage -->
 * </pre>
 *
 * <pre>
 * <!-- START SNIPPET: exampleXwork -->
 *
 * &lt;!-- xwork.xml --&gt;
 * &lt;xwork&gt;
 * &lt;include file="xwork-default.xml" /&gt;
 * ....
 * &lt;package name="myPackage" extends="xwork-default"&gt;
 *   ....
 *   &lt;action name="someAction" class="example.MyActionSupport.java"&gt;
 *      &lt;result name="input"&gt;myJspPage.jsp&lt;/result&gt;
 *      &lt;result&gt;success.jsp&lt;/result&gt;
 *   &lt;/action&gt;
 *   ....
 * &lt;/package&gt;
 * ....
 * &lt;/xwork&gt;
 *
 * <!-- END SNIPPET:exampleXwork -->
 * </pre>
 *
 *
 * <pre>
 * <!-- START SNIPPET: exampleJava -->
 *
 * &lt;!-- MyActionSupport.java --&gt;
 * public class MyActionSupport extends ActionSupport {
 *    private Integer myIntegerField;
 *
 *    public Integer getMyIntegerField() { return this.myIntegerField; }
 *    public void setMyIntegerField(Integer myIntegerField) {
 *       this.myIntegerField = myIntegerField;
 *    }
 * }
 *
 * <!-- END SNIPPET: exampleJava -->
 * </pre>
 *
 *
 * <pre>
 * <!-- START SNIPPET: exampleValidation -->
 *
 * &lt;!-- MyActionSupport-someAction-validation.xml --&gt;
 * &lt;validators&gt;
 *   ...
 *   &lt;field name="myIntegerField"&gt;
 *      &lt;field-validator type="conversion"&gt;
 *         &lt;param name="repopulateField"&gt;true&lt;/param&gt;
 *         &lt;message&gt;Conversion Error (Integer Wanted)&lt;/message&gt;
 *      &lt;/field-validator&gt;
 *   &lt;/field&gt;
 *   ...
 * &lt;/validators&gt;
 *
 * <!-- END SNIPPET: exampleValidation -->
 * </pre>
 *
 * @author tm_jee
 * @version $Date$ $Id$
 */
public abstract class RepopulateConversionErrorFieldValidatorSupport extends FieldValidatorSupport {

    private static final Logger LOG = LoggerFactory.getLogger(RepopulateConversionErrorFieldValidatorSupport.class);

    private boolean repopulateField = false;

    public boolean isRepopulateField() {
        return repopulateField;
    }

    public void setRepopulateField(boolean repopulateField) {
        this.repopulateField = repopulateField;
    }

    public void validate(Object object) throws ValidationException {
        doValidate(object);
        if (repopulateField) {
            repopulateField(object);
        }
    }

    public void repopulateField(Object object) throws ValidationException {

        ActionInvocation invocation = ActionContext.getContext().getActionInvocation();
        Map<String, Object> conversionErrors = ActionContext.getContext().getConversionErrors();

        String fieldName = getFieldName();
        String fullFieldName = getValidatorContext().getFullFieldName(fieldName);
        if (conversionErrors.containsKey(fullFieldName)) {
            Object value = conversionErrors.get(fullFieldName);

            final Map<Object, Object> fakeParams = new LinkedHashMap<Object, Object>();
            boolean doExprOverride = false;

            if (value instanceof String[]) {
                // take the first element, if possible
                String[] tmpValue = (String[]) value;
                if ((tmpValue.length > 0)) {
                    doExprOverride = true;
                    fakeParams.put(fullFieldName, escape(tmpValue[0]));
                } else {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("value is an empty array of String or with first element in it as null [" + value + "], will not repopulate conversion error ");
                    }
                }
            } else if (value instanceof String) {
                String tmpValue = (String) value;
                doExprOverride = true;
                fakeParams.put(fullFieldName, escape(tmpValue));
            } else {
                // opps... it should be 
                if (LOG.isWarnEnabled()) {
                    LOG.warn("conversion error value is not a String or array of String but instead is [" + value + "], will not repopulate conversion error");
                }
            }

            if (doExprOverride) {
                invocation.addPreResultListener(new PreResultListener() {
                    public void beforeResult(ActionInvocation invocation, String resultCode) {
                        ValueStack stack = ActionContext.getContext().getValueStack();
                        stack.setExprOverrides(fakeParams);
                    }
                });
            }
        }
    }

    protected String escape(String value) {
        return "\"" + StringEscapeUtils.escapeJava(value) + "\"";
    }

    protected abstract void doValidate(Object object) throws ValidationException;
}
