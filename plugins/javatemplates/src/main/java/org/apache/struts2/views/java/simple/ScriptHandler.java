package org.apache.struts2.views.java.simple;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.csp.DefaultCspSettings;
import org.apache.struts2.views.java.Attributes;
import org.apache.struts2.views.java.TagGenerator;

import java.io.IOException;
import java.util.Map;

public class ScriptHandler extends AbstractTagHandler implements TagGenerator {


    @Override
    public void generate() throws IOException {
        Map<String, Object> params = context.getParameters();
        Attributes attrs = new Attributes();

        // TODO check that
        // Order of execution of the interceptor
        // Maybe first check if new nonce value was generated
        //      if yes, getNonceString
        //      if not, createNonce

        attrs.add("nonce", (String) params.get("nonce"))
            .addIfExists("async", params.get("async"))
            .addIfExists("charset", params.get("charset"))
            .addIfExists("defer", params.get("defer"))
            .addIfExists("src", params.get("src"))
            .addIfExists("type", params.get("type"))
            .addIfExists("name", params.get("name"))
            .addIfExists("referrerpolicy", params.get("referrerpolicy"))
            .addIfExists("nomodule", params.get("nomodule"))
            .addIfExists("integrity", params.get("integrity"))
            .addIfExists("crossorigin", params.get("crossorigin"));

        start("script", attrs);
        String body = (String) params.get("body");
        if (StringUtils.isNotEmpty(body))
            characters(body, false); // false means no HTML encoding
        // TODO how to connect helper with tag
        end("script");
    }
}
