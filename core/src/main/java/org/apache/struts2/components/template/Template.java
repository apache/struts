/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.components.template;

import java.util.ArrayList;
import java.util.List;

/**
 * A template.
 * <p/>
 * A template is used as a model for rendering output.
 * This object contains basic common template information
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

    public List<Template> getPossibleTemplates(TemplateEngine engine) {
        List<Template> list = new ArrayList<Template>(3);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Template template = (Template) o;

        if (dir != null ? !dir.equals(template.dir) : template.dir != null) return false;
        if (name != null ? !name.equals(template.name) : template.name != null) return false;
        if (theme != null ? !theme.equals(template.theme) : template.theme != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dir != null ? dir.hashCode() : 0;
        result = 31 * result + (theme != null ? theme.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

}
