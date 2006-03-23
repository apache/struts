/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.date");

/**
 * Sets the current Date object to the time given in an ISO 8601 date/time
 * stamp
 *
 * @param string The date/time formted as an ISO 8601 string
 */
dojo.date.setIso8601 = function (dateObject, string) {
	var comps = string.split('T');
	dojo.date.setIso8601Date(dateObject, comps[0]);
	if (comps.length == 2) { dojo.date.setIso8601Time(dateObject, comps[1]); }
	return dateObject;
}

dojo.date.fromIso8601 = function (string) {
	return dojo.date.setIso8601(new Date(0), string);
}

/**
 * Sets the current Date object to the date given in an ISO 8601 date
 * stamp. The time is left unchanged.
 *
 * @param string The date formted as an ISO 8601 string
 */
dojo.date.setIso8601Date = function (dateObject, string) {
	var regexp = "^([0-9]{4})((-?([0-9]{2})(-?([0-9]{2}))?)|" +
			"(-?([0-9]{3}))|(-?W([0-9]{2})(-?([1-7]))?))?$";
	var d = string.match(new RegExp(regexp));

	var year = d[1];
	var month = d[4];
	var date = d[6];
	var dayofyear = d[8];
	var week = d[10];
	var dayofweek = (d[12]) ? d[12] : 1;

	dateObject.setYear(year);
	
	if (dayofyear) { dojo.date.setDayOfYear(dateObject, Number(dayofyear)); }
	else if (week) {
		dateObject.setMonth(0);
		dateObject.setDate(1);
		var gd = dateObject.getDay();
		var day =  (gd) ? gd : 7;
		var offset = Number(dayofweek) + (7 * Number(week));
		
		if (day <= 4) { dateObject.setDate(offset + 1 - day); }
		else { dateObject.setDate(offset + 8 - day); }
	} else {
		if (month) { dateObject.setMonth(month - 1); }
		if (date) { dateObject.setDate(date); }
	}
	
	return dateObject;
}

dojo.date.fromIso8601Date = function (string) {
	return dojo.date.setIso8601Date(new Date(0), string);
}

/**
 * Sets the current Date object to the date given in an ISO 8601 time
 * stamp. The date is left unchanged.
 *
 * @param string The time formted as an ISO 8601 string
 */
dojo.date.setIso8601Time = function (dateObject, string) {
	// first strip timezone info from the end
	var timezone = "Z|(([-+])([0-9]{2})(:?([0-9]{2}))?)$";
	var d = string.match(new RegExp(timezone));

	var offset = 0; // local time if no tz info
	if (d) {
		if (d[0] != 'Z') {
			offset = (Number(d[3]) * 60) + Number(d[5]);
			offset *= ((d[2] == '-') ? 1 : -1);
		}
		offset -= dateObject.getTimezoneOffset()
		string = string.substr(0, string.length - d[0].length);
	}

	// then work out the time
	var regexp = "^([0-9]{2})(:?([0-9]{2})(:?([0-9]{2})(\.([0-9]+))?)?)?$";
	var d = string.match(new RegExp(regexp));

	var hours = d[1];
	var mins = Number((d[3]) ? d[3] : 0) + offset;
	var secs = (d[5]) ? d[5] : 0;
	var ms = d[7] ? (Number("0." + d[7]) * 1000) : 0;

	dateObject.setHours(hours);
	dateObject.setMinutes(mins);
	dateObject.setSeconds(secs);
	dateObject.setMilliseconds(ms);
	
	return dateObject;
}

dojo.date.fromIso8601Time = function (string) {
	return dojo.date.setIso8601Time(new Date(0), string);
}

/**
 * Sets the date to the day of year
 *
 * @param date The day of year
 */
dojo.date.setDayOfYear = function (dateObject, dayofyear) {
	dateObject.setMonth(0);
	dateObject.setDate(dayofyear);
}

/**
 * Retrieves the day of the year the Date is set to.
 *
 * @return The day of the year
 */
dojo.date.getDayOfYear = function (dateObject) {
	var tmpdate = new Date(0);
	tmpdate.setMonth(dateObject.getMonth());
	tmpdate.setDate(dateObject.getDate());
	return Number(tmpdate) / 86400000; // # milliseconds in a day
}


/**
 * Returns the number of days in the given month. Leap years are accounted
 * for.
 *
 * @param month The month
 * @param year  The year
 * @return The number of days in the given month
 */
dojo.date.daysInMonth = function (month, year) {
	/*
	 * Leap years are years with an additional day YYYY-02-29, where the year
	 * number is a multiple of four with the following exception: If a year
	 * is a multiple of 100, then it is only a leap year if it is also a
	 * multiple of 400. For example, 1900 was not a leap year, but 2000 is one.
	 */
	var days = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
	if (month == 1 && year) {
		if ((!(year % 4) && (year % 100)) ||
			(!(year % 4) && !(year % 100) && !(year % 400))) { return 29; }
		else { return 28; }
	} else { return days[month]; }
}

dojo.date.months = ["January", "February", "March", "April", "May", "June",
	"July", "August", "September", "October", "November", "December"];
dojo.date.shortMonths = ["Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"];
dojo.date.days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
dojo.date.shortDays = ["Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat"];

/**
 *
 * Returns a string of the date in the version "January 1, 2004"
 *
 * @param date The date object
 */
dojo.date.toLongDateString = function(date) {
	return dojo.date.months[date.getMonth()] + " " + date.getDate() + ", " + date.getFullYear();
}

/**
 *
 * Returns a string of the date in the version "Jan 1, 2004"
 *
 * @param date The date object
 */
dojo.date.toShortDateString = function(date) {
	return dojo.date.shortMonths[date.getMonth()] + " " + date.getDate() + ", " + date.getFullYear();
}

/**
 *
 * Returns military formatted time
 *
 * @param date the date object
 */
dojo.date.toMilitaryTimeString = function(date){
	var h = "00" + date.getHours();
	var m = "00" + date.getMinutes();
	var s = "00" + date.getSeconds();
	return h.substr(h.length-2,2) + ":" + m.substr(m.length-2,2) + ":" + s.substr(s.length-2,2);
}

/**
 *
 * Returns a string of the date relative to the current date.
 *
 * @param date The date object
 *
 * Example returns:
 * - "1 minute ago"
 * - "4 minutes ago"
 * - "Yesterday"
 * - "2 days ago"
 */
dojo.date.toRelativeString = function(date) {
	var now = new Date();
	var diff = (now - date) / 1000;
	var end = " ago";
	var future = false;
	if(diff < 0) {
		future = true;
		end = " from now";
		diff = -diff;
	}

	if(diff < 60) {
		diff = Math.round(diff);
		return diff + " second" + (diff == 1 ? "" : "s") + end;
	} else if(diff < 3600) {
		diff = Math.round(diff/60);
		return diff + " minute" + (diff == 1 ? "" : "s") + end;
	} else if(diff < 3600*24 && date.getDay() == now.getDay()) {
		diff = Math.round(diff/3600);
		return diff + " hour" + (diff == 1 ? "" : "s") + end;
	} else if(diff < 3600*24*7) {
		diff = Math.round(diff/(3600*24));
		if(diff == 1) {
			return future ? "Tomorrow" : "Yesterday";
		} else {
			return diff + " days" + end;
		}
	} else {
		return dojo.date.toShortDateString(date);
	}
}
