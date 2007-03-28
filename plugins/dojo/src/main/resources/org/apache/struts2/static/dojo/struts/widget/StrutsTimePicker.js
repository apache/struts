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
  
  postCreate: function() {
    struts.widget.StrutsTimePicker.superclass.postCreate.apply(this, arguments);
  
    if(this.value.toLowerCase() == "today") {
      this.value = dojo.date.toRfc3339(new Date());
    }

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
  },
  
  onSetTime: function() {
    struts.widget.StrutsTimePicker.superclass.onSetTime.apply(this, arguments);
    if(this.timePicker.selectedTime.anyTime){
      this.valueNode.value = "";
    } else {
      this.valueNode.value = dojo.date.toRfc3339(this.timePicker.time);
    }
  }
});