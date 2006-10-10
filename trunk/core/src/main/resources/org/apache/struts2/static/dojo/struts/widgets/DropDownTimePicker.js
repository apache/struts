dojo.provide("struts.widgets.DropDownTimePicker");
dojo.require("struts.widgets.DropdownContainer");
dojo.require("dojo.widget.html.TimePicker");
dojo.require("struts.widgets.DateTimeUtil");

struts.widgets.DropDownTimePicker = function() {
    struts.widgets.DropdownContainer.call(this);
    this.widgetType = "DropDownTimePicker";
    var timeFormat = "#hh:#mm #TT";
    
    this.initUI = function() {
		var properties = {
			widgetContainerId: this.widgetId
		}
		
		this.subWidgetRef = dojo.widget.createWidget("TimePicker", properties,   this.subWidgetNode);
		dojo.event.connect(this.subWidgetRef, "onSetTime", this, "onPopulate");
		dojo.event.connect(this.valueInputNode, "onkeyup", this, "onInputChange");
		this.onInputChange();
    }
    
    this.onPopulate = function() {
		this.valueInputNode.value = dojo.date.toString(this.subWidgetRef.time, this.timeFormat);
	}

	this.onInputChange = function(){
	    if (this.valueInputNode.value && this.valueInputNode.value.toString().length > 0) {
		  var test = struts.widgets.DateTimeUtil.parseTime(this.valueInputNode.value, this.timeFormat);
		  // test.setTime(this.valueInputNode.value);
		  this.subWidgetRef.time = test;
		  this.subWidgetRef.setDateTime(dojo.widget.TimePicker.util.toRfcDateTime(test));
	   	  this.subWidgetRef.initUI();
		  //this.onPopulate();
	    }
	}
}

dojo.inherits(struts.widgets.DropDownTimePicker, struts.widgets.DropdownContainer);
dojo.widget.tags.addParseTreeHandler("dojo:dropdowntimepicker");
dojo.lang.extend(struts.widgets.DropDownTimePicker, {
    timeFormat: "#hh:#mm #TT",
	iconPath: "/struts/dojo/struts/widgets/timeIcon.gif",
	iconAlt: "time",
	iconTitle: "Select a time",
	inputWidth:"7em"
});
