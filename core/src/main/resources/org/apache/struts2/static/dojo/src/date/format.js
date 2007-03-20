/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.date.format");

dojo.require("dojo.date.common");
dojo.require("dojo.date.supplemental");
dojo.require("dojo.lang.array");
dojo.require("dojo.lang.common");
dojo.require("dojo.lang.func");
dojo.require("dojo.string.common");
dojo.require("dojo.i18n.common");

// Load the bundles containing localization information for
// names and formats
dojo.requireLocalization("dojo.i18n.calendar", "gregorian");
dojo.requireLocalization("dojo.i18n.calendar", "gregorianExtras");

//NOTE: Everything in this module assumes Gregorian calendars.
// Other calendars will be implemented in separate modules.

(function(){
dojo.date.format = function(/*Date*/dateObject, /*Object?*/options){
//
// summary:
//		Format a Date object as a String, using locale-specific settings.
//
// description:
//		Create a string from a Date object using a known localized pattern.
//		By default, this method formats both date and time from dateObject.
//		Formatting patterns are chosen appropriate to the locale.  Different
//		formatting lengths may be chosen, with "full" used by default.
//		Custom patterns may be used or registered with translations using
//		the addCustomBundle method.
//		Formatting patterns are implemented using the syntax described at
//		http://www.unicode.org/reports/tr35/tr35-4.html#Date_Format_Patterns
//
// dateObject:
//		the date and/or time to be formatted.  If a time only is formatted,
//		the values in the year, month, and day fields are irrelevant.  The
//		opposite is true when formatting only dates.
//
// options: object {selector: string, formatLength: string, datePattern: string, timePattern: string, locale: string}
//		selector- choice of timeOnly,dateOnly (default: date and time)
//		formatLength- choice of long, short, medium or full (plus any custom additions).  Defaults to 'full'
//		datePattern,timePattern- override pattern with this string
//		am,pm- override strings for am/pm in times
//		locale- override the locale used to determine formatting rules
//

	if(typeof options == "string"){
		dojo.deprecated("dojo.date.format", "To format dates with POSIX-style strings, please use dojo.date.strftime instead", "0.5");
		return dojo.date.strftime(dateObject, options);
	}

	// Format a pattern without literals
	function formatPattern(dateObject, pattern){
		return pattern.replace(/[a-zA-Z]+/g, function(match){
			var s;
			var c = match.charAt(0);
			var l = match.length;
			var pad;
			var widthList = ["abbr", "wide", "narrow"];
			switch(c){
				case 'G':
					if(l>3){dojo.unimplemented("Era format not implemented");}
					s = info.eras[dateObject.getFullYear() < 0 ? 1 : 0];
					break;
				case 'y':
					s = dateObject.getFullYear();
					switch(l){
						case 1:
							break;
						case 2:
							s = String(s).substr(-2);
							break;
						default:
							pad = true;
					}
					break;
				case 'Q':
				case 'q':
					s = Math.ceil((dateObject.getMonth()+1)/3);
					switch(l){
						case 1: case 2:
							pad = true;
							break;
						case 3:
						case 4:
							dojo.unimplemented("Quarter format not implemented");
					}
					break;
				case 'M':
				case 'L':
					var m = dateObject.getMonth();
					var width;
					switch(l){
						case 1: case 2:
							s = m+1; pad = true;
							break;
						case 3: case 4: case 5:
							width = widthList[l-3];
							break;
					}
					if(width){
						var type = (c == "L") ? "standalone" : "format";
						var prop = ["months",type,width].join("-");
						s = info[prop][m];
					}
					break;
				case 'w':
					var firstDay = 0;
					s = dojo.date.getWeekOfYear(dateObject, firstDay); pad = true;
					break;
				case 'd':
					s = dateObject.getDate(); pad = true;
					break;
				case 'D':
					s = dojo.date.getDayOfYear(dateObject); pad = true;
					break;
				case 'E':
				case 'e':
				case 'c': // REVIEW: don't see this in the spec?
					var d = dateObject.getDay();
					var width;
					switch(l){
						case 1: case 2:
							if(c == 'e'){
								var first = dojo.date.getFirstDayOfWeek(options.locale);
								d = (d-first+7)%7;
							}
							if(c != 'c'){
								s = d+1; pad = true;
								break;
							}
							// else fallthrough...
						case 3: case 4: case 5:
							width = widthList[l-3];
							break;
					}
					if(width){
						var type = (c == "c") ? "standalone" : "format";
						var prop = ["days",type,width].join("-");
						s = info[prop][d];
					}
					break;
				case 'a':
					var timePeriod = (dateObject.getHours() < 12) ? 'am' : 'pm';
					s = info[timePeriod];
					break;
				case 'h':
				case 'H':
				case 'K':
				case 'k':
					var h = dateObject.getHours();
					// strange choices in the date format make it impossible to write this succinctly
					switch (c) {
						case 'h': // 1-12
							s = (h % 12) || 12;
							break;
						case 'H': // 0-23
							s = h;
							break;
						case 'K': // 0-11
							s = (h % 12);
							break;
						case 'k': // 1-24
							s = h || 24;
							break;
					}
					pad = true;
					break;
				case 'm':
					s = dateObject.getMinutes(); pad = true;
					break;
				case 's':
					s = dateObject.getSeconds(); pad = true;
					break;
				case 'S':
					s = Math.round(dateObject.getMilliseconds() * Math.pow(10, l-3));
					break;
				case 'v': // FIXME: don't know what this is. seems to be same as z?
				case 'z':
					// We only have one timezone to offer; the one from the browser
					s = dojo.date.getTimezoneName(dateObject);
					if(s){break;}
					l=4;
					// fallthrough... use GMT if tz not available
				case 'Z':
					var offset = dateObject.getTimezoneOffset();
					var tz = [
						(offset<=0 ? "+" : "-"),
						dojo.string.pad(Math.floor(Math.abs(offset)/60), 2),
						dojo.string.pad(Math.abs(offset)% 60, 2)
					];
					if(l==4){
						tz.splice(0, 0, "GMT");
						tz.splice(3, 0, ":");
					}
					s = tz.join("");
					break;
				case 'Y':
				case 'u':
				case 'W':
				case 'F':
				case 'g':
				case 'A':
					dojo.debug(match+" modifier not yet implemented");
					s = "?";
					break;
				default:
					dojo.raise("dojo.date.format: invalid pattern char: "+pattern);
			}
			if(pad){ s = dojo.string.pad(s, l); }
			return s;
		});
	}

	options = options || {};

	var locale = dojo.hostenv.normalizeLocale(options.locale);
	var formatLength = options.formatLength || 'full';
	var info = dojo.date._getGregorianBundle(locale);
	var str = [];
	var sauce = dojo.lang.curry(this, formatPattern, dateObject);
	if(options.selector != "timeOnly"){
		var datePattern = options.datePattern || info["dateFormat-"+formatLength];
		if(datePattern){str.push(_processPattern(datePattern, sauce));}
	}
	if(options.selector != "dateOnly"){
		var timePattern = options.timePattern || info["timeFormat-"+formatLength];
		if(timePattern){str.push(_processPattern(timePattern, sauce));}
	}
	var result = str.join(" "); //TODO: use locale-specific pattern to assemble date + time
	return result; /*String*/
};

dojo.date.parse = function(/*String*/value, /*Object?*/options){
//
// summary:
//		Convert a properly formatted string to a primitive Date object,
//		using locale-specific settings.
//
// description:
//		Create a Date object from a string using a known localized pattern.
//		By default, this method parses looking for both date and time in the string.
//		Formatting patterns are chosen appropriate to the locale.  Different
//		formatting lengths may be chosen, with "full" used by default.
//		Custom patterns may be used or registered with translations using
//		the addCustomBundle method.
//		Formatting patterns are implemented using the syntax described at
//		http://www.unicode.org/reports/tr35/#Date_Format_Patterns
//
// value:
//		A string representation of a date
//
// options: object {selector: string, formatLength: string, datePattern: string, timePattern: string, locale: string, strict: boolean}
//		selector- choice of timeOnly, dateOnly, dateTime (default: dateOnly)
//		formatLength- choice of long, short, medium or full (plus any custom additions).  Defaults to 'full'
//		datePattern,timePattern- override pattern with this string
//		am,pm- override strings for am/pm in times
//		locale- override the locale used to determine formatting rules
//		strict- strict parsing, off by default
//

	options = options || {};
	var locale = dojo.hostenv.normalizeLocale(options.locale);
	var info = dojo.date._getGregorianBundle(locale);
	var formatLength = options.formatLength || 'full';
	if(!options.selector){ options.selector = 'dateOnly'; }
	var datePattern = options.datePattern || info["dateFormat-" + formatLength];
	var timePattern = options.timePattern || info["timeFormat-" + formatLength];

	var pattern;
	if(options.selector == 'dateOnly'){
		pattern = datePattern;
	}
	else if(options.selector == 'timeOnly'){
		pattern = timePattern;
	}else if(options.selector == 'dateTime'){
		pattern = datePattern + ' ' + timePattern; //TODO: use locale-specific pattern to assemble date + time
	}else{
		var msg = "dojo.date.parse: Unknown selector param passed: '" + options.selector + "'.";
		msg += " Defaulting to date pattern.";
		dojo.debug(msg);
		pattern = datePattern;
	}

	var groups = [];
	var dateREString = _processPattern(pattern, dojo.lang.curry(this, _buildDateTimeRE, groups, info, options));
	var dateRE = new RegExp("^" + dateREString + "$");

	var match = dateRE.exec(value);
	if(!match){
		return null;
	}

	var widthList = ['abbr', 'wide', 'narrow'];
	//1972 is a leap year.  We want to avoid Feb 29 rolling over into Mar 1,
	//in the cases where the year is parsed after the month and day.
	var result = new Date(1972, 0);
	var expected = {};
	for(var i=1; i<match.length; i++){
		var grp=groups[i-1];
		var l=grp.length;
		var v=match[i];
		switch(grp.charAt(0)){
			case 'y':
				if(l != 2){
					//interpret year literally, so '5' would be 5 A.D.
					result.setFullYear(v);
					expected.year = v;
				}else{
					if(v<100){
						v = Number(v);
						//choose century to apply, according to a sliding window
						//of 80 years before and 20 years after present year
						var year = '' + new Date().getFullYear();
						var century = year.substring(0, 2) * 100;
						var yearPart = Number(year.substring(2, 4));
						var cutoff = Math.min(yearPart + 20, 99);
						var num = (v < cutoff) ? century + v : century - 100 + v;
						result.setFullYear(num);
						expected.year = num;
					}else{
						//we expected 2 digits and got more...
						if(options.strict){
							return null;
						}
						//interpret literally, so '150' would be 150 A.D.
						//also tolerate '1950', if 'yyyy' input passed to 'yy' format
						result.setFullYear(v);
						expected.year = v;
					}
				}
				break;
			case 'M':
				if (l>2) {
					if(!options.strict){
						//Tolerate abbreviating period in month part
						v = v.replace(/\./g,'');
						//Case-insensitive
						v = v.toLowerCase();
					}
					var months = info['months-format-' + widthList[l-3]].concat();
					for (var j=0; j<months.length; j++){
						if(!options.strict){
							//Case-insensitive
							months[j] = months[j].toLowerCase();
						}
						if(v == months[j]){
							result.setMonth(j);
							expected.month = j;
							break;
						}
					}
					if(j==months.length){
						dojo.debug("dojo.date.parse: Could not parse month name: '" + v + "'.");
						return null;
					}
				}else{
					result.setMonth(v-1);
					expected.month = v-1;
				}
				break;
			case 'E':
			case 'e':
				if(!options.strict){
					//Case-insensitive
					v = v.toLowerCase();
				}
				var days = info['days-format-' + widthList[l-3]].concat();
				for (var j=0; j<days.length; j++){
					if(!options.strict){
						//Case-insensitive
						days[j] = days[j].toLowerCase();
					}
					if(v == days[j]){
						//TODO: not sure what to actually do with this input,
						//in terms of setting something on the Date obj...?
						//without more context, can't affect the actual date
						break;
					}
				}
				if(j==days.length){
					dojo.debug("dojo.date.parse: Could not parse weekday name: '" + v + "'.");
					return null;
				}
				break;	
			case 'd':
				result.setDate(v);
				expected.date = v;
				break;
			case 'a': //am/pm
				var am = options.am || info.am;
				var pm = options.pm || info.pm;
				if(!options.strict){
					v = v.replace(/\./g,'').toLowerCase();
					am = am.replace(/\./g,'').toLowerCase();
					pm = pm.replace(/\./g,'').toLowerCase();
				}
				if(options.strict && v != am && v != pm){
					dojo.debug("dojo.date.parse: Could not parse am/pm part.");
					return null;
				}
				var hours = result.getHours();
				if(v == pm && hours < 12){
					result.setHours(hours + 12); //e.g., 3pm -> 15
				} else if(v == am && hours == 12){
					result.setHours(0); //12am -> 0
				}
				break;
			case 'K': //hour (1-24)
				if(v==24){v=0;}
				// fallthrough...
			case 'h': //hour (1-12)
			case 'H': //hour (0-23)
			case 'k': //hour (0-11)
				//TODO: strict bounds checking, padding
				if(v>23){
					dojo.debug("dojo.date.parse: Illegal hours value");
					return null;
				}

				//in the 12-hour case, adjusting for am/pm requires the 'a' part
				//which for now we will assume always comes after the 'h' part
				result.setHours(v);
				break;
			case 'm': //minutes
				result.setMinutes(v);
				break;
			case 's': //seconds
				result.setSeconds(v);
				break;
			case 'S': //milliseconds
				result.setMilliseconds(v);
				break;
			default:
				dojo.unimplemented("dojo.date.parse: unsupported pattern char=" + grp.charAt(0));
		}
	}

	//validate parse date fields versus input date fields
	if(expected.year && result.getFullYear() != expected.year){
		dojo.debug("Parsed year: '" + result.getFullYear() + "' did not match input year: '" + expected.year + "'.");
		return null;
	}
	if(expected.month && result.getMonth() != expected.month){
		dojo.debug("Parsed month: '" + result.getMonth() + "' did not match input month: '" + expected.month + "'.");
		return null;
	}
	if(expected.date && result.getDate() != expected.date){
		dojo.debug("Parsed day of month: '" + result.getDate() + "' did not match input day of month: '" + expected.date + "'.");
		return null;
	}

	//TODO: implement a getWeekday() method in order to test 
	//validity of input strings containing 'EEE' or 'EEEE'...

	return result; /*Date*/
};

function _processPattern(pattern, applyPattern, applyLiteral, applyAll){
	// Process a pattern with literals in it
	// Break up on single quotes, treat every other one as a literal, except '' which becomes '
	var identity = function(x){return x;};
	applyPattern = applyPattern || identity;
	applyLiteral = applyLiteral || identity;
	applyAll = applyAll || identity;

	//split on single quotes (which escape literals in date format strings) 
	//but preserve escaped single quotes (e.g., o''clock)
	var chunks = pattern.match(/(''|[^'])+/g); 
	var literal = false;

	for(var i=0; i<chunks.length; i++){
		if(!chunks[i]){
			chunks[i]='';
		} else {
			chunks[i]=(literal ? applyLiteral : applyPattern)(chunks[i]);
			literal = !literal;
		}
	}
	return applyAll(chunks.join(''));
}

function _buildDateTimeRE(groups, info, options, pattern){
	return pattern.replace(/[a-zA-Z]+/g, function(match){
		// Build a simple regexp without parenthesis, which would ruin the match list
		var s;
		var c = match.charAt(0);
		var l = match.length;
		switch(c){
			case 'y':
				s = '\\d' + ((l==2) ? '{2,4}' : '+');
				break;
			case 'M':
				s = (l>2) ? '\\S+' : '\\d{1,2}';
				break;
			case 'd':
				s = '\\d{1,2}';
				break;
		    case 'E':
				s = '\\S+';
				break;
			case 'h': 
			case 'H': 
			case 'K': 
			case 'k':
				s = '\\d{1,2}';
				break;
			case 'm':
			case 's':
				s = '[0-5]\\d';
				break;
			case 'S':
				s = '\\d{1,3}';
				break;
			case 'a':
				var am = options.am || info.am || 'AM';
				var pm = options.pm || info.pm || 'PM';
				if(options.strict){
					s = am + '|' + pm;
				}else{
					s = am;
					s += (am != am.toLowerCase()) ? '|' + am.toLowerCase() : '';
					s += '|';
					s += (pm != pm.toLowerCase()) ? pm + '|' + pm.toLowerCase() : pm;
				}
				break;
			default:
				dojo.unimplemented("parse of date format, pattern=" + pattern);
		}

		if(groups){ groups.push(match); }

//FIXME: replace whitespace within final regexp with more flexible whitespace match instead?
		//tolerate whitespace
		return '\\s*(' + s + ')\\s*';
	});
}
})();

//TODO: try to common strftime and format code somehow?

dojo.date.strftime = function(/*Date*/dateObject, /*String*/format, /*String?*/locale){
//
// summary:
//		Formats the date object using the specifications of the POSIX strftime function
//
// description:
//		see <http://www.opengroup.org/onlinepubs/007908799/xsh/strftime.html>

	// zero pad
	var padChar = null;
	function _(s, n){
		return dojo.string.pad(s, n || 2, padChar || "0");
	}

	var info = dojo.date._getGregorianBundle(locale);

	function $(property){
		switch (property){
			case "a": // abbreviated weekday name according to the current locale
				return dojo.date.getDayShortName(dateObject, locale);

			case "A": // full weekday name according to the current locale
				return dojo.date.getDayName(dateObject, locale);

			case "b":
			case "h": // abbreviated month name according to the current locale
				return dojo.date.getMonthShortName(dateObject, locale);
				
			case "B": // full month name according to the current locale
				return dojo.date.getMonthName(dateObject, locale);
				
			case "c": // preferred date and time representation for the current
				      // locale
				return dojo.date.format(dateObject, {locale: locale});

			case "C": // century number (the year divided by 100 and truncated
				      // to an integer, range 00 to 99)
				return _(Math.floor(dateObject.getFullYear()/100));
				
			case "d": // day of the month as a decimal number (range 01 to 31)
				return _(dateObject.getDate());
				
			case "D": // same as %m/%d/%y
				return $("m") + "/" + $("d") + "/" + $("y");
					
			case "e": // day of the month as a decimal number, a single digit is
				      // preceded by a space (range ' 1' to '31')
				if(padChar == null){ padChar = " "; }
				return _(dateObject.getDate());
			
			case "f": // month as a decimal number, a single digit is
							// preceded by a space (range ' 1' to '12')
				if(padChar == null){ padChar = " "; }
				return _(dateObject.getMonth()+1);				
			
			case "g": // like %G, but without the century.
				break;
			
			case "G": // The 4-digit year corresponding to the ISO week number
				      // (see %V).  This has the same format and value as %Y,
				      // except that if the ISO week number belongs to the
				      // previous or next year, that year is used instead.
				dojo.unimplemented("unimplemented modifier 'G'");
				break;
			
			case "F": // same as %Y-%m-%d
				return $("Y") + "-" + $("m") + "-" + $("d");
				
			case "H": // hour as a decimal number using a 24-hour clock (range
				      // 00 to 23)
				return _(dateObject.getHours());
				
			case "I": // hour as a decimal number using a 12-hour clock (range
				      // 01 to 12)
				return _(dateObject.getHours() % 12 || 12);
				
			case "j": // day of the year as a decimal number (range 001 to 366)
				return _(dojo.date.getDayOfYear(dateObject), 3);
				
			case "k": // Hour as a decimal number using a 24-hour clock (range
					  // 0 to 23 (space-padded))
				if (padChar == null) { padChar = " "; }
				return _(dateObject.getHours());

			case "l": // Hour as a decimal number using a 12-hour clock (range
					  // 1 to 12 (space-padded))
				if (padChar == null) { padChar = " "; }
				return _(dateObject.getHours() % 12 || 12);
			
			case "m": // month as a decimal number (range 01 to 12)
				return _(dateObject.getMonth() + 1);
				
			case "M": // minute as a decimal number
				return _(dateObject.getMinutes());
			
			case "n":
				return "\n";

			case "p": // either `am' or `pm' according to the given time value,
				      // or the corresponding strings for the current locale
				return info[dateObject.getHours() < 12 ? "am" : "pm"];
				
			case "r": // time in a.m. and p.m. notation
				return $("I") + ":" + $("M") + ":" + $("S") + " " + $("p");
				
			case "R": // time in 24 hour notation
				return $("H") + ":" + $("M");
				
			case "S": // second as a decimal number
				return _(dateObject.getSeconds());

			case "t":
				return "\t";

			case "T": // current time, equal to %H:%M:%S
				return $("H") + ":" + $("M") + ":" + $("S");
				
			case "u": // weekday as a decimal number [1,7], with 1 representing
				      // Monday
				return String(dateObject.getDay() || 7);
				
			case "U": // week number of the current year as a decimal number,
				      // starting with the first Sunday as the first day of the
				      // first week
				return _(dojo.date.getWeekOfYear(dateObject));

			case "V": // week number of the year (Monday as the first day of the
				      // week) as a decimal number [01,53]. If the week containing
				      // 1 January has four or more days in the new year, then it 
				      // is considered week 1. Otherwise, it is the last week of 
				      // the previous year, and the next week is week 1.
				return _(dojo.date.getIsoWeekOfYear(dateObject));
				
			case "W": // week number of the current year as a decimal number,
				      // starting with the first Monday as the first day of the
				      // first week
				return _(dojo.date.getWeekOfYear(dateObject, 1));
				
			case "w": // day of the week as a decimal, Sunday being 0
				return String(dateObject.getDay());

			case "x": // preferred date representation for the current locale
				      // without the time
				return dojo.date.format(dateObject, {selector:'dateOnly', locale:locale});

			case "X": // preferred time representation for the current locale
				      // without the date
				return dojo.date.format(dateObject, {selector:'timeOnly', locale:locale});

			case "y": // year as a decimal number without a century (range 00 to
				      // 99)
				return _(dateObject.getFullYear()%100);
				
			case "Y": // year as a decimal number including the century
				return String(dateObject.getFullYear());
			
			case "z": // time zone or name or abbreviation
				var timezoneOffset = dateObject.getTimezoneOffset();
				return (timezoneOffset > 0 ? "-" : "+") + 
					_(Math.floor(Math.abs(timezoneOffset)/60)) + ":" +
					_(Math.abs(timezoneOffset)%60);

			case "Z": // time zone or name or abbreviation
				return dojo.date.getTimezoneName(dateObject);
			
			case "%":
				return "%";
		}
	}

	// parse the formatting string and construct the resulting string
	var string = "";
	var i = 0;
	var index = 0;
	var switchCase = null;
	while ((index = format.indexOf("%", i)) != -1){
		string += format.substring(i, index++);
		
		// inspect modifier flag
		switch (format.charAt(index++)) {
			case "_": // Pad a numeric result string with spaces.
				padChar = " "; break;
			case "-": // Do not pad a numeric result string.
				padChar = ""; break;
			case "0": // Pad a numeric result string with zeros.
				padChar = "0"; break;
			case "^": // Convert characters in result string to uppercase.
				switchCase = "upper"; break;
			case "*": // Convert characters in result string to lowercase
				switchCase = "lower"; break;
			case "#": // Swap the case of the result string.
				switchCase = "swap"; break;
			default: // no modifier flag so decrement the index
				padChar = null; index--; break;
		}

		// toggle case if a flag is set
		var property = $(format.charAt(index++));
		switch (switchCase){
			case "upper":
				property = property.toUpperCase();
				break;
			case "lower":
				property = property.toLowerCase();
				break;
			case "swap": // Upper to lower, and versey-vicea
				var compareString = property.toLowerCase();
				var swapString = '';
				var j = 0;
				var ch = '';
				while (j < property.length){
					ch = property.charAt(j);
					swapString += (ch == compareString.charAt(j)) ?
						ch.toUpperCase() : ch.toLowerCase();
					j++;
				}
				property = swapString;
				break;
			default:
				break;
		}
		switchCase = null;
		
		string += property;
		i = index;
	}
	string += format.substring(i);
	
	return string; // String
};

(function(){
var _customFormats = [];
dojo.date.addCustomFormats = function(/*String*/packageName, /*String*/bundleName){
//
// summary:
//		Add a reference to a bundle containing localized custom formats to be
//		used by date/time formatting and parsing routines.
//
// description:
//		The user may add custom localized formats where the bundle has properties following the
//		same naming convention used by dojo for the CLDR data: dateFormat-xxxx / timeFormat-xxxx
//		The pattern string should match the format used by the CLDR.
//		See dojo.date.format for details.
//		The resources must be loaded by dojo.requireLocalization() prior to use

	_customFormats.push({pkg:packageName,name:bundleName});
};

dojo.date._getGregorianBundle = function(/*String*/locale){
	var gregorian = {};
	dojo.lang.forEach(_customFormats, function(desc){
		var bundle = dojo.i18n.getLocalization(desc.pkg, desc.name, locale);
		gregorian = dojo.lang.mixin(gregorian, bundle);
	}, this);
	return gregorian; /*Object*/
};
})();

dojo.date.addCustomFormats("dojo.i18n.calendar","gregorian");
dojo.date.addCustomFormats("dojo.i18n.calendar","gregorianExtras");

dojo.date.getNames = function(/*String*/item, /*String*/type, /*String?*/use, /*String?*/locale){
//
// summary:
//		Used to get localized strings for day or month names.
//
// item: 'months' || 'days'
// type: 'wide' || 'narrow' || 'abbr' (e.g. "Monday", "Mon", or "M" respectively, in English)
// use: 'standAlone' || 'format' (default)
// locale: override locale used to find the names

	var label;
	var lookup = dojo.date._getGregorianBundle(locale);
	var props = [item, use, type];
	if(use == 'standAlone'){
		label = lookup[props.join('-')];
	}
	props[1] = 'format';

	// return by copy so changes won't be made accidentally to the in-memory model
	return (label || lookup[props.join('-')]).concat(); /*Array*/
};

// Convenience methods

dojo.date.getDayName = function(/*Date*/dateObject, /*String?*/locale){
// summary: gets the full localized day of the week corresponding to the date object
	return dojo.date.getNames('days', 'wide', 'format', locale)[dateObject.getDay()]; /*String*/
};

dojo.date.getDayShortName = function(/*Date*/dateObject, /*String?*/locale){
// summary: gets the abbreviated localized day of the week corresponding to the date object
	return dojo.date.getNames('days', 'abbr', 'format', locale)[dateObject.getDay()]; /*String*/
};

dojo.date.getMonthName = function(/*Date*/dateObject, /*String?*/locale){
// summary: gets the full localized month name corresponding to the date object
	return dojo.date.getNames('months', 'wide', 'format', locale)[dateObject.getMonth()]; /*String*/
};

dojo.date.getMonthShortName = function(/*Date*/dateObject, /*String?*/locale){
// summary: gets the abbreviated localized month name corresponding to the date object
	return dojo.date.getNames('months', 'abbr', 'format', locale)[dateObject.getMonth()]; /*String*/
};

//FIXME: not localized
dojo.date.toRelativeString = function(/*Date*/dateObject){
// summary:
//	Returns an description in English of the date relative to the current date.  Note: this is not localized yet.  English only.
//
// description: Example returns:
//	 - "1 minute ago"
//	 - "4 minutes ago"
//	 - "Yesterday"
//	 - "2 days ago"

	var now = new Date();
	var diff = (now - dateObject) / 1000;
	var end = " ago";
	var future = false;
	if(diff < 0){
		future = true;
		end = " from now";
		diff = -diff;
	}

	if(diff < 60){
		diff = Math.round(diff);
		return diff + " second" + (diff == 1 ? "" : "s") + end;
	}
	if(diff < 60*60){
		diff = Math.round(diff/60);
		return diff + " minute" + (diff == 1 ? "" : "s") + end;
	}
	if(diff < 60*60*24){
		diff = Math.round(diff/3600);
		return diff + " hour" + (diff == 1 ? "" : "s") + end;
	}
	if(diff < 60*60*24*7){
		diff = Math.round(diff/(3600*24));
		if(diff == 1){
			return future ? "Tomorrow" : "Yesterday";
		}else{
			return diff + " days" + end;
		}
	}
	return dojo.date.format(dateObject); // String
};

//FIXME: SQL methods can probably be moved to a different module without i18n deps

dojo.date.toSql = function(/*Date*/dateObject, /*Boolean?*/noTime){
// summary:
//	Convert a Date to a SQL string
// noTime: whether to ignore the time portion of the Date.  Defaults to false.

	return dojo.date.strftime(dateObject, "%F" + !noTime ? " %T" : ""); // String
};

dojo.date.fromSql = function(/*String*/sqlDate){
// summary:
//	Convert a SQL date string to a JavaScript Date object

	var parts = sqlDate.split(/[\- :]/g);
	while(parts.length < 6){
		parts.push(0);
	}
	return new Date(parts[0], (parseInt(parts[1],10)-1), parts[2], parts[3], parts[4], parts[5]); // Date
};
