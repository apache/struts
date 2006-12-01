package org.apache.struts2.showcase.ajax;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;

public class AutocompleterExampleAction extends ActionSupport {
  private String select;
  private List<String> options = new ArrayList<String>();

  private static final long serialVersionUID = -8481638176160014396L;

  public String execute() throws Exception {
    if ("fruits".equals(select)) {
      options.add("apple");
      options.add("banana");
      options.add("grape");
      options.add("pear");
    } else if ("colors".equals(select)) {
      options.add("red");
      options.add("green");
      options.add("blue");
    }
    return SUCCESS;
  }

  public String getSelect() {
    return select;
  }

  public void setSelect(String select) {
    this.select = select;
  }

  public List<String> getOptions() {
    return options;
  }
}
