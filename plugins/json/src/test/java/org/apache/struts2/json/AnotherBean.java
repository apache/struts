package org.apache.struts2.json;

import java.util.ArrayList;
import java.util.List;

public class AnotherBean {
    private List<Bean> beans;

    private AnotherBean yetAnotherBean;

    public List<Bean> getBeans() {
        if (this.beans == null) {
            this.beans = new ArrayList<Bean>();
        }
        return this.beans;
    }

    public void setBeans(List<Bean> beans) {
        this.beans = beans;
    }

    public AnotherBean getYetAnotherBean() {
        if(this.yetAnotherBean == null) {
            this.yetAnotherBean = new AnotherBean();
        }
        return yetAnotherBean;
    }

    public void setYetAnotherBean(AnotherBean yetAnotherBean) {
        this.yetAnotherBean = yetAnotherBean;
    }
}
