package org.apache.struts.action2.showcase.ajax.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * @author <a href="mailto:plightbo@gmail.com">Patrick Lightbody</a>
 */
public class Category {
    private static Map<Long, Category> catMap = new HashMap<Long, Category>();

    static {
        new Category(1, "Root",
                new Category(2, "Java",
                        new Category(3, "Web Frameworks",
                                new Category(4, "WebWork"),
                                new Category(5, "Struts Action"),
                                new Category(6, "Struts Shale"),
                                new Category(7, "Stripes"),
                                new Category(8, "Rife")),
                        new Category(9, "Persistence",
                                new Category(10, "iBatis"),
                                new Category(11, "Hibernate"),
                                new Category(12, "JDO"),
                                new Category(13, "JDBC"))),
                new Category(14, "JavaScript",
                        new Category(15, "Dojo"),
                        new Category(16, "Prototype"),
                        new Category(17, "Scriptaculous"),
                        new Category(18, "OpenRico"),
                        new Category(19, "DWR")));
    }

    public static Category getById(long id) {
        return catMap.get(id);
    }

    private long id;
    private String name;
    private List<Category> children;
    private boolean toggle;

    public Category(long id, String name, Category... children) {
        this.id = id;
        this.name = name;
        this.children = new ArrayList<Category>();
        for (Category child : children) {
            this.children.add(child);
        }

        catMap.put(id, this);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public void toggle() {
        toggle = !toggle;
    }

    public boolean isToggle() {
        return toggle;
    }
}
