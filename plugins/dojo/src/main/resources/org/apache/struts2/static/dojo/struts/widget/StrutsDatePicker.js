dojo.provide("struts.widget.StrutsDatePicker");

dojo.require("dojo.widget.DropdownDatePicker");
dojo.widget.defineWidget(
  "struts.widget.StrutsDatePicker",
  dojo.widget.DropdownDatePicker, {
  widgetType : "StrutsDatePicker",

  valueNotifyTopics : "",
  valueNotifyTopicsArray : null,
  
  postCreate: function() {
    struts.widget.StrutsDatePicker.superclass.postCreate.apply(this, arguments);
    
    //set cssClass
    if(this.extraArgs["class"]) {
      dojo.html.setClass(this.inputNode, this.extraArgs["class"]);
    }  
    
    //set cssStyle
    if(this.extraArgs.style) {
      dojo.html.setStyleText(this.inputNode, this.extraArgs.style);
    }  
    
    //value topics
    if(!dojo.string.isBlank(this.valueNotifyTopics)) {
      this.valueNotifyTopicsArray = this.valueNotifyTopics.split(",");
    }
  },
  
  _updateText : function() {
    struts.widget.StrutsDatePicker.superclass._updateText.apply(this, arguments);
    if(this.valueNotifyTopicsArray != null) {
      for(var i = 0; i < this.valueNotifyTopicsArray.length; i++) {
        var topic = this.valueNotifyTopicsArray[i];
        if(!dojo.string.isBlank(topic)) {
          try {
            dojo.event.topic.publish(topic, this.inputNode.value);
          } catch(ex) {
            dojo.debug(ex);
          }
        }
      }
    }
  }
});