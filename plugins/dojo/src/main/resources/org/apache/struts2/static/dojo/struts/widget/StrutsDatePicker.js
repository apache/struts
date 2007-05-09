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
  
  _syncValueNode:function () {
    var date = this.datePicker.value;
    var value = "";
    switch (this.saveFormat.toLowerCase()) {
      case "rfc":
      case "iso":
      case "":
        value = dojo.date.toRfc3339(date);
        break;
      case "posix":
      case "unix":
        value = Number(date);
        break;
      default:
        if (date) {
            value = dojo.date.format(date, {datePattern:this.saveFormat, selector:"dateOnly", locale:this.lang});
        }
    }
    this.valueNode.value = value;
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