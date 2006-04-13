/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.TimePicker");
dojo.provide("dojo.widget.TimePicker.util");
dojo.require("dojo.widget.DomWidget");

dojo.widget.TimePicker = function(){
	dojo.widget.Widget.call(this);
	this.widgetType = "TimePicker";
	this.isContainer = false;
	// the following aliases prevent breaking people using 0.2.x
	this.toRfcDateTime = dojo.widget.TimePicker.util.toRfcDateTime;
	this.fromRfcDateTime = dojo.widget.TimePicker.util.fromRfcDateTime;
	this.toAmPmHour = dojo.widget.TimePicker.util.toAmPmHour;
	this.fromAmPmHour = dojo.widget.TimePicker.util.fromAmPmHour;
}

dojo.inherits(dojo.widget.TimePicker, dojo.widget.Widget);
dojo.widget.tags.addParseTreeHandler("dojo:timepicker");

dojo.requireAfterIf("html", "dojo.widget.html.TimePicker");

dojo.widget.TimePicker.util = new function() {
	// utility functions
	this.toRfcDateTime = function(jsDate) {
		if(!jsDate) {
			jsDate = new Date();
		}
		var year = jsDate.getFullYear();
		var month = jsDate.getMonth() + 1;
		if (month < 10) {
			month = "0" + month.toString();
		}
		var date = jsDate.getDate();
		if (date < 10) {
			date = "0" + date.toString();
		}
		var hour = jsDate.getHours();
		if (hour < 10) {
			hour = "0" + hour.toString();
		}
		var minute = jsDate.getMinutes();
		if (minute < 10) {
			minute = "0" + minute.toString();
		}
		// no way to set seconds, so set to zero
		var second = "00";
		var timeZone = jsDate.getTimezoneOffset();
		var timeZoneHour = parseInt(timeZone/60);
		if(timeZoneHour > -10 && timeZoneHour < 0) {
			timeZoneHour = "-0" + Math.abs(timeZoneHour);
		} else if(timeZoneHour < 10) {
			timeZoneHour = "+0" + timeZoneHour.toString();
		} else if(timeZoneHour >= 10) {
			timeZoneHour = "+" + timeZoneHour.toString();
		}
		var timeZoneMinute = timeZone%60;
		if(timeZoneMinute < 10) {
			timeZoneMinute = "0" + timeZoneMinute.toString();
		}
		return year + "-" + month + "-" + date + "T" + hour + ":" + minute + ":" + second + timeZoneHour +":" + timeZoneMinute;
	}

	this.fromRfcDateTime = function(rfcDate, useDefaultMinutes) {
		var tempDate = new Date();
		if(!rfcDate || !rfcDate.split("T")[1]) {
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
