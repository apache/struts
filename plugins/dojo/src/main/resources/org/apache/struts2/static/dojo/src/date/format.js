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
dojo.requireLocalization("dojo.i18n.calendar", "gregorian", null, "ko,zh-cn,zh,sv,ja,en,zh-tw,it,hu,nl,fi,zh-hk,fr,pt,ROOT,es,de,pt-br");
dojo.requireLocalization("dojo.i18n.calendar", "gregorianExtras", null, "zh,ROOT,ja");
(function () {
	dojo.date.format = function (dateObject, options) {
		if (typeof options == "string") {
			dojo.deprecated("dojo.date.format", "To format dates with POSIX-style strings, please use dojo.date.strftime instead", "0.5");
			return dojo.date.strftime(dateObject, options);
		}
		function formatPattern(dateObject, pattern) {
			return pattern.replace(/([a-z])\1*/ig, function (match) {
				var s;
				var c = match.charAt(0);
				var l = match.length;
				var pad;
				var widthList = ["abbr", "wide", "narrow"];
				switch (c) {
				  case "G":
					if (l > 3) {
						dojo.unimplemented("Era format not implemented");
					}
					s = info.eras[dateObject.getFullYear() < 0 ? 1 : 0];
					break;
				  case "y":
					s = dateObject.getFullYear();
					switch (l) {
					  case 1:
						break;
					  case 2:
						s = String(s).substr(-2);
						break;
					  default:
						pad = true;
					}
					break;
				  case "Q":
				  case "q":
					s = Math.ceil((dateObject.getMonth() + 1) / 3);
					switch (l) {
					  case 1:
					  case 2:
						pad = true;
						break;
					  case 3:
					  case 4:
						dojo.unimplemented("Quarter format not implemented");
					}
					break;
				  case "M":
				  case "L":
					var m = dateObject.getMonth();
					var width;
					switch (l) {
					  case 1:
					  case 2:
						s = m + 1;
						pad = true;
						break;
					  case 3:
					  case 4:
					  case 5:
						width = widthList[l - 3];
						break;
					}
					if (width) {
						var type = (c == "L") ? "standalone" : "format";
						var prop = ["months", type, width].join("-");
						s = info[prop][m];
					}
					break;
				  case "w":
					var firstDay = 0;
					s = dojo.date.getWeekOfYear(dateObject, firstDay);
					pad = true;
					break;
				  case "d":
					s = dateObject.getDate();
					pad = true;
					break;
				  case "D":
					s = dojo.date.getDayOfYear(dateObject);
					pad = true;
					break;
				  case "E":
				  case "e":
				  case "c":
					var d = dateObject.getDay();
					var width;
					switch (l) {
					  case 1:
					  case 2:
						if (c == "e") {
							var first = dojo.date.getFirstDayOfWeek(options.locale);
							d = (d - first + 7) % 7;
						}
						if (c != "c") {
							s = d + 1;
							pad = true;
							break;
						}
					  case 3:
					  case 4:
					  case 5:
						width = widthList[l - 3];
						break;
					}
					if (width) {
						var type = (c == "c") ? "standalone" : "format";
						var prop = ["days", type, width].join("-");
						s = info[prop][d];
					}
					break;
				  case "a":
					var timePeriod = (dateObject.getHours() < 12) ? "am" : "pm";
					s = info[timePeriod];
					break;
				  case "h":
				  case "H":
				  case "K":
				  case "k":
					var h = dateObject.getHours();
					switch (c) {
					  case "h":
						s = (h % 12) || 12;
						break;
					  case "H":
						s = h;
						break;
					  case "K":
						s = (h % 12);
						break;
					  case "k":
						s = h || 24;
						break;
					}
					pad = true;
					break;
				  case "m":
					s = dateObject.getMinutes();
					pad = true;
					break;
				  case "s":
					s = dateObject.getSeconds();
					pad = true;
					break;
				  case "S":
					s = Math.round(dateObject.getMilliseconds() * Math.pow(10, l - 3));
					break;
				  case "v":
				  case "z":
					s = dojo.date.getTimezoneName(dateObject);
					if (s) {
						break;
					}
					l = 4;
				  case "Z":
					var offset = dateObject.getTimezoneOffset();
					var tz = [(offset <= 0 ? "+" : "-"), dojo.string.pad(Math.floor(Math.abs(offset) / 60), 2), dojo.string.pad(Math.abs(offset) % 60, 2)];
					if (l == 4) {
						tz.splice(0, 0, "GMT");
						tz.splice(3, 0, ":");
					}
					s = tz.join("");
					break;
				  case "Y":
				  case "u":
				  case "W":
				  case "F":
				  case "g":
				  case "A":
					dojo.debug(match + " modifier not yet implemented");
					s = "?";
					break;
				  default:
					dojo.raise("dojo.date.format: invalid pattern char: " + pattern);
				}
				if (pad) {
					s = dojo.string.pad(s, l);
				}
				return s;
			});
		}
		options = options || {};
		var locale = dojo.hostenv.normalizeLocale(options.locale);
		var formatLength = options.formatLength || "full";
		var info = dojo.date._getGregorianBundle(locale);
		var str = [];
		var sauce = dojo.lang.curry(this, formatPattern, dateObject);
		if (options.selector != "timeOnly") {
			var datePattern = options.datePattern || info["dateFormat-" + formatLength];
			if (datePattern) {
				str.push(_processPattern(datePattern, sauce));
			}
		}
		if (options.selector != "dateOnly") {
			var timePattern = options.timePattern || info["timeFormat-" + formatLength];
			if (timePattern) {
				str.push(_processPattern(timePattern, sauce));
			}
		}
		var result = str.join(" ");
		return result;
	};
	dojo.date.parse = function (value, options) {
		options = options || {};
		var locale = dojo.hostenv.normalizeLocale(options.locale);
		var info = dojo.date._getGregorianBundle(locale);
		var formatLength = options.formatLength || "full";
		if (!options.selector) {
			options.selector = "dateOnly";
		}
		var datePattern = options.datePattern || info["dateFormat-" + formatLength];
		var timePattern = options.timePattern || info["timeFormat-" + formatLength];
		var pattern;
		if (options.selector == "dateOnly") {
			pattern = datePattern;
		} else {
			if (options.selector == "timeOnly") {
				pattern = timePattern;
			} else {
				if (options.selector == "dateTime") {
					pattern = datePattern + " " + timePattern;
				} else {
					var msg = "dojo.date.parse: Unknown selector param passed: '" + options.selector + "'.";
					msg += " Defaulting to date pattern.";
					dojo.debug(msg);
					pattern = datePattern;
				}
			}
		}
		var groups = [];
		var dateREString = _processPattern(pattern, dojo.lang.curry(this, _buildDateTimeRE, groups, info, options));
		var dateRE = new RegExp("^" + dateREString + "$");
		var match = dateRE.exec(value);
		if (!match) {
			return null;
		}
		var widthList = ["abbr", "wide", "narrow"];
		var result = new Date(1972, 0);
		var expected = {};
		for (var i = 1; i < match.length; i++) {
			var grp = groups[i - 1];
			var l = grp.length;
			var v = match[i];
			switch (grp.charAt(0)) {
			  case "y":
				if (l != 2) {
					result.setFullYear(v);
					expected.year = v;
				} else {
					if (v < 100) {
						v = Number(v);
						var year = "" + new Date().getFullYear();
						var century = year.substring(0, 2) * 100;
						var yearPart = Number(year.substring(2, 4));
						var cutoff = Math.min(yearPart + 20, 99);
						var num = (v < cutoff) ? century + v : century - 100 + v;
						result.setFullYear(num);
						expected.year = num;
					} else {
						if (options.strict) {
							return null;
						}
						result.setFullYear(v);
						expected.year = v;
					}
				}
				break;
			  case "M":
				if (l > 2) {
					if (!options.strict) {
						v = v.replace(/\./g, "");
						v = v.toLowerCase();
					}
					var months = info["months-format-" + widthList[l - 3]].concat();
					for (var j = 0; j < months.length; j++) {
						if (!options.strict) {
							months[j] = months[j].toLowerCase();
						}
						if (v == months[j]) {
							result.setMonth(j);
							expected.month = j;
							break;
						}
					}
					if (j == months.length) {
						dojo.debug("dojo.date.parse: Could not parse month name: '" + v + "'.");
						return null;
					}
				} else {
					result.setMonth(v - 1);
					expected.month = v - 1;
				}
				break;
			  case "E":
			  case "e":
				if (!options.strict) {
					v = v.toLowerCase();
				}
				var days = info["days-format-" + widthList[l - 3]].concat();
				for (var j = 0; j < days.length; j++) {
					if (!options.strict) {
						days[j] = days[j].toLowerCase();
					}
					if (v == days[j]) {
						break;
					}
				}
				if (j == days.length) {
					dojo.debug("dojo.date.parse: Could not parse weekday name: '" + v + "'.");
					return null;
				}
				break;
			  case "d":
				result.setDate(v);
				expected.date = v;
				break;
			  case "a":
				var am = options.am || info.am;
				var pm = options.pm || info.pm;
				if (!options.strict) {
					v = v.replace(/\./g, "").toLowerCase();
					am = am.replace(/\./g, "").toLowerCase();
					pm = pm.replace(/\./g, "").toLowerCase();
				}
				if (options.strict && v != am && v != pm) {
					dojo.debug("dojo.date.parse: Could not parse am/pm part.");
					return null;
				}
				var hours = result.getHours();
				if (v == pm && hours < 12) {
					result.setHours(hours + 12);
				} else {
					if (v == am && hours == 12) {
						result.setHours(0);
					}
				}
				break;
			  case "K":
				if (v == 24) {
					v = 0;
				}
			  case "h":
			  case "H":
			  case "k":
				if (v > 23) {
					dojo.debug("dojo.date.parse: Illegal hours value");
					return null;
				}
				result.setHours(v);
				break;
			  case "m":
				result.setMinutes(v);
				break;
			  case "s":
				result.setSeconds(v);
				break;
			  case "S":
				result.setMilliseconds(v);
				break;
			  default:
				dojo.unimplemented("dojo.date.parse: unsupported pattern char=" + grp.charAt(0));
			}
		}
		if (expected.year && result.getFullYear() != expected.year) {
			dojo.debug("Parsed year: '" + result.getFullYear() + "' did not match input year: '" + expected.year + "'.");
			return null;
		}
		if (expected.month && result.getMonth() != expected.month) {
			dojo.debug("Parsed month: '" + result.getMonth() + "' did not match input month: '" + expected.month + "'.");
			return null;
		}
		if (expected.date && result.getDate() != expected.date) {
			dojo.debug("Parsed day of month: '" + result.getDate() + "' did not match input day of month: '" + expected.date + "'.");
			return null;
		}
		return result;
	};
	function _processPattern(pattern, applyPattern, applyLiteral, applyAll) {
		var identity = function (x) {
			return x;
		};
		applyPattern = applyPattern || identity;
		applyLiteral = applyLiteral || identity;
		applyAll = applyAll || identity;
		var chunks = pattern.match(/(''|[^'])+/g);
		var literal = false;
		for (var i = 0; i < chunks.length; i++) {
			if (!chunks[i]) {
				chunks[i] = "";
			} else {
				chunks[i] = (literal ? applyLiteral : applyPattern)(chunks[i]);
				literal = !literal;
			}
		}
		return applyAll(chunks.join(""));
	}
	function _buildDateTimeRE(groups, info, options, pattern) {
		return pattern.replace(/([a-z])\1*/ig, function (match) {
			var s;
			var c = match.charAt(0);
			var l = match.length;
			switch (c) {
			  case "y":
				s = "\\d" + ((l == 2) ? "{2,4}" : "+");
				break;
			  case "M":
				s = (l > 2) ? "\\S+" : "\\d{1,2}";
				break;
			  case "d":
				s = "\\d{1,2}";
				break;
			  case "E":
				s = "\\S+";
				break;
			  case "h":
			  case "H":
			  case "K":
			  case "k":
				s = "\\d{1,2}";
				break;
			  case "m":
			  case "s":
				s = "[0-5]\\d";
				break;
			  case "S":
				s = "\\d{1,3}";
				break;
			  case "a":
				var am = options.am || info.am || "AM";
				var pm = options.pm || info.pm || "PM";
				if (options.strict) {
					s = am + "|" + pm;
				} else {
					s = am;
					s += (am != am.toLowerCase()) ? "|" + am.toLowerCase() : "";
					s += "|";
					s += (pm != pm.toLowerCase()) ? pm + "|" + pm.toLowerCase() : pm;
				}
				break;
			  default:
				dojo.unimplemented("parse of date format, pattern=" + pattern);
			}
			if (groups) {
				groups.push(match);
			}
			return "\\s*(" + s + ")\\s*";
		});
	}
})();
dojo.date.strftime = function (dateObject, format, locale) {
	var padChar = null;
	function _(s, n) {
		return dojo.string.pad(s, n || 2, padChar || "0");
	}
	var info = dojo.date._getGregorianBundle(locale);
	function $(property) {
		switch (property) {
		  case "a":
			return dojo.date.getDayShortName(dateObject, locale);
		  case "A":
			return dojo.date.getDayName(dateObject, locale);
		  case "b":
		  case "h":
			return dojo.date.getMonthShortName(dateObject, locale);
		  case "B":
			return dojo.date.getMonthName(dateObject, locale);
		  case "c":
			return dojo.date.format(dateObject, {locale:locale});
		  case "C":
			return _(Math.floor(dateObject.getFullYear() / 100));
		  case "d":
			return _(dateObject.getDate());
		  case "D":
			return $("m") + "/" + $("d") + "/" + $("y");
		  case "e":
			if (padChar == null) {
				padChar = " ";
			}
			return _(dateObject.getDate());
		  case "f":
			if (padChar == null) {
				padChar = " ";
			}
			return _(dateObject.getMonth() + 1);
		  case "g":
			break;
		  case "G":
			dojo.unimplemented("unimplemented modifier 'G'");
			break;
		  case "F":
			return $("Y") + "-" + $("m") + "-" + $("d");
		  case "H":
			return _(dateObject.getHours());
		  case "I":
			return _(dateObject.getHours() % 12 || 12);
		  case "j":
			return _(dojo.date.getDayOfYear(dateObject), 3);
		  case "k":
			if (padChar == null) {
				padChar = " ";
			}
			return _(dateObject.getHours());
		  case "l":
			if (padChar == null) {
				padChar = " ";
			}
			return _(dateObject.getHours() % 12 || 12);
		  case "m":
			return _(dateObject.getMonth() + 1);
		  case "M":
			return _(dateObject.getMinutes());
		  case "n":
			return "\n";
		  case "p":
			return info[dateObject.getHours() < 12 ? "am" : "pm"];
		  case "r":
			return $("I") + ":" + $("M") + ":" + $("S") + " " + $("p");
		  case "R":
			return $("H") + ":" + $("M");
		  case "S":
			return _(dateObject.getSeconds());
		  case "t":
			return "\t";
		  case "T":
			return $("H") + ":" + $("M") + ":" + $("S");
		  case "u":
			return String(dateObject.getDay() || 7);
		  case "U":
			return _(dojo.date.getWeekOfYear(dateObject));
		  case "V":
			return _(dojo.date.getIsoWeekOfYear(dateObject));
		  case "W":
			return _(dojo.date.getWeekOfYear(dateObject, 1));
		  case "w":
			return String(dateObject.getDay());
		  case "x":
			return dojo.date.format(dateObject, {selector:"dateOnly", locale:locale});
		  case "X":
			return dojo.date.format(dateObject, {selector:"timeOnly", locale:locale});
		  case "y":
			return _(dateObject.getFullYear() % 100);
		  case "Y":
			return String(dateObject.getFullYear());
		  case "z":
			var timezoneOffset = dateObject.getTimezoneOffset();
			return (timezoneOffset > 0 ? "-" : "+") + _(Math.floor(Math.abs(timezoneOffset) / 60)) + ":" + _(Math.abs(timezoneOffset) % 60);
		  case "Z":
			return dojo.date.getTimezoneName(dateObject);
		  case "%":
			return "%";
		}
	}
	var string = "";
	var i = 0;
	var index = 0;
	var switchCase = null;
	while ((index = format.indexOf("%", i)) != -1) {
		string += format.substring(i, index++);
		switch (format.charAt(index++)) {
		  case "_":
			padChar = " ";
			break;
		  case "-":
			padChar = "";
			break;
		  case "0":
			padChar = "0";
			break;
		  case "^":
			switchCase = "upper";
			break;
		  case "*":
			switchCase = "lower";
			break;
		  case "#":
			switchCase = "swap";
			break;
		  default:
			padChar = null;
			index--;
			break;
		}
		var property = $(format.charAt(index++));
		switch (switchCase) {
		  case "upper":
			property = property.toUpperCase();
			break;
		  case "lower":
			property = property.toLowerCase();
			break;
		  case "swap":
			var compareString = property.toLowerCase();
			var swapString = "";
			var j = 0;
			var ch = "";
			while (j < property.length) {
				ch = property.charAt(j);
				swapString += (ch == compareString.charAt(j)) ? ch.toUpperCase() : ch.toLowerCase();
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
	return string;
};
(function () {
	var _customFormats = [];
	dojo.date.addCustomFormats = function (packageName, bundleName) {
		_customFormats.push({pkg:packageName, name:bundleName});
	};
	dojo.date._getGregorianBundle = function (locale) {
		var gregorian = {};
		dojo.lang.forEach(_customFormats, function (desc) {
			var bundle = dojo.i18n.getLocalization(desc.pkg, desc.name, locale);
			gregorian = dojo.lang.mixin(gregorian, bundle);
		}, this);
		return gregorian;
	};
})();
dojo.date.addCustomFormats("dojo.i18n.calendar", "gregorian");
dojo.date.addCustomFormats("dojo.i18n.calendar", "gregorianExtras");
dojo.date.getNames = function (item, type, use, locale) {
	var label;
	var lookup = dojo.date._getGregorianBundle(locale);
	var props = [item, use, type];
	if (use == "standAlone") {
		label = lookup[props.join("-")];
	}
	props[1] = "format";
	return (label || lookup[props.join("-")]).concat();
};
dojo.date.getDayName = function (dateObject, locale) {
	return dojo.date.getNames("days", "wide", "format", locale)[dateObject.getDay()];
};
dojo.date.getDayShortName = function (dateObject, locale) {
	return dojo.date.getNames("days", "abbr", "format", locale)[dateObject.getDay()];
};
dojo.date.getMonthName = function (dateObject, locale) {
	return dojo.date.getNames("months", "wide", "format", locale)[dateObject.getMonth()];
};
dojo.date.getMonthShortName = function (dateObject, locale) {
	return dojo.date.getNames("months", "abbr", "format", locale)[dateObject.getMonth()];
};
dojo.date.toRelativeString = function (dateObject) {
	var now = new Date();
	var diff = (now - dateObject) / 1000;
	var end = " ago";
	var future = false;
	if (diff < 0) {
		future = true;
		end = " from now";
		diff = -diff;
	}
	if (diff < 60) {
		diff = Math.round(diff);
		return diff + " second" + (diff == 1 ? "" : "s") + end;
	}
	if (diff < 60 * 60) {
		diff = Math.round(diff / 60);
		return diff + " minute" + (diff == 1 ? "" : "s") + end;
	}
	if (diff < 60 * 60 * 24) {
		diff = Math.round(diff / 3600);
		return diff + " hour" + (diff == 1 ? "" : "s") + end;
	}
	if (diff < 60 * 60 * 24 * 7) {
		diff = Math.round(diff / (3600 * 24));
		if (diff == 1) {
			return future ? "Tomorrow" : "Yesterday";
		} else {
			return diff + " days" + end;
		}
	}
	return dojo.date.format(dateObject);
};
dojo.date.toSql = function (dateObject, noTime) {
	return dojo.date.strftime(dateObject, "%F" + !noTime ? " %T" : "");
};
dojo.date.fromSql = function (sqlDate) {
	var parts = sqlDate.split(/[\- :]/g);
	while (parts.length < 6) {
		parts.push(0);
	}
	return new Date(parts[0], (parseInt(parts[1], 10) - 1), parts[2], parts[3], parts[4], parts[5]);
};

