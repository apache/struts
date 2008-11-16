/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.date.common");


/* Supplementary Date Functions
 *******************************/

dojo.date.setDayOfYear = function(/*Date*/dateObject, /*Number*/dayOfYear){
	// summary: sets dateObject according to day of the year (1..366)
	dateObject.setMonth(0);
	dateObject.setDate(dayOfYear);
	return dateObject; // Date
}

dojo.date.getDayOfYear = function(/*Date*/dateObject){
	// summary: gets the day of the year as represented by dateObject
	var fullYear = dateObject.getFullYear();
	var lastDayOfPrevYear = new Date(fullYear-1, 11, 31);
	return Math.floor((dateObject.getTime() -
		lastDayOfPrevYear.getTime()) / 86400000); // Number
}


dojo.date.setWeekOfYear = function(/*Date*/dateObject, /*Number*/week, /*Number*/firstDay){
	if(arguments.length == 1){ firstDay = 0; } // Sunday
	dojo.unimplemented("dojo.date.setWeekOfYear");
}

dojo.date.getWeekOfYear = function(/*Date*/dateObject, /*Number*/firstDay){
	if(arguments.length == 1){ firstDay = 0; } // Sunday

	// work out the first day of the year corresponding to the week
	var firstDayOfYear = new Date(dateObject.getFullYear(), 0, 1);
	var day = firstDayOfYear.getDay();
	firstDayOfYear.setDate(firstDayOfYear.getDate() -
			day + firstDay - (day > firstDay ? 7 : 0));

	return Math.floor((dateObject.getTime() -
		firstDayOfYear.getTime()) / 604800000);
}

dojo.date.setIsoWeekOfYear = function(/*Date*/dateObject, /*Number*/week, /*Number*/firstDay){
	// summary: unimplemented
	if (arguments.length == 1) { firstDay = 1; } // Monday
	dojo.unimplemented("dojo.date.setIsoWeekOfYear");
}

dojo.date.getIsoWeekOfYear = function(/*Date*/dateObject, /*Number*/firstDay) {
	// summary: unimplemented
	if (arguments.length == 1) { firstDay = 1; } // Monday
	dojo.unimplemented("dojo.date.getIsoWeekOfYear");
}


/* Informational Functions
 **************************/

//DEPRECATED: These timezone arrays will be deprecated in 0.5
dojo.date.shortTimezones = ["IDLW", "BET", "HST", "MART", "AKST", "PST", "MST",
	"CST", "EST", "AST", "NFT", "BST", "FST", "AT", "GMT", "CET", "EET", "MSK",
	"IRT", "GST", "AFT", "AGTT", "IST", "NPT", "ALMT", "MMT", "JT", "AWST",
	"JST", "ACST", "AEST", "LHST", "VUT", "NFT", "NZT", "CHAST", "PHOT",
	"LINT"];
dojo.date.timezoneOffsets = [-720, -660, -600, -570, -540, -480, -420, -360,
	-300, -240, -210, -180, -120, -60, 0, 60, 120, 180, 210, 240, 270, 300,
	330, 345, 360, 390, 420, 480, 540, 570, 600, 630, 660, 690, 720, 765, 780,
	840];
/*
dojo.date.timezones = ["International Date Line West", "Bering Standard Time",
	"Hawaiian Standard Time", "Marquesas Time", "Alaska Standard Time",
	"Pacific Standard Time (USA)", "Mountain Standard Time",
	"Central Standard Time (USA)", "Eastern Standard Time (USA)",
	"Atlantic Standard Time", "Newfoundland Time", "Brazil Standard Time",
	"Fernando de Noronha Standard Time (Brazil)", "Azores Time",
	"Greenwich Mean Time", "Central Europe Time", "Eastern Europe Time",
	"Moscow Time", "Iran Standard Time", "Gulf Standard Time",
	"Afghanistan Time", "Aqtobe Time", "Indian Standard Time", "Nepal Time",
	"Almaty Time", "Myanmar Time", "Java Time",
	"Australian Western Standard Time", "Japan Standard Time",
	"Australian Central Standard Time", "Lord Hove Standard Time (Australia)",
	"Vanuata Time", "Norfolk Time (Australia)", "New Zealand Standard Time",
	"Chatham Standard Time (New Zealand)", "Phoenix Islands Time (Kribati)",
	"Line Islands Time (Kribati)"];
*/

dojo.date.getDaysInMonth = function(/*Date*/dateObject){
	// summary: returns the number of days in the month used by dateObject
	var month = dateObject.getMonth();
	var days = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
	if (month == 1 && dojo.date.isLeapYear(dateObject)) { return 29; } // Number
	else { return days[month]; } // Number
}

dojo.date.isLeapYear = function(/*Date*/dateObject){
// summary:
//	Determines if the year of the dateObject is a leap year
//
// description:
//	Leap years are years with an additional day YYYY-02-29, where the year
//	number is a multiple of four with the following exception: If a year
//	is a multiple of 100, then it is only a leap year if it is also a
//	multiple of 400. For example, 1900 was not a leap year, but 2000 is one.

	var year = dateObject.getFullYear();
	return (year%400 == 0) ? true : (year%100 == 0) ? false : (year%4 == 0) ? true : false; // Boolean
}

// FIXME: This is not localized
dojo.date.getTimezoneName = function(/*Date*/dateObject){
// summary:
//	Get the user's time zone as provided by the browser
//
// dateObject: needed because the timezone may vary with time (daylight savings)
//
// description:
//	Try to get time zone info from toString or toLocaleString
//	method of the Date object -- UTC offset is not a time zone.
//	See http://www.twinsun.com/tz/tz-link.htm
//	Note: results may be inconsistent across browsers.

	var str = dateObject.toString(); // Start looking in toString
	var tz = ''; // The result -- return empty string if nothing found
	var match;

	// First look for something in parentheses -- fast lookup, no regex
	var pos = str.indexOf('(');
	if (pos > -1) {
		pos++;
		tz = str.substring(pos, str.indexOf(')'));
	}
	// If at first you don't succeed ...
	else{
		// If IE knows about the TZ, it appears before the year
		// Capital letters or slash before a 4-digit year 
		// at the end of string
		var pat = /([A-Z\/]+) \d{4}$/;
		if((match = str.match(pat))) {
			tz = match[1];
		}
		// Some browsers (e.g. Safari) glue the TZ on the end
		// of toLocaleString instead of putting it in toString
		else{
			str = dateObject.toLocaleString();
			// Capital letters or slash -- end of string, 
			// after space
			pat = / ([A-Z\/]+)$/;
			if((match = str.match(pat))) {
				tz = match[1];
			}
		}
	}

	// Make sure it doesn't somehow end up return AM or PM
	return tz == 'AM' || tz == 'PM' ? '' : tz; //String
}


//FIXME: not localized
dojo.date.getOrdinal = function(dateObject){
	// summary: returns the appropriate suffix (English only) for the day of the month, e.g. 'st' for 1, 'nd' for 2, etc.)
	var date = dateObject.getDate();

	if(date%100 != 11 && date%10 == 1){ return "st"; } // String
	else if(date%100 != 12 && date%10 == 2){ return "nd"; } // String
	else if(date%100 != 13 && date%10 == 3){ return "rd"; } // String
	else{ return "th"; } // String
}


/* compare and add
 ******************/
dojo.date.compareTypes={
	// 	summary
	//	bitmask for comparison operations.
	DATE:1, TIME:2 
};
dojo.date.compare=function(/* Date */ dateA, /* Date */ dateB, /* dojo.date.compareTypes */ options){
	//	summary
	//	Compare two date objects by date, time, or both.  Returns 0 if equal, positive if a > b, else negative.
	var dA=dateA;
	var dB=dateB||new Date();
	var now=new Date();
	//FIXME: shorten this code
	with(dojo.date.compareTypes){
		var opt=options||(DATE|TIME);
		var d1=new Date(
			(opt&DATE)?dA.getFullYear():now.getFullYear(), 
			(opt&DATE)?dA.getMonth():now.getMonth(),
			(opt&DATE)?dA.getDate():now.getDate(),
			(opt&TIME)?dA.getHours():0,
			(opt&TIME)?dA.getMinutes():0,
			(opt&TIME)?dA.getSeconds():0
		);
		var d2=new Date(
			(opt&DATE)?dB.getFullYear():now.getFullYear(),
			(opt&DATE)?dB.getMonth():now.getMonth(),
			(opt&DATE)?dB.getDate():now.getDate(),
			(opt&TIME)?dB.getHours():0,
			(opt&TIME)?dB.getMinutes():0,
			(opt&TIME)?dB.getSeconds():0
		);
	}
	if(d1.valueOf()>d2.valueOf()){
		return 1;	//	int
	}
	if(d1.valueOf()<d2.valueOf()){
		return -1;	//	int
	}
	return 0;	//	int
}

dojo.date.dateParts={ 
	//	summary
	//	constants for use in dojo.date.add
	YEAR:0, MONTH:1, DAY:2, HOUR:3, MINUTE:4, SECOND:5, MILLISECOND:6, QUARTER:7, WEEK:8, WEEKDAY:9
};

dojo.date.add = function(/* Date */ dt, /* dojo.date.dateParts */ interv, /* int */ incr){
//	summary:
//		Add to a Date in intervals of different size, from milliseconds to years
//
//	dt:
//		A Javascript Date object to start with
//
//	interv:
//		A constant representing the interval, e.g. YEAR, MONTH, DAY.  See dojo.date.dateParts.
//
//	incr:
//		How much to add to the date

	if(typeof dt == 'number'){dt = new Date(dt);} // Allow timestamps
//FIXME: what's the reason behind this?	incr = incr || 1;

	function fixOvershoot(){
		if (sum.getDate() < dt.getDate()){
			sum.setDate(0);
		}
	}
	
	var sum = new Date(dt);

	with(dojo.date.dateParts){
		switch(interv){
			case YEAR:
				sum.setFullYear(dt.getFullYear()+incr);
				// Keep increment/decrement from 2/29 out of March
				fixOvershoot();
				break;
			case QUARTER:
				// Naive quarter is just three months
				incr*=3;
				// fallthrough...
			case MONTH:
				sum.setMonth(dt.getMonth()+incr);
				// Reset to last day of month if you overshoot
				fixOvershoot();
				break;
			case WEEK:
				incr*=7;
				// fallthrough...
			case DAY:
				sum.setDate(dt.getDate() + incr);
				break;
			case WEEKDAY:
				//FIXME: assumes Saturday/Sunday weekend, but even this is not fixed.  There are CLDR entries to localize this.
				var dat = dt.getDate();
				var weeks = 0;
				var days = 0;
				var strt = 0;
				var trgt = 0;
				var adj = 0;
				// Divide the increment time span into weekspans plus leftover days
				// e.g., 8 days is one 5-day weekspan / and two leftover days
				// Can't have zero leftover days, so numbers divisible by 5 get
				// a days value of 5, and the remaining days make up the number of weeks
				var mod = incr % 5;
				if (mod == 0) {
					days = (incr > 0) ? 5 : -5;
					weeks = (incr > 0) ? ((incr-5)/5) : ((incr+5)/5);
				}
				else {
					days = mod;
					weeks = parseInt(incr/5);
				}
				// Get weekday value for orig date param
				strt = dt.getDay();
				// Orig date is Sat / positive incrementer
				// Jump over Sun
				if (strt == 6 && incr > 0) {
					adj = 1;
				}
				// Orig date is Sun / negative incrementer
				// Jump back over Sat
				else if (strt == 0 && incr < 0) {
					adj = -1;
				}
				// Get weekday val for the new date
				trgt = (strt + days);
				// New date is on Sat or Sun
				if (trgt == 0 || trgt == 6) {
					adj = (incr > 0) ? 2 : -2;
				}
				// Increment by number of weeks plus leftover days plus
				// weekend adjustments
				sum.setDate(dat + (7*weeks) + days + adj);
				break;
			case HOUR:
				sum.setHours(sum.getHours()+incr);
				break;
			case MINUTE:
				sum.setMinutes(sum.getMinutes()+incr);
				break;
			case SECOND:
				sum.setSeconds(sum.getSeconds()+incr);
				break;
			case MILLISECOND:
				sum.setMilliseconds(sum.getMilliseconds()+incr);
				break;
			default:
				// Do nothing
				break;
		}
	}

	return sum; // Date
};

dojo.date.diff = function(/* Date */ dtA, /* Date */ dtB, /* dojo.date.dateParts */ interv){
//	summary:
//		Get the difference in a specific unit of time (e.g., number of months, weeks,
//		days, etc.) between two dates.
//
//	dtA:
//		A Javascript Date object
//
//	dtB:
//		A Javascript Date object
//
//	interv:
//		A constant representing the interval, e.g. YEAR, MONTH, DAY.  See dojo.date.dateParts.

	// Accept timestamp input
	if(typeof dtA == 'number'){dtA = new Date(dtA);}
	if(typeof dtB == 'number'){dtB = new Date(dtB);}
	var yeaDiff = dtB.getFullYear() - dtA.getFullYear();
	var monDiff = (dtB.getMonth() - dtA.getMonth()) + (yeaDiff * 12);
	var msDiff = dtB.getTime() - dtA.getTime(); // Millisecs
	var secDiff = msDiff/1000;
	var minDiff = secDiff/60;
	var houDiff = minDiff/60;
	var dayDiff = houDiff/24;
	var weeDiff = dayDiff/7;
	var delta = 0; // Integer return value

	with(dojo.date.dateParts){
		switch(interv){
			case YEAR:
				delta = yeaDiff;
				break;
			case QUARTER:
				var mA = dtA.getMonth();
				var mB = dtB.getMonth();
				// Figure out which quarter the months are in
				var qA = Math.floor(mA/3) + 1;
				var qB = Math.floor(mB/3) + 1;
				// Add quarters for any year difference between the dates
				qB += (yeaDiff * 4);
				delta = qB - qA;
				break;
			case MONTH:
				delta = monDiff;
				break;
			case WEEK:
				// Truncate instead of rounding
				// Don't use Math.floor -- value may be negative
				delta = parseInt(weeDiff);
				break;
			case DAY:
				delta = dayDiff;
				break;
			case WEEKDAY:
				var days = Math.round(dayDiff);
				var weeks = parseInt(days/7);
				var mod = days % 7;

				// Even number of weeks
				if (mod == 0) {
					days = weeks*5;
				}
				// Weeks plus spare change (< 7 days)
				else {
					var adj = 0;
					var aDay = dtA.getDay();
					var bDay = dtB.getDay();
	
					weeks = parseInt(days/7);
					mod = days % 7;
					// Mark the date advanced by the number of
					// round weeks (may be zero)
					var dtMark = new Date(dtA);
					dtMark.setDate(dtMark.getDate()+(weeks*7));
					var dayMark = dtMark.getDay();
					// Spare change days -- 6 or less
					// ----------
					// Positive diff
					if (dayDiff > 0) {
						switch (true) {
							// Range starts on Sat
							case aDay == 6:
								adj = -1;
								break;
							// Range starts on Sun
							case aDay == 0:
								adj = 0;
								break;
							// Range ends on Sat
							case bDay == 6:
								adj = -1;
								break;
							// Range ends on Sun
							case bDay == 0:
								adj = -2;
								break;
							// Range contains weekend
							case (dayMark + mod) > 5:
								adj = -2;
								break;
							default:
								// Do nothing
								break;
						}
					}
					// Negative diff
					else if (dayDiff < 0) {
						switch (true) {
							// Range starts on Sat
							case aDay == 6:
								adj = 0;
								break;
							// Range starts on Sun
							case aDay == 0:
								adj = 1;
								break;
							// Range ends on Sat
							case bDay == 6:
								adj = 2;
								break;
							// Range ends on Sun
							case bDay == 0:
								adj = 1;
								break;
							// Range contains weekend
							case (dayMark + mod) < 0:
								adj = 2;
								break;
							default:
								// Do nothing
								break;
						}
					}
					days += adj;
					days -= (weeks*2);
				}
				delta = days;

				break;
			case HOUR:
				delta = houDiff;
				break;
			case MINUTE:
				delta = minDiff;
				break;
			case SECOND:
				delta = secDiff;
				break;
			case MILLISECOND:
				delta = msDiff;
				break;
			default:
				// Do nothing
				break;
		}
	}

	// Round for fractional values and DST leaps
	return Math.round(delta); // Number (integer)
};
