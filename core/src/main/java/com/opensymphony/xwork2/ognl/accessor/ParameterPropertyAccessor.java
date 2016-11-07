/**
 *
 */
package com.opensymphony.xwork2.ognl.accessor;

import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;
import org.apache.struts2.dispatcher.Parameter;

import java.util.Map;

public class ParameterPropertyAccessor extends ObjectPropertyAccessor {

    @Override
    public Object getProperty(Map context, Object target, Object oname) throws OgnlException {
        if (target instanceof Parameter) {
            if ("value".equalsIgnoreCase(String.valueOf(oname))) {
                throw new OgnlException("Access to " + oname + " is not allowed! Call parameter name directly!");
            }
            return ((Parameter) target).getObject();
        }
        return super.getProperty(context, target, oname);
    }

    @Override
    public void setProperty(Map context, Object target, Object oname, Object value) throws OgnlException {
        if (target instanceof Parameter) {
            throw new OgnlException("Access to " + target.getClass().getName() + " is read-only!");
        } else {
            super.setProperty(context, target, oname, value);
        }
    }
}