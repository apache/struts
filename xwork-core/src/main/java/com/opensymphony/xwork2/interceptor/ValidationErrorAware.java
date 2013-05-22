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

package com.opensymphony.xwork2.interceptor;

/**
 * ValidationErrorAware classes can be notified about validation errors
 * before {@link com.opensymphony.xwork2.interceptor.DefaultWorkflowInterceptor} will return 'inputResultName' result
 * to allow change or not the result name
 *
 * This interface can be only applied to action which already implements {@link com.opensymphony.xwork2.ValidationAware} interface!
 *
 * @since 2.3.15
 */
public interface ValidationErrorAware {

    /**
     * Allows to notify action about occurred action/field errors
     *
     * @param currentResultName current result name, action can change it or return the same
     * @return new result name or passed currentResultName
     */
    String actionErrorOccurred(final String currentResultName);

}
