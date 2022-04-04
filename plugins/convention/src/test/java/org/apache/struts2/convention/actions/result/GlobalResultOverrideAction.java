package org.apache.struts2.convention.actions.result;

import org.apache.struts2.convention.annotation.ParentPackage;

/**
 * Used to test that &lt;global-results&gt; in struts.xml are are overridden when a matching result location can be
 * found.  For example, action-error.jsp overrides a global "error" result.
 *
 * @author Mark Woon
 */
@ParentPackage("class-level")
public class GlobalResultOverrideAction {

    public String execute() {
        return "error";
    }
}
