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
package org.apache.struts2.xwork2.validator.validators;

import org.apache.struts2.xwork2.validator.ValidationException;
import org.apache.struts2.xwork2.util.URLUtil;


/**
 * <!-- START SNIPPET: javadoc -->
 * 
 * URLValidator checks that a given field is a String and a valid URL
 * 
 * <!-- END SNIPPET: javadoc -->
 * 
 * <p/>
 * 
 * <!-- START SNIPPET: parameters -->
 * 
 * <ul>
 * 		<li>fieldName - The field name this validator is validating. Required if using Plain-Validator Syntax otherwise not required</li>
 * </ul>
 * 
 * <!-- END SNIPPET: parameters -->
 *
 * <p/>
 *
 * <pre>
 * <!-- START SNIPPET: examples -->
 * 
 *     &lt;validators&gt;
 *          &lt;!-- Plain Validator Syntax --&gt;
 *          &lt;validator type="url"&gt;
 *              &lt;param name="fieldName"&gt;myHomePage&lt;/param&gt;
 *              &lt;message&gt;Invalid homepage url&lt;/message&gt;
 *          &lt;/validator&gt;
 *          
 *          &lt;!-- Field Validator Syntax --&gt;
 *          &lt;field name="myHomepage"&gt;
 *              &lt;message&gt;Invalid homepage url&lt;/message&gt;
 *          &lt;/field&gt;
 *     &lt;/validators&gt;
 *     
 * <!-- END SNIPPET: examples -->
 * </pre>
 *
 *
 * @author $Author: lukaszlenart $
 * @version $Date: 2011-12-02 12:24:48 +0100 (Fri, 02 Dec 2011) $ $Revision: 1209415 $
 */
public class URLValidator extends FieldValidatorSupport {

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);

        // if there is no value - don't do comparison
        // if a value is required, a required validator should be added to the field
        if (value == null || value.toString().length() == 0) {
            return;
        }

        if (!(value.getClass().equals(String.class)) || !URLUtil.verifyUrl((String) value)) {
            addFieldError(fieldName, object);
        }
    }
}
