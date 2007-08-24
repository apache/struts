/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.PageContainer");
dojo.require("dojo.lang.func");
dojo.require("dojo.widget.*");
dojo.require("dojo.event.*");
dojo.require("dojo.html.selection");
dojo.widget.defineWidget("dojo.widget.PageContainer", dojo.widget.HtmlWidget, {isContainer:true, doLayout:true, templateString:"<div dojoAttachPoint='containerNode'></div>", selectedChild:"", fillInTemplate:function (args, frag) {
	var source = this.getFragNodeRef(frag);
	dojo.html.copyStyle(this.domNode, source);
	dojo.widget.PageContainer.superclass.fillInTemplate.apply(this, arguments);
}, postCreate:function (args, frag) {
	if (this.children.length) {
		dojo.lang.forEach(this.children, this._setupChild, this);
		var initialChild;
		if (this.selectedChild) {
			this.selectChild(this.selectedChild);
		} else {
			for (var i = 0; i < this.children.length; i++) {
				if (this.children[i].selected) {
					this.selectChild(this.children[i]);
					break;
				}
			}
			if (!this.selectedChildWidget) {
				this.selectChild(this.children[0]);
			}
		}
	}
}, addChild:function (child) {
	dojo.widget.PageContainer.superclass.addChild.apply(this, arguments);
	this._setupChild(child);
	this.onResized();
	if (!this.selectedChildWidget) {
		this.selectChild(child);
	}
}, _setupChild:function (page) {
	page.hide();
	page.domNode.style.position = "relative";
	dojo.event.topic.publish(this.widgetId + "-addChild", page);
}, removeChild:function (page) {
	dojo.widget.PageContainer.superclass.removeChild.apply(this, arguments);
	if (this._beingDestroyed) {
		return;
	}
	dojo.event.topic.publish(this.widgetId + "-removeChild", page);
	this.onResized();
	if (this.selectedChildWidget === page) {
		this.selectedChildWidget = undefined;
		if (this.children.length > 0) {
			this.selectChild(this.children[0], true);
		}
	}
}, selectChild:function (page, callingWidget) {
	page = dojo.widget.byId(page);
	this.correspondingPageButton = callingWidget;
	if (this.selectedChildWidget) {
		this._hideChild(this.selectedChildWidget);
	}
	this.selectedChildWidget = page;
	this.selectedChild = page.widgetId;
	this._showChild(page);
	page.isFirstChild = (page == this.children[0]);
	page.isLastChild = (page == this.children[this.children.length - 1]);
	dojo.event.topic.publish(this.widgetId + "-selectChild", page);
}, forward:function () {
	var index = dojo.lang.find(this.children, this.selectedChildWidget);
	this.selectChild(this.children[index + 1]);
}, back:function () {
	var index = dojo.lang.find(this.children, this.selectedChildWidget);
	this.selectChild(this.children[index - 1]);
}, onResized:function () {
	if (this.doLayout && this.selectedChildWidget) {
		with (this.selectedChildWidget.domNode.style) {
			top = dojo.html.getPixelValue(this.containerNode, "padding-top", true);
			left = dojo.html.getPixelValue(this.containerNode, "padding-left", true);
		}
		var content = dojo.html.getContentBox(this.containerNode);
		this.selectedChildWidget.resizeTo(content.width, content.height);
	}
}, _showChild:function (page) {
	if (this.doLayout) {
		var content = dojo.html.getContentBox(this.containerNode);
		page.resizeTo(content.width, content.height);
	}
	page.selected = true;
	page.show();
}, _hideChild:function (page) {
	page.selected = false;
	page.hide();
}, closeChild:function (page) {
	var remove = page.onClose(this, page);
	if (remove) {
		this.removeChild(page);
		page.destroy();
	}
}, destroy:function () {
	this._beingDestroyed = true;
	dojo.event.topic.destroy(this.widgetId + "-addChild");
	dojo.event.topic.destroy(this.widgetId + "-removeChild");
	dojo.event.topic.destroy(this.widgetId + "-selectChild");
	dojo.widget.PageContainer.superclass.destroy.apply(this, arguments);
}});
dojo.widget.defineWidget("dojo.widget.PageController", dojo.widget.HtmlWidget, {templateString:"<span wairole='tablist' dojoAttachEvent='onKey'></span>", isContainer:true, containerId:"", buttonWidget:"PageButton", "class":"dojoPageController", fillInTemplate:function () {
	dojo.html.addClass(this.domNode, this["class"]);
	dojo.widget.wai.setAttr(this.domNode, "waiRole", "role", "tablist");
}, postCreate:function () {
	this.pane2button = {};
	var container = dojo.widget.byId(this.containerId);
	if (container) {
		dojo.lang.forEach(container.children, this.onAddChild, this);
	}
	dojo.event.topic.subscribe(this.containerId + "-addChild", this, "onAddChild");
	dojo.event.topic.subscribe(this.containerId + "-removeChild", this, "onRemoveChild");
	dojo.event.topic.subscribe(this.containerId + "-selectChild", this, "onSelectChild");
}, destroy:function () {
	dojo.event.topic.unsubscribe(this.containerId + "-addChild", this, "onAddChild");
	dojo.event.topic.unsubscribe(this.containerId + "-removeChild", this, "onRemoveChild");
	dojo.event.topic.unsubscribe(this.containerId + "-selectChild", this, "onSelectChild");
	dojo.widget.PageController.superclass.destroy.apply(this, arguments);
}, onAddChild:function (page) {
	var button = dojo.widget.createWidget(this.buttonWidget, {label:page.label, closeButton:page.closable});
	this.addChild(button);
	this.domNode.appendChild(button.domNode);
	this.pane2button[page] = button;
	page.controlButton = button;
	var _this = this;
	dojo.event.connect(button, "onClick", function () {
		_this.onButtonClick(page);
	});
	dojo.event.connect(button, "onCloseButtonClick", function () {
		_this.onCloseButtonClick(page);
	});
}, onRemoveChild:function (page) {
	if (this._currentChild == page) {
		this._currentChild = null;
	}
	var button = this.pane2button[page];
	if (button) {
		button.destroy();
	}
	this.pane2button[page] = null;
}, onSelectChild:function (page) {
	if (this._currentChild) {
		var oldButton = this.pane2button[this._currentChild];
		oldButton.clearSelected();
	}
	var newButton = this.pane2button[page];
	newButton.setSelected();
	this._currentChild = page;
}, onButtonClick:function (page) {
	var container = dojo.widget.byId(this.containerId);
	container.selectChild(page, false, this);
}, onCloseButtonClick:function (page) {
	var container = dojo.widget.byId(this.containerId);
	container.closeChild(page);
}, onKey:function (evt) {
	if ((evt.keyCode == evt.KEY_RIGHT_ARROW) || (evt.keyCode == evt.KEY_LEFT_ARROW)) {
		var current = 0;
		var next = null;
		var current = dojo.lang.find(this.children, this.pane2button[this._currentChild]);
		if (evt.keyCode == evt.KEY_RIGHT_ARROW) {
			next = this.children[(current + 1) % this.children.length];
		} else {
			next = this.children[(current + (this.children.length - 1)) % this.children.length];
		}
		dojo.event.browser.stopEvent(evt);
		next.onClick();
	}
}});
dojo.widget.defineWidget("dojo.widget.PageButton", dojo.widget.HtmlWidget, {templateString:"<span class='item'>" + "<span dojoAttachEvent='onClick' dojoAttachPoint='titleNode' class='selectButton'>${this.label}</span>" + "<span dojoAttachEvent='onClick:onCloseButtonClick' class='closeButton'>[X]</span>" + "</span>", label:"foo", closeButton:false, onClick:function () {
	this.focus();
}, onCloseButtonMouseOver:function () {
	dojo.html.addClass(this.closeButtonNode, "closeHover");
}, onCloseButtonMouseOut:function () {
	dojo.html.removeClass(this.closeButtonNode, "closeHover");
}, onCloseButtonClick:function (evt) {
}, setSelected:function () {
	dojo.html.addClass(this.domNode, "current");
	this.titleNode.setAttribute("tabIndex", "0");
}, clearSelected:function () {
	dojo.html.removeClass(this.domNode, "current");
	this.titleNode.setAttribute("tabIndex", "-1");
}, focus:function () {
	if (this.titleNode.focus) {
		this.titleNode.focus();
	}
}});
dojo.lang.extend(dojo.widget.Widget, {label:"", selected:false, closable:false, onClose:function () {
	return true;
}});

