package org.apache.struts2.dispatcher.mapper;

import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.StrutsConstants;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <!-- START SNIPPET: description -->
 *
 * A prefix based action mapper that is capable of delegating to other {@link ActionMapper}s based on the request's prefix
 *
 * It is configured through struts.xml
 *
 * For example, with the following entries in struts.properties
 *
 * <pre>
 * &lt;constant name="struts.mapper.class" value="org.apache.struts2.dispatcher.mapper.PrefixBasedActionMapper"/&gt;
 * &lt;constant name="struts.mapper.prefixMapping" value="/communities:pseudoRestful,/communityTags:pseudoRestful,/events:pseudoRestful,/mediaList:pseudoRestful,/users:pseudoRestful,/community:struts,/communityTag:struts,/event:struts,/media:struts,/user:struts,:struts"/&gt;
 * </pre>
 * <p/>
 * When {@link PrefixBasedActionMapper#getMapping(HttpServletRequest, ConfigurationManager)} or
 * {@link PrefixBasedActionMapper#getUriFromActionMapping(ActionMapping)} is invoked,
 * {@link PrefixBasedActionMapper} will check each possible prefix (url prefix terminating just before a /) to find the most specific ActionMapper that returns a mapping when asked to map the request.  If none are found, null is returned for both
 * {@link PrefixBasedActionMapper#getMapping(HttpServletRequest, ConfigurationManager)} and
 * {@link PrefixBasedActionMapper#getUriFromActionMapping(ActionMapping)} methods.
 * <p/>
 *
 * <!-- END SNIPPET: description -->
 *
 * @see ActionMapper
 * @see ActionMapping
 */
public class PrefixBasedActionMapper extends DefaultActionMapper implements ActionMapper {

    private static final Logger LOG = LoggerFactory.getLogger(PrefixBasedActionMapper.class);

    protected Container container;
    protected Map<String, ActionMapper> actionMappers = new HashMap<String, ActionMapper>();

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject(StrutsConstants.PREFIX_BASED_MAPPER_CONFIGURATION)
    public void setPrefixBasedActionMappers(String list) {
        if (list != null) {
            String[] mappers = list.split(",");
            for (String mapper : mappers) {
                String[] thisMapper = mapper.split(":");
                if ((thisMapper != null) && (thisMapper.length == 2)) {
                    String mapperPrefix = thisMapper[0].trim();
                    String mapperName = thisMapper[1].trim();
                    Object obj = container.getInstance(ActionMapper.class, mapperName);
                    if (obj != null) {
                        actionMappers.put(mapperPrefix, (ActionMapper) obj);
                    } else if (LOG.isDebugEnabled()) {
                        LOG.debug("invalid PrefixBasedActionMapper config entry: [#0]", mapper);
                    }
                }
            }
        }
    }


    @SuppressWarnings("unchecked")
    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
        String uri = RequestUtils.getUri(request);
        for (int lastIndex = uri.lastIndexOf('/'); lastIndex > (-1); lastIndex = uri.lastIndexOf('/', lastIndex - 1)) {
            ActionMapper actionMapper = actionMappers.get(uri.substring(0, lastIndex));
            if (actionMapper != null) {
                ActionMapping actionMapping = actionMapper.getMapping(request, configManager);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Using ActionMapper [#0]", actionMapper.toString());
                }
                if (actionMapping != null) {
                    if (LOG.isDebugEnabled()) {
                        if (actionMapping.getParams() != null) {
                            LOG.debug("ActionMapper found mapping. Parameters: [#0]", actionMapping.getParams().toString());
                            for (Map.Entry<String, Object> mappingParameterEntry : actionMapping.getParams().entrySet()) {
                                Object paramValue = mappingParameterEntry.getValue();
                                if (paramValue == null) {
                                    LOG.debug("[#0] : null!", mappingParameterEntry.getKey());
                                } else if (paramValue instanceof String[]) {
                                    LOG.debug("[#0] : (String[]) #1", mappingParameterEntry.getKey(), Arrays.toString((String[]) paramValue));
                                } else if (paramValue instanceof String) {
                                    LOG.debug("[#0] : (String) [#1]", mappingParameterEntry.getKey(), paramValue.toString());
                                } else {
                                    LOG.debug("[#0] : (Object) [#1]", mappingParameterEntry.getKey(), paramValue.toString());
                                }
                            }
                        }
                    }
                    return actionMapping;
                } else if (LOG.isDebugEnabled()) {
                    LOG.debug("ActionMapper [#0] failed to return an ActionMapping", actionMapper.toString());
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("No ActionMapper found");
        }
        return null;
    }

    public String getUriFromActionMapping(ActionMapping mapping) {
        String namespace = mapping.getNamespace();
        for (int lastIndex = namespace.length(); lastIndex > (-1); lastIndex = namespace.lastIndexOf('/', lastIndex - 1)) {
            ActionMapper actionMapper = actionMappers.get(namespace.substring(0, lastIndex));
            if (actionMapper != null) {
                String uri = actionMapper.getUriFromActionMapping(mapping);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Using ActionMapper [#0]", actionMapper.toString());
                }
                if (uri != null) {
                    return uri;
                } else if (LOG.isDebugEnabled()) {
                    LOG.debug("ActionMapper [#0] failed to return an ActionMapping (null)", actionMapper.toString());
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("ActionMapper failed to return a uri");
        }
        return null;
    }

}
