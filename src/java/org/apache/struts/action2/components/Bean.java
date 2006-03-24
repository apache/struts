package org.apache.struts.action2.components;

import com.opensymphony.util.ClassLoaderUtil;
import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.util.OgnlUtil;
import com.opensymphony.xwork.util.OgnlValueStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Writer;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>Instantiates a class that conforms to the JavaBeans specification. This tag has a body which can contain
 * a number of {@link Param} elements to set any mutator methods on that class.</p>
 * <p/>
 * <p>If the id attribute is set on the BeanTag, it will place the instantiated bean into the
 * stack's Context.</p>
 * <p/>
 * <!-- END SNIPPET: javadoc -->
 *
 *
 * <!-- START SNIPPET: params -->
 * <ul>
 * 		<li>id - the stack's context id (if supplied) that the created bean will be store under</li>
 * 		<li>name* - the class name of the bean to be instantiated (must respect JavaBean specification)</li>
 * </ul>
 * <!-- END SNIPPET: params -->
 *
 *
 * <p>Examples:</p>
 * <p/>
 * <pre>
 * <!-- START SNIPPET: examples -->
 * &lt;-- in freemarker form --&gt;
 * [ww.bean name="org.apache.struts.action2.example.counter.SimpleCounter" id="counter"]
 *   [ww:param name="foo" value="BAR"/]
 *   The value of foo is : [ww:property value="foo"/], when inside the bean tag.<br />
 * [/ww:bean]
 *
 * &lt;-- in jsp form --&gt;
 * &lt;ww:bean name="org.apache.struts.action2.example.counter.SimpleCounter" id="counter"&gt;
 * 	 &lt;ww:param name="foo" value="BAR" /&gt;
 *   The value of foot is : &lt;ww:property value="foo"/&gt;, when inside the bean tag &lt;br /&gt;
 * &lt;/ww:bean&gt;
 * <!-- END SNIPPET: examples -->
 * </pre>
 * <p/>
 *
 * <!-- START SNIPPET: examplesdescription -->
 * <p>This example instantiates a bean called SimpleCounter and sets the foo property (setFoo('BAR')). The
 * SimpleCounter object is then pushed onto the Valuestack, which means that we can called its accessor methods (getFoo())
 * with the Property tag and get their values.</p>
 * <p/>
 * <p>In the above example, the id has been set to a value of <i>counter</i>. This means that the SimpleCounter class
 * will be placed into the stack's context. You can access the SimpleCounter class using WW's tag:</p>
 * <p/>
 * <pre>
 * &lt;-- jsp form --&gt;
 * &lt;ww:property value="#counter" /&gt;
 *
 * &lt;-- freemarker form --&gt;
 * [ww:property value="#counter.foo"/]
 * </pre>
 * <p/>
 * <p>In the property tag example, the <i>#</i> tells Ognl to search the context for the SimpleCounter class which has
 * an id(key) of <i>counter</i></p>
 * <!-- END SNIPPET: examplesdescription -->
 *
 * @author $author$
 * @author Rick Salsa (rsal@mb.sympatico.ca)
 * @author Brock Bulger
 * @author Rene Gielen
 * @version $Revision: 1.14 $
 * @since 2.2
 *
 * @see Param
 *
 * @ww.tag name="bean" tld-body-content="JSP" tld-tag-class="org.apache.struts.action2.views.jsp.BeanTag"
 * description="Instantiate a JavaBean and place it in the context."
 */
public class Bean extends Component {
    protected static Log log = LogFactory.getLog(Bean.class);

    protected Object bean;
    protected String name;

    public Bean(OgnlValueStack stack) {
        super(stack);
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);

        OgnlValueStack stack = getStack();

        try {
            String beanName = findString(name, "name", "Bean name is required. Example: com.acme.FooBean");
            bean = ObjectFactory.getObjectFactory().buildBean(ClassLoaderUtil.loadClass(beanName, getClass()), stack.getContext());
        } catch (Exception e) {
            log.error("Could not instantiate bean", e);

            return false;
        }

        // push bean on stack
        stack.push(bean);

        // store for reference later
        if (getId() != null) {
            getStack().getContext().put(getId(), bean);
        }

        return result;
    }

    public boolean end(Writer writer, String body) {
        OgnlValueStack stack = getStack();
        stack.pop();

        return super.end(writer, body);
    }

    public void addParameter(String key, Object value) {
        OgnlUtil.setProperty(key, value, bean, getStack().getContext());
    }

    /**
     * the class name of the bean to be instantiated (must respect JavaBean specification)
     * @ww.tagattribute required="true" type="String"
     */
    public void setName(String name) {
        this.name = name;
    }
}
