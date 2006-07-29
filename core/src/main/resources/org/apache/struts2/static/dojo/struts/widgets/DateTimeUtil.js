/* 
 * Struts2
 * =======
 * 
 * This is a simple DateTimeUtil used by Struts2 DatePicker and TimePicker.
 * Its a pretty crude one, and there's lots of room for improvement. Please 
 * feel free to improve it if you like.
 * 
 * It's main methods are :-
 * -  struts.widgets.DateTimeUtil.parseDate(date, format);
 * -  struts.widgets.DateTimeUtil.parseTime(date, format);
 * -  struts.widgets.DateTimeUtil.parseDateTime(date, format);
 * 
 * which parse the 'date' string using the 'format' specifed and return a
 * js Date object. If not parsing is possible, it will just return the current
 * date as a Date object.
 * 
 * version $Date$ $Id$
 */
dojo.provide("struts.widgets.DateTimeUtil");

struts.widgets.DateTimeUtil.parseDate = function(date, format) {
    var _d = new Date();
    struts.widgets.DateTimeUtil.tryToParseForDay(_d, date, format);
    struts.widgets.DateTimeUtil.tryToParseForMonth(_d, date, format);
    struts.widgets.DateTimeUtil.tryToParseForYear(_d, date, format);
    return _d;
}

struts.widgets.DateTimeUtil.parseTime = function(date, format) {
    var _d = new Date();
    struts.widgets.DateTimeUtil.tryToParseForHours(_d, date, format);
    struts.widgets.DateTimeUtil.tryToParseForMinutes(_d, date, format);
    return _d;
}

struts.widgets.DateTimeUtil.parseDateTime = function(date, format) {
    var _d = new date();
    struts.widgets.DateTimeUtil.tryToParseForDay(_d, date, format);
    struts.widgets.DateTimeUtil.tryToParseForMonth(_d, date, format);
    struts.widgets.DateTimeUtil.tryToParseForYear(_d, date, format);
    struts.widgets.DateTimeUtil.tryToParseForHours(_d, date, format);
    struts.widgets.DateTimeUtil.tryToPraseForMinutes(_d, date, format);
    return _d;
}


struts.widgets.DateTimeUtil.tryToParseForDay = function(dateObj, date, format) {
    var tmp = format;
    var _function;

	if (tmp.indexOf("#dd") > -1) {
	    tmp = tmp.replace(/#dd/g, "(\\d+)");
		_function = function(dateObject, day) {
		    dateObject.setDate(day);
		}
	}
	else if (tmp.indexOf("#d") > -1) {
	    tmp = tmp.replace(/#d/g, "(\\d+)");
	    _function = function(dateObject, day) {
	        dateObject.setDate(day);
	    }
	}
	
    if (tmp.indexOf("#M") > -1) {
		tmp = tmp.replace(/#MMMM/g, "\\w+");
		tmp = tmp.replace(/#MMM/g, "\\w+");
		tmp = tmp.replace(/#MM/g, "\\w+");
		tmp = tmp.replace(/#M/g, "\\w+");
	}

	if (tmp.indexOf("#y") > -1) {
		tmp = tmp.replace(/#yyyy/g, "\\w+");
		tmp = tmp.replace(/#yy/g, "\\w+");
		tmp = tmp.replace(/#y/g, "\\w+");
	}

	var regexp = tmp;
	var rg = new RegExp("\\b"+regexp+"\\b", "g");
	var r = rg.exec(date);
	if (r && r.length >= 1 && _function) {
	    _function(dateObj, r[1]);
	}
}


struts.widgets.DateTimeUtil.tryToParseForMonth = function(dateObj, date, format) {
    var tmp = format;
    var _function;

	if (tmp.indexOf("#MM") > -1) {
	    tmp = tmp.replace(/#MM/g, "(\\d+)");
		_function = function(dateObject, month) {
		    dateObject.setMonth(month - 1);
		}
	}
	else if (tmp.indexOf("#M") > -1) {
	    tmp = tmp.replace(/#M/g, "(\\d+)");
	    _function = function(dateObject, month) {
	        dateObject.setDate(month - 1);
	    }
	}
	
	if (tmp.indexOf("#d") > -1) {
		tmp = tmp.replace(/#dddd/g, "\\w+");
		tmp = tmp.replace(/#ddd/g, "\\w+");
		tmp = tmp.replace(/#dd/g, "\\w+");
		tmp = tmp.replace(/#d/g, "\\w+");
	}
	
	if (tmp.indexOf("#y") > -1) {
		tmp = tmp.replace(/#yyyy/g, "\\w+");
		tmp = tmp.replace(/#yy/g, "\\w+");
		tmp = tmp.replace(/#y/g, "\\w+");
	}

	var regexp = tmp;
	var rg = new RegExp("\\b"+regexp+"\\b", "g");
	var r = rg.exec(date);
	if (r && r.length >= 1 && _function) {
	    _function(dateObj, r[1]);
	}
}


struts.widgets.DateTimeUtil.tryToParseForYear = function(dateObj, date, format) {
    var tmp = format;
    var _function;

	if (tmp.indexOf("#yyyy") > -1) {
	    tmp = tmp.replace(/#yyyy/g, "(\\d+)");
		_function = function(dateObject, year) {
		    dateObject.setYear(year);
		}
	}
	else if (tmp.indexOf("#yy") > -1) {
	    tmp = tmp.replace(/#yy/g, "(\\d+)");
	    _function = function(dateObject, year) {
	        var _d = new Date();
	        var _y = _d.getFullYear().substring(0, 2)+''+year;
	        dateObject.setYear(_y);
	    }
	}
	else if (tmp.indexOf("#y") > -1) {
	    tmp = tmp.replace(/#y/g, "(\\d+)");
	    _function = function(dateObject, year) {
	        var _d = new Date();
	        var _y = _d.getFullYear().substring(0, 3)+''+year;
	        dateObject.setYear(_y);
	    }
	}
	
	if (tmp.indexOf("#d") > -1) {
		tmp = tmp.replace(/#dddd/g, "\\w+");
		tmp = tmp.replace(/#ddd/g, "\\w+");
		tmp = tmp.replace(/#dd/g, "\\w+");
		tmp = tmp.replace(/#d/g, "\\w+");
	}
	
	if (tmp.indexOf("#M") > -1) {
		tmp = tmp.replace(/#MMMM/g, "\\w+");
		tmp = tmp.replace(/#MMM/g, "\\w+");
		tmp = tmp.replace(/#MM/g, "\\w+");
		tmp = tmp.replace(/#M/g, "\\w+");
	}

	var regexp = tmp;
	var rg = new RegExp("\\b"+regexp+"\\b", "g");
	var r = rg.exec(date);
	if (r && r.length >= 1 && _function) {
	    _function(dateObj, r[1]);
	}
}


struts.widgets.DateTimeUtil.tryToParseForHours = function(dateObj, date, format) {
    var tmp = format;
    var _function;
    
    if (tmp.indexOf("#h") > -1) {
        tmp = tmp.replace(/#hh/g, "(\\d+)");
        tmp = tmp.replace(/#h/g, "(\\d+)");
        _function = function(dateObj, hour) {
            dateObj.setHours(hour);
        }
    }
    if (tmp.indexOf("#H") > -1) {
        tmp = tmp.replace(/#HH/g, "(\\d+)");
        tmp = tmp.replace(/#H/g, "(\\d+)");
        _function = function(dateObj, hour) {
            dateObj.setHours(hour);
        }
    }
    if (tmp.indexOf("#m") > -1) {
		tmp = tmp.replace(/#mm/g, "\\w+");
		tmp = tmp.replace(/#m/g, "\\w+");
	}
	if (tmp.indexOf("#T") > -1) {
	    tmp = tmp.replace(/#TT/g, "\\w+");
	    tmp = tmp.replace(/#T/g, "\\w+");
	}
	if (tmp.indexOf("#t") > -1) {
	    tmp = tmp.replace(/#tt/g, "\\w+");
	    tmp = tmp.replace(/#t/g, "\\w+");
	}
	var regexp = tmp;
	var rg = new RegExp("\\b"+tmp+"\\b", "g");
	var r = rg.exec(date);
	if (r && r.length >= 1 && _function) {
	    _function(dateObj, r[1]);
	}
}

struts.widgets.DateTimeUtil.tryToParseForMinutes = function(dateObj, date, format) {
    var tmp = format;
    var _function;
    
    if (tmp.indexOf("#m") > -1) {
        tmp = tmp.replace(/#mm/g, "(\\d+)");
        tmp = tmp.replace(/#m/g, "(\\d+)");
        _function = function(dateObj, minutes) {
            dateObj.setMinutes(minutes);
        }
    }
	if (tmp.indexOf("#H") > -1) {
	    tmp = tmp.replace(/#HH/g, "\\w+");
	    tmp = tmp.replace(/#H/g, "\\w+");
	}
	if (tmp.indexOf("#h") > -1) {
	    tmp = tmp.replace(/#hh/g, "\\w+");
	    tmp = tmp.replace(/#h/g, "\\w+");
	}
	if (tmp.indexOf("#T") > -1) {
	    tmp = tmp.replace(/#TT/g, "\\w+");
	    tmp = tmp.replace(/#T/g, "\\w+");
	}
	if (tmp.indexOf("#t") > -1) {
	    tmp = tmp.replace(/#tt/g, "\\w+");
	    tmp = tmp.replace(/#t/g, "\\w+");
	}
	var regexp = tmp;
	var rg = new RegExp("\\b"+tmp+"\\b", "g");
	var r = rg.exec(date);
	if (r && r.length >= 1 && _function) {
	    _function(dateObj, r[1]);
	}
}


