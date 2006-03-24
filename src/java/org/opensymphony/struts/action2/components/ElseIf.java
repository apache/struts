package com.opensymphony.webwork.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import java.io.Writer;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * <p>Perform basic condition flow. 'If' tag could be used by itself or with 'Else If' Tag and/or single/multiple 'Else'
 * Tag.</p>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *
 * <li>test* (Boolean) - Logic to determined if body of tag is to be displayed</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *  &lt;ww:if test="%{false}"&gt;
 * 	    &lt;div&gt;Will Not Be Executed&lt;/div&gt;
 *  &lt;/ww:if&gt;
 * 	&lt;ww:elseif test="%{true}"&gt;
 * 	    &lt;div&gt;Will Be Executed&lt;/div&gt;
 *  &lt;/ww:elseif&gt;
 *  &lt;ww:else&gt;
 * 	    &lt;div&gt;Will Not Be Executed&lt;/div&gt;
 *  &lt;/ww:else&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Rick Salsa (rsal@mb.sympatico.ca)
 * @author tmjee
 * @ww.tag name="elseif" tld-body-content="JSP" description="Elseif tag"  tld-tag-class="com.opensymphony.webwork.views.jsp.ElseIfTag"
 */
public class ElseIf extends Component {
    public ElseIf(OgnlValueStack stack) {
        super(stack);
    }

    protected Boolean answer;
    protected String test;

    public boolean start(Writer writer) {
        Boolean ifResult = (Boolean) stack.getContext().get(If.ANSWER);

        if ((ifResult == null) || (ifResult.booleanValue())) {
            return false;
        }

        //make the comparision
        answer = (Boolean) findValue(test, Boolean.class);

        return answer != null && answer.booleanValue();
    }

    public boolean end(Writer writer, String body) {
        if (answer == null) {
            answer = Boolean.FALSE;
        }

        if (answer.booleanValue()) {
            stack.getContext().put(If.ANSWER, answer);
        }

        return super.end(writer, "");
    }

    /**
     * Expression to determine if body of tag is to be displayed
     * @ww.tagattribute required="true" type="Boolean"
     */
    public void setTest(String test) {
        this.test = test;
    }
}
