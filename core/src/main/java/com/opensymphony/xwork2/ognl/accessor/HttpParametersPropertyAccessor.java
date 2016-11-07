/**
 *
 */
package com.opensymphony.xwork2.ognl.accessor;

import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;

import java.util.Map;

public class HttpParametersPropertyAccessor extends ObjectPropertyAccessor {

    @Override
    public Object getProperty(Map context, Object target, Object oname) throws OgnlException {
        HttpParameters parameters = (HttpParameters) target;
        return parameters.get(String.valueOf(oname)).getObject();
    }

    @Override
    public void setProperty(Map context, Object target, Object oname, Object value) throws OgnlException {
        throw new OgnlException("Access to " + target.getClass().getName() + " is read-only!");
    }
}