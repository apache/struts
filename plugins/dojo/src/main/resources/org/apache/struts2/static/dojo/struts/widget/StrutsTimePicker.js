//If we use "TimePicker" for the name, Dojo get's confused and breaks
//TODO remove this file on next Dojo release

dojo.provide("struts.widget.StrutsTimePicker");

dojo.require("dojo.widget.DropdownTimePicker");

dojo.widget.defineWidget(
  "struts.widget.StrutsTimePicker",
  dojo.widget.DropdownTimePicker, {
  widgetType : "StrutsTimePicker",

  inputName: "",
  name: "",
  
  valueNotifyTopics : "",
  valueNotifyTopicsArray : null,
  
  postCreate: function() {
    struts.widget.StrutsTimePicker.superclass.postCreate.apply(this, arguments);
  
    this.inputNode.name = this.name;
    
    //set cssClass
    if(this.extraArgs["class"]) {
      dojo.html.setClass(this.inputNode, this.extraArgs["class"]);
    }  
    
    //set cssStyle
    if(this.extraArgs.style) {
      dojo.html.setStyleText(this.inputNode, this.extraArgs.style);
    }  
    
    this.valueNode.name = this.inputName;
    
    //value topics
    if(!dojo.string.isBlank(this.valueNotifyTopics)) {
      this.valueNotifyTopicsArray = this.valueNotifyTopics.split(",");
    }
  },
  
  _syncValueNode:function () {
    var time = this.timePicker.time;
    var value;
    switch (this.saveFormat.toLowerCase()) {
      case "rfc":
      case "iso":
      case "":
      //originally, Dojo only saves the time part
      value = dojo.date.toRfc3339(time);
      break;
      case "posix":
      case "unix":
      value = Number(time);
      break;
      default:
      value = dojo.date.format(time, {datePattern:this.saveFormat, selector:"timeOnly", locale:this.lang});
    }
    this.valueNode.value = value;
  },
  
  _updateText : function() {
    struts.widget.StrutsTimePicker.superclass._updateText.apply(this, arguments);
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