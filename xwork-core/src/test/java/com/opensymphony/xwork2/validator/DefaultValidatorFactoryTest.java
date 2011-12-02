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
package com.opensymphony.xwork2.validator;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import junit.framework.TestCase;

/**
 * <code>DefaultValidatorFactoryTest</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
 */
public class DefaultValidatorFactoryTest extends TestCase {

    public void testParseValidators() {
        Mock mockValidatorFileParser = new Mock(ValidatorFileParser.class);
        mockValidatorFileParser.expect("parseValidatorDefinitions", C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("com/opensymphony/xwork2/validator/validators/default.xml")));
        mockValidatorFileParser.expect("parseValidatorDefinitions", C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("validators.xml")));
        mockValidatorFileParser.expect("parseValidatorDefinitions", C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("myOther-validators.xml")));
        mockValidatorFileParser.expect("parseValidatorDefinitions", C.args(C.IS_NOT_NULL, C.IS_NOT_NULL, C.eq("my-validators.xml")));
        DefaultValidatorFactory factory = new DefaultValidatorFactory(null, (ValidatorFileParser) mockValidatorFileParser.proxy());
    }
}
