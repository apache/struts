/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tiles.request.attribute.Addable;

/**
 * Encapsulation of request information.
 */
public interface Request {

    /** the name of the (mandatory) "application" scope */
    String APPLICATION_SCOPE = "application";

    /** the name of the "request" context */
    String REQUEST_SCOPE = "request";

    /**
     * Return an immutable Map that maps header names to the first (or only)
     * header value (as a String).
     *
     * @return The header map.
     */
    Map<String, String> getHeader();

    /**
     * Return an immutable Map that maps header names to the set of all values
     * specified in the request (as a String array). Header names must be
     * matched in a case-insensitive manner.
     *
     * @return The header values map.
     */
    Map<String, String[]> getHeaderValues();

    /**
     * Return an Addable object that can be used to write headers to the response.
     *
     * @return An Addable object.
     */
    Addable<String> getResponseHeaders();

    /**
     * Returns a context map, given the scope name.
     * This method always return a map for all the scope names returned by
     * getAvailableScopes(). That map may be writable, or immutable, depending
     * on the implementation.
     *
     * @param scope The name of the scope.
     * @return The context.
     */
    Map<String, Object> getContext(String scope);

    /**
     * Returns all available scopes.
     * The scopes are ordered according to their lifetime,
     * the innermost, shorter lived scope appears first,
     * and the outermost, longer lived scope appears last.
     * Besides, the scopes "request" and "application" always included
     * in the list.
     *
     * @return All the available scopes.
     */
    List<String> getAvailableScopes();

    /**
     * Returns the associated application context.
     *
     * @return The application context associated to this request.
     */
    ApplicationContext getApplicationContext();

    /**
     * Returns an output stream to be used to write directly in the response.
     *
     * @return The output stream that writes in the response.
     * @throws IOException If something goes wrong when getting the output stream.
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * Returns a writer to be used to write directly in the response.
     *
     * @return The writer that writes in the response.
     * @throws IOException If something goes wrong when getting the writer.
     */
    Writer getWriter() throws IOException;

    /**
     * Returns a print writer to be used to write directly in the response.
     *
     * @return The print writer that writes in the response.
     * @throws IOException If something goes wrong when getting the print
     * writer.
     */
    PrintWriter getPrintWriter() throws IOException;

    /**
     * Checks if the response has been committed.
     *
     * @return <code>true</code> only if the response has been committed.
     */
    boolean isResponseCommitted();

    /**
     * Return an immutable Map that maps request parameter names to the first
     * (or only) value (as a String).
     *
     * @return The parameter map.
     */
    Map<String, String> getParam();

    /**
     * Return an immutable Map that maps request parameter names to the set of
     * all values (as a String array).
     *
     * @return The parameter values map.
     */
    Map<String, String[]> getParamValues();

    /**
     * Return the preferred Locale in which the client will accept content.
     *
     * @return The current request locale. It is the locale of the request
     * object itself, and it is NOT the locale that the user wants to use. See
     * {@link org.apache.tiles.core.locale.LocaleResolver} to implement strategies to
     * resolve locales.
     */
    Locale getRequestLocale();

    /**
     * Determine whether the specified user is in the given role.
     * @param role the role to check against.
     * @return <code>true</code> if the user is in the given role.
     */
    boolean isUserInRole(String role);

}
