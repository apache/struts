/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.freemarker.ext.jsp;

import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.StringUtil;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class TaglibMethodUtil {

    private TaglibMethodUtil() {
        // Not meant to be instantiated
    }

    private static final Pattern FUNCTION_SIGNATURE_PATTERN = 
            Pattern.compile("^([\\w\\.]+(\\s*\\[\\s*\\])?)\\s+(\\w+)\\s*\\((.*)\\)$", Pattern.DOTALL);
    private static final Pattern FUNCTION_PARAMETER_PATTERN = 
            Pattern.compile("^([\\w\\.]+)(\\s*\\[\\s*\\])?$");

    /**
     * Finds method by function signature string which is compliant with
     * Tag Library function signature in Java Server Page (TM) Specification.
     * A function signature example is as follows: {@code java.lang.String nickName( java.lang.String, int)}
     * 
     * @param clazz Class having the method.
     * @param signature Java Server Page (TM) Specification compliant function signature string.
     * @return method if found.
     */
    static Method getMethodByFunctionSignature(Class clazz, String signature)
            throws SecurityException, NoSuchMethodException, ClassNotFoundException {
        Matcher m1 = FUNCTION_SIGNATURE_PATTERN.matcher(signature);

        if (!m1.matches()) {
            throw new IllegalArgumentException("Invalid function signature (doesn't match this pattern: "
                    + FUNCTION_SIGNATURE_PATTERN + ")");
        }

            String methodName = m1.group(3);
            String params = m1.group(4).trim();
            Class [] paramTypes = null;

            if ("".equals(params)) {
                paramTypes = new Class[0];
            } else {
                String [] paramsArray = StringUtil.split(params, ',');
                paramTypes = new Class[paramsArray.length];
                String token = null;
                String paramType = null;
                boolean isPrimitive = false;
                boolean isArrayType = false;
                Matcher m2 = null;

                for (int i = 0; i < paramsArray.length; i++) {
                    token = paramsArray[i].trim();
                    m2 = FUNCTION_PARAMETER_PATTERN.matcher(token);
                    if (!m2.matches()) {
                        throw new IllegalArgumentException("Invalid argument signature (doesn't match pattern " +
                                FUNCTION_PARAMETER_PATTERN + "): " + token);
                    }

                    paramType = m2.group(1);
                    isPrimitive = (paramType.indexOf('.') == -1);
                    isArrayType = (m2.group(2) != null);

                    if (isPrimitive) {
                        if ("byte".equals(paramType)) {
                            paramTypes[i] = (isArrayType ? byte[].class : byte.class);
                        } else if ("short".equals(paramType)) {
                            paramTypes[i] = (isArrayType ? short[].class : short.class);
                        } else if ("int".equals(paramType)) {
                            paramTypes[i] = (isArrayType ? int[].class : int.class);
                        } else if ("long".equals(paramType)) {
                            paramTypes[i] = (isArrayType ? long[].class : long.class);
                        } else if ("float".equals(paramType)) {
                            paramTypes[i] = (isArrayType ? float[].class : float.class);
                        } else if ("double".equals(paramType)) {
                            paramTypes[i] = (isArrayType ? double[].class : double.class);
                        } else if ("boolean".equals(paramType)) {
                            paramTypes[i] = (isArrayType ? boolean[].class : boolean.class);
                        } else if ("char".equals(paramType)) {
                            paramTypes[i] = (isArrayType ? char[].class : char.class);
                        } else {
                            throw new IllegalArgumentException("Invalid primitive type: '" + paramType + "'.");
                        }
                    } else {
                        if (isArrayType) {
                            paramTypes[i] = ClassUtil.forName("[L" + paramType + ";");
                        } else {
                            paramTypes[i] = ClassUtil.forName(paramType);
                        }
                    }
                }
            }

            return clazz.getMethod(methodName, paramTypes);
    }
    
}
