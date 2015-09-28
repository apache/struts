package org.apache.struts2.convention.actions.allowedmethods;

import org.apache.struts2.convention.annotation.AllowedMethods;

@AllowedMethods("end")
public class ClassLevelAllowedMethodsAction {

    public String execute() { return null; }

}
