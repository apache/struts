/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.event.browser");
dojo.require("dojo.event.common");
dojo._ie_clobber = new function () {
	this.clobberNodes = [];
	function nukeProp(node, prop) {
		try {
			node[prop] = null;
		}
		catch (e) {
		}
		try {
			delete node[prop];
		}
		catch (e) {
		}
		try {
			node.removeAttribute(prop);
		}
		catch (e) {
		}
	}
	this.clobber = function (nodeRef) {
		var na;
		var tna;
		if (nodeRef) {
			tna = nodeRef.all || nodeRef.getElementsByTagName("*");
			na = [nodeRef];
			for (var x = 0; x < tna.length; x++) {
				if (tna[x]["__doClobber__"]) {
					na.push(tna[x]);
				}
			}
		} else {
			try {
				window.onload = null;
			}
			catch (e) {
			}
			na = (this.clobberNodes.length) ? this.clobberNodes : document.all;
		}
		tna = null;
		var basis = {};
		for (var i = na.length - 1; i >= 0; i = i - 1) {
			var el = na[i];
			try {
				if (el && el["__clobberAttrs__"]) {
					for (var j = 0; j < el.__clobberAttrs__.length; j++) {
						nukeProp(el, el.__clobberAttrs__[j]);
					}
					nukeProp(el, "__clobberAttrs__");
					nukeProp(el, "__doClobber__");
				}
			}
			catch (e) {
			}
		}
		na = null;
	};
};
if (dojo.render.html.ie) {
	dojo.addOnUnload(function () {
		dojo._ie_clobber.clobber();
		try {
			if ((dojo["widget"]) && (dojo.widget["manager"])) {
				dojo.widget.manager.destroyAll();
			}
		}
		catch (e) {
		}
		if (dojo.widget) {
			for (var name in dojo.widget._templateCache) {
				if (dojo.widget._templateCache[name].node) {
					dojo.dom.destroyNode(dojo.widget._templateCache[name].node);
					dojo.widget._templateCache[name].node = null;
					delete dojo.widget._templateCache[name].node;
				}
			}
		}
		try {
			window.onload = null;
		}
		catch (e) {
		}
		try {
			window.onunload = null;
		}
		catch (e) {
		}
		dojo._ie_clobber.clobberNodes = [];
	});
}
dojo.event.browser = new function () {
	var clobberIdx = 0;
	this.normalizedEventName = function (eventName) {
		switch (eventName) {
		  case "CheckboxStateChange":
		  case "DOMAttrModified":
		  case "DOMMenuItemActive":
		  case "DOMMenuItemInactive":
		  case "DOMMouseScroll":
		  case "DOMNodeInserted":
		  case "DOMNodeRemoved":
		  case "RadioStateChange":
			return eventName;
			break;
		  default:
			var lcn = eventName.toLowerCase();
			return (lcn.indexOf("on") == 0) ? lcn.substr(2) : lcn;
			break;
		}
	};
	this.clean = function (node) {
		if (dojo.render.html.ie) {
			dojo._ie_clobber.clobber(node);
		}
	};
	this.addClobberNode = function (node) {
		if (!dojo.render.html.ie) {
			return;
		}
		if (!node["__doClobber__"]) {
			node.__doClobber__ = true;
			dojo._ie_clobber.clobberNodes.push(node);
			node.__clobberAttrs__ = [];
		}
	};
	this.addClobberNodeAttrs = function (node, props) {
		if (!dojo.render.html.ie) {
			return;
		}
		this.addClobberNode(node);
		for (var x = 0; x < props.length; x++) {
			node.__clobberAttrs__.push(props[x]);
		}
	};
	this.removeListener = function (node, evtName, fp, capture) {
		if (!capture) {
			var capture = false;
		}
		evtName = dojo.event.browser.normalizedEventName(evtName);
		if (evtName == "key") {
			if (dojo.render.html.ie) {
				this.removeListener(node, "onkeydown", fp, capture);
			}
			evtName = "keypress";
		}
		if (node.removeEventListener) {
			node.removeEventListener(evtName, fp, capture);
		}
	};
	this.addListener = function (node, evtName, fp, capture, dontFix) {
		if (!node) {
			return;
		}
		if (!capture) {
			var capture = false;
		}
		evtName = dojo.event.browser.normalizedEventName(evtName);
		if (evtName == "key") {
			if (dojo.render.html.ie) {
				this.addListener(node, "onkeydown", fp, capture, dontFix);
			}
			evtName = "keypress";
		}
		if (!dontFix) {
			var newfp = function (evt) {
				if (!evt) {
					evt = window.event;
				}
				var ret = fp(dojo.event.browser.fixEvent(evt, this));
				if (capture) {
					dojo.event.browser.stopEvent(evt);
				}
				return ret;
			};
		} else {
			newfp = fp;
		}
		if (node.addEventListener) {
			node.addEventListener(evtName, newfp, capture);
			return newfp;
		} else {
			evtName = "on" + evtName;
			if (typeof node[evtName] == "function") {
				var oldEvt = node[evtName];
				node[evtName] = function (e) {
					oldEvt(e);
					return newfp(e);
				};
			} else {
				node[evtName] = newfp;
			}
			if (dojo.render.html.ie) {
				this.addClobberNodeAttrs(node, [evtName]);
			}
			return newfp;
		}
	};
	this.isEvent = function (obj) {
		return (typeof obj != "undefined") && (obj) && (typeof Event != "undefined") && (obj.eventPhase);
	};
	this.currentEvent = null;
	this.callListener = function (listener, curTarget) {
		if (typeof listener != "function") {
			dojo.raise("listener not a function: " + listener);
		}
		dojo.event.browser.currentEvent.currentTarget = curTarget;
		return listener.call(curTarget, dojo.event.browser.currentEvent);
	};
	this._stopPropagation = function () {
		dojo.event.browser.currentEvent.cancelBubble = true;
	};
	this._preventDefault = function () {
		dojo.event.browser.currentEvent.returnValue = false;
	};
	this.keys = {KEY_BACKSPACE:8, KEY_TAB:9, KEY_CLEAR:12, KEY_ENTER:13, KEY_SHIFT:16, KEY_CTRL:17, KEY_ALT:18, KEY_PAUSE:19, KEY_CAPS_LOCK:20, KEY_ESCAPE:27, KEY_SPACE:32, KEY_PAGE_UP:33, KEY_PAGE_DOWN:34, KEY_END:35, KEY_HOME:36, KEY_LEFT_ARROW:37, KEY_UP_ARROW:38, KEY_RIGHT_ARROW:39, KEY_DOWN_ARROW:40, KEY_INSERT:45, KEY_DELETE:46, KEY_HELP:47, KEY_LEFT_WINDOW:91, KEY_RIGHT_WINDOW:92, KEY_SELECT:93, KEY_NUMPAD_0:96, KEY_NUMPAD_1:97, KEY_NUMPAD_2:98, KEY_NUMPAD_3:99, KEY_NUMPAD_4:100, KEY_NUMPAD_5:101, KEY_NUMPAD_6:102, KEY_NUMPAD_7:103, KEY_NUMPAD_8:104, KEY_NUMPAD_9:105, KEY_NUMPAD_MULTIPLY:106, KEY_NUMPAD_PLUS:107, KEY_NUMPAD_ENTER:108, KEY_NUMPAD_MINUS:109, KEY_NUMPAD_PERIOD:110, KEY_NUMPAD_DIVIDE:111, KEY_F1:112, KEY_F2:113, KEY_F3:114, KEY_F4:115, KEY_F5:116, KEY_F6:117, KEY_F7:118, KEY_F8:119, KEY_F9:120, KEY_F10:121, KEY_F11:122, KEY_F12:123, KEY_F13:124, KEY_F14:125, KEY_F15:126, KEY_NUM_LOCK:144, KEY_SCROLL_LOCK:145};
	this.revKeys = [];
	for (var key in this.keys) {
		this.revKeys[this.keys[key]] = key;
	}
	this.fixEvent = function (evt, sender) {
		if (!evt) {
			if (window["event"]) {
				evt = window.event;
			}
		}
		if ((evt["type"]) && (evt["type"].indexOf("key") == 0)) {
			evt.keys = this.revKeys;
			for (var key in this.keys) {
				evt[key] = this.keys[key];
			}
			if (evt["type"] == "keydown" && dojo.render.html.ie) {
				switch (evt.keyCode) {
				  case evt.KEY_SHIFT:
				  case evt.KEY_CTRL:
				  case evt.KEY_ALT:
				  case evt.KEY_CAPS_LOCK:
				  case evt.KEY_LEFT_WINDOW:
				  case evt.KEY_RIGHT_WINDOW:
				  case evt.KEY_SELECT:
				  case evt.KEY_NUM_LOCK:
				  case evt.KEY_SCROLL_LOCK:
				  case evt.KEY_NUMPAD_0:
				  case evt.KEY_NUMPAD_1:
				  case evt.KEY_NUMPAD_2:
				  case evt.KEY_NUMPAD_3:
				  case evt.KEY_NUMPAD_4:
				  case evt.KEY_NUMPAD_5:
				  case evt.KEY_NUMPAD_6:
				  case evt.KEY_NUMPAD_7:
				  case evt.KEY_NUMPAD_8:
				  case evt.KEY_NUMPAD_9:
				  case evt.KEY_NUMPAD_PERIOD:
					break;
				  case evt.KEY_NUMPAD_MULTIPLY:
				  case evt.KEY_NUMPAD_PLUS:
				  case evt.KEY_NUMPAD_ENTER:
				  case evt.KEY_NUMPAD_MINUS:
				  case evt.KEY_NUMPAD_DIVIDE:
					break;
				  case evt.KEY_PAUSE:
				  case evt.KEY_TAB:
				  case evt.KEY_BACKSPACE:
				  case evt.KEY_ENTER:
				  case evt.KEY_ESCAPE:
				  case evt.KEY_PAGE_UP:
				  case evt.KEY_PAGE_DOWN:
				  case evt.KEY_END:
				  case evt.KEY_HOME:
				  case evt.KEY_LEFT_ARROW:
				  case evt.KEY_UP_ARROW:
				  case evt.KEY_RIGHT_ARROW:
				  case evt.KEY_DOWN_ARROW:
				  case evt.KEY_INSERT:
				  case evt.KEY_DELETE:
				  case evt.KEY_F1:
				  case evt.KEY_F2:
				  case evt.KEY_F3:
				  case evt.KEY_F4:
				  case evt.KEY_F5:
				  case evt.KEY_F6:
				  case evt.KEY_F7:
				  case evt.KEY_F8:
				  case evt.KEY_F9:
				  case evt.KEY_F10:
				  case evt.KEY_F11:
				  case evt.KEY_F12:
				  case evt.KEY_F12:
				  case evt.KEY_F13:
				  case evt.KEY_F14:
				  case evt.KEY_F15:
				  case evt.KEY_CLEAR:
				  case evt.KEY_HELP:
					evt.key = evt.keyCode;
					break;
				  default:
					if (evt.ctrlKey || evt.altKey) {
						var unifiedCharCode = evt.keyCode;
						if (unifiedCharCode >= 65 && unifiedCharCode <= 90 && evt.shiftKey == false) {
							unifiedCharCode += 32;
						}
						if (unifiedCharCode >= 1 && unifiedCharCode <= 26 && evt.ctrlKey) {
							unifiedCharCode += 96;
						}
						evt.key = String.fromCharCode(unifiedCharCode);
					}
				}
			} else {
				if (evt["type"] == "keypress") {
					if (dojo.render.html.opera) {
						if (evt.which == 0) {
							evt.key = evt.keyCode;
						} else {
							if (evt.which > 0) {
								switch (evt.which) {
								  case evt.KEY_SHIFT:
								  case evt.KEY_CTRL:
								  case evt.KEY_ALT:
								  case evt.KEY_CAPS_LOCK:
								  case evt.KEY_NUM_LOCK:
								  case evt.KEY_SCROLL_LOCK:
									break;
								  case evt.KEY_PAUSE:
								  case evt.KEY_TAB:
								  case evt.KEY_BACKSPACE:
								  case evt.KEY_ENTER:
								  case evt.KEY_ESCAPE:
									evt.key = evt.which;
									break;
								  default:
									var unifiedCharCode = evt.which;
									if ((evt.ctrlKey || evt.altKey || evt.metaKey) && (evt.which >= 65 && evt.which <= 90 && evt.shiftKey == false)) {
										unifiedCharCode += 32;
									}
									evt.key = String.fromCharCode(unifiedCharCode);
								}
							}
						}
					} else {
						if (dojo.render.html.ie) {
							if (!evt.ctrlKey && !evt.altKey && evt.keyCode >= evt.KEY_SPACE) {
								evt.key = String.fromCharCode(evt.keyCode);
							}
						} else {
							if (dojo.render.html.safari) {
								switch (evt.keyCode) {
								  case 25:
									evt.key = evt.KEY_TAB;
									evt.shift = true;
									break;
								  case 63232:
									evt.key = evt.KEY_UP_ARROW;
									break;
								  case 63233:
									evt.key = evt.KEY_DOWN_ARROW;
									break;
								  case 63234:
									evt.key = evt.KEY_LEFT_ARROW;
									break;
								  case 63235:
									evt.key = evt.KEY_RIGHT_ARROW;
									break;
								  case 63236:
									evt.key = evt.KEY_F1;
									break;
								  case 63237:
									evt.key = evt.KEY_F2;
									break;
								  case 63238:
									evt.key = evt.KEY_F3;
									break;
								  case 63239:
									evt.key = evt.KEY_F4;
									break;
								  case 63240:
									evt.key = evt.KEY_F5;
									break;
								  case 63241:
									evt.key = evt.KEY_F6;
									break;
								  case 63242:
									evt.key = evt.KEY_F7;
									break;
								  case 63243:
									evt.key = evt.KEY_F8;
									break;
								  case 63244:
									evt.key = evt.KEY_F9;
									break;
								  case 63245:
									evt.key = evt.KEY_F10;
									break;
								  case 63246:
									evt.key = evt.KEY_F11;
									break;
								  case 63247:
									evt.key = evt.KEY_F12;
									break;
								  case 63250:
									evt.key = evt.KEY_PAUSE;
									break;
								  case 63272:
									evt.key = evt.KEY_DELETE;
									break;
								  case 63273:
									evt.key = evt.KEY_HOME;
									break;
								  case 63275:
									evt.key = evt.KEY_END;
									break;
								  case 63276:
									evt.key = evt.KEY_PAGE_UP;
									break;
								  case 63277:
									evt.key = evt.KEY_PAGE_DOWN;
									break;
								  case 63302:
									evt.key = evt.KEY_INSERT;
									break;
								  case 63248:
								  case 63249:
								  case 63289:
									break;
								  default:
									evt.key = evt.charCode >= evt.KEY_SPACE ? String.fromCharCode(evt.charCode) : evt.keyCode;
								}
							} else {
								evt.key = evt.charCode > 0 ? String.fromCharCode(evt.charCode) : evt.keyCode;
							}
						}
					}
				}
			}
		}
		if (dojo.render.html.ie) {
			if (!evt.target) {
				evt.target = evt.srcElement;
			}
			if (!evt.currentTarget) {
				evt.currentTarget = (sender ? sender : evt.srcElement);
			}
			if (!evt.layerX) {
				evt.layerX = evt.offsetX;
			}
			if (!evt.layerY) {
				evt.layerY = evt.offsetY;
			}
			var doc = (evt.srcElement && evt.srcElement.ownerDocument) ? evt.srcElement.ownerDocument : document;
			var docBody = ((dojo.render.html.ie55) || (doc["compatMode"] == "BackCompat")) ? doc.body : doc.documentElement;
			if (!evt.pageX) {
				evt.pageX = evt.clientX + (docBody.scrollLeft || 0);
			}
			if (!evt.pageY) {
				evt.pageY = evt.clientY + (docBody.scrollTop || 0);
			}
			if (evt.type == "mouseover") {
				evt.relatedTarget = evt.fromElement;
			}
			if (evt.type == "mouseout") {
				evt.relatedTarget = evt.toElement;
			}
			this.currentEvent = evt;
			evt.callListener = this.callListener;
			evt.stopPropagation = this._stopPropagation;
			evt.preventDefault = this._preventDefault;
		}
		return evt;
	};
	this.stopEvent = function (evt) {
		if (window.event) {
			evt.cancelBubble = true;
			evt.returnValue = false;
		} else {
			evt.preventDefault();
			evt.stopPropagation();
		}
	};
};

