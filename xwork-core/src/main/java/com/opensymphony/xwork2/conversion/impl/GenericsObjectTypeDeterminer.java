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

package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.util.reflection.ReflectionProvider;


/**
 * GenericsObjectTypeDeterminer
 *
 * @author Patrick Lightbody
 * @author Rainer Hermanns
 * @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
 * 
 * @deprecated Use DefaultObjectTypeDeterminer instead. Since XWork 2.0.4 the DefaultObjectTypeDeterminer handles the
 *             annotation processing.
 */
@Deprecated public class GenericsObjectTypeDeterminer extends DefaultObjectTypeDeterminer {

    public GenericsObjectTypeDeterminer(XWorkConverter conv,
            XWorkBasicConverter basicConv, ReflectionProvider prov) {
        super(conv, prov);
    }
}
