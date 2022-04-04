package org.apache.struts2.convention.actions.result;

import org.apache.struts2.convention.annotation.ParentPackage;

/**
 * Used to test that &lt;global-results&gt; in struts.xml are respected.
 *
 * @author Mark Woon
 */
@ParentPackage("class-level")
public class GlobalResultAction {

    public String execute() {
        return "error";
    }
}
