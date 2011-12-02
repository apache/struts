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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.validator.annotations.IntRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

import java.util.Date;


/**
 * AnnotatedTestBean
 * @author Jason Carreira
 * @author Rainer Hermanns
 * Created Aug 4, 2003 12:39:53 AM
 */
public class AnnotatedTestBean {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Date birth;
    private String name;
    private int count;

    //~ Constructors ///////////////////////////////////////////////////////////

    public AnnotatedTestBean() {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public Date getBirth() {
        return birth;
    }

    @Validations(
            intRangeFields = {
                @IntRangeFieldValidator(shortCircuit = true, min = "1", max="100", key="invalid.count", message = "Invalid Count!"),
                @IntRangeFieldValidator(shortCircuit = true, min = "20", max="28", key="invalid.count.bad", message = "Smaller Invalid Count: ${count}")
            }

    )
    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @RequiredStringValidator(message = "You must enter a name.")
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
