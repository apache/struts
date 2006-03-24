/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.html.ComboBox");
dojo.require("dojo.widget.ComboBox");
dojo.require("dojo.widget.*");
dojo.require("dojo.io.*");
dojo.require("dojo.fx.*");
dojo.require("dojo.dom");
dojo.require("dojo.html");
dojo.require("dojo.string");
dojo.require("dojo.widget.html.stabile");

dojo.widget.html.ComboBox = function(){
	dojo.widget.ComboBox.call(this);
	dojo.widget.HtmlWidget.call(this);

	this.autoComplete = true;
	this.formInputName = "";
	this.name = ""; // clone in the name from the DOM node
	this.textInputNode = null;
	this.comboBoxValue = null;
	this.comboBoxSelectionValue = null;
	this.optionsListNode = null;
	this.downArrowNode = null;
	this.cbTableNode = null;
	this.searchTimer = null;
	this.searchDelay = 100;
	this.dataUrl = "";
	// mode can also be "remote" for JSON-returning live search or "html" for
	// dumber live search
	this.mode = "local"; 
	this.selectedResult = null;
	this._highlighted_option = null;
	this._prev_key_backspace = false;
	this._prev_key_esc = false;
	this._result_list_open = false;
}

dojo.inherits(dojo.widget.html.ComboBox, dojo.widget.HtmlWidget);

// copied from superclass since we can't really over-ride via prototype
dojo.lang.extend(dojo.widget.html.ComboBox, dojo.widget.ComboBox.defaults);

dojo.lang.extend(dojo.widget.html.ComboBox, {


	templatePath: dojo.uri.dojoUri("src/widget/templates/HtmlComboBox.html"),
	templateCssPath: dojo.uri.dojoUri("src/widget/templates/HtmlComboBox.css"),

	setValue: function(value) {
		this.comboBoxValue.value = this.textInputNode.value = value;
		dojo.widget.html.stabile.setState(this.widgetId, this.getState(), true);
	},

	getValue: function() {
		return this.comboBoxValue.value;
	},

	getState: function() {
		return {value: this.getValue()};
	},

	setState: function(state) {
        	this.setValue(state.value);
	},


	getCaretPos: function(element){
		// FIXME: we need to figure this out for Konq/Safari!
		if(dojo.render.html.mozilla){
			// FIXME: this is totally borked on Moz < 1.3. Any recourse?
			return element.selectionStart;
		}else if(dojo.render.html.ie){
			// in the case of a mouse click in a popup being handled,
			// then the document.selection is not the textarea, but the popup
			// var r = document.selection.createRange();
			// hack to get IE 6 to play nice. What a POS browser.
			// var tr = r.duplicate();
			var tr = document.selection.createRange().duplicate();
			// var ntr = document.selection.createRange().duplicate();
			var ntr = element.createTextRange();
			// FIXME: this seems to work but I'm getting some execptions on reverse-tab
			tr.move("character",0);
			ntr.move("character",0);
			/*
			try{
				ntr.moveToElementText(element);
			}catch(e){ dojo.debug(e); }
			*/
			ntr.setEndPoint("EndToEnd", tr);
			return String(ntr.text).replace(/\r/g,"").length;
		}
	},

	setCaretPos: function(element, location){
		location = parseInt(location);
		this.setSelectedRange(element, location, location);
	},

	setSelectedRange: function(element, start, end){
		if(!end){ end = element.value.length; }
		// Mozilla
		// parts borrowed from http://www.faqts.com/knowledge_base/view.phtml/aid/13562/fid/130
		if(element.setSelectionRange){
			element.focus();
			element.setSelectionRange(start, end);
		}else if(element.createTextRange){ // IE
			var range = element.createTextRange();
			with(range){
				collapse(true);
				moveEnd('character', end);
				moveStart('character', start);
				select();
			}
		}else{ //otherwise try the event-creation hack (our own invention)
			// do we need these?
			element.value = element.value;
			element.blur();
			element.focus();
			// figure out how far back to go
			var dist = parseInt(element.value.length)-end;
			var tchar = String.fromCharCode(37);
			var tcc = tchar.charCodeAt(0);
			for(var x = 0; x < dist; x++){
				var te = document.createEvent("KeyEvents");
				te.initKeyEvent("keypress", true, true, null, false, false, false, false, tcc, tcc);
				twe.dispatchEvent(te);
			}
		}
	},

	killEvent: function(evt){
		evt.preventDefault();
		evt.stopPropagation();
	},

	onKeyDown: function(evt){
	},

	setSelectedValue: function(value){
		// FIXME, not sure what to do here!
		this.comboBoxSelectionValue.value = value;
		this.hideResultList();
	},

	highlightNextOption: function(){
		if(this._highlighted_option){
			dojo.html.removeClass(this._highlighted_option, "cbItemHighlight");
		}
		if((!this._highlighted_option)||(!this._highlighted_option.nextSibling)){
			this._highlighted_option = this.optionsListNode.firstChild;
		}else{
			this._highlighted_option = this._highlighted_option.nextSibling;
		}
		dojo.html.addClass(this._highlighted_option, "cbItemHighlight");
	},

	highlightPrevOption: function(){
		if(this._highlighted_option){
			dojo.html.removeClass(this._highlighted_option, "cbItemHighlight");
		}
		if((!this._highlighted_option)||(!this._highlighted_option.previousSibling)){
			this._highlighted_option = this.optionsListNode.lastChild;
		}else{
			this._highlighted_option = this._highlighted_option.previousSibling;
		}
		dojo.html.addClass(this._highlighted_option, "cbItemHighlight");
	},

	onKeyUp: function(evt){
		if(evt.keyCode == 27){ // esc is 27
			this.hideResultList();
			if(this._prev_key_esc){
				this.textInputNode.blur();
				this.selectedResult = null;
			}
			this._prev_key_esc = true;
			return;
		}else if((evt.keyCode == 32)||(evt.keyCode == 13)){ // space is 32, enter is 13.
			/*
			// Cancel the enter key event bubble to avoid submitting the form.
			if (evt.keyCode == 13) {
				// FIXME: the does not cancel the form submission.
				this.killEvent(evt);
			}
			*/
			// If the list is open select the option with the event.
			if(this._result_list_open){
				evt = { target: this._highlighted_option };
				this.selectOption(evt);
			}else{
				// Otherwise select the option with out the event.
				this.selectOption();
			}
			return;
		}else if(evt.keyCode == 40){ // down is 40
			if(!this._result_list_open){
				this.startSearchFromInput();
			}
			this.highlightNextOption();
			return;
		}else if(evt.keyCode == 38){ // up is 38
			this.highlightPrevOption();
			return;
		}else{
			this.setValue(this.textInputNode.value);
		}

		// backspace is 8
		this._prev_key_backspace = (evt.keyCode == 8) ? true : false;
		this._prev_key_esc = false;

		if(this.searchTimer){
			clearTimeout(this.searchTimer);
		}
		if((this._prev_key_backspace)&&(!this.textInputNode.value.length)){
			this.hideResultList();
		}else{
			this.searchTimer = setTimeout(dojo.lang.hitch(this, this.startSearchFromInput), this.searchDelay);
		}
	},

	fillInTemplate: function(args, frag){
		// FIXME: need to get/assign DOM node names for form participation here.
		this.comboBoxValue.name = this.name;
		this.comboBoxSelectionValue.name = this.name+"_selected";

		// FIXME: add logic
		this.dataProvider = new dojo.widget.ComboBoxDataProvider();

		if(!dojo.string.isBlank(this.dataUrl)){
			if("local" == this.mode){
				var _this = this;
				dojo.io.bind({
					url: this.dataUrl,
					load: function(type, data, evt){ 
						if(type=="load"){
							_this.dataProvider.setData(data);
						}
					},
					mimetype: "text/javascript"
				});
			}else if("remote" == this.mode){
				this.dataProvider = new dojo.widget.incrementalComboBoxDataProvider(this.dataUrl);
			}
		}else{
			// check to see if we can populate the list from <option> elements
			var node = frag["dojo:"+this.widgetType.toLowerCase()]["nodeRef"];
			if((node)&&(node.nodeName.toLowerCase() == "select")){
				// NOTE: we're not handling <optgroup> here yet
				var opts = node.getElementsByTagName("option");
				var ol = opts.length;
				var data = [];
				for(var x=0; x<ol; x++){
					data.push([new String(opts[x].innerHTML), new String(opts[x].value)]);
				}
				this.dataProvider.setData(data);
			}
		}
	},

	openResultList: function(results){
		this.clearResultList();
		if(!results.length){
			this.hideResultList();
		}else{
			this.showResultList();
		}
		if(	(this.autoComplete)&&
			(results.length)&&
			(!this._prev_key_backspace)&&
			(this.textInputNode.value.length > 0)){
			var cpos = this.getCaretPos(this.textInputNode);
			// only try to extend if we added the last charachter at the end of the input
			if((cpos+1) >= this.textInputNode.value.length){
				this.textInputNode.value = results[0][0];
				// build a new range that has the distance from the earlier
				// caret position to the end of the first string selected
				this.setSelectedRange(this.textInputNode, cpos, this.textInputNode.value.length);
			}
		}

		var even = true;
		while(results.length){
			var tr = results.shift();
			var td = document.createElement("div");
			td.appendChild(document.createTextNode(tr[0]));
			td.setAttribute("resultName", tr[0]);
			td.setAttribute("resultValue", tr[1]);
			td.className = "cbItem "+((even) ? "cbItemEven" : "cbItemOdd");
			even = (!even);
			this.optionsListNode.appendChild(td);
		}

		dojo.event.connect(this.optionsListNode, "onclick", this, "selectOption");
		dojo.event.kwConnect({
			once: true,
			srcObj: dojo.html.body(),
			srcFunc: "onclick", 
			adviceObj: this, 
			adviceFunc: "hideResultList"
		});
		// dojo.event.connect(dojo.html.body(), "onclick", this, "hideResultList");
	},

	selectOption: function(evt){
		if(!evt){
			evt = { target: this._highlighted_option };
		}

		if(!dojo.dom.isDescendantOf(evt.target, this.optionsListNode)){
			return;
		}

		var tgt = evt.target;
		while((tgt.nodeType!=1)||(!tgt.getAttribute("resultName"))){
			tgt = tgt.parentNode;
			if(tgt === dojo.html.body()){
				return false;
			}
		}

		this.textInputNode.value = tgt.getAttribute("resultName");
		this.selectedResult = [tgt.getAttribute("resultName"), tgt.getAttribute("resultValue")];
		this.setValue(tgt.getAttribute("resultName"));
		this.comboBoxSelectionValue.value = tgt.getAttribute("resultValue");
		this.hideResultList();
	},

	clearResultList: function(){
		var oln = this.optionsListNode;
		while(oln.firstChild){
			oln.removeChild(oln.firstChild);
		}
	},

	hideResultList: function(){
		dojo.fx.fadeHide(this.optionsListNode, 200);
		dojo.event.disconnect(dojo.html.body(), "onclick", this, "hideResultList");
		this._result_list_open = false;
		return;
	},

	showResultList: function(){
		if(this._result_list_open){ return; }
		with(this.optionsListNode.style){
			display = "";
			// visibility = "hidden";
			height = "";
			width = dojo.html.getInnerWidth(this.downArrowNode)+dojo.html.getInnerWidth(this.textInputNode)+"px";
			if(dojo.render.html.khtml){
				marginTop = dojo.html.totalOffsetTop(this.optionsListNode.parentNode)+"px";
			/*
				left = dojo.html.totalOffsetLeft(this.optionsListNode.parentNode)+3+"px";
				zIndex = "1000";
				position = "relative";
			*/
			}
		}
		dojo.html.setOpacity(this.optionsListNode, 0);
		dojo.fx.fadeIn(this.optionsListNode, 200);
		this._result_list_open = true;
		return;
	},

	handleArrowClick: function(){
		if(this._result_list_open){
			this.hideResultList();
		}else{
			this.startSearchFromInput();
		}
	},

	startSearchFromInput: function(){
		this.startSearch(this.textInputNode.value);
	},

	postCreate: function(){
		dojo.event.connect(this, "startSearch", this.dataProvider, "startSearch");
		dojo.event.connect(this.dataProvider, "provideSearchResults", this, "openResultList");
		var s = dojo.widget.html.stabile.getState(this.widgetId);
		if (s) {
			this.setState(s);
		}
	}

});
