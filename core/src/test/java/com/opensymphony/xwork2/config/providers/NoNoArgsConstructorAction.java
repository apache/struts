package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.Action;

/**
 * Action with no public constructor taking no args.
 * <p/>
 * Used for unit test of {@link com.opensymphony.xwork2.config.providers.XmlConfigurationProvider}.
 *
 * @author Claus Ibsen
 */
public class NoNoArgsConstructorAction implements Action {

    private int foo;

    public NoNoArgsConstructorAction(int foo) {
        this.foo = foo;
    }

    public String execute() throws Exception {
        return SUCCESS;
    }

}
