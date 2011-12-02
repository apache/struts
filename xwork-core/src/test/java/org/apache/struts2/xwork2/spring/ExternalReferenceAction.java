/*
 * Created on Nov 11, 2003
 */
package org.apache.struts2.xwork2.spring;

import org.apache.struts2.xwork2.Action;

/**
 * @author Mike
 */
public class ExternalReferenceAction implements Action
{
    private Foo foo;
    private Bar bar;

    public String execute() throws Exception {
        return SUCCESS;
    }

    /**
     * @return Returns the foo.
     */
    public Foo getFoo() {
        return foo;
    }

    /**
     * @param foo
     *            The foo to set.
     */
    public void setFoo(Foo foo) {
        this.foo = foo;
    }

    /**
     * @return Returns the bar.
     */
    public Bar getBar() {
        return bar;
    }

    /**
     * @param bar
     *            The bar to set.
     */
    public void setBar(Bar bar) {
        this.bar = bar;
    }
}
