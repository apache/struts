package org.apache.struts.action2.showcase.ajax.tree;

import com.opensymphony.xwork.ActionSupport;

/**
 * @author <a href="mailto:plightbo@gmail.com">Patrick Lightbody</a>
 */
public class GetCategory extends ActionSupport {
    private long catId;
    private Category category;

    public String execute() throws Exception {
        if (catId < 1) {
            // force the root
            catId = 1;
        }

        category = Category.getById(catId);

        return SUCCESS;
    }

    public void setCatId(long catId) {
        this.catId = catId;
    }

    public Category getCategory() {
        return category;
    }
}
