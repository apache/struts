/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.MonthlyCalendar");
dojo.require("dojo.date.common");
dojo.require("dojo.date.format");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.DatePicker");
dojo.require("dojo.event.*");
dojo.require("dojo.html.*");
dojo.require("dojo.experimental");
dojo.experimental("dojo.widget.MonthlyCalendar");
dojo.widget.defineWidget("dojo.widget.MonthlyCalendar", dojo.widget.DatePicker, {dayWidth:"wide", templateString:"<div class=\"datePickerContainer\" dojoAttachPoint=\"datePickerContainerNode\">\n\t<h3 class=\"monthLabel\">\n\t<!--\n\t<span \n\t\tdojoAttachPoint=\"decreaseWeekNode\" \n\t\tdojoAttachEvent=\"onClick: onIncrementWeek;\" \n\t\tclass=\"incrementControl\">\n\t\t<img src=\"${dojoWidgetModuleUri}templates/decrementWeek.gif\" alt=\"&uarr;\" />\n\t</span>\n\t-->\n\t<span \n\t\tdojoAttachPoint=\"decreaseMonthNode\" \n\t\tdojoAttachEvent=\"onClick: onIncrementMonth;\" class=\"incrementControl\">\n\t\t<img src=\"${dojoWidgetModuleUri}templates/decrementMonth.gif\" \n\t\t\talt=\"&uarr;\" dojoAttachPoint=\"decrementMonthImageNode\">\n\t</span>\n\t<span dojoAttachPoint=\"monthLabelNode\" class=\"month\">July</span>\n\t<span \n\t\tdojoAttachPoint=\"increaseMonthNode\" \n\t\tdojoAttachEvent=\"onClick: onIncrementMonth;\" class=\"incrementControl\">\n\t\t<img src=\"${dojoWidgetModuleUri}templates/incrementMonth.gif\" \n\t\t\talt=\"&darr;\"  dojoAttachPoint=\"incrementMonthImageNode\">\n\t</span>\n\t<!--\n\t\t<span dojoAttachPoint=\"increaseWeekNode\" \n\t\t\tdojoAttachEvent=\"onClick: onIncrementWeek;\" \n\t\t\tclass=\"incrementControl\">\n\t\t\t<img src=\"${dojoWidgetModuleUri}templates/incrementWeek.gif\" \n\t\t\talt=\"&darr;\" />\n\t\t</span>\n\t-->\n\t</h3>\n\t<table class=\"calendarContainer\">\n\t\t<thead>\n\t\t\t<tr dojoAttachPoint=\"dayLabelsRow\">\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t</tr>\n\t\t</thead>\n\t\t<tbody dojoAttachPoint=\"calendarDatesContainerNode\" \n\t\t\tdojoAttachEvent=\"onClick: onSetDate;\">\n\t\t\t<tr dojoAttachPoint=\"calendarRow0\">\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t</tr>\n\t\t\t<tr dojoAttachPoint=\"calendarRow1\">\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t</tr>\n\t\t\t<tr dojoAttachPoint=\"calendarRow2\">\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t</tr>\n\t\t\t<tr dojoAttachPoint=\"calendarRow3\">\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t</tr>\n\t\t\t<tr dojoAttachPoint=\"calendarRow4\">\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t</tr>\n\t\t\t<tr dojoAttachPoint=\"calendarRow5\">\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t\t<td></td>\n\t\t\t</tr>\n\t\t</tbody>\n\t</table>\n\t<h3 class=\"yearLabel\">\n\t\t<span dojoAttachPoint=\"previousYearLabelNode\"\n\t\t\tdojoAttachEvent=\"onClick: onIncrementYear;\" class=\"previousYear\"></span>\n\t\t<span class=\"selectedYear\" dojoAttachPoint=\"currentYearLabelNode\"></span>\n\t\t<span dojoAttachPoint=\"nextYearLabelNode\" \n\t\t\tdojoAttachEvent=\"onClick: onIncrementYear;\" class=\"nextYear\"></span>\n\t</h3>\n</div>\n", templateCssString:".datePickerContainer {\n\tmargin:0.5em 2em 0.5em 0;\n\t/*width:10em;*/\n\tfloat:left;\n}\n\n.previousMonth {\n\tbackground-color:#bbbbbb;\n}\n\n.currentMonth {\n\tbackground-color:#8f8f8f;\n}\n\n.nextMonth {\n\tbackground-color:#eeeeee;\n}\n\n.currentDate {\n\ttext-decoration:underline;\n\tfont-style:italic;\n}\n\n.selectedItem {\n\tbackground-color:#3a3a3a;\n\tcolor:#ffffff;\n}\n\n.calendarContainer {\n\tborder-collapse:collapse;\n\tborder-spacing:0;\n\tborder-bottom:1px solid #e6e6e6;\n\toverflow: hidden;\n\ttext-align: right;\n}\n\n.calendarContainer thead{\n\tborder-bottom:1px solid #e6e6e6;\n}\n\n.calendarContainer tbody * td {\n		height: 100px;\n		border: 1px solid gray;\n}\n\n.calendarContainer td {\n		width: 100px;\n		padding: 2px;\n\tvertical-align: top;\n}\n\n.monthLabel {\n\tfont-size:0.9em;\n\tfont-weight:400;\n\tmargin:0;\n\ttext-align:center;\n}\n\n.monthLabel .month {\n\tpadding:0 0.4em 0 0.4em;\n}\n\n.yearLabel {\n\tfont-size:0.9em;\n\tfont-weight:400;\n\tmargin:0.25em 0 0 0;\n\ttext-align:right;\n\tcolor:#a3a3a3;\n}\n\n.yearLabel .selectedYear {\n\tcolor:#000;\n\tpadding:0 0.2em;\n}\n\n.nextYear, .previousYear {\n\tcursor:pointer;cursor:hand;\n}\n\n.incrementControl {\n\tcursor:pointer;cursor:hand;\n\twidth:1em;\n}\n\n.dojoMonthlyCalendarEvent {\n\tfont-size:0.7em;\n\toverflow: hidden;\n\tfont-color: grey;\n\twhite-space: nowrap;\n\ttext-align: left;\n}\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/MonthlyCalendar.css"), initializer:function () {
	this.iCalendars = [];
}, addCalendar:function (cal) {
	dojo.debug("Adding Calendar");
	this.iCalendars.push(cal);
	dojo.debug("Starting init");
	this.initUI();
	dojo.debug("done init");
}, createDayContents:function (node, mydate) {
	dojo.html.removeChildren(node);
	node.appendChild(document.createTextNode(mydate.getDate()));
	for (var x = 0; x < this.iCalendars.length; x++) {
		var evts = this.iCalendars[x].getEvents(mydate);
		if ((dojo.lang.isArray(evts)) && (evts.length > 0)) {
			for (var y = 0; y < evts.length; y++) {
				var el = document.createElement("div");
				dojo.html.addClass(el, "dojoMonthlyCalendarEvent");
				el.appendChild(document.createTextNode(evts[y].summary.value));
				el.width = dojo.html.getContentBox(node).width;
				node.appendChild(el);
			}
		}
	}
}, initUI:function () {
	var dayLabels = dojo.date.getNames("days", this.dayWidth, "standAlone", this.lang);
	var dayLabelNodes = this.dayLabelsRow.getElementsByTagName("td");
	for (var i = 0; i < 7; i++) {
		dayLabelNodes.item(i).innerHTML = dayLabels[i];
	}
	this.selectedIsUsed = false;
	this.currentIsUsed = false;
	var currentClassName = "";
	var previousDate = new Date();
	var calendarNodes = this.calendarDatesContainerNode.getElementsByTagName("td");
	var currentCalendarNode;
	previousDate.setHours(8);
	var nextDate = new Date(this.firstSaturday.year, this.firstSaturday.month, this.firstSaturday.date, 8);
	var lastDay = new Date(this.firstSaturday.year, this.firstSaturday.month, this.firstSaturday.date + 42, 8);
	if (this.iCalendars.length > 0) {
		for (var x = 0; x < this.iCalendars.length; x++) {
			this.iCalendars[x].preComputeRecurringEvents(lastDay);
		}
	}
	if (this.firstSaturday.date < 7) {
		var dayInWeek = 6;
		for (var i = this.firstSaturday.date; i > 0; i--) {
			currentCalendarNode = calendarNodes.item(dayInWeek);
			this.createDayContents(currentCalendarNode, nextDate);
			dojo.html.setClass(currentCalendarNode, this.getDateClassName(nextDate, "current"));
			dayInWeek--;
			previousDate = nextDate;
			nextDate = this.incrementDate(nextDate, false);
		}
		for (var i = dayInWeek; i > -1; i--) {
			currentCalendarNode = calendarNodes.item(i);
			this.createDayContents(currentCalendarNode, nextDate);
			dojo.html.setClass(currentCalendarNode, this.getDateClassName(nextDate, "previous"));
			previousDate = nextDate;
			nextDate = this.incrementDate(nextDate, false);
		}
	} else {
		nextDate.setDate(1);
		for (var i = 0; i < 7; i++) {
			currentCalendarNode = calendarNodes.item(i);
			this.createDayContents(currentCalendarNode, nextDate);
			dojo.html.setClass(currentCalendarNode, this.getDateClassName(nextDate, "current"));
			previousDate = nextDate;
			nextDate = this.incrementDate(nextDate, true);
		}
	}
	previousDate.setDate(this.firstSaturday.date);
	previousDate.setMonth(this.firstSaturday.month);
	previousDate.setFullYear(this.firstSaturday.year);
	nextDate = this.incrementDate(previousDate, true);
	var count = 7;
	currentCalendarNode = calendarNodes.item(count);
	while ((nextDate.getMonth() == previousDate.getMonth()) && (count < 42)) {
		this.createDayContents(currentCalendarNode, nextDate);
		dojo.html.setClass(currentCalendarNode, this.getDateClassName(nextDate, "current"));
		currentCalendarNode = calendarNodes.item(++count);
		previousDate = nextDate;
		nextDate = this.incrementDate(nextDate, true);
	}
	while (count < 42) {
		this.createDayContents(currentCalendarNode, nextDate);
		dojo.html.setClass(currentCalendarNode, this.getDateClassName(nextDate, "next"));
		currentCalendarNode = calendarNodes.item(++count);
		previousDate = nextDate;
		nextDate = this.incrementDate(nextDate, true);
	}
	this.setMonthLabel(this.firstSaturday.month);
	this.setYearLabels(this.firstSaturday.year);
}});
dojo.widget.MonthlyCalendar.util = new function () {
	this.toRfcDate = function (jsDate) {
		if (!jsDate) {
			jsDate = this.today;
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
		return year + "-" + month + "-" + date + "T00:00:00+00:00";
	};
	this.fromRfcDate = function (rfcDate) {
		var tempDate = rfcDate.split("-");
		if (tempDate.length < 3) {
			return new Date();
		}
		return new Date(parseInt(tempDate[0]), (parseInt(tempDate[1], 10) - 1), parseInt(tempDate[2].substr(0, 2), 10));
	};
	this.initFirstSaturday = function (month, year) {
		if (!month) {
			month = this.date.getMonth();
		}
		if (!year) {
			year = this.date.getFullYear();
		}
		var firstOfMonth = new Date(year, month, 1);
		return {year:year, month:month, date:7 - firstOfMonth.getDay()};
	};
};

