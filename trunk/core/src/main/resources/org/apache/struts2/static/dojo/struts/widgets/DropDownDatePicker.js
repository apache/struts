dojo.provide("struts.widgets.DropDownDatePicker");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.DatePicker");
dojo.require("dojo.event.*");
dojo.require("dojo.html");
dojo.require("struts.widgets.DropdownContainer");
dojo.require("struts.widgets.DateTimeUtil");

struts.widgets.DropDownDatePicker = function () {
    struts.widgets.DropdownContainer.call(this);
    this.widgetType = "DropDownDatePicker";
    
    this.initUI = function() {
		var properties = {
			widgetContainerId: this.widgetId
		}

		this.subWidgetRef = dojo.widget.createWidget("DatePicker", properties,   this.subWidgetNode);
		dojo.event.connect(this.subWidgetRef, "onSetDate", this, "onPopulate");
		dojo.event.connect(this.valueInputNode, "onkeyup", this, "onInputChange");
		this.onUpdateDate = function(evt) {
			this.storedDate = evt.storedDate;
		}
		this.onInputChange();
	}
	
	this.onPopulate = function() {
		this.valueInputNode.value = dojo.date.toString(this.subWidgetRef.date, this.dateFormat);
	}

	this.onInputChange = function(){
		//var test = new Date(this.valueInputNode.value);
		var test = struts.widgets.DateTimeUtil.parseDate(this.valueInputNode.value, this.dateFormat);
		this.subWidgetRef.date = test;
		this.subWidgetRef.setDate(dojo.widget.DatePicker.util.toRfcDate(test));
		this.subWidgetRef.initUI();
		//this.onPopulate();
	}
}

dojo.inherits(struts.widgets.DropDownDatePicker, struts.widgets.DropdownContainer);
dojo.widget.tags.addParseTreeHandler("dojo:dropdowndatepicker");
dojo.lang.extend(struts.widgets.DropDownDatePicker, {
	
	//	default attributes
	dateFormat: "#MM/#dd/#yyyy",
	iconPath: "/struts/dojo/struts/widgets/dateIcon.gif",
	iconAlt: "date",
	iconTitle: "Select a date",
	inputWidth:"7em"
	
});

