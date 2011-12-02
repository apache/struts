/*
 * Copyright 2002-2007,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2;

/**
 * All actions <b>may</b> implement this interface, which exposes the <code>execute()</code> method.
 * <p/>
 * However, as of XWork 1.1, this is <b>not</b> required and is only here to assist users. You are free to create POJOs
 * that honor the same contract defined by this interface without actually implementing the interface.
 */
public interface Action {

    /**
     * The action execution was successful. Show result
     * view to the end user.
     */
    public static final String SUCCESS = "success";

    /**
     * The action execution was successful but do not
     * show a view. This is useful for actions that are
     * handling the view in another fashion like redirect.
     */
    public static final String NONE = "none";

    /**
     * The action execution was a failure.
     * Show an error view, possibly asking the
     * user to retry entering data.
     */
    public static final String ERROR = "error";

    /**
     * The action execution require more input
     * in order to succeed.
     * This result is typically used if a form
     * handling action has been executed so as
     * to provide defaults for a form. The
     * form associated with the handler should be
     * shown to the end user.
     * <p/>
     * This result is also used if the given input
     * params are invalid, meaning the user
     * should try providing input again.
     */
    public static final String INPUT = "input";

    /**
     * The action could not execute, since the
     * user most was not logged in. The login view
     * should be shown.
     */
    public static final String LOGIN = "login";


    /**
     * Where the logic of the action is executed.
     *
     * @return a string representing the logical result of the execution.
     *         See constants in this interface for a list of standard result values.
     * @throws Exception thrown if a system level exception occurs.
     *                   <b>Note:</b> Application level exceptions should be handled by returning
     *                   an error value, such as <code>Action.ERROR</code>.
     */
    public String execute() throws Exception;

}
