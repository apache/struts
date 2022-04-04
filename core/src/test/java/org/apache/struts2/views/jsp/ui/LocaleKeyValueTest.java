package org.apache.struts2.views.jsp.ui;

public enum LocaleKeyValueTest {

    ONE, TWO, THREE;
    
    public String getValueKey() {
        return getClass().getSimpleName() + "." + name();
    }

}
