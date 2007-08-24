/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.require("dojo.lang.common");
dojo.require("dojo.lang.func");
dojo.require("dojo.lang.declare");
dojo.provide("dojo.dnd.DragAndDrop");
dojo.declare("dojo.dnd.DragSource", null, {type:"", onDragEnd:function (evt) {
}, onDragStart:function (evt) {
}, onSelected:function (evt) {
}, unregister:function () {
	dojo.dnd.dragManager.unregisterDragSource(this);
}, reregister:function () {
	dojo.dnd.dragManager.registerDragSource(this);
}});
dojo.declare("dojo.dnd.DragObject", null, {type:"", register:function () {
	var dm = dojo.dnd.dragManager;
	if (dm["registerDragObject"]) {
		dm.registerDragObject(this);
	}
}, onDragStart:function (evt) {
}, onDragMove:function (evt) {
}, onDragOver:function (evt) {
}, onDragOut:function (evt) {
}, onDragEnd:function (evt) {
}, onDragLeave:dojo.lang.forward("onDragOut"), onDragEnter:dojo.lang.forward("onDragOver"), ondragout:dojo.lang.forward("onDragOut"), ondragover:dojo.lang.forward("onDragOver")});
dojo.declare("dojo.dnd.DropTarget", null, {acceptsType:function (type) {
	if (!dojo.lang.inArray(this.acceptedTypes, "*")) {
		if (!dojo.lang.inArray(this.acceptedTypes, type)) {
			return false;
		}
	}
	return true;
}, accepts:function (dragObjects) {
	if (!dojo.lang.inArray(this.acceptedTypes, "*")) {
		for (var i = 0; i < dragObjects.length; i++) {
			if (!dojo.lang.inArray(this.acceptedTypes, dragObjects[i].type)) {
				return false;
			}
		}
	}
	return true;
}, unregister:function () {
	dojo.dnd.dragManager.unregisterDropTarget(this);
}, onDragOver:function (evt) {
}, onDragOut:function (evt) {
}, onDragMove:function (evt) {
}, onDropStart:function (evt) {
}, onDrop:function (evt) {
}, onDropEnd:function () {
}}, function () {
	this.acceptedTypes = [];
});
dojo.dnd.DragEvent = function () {
	this.dragSource = null;
	this.dragObject = null;
	this.target = null;
	this.eventStatus = "success";
};
dojo.declare("dojo.dnd.DragManager", null, {selectedSources:[], dragObjects:[], dragSources:[], registerDragSource:function (source) {
}, dropTargets:[], registerDropTarget:function (target) {
}, lastDragTarget:null, currentDragTarget:null, onKeyDown:function () {
}, onMouseOut:function () {
}, onMouseMove:function () {
}, onMouseUp:function () {
}});

