package com.opensymphony.webwork.dispatcher.mapper;

import javax.servlet.http.HttpServletRequest;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * The ActionMapper is responsible for providing a mapping between HTTP requests and action invocation requests and
 * vice-versa. When given an HttpServletRequest, the ActionMapper may return null if no action invocation request maps,
 * or it may return an {@link ActionMapping} that describes an action invocation that WebWork should attempt to try. The
 * ActionMapper is not required to guarantee that the {@link ActionMapping} returned be a real action or otherwise
 * ensure a valid request. This means that most ActionMappers do not need to consult WebWork's configuration to
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
