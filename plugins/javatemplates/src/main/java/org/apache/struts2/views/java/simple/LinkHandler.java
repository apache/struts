package org.apache.struts2.views.java.simple;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.csp.DefaultCspSettings;
import org.apache.struts2.views.java.Attributes;
import org.apache.struts2.views.java.TagGenerator;

import java.io.IOException;
import java.util.Map;

public class LinkHandler extends AbstractTagHandler implements TagGenerator {

    @Override
    public void generate() throws IOException {
        Map<String, Object> params = context.getParameters();
        Attributes attrs = new Attributes();

        attrs.add("nonce", (String) params.get("nonce"))
                .addIfExists("hreflang", params.get("hreflang"))
                .addIfExists("rel", params.get("rel"))
                .addIfExists("media", params.get("media"))
                .addIfExists("sizes", params.get("sizes"))
                .addIfExists("crossorigin", params.get("crossorigin"))
                .addIfExists("referrerpolicy", params.get("referrerpolicy"))
                .addIfExists("type", params.get("type"))
                .addIfExists("as", params.get("as"))
                .addIfExists("disabled", params.get("disabled"))
                .addIfExists("title", params.get("title"));

        start("link", attrs);
        end("link");
    }
}
