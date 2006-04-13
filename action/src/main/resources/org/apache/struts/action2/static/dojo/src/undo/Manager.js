/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.undo.Manager");

dojo.undo.Manager = function () {

	this._undoStack = [];
	this._redoStack = [];

	this._undoRegistrationLevel = 0;
}

dojo.undo.Manager.prototype = {

//Registering undo operations
	//registerUndoWithTarget:selector:object:
	prepareWithInvocationTarget: function () {},
	forwardInvocation: function () {},

//Checking undo ability
	canUndo: false,
	canRedo: false,

//Performing undo and redo
	undo: function () {},
	undoNestedGroup: function () {},
	redo: function () {},

//Limiting the undo stack
	setLevelsOfUndo: function (levels) {
		this.levelsOfUndo = levels;
		if (levels != 0 && this._undoStack.length > levels) {
			this._undoStack.splice(levels, this._undoStack.length - levels);
		}
	},
	levelsOfUndo: 0,

//Creating undo groups
	beginUndoGrouping: function () {},
	endUndoGrouping: function () {},
	enableUndoRegistration: function () {
		if (++this._undoRegistrationLevel >= 0) {
			this._undoRegistrationLevel = 0;
			this.isUndoRegistrationEnabled = true;
		}
	},
	groupsByEvent: true,
	setGroupsByEvent: function (bool) { this.groupsByEvent = bool; },
	groupingLevel: 0,

//Disabling undo
	disableUndoRegistration = function () {
		this.isUndoRegistrationEnabled = false;
		this._undoRegistrationLevel--;
	},
	isUndoRegistrationEnabled: true,

//Checking whether undo or redo is being performed
	isUndoing: false,
	isRedoing: false,

//Clearing undo operations
	removeAllActions: function () {
		this._undoStack = [];
		this._redoStack = [];
	},
	removeAllActionsWithTarget: function (target) {},

//Setting and getting the action name
	setActionName: function () {},
	redoActionName: null,
	undoActionName: null,

//Getting and localizing menu item title
	redoMenuItemTitle: null,
	undoMenuItemTitle: null,
	redoMenuTitleForUndoActionName: function () {},
	undoMenuTitleForUndoActionName: function () {},
      
//Working with run loops
	runLoopModes: [],
	setRunLoopModes: function () {}
}