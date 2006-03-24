package com.opensymphony.webwork.views.freemarker.tags;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateTransformModel;
import freemarker.template.SimpleSequence;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class TagModel implements TemplateTransformModel {
    private static final Log LOG = LogFactory.getLog(TagModel.class);

    protected OgnlValueStack stack;
    protected HttpServletRequest req;
    protected HttpServletResponse res;

    public TagModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        this.stack = stack;
        this.req = req;
        this.res = res;
    }

    public Writer getWriter(Writer writer, Map params) throws TemplateModelException, IOException {
        Component bean = getBean();
        Map basicParams = convertParams(params);
        bean.copyParams(basicParams);
        bean.addAllParameters(getComplexParams(params));
        return new CallbackWriter(bean, writer);
    }

    protected abstract Component getBean();

    private Map convertParams(Map params) {
        HashMap map = new HashMap(params.size());
        for (Iterator iterator = params.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object value = entry.getValue();
            if (value != null && !complexType(value)) {
                map.put(entry.getKey(), value.toString());
            }
        }
        return map;
    }

    private Map getComplexParams(Map params) {
        HashMap map = new HashMap(params.size());
        for (Iterator iterator = params.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object value = entry.getValue();
            if (value != null && complexType(value)) {
                if (value instanceof freemarker.ext.beans.BeanModel) {
                    map.put(entry.getKey(), ((freemarker.ext.beans.BeanModel) value).getWrappedObject());
                } else if (value instanceof SimpleNumber) {
                    map.put(entry.getKey(), ((SimpleNumber) value).getAsNumber());
                } else if (value instanceof SimpleSequence) {
                    try {
                        map.put(entry.getKey(), ((SimpleSequence) value).toList());
                    } catch (TemplateModelException e) {
                        LOG.error("There was a problem converting a SimpleSequence to a list", e);
                    }
                }
            }
        }
        return map;
    }

    private boolean complexType(Object value) {
        return value instanceof freemarker.ext.beans.BeanModel
                || value instanceof SimpleNumber
                || value instanceof SimpleSequence;
    }
}
