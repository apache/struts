/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.lang.timing.Timer");
dojo.require("dojo.lang.func");
dojo.lang.timing.Timer = function (interval) {
	this.timer = null;
	this.isRunning = false;
	this.interval = interval;
	this.onStart = null;
	this.onStop = null;
};
dojo.extend(dojo.lang.timing.Timer, {onTick:function () {
}, setInterval:function (interval) {
	if (this.isRunning) {
		dj_global.clearInterval(this.timer);
	}
	this.interval = interval;
	if (this.isRunning) {
		this.timer = dj_global.setInterval(dojo.lang.hitch(this, "onTick"), this.interval);
	}
}, start:function () {
	if (typeof this.onStart == "function") {
		this.onStart();
	}
	this.isRunning = true;
	this.timer = dj_global.setInterval(dojo.lang.hitch(this, "onTick"), this.interval);
}, stop:function () {
	if (typeof this.onStop == "function") {
		this.onStop();
	}
	this.isRunning = false;
	dj_global.clearInterval(this.timer);
}});

