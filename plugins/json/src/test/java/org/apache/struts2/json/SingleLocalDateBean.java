package org.apache.struts2.json;

import java.time.LocalDate;


public class SingleLocalDateBean {
    private LocalDate localDate;
    
    public LocalDate getLocalDate() {
      return localDate;
    }
    
    public void setLocalDate(LocalDate localDate) {
      this.localDate = localDate;
    }
}
