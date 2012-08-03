/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.regexp");
dojo.evalObjPath("dojo.regexp.us", true);
dojo.regexp.tld = function (flags) {
	flags = (typeof flags == "object") ? flags : {};
	if (typeof flags.allowCC != "boolean") {
		flags.allowCC = true;
	}
	if (typeof flags.allowInfra != "boolean") {
		flags.allowInfra = true;
	}
	if (typeof flags.allowGeneric != "boolean") {
		flags.allowGeneric = true;
	}
	var infraRE = "arpa";
	var genericRE = "aero|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|xxx|jobs|mobi|post";
	var ccRE = "ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|" + "bs|bt|bv|bw|by|bz|ca|cc|cd|cf|cg|ch|ci|ck|cl|cm|cn|co|cr|cu|cv|cx|cy|cz|de|dj|dk|dm|do|dz|" + "ec|ee|eg|er|eu|es|et|fi|fj|fk|fm|fo|fr|ga|gd|ge|gf|gg|gh|gi|gl|gm|gn|gp|gq|gr|gs|gt|gu|gw|" + "gy|hk|hm|hn|hr|ht|hu|id|ie|il|im|in|io|ir|is|it|je|jm|jo|jp|ke|kg|kh|ki|km|kn|kr|kw|ky|kz|" + "la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|mg|mh|mk|ml|mm|mn|mo|mp|mq|mr|ms|mt|mu|mv|mw|mx|" + "my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nu|nz|om|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|ps|pt|pw|py|qa|" + "re|ro|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sk|sl|sm|sn|sr|st|su|sv|sy|sz|tc|td|tf|tg|th|tj|tk|tm|" + "tn|to|tr|tt|tv|tw|tz|ua|ug|uk|us|uy|uz|va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|yu|za|zm|zw";
	var a = [];
	if (flags.allowInfra) {
		a.push(infraRE);
	}
	if (flags.allowGeneric) {
		a.push(genericRE);
	}
	if (flags.allowCC) {
		a.push(ccRE);
	}
	var tldRE = "";
	if (a.length > 0) {
		tldRE = "(" + a.join("|") + ")";
	}
	return tldRE;
};
dojo.regexp.ipAddress = function (flags) {
	flags = (typeof flags == "object") ? flags : {};
	if (typeof flags.allowDottedDecimal != "boolean") {
		flags.allowDottedDecimal = true;
	}
	if (typeof flags.allowDottedHex != "boolean") {
		flags.allowDottedHex = true;
	}
	if (typeof flags.allowDottedOctal != "boolean") {
		flags.allowDottedOctal = true;
	}
	if (typeof flags.allowDecimal != "boolean") {
		flags.allowDecimal = true;
	}
	if (typeof flags.allowHex != "boolean") {
		flags.allowHex = true;
	}
	if (typeof flags.allowIPv6 != "boolean") {
		flags.allowIPv6 = true;
	}
	if (typeof flags.allowHybrid != "boolean") {
		flags.allowHybrid = true;
	}
	var dottedDecimalRE = "((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])";
	var dottedHexRE = "(0[xX]0*[\\da-fA-F]?[\\da-fA-F]\\.){3}0[xX]0*[\\da-fA-F]?[\\da-fA-F]";
	var dottedOctalRE = "(0+[0-3][0-7][0-7]\\.){3}0+[0-3][0-7][0-7]";
	var decimalRE = "(0|[1-9]\\d{0,8}|[1-3]\\d{9}|4[01]\\d{8}|42[0-8]\\d{7}|429[0-3]\\d{6}|" + "4294[0-8]\\d{5}|42949[0-5]\\d{4}|429496[0-6]\\d{3}|4294967[01]\\d{2}|42949672[0-8]\\d|429496729[0-5])";
	var hexRE = "0[xX]0*[\\da-fA-F]{1,8}";
	var ipv6RE = "([\\da-fA-F]{1,4}\\:){7}[\\da-fA-F]{1,4}";
	var hybridRE = "([\\da-fA-F]{1,4}\\:){6}" + "((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])";
	var a = [];
	if (flags.allowDottedDecimal) {
		a.push(dottedDecimalRE);
	}
	if (flags.allowDottedHex) {
		a.push(dottedHexRE);
	}
	if (flags.allowDottedOctal) {
		a.push(dottedOctalRE);
	}
	if (flags.allowDecimal) {
		a.push(decimalRE);
	}
	if (flags.allowHex) {
		a.push(hexRE);
	}
	if (flags.allowIPv6) {
		a.push(ipv6RE);
	}
	if (flags.allowHybrid) {
		a.push(hybridRE);
	}
	var ipAddressRE = "";
	if (a.length > 0) {
		ipAddressRE = "(" + a.join("|") + ")";
	}
	return ipAddressRE;
};
dojo.regexp.host = function (flags) {
	flags = (typeof flags == "object") ? flags : {};
	if (typeof flags.allowIP != "boolean") {
		flags.allowIP = true;
	}
	if (typeof flags.allowLocal != "boolean") {
		flags.allowLocal = false;
	}
	if (typeof flags.allowPort != "boolean") {
		flags.allowPort = true;
	}
	var domainNameRE = "([0-9a-zA-Z]([-0-9a-zA-Z]{0,61}[0-9a-zA-Z])?\\.)+" + dojo.regexp.tld(flags);
	var portRE = (flags.allowPort) ? "(\\:" + dojo.regexp.integer({signed:false}) + ")?" : "";
	var hostNameRE = domainNameRE;
	if (flags.allowIP) {
		hostNameRE += "|" + dojo.regexp.ipAddress(flags);
	}
	if (flags.allowLocal) {
		hostNameRE += "|localhost";
	}
	return "(" + hostNameRE + ")" + portRE;
};
dojo.regexp.url = function (flags) {
	flags = (typeof flags == "object") ? flags : {};
	if (typeof flags.scheme == "undefined") {
		flags.scheme = [true, false];
	}
	var protocolRE = dojo.regexp.buildGroupRE(flags.scheme, function (q) {
		if (q) {
			return "(https?|ftps?)\\://";
		}
		return "";
	});
	var pathRE = "(/([^?#\\s/]+/)*)?([^?#\\s/]+(\\?[^?#\\s/]*)?(#[A-Za-z][\\w.:-]*)?)?";
	return protocolRE + dojo.regexp.host(flags) + pathRE;
};
dojo.regexp.emailAddress = function (flags) {
	flags = (typeof flags == "object") ? flags : {};
	if (typeof flags.allowCruft != "boolean") {
		flags.allowCruft = false;
	}
	flags.allowPort = false;
	var usernameRE = "([\\da-z]+[-._+&'])*[\\da-z]+";
	var emailAddressRE = usernameRE + "@" + dojo.regexp.host(flags);
	if (flags.allowCruft) {
		emailAddressRE = "<?(mailto\\:)?" + emailAddressRE + ">?";
	}
	return emailAddressRE;
};
dojo.regexp.emailAddressList = function (flags) {
	flags = (typeof flags == "object") ? flags : {};
	if (typeof flags.listSeparator != "string") {
		flags.listSeparator = "\\s;,";
	}
	var emailAddressRE = dojo.regexp.emailAddress(flags);
	var emailAddressListRE = "(" + emailAddressRE + "\\s*[" + flags.listSeparator + "]\\s*)*" + emailAddressRE + "\\s*[" + flags.listSeparator + "]?\\s*";
	return emailAddressListRE;
};
dojo.regexp.integer = function (flags) {
	flags = (typeof flags == "object") ? flags : {};
	if (typeof flags.signed == "undefined") {
		flags.signed = [true, false];
	}
	if (typeof flags.separator == "undefined") {
		flags.separator = "";
	} else {
		if (typeof flags.groupSize == "undefined") {
			flags.groupSize = 3;
		}
	}
	var signRE = dojo.regexp.buildGroupRE(flags.signed, function (q) {
		return q ? "[-+]" : "";
	});
	var numberRE = dojo.regexp.buildGroupRE(flags.separator, function (sep) {
		if (sep == "") {
			return "(0|[1-9]\\d*)";
		}
		var grp = flags.groupSize, grp2 = flags.groupSize2;
		if (typeof grp2 != "undefined") {
			var grp2RE = "(0|[1-9]\\d{0," + (grp2 - 1) + "}([" + sep + "]\\d{" + grp2 + "})*[" + sep + "]\\d{" + grp + "})";
			return ((grp - grp2) > 0) ? "(" + grp2RE + "|(0|[1-9]\\d{0," + (grp - 1) + "}))" : grp2RE;
		}
		return "(0|[1-9]\\d{0," + (grp - 1) + "}([" + sep + "]\\d{" + grp + "})*)";
	});
	return signRE + numberRE;
};
dojo.regexp.realNumber = function (flags) {
	flags = (typeof flags == "object") ? flags : {};
	if (typeof flags.places != "number") {
		flags.places = Infinity;
	}
	if (typeof flags.decimal != "string") {
		flags.decimal = ".";
	}
	if (typeof flags.fractional == "undefined") {
		flags.fractional = [true, false];
	}
	if (typeof flags.exponent == "undefined") {
		flags.exponent = [true, false];
	}
	if (typeof flags.eSigned == "undefined") {
		flags.eSigned = [true, false];
	}
	var integerRE = dojo.regexp.integer(flags);
	var decimalRE = dojo.regexp.buildGroupRE(flags.fractional, function (q) {
		var re = "";
		if (q && (flags.places > 0)) {
			re = "\\" + flags.decimal;
			if (flags.places == Infinity) {
				re = "(" + re + "\\d+)?";
			} else {
				re = re + "\\d{" + flags.places + "}";
			}
		}
		return re;
	});
	var exponentRE = dojo.regexp.buildGroupRE(flags.exponent, function (q) {
		if (q) {
			return "([eE]" + dojo.regexp.integer({signed:flags.eSigned}) + ")";
		}
		return "";
	});
	return integerRE + decimalRE + exponentRE;
};
dojo.regexp.currency = function (flags) {
	flags = (typeof flags == "object") ? flags : {};
	if (typeof flags.signed == "undefined") {
		flags.signed = [true, false];
	}
	if (typeof flags.symbol == "undefined") {
		flags.symbol = "$";
	}
	if (typeof flags.placement != "string") {
		flags.placement = "before";
	}
	if (typeof flags.signPlacement != "string") {
		flags.signPlacement = "before";
	}
	if (typeof flags.separator == "undefined") {
		flags.separator = ",";
	}
	if (typeof flags.fractional == "undefined" && typeof flags.cents != "undefined") {
		dojo.deprecated("dojo.regexp.currency: flags.cents", "use flags.fractional instead", "0.5");
		flags.fractional = flags.cents;
	}
	if (typeof flags.decimal != "string") {
		flags.decimal = ".";
	}
	var signRE = dojo.regexp.buildGroupRE(flags.signed, function (q) {
		if (q) {
			return "[-+]";
		}
		return "";
	});
	var symbolRE = dojo.regexp.buildGroupRE(flags.symbol, function (symbol) {
		return "\\s?" + symbol.replace(/([.$?*!=:|\\\/^])/g, "\\$1") + "\\s?";
	});
	switch (flags.signPlacement) {
	  case "before":
		symbolRE = signRE + symbolRE;
		break;
	  case "after":
		symbolRE = symbolRE + signRE;
		break;
	}
	var flagsCopy = flags;
	flagsCopy.signed = false;
	flagsCopy.exponent = false;
	var numberRE = dojo.regexp.realNumber(flagsCopy);
	var currencyRE;
	switch (flags.placement) {
	  case "before":
		currencyRE = symbolRE + numberRE;
		break;
	  case "after":
		currencyRE = numberRE + symbolRE;
		break;
	}
	switch (flags.signPlacement) {
	  case "around":
		currencyRE = "(" + currencyRE + "|" + "\\(" + currencyRE + "\\)" + ")";
		break;
	  case "begin":
		currencyRE = signRE + currencyRE;
		break;
	  case "end":
		currencyRE = currencyRE + signRE;
		break;
	}
	return currencyRE;
};
dojo.regexp.us.state = function (flags) {
	flags = (typeof flags == "object") ? flags : {};
	if (typeof flags.allowTerritories != "boolean") {
		flags.allowTerritories = true;
	}
	if (typeof flags.allowMilitary != "boolean") {
		flags.allowMilitary = true;
	}
	var statesRE = "AL|AK|AZ|AR|CA|CO|CT|DE|DC|FL|GA|HI|ID|IL|IN|IA|KS|KY|LA|ME|MD|MA|MI|MN|MS|MO|MT|" + "NE|NV|NH|NJ|NM|NY|NC|ND|OH|OK|OR|PA|RI|SC|SD|TN|TX|UT|VT|VA|WA|WV|WI|WY";
	var territoriesRE = "AS|FM|GU|MH|MP|PW|PR|VI";
	var militaryRE = "AA|AE|AP";
	if (flags.allowTerritories) {
		statesRE += "|" + territoriesRE;
	}
	if (flags.allowMilitary) {
		statesRE += "|" + militaryRE;
	}
	return "(" + statesRE + ")";
};
dojo.regexp.time = function (flags) {
	dojo.deprecated("dojo.regexp.time", "Use dojo.date.parse instead", "0.5");
	flags = (typeof flags == "object") ? flags : {};
	if (typeof flags.format == "undefined") {
		flags.format = "h:mm:ss t";
	}
	if (typeof flags.amSymbol != "string") {
		flags.amSymbol = "AM";
	}
	if (typeof flags.pmSymbol != "string") {
		flags.pmSymbol = "PM";
	}
	var timeRE = function (format) {
		format = format.replace(/([.$?*!=:|{}\(\)\[\]\\\/^])/g, "\\$1");
		var amRE = flags.amSymbol.replace(/([.$?*!=:|{}\(\)\[\]\\\/^])/g, "\\$1");
		var pmRE = flags.pmSymbol.replace(/([.$?*!=:|{}\(\)\[\]\\\/^])/g, "\\$1");
		format = format.replace("hh", "(0[1-9]|1[0-2])");
		format = format.replace("h", "([1-9]|1[0-2])");
		format = format.replace("HH", "([01][0-9]|2[0-3])");
		format = format.replace("H", "([0-9]|1[0-9]|2[0-3])");
		format = format.replace("mm", "([0-5][0-9])");
		format = format.replace("m", "([1-5][0-9]|[0-9])");
		format = format.replace("ss", "([0-5][0-9])");
		format = format.replace("s", "([1-5][0-9]|[0-9])");
		format = format.replace("t", "\\s?(" + amRE + "|" + pmRE + ")\\s?");
		return format;
	};
	return dojo.regexp.buildGroupRE(flags.format, timeRE);
};
dojo.regexp.numberFormat = function (flags) {
	flags = (typeof flags == "object") ? flags : {};
	if (typeof flags.format == "undefined") {
		flags.format = "###-###-####";
	}
	var digitRE = function (format) {
		format = format.replace(/([.$*!=:|{}\(\)\[\]\\\/^])/g, "\\$1");
		format = format.replace(/\?/g, "\\d?");
		format = format.replace(/#/g, "\\d");
		return format;
	};
	return dojo.regexp.buildGroupRE(flags.format, digitRE);
};
dojo.regexp.buildGroupRE = function (a, re) {
	if (!(a instanceof Array)) {
		return re(a);
	}
	var b = [];
	for (var i = 0; i < a.length; i++) {
		b.push(re(a[i]));
	}
	return "(" + b.join("|") + ")";
};

