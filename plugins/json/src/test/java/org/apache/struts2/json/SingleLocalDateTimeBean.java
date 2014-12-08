package org.apache.struts2.json;

import java.time.LocalDateTime;


public class SingleLocalDateTimeBean {
    private LocalDateTime localDateTime;
    
    public LocalDateTime getLocalDateTime() {
      return localDateTime;
    }
    
    public void setLocalDate(LocalDateTime localDateTime) {
      this.localDateTime = localDateTime;
    }
}
