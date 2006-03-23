/*
 *  Copyright (c) 2002-2006 by OpenSymphony
 *  All rights reserved.
 */
package com.opensymphony.webwork.components.template;

import java.util.ArrayList;
import java.util.List;

/**
 * A template.
 * <p/>
 * A template is used as a model for rendering output.
 * This object contains basic common template information
 *
 * @author plightbo
 */
public class Template implements Cloneable {
    String dir;
    String theme;
    String name;

    /**
     * Constructor.
     *
     * @param dir  base folder where the template is stored.
     * @param theme  the theme of the template
     * @param name   the name of the template.
     */
    public Template(String dir, String theme, String name) {
        this.dir = dir;
        this.theme = theme;
        this.name = name;
    }

    public String getDir() {
        return dir;
    }

    public String getTheme() {
        return theme;
    }

    public String getName() {
        return name;
    }

    public List getPossibleTemplates(TemplateEngine engine) {
        List list = new ArrayList(3);
        Template template = this;
        String parentTheme;
        list.add(template);
        while ((parentTheme = (String) engine.getThemeProps(template).get("parent")) != null) {
            try {
                template = (Template) template.clone();
                template.theme = parentTheme;
                list.add(template);
            } catch (CloneNotSupportedException e) {
                // do nothing
            }
        }

        return list;
    }

    /**
     * Constructs a string in the format <code>/dir/theme/name</code>.
     * @return a string in the format <code>/dir/theme/name</code>.
     */
    public String toString() {
        return "/" + dir + "/" + theme + "/" + name;
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
