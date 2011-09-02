/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.TreeTimeoutIterator");
dojo.require("dojo.event.*");
dojo.require("dojo.json");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.TreeCommon");
dojo.declare("dojo.widget.TreeTimeoutIterator", null, function (elem, callFunc, callObj) {
	var _this = this;
	this.currentParent = elem;
	this.callFunc = callFunc;
	this.callObj = callObj ? callObj : this;
	this.stack = [];
}, {maxStackDepth:Number.POSITIVE_INFINITY, stack:null, currentParent:null, currentIndex:0, filterFunc:function () {
	return true;
}, finishFunc:function () {
	return true;
}, setFilter:function (func, obj) {
	this.filterFunc = func;
	this.filterObj = obj;
}, setMaxLevel:function (level) {
	this.maxStackDepth = level - 2;
}, forward:function (timeout) {
	var _this = this;
	if (this.timeout) {
		var tid = setTimeout(function () {
			_this.processNext();
			clearTimeout(tid);
		}, _this.timeout);
	} else {
		return this.processNext();
	}
}, start:function (processFirst) {
	if (processFirst) {
		return this.callFunc.call(this.callObj, this.currentParent, this);
	}
	return this.processNext();
}, processNext:function () {
	var handler;
	var _this = this;
	var found;
	var next;
	if (this.maxStackDepth == -2) {
		return;
	}
	while (true) {
		var children = this.currentParent.children;
		if (children && children.length) {
			do {
				next = children[this.currentIndex];
			} while (this.currentIndex++ < children.length && !(found = this.filterFunc.call(this.filterObj, next)));
			if (found) {
				if (next.isFolder && this.stack.length <= this.maxStackDepth) {
					this.moveParent(next, 0);
				}
				return this.callFunc.call(this.callObj, next, this);
			}
		}
		if (this.stack.length) {
			this.popParent();
			continue;
		}
		break;
	}
	return this.finishFunc.call(this.finishObj);
}, setFinish:function (func, obj) {
	this.finishFunc = func;
	this.finishObj = obj;
}, popParent:function () {
	var p = this.stack.pop();
	this.currentParent = p[0];
	this.currentIndex = p[1];
}, moveParent:function (nextParent, nextIndex) {
	this.stack.push([this.currentParent, this.currentIndex]);
	this.currentParent = nextParent;
	this.currentIndex = nextIndex;
}});

