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
package example;

import com.opensymphony.xwork2.ActionSupport;

/**
 * <code>Set welcome message.</code>
 *
 */
public class Welcome extends ActionSupport {

	private static final long serialVersionUID = -3881551454078687096L;

    public static final String MESSAGE = "Struts is up and running ...";

    public String execute() throws Exception {
        setMessage(MESSAGE);
        return SUCCESS;
    }

    /**
     * Field for Message property.
     */
    private String message;

    /**
     * Return Message property.
     * @return Message property
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set Message property.
     * @param message Text to display on Welcome page.
     */
    public void setMessage(String message){
        this.message = message;
    }
}
