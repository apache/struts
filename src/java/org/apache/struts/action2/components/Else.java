package org.apache.struts.action2.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import java.io.Writer;
import java.util.Map;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * <p>Perform basic condition flow. 'If' tag could be used by itself or with 'Else If' Tag and/or single/multiple 'Else'
 * Tag.</p>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <!-- START SNIPPET: params -->
 *
 * no params
 *
 * <!-- END SNIPPET: params -->
 *
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *  &lt;a:if test="%{false}"&gt;
 * 	    &lt;div&gt;Will Not Be Executed&lt;/div&gt;
 *  &lt;/a:if&gt;
 * 	&lt;a:elseif test="%{true}"&gt;
 * 	    &lt;div&gt;Will Be Executed&lt;/div&gt;
 *  &lt;/a:elseif&gt;
 *  &lt;a:else&gt;
 * 	    &lt;div&gt;Will Not Be Executed&lt;/div&gt;
 *  &lt;/a:else&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Rick Salsa (rsal@mb.sympatico.ca)
 * @author tmjee
 * @ww.tag name="else" bodycontent="JSP" description="Else tag"  tld-tag-class="org.apache.struts.action2.views.jsp.ElseTag"
 */
public class Else extends Component {
    public Else(OgnlValueStack stack) {
        super(stack);
    }

    public boolean start(Writer writer) {
        Map context = stack.getContext();
        Boolean ifResult = (Boolean) context.get(If.ANSWER);

        context.remove(If.ANSWER);

        return !((ifResult == null) || (ifResult.booleanValue()));

    }
}
