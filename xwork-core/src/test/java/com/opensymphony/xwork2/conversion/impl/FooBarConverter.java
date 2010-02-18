/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.util.AnnotatedCat;
import com.opensymphony.xwork2.util.Bar;
import com.opensymphony.xwork2.util.Cat;

import java.lang.reflect.Member;
import java.util.Map;


/**
 * @author <a href="mailto:plightbo@cisco.com">Pat Lightbody</a>
 * @author $Author$
 * @version $Revision$
 */
public class FooBarConverter extends DefaultTypeConverter {

    @Override
    public Object convertValue(Map<String, Object> context, Object value, Class toType) {
        if (toType == String.class) {
            Bar bar = (Bar) value;

            return bar.getTitle() + ":" + bar.getSomethingElse();
        } else if (toType == Bar.class) {
            String valueStr = (String) value;
            int loc = valueStr.indexOf(":");
            String title = valueStr.substring(0, loc);
            String rest = valueStr.substring(loc + 1);

            Bar bar = new Bar();
            bar.setTitle(title);
            bar.setSomethingElse(Integer.parseInt(rest));

            return bar;
        } else if (toType == Cat.class) {
            Cat cat = new Cat();
            cat.setName((String) value);

            return cat;
        } else if (toType == AnnotatedCat.class) {
            AnnotatedCat cat = new AnnotatedCat();
            cat.setName((String) value);

            return cat;
        } else {
            System.out.println("Don't know how to convert between " + value.getClass().getName() +
                    " and " + toType.getName());
        }

        return null;
    }

    @Override
    public Object convertValue(Map<String, Object> context, Object source, Member member, String property, Object value, Class toClass) {
        return convertValue(context, value, toClass);
    }
}
