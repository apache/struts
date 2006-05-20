package org.apache.struts.action2.showcase;

import org.apache.struts.action2.showcase.ajax.tree.Category;

import com.opensymphony.xwork.ActionSupport;


public class ShowDynamicTreeAction extends ActionSupport {
	
	public Category getTreeRootNode() {
		return Category.getById(1);
	}
}

