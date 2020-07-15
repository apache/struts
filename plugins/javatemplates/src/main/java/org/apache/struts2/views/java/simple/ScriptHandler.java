package org.apache.struts2.views.java.simple;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.csp.DefaultCspSettings;
import org.apache.struts2.views.java.Attributes;
import org.apache.struts2.views.java.TagGenerator;

import java.io.IOException;
import java.util.Map;

public class ScriptHandler extends AbstractTagHandler implements TagGenerator {

    private final DefaultCspSettings settings = new DefaultCspSettings();

    @Override
    public void generate() throws IOException {
        Map<String, Object> params = context.getParameters();
        Attributes attrs = new Attributes();

        // TODO check that
        // Order of execution of the interceptor
        // Maybe first check if new nonce value was generated
        //      if yes, getNonceString
        //      if not, createNonce

//        String nonceValue = settings.getNonceString();

        String nonceValue = "foobar";

        attrs.add("nonce", nonceValue)
            .addIfExists("async", params.get("async"))
            .addIfExists("charset", params.get("charset"))
            .addIfExists("defer", params.get("defer"))
            .addIfExists("src", params.get("src"))
            .addIfExists("type", params.get("type"));

        start("script", attrs);
        String body = (String) params.get("body");
        if (StringUtils.isNotEmpty(body))
            characters(body, false); // false means no HTML encoding
        // TODO how to connect helper with tag
        end("script");
    }
}
