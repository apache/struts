/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.hostenv.name_ = "spidermonkey";
dojo.hostenv.println = print;
dojo.hostenv.exit = function (exitcode) {
	quit(exitcode);
};
dojo.hostenv.getVersion = function () {
	return version();
};
if (typeof line2pc == "undefined") {
	dojo.raise("attempt to use SpiderMonkey host environment when no 'line2pc' global");
}
function dj_spidermonkey_current_file(depth) {
	var s = "";
	try {
		throw Error("whatever");
	}
	catch (e) {
		s = e.stack;
	}
	var matches = s.match(/[^@]*\.js/gi);
	if (!matches) {
		dojo.raise("could not parse stack string: '" + s + "'");
	}
	var fname = (typeof depth != "undefined" && depth) ? matches[depth + 1] : matches[matches.length - 1];
	if (!fname) {
		dojo.raise("could not find file name in stack string '" + s + "'");
	}
	return fname;
}
if (!dojo.hostenv.library_script_uri_) {
	dojo.hostenv.library_script_uri_ = dj_spidermonkey_current_file(0);
}
dojo.hostenv.loadUri = function (uri) {
	var ok = load(uri);
	return 1;
};
dojo.requireIf((djConfig["isDebug"] || djConfig["debugAtAllCosts"]), "dojo.debug");

