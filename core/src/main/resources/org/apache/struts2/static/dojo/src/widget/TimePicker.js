/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.TimePicker");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.event.*");
dojo.require("dojo.date.serialize");
dojo.require("dojo.date.format");
dojo.require("dojo.dom");
dojo.require("dojo.html.style");

dojo.requireLocalization("dojo.i18n.calendar", "gregorian");
dojo.requireLocalization("dojo.widget", "TimePicker");

dojo.widget.defineWidget(
	"dojo.widget.TimePicker",
	dojo.widget.HtmlWidget,
	function(){
		// selected time, JS Date object
		this.time = "";
		// set following flag to true if a default time should be set
		this.useDefaultTime = false;
		// set the following to true to set default minutes to current time, false to // use zero
		this.useDefaultMinutes = false;
		// rfc 3339 date
		this.storedTime = "";
		// time currently selected in the UI, stored in hours, minutes, seconds in the format that will be actually displayed
		this.currentTime = {};
		this.classNames = {
			selectedTime: "selectedItem"
		};
		this.any = "any"; //FIXME: never used?
		// dom node indecies for selected hour, minute, amPm, and "any time option"
		this.selectedTime = {
			hour: "",
			minute: "",
			amPm: "",
			anyTime: false
		};

		// minutes are ordered as follows: ["12", "6", "1", "7", "2", "8", "3", "9", "4", "10", "5", "11"]
		this.hourIndexMap = ["", 2, 4, 6, 8, 10, 1, 3, 5, 7, 9, 11, 0];
		// minutes are ordered as follows: ["00", "30", "05", "35", "10", "40", "15", "45", "20", "50", "25", "55"]
		this.minuteIndexMap = [0, 2, 4, 6, 8, 10, 1, 3, 5, 7, 9, 11];
	},
{
	isContainer: false,
	templatePath: dojo.uri.dojoUri("src/widget/templates/TimePicker.html"),
	templateCssPath: dojo.uri.dojoUri("src/widget/templates/TimePicker.css"),

	fillInTemplate: function(args, frag){
		// Copy style info from input node to output node
		var source = this.getFragNodeRef(frag);
		dojo.html.copyStyle(this.domNode, source);

		this.initData();
		this.initUI();
	},

	postMixInProperties: function(localProperties, frag) {
		dojo.widget.TimePicker.superclass.postMixInProperties.apply(this, arguments);
		this.calendar = dojo.i18n.getLocalization("dojo.i18n.calendar", "gregorian", this.lang); // "am","pm"
		this.widgetStrings = dojo.i18n.getLocalization("dojo.widget", "TimePicker", this.lang); // "any"
	},

	initData: function() {
		// FIXME: doesn't currently validate the time before trying to set it
		// Determine the date/time from stored info, or by default don't 
		//  have a set time
		// FIXME: should normalize against whitespace on storedTime... for now 
		// just a lame hack
		if(this.storedTime.indexOf("T")!=-1 && this.storedTime.split("T")[1] && this.storedTime!=" " && this.storedTime.split("T")[1]!="any") {
			this.time = dojo.widget.TimePicker.util.fromRfcDateTime(this.storedTime, this.useDefaultMinutes, this.selectedTime.anyTime);
		} else if (this.useDefaultTime) {
			this.time = dojo.widget.TimePicker.util.fromRfcDateTime("", this.useDefaultMinutes, this.selectedTime.anyTime);
		} else {
			this.selectedTime.anyTime = true;
			this.time = dojo.widget.TimePicker.util.fromRfcDateTime("", 0, 1);
		}
	},

	initUI: function() {
		// set UI to match the currently selected time
		if(!this.selectedTime.anyTime && this.time) {
			var amPmHour = dojo.widget.TimePicker.util.toAmPmHour(this.time.getHours());
			var hour = amPmHour[0];
			var isAm = amPmHour[1];
			var minute = this.time.getMinutes();
			var minuteIndex = parseInt(minute/5);
			this.onSetSelectedHour(this.hourIndexMap[hour]);
			this.onSetSelectedMinute(this.minuteIndexMap[minuteIndex]);
			this.onSetSelectedAmPm(isAm);
		} else {
			this.onSetSelectedAnyTime();
		}
	},
	
	setTime: function(date) {
		if(date) {
			this.selectedTime.anyTime = false;
			this.setDateTime(dojo.date.toRfc3339(date));
		} else {
			this.selectedTime.anyTime = true;
		}
		this.initData();
		this.initUI();
	},

	setDateTime: function(rfcDate) {
		this.storedTime = rfcDate;
	},
	
	onClearSelectedHour: function(evt) {
		this.clearSelectedHour();
	},

	onClearSelectedMinute: function(evt) {
		this.clearSelectedMinute();
	},

	onClearSelectedAmPm: function(evt) {
		this.clearSelectedAmPm();
	},

	onClearSelectedAnyTime: function(evt) {
		this.clearSelectedAnyTime();
		if(this.selectedTime.anyTime) {
			this.selectedTime.anyTime = false;
			this.time = dojo.widget.TimePicker.util.fromRfcDateTime("", this.useDefaultMinutes);
			this.initUI();
		}
	},

	clearSelectedHour: function() {
		var hourNodes = this.hourContainerNode.getElementsByTagName("td");
		for (var i=0; i<hourNodes.length; i++) {
			dojo.html.setClass(hourNodes.item(i), "");
		}
	},

	clearSelectedMinute: function() {
		var minuteNodes = this.minuteContainerNode.getElementsByTagName("td");
		for (var i=0; i<minuteNodes.length; i++) {
			dojo.html.setClass(minuteNodes.item(i), "");
		}
	},

	clearSelectedAmPm: function() {
		var amPmNodes = this.amPmContainerNode.getElementsByTagName("td");
		for (var i=0; i<amPmNodes.length; i++) {
			dojo.html.setClass(amPmNodes.item(i), "");
		}
	},

	clearSelectedAnyTime: function() {
		dojo.html.setClass(this.anyTimeContainerNode, "anyTimeContainer");
	},

	onSetSelectedHour: function(evt) {
		this.onClearSelectedAnyTime();
		this.onClearSelectedHour();
		this.setSelectedHour(evt);
		this.onSetTime();
	},

	setSelectedHour: function(evt) {
		if(evt && evt.target) {
			if(evt.target.nodeType == dojo.dom.ELEMENT_NODE) {
				var eventTarget = evt.target;
			} else {
				var eventTarget = evt.target.parentNode;
			}
			dojo.event.browser.stopEvent(evt);
			dojo.html.setClass(eventTarget, this.classNames.selectedTime);
			this.selectedTime["hour"] = eventTarget.innerHTML;
		} else if (!isNaN(evt)) {
			var hourNodes = this.hourContainerNode.getElementsByTagName("td");
			if(hourNodes.item(evt)) {
				dojo.html.setClass(hourNodes.item(evt), this.classNames.selectedTime);
				this.selectedTime["hour"] = hourNodes.item(evt).innerHTML;
			}
		}
		this.selectedTime.anyTime = false;
	},

	onSetSelectedMinute: function(evt) {
		this.onClearSelectedAnyTime();
		this.onClearSelectedMinute();
		this.setSelectedMinute(evt);
		this.selectedTime.anyTime = false;
		this.onSetTime();
	},

	setSelectedMinute: function(evt) {
		if(evt && evt.target) {
			if(evt.target.nodeType == dojo.dom.ELEMENT_NODE) {
				var eventTarget = evt.target;
			} else {
				var eventTarget = evt.target.parentNode;
			}
			dojo.event.browser.stopEvent(evt);
			dojo.html.setClass(eventTarget, this.classNames.selectedTime);
			this.selectedTime["minute"] = eventTarget.innerHTML;
		} else if (!isNaN(evt)) {
			var minuteNodes = this.minuteContainerNode.getElementsByTagName("td");
			if(minuteNodes.item(evt)) {
				dojo.html.setClass(minuteNodes.item(evt), this.classNames.selectedTime);
				this.selectedTime["minute"] = minuteNodes.item(evt).innerHTML;
			}
		}
	},

	onSetSelectedAmPm: function(evt) {
		this.onClearSelectedAnyTime();
		this.onClearSelectedAmPm();
		this.setSelectedAmPm(evt);
		this.selectedTime.anyTime = false;
		this.onSetTime();
	},

	setSelectedAmPm: function(evt) {
		var eventTarget = evt.target;
		if(evt && eventTarget) {
			if(eventTarget.nodeType != dojo.dom.ELEMENT_NODE) {
				eventTarget = eventTarget.parentNode;
			}
			dojo.event.browser.stopEvent(evt);
			this.selectedTime.amPm = eventTarget.id;
			dojo.html.setClass(eventTarget, this.classNames.selectedTime);
		} else {
			evt = evt ? 0 : 1;
			var amPmNodes = this.amPmContainerNode.getElementsByTagName("td");
			if(amPmNodes.item(evt)) {
				this.selectedTime.amPm = amPmNodes.item(evt).id;
				dojo.html.setClass(amPmNodes.item(evt), this.classNames.selectedTime);
			}
		}
	},

	onSetSelectedAnyTime: function(evt) {
		this.onClearSelectedHour();
		this.onClearSelectedMinute();
		this.onClearSelectedAmPm();
		this.setSelectedAnyTime();
		this.onSetTime();
	},

	setSelectedAnyTime: function(evt) {
		this.selectedTime.anyTime = true;
		dojo.html.setClass(this.anyTimeContainerNode, this.classNames.selectedTime + " " + "anyTimeContainer");
	},

	onClick: function(evt) {
		dojo.event.browser.stopEvent(evt);
	},

	onSetTime: function() {
		if(this.selectedTime.anyTime) {
			this.time = new Date();
			var tempDateTime = dojo.widget.TimePicker.util.toRfcDateTime(this.time);
			this.setDateTime(tempDateTime.split("T")[0]);
		} else {
			var hour = 12;
			var minute = 0;
			var isAm = false;
			if(this.selectedTime["hour"]) {
				hour = parseInt(this.selectedTime["hour"], 10);
			}
			if(this.selectedTime["minute"]) {
				minute = parseInt(this.selectedTime["minute"], 10);
			}
			if(this.selectedTime["amPm"]) {
				isAm = (this.selectedTime["amPm"].toLowerCase() == "am");
			}
			this.time = new Date();
			this.time.setHours(dojo.widget.TimePicker.util.fromAmPmHour(hour, isAm));
			this.time.setMinutes(minute);
			this.setDateTime(dojo.widget.TimePicker.util.toRfcDateTime(this.time));
		}
	}
});

dojo.widget.TimePicker.util = new function() {
	// utility functions
	this.toRfcDateTime = function(jsDate) {
		if(!jsDate) {
			jsDate = new Date();
		}
		jsDate.setSeconds(0);
		return dojo.date.strftime(jsDate, "%Y-%m-%dT%H:%M:00%z"); //FIXME: use dojo.date.toRfc3339 instead
	}

	this.fromRfcDateTime = function(rfcDate, useDefaultMinutes, isAnyTime) {
		var tempDate = new Date();
		if(!rfcDate || rfcDate.indexOf("T")==-1) {
			if(useDefaultMinutes) {
				tempDate.setMinutes(Math.floor(tempDate.getMinutes()/5)*5);
			} else {
				tempDate.setMinutes(0);
			}
		} else {
			var tempTime = rfcDate.split("T")[1].split(":");
			// fullYear, month, date
			var tempDate = new Date();
			tempDate.setHours(tempTime[0]);
			tempDate.setMinutes(tempTime[1]);
		}
		return tempDate;
	}

	this.toAmPmHour = function(hour) {
		var amPmHour = hour;
		var isAm = true;
		if (amPmHour == 0) {
			amPmHour = 12;
		} else if (amPmHour>12) {
			amPmHour = amPmHour - 12;
			isAm = false;
		} else if (amPmHour == 12) {
			isAm = false;
		}
		return [amPmHour, isAm];
	}

	this.fromAmPmHour = function(amPmHour, isAm) {
		var hour = parseInt(amPmHour, 10);
		if(isAm && hour == 12) {
			hour = 0;
		} else if (!isAm && hour<12) {
			hour = hour + 12;
		}
		return hour;
	}
}
