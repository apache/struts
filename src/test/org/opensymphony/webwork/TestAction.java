/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2;

import org.apache.struts.action2.views.jsp.ui.User;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * @author Matt Ho <a href="mailto:matt@enginegreen.com">&lt;matt@enginegreen.com&gt;</a>
 * @version $Id: TestAction.java,v 1.15 2006/03/19 11:33:54 davsclaus Exp $
 */
public class TestAction extends ActionSupport {

    private Collection collection;
    private Collection collection2;
    private Map map;
    private String foo;
    private String result;
    private User user;
    private String[] array;
    private String[][] list;
    private List list2;
    private List list3;

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String[] getArray() {
        return array;
    }

    public void setArray(String[] array) {
        this.array = array;
    }

    public String[][] getList() {
        return list;
    }

    public void setList(String[][] list) {
        this.list = list;
    }

    public List getList2() {
        return list2;
    }

    public void setList2(List list2) {
        this.list2 = list2;
    }

    public void setList3(List list) {
    	this.list3 = list;
    }
    
    public List getList3() {
    	return this.list3;
    }
    
    public Collection getCollection2() {
    	return this.collection2;
    }
    
    public void setCollection2(Collection collection) {
    	this.collection2 = collection;
    }
    
    public String execute() throws Exception {
        if (result == null) {
            result = Action.SUCCESS;
        }

        return result;
    }

    public String doInput() throws Exception {
        return INPUT;
    }
    
}
