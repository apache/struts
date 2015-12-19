package org.apache.struts2.tiles;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.util.WildcardUtil;
import com.opensymphony.xwork2.util.finder.ResourceFinder;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.tiles.servlet.context.ServletTilesApplicationContext;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class StrutsWildcardServletTilesApplicationContext extends ServletTilesApplicationContext {

    private static final Logger LOG = LoggerFactory.getLogger(StrutsWildcardServletTilesApplicationContext.class);

    private ResourceFinder finder;

    public StrutsWildcardServletTilesApplicationContext(ServletContext context) {
        super(context);

        Set<URL> urls = new HashSet<URL>();
        for (Object path : context.getResourcePaths("/")) {
            try {
                URL url = new File(context.getRealPath(String.valueOf(path))).toURI().toURL();
                urls.add(url);
            } catch (MalformedURLException e) {
                throw new ConfigurationException(e);
            }
        }

        finder = new ResourceFinder(urls.toArray(new URL[urls.size()]));
    }

    public Set<URL> getResources(String path) throws IOException {
        Set<URL> resources = new HashSet<URL>();

        if (path.startsWith("/")) {
            LOG.trace("Using ServletContext to load resource #0", path);
            URL resource = getResource(path);
            if (resource != null) {
                resources.add(resource);
            }
        }
        resources.addAll(findResources(path));

        return resources;
    }

    protected Set<URL> findResources(String path) throws IOException {
        Set<URL> resources = new HashSet<URL>();

        LOG.trace("Using ResourceFinder to find matches for #0", path);

        Pattern pattern = WildcardUtil.compileWildcardPattern(path);
        Map<String, URL> matches = finder.getResourcesMap("");

        for (String resource : matches.keySet()) {
            if (pattern.matcher(resource).matches()) {
                resources.add(matches.get(resource));
            }
        }

        LOG.trace("Found resources #0 for path #1", resources, path);
        return resources;
    }

}
