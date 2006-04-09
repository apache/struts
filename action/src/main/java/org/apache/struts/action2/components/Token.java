/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.struts.action2.components;

import org.apache.struts.action2.util.TokenHelper;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * <!-- START SNIPPET: javadoc -->
 * Stop double-submission of forms.</p>
 *
 * The token tag is used to help with the "double click" submission problem. It is needed if you are using the
 * TokenInterceptor or the TokenSessionInterceptor. The a:token tag merely places a hidden element that contains
 * the unique token.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;a:token /&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Patrick Lightbody
 * @author Rene Gielen
 * @author Rainer Hermanns
 * @version $Date$ $Id$
 * @since 2.2
 *
 * @see org.apache.struts.action2.interceptor.TokenInterceptor
 * @see org.apache.struts.action2.interceptor.TokenSessionStoreInterceptor
 *
 * @a2.tag name="token" tld-body-content="JSP" tld-tag-class="org.apache.struts.action2.views.jsp.ui.TokenTag"
 * description="Stop double-submission of forms"
 */
public class Token extends UIBean {
    
    public static final String TEMPLATE = "token";

    public Token(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    /**
     * First looks for the token in the PageContext using the supplied name (or {@link org.apache.struts.action2.util.TokenHelper#DEFAULT_TOKEN_NAME}
     * if no name is provided) so that the same token can be re-used for the scope of a request for the same name. If
     * the token is not in the PageContext, a new Token is created and set into the Session and the PageContext with
     * the name.
     */
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        String tokenName;
        Map parameters = getParameters();

        if (parameters.containsKey("name")) {
            tokenName = (String) parameters.get("name");
        } else {
            if (name == null) {
                tokenName = TokenHelper.DEFAULT_TOKEN_NAME;
            } else {
                tokenName = findString(name);

                if (tokenName == null) {
                    tokenName = name;
                }
            }

            addParameter("name", tokenName);
        }

        String token = buildToken(tokenName);
        addParameter("token", token);
        addParameter("tokenNameField", TokenHelper.TOKEN_NAME_FIELD);
    }

    /**
     * This will be removed in a future version of Struts Action Framework.
     * @deprecated Templates should use $parameters from now on, not $tag.
     */
    public String getTokenNameField() {
        return TokenHelper.TOKEN_NAME_FIELD;
    }

    private String buildToken(String name) {
        Map context = stack.getContext();
        Object myToken = context.get(name);

        if (myToken == null) {
            myToken = TokenHelper.setToken(name);
            context.put(name, myToken);
        }

        return myToken.toString();
    }
}
