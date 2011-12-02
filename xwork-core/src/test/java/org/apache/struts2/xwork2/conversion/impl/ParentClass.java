package org.apache.struts2.xwork2.conversion.impl;

/**
 * <code>ParentClass</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id: ParentClass.java 1209415 2011-12-02 11:24:48Z lukaszlenart $
 */
public class ParentClass {

    public enum NestedEnum {
        TEST,
        TEST2,
        TEST3
    }


    private NestedEnum value;

    public void setValue(NestedEnum value) {
        this.value = value;
    }

    public NestedEnum getValue() {
        return value;
    }
}
