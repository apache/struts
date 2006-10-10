package org.apache.struts2.showcase;

import org.apache.struts2.showcase.ajax.tree.Category;

import com.opensymphony.xwork2.ActionSupport;

// START SNIPPET: treeExampleDynamicJavaShow 

public class ShowDynamicTreeAction extends ActionSupport {
	
	public Category getTreeRootNode() {
		return Category.getById(1);
	}
}

// END SNIPPET: treeExampleDynamicJavaShow

