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
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.validators.EmailValidator;

/**
 * Test case for Email Validator
 *
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class EmailValidatorTest extends XWorkTestCase {

    public void testEmailValidity() throws Exception {
        assertTrue(verifyEmailValidity("tmjee@yahoo.com"));
        assertTrue(verifyEmailValidityWithExpression("tmjee@yahoo.com", "\\b^[a-z]+@[a-z]+(\\.[a-z]+)*\\.com$\\b"));
        assertTrue(verifyEmailValidity("tm_jee@yahoo.co"));
        assertTrue(verifyEmailValidityWithExpression("tm_jee@yahoo.co", "\\b^[a-z_]+@[a-z]+(\\.[a-z]+)*\\.co$\\b"));
        assertTrue(verifyEmailValidity("tm.jee@yahoo.co.uk"));
        assertTrue(verifyEmailValidity("tm.jee@yahoo.co.biz"));
        assertTrue(verifyEmailValidity("tm_jee@yahoo.com"));
        assertTrue(verifyEmailValidity("tm_jee@yahoo.net"));
        assertTrue(verifyEmailValidity(" user@subname1.subname2.subname3.domainname.co.uk "));
        assertTrue(verifyEmailValidity("tm.j'ee@yahoo.co.uk"));
        assertTrue(verifyEmailValidity("tm.j'e.e'@yahoo.co.uk"));
        assertTrue(verifyEmailValidity("tmj'ee@yahoo.com"));
        assertTrue(verifyEmailValidity("ferda+mravenec@yahoo.com"));
        assertTrue(verifyEmailValidity("Ferda+Mravenec@yaHoo.CoM"));
        assertTrue(verifyEmailValidity("Ferda+Mravenec@yaHoo.cat"));

        assertFalse(verifyEmailValidity("tm_jee#marry@yahoo.co.uk"));
        assertFalse(verifyEmailValidity("tm_jee@ yahoo.co.uk"));
        assertFalse(verifyEmailValidity("tm_jee  @yahoo.co.uk"));
        assertFalse(verifyEmailValidity("tm_j ee  @yah oo.co.uk"));
        assertFalse(verifyEmailValidity("tm_jee  @yah oo.co.uk"));
        assertFalse(verifyEmailValidity("tm_jee @ yahoo.com"));
        assertFalse(verifyEmailValidity(" user@subname1.subname2.subname3.domainn#ame.co.uk "));
        assertFalse(verifyEmailValidity("aaa@aa.aaaaaaa"));
        assertFalse(verifyEmailValidity("+ferdamravenec@yahoo.com"));

        assertTrue(verifyEmailValidityWithExpression("tmjee@yahoo.co", "\\b^[a-z]+@[a-z]+(\\.[a-z]+)*\\.com$\\b"));
    }

    protected boolean verifyEmailValidity(final String email) throws Exception {
        ActionSupport action = new ActionSupport() {
            public String getMyEmail() {
                return email;
            }
        };

        EmailValidator validator = new EmailValidator();
        validator.setValidatorContext(new DelegatingValidatorContext(action));
        validator.setFieldName("myEmail");
        validator.setDefaultMessage("invalid email");
        validator.setValueStack(ActionContext.getContext().getValueStack());
        validator.validate(action);

        return (action.getFieldErrors().size() == 0);
    }

    public boolean verifyEmailValidityWithExpression(final String email, final String expression) throws Exception {
        ActionSupport action = new ActionSupport() {
            public String getMyEmail() {
                return email;
            }

            public String getEmailExpression() {
                return expression;
            }
        };

        EmailValidator validator = new EmailValidator();
        ValueStack valueStack = ActionContext.getContext().getValueStack();
        valueStack.push(action);
        validator.setValueStack(valueStack);

        validator.setValidatorContext(new DelegatingValidatorContext(action));
        validator.setFieldName("myEmail");
        validator.setDefaultMessage("invalid email");
        validator.setRegexExpression("${emailExpression}");

        validator.validate(action);

        return (action.getFieldErrors().size() == 0);
    }

    public void testCaseSensitiveViaExpression() throws Exception {
        EmailValidator validator = verifyCaseSensitive(true);
        assertTrue(validator.isCaseSensitive());

        validator = verifyCaseSensitive(false);
        assertFalse(validator.isCaseSensitive());
    }

    private EmailValidator verifyCaseSensitive(final boolean caseSensitive) {
        ActionSupport action = new ActionSupport() {
            public boolean getEmailCaseSensitive() {
                return caseSensitive;
            }
        };

        EmailValidator validator = new EmailValidator();
        ValueStack valueStack = ActionContext.getContext().getValueStack();
        valueStack.push(action);
        validator.setValueStack(valueStack);

        validator.setCaseSensitiveExpression("${emailCaseSensitive}");

        return validator;
    }

    public void testTrimViaExpression() throws Exception {
        EmailValidator validator = verifyTrim(true);
        assertTrue(validator.isTrimed());

        validator = verifyTrim(false);
        assertFalse(validator.isTrimed());
    }

    private EmailValidator verifyTrim(final boolean trim) {
        ActionSupport action = new ActionSupport() {
            public boolean getTrimEmail() {
                return trim;
            }
        };

        EmailValidator validator = new EmailValidator();
        ValueStack valueStack = ActionContext.getContext().getValueStack();
        valueStack.push(action);
        validator.setValueStack(valueStack);

        validator.setTrimExpression("${trimEmail}");

        return validator;
    }

}
