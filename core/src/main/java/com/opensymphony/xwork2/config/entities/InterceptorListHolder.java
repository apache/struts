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
package com.opensymphony.xwork2.config.entities;

import java.util.List;

/**
 * InterceptorListHolder
 *
 * @author Jason Carreira
 *         Created Jun 1, 2003 1:02:48 AM
 */
public interface InterceptorListHolder {

    InterceptorListHolder addInterceptor(InterceptorMapping interceptor);

    InterceptorListHolder addInterceptors(List<InterceptorMapping> interceptors);
}
