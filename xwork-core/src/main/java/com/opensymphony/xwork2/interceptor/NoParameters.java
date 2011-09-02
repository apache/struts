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
 * Marker interface to incidate no auto setting of parameters.
 * <p/>
 * This marker interface should be implemented by actions that do not want any
 * request parameters set on them automatically (by the ParametersInterceptor).
 * This may be useful if one is using the action tag and want to supply
 * the parameters to the action manually using the param tag.
 * It may also be useful if one for security reasons wants to make sure that
 * parameters cannot be set by malicious users.
 *
 * @author Dick Zetterberg (dick@transitor.se)
 */
public interface NoParameters {
}
