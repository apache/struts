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
dojo.requireLocalization("dojo.i18n.calendar", "gregorian", null, "ko,zh-cn,zh,sv,ja,en,zh-tw,it,hu,nl,fi,zh-hk,fr,pt,ROOT,es,de,pt-br");
dojo.requireLocalization("dojo.widget", "TimePicker", null, "ROOT");
dojo.widget.defineWidget("dojo.widget.TimePicker", dojo.widget.HtmlWidget, function () {
	this.time = "";
	this.useDefaultTime = false;
	this.useDefaultMinutes = false;
	this.storedTime = "";
	this.currentTime = {};
	this.classNames = {selectedTime:"selectedItem"};
	this.any = "any";
	this.selectedTime = {hour:"", minute:"", amPm:"", anyTime:false};
	this.hourIndexMap = ["", 2, 4, 6, 8, 10, 1, 3, 5, 7, 9, 11, 0];
	this.minuteIndexMap = [0, 2, 4, 6, 8, 10, 1, 3, 5, 7, 9, 11];
}, {isContainer:false, templateString:"<div class=\"timePickerContainer\" dojoAttachPoint=\"timePickerContainerNode\">\n\t<table class=\"timeContainer\" cellspacing=\"0\" >\n\t\t<thead>\n\t\t\t<tr>\n\t\t\t\t<td class=\"timeCorner cornerTopLeft\" valign=\"top\">&nbsp;</td>\n\t\t\t\t<td class=\"timeLabelContainer hourSelector\">${this.calendar.field-hour}</td>\n\t\t\t\t<td class=\"timeLabelContainer minutesHeading\">${this.calendar.field-minute}</td>\n\t\t\t\t<td class=\"timeCorner cornerTopRight\" valign=\"top\">&nbsp;</td>\n\t\t\t</tr>\n\t\t</thead>\n\t\t<tbody>\n\t\t\t<tr>\n\t\t\t\t<td valign=\"top\" colspan=\"2\" class=\"hours\">\n\t\t\t\t\t<table align=\"center\">\n\t\t\t\t\t\t<tbody dojoAttachPoint=\"hourContainerNode\"  \n\t\t\t\t\t\t\tdojoAttachEvent=\"onClick: onSetSelectedHour;\">\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>12</td>\n\t\t\t\t\t\t\t\t<td>6</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>1</td>\n\t\t\t\t\t\t\t\t<td>7</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>2</td>\n\t\t\t\t\t\t\t\t<td>8</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>3</td>\n\t\t\t\t\t\t\t\t<td>9</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>4</td>\n\t\t\t\t\t\t\t\t<td>10</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>5</td>\n\t\t\t\t\t\t\t\t<td>11</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t</tbody>\n\t\t\t\t\t</table>\n\t\t\t\t</td>\n\t\t\t\t<td valign=\"top\" class=\"minutes\" colspan=\"2\">\n\t\t\t\t\t<table align=\"center\">\n\t\t\t\t\t\t<tbody dojoAttachPoint=\"minuteContainerNode\" \n\t\t\t\t\t\t\tdojoAttachEvent=\"onClick: onSetSelectedMinute;\">\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>00</td>\n\t\t\t\t\t\t\t\t<td>30</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>05</td>\n\t\t\t\t\t\t\t\t<td>35</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>10</td>\n\t\t\t\t\t\t\t\t<td>40</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>15</td>\n\t\t\t\t\t\t\t\t<td>45</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>20</td>\n\t\t\t\t\t\t\t\t<td>50</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>25</td>\n\t\t\t\t\t\t\t\t<td>55</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t</tbody>\n\t\t\t\t\t</table>\n\t\t\t\t</td>\n\t\t\t</tr>\n\t\t\t<tr>\n\t\t\t\t<td class=\"cornerBottomLeft\">&nbsp;</td>\n\t\t\t\t<td valign=\"top\" class=\"timeOptions\">\n\t\t\t\t\t<table class=\"amPmContainer\">\n\t\t\t\t\t\t<tbody dojoAttachPoint=\"amPmContainerNode\" \n\t\t\t\t\t\t\tdojoAttachEvent=\"onClick: onSetSelectedAmPm;\">\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td id=\"am\">${this.calendar.am}</td>\n\t\t\t\t\t\t\t\t<td id=\"pm\">${this.calendar.pm}</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t</tbody>\n\t\t\t\t\t</table>\n\t\t\t\t</td>\n\t\t\t\t<td class=\"timeOptions\">\n\t\t\t\t\t<div dojoAttachPoint=\"anyTimeContainerNode\" \n\t\t\t\t\t\tdojoAttachEvent=\"onClick: onSetSelectedAnyTime;\" \n\t\t\t\t\t\tclass=\"anyTimeContainer\">${this.widgetStrings.any}</div>\n\t\t\t\t</td>\n\t\t\t\t<td class=\"cornerBottomRight\">&nbsp;</td>\n\t\t\t</tr>\n\t\t</tbody>\n\t</table>\n</div>\n", templateCssString:"/*Time Picker */\n.timePickerContainer {\n\twidth:122px;\n\tfont-family:Tahoma, Myriad, Helvetica, Arial, Verdana, sans-serif;\n\tfont-size:16px;\n}\n\n.timeContainer {\n\tborder-collapse:collapse;\n\tborder-spacing:0;\n}\n\n.timeContainer thead {\n\tcolor:#293a4b;\n\tfont-size:0.9em;\n\tfont-weight:700;\n}\n\n.timeContainer thead td {\n\tpadding:0.25em;\n\tfont-size:0.80em;\n\tborder-bottom:1px solid #6782A8;\n}\n\n.timeCorner {\n\twidth:10px;\n}\n\n.cornerTopLeft {\n\tbackground: url(\"images/dpCurveTL.png\") top left no-repeat;\n}\n\n.cornerTopRight {\n\tbackground: url(\"images/dpCurveTR.png\") top right no-repeat;\n}\n\n.timeLabelContainer {\n\tbackground: url(\"images/dpMonthBg.png\") top left repeat-x;\n}\n\n.hours, .minutes, .timeBorder {\n\tbackground: #7591bc url(\"images/dpBg.gif\") top left repeat-x;\n\n}\n\n.hours td, .minutes td {\n\tpadding:0.2em;\n\ttext-align:center;\n\tfont-size:0.7em;\n\tfont-weight:bold;\n\tcursor:pointer;\n\tcursor:hand;\n\tcolor:#fff;\n}\n\n.minutes {\n\tborder-left:1px solid #f5d1db;\n}\n\n.hours {\n\tborder-right:1px solid #6782A8;\n}\n\n.hourSelector {\n\tborder-right:1px solid #6782A8;\n\tpadding:5px;\n\tpadding-right:10px;\n}\n\n.minutesSelector {\n\tpadding:5px;\n\tborder-left:1px solid #f5c7d4;\n\ttext-align:center;\n}\n\n.minutesHeading {\n\tpadding-left:9px !important;\n}\n\n.timeOptions {\n\tbackground-color:#F9C9D7;\n}\n\n.timeContainer .cornerBottomLeft, .timeContainer .cornerBottomRight, .timeContainer .timeOptions {\n\tborder-top:1px solid #6782A8;\n}\n\n.timeContainer .cornerBottomLeft {\n\tbackground: url(\"images/dpCurveBL.png\") bottom left no-repeat !important;\n\twidth:9px !important;\n\tpadding:0;\n\tmargin:0;\n}\n\n.timeContainer .cornerBottomRight {\n\tbackground: url(\"images/dpCurveBR.png\") bottom right no-repeat !important;\n\twidth:9px !important;\n\tpadding:0;\n\tmargin:0;\n}\n\n.timeOptions {\n\tcolor:#fff;\n\tbackground:url(\"images/dpYearBg.png\") top left repeat-x;\n\n}\n\n.selectedItem {\n\tbackground-color:#fff;\n\tcolor:#6782a8 !important;\n}\n\n.timeOptions .selectedItem {\n\tcolor:#fff !important;\n\tbackground-color:#9ec3fb !important;\n}\n\n.anyTimeContainer {\n\ttext-align:center;\n\tfont-weight:bold;\n\tfont-size:0.7em;\n\tpadding:0.1em;\n\tcursor:pointer;\n\tcursor:hand;\n\tcolor:#fff !important;\n}\n\n.amPmContainer {\n\twidth:100%;\n}\n\n.amPmContainer td {\n\ttext-align:center;\n\tfont-size:0.7em;\n\tfont-weight:bold;\n\tcursor:pointer;\n\tcursor:hand;\n\tcolor:#fff;\n}\n\n\n\n/*.timePickerContainer {\n\tmargin:1.75em 0 0.5em 0;\n\twidth:10em;\n\tfloat:left;\n}\n\n.timeContainer {\n\tborder-collapse:collapse;\n\tborder-spacing:0;\n}\n\n.timeContainer thead td{\n\tborder-bottom:1px solid #e6e6e6;\n\tpadding:0 0.4em 0.2em 0.4em;\n}\n\n.timeContainer td {\n\tfont-size:0.9em;\n\tpadding:0 0.25em 0 0.25em;\n\ttext-align:left;\n\tcursor:pointer;cursor:hand;\n}\n\n.timeContainer td.minutesHeading {\n\tborder-left:1px solid #e6e6e6;\n\tborder-right:1px solid #e6e6e6;\t\n}\n\n.timeContainer .minutes {\n\tborder-left:1px solid #e6e6e6;\n\tborder-right:1px solid #e6e6e6;\n}\n\n.selectedItem {\n\tbackground-color:#3a3a3a;\n\tcolor:#ffffff;\n}*/\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/TimePicker.css"), postMixInProperties:function (localProperties, frag) {
	dojo.widget.TimePicker.superclass.postMixInProperties.apply(this, arguments);
	this.calendar = dojo.i18n.getLocalization("dojo.i18n.calendar", "gregorian", this.lang);
	this.widgetStrings = dojo.i18n.getLocalization("dojo.widget", "TimePicker", this.lang);
}, fillInTemplate:function (args, frag) {
	var source = this.getFragNodeRef(frag);
	dojo.html.copyStyle(this.domNode, source);
	if (args.value) {
		if (args.value instanceof Date) {
			this.storedTime = dojo.date.toRfc3339(args.value);
		} else {
			this.storedTime = args.value;
		}
	}
	this.initData();
	this.initUI();
}, initData:function () {
	if (this.storedTime.indexOf("T") != -1 && this.storedTime.split("T")[1] && this.storedTime != " " && this.storedTime.split("T")[1] != "any") {
		this.time = dojo.widget.TimePicker.util.fromRfcDateTime(this.storedTime, this.useDefaultMinutes, this.selectedTime.anyTime);
	} else {
		if (this.useDefaultTime) {
			this.time = dojo.widget.TimePicker.util.fromRfcDateTime("", this.useDefaultMinutes, this.selectedTime.anyTime);
		} else {
			this.selectedTime.anyTime = true;
			this.time = dojo.widget.TimePicker.util.fromRfcDateTime("", 0, 1);
		}
	}
}, initUI:function () {
	if (!this.selectedTime.anyTime && this.time) {
		var amPmHour = dojo.widget.TimePicker.util.toAmPmHour(this.time.getHours());
		var hour = amPmHour[0];
		var isAm = amPmHour[1];
		var minute = this.time.getMinutes();
		var minuteIndex = parseInt(minute / 5);
		this.onSetSelectedHour(this.hourIndexMap[hour]);
		this.onSetSelectedMinute(this.minuteIndexMap[minuteIndex]);
		this.onSetSelectedAmPm(isAm);
	} else {
		this.onSetSelectedAnyTime();
	}
}, setTime:function (date) {
	if (date) {
		this.selectedTime.anyTime = false;
		this.setDateTime(dojo.date.toRfc3339(date));
	} else {
		this.selectedTime.anyTime = true;
	}
	this.initData();
	this.initUI();
}, setDateTime:function (rfcDate) {
	this.storedTime = rfcDate;
}, onClearSelectedHour:function (evt) {
	this.clearSelectedHour();
}, onClearSelectedMinute:function (evt) {
	this.clearSelectedMinute();
}, onClearSelectedAmPm:function (evt) {
	this.clearSelectedAmPm();
}, onClearSelectedAnyTime:function (evt) {
	this.clearSelectedAnyTime();
	if (this.selectedTime.anyTime) {
		this.selectedTime.anyTime = false;
		this.time = dojo.widget.TimePicker.util.fromRfcDateTime("", this.useDefaultMinutes);
		this.initUI();
	}
}, clearSelectedHour:function () {
	var hourNodes = this.hourContainerNode.getElementsByTagName("td");
	for (var i = 0; i < hourNodes.length; i++) {
		dojo.html.setClass(hourNodes.item(i), "");
	}
}, clearSelectedMinute:function () {
	var minuteNodes = this.minuteContainerNode.getElementsByTagName("td");
	for (var i = 0; i < minuteNodes.length; i++) {
		dojo.html.setClass(minuteNodes.item(i), "");
	}
}, clearSelectedAmPm:function () {
	var amPmNodes = this.amPmContainerNode.getElementsByTagName("td");
	for (var i = 0; i < amPmNodes.length; i++) {
		dojo.html.setClass(amPmNodes.item(i), "");
	}
}, clearSelectedAnyTime:function () {
	dojo.html.setClass(this.anyTimeContainerNode, "anyTimeContainer");
}, onSetSelectedHour:function (evt) {
	this.onClearSelectedAnyTime();
	this.onClearSelectedHour();
	this.setSelectedHour(evt);
	this.onSetTime();
}, setSelectedHour:function (evt) {
	if (evt && evt.target) {
		if (evt.target.nodeType == dojo.dom.ELEMENT_NODE) {
			var eventTarget = evt.target;
		} else {
			var eventTarget = evt.target.parentNode;
		}
		dojo.event.browser.stopEvent(evt);
		dojo.html.setClass(eventTarget, this.classNames.selectedTime);
		this.selectedTime["hour"] = eventTarget.innerHTML;
	} else {
		if (!isNaN(evt)) {
			var hourNodes = this.hourContainerNode.getElementsByTagName("td");
			if (hourNodes.item(evt)) {
				dojo.html.setClass(hourNodes.item(evt), this.classNames.selectedTime);
				this.selectedTime["hour"] = hourNodes.item(evt).innerHTML;
			}
		}
	}
	this.selectedTime.anyTime = false;
}, onSetSelectedMinute:function (evt) {
	this.onClearSelectedAnyTime();
	this.onClearSelectedMinute();
	this.setSelectedMinute(evt);
	this.selectedTime.anyTime = false;
	this.onSetTime();
}, setSelectedMinute:function (evt) {
	if (evt && evt.target) {
		if (evt.target.nodeType == dojo.dom.ELEMENT_NODE) {
			var eventTarget = evt.target;
		} else {
			var eventTarget = evt.target.parentNode;
		}
		dojo.event.browser.stopEvent(evt);
		dojo.html.setClass(eventTarget, this.classNames.selectedTime);
		this.selectedTime["minute"] = eventTarget.innerHTML;
	} else {
		if (!isNaN(evt)) {
			var minuteNodes = this.minuteContainerNode.getElementsByTagName("td");
			if (minuteNodes.item(evt)) {
				dojo.html.setClass(minuteNodes.item(evt), this.classNames.selectedTime);
				this.selectedTime["minute"] = minuteNodes.item(evt).innerHTML;
			}
		}
	}
}, onSetSelectedAmPm:function (evt) {
	this.onClearSelectedAnyTime();
	this.onClearSelectedAmPm();
	this.setSelectedAmPm(evt);
	this.selectedTime.anyTime = false;
	this.onSetTime();
}, setSelectedAmPm:function (evt) {
	var eventTarget = evt.target;
	if (evt && eventTarget) {
		if (eventTarget.nodeType != dojo.dom.ELEMENT_NODE) {
			eventTarget = eventTarget.parentNode;
		}
		dojo.event.browser.stopEvent(evt);
		this.selectedTime.amPm = eventTarget.id;
		dojo.html.setClass(eventTarget, this.classNames.selectedTime);
	} else {
		evt = evt ? 0 : 1;
		var amPmNodes = this.amPmContainerNode.getElementsByTagName("td");
		if (amPmNodes.item(evt)) {
			this.selectedTime.amPm = amPmNodes.item(evt).id;
			dojo.html.setClass(amPmNodes.item(evt), this.classNames.selectedTime);
		}
	}
}, onSetSelectedAnyTime:function (evt) {
	this.onClearSelectedHour();
	this.onClearSelectedMinute();
	this.onClearSelectedAmPm();
	this.setSelectedAnyTime();
	this.onSetTime();
}, setSelectedAnyTime:function (evt) {
	this.selectedTime.anyTime = true;
	dojo.html.setClass(this.anyTimeContainerNode, this.classNames.selectedTime + " " + "anyTimeContainer");
}, onClick:function (evt) {
	dojo.event.browser.stopEvent(evt);
}, onSetTime:function () {
	if (this.selectedTime.anyTime) {
		this.time = new Date();
		var tempDateTime = dojo.widget.TimePicker.util.toRfcDateTime(this.time);
		this.setDateTime(tempDateTime.split("T")[0]);
	} else {
		var hour = 12;
		var minute = 0;
		var isAm = false;
		if (this.selectedTime["hour"]) {
			hour = parseInt(this.selectedTime["hour"], 10);
		}
		if (this.selectedTime["minute"]) {
			minute = parseInt(this.selectedTime["minute"], 10);
		}
		if (this.selectedTime["amPm"]) {
			isAm = (this.selectedTime["amPm"].toLowerCase() == "am");
		}
		this.time = new Date();
		this.time.setHours(dojo.widget.TimePicker.util.fromAmPmHour(hour, isAm));
		this.time.setMinutes(minute);
		this.setDateTime(dojo.widget.TimePicker.util.toRfcDateTime(this.time));
	}
	this.onValueChanged(this.time);
}, onValueChanged:function (date) {
}});
dojo.widget.TimePicker.util = new function () {
	this.toRfcDateTime = function (jsDate) {
		if (!jsDate) {
			jsDate = new Date();
		}
		jsDate.setSeconds(0);
		return dojo.date.strftime(jsDate, "%Y-%m-%dT%H:%M:00%z");
	};
	this.fromRfcDateTime = function (rfcDate, useDefaultMinutes, isAnyTime) {
		var tempDate = new Date();
		if (!rfcDate || rfcDate.indexOf("T") == -1) {
			if (useDefaultMinutes) {
				tempDate.setMinutes(Math.floor(tempDate.getMinutes() / 5) * 5);
			} else {
				tempDate.setMinutes(0);
			}
		} else {
			var tempTime = rfcDate.split("T")[1].split(":");
			var tempDate = new Date();
			tempDate.setHours(tempTime[0]);
			tempDate.setMinutes(tempTime[1]);
		}
		return tempDate;
	};
	this.toAmPmHour = function (hour) {
		var amPmHour = hour;
		var isAm = true;
		if (amPmHour == 0) {
			amPmHour = 12;
		} else {
			if (amPmHour > 12) {
				amPmHour = amPmHour - 12;
				isAm = false;
			} else {
				if (amPmHour == 12) {
					isAm = false;
				}
			}
		}
		return [amPmHour, isAm];
	};
	this.fromAmPmHour = function (amPmHour, isAm) {
		var hour = parseInt(amPmHour, 10);
		if (isAm && hour == 12) {
			hour = 0;
		} else {
			if (!isAm && hour < 12) {
				hour = hour + 12;
			}
		}
		return hour;
	};
};

