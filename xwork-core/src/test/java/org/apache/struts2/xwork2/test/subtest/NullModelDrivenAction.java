package org.apache.struts2.xwork2.test.subtest;

import org.apache.struts2.xwork2.ModelDrivenAction;

/**
 * Extends ModelDrivenAction to return a null model.
 *
 * @author Mark Woon
 */
public class NullModelDrivenAction extends ModelDrivenAction {

    /**
     * @return the model to be pushed onto the ValueStack instead of the Action itself
     */
    @Override
    public Object getModel() {
        return null;
    }
}
