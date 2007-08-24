/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.PopupContainer");
dojo.require("dojo.html.style");
dojo.require("dojo.html.layout");
dojo.require("dojo.html.selection");
dojo.require("dojo.html.iframe");
dojo.require("dojo.event.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.declare("dojo.widget.PopupContainerBase", null, function () {
	this.queueOnAnimationFinish = [];
}, {isShowingNow:false, currentSubpopup:null, beginZIndex:1000, parentPopup:null, parent:null, popupIndex:0, aroundBox:dojo.html.boxSizing.BORDER_BOX, openedForWindow:null, processKey:function (evt) {
	return false;
}, applyPopupBasicStyle:function () {
	with (this.domNode.style) {
		display = "none";
		position = "absolute";
	}
}, aboutToShow:function () {
}, open:function (x, y, parent, explodeSrc, orient, padding) {
	if (this.isShowingNow) {
		return;
	}
	if (this.animationInProgress) {
		this.queueOnAnimationFinish.push(this.open, arguments);
		return;
	}
	this.aboutToShow();
	var around = false, node, aroundOrient;
	if (typeof x == "object") {
		node = x;
		aroundOrient = explodeSrc;
		explodeSrc = parent;
		parent = y;
		around = true;
	}
	this.parent = parent;
	dojo.body().appendChild(this.domNode);
	explodeSrc = explodeSrc || parent["domNode"] || [];
	var parentPopup = null;
	this.isTopLevel = true;
	while (parent) {
		if (parent !== this && (parent.setOpenedSubpopup != undefined && parent.applyPopupBasicStyle != undefined)) {
			parentPopup = parent;
			this.isTopLevel = false;
			parentPopup.setOpenedSubpopup(this);
			break;
		}
		parent = parent.parent;
	}
	this.parentPopup = parentPopup;
	this.popupIndex = parentPopup ? parentPopup.popupIndex + 1 : 1;
	if (this.isTopLevel) {
		var button = dojo.html.isNode(explodeSrc) ? explodeSrc : null;
		dojo.widget.PopupManager.opened(this, button);
	}
	if (this.isTopLevel && !dojo.withGlobal(this.openedForWindow || dojo.global(), dojo.html.selection.isCollapsed)) {
		this._bookmark = dojo.withGlobal(this.openedForWindow || dojo.global(), dojo.html.selection.getBookmark);
	} else {
		this._bookmark = null;
	}
	if (explodeSrc instanceof Array) {
		explodeSrc = {left:explodeSrc[0], top:explodeSrc[1], width:0, height:0};
	}
	with (this.domNode.style) {
		display = "";
		zIndex = this.beginZIndex + this.popupIndex;
	}
	if (around) {
		this.move(node, padding, aroundOrient);
	} else {
		this.move(x, y, padding, orient);
	}
	this.domNode.style.display = "none";
	this.explodeSrc = explodeSrc;
	this.show();
	this.isShowingNow = true;
}, move:function (x, y, padding, orient) {
	var around = (typeof x == "object");
	if (around) {
		var aroundOrient = padding;
		var node = x;
		padding = y;
		if (!aroundOrient) {
			aroundOrient = {"BL":"TL", "TL":"BL"};
		}
		dojo.html.placeOnScreenAroundElement(this.domNode, node, padding, this.aroundBox, aroundOrient);
	} else {
		if (!orient) {
			orient = "TL,TR,BL,BR";
		}
		dojo.html.placeOnScreen(this.domNode, x, y, padding, true, orient);
	}
}, close:function (force) {
	if (force) {
		this.domNode.style.display = "none";
	}
	if (this.animationInProgress) {
		this.queueOnAnimationFinish.push(this.close, []);
		return;
	}
	this.closeSubpopup(force);
	this.hide();
	if (this.bgIframe) {
		this.bgIframe.hide();
		this.bgIframe.size({left:0, top:0, width:0, height:0});
	}
	if (this.isTopLevel) {
		dojo.widget.PopupManager.closed(this);
	}
	this.isShowingNow = false;
	if (this.parent) {
		setTimeout(dojo.lang.hitch(this, function () {
			try {
				if (this.parent["focus"]) {
					this.parent.focus();
				} else {
					this.parent.domNode.focus();
				}
			}
			catch (e) {
				dojo.debug("No idea how to focus to parent", e);
			}
		}), 10);
	}
	if (this._bookmark && dojo.withGlobal(this.openedForWindow || dojo.global(), dojo.html.selection.isCollapsed)) {
		if (this.openedForWindow) {
			this.openedForWindow.focus();
		}
		try {
			dojo.withGlobal(this.openedForWindow || dojo.global(), "moveToBookmark", dojo.html.selection, [this._bookmark]);
		}
		catch (e) {
		}
	}
	this._bookmark = null;
}, closeAll:function (force) {
	if (this.parentPopup) {
		this.parentPopup.closeAll(force);
	} else {
		this.close(force);
	}
}, setOpenedSubpopup:function (popup) {
	this.currentSubpopup = popup;
}, closeSubpopup:function (force) {
	if (this.currentSubpopup == null) {
		return;
	}
	this.currentSubpopup.close(force);
	this.currentSubpopup = null;
}, onShow:function () {
	dojo.widget.PopupContainer.superclass.onShow.apply(this, arguments);
	this.openedSize = {w:this.domNode.style.width, h:this.domNode.style.height};
	if (dojo.render.html.ie) {
		if (!this.bgIframe) {
			this.bgIframe = new dojo.html.BackgroundIframe();
			this.bgIframe.setZIndex(this.domNode);
		}
		this.bgIframe.size(this.domNode);
		this.bgIframe.show();
	}
	this.processQueue();
}, processQueue:function () {
	if (!this.queueOnAnimationFinish.length) {
		return;
	}
	var func = this.queueOnAnimationFinish.shift();
	var args = this.queueOnAnimationFinish.shift();
	func.apply(this, args);
}, onHide:function () {
	dojo.widget.HtmlWidget.prototype.onHide.call(this);
	if (this.openedSize) {
		with (this.domNode.style) {
			width = this.openedSize.w;
			height = this.openedSize.h;
		}
	}
	this.processQueue();
}});
dojo.widget.defineWidget("dojo.widget.PopupContainer", [dojo.widget.HtmlWidget, dojo.widget.PopupContainerBase], {isContainer:true, fillInTemplate:function () {
	this.applyPopupBasicStyle();
	dojo.widget.PopupContainer.superclass.fillInTemplate.apply(this, arguments);
}});
dojo.widget.PopupManager = new function () {
	this.currentMenu = null;
	this.currentButton = null;
	this.currentFocusMenu = null;
	this.focusNode = null;
	this.registeredWindows = [];
	this.registerWin = function (win) {
		if (!win.__PopupManagerRegistered) {
			dojo.event.connect(win.document, "onmousedown", this, "onClick");
			dojo.event.connect(win, "onscroll", this, "onClick");
			dojo.event.connect(win.document, "onkey", this, "onKey");
			win.__PopupManagerRegistered = true;
			this.registeredWindows.push(win);
		}
	};
	this.registerAllWindows = function (targetWindow) {
		if (!targetWindow) {
			targetWindow = dojo.html.getDocumentWindow(window.top && window.top.document || window.document);
		}
		this.registerWin(targetWindow);
		for (var i = 0; i < targetWindow.frames.length; i++) {
			try {
				var win = dojo.html.getDocumentWindow(targetWindow.frames[i].document);
				if (win) {
					this.registerAllWindows(win);
				}
			}
			catch (e) {
			}
		}
	};
	this.unRegisterWin = function (win) {
		if (win.__PopupManagerRegistered) {
			dojo.event.disconnect(win.document, "onmousedown", this, "onClick");
			dojo.event.disconnect(win, "onscroll", this, "onClick");
			dojo.event.disconnect(win.document, "onkey", this, "onKey");
			win.__PopupManagerRegistered = false;
		}
	};
	this.unRegisterAllWindows = function () {
		for (var i = 0; i < this.registeredWindows.length; ++i) {
			this.unRegisterWin(this.registeredWindows[i]);
		}
		this.registeredWindows = [];
	};
	dojo.addOnLoad(this, "registerAllWindows");
	dojo.addOnUnload(this, "unRegisterAllWindows");
	this.closed = function (menu) {
		if (this.currentMenu == menu) {
			this.currentMenu = null;
			this.currentButton = null;
			this.currentFocusMenu = null;
		}
	};
	this.opened = function (menu, button) {
		if (menu == this.currentMenu) {
			return;
		}
		if (this.currentMenu) {
			this.currentMenu.close();
		}
		this.currentMenu = menu;
		this.currentFocusMenu = menu;
		this.currentButton = button;
	};
	this.setFocusedMenu = function (menu) {
		this.currentFocusMenu = menu;
	};
	this.onKey = function (e) {
		if (!e.key) {
			return;
		}
		if (!this.currentMenu || !this.currentMenu.isShowingNow) {
			return;
		}
		var m = this.currentFocusMenu;
		while (m) {
			if (m.processKey(e)) {
				e.preventDefault();
				e.stopPropagation();
				break;
			}
			m = m.parentPopup || m.parentMenu;
		}
	}, this.onClick = function (e) {
		if (!this.currentMenu) {
			return;
		}
		var scrolloffset = dojo.html.getScroll().offset;
		var m = this.currentMenu;
		while (m) {
			if (dojo.html.overElement(m.domNode, e) || dojo.html.isDescendantOf(e.target, m.domNode)) {
				return;
			}
			m = m.currentSubpopup;
		}
		if (this.currentButton && dojo.html.overElement(this.currentButton, e)) {
			return;
		}
		this.currentMenu.closeAll(true);
	};
};

