package org.apache.struts2.views.gxp.inject;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStackFactory;

/**
 * This class is ugly and hackish.
 */
public class InjectedObjectContainer {

  private static ValueStackFactory valueStackFactory;
  
  @Inject
  public static void setValueStackFactory(ValueStackFactory valueStackFactory) {
    InjectedObjectContainer.valueStackFactory = valueStackFactory;
  }
  
  public static ValueStackFactory getValueStackFactory() {
    return valueStackFactory;
  }
  
}
