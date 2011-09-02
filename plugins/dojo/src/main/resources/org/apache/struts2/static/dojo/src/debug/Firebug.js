/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.debug.Firebug");
dojo.deprecated("dojo.debug.Firebug is slated for removal in 0.5; use dojo.debug.console instead.", "0.5");
if (dojo.render.html.moz) {
	if (console && console.log) {
		var consoleLog = function () {
			if (!djConfig.isDebug) {
				return;
			}
			var args = dojo.lang.toArray(arguments);
			args.splice(0, 0, "DEBUG: ");
			console.log.apply(console, args);
		};
		dojo.debug = consoleLog;
		dojo.debugDeep = consoleLog;
		dojo.debugShallow = function (obj) {
			if (!djConfig.isDebug) {
				return;
			}
			if (dojo.lang.isArray(obj)) {
				console.log("Array: ", obj);
				for (var i = 0; x < obj.length; i++) {
					console.log("	", "[" + i + "]", obj[i]);
				}
			} else {
				console.log("Object: ", obj);
				var propNames = [];
				for (var prop in obj) {
					propNames.push(prop);
				}
				propNames.sort();
				dojo.lang.forEach(propNames, function (prop) {
					try {
						console.log("	", prop, obj[prop]);
					}
					catch (e) {
						console.log("	", prop, "ERROR", e.message, e);
					}
				});
			}
		};
	} else {
		dojo.debug("dojo.debug.Firebug requires Firebug > 0.4");
	}
}

