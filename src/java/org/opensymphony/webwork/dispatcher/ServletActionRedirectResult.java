package com.opensymphony.webwork.dispatcher;

import com.opensymphony.webwork.dispatcher.mapper.ActionMapper;
import com.opensymphony.webwork.dispatcher.mapper.ActionMapperFactory;
import com.opensymphony.webwork.dispatcher.mapper.ActionMapping;
import com.opensymphony.xwork.ActionInvocation;

/**
 * <!-- START SNIPPET: description -->
 *
 * This result uses the {@link ActionMapper} provided by the {@link ActionMapperFactory} to redirect the browser to a
 * URL that invokes the specified action and (optional) namespace. This is better than the {@link ServletRedirectResult}
 * because it does not require you to encode the URL patterns processed by the {@link ActionMapper} in to your xwork.xml
 * configuration files. This means you can change your URL patterns at any point and your application will still work.
 * It is strongly recommended that if you are redirecting to another action, you use this result rather than the
 * standard redirect result.
 *
 * <!-- END SNIPPET: description -->
 *
 * <b>This result type takes the following parameters:</b>
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *
 * <li><b>actionName (default)</b> - the name of the action that will be redirect to</li>
 *
 * <li><b>namespace</b> - used to determine which namespace the action is in that we're redirecting to . If namespace is
 * null, this defaults to the current namespace</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 * <b>Example:</b>
 *
 * <pre><!-- START SNIPPET: example -->
 * &lt;package name="public" extends="webwork-default"&gt;
 *     &lt;action name="login" class="..."&gt;
 *         &lt;!-- Redirect to another namespace --&gt;
 *         &lt;result type="redirect-action"&gt;
 *             &lt;param name="actionName"&gt;dashboard&lt;/param&gt;
 *             &lt;param name="namespace"&gt;/secure&lt;/param&gt;
 *         &lt;/result&gt;
 *     &lt;/action&gt;
 * &lt;/package&gt;
 *
 * &lt;package name="secure" extends="webwork-default" namespace="/secure"&gt;
 *     &lt;-- Redirect to an action in the same namespace --&gt;
 *     &lt;action name="dashboard" class="..."&gt;
 *         &lt;result&gt;dashboard.jsp&lt;/result&gt;
 *         &lt;result name="error" type="redirect-action&gt;error&lt;/result&gt;
 *     &lt;/action&gt;
 *
 *     &lt;action name="error" class="..."&gt;
 *         &lt;result&gt;error.jsp&lt;/result&gt;
 *     &lt;/action&gt;
 * &lt;/package&gt;
 * <!-- END SNIPPET: example --></pre>
 *
 * @see ActionMapper
 */
public class ServletActionRedirectResult extends ServletRedirectResult {
    public static final String DEFAULT_PARAM = "actionName";

    protected String actionName;
    protected String namespace;

    public void execute(ActionInvocation invocation) throws Exception {
        actionName = conditionalParse(actionName, invocation);
        if (namespace == null) {
            namespace = invocation.getProxy().getNamespace();
        } else {
            namespace = conditionalParse(namespace, invocation);
        }

        ActionMapper mapper = ActionMapperFactory.getMapper();
        location = mapper.getUriFromActionMapping(new ActionMapping(actionName, namespace, "", null));

        super.execute(invocation);
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
