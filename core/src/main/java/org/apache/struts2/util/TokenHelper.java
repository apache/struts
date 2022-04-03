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
package org.apache.struts2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocalizedTextProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.Parameter;
import org.apache.struts2.dispatcher.HttpParameters;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;

/**
 * TokenHelper
 *
 */
public class TokenHelper {

	/**
	 * The default namespace for storing token session values
	 */
	public static final String TOKEN_NAMESPACE = "struts.tokens";

	/**
     * The default name to map the token value
     */
    public static final String DEFAULT_TOKEN_NAME = "token";

    /**
     * The name of the field which will hold the token name
     */
    public static final String TOKEN_NAME_FIELD = "struts.token.name";
    private static final Logger LOG = LogManager.getLogger(TokenHelper.class);
    private static final Random RANDOM = new SecureRandom();


    /**
     * Sets a transaction token into the session using the default token name.
     *
     * @return the token string
     */
    public static String setToken() {
        return setToken(DEFAULT_TOKEN_NAME);
    }

	/**
	 * Sets a transaction token into the session based on the provided token name.
	 *
	 * @param tokenName the token name based on which a generated token value is stored into session; for actual session
	 *                  store, this name will be prefixed by a namespace.
	 *
	 * @return the token string
	 */
	public static String setToken( String tokenName ) {
		String token = generateGUID();
		setSessionToken(tokenName, token);
		return token;
	}

	/**
	 * Put a given named token into the session map. The token will be stored with a namespace prefix prepended.
	 *
	 * @param tokenName the token name based on which given token value is stored into session; for actual session store,
	 *                  this name will be prefixed by a namespace.
	 * @param token     the token value to store
	 */
	public static void setSessionToken( String tokenName, String token ) {
		Map<String, Object> session = ActionContext.getContext().getSession();
		try {
			session.put(buildTokenSessionAttributeName(tokenName), token);
		} catch ( IllegalStateException e ) {
			// WW-1182 explain to user what the problem is
            String msg = "Error creating HttpSession due response is committed to client. You can use the CreateSessionInterceptor or create the HttpSession from your action before the result is rendered to the client: " + e.getMessage();
            LOG.error(msg, e);
            throw new IllegalArgumentException(msg);
		}
	}


	/**
	 * Build a name-spaced token session attribute name based on the given token name.
	 *
	 * @param tokenName the token name to prefix
	 *
	 * @return the name space prefixed session token name
	 */
	public static String buildTokenSessionAttributeName( String tokenName ) {
		return TOKEN_NAMESPACE + "." + tokenName;
	}

	/**
     * Gets a transaction token from the params in the ServletActionContext using the default token name.
     *
     * @return token
     */
    public static String getToken() {
        return getToken(DEFAULT_TOKEN_NAME);
    }

    /**
     * Gets the Token value from the params in the ServletActionContext using the given name
     *
     * @param tokenName the name of the parameter which holds the token value
     * @return the token String or null, if the token could not be found
     */
    public static String getToken(String tokenName) {
        if (tokenName == null ) {
            return null;
        }
        HttpParameters params = ActionContext.getContext().getParameters();
        Parameter parameter = params.get(tokenName);

        if (!parameter.isDefined()) {
            LOG.warn("Could not find token mapped to token name: {}", tokenName);
            return null;
        }
        return parameter.getValue();
    }

    /**
     * Gets the token name from the Parameters in the ServletActionContext
     *
     * @return the token name found in the params, or null if it could not be found
     */
    public static String getTokenName() {
        HttpParameters params = ActionContext.getContext().getParameters();

        if (!params.contains(TOKEN_NAME_FIELD)) {
        	LOG.warn("Could not find token name in params.");
            return null;
        }

        Parameter parameter = params.get(TOKEN_NAME_FIELD);
        if (!parameter.isDefined()) {
        	LOG.warn("Got a null or empty token name.");
            return null;
        }
        return parameter.getValue();
    }

    /**
     * Checks for a valid transaction token in the current request params. If a valid token is found, it is
     * removed so the it is not valid again.
     *
     * @return false if there was no token set into the params (check by looking for {@link #TOKEN_NAME_FIELD}), true if a valid token is found
     */
    public static boolean validToken() {
        String tokenName = getTokenName();

        if (tokenName == null) {
            LOG.debug("No token name found -> Invalid token ");
            return false;
        }

        String token = getToken(tokenName);

        if (token == null) {
            LOG.debug("No token found for token name {} -> Invalid token ", tokenName);
            return false;
        }

        Map session = ActionContext.getContext().getSession();
		String tokenSessionName = buildTokenSessionAttributeName(tokenName);
        String sessionToken = (String) session.get(tokenSessionName);

        if (!token.equals(sessionToken)) {
            if (LOG.isWarnEnabled()) {
                LocalizedTextProvider localizedTextProvider = ActionContext.getContext().getContainer().getInstance(LocalizedTextProvider.class);
                LOG.warn(localizedTextProvider.findText(TokenHelper.class, "struts.internal.invalid.token", ActionContext.getContext().getLocale(), "Form token {0} does not match the session token {1}.", new Object[]{
                        token, sessionToken
                }));
            }

            return false;
        }

        // remove the token so it won't be used again
        session.remove(tokenSessionName);

        return true;
    }

    public static String generateGUID() {
        return new BigInteger(165, RANDOM).toString(36).toUpperCase();
    }
}
