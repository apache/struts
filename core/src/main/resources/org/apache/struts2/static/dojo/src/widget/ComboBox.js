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
dojo.require("dojo.lfx.*");
dojo.require("dojo.html.*");
dojo.require("dojo.html.display");
dojo.require("dojo.html.layout");
dojo.require("dojo.html.iframe");
dojo.require("dojo.string");
dojo.require("dojo.widget.html.stabile");
dojo.require("dojo.widget.PopupContainer");

dojo.widget.incrementalComboBoxDataProvider = function(/*String*/ url, /*Number*/ limit, /*Number*/ timeout){
	this.searchUrl = url;
	this.inFlight = false;
	this.activeRequest = null;
	this.allowCache = false;

	this.cache = {};

	this.init = function(/*Widget*/ cbox){
		this.searchUrl = cbox.dataUrl;
	};

	this.addToCache = function(/*String*/ keyword, /*Array*/ data){
		if(this.allowCache){
			this.cache[keyword] = data;
		}
	};

	this.startSearch = function(/*String*/ searchStr, /*String*/ type, /*Boolean*/ ignoreLimit){
		if(this.inFlight){
			// FIXME: implement backoff!
		}
		var tss = encodeURIComponent(searchStr);
		var realUrl = dojo.string.substituteParams(this.searchUrl, {"searchString": tss});
		var _this = this;
		var request = dojo.io.bind({
			url: realUrl,
			method: "get",
			mimetype: "text/json",
			load: function(type, data, evt){
				_this.inFlight = false;
				if(!dojo.lang.isArray(data)){
					var arrData = [];
					for(var key in data){
						arrData.push([data[key], key]);
					}
					data = arrData;
				}
				_this.addToCache(searchStr, data);
				_this.provideSearchResults(data);
			}
		});
		this.inFlight = true;
	};
};

dojo.widget.ComboBoxDataProvider = function(/*Array*/ dataPairs, /*Number*/ limit, /*Number*/ timeout){
	// NOTE: this data provider is designed as a naive reference
	// implementation, and as such it is written more for readability than
	// speed. A deployable data provider would implement lookups, search
	// caching (and invalidation), and a significantly less naive data
	// structure for storage of items.

	this.data = [];
	this.searchTimeout = timeout || 500;
	this.searchLimit = limit || 30;
	this.searchType = "STARTSTRING"; // may also be "STARTWORD" or "SUBSTRING"
	this.caseSensitive = false;
	// for caching optimizations
	this._lastSearch = "";
	this._lastSearchResults = null;

	this.init = function(/*Widget*/ cbox, /*DomNode*/ node){
		if(!dojo.string.isBlank(cbox.dataUrl)){
			this.getData(cbox.dataUrl);
		}else{
			// check to see if we can populate the list from <option> elements
			if((node)&&(node.nodeName.toLowerCase() == "select")){
				// NOTE: we're not handling <optgroup> here yet
				var opts = node.getElementsByTagName("option");
				var ol = opts.length;
				var data = [];
				for(var x=0; x<ol; x++){
					var text = opts[x].textContent || opts[x].innerText || opts[x].innerHTML;
					var keyValArr = [String(text), String(opts[x].value)];
					data.push(keyValArr);
					if(opts[x].selected){
						cbox.setAllValues(keyValArr[0], keyValArr[1]);
					}
				}
				this.setData(data);
			}
		}
	};

	this.getData = function(/*String*/ url){
		dojo.io.bind({
			url: url,
			load: dojo.lang.hitch(this, function(type, data, evt){
				if(!dojo.lang.isArray(data)){
					var arrData = [];
					for(var key in data){
						arrData.push([data[key], key]);
					}
					data = arrData;
				}
				this.setData(data);
			}),
			mimetype: "text/json"
		});
	};

	this.startSearch = function(/*String*/ searchStr, /*String*/ type, /*Boolean*/ ignoreLimit){
		// FIXME: need to add timeout handling here!!
		this._preformSearch(searchStr, type, ignoreLimit);
	};

	this._preformSearch = function(/*String*/ searchStr, /*String*/ type, /*Boolean*/ ignoreLimit){
		//
		//	NOTE: this search is LINEAR, which means that it exhibits perhaps
		//	the worst possible speed characteristics of any search type. It's
		//	written this way to outline the responsibilities and interfaces for
		//	a search.
		//
		var st = type||this.searchType;
		// FIXME: this is just an example search, which means that we implement
		// only a linear search without any of the attendant (useful!) optimizations
		var ret = [];
		if(!this.caseSensitive){
			searchStr = searchStr.toLowerCase();
		}
		for(var x=0; x<this.data.length; x++){
			if((!ignoreLimit)&&(ret.length >= this.searchLimit)){
				break;
			}
			// FIXME: we should avoid copies if possible!
			var dataLabel = new String((!this.caseSensitive) ? this.data[x][0].toLowerCase() : this.data[x][0]);
			if(dataLabel.length < searchStr.length){
				// this won't ever be a good search, will it? What if we start
				// to support regex search?
				continue;
			}

			if(st == "STARTSTRING"){
				if(searchStr == dataLabel.substr(0, searchStr.length)){
					ret.push(this.data[x]);
				}
			}else if(st == "SUBSTRING"){
				// this one is a gimmie
				if(dataLabel.indexOf(searchStr) >= 0){
					ret.push(this.data[x]);
				}
			}else if(st == "STARTWORD"){
				// do a substring search and then attempt to determine if the
				// preceeding char was the beginning of the string or a
				// whitespace char.
				var idx = dataLabel.indexOf(searchStr);
				if(idx == 0){
					// implicit match
					ret.push(this.data[x]);
				}
				if(idx <= 0){
					// if we didn't match or implicily matched, march onward
					continue;
				}
				// otherwise, we have to go figure out if the match was at the
				// start of a word...
				// this code is taken almost directy from nWidgets
				var matches = false;
				while(idx!=-1){
					// make sure the match either starts whole string, or
					// follows a space, or follows some punctuation
					if(" ,/(".indexOf(dataLabel.charAt(idx-1)) != -1){
						// FIXME: what about tab chars?
						matches = true; break;
					}
					idx = dataLabel.indexOf(searchStr, idx+1);
				}
				if(!matches){
					continue;
				}else{
					ret.push(this.data[x]);
				}
			}
		}
		this.provideSearchResults(ret);
	};

	this.provideSearchResults = function(/*Array*/ resultsDataPairs){
	};

	this.addData = function(/*Array*/ pairs){
		// FIXME: incredibly naive and slow!
		this.data = this.data.concat(pairs);
	};

	this.setData = function(/*Array*/ pdata){
		// populate this.data and initialize lookup structures
		this.data = pdata;
	};

	if(dataPairs){
		this.setData(dataPairs);
	}
};

dojo.widget.defineWidget(
	"dojo.widget.ComboBox",
	dojo.widget.HtmlWidget,
	{
		// Applies to any renderer
		isContainer: false,

		forceValidOption: false,
		searchType: "STARTSTRING",
		dataProvider: null,

		startSearch: function(/*String*/ searchString){},
		selectNextResult: function(){},
		selectPrevResult: function(){},
		setSelectedResult: function(){},

		// HTML specific stuff
		autoComplete: true,
		name: "", // clone in the name from the DOM node
		textInputNode: null,
		comboBoxValue: null,
		comboBoxSelectionValue: null,
		optionsListWrapper: null,
		optionsListNode: null,
		downArrowNode: null,
		searchTimer: null,
		searchDelay: 100,
		dataUrl: "",
		fadeTime: 200,
		disabled: false,
		// maxListLength limits list to X visible rows, scroll on rest
		maxListLength: 8,
		// mode can also be "remote" for JSON-returning live search or "html" for
		// dumber live search
		mode: "local",
		selectedResult: null,
		_highlighted_option: null,
		_prev_key_backspace: false,
		_prev_key_esc: false,
		_gotFocus: false,
		_mouseover_list: false,
		dataProviderClass: "dojo.widget.ComboBoxDataProvider",
		buttonSrc: dojo.uri.dojoUri("src/widget/templates/images/combo_box_arrow.png"),

		//the old implementation has builtin fade toggle, so we mimic it here
		dropdownToggle: "fade",

		templatePath: dojo.uri.dojoUri("src/widget/templates/ComboBox.html"),
		templateCssPath: dojo.uri.dojoUri("src/widget/templates/ComboBox.css"),


		setValue: function(/*String*/ value){
			this.comboBoxValue.value = value;
			if (this.textInputNode.value != value){ // prevent mucking up of selection
				this.textInputNode.value = value;
				// only change state and value if a new value is set
				dojo.widget.html.stabile.setState(this.widgetId, this.getState(), true);
				this.onValueChanged(value);
			}
		},

		// for user to override
		onValueChanged: function(){ },

		getValue: function(){
			return this.comboBoxValue.value;
		},

		getState: function(){
			return {value: this.getValue()};
		},

		setState: function(/*Object*/ state){
			this.setValue(state.value);
		},

		enable:function(){
			this.disabled=false;
			this.isEnabled = true;
			this.textInputNode.removeAttribute("disabled");
		},

		disable: function(){
			this.disabled = true;
			this.isEnabled = false;
			this.textInputNode.setAttribute("disabled",true);
		},

		getCaretPos: function(/*DomNode*/ element){
			// khtml 3.5.2 has selection* methods as does webkit nightlies from 2005-06-22
			if(dojo.lang.isNumber(element.selectionStart)){
				// FIXME: this is totally borked on Moz < 1.3. Any recourse?
				return element.selectionStart;
			}else if(dojo.render.html.ie){
				// in the case of a mouse click in a popup being handled,
				// then the document.selection is not the textarea, but the popup
				// var r = document.selection.createRange();
				// hack to get IE 6 to play nice. What a POS browser.
				var tr = document.selection.createRange().duplicate();
				var ntr = element.createTextRange();
				tr.move("character",0);
				ntr.move("character",0);
				try {
					// If control doesnt have focus, you get an exception.
					// Seems to happen on reverse-tab, but can also happen on tab (seems to be a race condition - only happens sometimes).
					// There appears to be no workaround for this - googled for quite a while.
					ntr.setEndPoint("EndToEnd", tr);
					return String(ntr.text).replace(/\r/g,"").length;
				} catch (e){
					return 0; // If focus has shifted, 0 is fine for caret pos.
				}

			}
		},

		setCaretPos: function(/*DomNode*/ element, /*Number*/ location){
			location = parseInt(location);
			this.setSelectedRange(element, location, location);
		},

		setSelectedRange: function(/*DomNode*/ element, /*Number*/ start, /*Number*/ end){
			if(!end){ end = element.value.length; }  // NOTE: Strange - should be able to put caret at start of text?
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
					element.dispatchEvent(te);
				}
			}
		},

		// does the keyboard related stuff
		_handleKeyEvents: function(/*Event*/ evt){
			if(evt.ctrlKey || evt.altKey || !evt.key){ return; }

			// reset these
			this._prev_key_backspace = false;
			this._prev_key_esc = false;

			var k = dojo.event.browser.keys;
			var doSearch = true;

			switch(evt.key){
	 			case k.KEY_DOWN_ARROW:
					if(!this.popupWidget.isShowingNow){
						this.startSearchFromInput();
					}
					this.highlightNextOption();
					dojo.event.browser.stopEvent(evt);
					return;
				case k.KEY_UP_ARROW:
					this.highlightPrevOption();
					dojo.event.browser.stopEvent(evt);
					return;
				case k.KEY_TAB:
					// using linux alike tab for autocomplete
					if(!this.autoComplete && this.popupWidget.isShowingNow && this._highlighted_option){
						dojo.event.browser.stopEvent(evt);
						this.selectOption({ 'target': this._highlighted_option, 'noHide': false});

						// put caret last
						this.setSelectedRange(this.textInputNode, this.textInputNode.value.length, null);
					}else{
						this.selectOption();
						return;
					}
					break;
				case k.KEY_ENTER:
					// prevent submitting form if we press enter with list open
					if(this.popupWidget.isShowingNow){
						dojo.event.browser.stopEvent(evt);
					}
					if(this.autoComplete){
						this.selectOption();
						return;
					}
					// fallthrough
				case " ":
					if(this.popupWidget.isShowingNow && this._highlighted_option){
						dojo.event.browser.stopEvent(evt);
						this.selectOption();
						this.hideResultList();
						return;
					}
					break;
				case k.KEY_ESCAPE:
					this.hideResultList();
					this._prev_key_esc = true;
					return;
				case k.KEY_BACKSPACE:
					this._prev_key_backspace = true;
					if(!this.textInputNode.value.length){
						this.setAllValues("", "");
						this.hideResultList();
						doSearch = false;
					}
					break;
				case k.KEY_RIGHT_ARROW: // fall through
				case k.KEY_LEFT_ARROW: // fall through
					doSearch = false;
					break;
				default:// non char keys (F1-F12 etc..)  shouldn't open list
					if(evt.charCode==0){
						doSearch = false;
					}
			}

			if(this.searchTimer){
				clearTimeout(this.searchTimer);
			}
			if(doSearch){
				// if we have gotten this far we dont want to keep our highlight
				this.blurOptionNode();

				// need to wait a tad before start search so that the event bubbles through DOM and we have value visible
				this.searchTimer = setTimeout(dojo.lang.hitch(this, this.startSearchFromInput), this.searchDelay);
			}
		},

		// When inputting characters using an input method, such as Asian
		// languages, it will generate this event instead of onKeyDown event
		compositionEnd: function(/*Event*/ evt){
			evt.key = evt.keyCode;
			this._handleKeyEvents(evt);
		},

		onKeyUp: function(/*Event*/ evt){
			this.setValue(this.textInputNode.value);
		},

		setSelectedValue: function(/*String*/ value){
			// FIXME, not sure what to do here!
			this.comboBoxSelectionValue.value = value;
		},

		setAllValues: function(/*String*/ value1, /*String*/ value2){
			this.setSelectedValue(value2);
			this.setValue(value1);
		},

		// does the actual highlight
		focusOptionNode: function(/*DomNode*/ node){
			if(this._highlighted_option != node){
				this.blurOptionNode();
				this._highlighted_option = node;
				dojo.html.addClass(this._highlighted_option, "dojoComboBoxItemHighlight");
			}
		},

		// removes highlight on highlighted
		blurOptionNode: function(){
			if(this._highlighted_option){
				dojo.html.removeClass(this._highlighted_option, "dojoComboBoxItemHighlight");
				this._highlighted_option = null;
			}
		},

		highlightNextOption: function(){
			if((!this._highlighted_option) || !this._highlighted_option.parentNode){
				this.focusOptionNode(this.optionsListNode.firstChild);
			}else if(this._highlighted_option.nextSibling){
				this.focusOptionNode(this._highlighted_option.nextSibling);
			}
			dojo.html.scrollIntoView(this._highlighted_option);
		},

		highlightPrevOption: function(){
			if(this._highlighted_option && this._highlighted_option.previousSibling){
				this.focusOptionNode(this._highlighted_option.previousSibling);
			}else{
				this._highlighted_option = null;
				this.hideResultList();
				return;
			}
			dojo.html.scrollIntoView(this._highlighted_option);
		},

		itemMouseOver: function(/*Event*/ evt){
			if (evt.target === this.optionsListNode){ return; }
			this.focusOptionNode(evt.target);
			dojo.html.addClass(this._highlighted_option, "dojoComboBoxItemHighlight");
		},

		itemMouseOut: function(/*Event*/ evt){
			if (evt.target === this.optionsListNode){ return; }
			this.blurOptionNode();
		},

		// reset button size; this function is called when the input area has changed size
		onResize: function(){
			var inputSize = dojo.html.getContentBox(this.textInputNode);
			if( inputSize.height == 0 ){
				// need more time to calculate size
				dojo.lang.setTimeout(this, "onResize", 50);
				return;
			}
			var buttonSize = { width: inputSize.height, height: inputSize.height};
			dojo.html.setContentBox(this.downArrowNode, buttonSize);
		},

		fillInTemplate: function(/*Object*/ args, /*Object*/ frag){
			// For inlining a table we need browser specific CSS
			dojo.html.applyBrowserClass(this.domNode);

			var source = this.getFragNodeRef(frag);
			if (! this.name && source.name){ this.name = source.name; }
			this.comboBoxValue.name = this.name;
			this.comboBoxSelectionValue.name = this.name+"_selected";

			/* different nodes get different parts of the style */
			dojo.html.copyStyle(this.domNode, source);
			dojo.html.copyStyle(this.textInputNode, source);
			dojo.html.copyStyle(this.downArrowNode, source);
			with (this.downArrowNode.style){ // calculate these later
				width = "0px";
				height = "0px";
			}

			var dpClass;
			if(this.mode == "remote"){
				dpClass = dojo.widget.incrementalComboBoxDataProvider;
			}else if(typeof this.dataProviderClass == "string"){
				dpClass = dojo.evalObjPath(this.dataProviderClass)
			}else{
				dpClass = this.dataProviderClass;
			}
			this.dataProvider = new dpClass();
			this.dataProvider.init(this, this.getFragNodeRef(frag));

			this.popupWidget = new dojo.widget.createWidget("PopupContainer",
				{toggle: this.dropdownToggle, toggleDuration: this.toggleDuration});
			dojo.event.connect(this, 'destroy', this.popupWidget, 'destroy');
			this.optionsListNode = this.popupWidget.domNode;
			this.domNode.appendChild(this.optionsListNode);
			dojo.html.addClass(this.optionsListNode, 'dojoComboBoxOptions');
			dojo.event.connect(this.optionsListNode, 'onclick', this, 'selectOption');
			dojo.event.connect(this.optionsListNode, 'onmouseover', this, '_onMouseOver');
			dojo.event.connect(this.optionsListNode, 'onmouseout', this, '_onMouseOut');

			dojo.event.connect(this.optionsListNode, "onmouseover", this, "itemMouseOver");
			dojo.event.connect(this.optionsListNode, "onmouseout", this, "itemMouseOut");
		},

		focus: function(){
			// summary
			//	set focus to input node from code
			this.tryFocus();
		},

		openResultList: function(/*Array*/ results){
			if (!this.isEnabled){
				return;
			}
			this.clearResultList();
			if(!results.length){
				this.hideResultList();
			}

			if(	(this.autoComplete)&&
				(results.length)&&
				(!this._prev_key_backspace)&&
				(this.textInputNode.value.length > 0)){
				var cpos = this.getCaretPos(this.textInputNode);
				// only try to extend if we added the last character at the end of the input
				if((cpos+1) > this.textInputNode.value.length){
					// only add to input node as we would overwrite Capitalisation of chars
					this.textInputNode.value += results[0][0].substr(cpos);
					// build a new range that has the distance from the earlier
					// caret position to the end of the first string selected
					this.setSelectedRange(this.textInputNode, cpos, this.textInputNode.value.length);
				}
			}

			var even = true;
			while(results.length){
				var tr = results.shift();
				if(tr){
					var td = document.createElement("div");
					td.appendChild(document.createTextNode(tr[0]));
					td.setAttribute("resultName", tr[0]);
					td.setAttribute("resultValue", tr[1]);
					td.className = "dojoComboBoxItem "+((even) ? "dojoComboBoxItemEven" : "dojoComboBoxItemOdd");
					even = (!even);
					this.optionsListNode.appendChild(td);
				}
			}

			// show our list (only if we have content, else nothing)
			this.showResultList();
		},

		onFocusInput: function(){
			this._hasFocus = true;
		},

		onBlurInput: function(){
			this._hasFocus = false;
			this._handleBlurTimer(true, 500);
		},

		// collect all blur timers issues here
		_handleBlurTimer: function(/*Boolean*/clear, /*Number*/ millisec){
			if(this.blurTimer && (clear || millisec)){
				clearTimeout(this.blurTimer);
			}
			if(millisec){ // we ignore that zero is false and never sets as that never happens in this widget
				this.blurTimer = dojo.lang.setTimeout(this, "checkBlurred", millisec);
			}
		},

		// these 2 are needed in IE and Safari as inputTextNode loses focus when scrolling optionslist
		_onMouseOver: function(/*Event*/ evt){
			if(!this._mouseover_list){
				this._handleBlurTimer(true, 0);
				this._mouseover_list = true;
			}
		},

		_onMouseOut:function(/*Event*/ evt){
			var relTarget = evt.relatedTarget;
			if(!relTarget || relTarget.parentNode!=this.optionsListNode){
				this._mouseover_list = false;
				this._handleBlurTimer(true, 100);
				this.tryFocus();
			}
		},

		_isInputEqualToResult: function(/*String*/ result){
			var input = this.textInputNode.value;
			if(!this.dataProvider.caseSensitive){
				input = input.toLowerCase();
				result = result.toLowerCase();
			}
			return (input == result);
		},

		_isValidOption: function(){
			var tgt = dojo.html.firstElement(this.optionsListNode);
			var isValidOption = false;
			while(!isValidOption && tgt){
				if(this._isInputEqualToResult(tgt.getAttribute("resultName"))){
					isValidOption = true;
				}else{
					tgt = dojo.html.nextElement(tgt);
				}
			}
			return isValidOption;
		},

		checkBlurred: function(){
			if(!this._hasFocus && !this._mouseover_list){
				this.hideResultList();
				// clear the list if the user empties field and moves away.
				if(!this.textInputNode.value.length){
					this.setAllValues("", "");
					return;
				}

				var isValidOption = this._isValidOption();
				// enforce selection from option list
				if(this.forceValidOption && !isValidOption){
					this.setAllValues("", "");
					return;
				}
				if(!isValidOption){// clear
					this.setSelectedValue("");
				}
			}
		},

		sizeBackgroundIframe: function(){
			var mb = dojo.html.getMarginBox(this.optionsListNode);
			if( mb.width==0 || mb.height==0 ){
				// need more time to calculate size
				dojo.lang.setTimeout(this, "sizeBackgroundIframe", 100);
				return;
			}
		},

		selectOption: function(/*Event*/ evt){
			var tgt = null;
			if(!evt){
				evt = { target: this._highlighted_option };
			}

			if(!dojo.html.isDescendantOf(evt.target, this.optionsListNode)){
				// handle autocompletion where the the user has hit ENTER or TAB

				// if the input is empty do nothing
				if(!this.textInputNode.value.length){
					return;
				}
				tgt = dojo.html.firstElement(this.optionsListNode);

				// user has input value not in option list
				if(!tgt || !this._isInputEqualToResult(tgt.getAttribute("resultName"))){
					return;
				}
				// otherwise the user has accepted the autocompleted value
			}else{
				tgt = evt.target;
			}

			while((tgt.nodeType!=1)||(!tgt.getAttribute("resultName"))){
				tgt = tgt.parentNode;
				if(tgt === dojo.body()){
					return false;
				}
			}

			this.selectedResult = [tgt.getAttribute("resultName"), tgt.getAttribute("resultValue")];
			this.setAllValues(tgt.getAttribute("resultName"), tgt.getAttribute("resultValue"));
			if(!evt.noHide){
				this.hideResultList();
				this.setSelectedRange(this.textInputNode, 0, null);
			}
			this.tryFocus();
		},

		clearResultList: function(){
			if(this.optionsListNode.innerHTML){
				this.optionsListNode.innerHTML = "";  // browser natively knows how to collect this memory
			}
		},

		hideResultList: function(){
			this.popupWidget.close();
		},

		showResultList: function(){
			// Our dear friend IE doesnt take max-height so we need to calculate that on our own every time
			var childs = this.optionsListNode.childNodes;
			if(childs.length){
				var visibleCount = this.maxListLength;
				if(childs.length < visibleCount){
					visibleCount = childs.length;
				}

				with(this.optionsListNode.style)
				{
					display = "";
					if(visibleCount == childs.length){
						//no scrollbar is required, so unset height to let browser calcuate it,
						//as in css, overflow is already set to auto
						height = "";
					}else{
						//show it first to get the correct dojo.style.getOuterHeight(childs[0])
						//FIXME: shall we cache the height of the item?
						height = visibleCount * dojo.html.getMarginBox(childs[0]).height +"px";
					}
					width = (dojo.html.getMarginBox(this.domNode).width-2)+"px";

				}
				this.popupWidget.open(this.domNode, this, this.downArrowNode);
			}else{
				this.hideResultList();
			}
		},

		handleArrowClick: function(){
			this._handleBlurTimer(true, 0);
			this.tryFocus();
			if(this.popupWidget.isShowingNow){
				this.hideResultList();
			}else{
				// forces full population of results, if they click
				// on the arrow it means they want to see more options
				this.startSearch("");
			}
		},

		tryFocus: function(){
			try {
				this.textInputNode.focus();
			} catch (e){
				// element isn't focusable if disabled, or not visible etc - not easy to test for.
	 		};
		},

		startSearchFromInput: function(){
			this.startSearch(this.textInputNode.value);
		},

		postCreate: function(){
			this.onResize();
			dojo.event.connect(this, "startSearch", this.dataProvider, "startSearch");
			dojo.event.connect(this.dataProvider, "provideSearchResults", this, "openResultList");
			dojo.event.connect(this.textInputNode, "onblur", this, "onBlurInput");
			dojo.event.connect(this.textInputNode, "onfocus", this, "onFocusInput");
			if (this.disabled){
				this.disable();
			}
			var s = dojo.widget.html.stabile.getState(this.widgetId);
			if (s){
				this.setState(s);
			}
		}
	}
);
