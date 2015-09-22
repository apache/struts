package org.apache.struts2;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.URL;

public class ConventionPluginResourceLoader extends DefaultResourceLoader {

    private static final Logger log = LogManager.getLogger(ConventionPluginResourceLoader.class);

    @Override
    public Resource getResource(String location) {
        if (StringUtils.startsWith(location, "/WEB-INF/")) {
            try {
                URL url = new URL("file:/" + System.getProperty("user.dir") + "/src/main/webapp" + location);
                return new UrlResource(url);
            } catch (Exception e) {
                log.error("Error occurred during get resource for location: {}", location, e);
            }
        }

        return super.getResource(location);
    }
}
