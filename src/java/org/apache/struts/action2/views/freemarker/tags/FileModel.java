package com.opensymphony.webwork.views.freemarker.tags;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.File;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see File
 */
public class FileModel extends TagModel {
    public FileModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new File(stack, req, res);
    }
}
