/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.string.common");

dojo.string.trim = function(/* string */str, /* integer? */wh){
	//	summary
	//	Trim whitespace from str.  If wh > 0, trim from start, if wh < 0, trim from end, else both
	if(!str.replace){ return str; }
	if(!str.length){ return str; }
	var re = (wh > 0) ? (/^\s+/) : (wh < 0) ? (/\s+$/) : (/^\s+|\s+$/g);
	return str.replace(re, "");	//	string
}

dojo.string.trimStart = function(/* string */str) {
	//	summary
	//	Trim whitespace at the beginning of 'str'
	return dojo.string.trim(str, 1);	//	string
}

dojo.string.trimEnd = function(/* string */str) {
	//	summary
	//	Trim whitespace at the end of 'str'
	return dojo.string.trim(str, -1);
}

dojo.string.repeat = function(/* string */str, /* integer */count, /* string? */separator) {
	//	summary
	//	Return 'str' repeated 'count' times, optionally placing 'separator' between each rep
	var out = "";
	for(var i = 0; i < count; i++) {
		out += str;
		if(separator && i < count - 1) {
			out += separator;
		}
	}
	return out;	//	string
}

dojo.string.pad = function(/* string */str, /* integer */len/*=2*/, /* string */ c/*='0'*/, /* integer */dir/*=1*/) {
	//	summary
	//	Pad 'str' to guarantee that it is at least 'len' length with the character 'c' at either the 
	//	start (dir=1) or end (dir=-1) of the string
	var out = String(str);
	if(!c) {
		c = '0';
	}
	if(!dir) {
		dir = 1;
	}
	while(out.length < len) {
		if(dir > 0) {
			out = c + out;
		} else {
			out += c;
		}
	}
	return out;	//	string
}

dojo.string.padLeft = function(/* string */str, /* integer */len, /* string */c) {
	//	summary
	//	same as dojo.string.pad(str, len, c, 1)
	return dojo.string.pad(str, len, c, 1);	//	string
}

dojo.string.padRight = function(/* string */str, /* integer */len, /* string */c) {
	//	summary
	//	same as dojo.string.pad(str, len, c, -1)
	return dojo.string.pad(str, len, c, -1);	//	string
}
