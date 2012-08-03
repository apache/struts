/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.require("dojo.html.common");
dojo.provide("dojo.html.selection");
dojo.require("dojo.dom");
dojo.require("dojo.lang.common");
dojo.html.selectionType = {NONE:0, TEXT:1, CONTROL:2};
dojo.html.clearSelection = function () {
	var _window = dojo.global();
	var _document = dojo.doc();
	try {
		if (_window["getSelection"]) {
			if (dojo.render.html.safari) {
				_window.getSelection().collapse();
			} else {
				_window.getSelection().removeAllRanges();
			}
		} else {
			if (_document.selection) {
				if (_document.selection.empty) {
					_document.selection.empty();
				} else {
					if (_document.selection.clear) {
						_document.selection.clear();
					}
				}
			}
		}
		return true;
	}
	catch (e) {
		dojo.debug(e);
		return false;
	}
};
dojo.html.disableSelection = function (element) {
	element = dojo.byId(element) || dojo.body();
	var h = dojo.render.html;
	if (h.mozilla) {
		element.style.MozUserSelect = "none";
	} else {
		if (h.safari) {
			element.style.KhtmlUserSelect = "none";
		} else {
			if (h.ie) {
				element.unselectable = "on";
			} else {
				return false;
			}
		}
	}
	return true;
};
dojo.html.enableSelection = function (element) {
	element = dojo.byId(element) || dojo.body();
	var h = dojo.render.html;
	if (h.mozilla) {
		element.style.MozUserSelect = "";
	} else {
		if (h.safari) {
			element.style.KhtmlUserSelect = "";
		} else {
			if (h.ie) {
				element.unselectable = "off";
			} else {
				return false;
			}
		}
	}
	return true;
};
dojo.html.selectElement = function (element) {
	dojo.deprecated("dojo.html.selectElement", "replaced by dojo.html.selection.selectElementChildren", 0.5);
};
dojo.html.selectInputText = function (element) {
	var _window = dojo.global();
	var _document = dojo.doc();
	element = dojo.byId(element);
	if (_document["selection"] && dojo.body()["createTextRange"]) {
		var range = element.createTextRange();
		range.moveStart("character", 0);
		range.moveEnd("character", element.value.length);
		range.select();
	} else {
		if (_window["getSelection"]) {
			var selection = _window.getSelection();
			element.setSelectionRange(0, element.value.length);
		}
	}
	element.focus();
};
dojo.html.isSelectionCollapsed = function () {
	dojo.deprecated("dojo.html.isSelectionCollapsed", "replaced by dojo.html.selection.isCollapsed", 0.5);
	return dojo.html.selection.isCollapsed();
};
dojo.lang.mixin(dojo.html.selection, {getType:function () {
	if (dojo.doc()["selection"]) {
		return dojo.html.selectionType[dojo.doc().selection.type.toUpperCase()];
	} else {
		var stype = dojo.html.selectionType.TEXT;
		var oSel;
		try {
			oSel = dojo.global().getSelection();
		}
		catch (e) {
		}
		if (oSel && oSel.rangeCount == 1) {
			var oRange = oSel.getRangeAt(0);
			if (oRange.startContainer == oRange.endContainer && (oRange.endOffset - oRange.startOffset) == 1 && oRange.startContainer.nodeType != dojo.dom.TEXT_NODE) {
				stype = dojo.html.selectionType.CONTROL;
			}
		}
		return stype;
	}
}, isCollapsed:function () {
	var _window = dojo.global();
	var _document = dojo.doc();
	if (_document["selection"]) {
		return _document.selection.createRange().text == "";
	} else {
		if (_window["getSelection"]) {
			var selection = _window.getSelection();
			if (dojo.lang.isString(selection)) {
				return selection == "";
			} else {
				return selection.isCollapsed || selection.toString() == "";
			}
		}
	}
}, getSelectedElement:function () {
	if (dojo.html.selection.getType() == dojo.html.selectionType.CONTROL) {
		if (dojo.doc()["selection"]) {
			var range = dojo.doc().selection.createRange();
			if (range && range.item) {
				return dojo.doc().selection.createRange().item(0);
			}
		} else {
			var selection = dojo.global().getSelection();
			return selection.anchorNode.childNodes[selection.anchorOffset];
		}
	}
}, getParentElement:function () {
	if (dojo.html.selection.getType() == dojo.html.selectionType.CONTROL) {
		var p = dojo.html.selection.getSelectedElement();
		if (p) {
			return p.parentNode;
		}
	} else {
		if (dojo.doc()["selection"]) {
			return dojo.doc().selection.createRange().parentElement();
		} else {
			var selection = dojo.global().getSelection();
			if (selection) {
				var node = selection.anchorNode;
				while (node && node.nodeType != dojo.dom.ELEMENT_NODE) {
					node = node.parentNode;
				}
				return node;
			}
		}
	}
}, getSelectedText:function () {
	if (dojo.doc()["selection"]) {
		if (dojo.html.selection.getType() == dojo.html.selectionType.CONTROL) {
			return null;
		}
		return dojo.doc().selection.createRange().text;
	} else {
		var selection = dojo.global().getSelection();
		if (selection) {
			return selection.toString();
		}
	}
}, getSelectedHtml:function () {
	if (dojo.doc()["selection"]) {
		if (dojo.html.selection.getType() == dojo.html.selectionType.CONTROL) {
			return null;
		}
		return dojo.doc().selection.createRange().htmlText;
	} else {
		var selection = dojo.global().getSelection();
		if (selection && selection.rangeCount) {
			var frag = selection.getRangeAt(0).cloneContents();
			var div = document.createElement("div");
			div.appendChild(frag);
			return div.innerHTML;
		}
		return null;
	}
}, hasAncestorElement:function (tagName) {
	return (dojo.html.selection.getAncestorElement.apply(this, arguments) != null);
}, getAncestorElement:function (tagName) {
	var node = dojo.html.selection.getSelectedElement() || dojo.html.selection.getParentElement();
	while (node) {
		if (dojo.html.selection.isTag(node, arguments).length > 0) {
			return node;
		}
		node = node.parentNode;
	}
	return null;
}, isTag:function (node, tags) {
	if (node && node.tagName) {
		for (var i = 0; i < tags.length; i++) {
			if (node.tagName.toLowerCase() == String(tags[i]).toLowerCase()) {
				return String(tags[i]).toLowerCase();
			}
		}
	}
	return "";
}, selectElement:function (element) {
	var _window = dojo.global();
	var _document = dojo.doc();
	element = dojo.byId(element);
	if (_document.selection && dojo.body().createTextRange) {
		try {
			var range = dojo.body().createControlRange();
			range.addElement(element);
			range.select();
		}
		catch (e) {
			dojo.html.selection.selectElementChildren(element);
		}
	} else {
		if (_window["getSelection"]) {
			var selection = _window.getSelection();
			if (selection["removeAllRanges"]) {
				var range = _document.createRange();
				range.selectNode(element);
				selection.removeAllRanges();
				selection.addRange(range);
			}
		}
	}
}, selectElementChildren:function (element) {
	var _window = dojo.global();
	var _document = dojo.doc();
	element = dojo.byId(element);
	if (_document.selection && dojo.body().createTextRange) {
		var range = dojo.body().createTextRange();
		range.moveToElementText(element);
		range.select();
	} else {
		if (_window["getSelection"]) {
			var selection = _window.getSelection();
			if (selection["setBaseAndExtent"]) {
				selection.setBaseAndExtent(element, 0, element, element.innerText.length - 1);
			} else {
				if (selection["selectAllChildren"]) {
					selection.selectAllChildren(element);
				}
			}
		}
	}
}, getBookmark:function () {
	var bookmark;
	var _document = dojo.doc();
	if (_document["selection"]) {
		var range = _document.selection.createRange();
		bookmark = range.getBookmark();
	} else {
		var selection;
		try {
			selection = dojo.global().getSelection();
		}
		catch (e) {
		}
		if (selection) {
			var range = selection.getRangeAt(0);
			bookmark = range.cloneRange();
		} else {
			dojo.debug("No idea how to store the current selection for this browser!");
		}
	}
	return bookmark;
}, moveToBookmark:function (bookmark) {
	var _document = dojo.doc();
	if (_document["selection"]) {
		var range = _document.selection.createRange();
		range.moveToBookmark(bookmark);
		range.select();
	} else {
		var selection;
		try {
			selection = dojo.global().getSelection();
		}
		catch (e) {
		}
		if (selection && selection["removeAllRanges"]) {
			selection.removeAllRanges();
			selection.addRange(bookmark);
		} else {
			dojo.debug("No idea how to restore selection for this browser!");
		}
	}
}, collapse:function (beginning) {
	if (dojo.global()["getSelection"]) {
		var selection = dojo.global().getSelection();
		if (selection.removeAllRanges) {
			if (beginning) {
				selection.collapseToStart();
			} else {
				selection.collapseToEnd();
			}
		} else {
			dojo.global().getSelection().collapse(beginning);
		}
	} else {
		if (dojo.doc().selection) {
			var range = dojo.doc().selection.createRange();
			range.collapse(beginning);
			range.select();
		}
	}
}, remove:function () {
	if (dojo.doc().selection) {
		var selection = dojo.doc().selection;
		if (selection.type.toUpperCase() != "NONE") {
			selection.clear();
		}
		return selection;
	} else {
		var selection = dojo.global().getSelection();
		for (var i = 0; i < selection.rangeCount; i++) {
			selection.getRangeAt(i).deleteContents();
		}
		return selection;
	}
}});

