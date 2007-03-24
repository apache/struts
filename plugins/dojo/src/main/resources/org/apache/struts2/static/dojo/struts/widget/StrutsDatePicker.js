dojo.provide("struts.widget.StrutsDatePicker");

dojo.require("dojo.widget.DropdownDatePicker");

dojo.widget.defineWidget(
  "struts.widget.StrutsDatePicker",
  dojo.widget.DropdownDatePicker, {
  widgetType : "StrutsDatePicker",

  postCreate: function() {
    struts.widget.StrutsDatePicker.superclass.postCreate.apply(this, arguments);
    
    //set cssClass
    if(this.extraArgs.class) {
      dojo.html.setClass(this.inputNode, this.extraArgs.class);
    }  
    
    //set cssStyle
    if(this.extraArgs.style) {
      dojo.html.setStyleText(this.inputNode, this.extraArgs.style);
    }  
  },
});