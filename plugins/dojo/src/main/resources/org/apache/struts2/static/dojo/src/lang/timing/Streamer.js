/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.lang.timing.Streamer");
dojo.require("dojo.lang.timing.Timer");
dojo.lang.timing.Streamer = function (input, output, interval, minimum, initialData) {
	var self = this;
	var queue = [];
	this.interval = interval || 1000;
	this.minimumSize = minimum || 10;
	this.inputFunction = input || function (q) {
	};
	this.outputFunction = output || function (point) {
	};
	var timer = new dojo.lang.timing.Timer(this.interval);
	var tick = function () {
		self.onTick(self);
		if (queue.length < self.minimumSize) {
			self.inputFunction(queue);
		}
		var obj = queue.shift();
		while (typeof (obj) == "undefined" && queue.length > 0) {
			obj = queue.shift();
		}
		if (typeof (obj) == "undefined") {
			self.stop();
			return;
		}
		self.outputFunction(obj);
	};
	this.setInterval = function (ms) {
		this.interval = ms;
		timer.setInterval(ms);
	};
	this.onTick = function (obj) {
	};
	this.start = function () {
		if (typeof (this.inputFunction) == "function" && typeof (this.outputFunction) == "function") {
			timer.start();
			return;
		}
		dojo.raise("You cannot start a Streamer without an input and an output function.");
	};
	this.onStart = function () {
	};
	this.stop = function () {
		timer.stop();
	};
	this.onStop = function () {
	};
	timer.onTick = this.tick;
	timer.onStart = this.onStart;
	timer.onStop = this.onStop;
	if (initialData) {
		queue.concat(initialData);
	}
};

