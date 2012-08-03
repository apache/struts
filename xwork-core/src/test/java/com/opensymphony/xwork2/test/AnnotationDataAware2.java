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
package com.opensymphony.xwork2.test;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;


/**
 * Used to test hierarchy traversal for interfaces.
 *
 * @author Mark Woon
 * @author Rainer Hermanns
 */
public interface AnnotationDataAware2 extends AnnotationDataAware {

    @RequiredStringValidator(message = "You must enter a value for data.")
    public void setBling(String bling);

    public String getBling();
}
