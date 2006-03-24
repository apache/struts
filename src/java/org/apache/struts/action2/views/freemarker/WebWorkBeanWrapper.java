package com.opensymphony.webwork.views.freemarker;

import freemarker.core.CollectionAndSequence;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.MapModel;
import freemarker.ext.util.ModelFactory;
import freemarker.template.*;

import java.util.Map;
import java.util.Set;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * The WebWorkBeanWrapper extends the default FreeMarker BeansWrapper and provides almost no change in functionality,
 * <b>except</b> for how it handles maps. Normally, FreeMarker has two modes of operation: either support for friendly
 * map built-ins (?keys, ?values, etc) but only support for String keys; OR no special built-in support (ie: ?keys
 * returns the methods on the map instead of the keys) but support for String and non-String keys alike. WebWork
 * provides an alternative implementation that gives us the best of both worlds.
 *
 * <p/> It is possible that this special behavior may be confusing or can cause problems. Therefore, you can set the
 * <b>webwork.freemarker.wrapper.altMap</b> property in webwork.properties to false, allowing the normal BeansWrapper
 * logic to take place instead.
 *
 * <!-- END SNIPPET: javadoc -->
 */
public class WebWorkBeanWrapper extends BeansWrapper {
    private static final boolean altMapWrapper
            = "true".equals(com.opensymphony.webwork.config.Configuration.get("webwork.freemarker.wrapper.altMap"));

    public TemplateModel wrap(Object object) throws TemplateModelException {
        if (object instanceof TemplateBooleanModel) {
            return super.wrap(object);
        }

        // attempt to get the best of both the SimpleMapModel and the MapModel of FM.
        if (altMapWrapper && object instanceof Map) {
            return getInstance(object, FriendlyMapModel.FACTORY);
        }

        return super.wrap(object);
    }

    /**
     * Attempting to get the best of both worlds of FM's MapModel and SimpleMapModel, by reimplementing the isEmpty(),
     * keySet() and values() methods. ?keys and ?values built-ins are thus available, just as well as plain Map
     * methods.
     */
    private final static class FriendlyMapModel extends MapModel implements TemplateHashModelEx {
        static final ModelFactory FACTORY = new ModelFactory() {
            public TemplateModel create(Object object, ObjectWrapper wrapper) {
                return new FriendlyMapModel((Map) object, (BeansWrapper) wrapper);
            }
        };

        public FriendlyMapModel(Map map, BeansWrapper wrapper) {
            super(map, wrapper);
        }

        public boolean isEmpty() {
            return ((Map) object).isEmpty();
        }

        protected Set keySet() {
            return ((Map) object).keySet();
        }

        public TemplateCollectionModel values() {
            return new CollectionAndSequence(new SimpleSequence(((Map) object).values(), wrapper));
        }
    }
}
