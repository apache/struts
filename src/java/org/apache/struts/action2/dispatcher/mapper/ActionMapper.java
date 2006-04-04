/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.struts.action2.dispatcher.mapper;

import javax.servlet.http.HttpServletRequest;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * The ActionMapper is responsible for providing a mapping between HTTP requests and action invocation requests and
 * vice-versa. When given an HttpServletRequest, the ActionMapper may return null if no action invocation request maps,
 * or it may return an {@link ActionMapping} that describes an action invocation that Struts should attempt to try. The
 * ActionMapper is not required to guarantee that the {@link ActionMapping} returned be a real action or otherwise
 * ensure a valid request. This means that most ActionMappers do not need to consult Struts Action Framework 's configuration to
 * determine if a request should be mapped.
 *
 * <p/> Just as requests can be mapped from HTTP to an action invocation, the opposite is true as well. However, because
 * HTTP requests (when shown in HTTP responses) must be in String form, a String is returned rather than an actual
 * request object.
 *
 * <!-- END SNIPPET: javadoc -->
 */
public interface ActionMapper {
    ActionMapping getMapping(HttpServletRequest request);

    String getUriFromActionMapping(ActionMapping mapping);
}
