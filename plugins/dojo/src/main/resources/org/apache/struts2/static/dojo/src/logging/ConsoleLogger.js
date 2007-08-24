/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.logging.ConsoleLogger");
dojo.require("dojo.logging.Logger");
dojo.lang.extend(dojo.logging.MemoryLogHandler, {debug:function () {
	dojo.hostenv.println.apply(this, arguments);
}, info:function () {
	dojo.hostenv.println.apply(this, arguments);
}, warn:function () {
	dojo.hostenv.println.apply(this, arguments);
}, error:function () {
	dojo.hostenv.println.apply(this, arguments);
}, critical:function () {
	dojo.hostenv.println.apply(this, arguments);
}, emit:function (record) {
	if (!djConfig.isDebug) {
		return;
	}
	var funcName = null;
	switch (record.level) {
	  case 1:
		funcName = "debug";
		break;
	  case 2:
		funcName = "info";
		break;
	  case 3:
		funcName = "warn";
		break;
	  case 4:
		funcName = "error";
		break;
	  case 5:
		funcName = "critical";
		break;
	  default:
		funcName = "debug";
	}
	var logStr = String(dojo.log.getLevelName(record.level) + ": " + record.time.toLocaleTimeString()) + ": " + record.message;
	if (record.msgArgs && record.msgArgs.length > 0) {
		this[funcName].call(this, logStr, record.msgArgs);
	} else {
		this[funcName].call(this, logStr);
	}
	this.data.push(record);
	if (this.numRecords != -1) {
		while (this.data.length > this.numRecords) {
			this.data.shift();
		}
	}
}});
if (!dj_undef("console") && !dj_undef("info", console)) {
	dojo.lang.extend(dojo.logging.MemoryLogHandler, {debug:function () {
		console.debug.apply(this, arguments);
	}, info:function () {
		console.info.apply(this, arguments);
	}, warn:function () {
		console.warn.apply(this, arguments);
	}, error:function () {
		console.error.apply(this, arguments);
	}, critical:function () {
		console.error.apply(this, arguments);
	}});
	dojo.lang.extend(dojo.logging.Logger, {exception:function (msg, e, squelch) {
		var args = [msg];
		if (e) {
			msg += " : " + e.name + " " + (e.description || e.message);
			args.push(e);
		}
		this.logType("ERROR", args);
		if (!squelch) {
			throw e;
		}
	}});
}

