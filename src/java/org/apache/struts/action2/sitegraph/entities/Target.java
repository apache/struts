package com.opensymphony.webwork.sitegraph.entities;


/**
 * User: plightbo
 * Date: Jun 25, 2005
 * Time: 2:11:30 PM
 */
public class Target {
    private String target;
    private int type;

    public Target(String target, int type) {
        this.target = target;
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public int getType() {
        return type;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Target)) return false;

        final Target target1 = (Target) o;

        if (type != target1.type) return false;
        if (target != null ? !target.equals(target1.target) : target1.target != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (target != null ? target.hashCode() : 0);
        result = 29 * result + type;
        return result;
    }
}
