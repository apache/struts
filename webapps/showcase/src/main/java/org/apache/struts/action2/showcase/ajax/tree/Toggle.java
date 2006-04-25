package org.apache.struts.action2.showcase.ajax.tree;

import com.opensymphony.xwork.ActionSupport;

/**
 * @author <a href="mailto:plightbo@gmail.com">Patrick Lightbody</a>
 */
public class Toggle extends GetCategory {
    public String execute() throws Exception {
        super.execute();

        getCategory().toggle();

        return SUCCESS;
    }
}
