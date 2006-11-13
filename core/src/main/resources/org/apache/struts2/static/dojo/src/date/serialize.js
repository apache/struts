/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.date.serialize");

dojo.require("dojo.string.common");

/* ISO 8601 Functions
 *********************/

dojo.date.setIso8601 = function(/*Date*/dateObject, /*String*/formattedString){
	// summary: sets a Date object based on an ISO 8601 formatted string (uses date and time)
	var comps = (formattedString.indexOf("T") == -1) ? formattedString.split(" ") : formattedString.split("T");
	dateObject = dojo.date.setIso8601Date(dateObject, comps[0]);
	if(comps.length == 2){ dateObject = dojo.date.setIso8601Time(dateObject, comps[1]); }
	return dateObject; /* Date or null */
};

dojo.date.fromIso8601 = function(/*String*/formattedString){
	// summary: returns a Date object based on an ISO 8601 formatted string (uses date and time)
	return dojo.date.setIso8601(new Date(0, 0), formattedString);
};

dojo.date.setIso8601Date = function(/*String*/dateObject, /*String*/formattedString){
	// summary: sets a Date object based on an ISO 8601 formatted string (date only)
	var regexp = "^([0-9]{4})((-?([0-9]{2})(-?([0-9]{2}))?)|" +
			"(-?([0-9]{3}))|(-?W([0-9]{2})(-?([1-7]))?))?$";
	var d = formattedString.match(new RegExp(regexp));
	if(!d){
		dojo.debug("invalid date string: " + formattedString);
		return null; // null
	}
	var year = d[1];
	var month = d[4];
	var date = d[6];
	var dayofyear = d[8];
	var week = d[10];
	var dayofweek = d[12] ? d[12] : 1;

	dateObject.setFullYear(year);

	if(dayofyear){
		dateObject.setMonth(0);
		dateObject.setDate(Number(dayofyear));
	}
	else if(week){
		dateObject.setMonth(0);
		dateObject.setDate(1);
		var gd = dateObject.getDay();
		var day =  gd ? gd : 7;
		var offset = Number(dayofweek) + (7 * Number(week));
		
		if(day <= 4){ dateObject.setDate(offset + 1 - day); }
		else{ dateObject.setDate(offset + 8 - day); }
	} else{
		if(month){
			dateObject.setDate(1);
			dateObject.setMonth(month - 1); 
		}
		if(date){ dateObject.setDate(date); }
	}
	
	return dateObject; // Date
};

dojo.date.fromIso8601Date = function(/*String*/formattedString){
	// summary: returns a Date object based on an ISO 8601 formatted string (date only)
	return dojo.date.setIso8601Date(new Date(0, 0), formattedString);
};

dojo.date.setIso8601Time = function(/*Date*/dateObject, /*String*/formattedString){
	// summary: sets a Date object based on an ISO 8601 formatted string (time only)

	// first strip timezone info from the end
	var timezone = "Z|(([-+])([0-9]{2})(:?([0-9]{2}))?)$";
	var d = formattedString.match(new RegExp(timezone));

	var offset = 0; // local time if no tz info
	if(d){
		if(d[0] != 'Z'){
			offset = (Number(d[3]) * 60) + Number(d[5]);
			offset *= ((d[2] == '-') ? 1 : -1);
		}
		offset -= dateObject.getTimezoneOffset();
		formattedString = formattedString.substr(0, formattedString.length - d[0].length);
	}

	// then work out the time
	var regexp = "^([0-9]{2})(:?([0-9]{2})(:?([0-9]{2})(\.([0-9]+))?)?)?$";
	d = formattedString.match(new RegExp(regexp));
	if(!d){
		dojo.debug("invalid time string: " + formattedString);
		return null; // null
	}
	var hours = d[1];
	var mins = Number((d[3]) ? d[3] : 0);
	var secs = (d[5]) ? d[5] : 0;
	var ms = d[7] ? (Number("0." + d[7]) * 1000) : 0;

	dateObject.setHours(hours);
	dateObject.setMinutes(mins);
	dateObject.setSeconds(secs);
	dateObject.setMilliseconds(ms);

	if(offset !== 0){
		dateObject.setTime(dateObject.getTime() + offset * 60000);
	}	
	return dateObject; // Date
};

dojo.date.fromIso8601Time = function(/*String*/formattedString){
	// summary: returns a Date object based on an ISO 8601 formatted string (date only)
	return dojo.date.setIso8601Time(new Date(0, 0), formattedString);
};


/* RFC-3339 Date Functions
 *************************/

dojo.date.toRfc3339 = function(/*Date?*/dateObject, /*String?*/selector){
//	summary:
//		Format a JavaScript Date object as a string according to RFC 3339
//
//	dateObject:
//		A JavaScript date, or the current date and time, by default
//
//	selector:
//		"dateOnly" or "timeOnly" to format selected portions of the Date object.
//		Date and time will be formatted by default.

//FIXME: tolerate Number, string input?
	if(!dateObject){
		dateObject = new Date();
	}

	var _ = dojo.string.pad;
	var formattedDate = [];
	if(selector != "timeOnly"){
		var date = [_(dateObject.getFullYear(),4), _(dateObject.getMonth()+1,2), _(dateObject.getDate(),2)].join('-');
		formattedDate.push(date);
	}
	if(selector != "dateOnly"){
		var time = [_(dateObject.getHours(),2), _(dateObject.getMinutes(),2), _(dateObject.getSeconds(),2)].join(':');
		var timezoneOffset = dateObject.getTimezoneOffset();
		time += (timezoneOffset > 0 ? "-" : "+") + 
					_(Math.floor(Math.abs(timezoneOffset)/60),2) + ":" +
					_(Math.abs(timezoneOffset)%60,2);
		formattedDate.push(time);
	}
	return formattedDate.join('T'); // String
};

dojo.date.fromRfc3339 = function(/*String*/rfcDate){
//	summary:
//		Create a JavaScript Date object from a string formatted according to RFC 3339
//
//	rfcDate:
//		A string such as 2005-06-30T08:05:00-07:00
//		"any" is also supported in place of a time.

	// backwards compatible support for use of "any" instead of just not 
	// including the time
	if(rfcDate.indexOf("Tany")!=-1){
		rfcDate = rfcDate.replace("Tany","");
	}
	var dateObject = new Date();
	return dojo.date.setIso8601(dateObject, rfcDate); // Date or null
};
