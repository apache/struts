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

import com.opensymphony.xwork2.SimpleAnnotationAction;
import com.opensymphony.xwork2.validator.annotations.IntRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;

/**
 * SimpleAction2
 *
 * @author Jason Carreira
 * @author Rainer Hermanns
 *         Created Jun 14, 2003 9:51:12 PM
 */
public class SimpleAnnotationAction2 extends SimpleAnnotationAction {

    private int count;

    @RequiredFieldValidator(message = "You must enter a value for count.")
    @IntRangeFieldValidator(min = "0", max = "5", message = "count must be between ${min} and ${max}, current value is ${count}.")
    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
