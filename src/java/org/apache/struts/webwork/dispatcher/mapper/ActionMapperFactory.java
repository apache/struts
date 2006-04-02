package org.apache.struts.webwork.dispatcher.mapper;

import org.apache.struts.webwork.config.Configuration;
import org.apache.struts.webwork.StrutsConstants;
import com.opensymphony.xwork.ObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Factory that creates {@link ActionMapper}s. This factory looks up the class name of the {@link ActionMapper} from
 * WebWork's configuration using the key <b>struts.mapper.class</b>.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * @author Patrick Lightbody
 */
public class ActionMapperFactory {
    protected static final Log LOG = LogFactory.getLog(ActionMapperFactory.class);

    private static final HashMap classMap = new HashMap();

    public static ActionMapper getMapper() {
        synchronized (classMap) {
            String clazz = (String) Configuration.get(StrutsConstants.STRUTS_MAPPER_CLASS);
            try {
                ActionMapper mapper = (ActionMapper) classMap.get(clazz);
                if (mapper == null) {
                    mapper = (ActionMapper) ObjectFactory.getObjectFactory().buildBean(clazz, null);
                    classMap.put(clazz, mapper);
                }

                return mapper;
            } catch (Exception e) {
                String msg = "Could not create ActionMapper: WebWork will *not* work!";
                LOG.fatal(msg, e);
                throw new RuntimeException(msg, e);
            }
        }
    }
}
