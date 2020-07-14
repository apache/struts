package org.apache.struts2.interceptor;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface for the resource isolation policies to be used for fetch metadata checks.
 *
 * Resource isolation policies are designed to protect against cross origin attacks and use the
 * {@code sec-fetch-*} request headers to decide whether to accept or reject a request. Read more
 * about <a href="https://web.dev/fetch-metadata/">Fetch Metadata.</a>
 *
 * See {@link DefaultResourceIsolationPolicy} for the default implementation used.
 *
 * @see <a href="https://web.dev/fetch-metadata/">https://web.dev/fetch-metadata/</a>
 *
 * @author Santiago Diaz - saldiaz@google.com
 * @author Giannis Chatziveroglou - giannic@google.com
 **/

@FunctionalInterface
public interface ResourceIsolationPolicy {
    String SEC_FETCH_SITE_HEADER = "sec-fetch-site";
    String SEC_FETCH_MODE_HEADER = "sec-fetch-mode";
    String SEC_FETCH_DEST_HEADER = "sec-fetch-dest";
    String VARY_HEADER = "Vary";
    String SAME_ORIGIN = "same-origin";
    String SAME_SITE = "same-site";
    String NONE = "none";
    String MODE_NAVIGATE = "navigate";
    String DEST_OBJECT = "object";
    String DEST_EMBED = "embed";
    String CROSS_SITE = "cross-site";
    String CORS = "cors";
    String DEST_SCRIPT = "script";
    String DEST_IMAGE = "image";

    boolean isRequestAllowed(HttpServletRequest request);
}
