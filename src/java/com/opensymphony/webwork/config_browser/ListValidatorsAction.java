package com.opensymphony.webwork.config_browser;

import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.validator.ActionValidatorManagerFactory;
import com.opensymphony.webwork.util.ClassLoaderUtils;

import java.util.Collections;
import java.util.List;

/**
 * ListValidatorsAction loads the validations for a given class and context
 *
 * @author Jason Carreira
 * @author Rainer Hermanns
 *         Date: May 31, 2004 5:06:16 PM
 */
public class ListValidatorsAction extends ActionSupport {
    private String clazz;
    private String context;
    List validators = Collections.EMPTY_LIST;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String stripPackage(Class clazz) {
        return clazz.getName().substring(clazz.getName().lastIndexOf('.') + 1);
    }

    public String stripPackage(String clazz) {
        return clazz.substring(clazz.lastIndexOf('.') + 1);
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public List getValidators() {
        return validators;
    }

    public String execute() throws Exception {
        loadValidators();
        return super.execute();
    }

    protected void loadValidators() {
        Class value = getClassInstance();
        if ( value != null ) {
            validators = ActionValidatorManagerFactory.getInstance().getValidators(value, context);
        }
    }

    private Class getClassInstance() {
        try {
            return ClassLoaderUtils.loadClass(clazz, ActionContext.getContext().getClass());
        } catch (Exception e) {
            LOG.error("Class '" + clazz + "' not found...",e);
        }
        return null;
    }
}
