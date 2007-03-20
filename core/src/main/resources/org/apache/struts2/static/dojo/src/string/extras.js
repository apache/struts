/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.string.extras");

dojo.require("dojo.string.common");
dojo.require("dojo.lang.common");
dojo.require("dojo.lang.array");

//TODO: should we use ${} substitution syntax instead, like widgets do?
dojo.string.substituteParams = function(/*string*/template, /* object - optional or ... */hash){
// summary:
//	Performs parameterized substitutions on a string. Throws an exception if any parameter is unmatched.
//
// description:
//	For example,
//		dojo.string.substituteParams("File '%{0}' is not found in directory '%{1}'.","foo.html","/temp");
//	returns
//		"File 'foo.html' is not found in directory '/temp'."
//
// template: the original string template with %{values} to be replaced
// hash: name/value pairs (type object) to provide substitutions.  Alternatively, substitutions may be
//	included as arguments 1..n to this function, corresponding to template parameters 0..n-1

	var map = (typeof hash == 'object') ? hash : dojo.lang.toArray(arguments, 1);

	return template.replace(/\%\{(\w+)\}/g, function(match, key){
		if(typeof(map[key]) != "undefined" && map[key] != null){
			return map[key];
		}
		dojo.raise("Substitution not found: " + key);
	}); // string
};

dojo.string.capitalize = function(/*string*/str){
// summary:
//	Uppercases the first letter of each word

	if(!dojo.lang.isString(str)){ return ""; }
	if(arguments.length == 0){ str = this; }

	var words = str.split(' ');
	for(var i=0; i<words.length; i++){
		words[i] = words[i].charAt(0).toUpperCase() + words[i].substring(1);
	}
	return words.join(" "); // string
}

dojo.string.isBlank = function(/*string*/str){
// summary:
//	Return true if the entire string is whitespace characters

	if(!dojo.lang.isString(str)){ return true; }
	return (dojo.string.trim(str).length == 0); // boolean
}

//FIXME: not sure exactly what encodeAscii is trying to do, or if it's working right
dojo.string.encodeAscii = function(/*string*/str){
	if(!dojo.lang.isString(str)){ return str; } // unknown
	var ret = "";
	var value = escape(str);
	var match, re = /%u([0-9A-F]{4})/i;
	while((match = value.match(re))){
		var num = Number("0x"+match[1]);
		var newVal = escape("&#" + num + ";");
		ret += value.substring(0, match.index) + newVal;
		value = value.substring(match.index+match[0].length);
	}
	ret += value.replace(/\+/g, "%2B");
	return ret; // string
}

dojo.string.escape = function(/*string*/type, /*string*/str){
// summary:
//	Adds escape sequences for special characters according to the convention of 'type'
//
// type: one of xml|html|xhtml|sql|regexp|regex|javascript|jscript|js|ascii
// str: the string to be escaped

	var args = dojo.lang.toArray(arguments, 1);
	switch(type.toLowerCase()){
		case "xml":
		case "html":
		case "xhtml":
			return dojo.string.escapeXml.apply(this, args); // string
		case "sql":
			return dojo.string.escapeSql.apply(this, args); // string
		case "regexp":
		case "regex":
			return dojo.string.escapeRegExp.apply(this, args); // string
		case "javascript":
		case "jscript":
		case "js":
			return dojo.string.escapeJavaScript.apply(this, args); // string
		case "ascii":
			// so it's encode, but it seems useful
			return dojo.string.encodeAscii.apply(this, args); // string
		default:
			return str; // string
	}
}

dojo.string.escapeXml = function(/*string*/str, /*boolean*/noSingleQuotes){
//summary:
//	Adds escape sequences for special characters in XML: &<>"'
//  Optionally skips escapes for single quotes

	str = str.replace(/&/gm, "&amp;").replace(/</gm, "&lt;")
		.replace(/>/gm, "&gt;").replace(/"/gm, "&quot;");
	if(!noSingleQuotes){ str = str.replace(/'/gm, "&#39;"); }
	return str; // string
}

dojo.string.escapeSql = function(/*string*/str){
//summary:
//	Adds escape sequences for single quotes in SQL expressions

	return str.replace(/'/gm, "''"); //string
}

dojo.string.escapeRegExp = function(/*string*/str){
//summary:
//	Adds escape sequences for special characters in regular expressions

	return str.replace(/\\/gm, "\\\\").replace(/([\f\b\n\t\r[\^$|?*+(){}])/gm, "\\$1"); // string
}

//FIXME: should this one also escape backslash?
dojo.string.escapeJavaScript = function(/*string*/str){
//summary:
//	Adds escape sequences for single and double quotes as well
//	as non-visible characters in JavaScript string literal expressions

	return str.replace(/(["'\f\b\n\t\r])/gm, "\\$1"); // string
}

//FIXME: looks a lot like escapeJavaScript, just adds quotes? deprecate one?
dojo.string.escapeString = function(/*string*/str){
//summary:
//	Adds escape sequences for non-visual characters, double quote and backslash
//	and surrounds with double quotes to form a valid string literal.
	return ('"' + str.replace(/(["\\])/g, '\\$1') + '"'
		).replace(/[\f]/g, "\\f"
		).replace(/[\b]/g, "\\b"
		).replace(/[\n]/g, "\\n"
		).replace(/[\t]/g, "\\t"
		).replace(/[\r]/g, "\\r"); // string
}

// TODO: make an HTML version
dojo.string.summary = function(/*string*/str, /*number*/len){
// summary:
//	Truncates 'str' after 'len' characters and appends periods as necessary so that it ends with "..."

	if(!len || str.length <= len){
		return str; // string
	}

	return str.substring(0, len).replace(/\.+$/, "") + "..."; // string
}

dojo.string.endsWith = function(/*string*/str, /*string*/end, /*boolean*/ignoreCase){
// summary:
//	Returns true if 'str' ends with 'end'

	if(ignoreCase){
		str = str.toLowerCase();
		end = end.toLowerCase();
	}
	if((str.length - end.length) < 0){
		return false; // boolean
	}
	return str.lastIndexOf(end) == str.length - end.length; // boolean
}

dojo.string.endsWithAny = function(/*string*/str /* , ... */){
// summary:
//	Returns true if 'str' ends with any of the arguments[2 -> n]

	for(var i = 1; i < arguments.length; i++) {
		if(dojo.string.endsWith(str, arguments[i])) {
			return true; // boolean
		}
	}
	return false; // boolean
}

dojo.string.startsWith = function(/*string*/str, /*string*/start, /*boolean*/ignoreCase){
// summary:
//	Returns true if 'str' starts with 'start'

	if(ignoreCase) {
		str = str.toLowerCase();
		start = start.toLowerCase();
	}
	return str.indexOf(start) == 0; // boolean
}

dojo.string.startsWithAny = function(/*string*/str /* , ... */){
// summary:
//	Returns true if 'str' starts with any of the arguments[2 -> n]

	for(var i = 1; i < arguments.length; i++) {
		if(dojo.string.startsWith(str, arguments[i])) {
			return true; // boolean
		}
	}
	return false; // boolean
}

dojo.string.has = function(/*string*/str /* , ... */) {
// summary:
//	Returns true if 'str' contains any of the arguments 2 -> n

	for(var i = 1; i < arguments.length; i++) {
		if(str.indexOf(arguments[i]) > -1){
			return true; // boolean
		}
	}
	return false; // boolean
}

dojo.string.normalizeNewlines = function(/*string*/text, /*string? (\n or \r)*/newlineChar){
// summary:
//	Changes occurences of CR and LF in text to CRLF, or if newlineChar is provided as '\n' or '\r',
//	substitutes newlineChar for occurrences of CR/LF and CRLF

	if (newlineChar == "\n"){
		text = text.replace(/\r\n/g, "\n");
		text = text.replace(/\r/g, "\n");
	} else if (newlineChar == "\r"){
		text = text.replace(/\r\n/g, "\r");
		text = text.replace(/\n/g, "\r");
	}else{
		text = text.replace(/([^\r])\n/g, "$1\r\n").replace(/\r([^\n])/g, "\r\n$1");
	}
	return text; // string
}

dojo.string.splitEscaped = function(/*string*/str, /*string of length=1*/charac){
// summary:
//	Splits 'str' into an array separated by 'charac', but skips characters escaped with a backslash

	var components = [];
	for (var i = 0, prevcomma = 0; i < str.length; i++){
		if (str.charAt(i) == '\\'){ i++; continue; }
		if (str.charAt(i) == charac){
			components.push(str.substring(prevcomma, i));
			prevcomma = i + 1;
		}
	}
	components.push(str.substr(prevcomma));
	return components; // array
}
