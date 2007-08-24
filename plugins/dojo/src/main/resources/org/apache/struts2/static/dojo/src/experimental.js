/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.experimental");
dojo.experimental = function (moduleName, extra) {
	var message = "EXPERIMENTAL: " + moduleName;
	message += " -- Not yet ready for use.  APIs subject to change without notice.";
	if (extra) {
		message += " " + extra;
	}
	dojo.debug(message);
};

