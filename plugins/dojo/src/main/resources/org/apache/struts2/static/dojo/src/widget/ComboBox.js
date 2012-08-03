/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.ComboBox");
dojo.require("dojo.widget.*");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.html.*");
dojo.require("dojo.string");
dojo.require("dojo.widget.html.stabile");
dojo.require("dojo.widget.PopupContainer");
dojo.declare("dojo.widget.incrementalComboBoxDataProvider", null, function (options) {
	this.searchUrl = options.dataUrl;
	this._cache = {};
	this._inFlight = false;
	this._lastRequest = null;
	this.allowCache = false;
}, {_addToCache:function (keyword, data) {
	if (this.allowCache) {
		this._cache[keyword] = data;
	}
}, startSearch:function (searchStr, callback) {
	if (this._inFlight) {
	}
	var tss = encodeURIComponent(searchStr);
	var realUrl = dojo.string.substituteParams(this.searchUrl, {"searchString":tss});
	var _this = this;
	var request = this._lastRequest = dojo.io.bind({url:realUrl, method:"get", mimetype:"text/json", load:function (type, data, evt) {
		_this._inFlight = false;
		if (!dojo.lang.isArray(data)) {
			var arrData = [];
			for (var key in data) {
				arrData.push([data[key], key]);
			}
			data = arrData;
		}
		_this._addToCache(searchStr, data);
		if (request == _this._lastRequest) {
			callback(data);
		}
	}});
	this._inFlight = true;
}});
dojo.declare("dojo.widget.basicComboBoxDataProvider", null, function (options, node) {
	this._data = [];
	this.searchLimit = 30;
	this.searchType = "STARTSTRING";
	this.caseSensitive = false;
	if (!dj_undef("dataUrl", options) && !dojo.string.isBlank(options.dataUrl)) {
		this._getData(options.dataUrl);
	} else {
		if ((node) && (node.nodeName.toLowerCase() == "select")) {
			var opts = node.getElementsByTagName("option");
			var ol = opts.length;
			var data = [];
			for (var x = 0; x < ol; x++) {
				var text = opts[x].textContent || opts[x].innerText || opts[x].innerHTML;
				var keyValArr = [String(text), String(opts[x].value)];
				data.push(keyValArr);
				if (opts[x].selected) {
					options.setAllValues(keyValArr[0], keyValArr[1]);
				}
			}
			this.setData(data);
		}
	}
}, {_getData:function (url) {
	dojo.io.bind({url:url, load:dojo.lang.hitch(this, function (type, data, evt) {
		if (!dojo.lang.isArray(data)) {
			var arrData = [];
			for (var key in data) {
				arrData.push([data[key], key]);
			}
			data = arrData;
		}
		this.setData(data);
	}), mimetype:"text/json"});
}, startSearch:function (searchStr, callback) {
	this._performSearch(searchStr, callback);
}, _performSearch:function (searchStr, callback) {
	var st = this.searchType;
	var ret = [];
	if (!this.caseSensitive) {
		searchStr = searchStr.toLowerCase();
	}
	for (var x = 0; x < this._data.length; x++) {
		if ((this.searchLimit > 0) && (ret.length >= this.searchLimit)) {
			break;
		}
		var dataLabel = new String((!this.caseSensitive) ? this._data[x][0].toLowerCase() : this._data[x][0]);
		if (dataLabel.length < searchStr.length) {
			continue;
		}
		if (st == "STARTSTRING") {
			if (searchStr == dataLabel.substr(0, searchStr.length)) {
				ret.push(this._data[x]);
			}
		} else {
			if (st == "SUBSTRING") {
				if (dataLabel.indexOf(searchStr) >= 0) {
					ret.push(this._data[x]);
				}
			} else {
				if (st == "STARTWORD") {
					var idx = dataLabel.indexOf(searchStr);
					if (idx == 0) {
						ret.push(this._data[x]);
					}
					if (idx <= 0) {
						continue;
					}
					var matches = false;
					while (idx != -1) {
						if (" ,/(".indexOf(dataLabel.charAt(idx - 1)) != -1) {
							matches = true;
							break;
						}
						idx = dataLabel.indexOf(searchStr, idx + 1);
					}
					if (!matches) {
						continue;
					} else {
						ret.push(this._data[x]);
					}
				}
			}
		}
	}
	callback(ret);
}, setData:function (pdata) {
	this._data = pdata;
}});
dojo.widget.defineWidget("dojo.widget.ComboBox", dojo.widget.HtmlWidget, {forceValidOption:false, searchType:"stringstart", dataProvider:null, autoComplete:true, searchDelay:100, dataUrl:"", fadeTime:200, maxListLength:8, mode:"local", selectedResult:null, dataProviderClass:"", buttonSrc:dojo.uri.moduleUri("dojo.widget", "templates/images/combo_box_arrow.png"), dropdownToggle:"fade", templateString:"<span _=\"whitespace and CR's between tags adds &nbsp; in FF\"\n\tclass=\"dojoComboBoxOuter\"\n\t><input style=\"display:none\"  tabindex=\"-1\" name=\"\" value=\"\" \n\t\tdojoAttachPoint=\"comboBoxValue\"\n\t><input style=\"display:none\"  tabindex=\"-1\" name=\"\" value=\"\" \n\t\tdojoAttachPoint=\"comboBoxSelectionValue\"\n\t><input type=\"text\" autocomplete=\"off\" class=\"dojoComboBox\"\n\t\tdojoAttachEvent=\"key:_handleKeyEvents; keyUp: onKeyUp; compositionEnd; onResize;\"\n\t\tdojoAttachPoint=\"textInputNode\"\n\t><img hspace=\"0\"\n\t\tvspace=\"0\"\n\t\tclass=\"dojoComboBox\"\n\t\tdojoAttachPoint=\"downArrowNode\"\n\t\tdojoAttachEvent=\"onMouseUp: handleArrowClick; onResize;\"\n\t\tsrc=\"${this.buttonSrc}\"\n></span>\n", templateCssString:".dojoComboBoxOuter {\n\tborder: 0px !important;\n\tmargin: 0px !important;\n\tpadding: 0px !important;\n\tbackground: transparent !important;\n\twhite-space: nowrap !important;\n}\n\n.dojoComboBox {\n\tborder: 1px inset #afafaf;\n\tmargin: 0px;\n\tpadding: 0px;\n\tvertical-align: middle !important;\n\tfloat: none !important;\n\tposition: static !important;\n\tdisplay: inline !important;\n}\n\n/* the input box */\ninput.dojoComboBox {\n\tborder-right-width: 0px !important; \n\tmargin-right: 0px !important;\n\tpadding-right: 0px !important;\n}\n\n/* the down arrow */\nimg.dojoComboBox {\n\tborder-left-width: 0px !important;\n\tpadding-left: 0px !important;\n\tmargin-left: 0px !important;\n}\n\n/* IE vertical-alignment calculations can be off by +-1 but these margins are collapsed away */\n.dj_ie img.dojoComboBox {\n\tmargin-top: 1px; \n\tmargin-bottom: 1px; \n}\n\n/* the drop down */\n.dojoComboBoxOptions {\n\tfont-family: Verdana, Helvetica, Garamond, sans-serif;\n\t/* font-size: 0.7em; */\n\tbackground-color: white;\n\tborder: 1px solid #afafaf;\n\tposition: absolute;\n\tz-index: 1000; \n\toverflow: auto;\n\tcursor: default;\n}\n\n.dojoComboBoxItem {\n\tpadding-left: 2px;\n\tpadding-top: 2px;\n\tmargin: 0px;\n}\n\n.dojoComboBoxItemEven {\n\tbackground-color: #f4f4f4;\n}\n\n.dojoComboBoxItemOdd {\n\tbackground-color: white;\n}\n\n.dojoComboBoxItemHighlight {\n\tbackground-color: #63709A;\n\tcolor: white;\n}\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/ComboBox.css"), setValue:function (value) {
	this.comboBoxValue.value = value;
	if (this.textInputNode.value != value) {
		this.textInputNode.value = value;
		dojo.widget.html.stabile.setState(this.widgetId, this.getState(), true);
		this.onValueChanged(value);
	}
}, onValueChanged:function (value) {
}, getValue:function () {
	return this.comboBoxValue.value;
}, getState:function () {
	return {value:this.getValue()};
}, setState:function (state) {
	this.setValue(state.value);
}, enable:function () {
	this.disabled = false;
	this.textInputNode.removeAttribute("disabled");
}, disable:function () {
	this.disabled = true;
	this.textInputNode.setAttribute("disabled", true);
}, _getCaretPos:function (element) {
	if (dojo.lang.isNumber(element.selectionStart)) {
		return element.selectionStart;
	} else {
		if (dojo.render.html.ie) {
			var tr = document.selection.createRange().duplicate();
			var ntr = element.createTextRange();
			tr.move("character", 0);
			ntr.move("character", 0);
			try {
				ntr.setEndPoint("EndToEnd", tr);
				return String(ntr.text).replace(/\r/g, "").length;
			}
			catch (e) {
				return 0;
			}
		}
	}
}, _setCaretPos:function (element, location) {
	location = parseInt(location);
	this._setSelectedRange(element, location, location);
}, _setSelectedRange:function (element, start, end) {
	if (!end) {
		end = element.value.length;
	}
	if (element.setSelectionRange) {
		element.focus();
		element.setSelectionRange(start, end);
	} else {
		if (element.createTextRange) {
			var range = element.createTextRange();
			with (range) {
				collapse(true);
				moveEnd("character", end);
				moveStart("character", start);
				select();
			}
		} else {
			element.value = element.value;
			element.blur();
			element.focus();
			var dist = parseInt(element.value.length) - end;
			var tchar = String.fromCharCode(37);
			var tcc = tchar.charCodeAt(0);
			for (var x = 0; x < dist; x++) {
				var te = document.createEvent("KeyEvents");
				te.initKeyEvent("keypress", true, true, null, false, false, false, false, tcc, tcc);
				element.dispatchEvent(te);
			}
		}
	}
}, _handleKeyEvents:function (evt) {
	if (evt.ctrlKey || evt.altKey || !evt.key) {
		return;
	}
	this._prev_key_backspace = false;
	this._prev_key_esc = false;
	var k = dojo.event.browser.keys;
	var doSearch = true;
	switch (evt.key) {
	  case k.KEY_DOWN_ARROW:
		if (!this.popupWidget.isShowingNow) {
			this._startSearchFromInput();
		}
		this._highlightNextOption();
		dojo.event.browser.stopEvent(evt);
		return;
	  case k.KEY_UP_ARROW:
		this._highlightPrevOption();
		dojo.event.browser.stopEvent(evt);
		return;
	  case k.KEY_TAB:
		if (!this.autoComplete && this.popupWidget.isShowingNow && this._highlighted_option) {
			dojo.event.browser.stopEvent(evt);
			this._selectOption({"target":this._highlighted_option, "noHide":false});
			this._setSelectedRange(this.textInputNode, this.textInputNode.value.length, null);
		} else {
			this._selectOption();
			return;
		}
		break;
	  case k.KEY_ENTER:
		if (this.popupWidget.isShowingNow) {
			dojo.event.browser.stopEvent(evt);
		}
		if (this.autoComplete) {
			this._selectOption();
			return;
		}
	  case " ":
		if (this.popupWidget.isShowingNow && this._highlighted_option) {
			dojo.event.browser.stopEvent(evt);
			this._selectOption();
			this._hideResultList();
			return;
		}
		break;
	  case k.KEY_ESCAPE:
		this._hideResultList();
		this._prev_key_esc = true;
		return;
	  case k.KEY_BACKSPACE:
		this._prev_key_backspace = true;
		if (!this.textInputNode.value.length) {
			this.setAllValues("", "");
			this._hideResultList();
			doSearch = false;
		}
		break;
	  case k.KEY_RIGHT_ARROW:
	  case k.KEY_LEFT_ARROW:
		doSearch = false;
		break;
	  default:
		if (evt.charCode == 0) {
			doSearch = false;
		}
	}
	if (this.searchTimer) {
		clearTimeout(this.searchTimer);
	}
	if (doSearch) {
		this._blurOptionNode();
		this.searchTimer = setTimeout(dojo.lang.hitch(this, this._startSearchFromInput), this.searchDelay);
	}
}, compositionEnd:function (evt) {
	evt.key = evt.keyCode;
	this._handleKeyEvents(evt);
}, onKeyUp:function (evt) {
	this.setValue(this.textInputNode.value);
}, setSelectedValue:function (value) {
	this.comboBoxSelectionValue.value = value;
}, setAllValues:function (value1, value2) {
	this.setSelectedValue(value2);
	this.setValue(value1);
}, _focusOptionNode:function (node) {
	if (this._highlighted_option != node) {
		this._blurOptionNode();
		this._highlighted_option = node;
		dojo.html.addClass(this._highlighted_option, "dojoComboBoxItemHighlight");
	}
}, _blurOptionNode:function () {
	if (this._highlighted_option) {
		dojo.html.removeClass(this._highlighted_option, "dojoComboBoxItemHighlight");
		this._highlighted_option = null;
	}
}, _highlightNextOption:function () {
	if ((!this._highlighted_option) || !this._highlighted_option.parentNode) {
		this._focusOptionNode(this.optionsListNode.firstChild);
	} else {
		if (this._highlighted_option.nextSibling) {
			this._focusOptionNode(this._highlighted_option.nextSibling);
		}
	}
	dojo.html.scrollIntoView(this._highlighted_option);
}, _highlightPrevOption:function () {
	if (this._highlighted_option && this._highlighted_option.previousSibling) {
		this._focusOptionNode(this._highlighted_option.previousSibling);
	} else {
		this._highlighted_option = null;
		this._hideResultList();
		return;
	}
	dojo.html.scrollIntoView(this._highlighted_option);
}, _itemMouseOver:function (evt) {
	if (evt.target === this.optionsListNode) {
		return;
	}
	this._focusOptionNode(evt.target);
	dojo.html.addClass(this._highlighted_option, "dojoComboBoxItemHighlight");
}, _itemMouseOut:function (evt) {
	if (evt.target === this.optionsListNode) {
		return;
	}
	this._blurOptionNode();
}, onResize:function () {
	var inputSize = dojo.html.getContentBox(this.textInputNode);
	if (inputSize.height <= 0) {
		dojo.lang.setTimeout(this, "onResize", 100);
		return;
	}
	var buttonSize = {width:inputSize.height, height:inputSize.height};
	dojo.html.setContentBox(this.downArrowNode, buttonSize);
}, fillInTemplate:function (args, frag) {
	dojo.html.applyBrowserClass(this.domNode);
	var source = this.getFragNodeRef(frag);
	if (!this.name && source.name) {
		this.name = source.name;
	}
	this.comboBoxValue.name = this.name;
	this.comboBoxSelectionValue.name = this.name + "_selected";
	dojo.html.copyStyle(this.domNode, source);
	dojo.html.copyStyle(this.textInputNode, source);
	dojo.html.copyStyle(this.downArrowNode, source);
	with (this.downArrowNode.style) {
		width = "0px";
		height = "0px";
	}
	var dpClass;
	if (this.dataProviderClass) {
		if (typeof this.dataProviderClass == "string") {
			dpClass = dojo.evalObjPath(this.dataProviderClass);
		} else {
			dpClass = this.dataProviderClass;
		}
	} else {
		if (this.mode == "remote") {
			dpClass = dojo.widget.incrementalComboBoxDataProvider;
		} else {
			dpClass = dojo.widget.basicComboBoxDataProvider;
		}
	}
	this.dataProvider = new dpClass(this, this.getFragNodeRef(frag));
	this.popupWidget = new dojo.widget.createWidget("PopupContainer", {toggle:this.dropdownToggle, toggleDuration:this.toggleDuration});
	dojo.event.connect(this, "destroy", this.popupWidget, "destroy");
	this.optionsListNode = this.popupWidget.domNode;
	this.domNode.appendChild(this.optionsListNode);
	dojo.html.addClass(this.optionsListNode, "dojoComboBoxOptions");
	dojo.event.connect(this.optionsListNode, "onclick", this, "_selectOption");
	dojo.event.connect(this.optionsListNode, "onmouseover", this, "_onMouseOver");
	dojo.event.connect(this.optionsListNode, "onmouseout", this, "_onMouseOut");
	dojo.event.connect(this.optionsListNode, "onmouseover", this, "_itemMouseOver");
	dojo.event.connect(this.optionsListNode, "onmouseout", this, "_itemMouseOut");
}, _openResultList:function (results) {
	if (this.disabled) {
		return;
	}
	this._clearResultList();
	if (!results.length) {
		this._hideResultList();
	}
	if ((this.autoComplete) && (results.length) && (!this._prev_key_backspace) && (this.textInputNode.value.length > 0)) {
		var cpos = this._getCaretPos(this.textInputNode);
		if ((cpos + 1) > this.textInputNode.value.length) {
			this.textInputNode.value += results[0][0].substr(cpos);
			this._setSelectedRange(this.textInputNode, cpos, this.textInputNode.value.length);
		}
	}
	var even = true;
	while (results.length) {
		var tr = results.shift();
		if (tr) {
			var td = document.createElement("div");
			td.appendChild(document.createTextNode(tr[0]));
			td.setAttribute("resultName", tr[0]);
			td.setAttribute("resultValue", tr[1]);
			td.className = "dojoComboBoxItem " + ((even) ? "dojoComboBoxItemEven" : "dojoComboBoxItemOdd");
			even = (!even);
			this.optionsListNode.appendChild(td);
		}
	}
	this._showResultList();
}, _onFocusInput:function () {
	this._hasFocus = true;
}, _onBlurInput:function () {
	this._hasFocus = false;
	this._handleBlurTimer(true, 500);
}, _handleBlurTimer:function (clear, millisec) {
	if (this.blurTimer && (clear || millisec)) {
		clearTimeout(this.blurTimer);
	}
	if (millisec) {
		this.blurTimer = dojo.lang.setTimeout(this, "_checkBlurred", millisec);
	}
}, _onMouseOver:function (evt) {
	if (!this._mouseover_list) {
		this._handleBlurTimer(true, 0);
		this._mouseover_list = true;
	}
}, _onMouseOut:function (evt) {
	var relTarget = evt.relatedTarget;
	try {
		if (!relTarget || relTarget.parentNode != this.optionsListNode) {
			this._mouseover_list = false;
			this._handleBlurTimer(true, 100);
			this._tryFocus();
		}
	}
	catch (e) {
	}
}, _isInputEqualToResult:function (result) {
	var input = this.textInputNode.value;
	if (!this.dataProvider.caseSensitive) {
		input = input.toLowerCase();
		result = result.toLowerCase();
	}
	return (input == result);
}, _isValidOption:function () {
	var tgt = dojo.html.firstElement(this.optionsListNode);
	var isValidOption = false;
	while (!isValidOption && tgt) {
		if (this._isInputEqualToResult(tgt.getAttribute("resultName"))) {
			isValidOption = true;
		} else {
			tgt = dojo.html.nextElement(tgt);
		}
	}
	return isValidOption;
}, _checkBlurred:function () {
	if (!this._hasFocus && !this._mouseover_list) {
		this._hideResultList();
		if (!this.textInputNode.value.length) {
			this.setAllValues("", "");
			return;
		}
		var isValidOption = this._isValidOption();
		if (this.forceValidOption && !isValidOption) {
			this.setAllValues("", "");
			return;
		}
		if (!isValidOption) {
			this.setSelectedValue("");
		}
	}
}, _selectOption:function (evt) {
	var tgt = null;
	if (!evt) {
		evt = {target:this._highlighted_option};
	}
	if (!dojo.html.isDescendantOf(evt.target, this.optionsListNode)) {
		if (!this.textInputNode.value.length) {
			return;
		}
		tgt = dojo.html.firstElement(this.optionsListNode);
		if (!tgt || !this._isInputEqualToResult(tgt.getAttribute("resultName"))) {
			return;
		}
	} else {
		tgt = evt.target;
	}
	while ((tgt.nodeType != 1) || (!tgt.getAttribute("resultName"))) {
		tgt = tgt.parentNode;
		if (tgt === dojo.body()) {
			return false;
		}
	}
    this.textInputNode.value="";
    this.selectedResult = [tgt.getAttribute("resultName"), tgt.getAttribute("resultValue")];
	this.setAllValues(tgt.getAttribute("resultName"), tgt.getAttribute("resultValue"));
	if (!evt.noHide) {
		this._hideResultList();
		this._setSelectedRange(this.textInputNode, 0, null);
	}
	this._tryFocus();
}, _clearResultList:function () {
	if (this.optionsListNode.innerHTML) {
		this.optionsListNode.innerHTML = "";
	}
}, _hideResultList:function () {
	this.popupWidget.close();
}, _showResultList:function () {
	var childs = this.optionsListNode.childNodes;
	if (childs.length) {
		var visibleCount = Math.min(childs.length, this.maxListLength);
		with (this.optionsListNode.style) {
			display = "";
			if (visibleCount == childs.length) {
				height = "";
			} else {
				height = visibleCount * dojo.html.getMarginBox(childs[0]).height + "px";
			}
			width = (dojo.html.getMarginBox(this.domNode).width - 2) + "px";
		}
		this.popupWidget.open(this.domNode, this, this.downArrowNode);
	} else {
		this._hideResultList();
	}
}, handleArrowClick:function () {
	this._handleBlurTimer(true, 0);
	this._tryFocus();
	if (this.popupWidget.isShowingNow) {
		this._hideResultList();
	} else {
		this._startSearch("");
	}
}, _tryFocus:function () {
	try {
		this.textInputNode.focus();
	}
	catch (e) {
	}
}, _startSearchFromInput:function () {
	this._startSearch(this.textInputNode.value);
}, _startSearch:function (key) {
	this.dataProvider.startSearch(key, dojo.lang.hitch(this, "_openResultList"));
}, postCreate:function () {
	this.onResize();
	dojo.event.connect(this.textInputNode, "onblur", this, "_onBlurInput");
	dojo.event.connect(this.textInputNode, "onfocus", this, "_onFocusInput");
	if (this.disabled) {
		this.disable();
	}
	var s = dojo.widget.html.stabile.getState(this.widgetId);
	if (s) {
		this.setState(s);
	}
}});

