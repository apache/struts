package org.apache.struts2.showcase.freemarker;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

import java.text.DateFormatSymbols;

/**
 * Showcase action for freemarker templates.
 */
public class StandardTagsAction extends ActionSupport implements Preparable {

    private String name;
    private String[] gender;
    private String[] months;

    public void prepare() {
        months = new DateFormatSymbols().getMonths();
        name = StandardTagsAction.class.getName().substring(StandardTagsAction.class.getName().lastIndexOf(".")+1);
        gender = new String[] { "Male", "Femal" };
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getMonths() {
        return months;
    }

    public void setMonths(String[] months) {
        this.months = months;
    }


    public String[] getGender() {
        return gender;
    }

    public void setGender(String[] gender) {
        this.gender = gender;
    }
}
