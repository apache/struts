package org.apache.struts2.views.java.simple;

import org.apache.struts2.views.java.Attributes;
import org.apache.struts2.views.java.TagGenerator;
import org.apache.commons.lang.xwork.StringUtils;
import org.apache.commons.lang.xwork.StringEscapeUtils;

import java.io.IOException;
import java.util.Map;

public class CheckboxHandler extends AbstractTagHandler implements TagGenerator {
    public void generate() throws IOException {
        Map<String, Object> params = context.getParameters();
        Attributes attrs = new Attributes();

        String fieldValue = (String) params.get("fieldValue");
        String id = (String) params.get("id");
        String name = (String) params.get("name");
        Object disabled = params.get("disabled");

        attrs.add("type", "checkbox")
                .add("name", name)
                .add("value", fieldValue)
                .addIfTrue("checked", params.get("nameValue"))
                .addIfTrue("readonly", params.get("readonly"))
                .addIfTrue("disabled", disabled)
                .addIfExists("tabindex", params.get("tabindex"))
                .addIfExists("id", id)
                .addIfExists("class", params.get("cssClass"))
                .addIfExists("style", params.get("cssStyle"))
                .addIfExists("title", params.get("title"));
        start("input", attrs);
        end("input");

        //hidden input
        attrs = new Attributes();
        attrs.add("type", "hidden")
                .add("id", "__checkbox_" + StringUtils.defaultString(StringEscapeUtils.escapeHtml(id)))
                .add("name", "__checkbox_" + StringUtils.defaultString(StringEscapeUtils.escapeHtml(name)))
                .add("value", "__checkbox_" + StringUtils.defaultString(StringEscapeUtils.escapeHtml(fieldValue)))
                .addIfTrue("disabled", disabled);
        start("input", attrs);
        end("input");
    }
}
