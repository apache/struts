/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

 /* -*- tab-width: 4 -*- */
dojo.provide("dojo.widget.RichText");

dojo.require("dojo.widget.*");
dojo.require("dojo.html.*");
dojo.require("dojo.html.layout");
dojo.require("dojo.event.*");
dojo.require("dojo.string.extras");
dojo.require("dojo.uri.Uri");

// used to save content
if(dojo.hostenv.post_load_){
	(function(){
		var savetextarea = dojo.doc().createElement('textarea');
		savetextarea.id = "dojo.widget.RichText.savedContent";
		savetextarea.style = "display:none;position:absolute;top:-100px;left:-100px;height:3px;width:3px;overflow:hidden;";
		dojo.body().appendChild(savetextarea);
	})();
}else{
	//dojo.body() is not available before onLoad is fired
	try {
		dojo.doc().write('<textarea id="dojo.widget.RichText.savedContent" ' +
			'style="display:none;position:absolute;top:-100px;left:-100px;height:3px;width:3px;overflow:hidden;"></textarea>');
	}catch(e){ }
}

// summary:
//		dojo.widget.RichText is the core of the WYSIWYG editor in dojo, which
//		provides the basic editing features. It also encapsulates the differences
//		of different js engines for various browsers
dojo.widget.defineWidget(
	"dojo.widget.RichText",
	dojo.widget.HtmlWidget,
	{
		// Boolean:
		//		whether to inherit the parent's width or simply use 100%
		inheritWidth: false,

		// Boolean:
		//		whether focusing into this instance of richtext when page onload
		focusOnLoad: true,

		// String:
		//		If a save name is specified the content is saved and restored if the
		//		editor is not properly closed after editing has started.
		saveName: "",

		// String:
		//		temporary content storage
		_content: "",

		// String:
		//		set height to fix the editor at a specific height, with scrolling
		height: "",

		// String:
		//		The minimum height that the editor should have
		minHeight: "1em",

		// Boolean:
		isClosed: true,
		// Boolean:
		isLoaded: false,

		// Boolean:
		//		whether to use the active-x object in IE
		useActiveX: false,

		// Boolean:
		//		whether to use relative URLs for images - if this is enabled
		//		images will be given absolute URLs when inside the editor but
		//		will be changed to use relative URLs (to the current page) on save
		relativeImageUrls: false,

		// String:
		//		used to concat contents from multiple textareas into a single string
		_SEPARATOR: "@@**%%__RICHTEXTBOUNDRY__%%**@@",

	/* Init
	 *******/

		fillInTemplate: function(){
			// summary: see dojo.widget.DomWidget
			dojo.event.topic.publish("dojo.widget.RichText::init", this);
			this.open();

			// backwards compatibility, needs to be removed
			dojo.event.connect(this, "onKeyPressed", this, "afterKeyPress");
			dojo.event.connect(this, "onKeyPress", this, "keyPress");
			dojo.event.connect(this, "onKeyDown", this, "keyDown");
			dojo.event.connect(this, "onKeyUp", this, "keyUp");

			// add default some key handlers
			var ctrl = this.KEY_CTRL;
			var exec = function (cmd, arg) {
				return arguments.length == 1 ? function () { this.execCommand(cmd); } :
					function () { this.execCommand(cmd, arg); }
			}

			this.addKeyHandler("b", ctrl, exec("bold"));
			this.addKeyHandler("i", ctrl, exec("italic"));
			this.addKeyHandler("u", ctrl, exec("underline"));
			this.addKeyHandler("a", ctrl, exec("selectall"));
			//this.addKeyHandler("k", ctrl, exec("createlink", ""));
			//this.addKeyHandler("K", ctrl, exec("unlink"));
			this.addKeyHandler("s", ctrl, function () { this.save(true); });

			this.addKeyHandler("1", ctrl, exec("formatblock", "h1"));
			this.addKeyHandler("2", ctrl, exec("formatblock", "h2"));
			this.addKeyHandler("3", ctrl, exec("formatblock", "h3"));
			this.addKeyHandler("4", ctrl, exec("formatblock", "h4"));

			this.addKeyHandler("\\", ctrl, exec("insertunorderedlist"));
			if(!dojo.render.html.ie){
				this.addKeyHandler("Z", ctrl, exec("redo"));
			}
		},


		// Array: events which should be connected to the underlying editing area
		events: ["onBlur", "onFocus", "onKeyPress", "onKeyDown", "onKeyUp", "onClick"],

		/**
		 * Transforms the node referenced in this.domNode into a rich text editing
		 * node. This can result in the creation and replacement with an <iframe> if
		 * designMode is used, an <object> and active-x component if inside of IE or
		 * a reguler element if contentEditable is available.
		 */
		open: function (/*DomNode, optional*/element) {
			// summary:
			//		Transforms the node referenced in this.domNode into a rich text editing
			//		node. This can result in the creation and replacement with an <iframe> if
			//		designMode is used, an <object> and active-x component if inside of IE or
			//		a reguler element if contentEditable is available.
			var h = dojo.render.html;
			dojo.event.topic.publish("dojo.widget.RichText::open", this);

			if (!this.isClosed) { this.close(); }
			this._content = "";
			if((arguments.length == 1)&&(element["nodeName"])){ this.domNode = element; } // else unchanged

			if(	(this.domNode["nodeName"])&&
				(this.domNode.nodeName.toLowerCase() == "textarea")){
				this.textarea = this.domNode;
				var html = dojo.string.trim(this.textarea.value);
				if(html == ""){ html = "&nbsp;"; }
				this.domNode = dojo.doc().createElement("div");
				dojo.html.copyStyle(this.domNode, this.textarea);
				var tmpFunc = dojo.lang.hitch(this, function(){
					//some browsers refuse to submit display=none textarea, so
					//move the textarea out of screen instead
					with(this.textarea.style){
						display = "block";
						position = "absolute";
						left = top = "-1000px";

						if(h.ie){ //nasty IE bug: abnormal formatting if overflow is not hidden
							this.__overflow = overflow;
							overflow = "hidden";
						}
					}
				});
				if(h.ie){
					setTimeout(tmpFunc, 10);
				}else{
					tmpFunc();
				}
				if(!h.safari){
					// FIXME: VERY STRANGE safari 2.0.4 behavior here caused by
					// moving the textarea. Often crashed the browser!!! Seems
					// fixed on webkit nightlies.
					dojo.html.insertBefore(this.domNode, this.textarea);
				}
				// this.domNode.innerHTML = html;

				if(this.textarea.form){
					dojo.event.connect(this.textarea.form, "onsubmit",
						// FIXME: should we be calling close() here instead?
						dojo.lang.hitch(this, function(){
							this.textarea.value = this.getEditorContent();
						})
					);
				}

				// dojo plucks our original domNode from the document so we need
				// to go back and put ourselves back in
				var editor = this;
				dojo.event.connect(this, "postCreate", function (){
					dojo.html.insertAfter(editor.textarea, editor.domNode);
				});
			}else{
				var html = this._preFilterContent(dojo.string.trim(this.domNode.innerHTML));
				if(html == ""){ html = "&nbsp;"; }
			}

			var content = dojo.html.getContentBox(this.domNode);
			this._oldHeight = content.height;
			this._oldWidth = content.width;

			this._firstChildContributingMargin = this._getContributingMargin(this.domNode, "top");
			this._lastChildContributingMargin = this._getContributingMargin(this.domNode, "bottom");

			this.savedContent = dojo.doc().createElement("div");
			while (this.domNode.hasChildNodes()) {
				this.savedContent.appendChild(this.domNode.firstChild);
			}

			this.editingArea = dojo.doc().createElement("div");
			this.domNode.appendChild(this.editingArea);

			// If we're a list item we have to put in a blank line to force the
			// bullet to nicely align at the top of text
			if(	(this.domNode["nodeName"])&&
				(this.domNode.nodeName == "LI")){
				this.domNode.innerHTML = " <br>";
			}

			if(this.saveName != ""){
				var saveTextarea = dojo.doc().getElementById("dojo.widget.RichText.savedContent");
				if (saveTextarea.value != "") {
					var datas = saveTextarea.value.split(this._SEPARATOR);
					for (var i = 0; i < datas.length; i++) {
						var data = datas[i].split(":");
						if (data[0] == this.saveName) {
							html = data[1];
							datas.splice(i, 1);
							break;
						}
					}
				}
				dojo.event.connect("before", window, "onunload", this, "_saveContent");
				// dojo.event.connect(window, "onunload", this, "_saveContent");
			}

			if(h.ie70 && this.useActiveX){
				dojo.debug("activeX in ie70 is not currently supported, useActiveX is ignored for now.");
				this.useActiveX = false;
			}
			// Safari's selections go all out of whack if we do it inline,
			// so for now IE is our only hero
			//if (typeof document.body.contentEditable != "undefined") {
			if(this.useActiveX && h.ie){ // active-x
				var self = this;
				//if call _drawObject directly here, textarea replacement
				//won't work: no content is shown. However, add a delay
				//can workaround this. No clue why.
				setTimeout(function(){self._drawObject(html);}, 0);
			}else if(h.ie){ // contentEditable, easy
				this.iframe = dojo.doc().createElement( 'iframe' ) ;
				this.iframe.src = 'javascript:void(0)';
				this.editorObject = this.iframe;
				with(this.iframe.style){
					border = '0';
					width = "100%";
				}
				this.iframe.frameBorder = 0;
				this.editingArea.appendChild(this.iframe)
				this.window = this.iframe.contentWindow;
				this.document = this.window.document;
				this.document.open();
				this.document.write("<html><head></head><body style='margin: 0; padding: 0;border: 0; overflow: hidden;'><div></div></body></html>");
				this.document.close();
				this.editNode = this.document.body.firstChild;//document.createElement("div");
				this.editNode.contentEditable = true;
				with (this.iframe.style) {
					if(h.ie70){
						if(this.height){
							height = this.height;
						}
						if(this.minHeight){
							minHeight = this.minHeight;
						}
					}else{
						height = this.height ? this.height : this.minHeight;
					}
				}

				// FIXME: setting contentEditable on switches this element to
				// IE's hasLayout mode, triggering weird margin collapsing
				// behavior. It's particularly bad if the element you're editing
				// contains childnodes that don't have margin: defined in local
				// css rules. It would be nice if it was possible to hack around
				// this. Sadly _firstChildContributingMargin and
				// _lastChildContributingMargin don't work on IE unless all
				// elements have margins set in CSS :-(

				//if the normal way fails, we try the hard way to get the list
				if(!this._cacheLocalBlockFormatNames()){
					//in the array below, ul can not come directly after ol, otherwise the queryCommandValue returns Normal for it
					var formats = ['p', 'pre', 'address', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'ol', 'div', 'ul'];
					var localhtml = "";
					for(var i in formats){
						if(formats[i].charAt(1) != 'l'){
							localhtml += "<"+formats[i]+"><span>content</span></"+formats[i]+">";
						}else{
							localhtml += "<"+formats[i]+"><li>content</li></"+formats[i]+">";
						}
					}
					//queryCommandValue returns empty if we hide editNode, so move it out of screen temporary
					with(this.editNode.style){
						position = "absolute";
						left = "-2000px";
						top = "-2000px";
					}
					this.editNode.innerHTML = localhtml;
					var node = this.editNode.firstChild;
					while(node){
						dojo.withGlobal(this.window, "selectElement", dojo.html.selection, [node.firstChild]);
						var nativename = node.tagName.toLowerCase();
						this._local2NativeFormatNames[nativename] = this.queryCommandValue("formatblock");
//						dojo.debug([nativename,this._local2NativeFormatNames[nativename]]);
						this._native2LocalFormatNames[this._local2NativeFormatNames[nativename]] = nativename;
						node = node.nextSibling;
					}
					with(this.editNode.style){
						position = "";
						left = "";
						top = "";
					}
				}

				this.editNode.innerHTML = html;
				if(this.height){ this.document.body.style.overflowY="scroll"; }

				dojo.lang.forEach(this.events, function(e){
					dojo.event.connect(this.editNode, e.toLowerCase(), this, e);
				}, this);

				this.onLoad();
			} else { // designMode in iframe
				this._drawIframe(html);
				this.editorObject = this.iframe;
			}

			// TODO: this is a guess at the default line-height, kinda works
			if (this.domNode.nodeName == "LI") { this.domNode.lastChild.style.marginTop = "-1.2em"; }
			dojo.html.addClass(this.domNode, "RichTextEditable");

			this.isClosed = false;
		},

		_hasCollapseableMargin: function(/*DomNode*/element, /*String*/side) {
			// summary:
			//		check if an element has padding or borders on the given side
			//		which would prevent it from collapsing margins
			if (dojo.html.getPixelValue(element,
										 'border-'+side+'-width',
										 false)) {
				return false;
			} else if (dojo.html.getPixelValue(element,
												'padding-'+side,
												false)) {
				return false;
			} else {
				return true;
			}
		},

		_getContributingMargin:	function(/*DomNode*/element, /*String*/topOrBottom) {
			// summary:
			//		calculate how much margin this element and its first or last
			//		child are contributing to the total margin between this element
			//		and the adjacent node. CSS border collapsing makes this
			//		necessary.

			if (topOrBottom == "top") {
				var siblingAttr = "previousSibling";
				var childSiblingAttr = "nextSibling";
				var childAttr = "firstChild";
				var marginProp = "margin-top";
				var siblingMarginProp = "margin-bottom";
			} else {
				var siblingAttr = "nextSibling";
				var childSiblingAttr = "previousSibling";
				var childAttr = "lastChild";
				var marginProp = "margin-bottom";
				var siblingMarginProp = "margin-top";
			}

			var elementMargin = dojo.html.getPixelValue(element, marginProp, false);

			function isSignificantNode(element) {
				// see if an node is significant in the current context
				// for calulating margins
				return !(element.nodeType==3 && dojo.string.isBlank(element.data))
					&& dojo.html.getStyle(element, "display") != "none"
					&& !dojo.html.isPositionAbsolute(element);
			}

			// walk throuh first/last children to find total collapsed margin size
			var childMargin = 0;
			var child = element[childAttr];
			while (child) {
				// skip over insignificant elements (whitespace, etc)
				while ((!isSignificantNode(child)) && child[childSiblingAttr]) {
					child = child[childSiblingAttr];
				}

				childMargin = Math.max(childMargin, dojo.html.getPixelValue(child, marginProp, false));
				// stop if we hit a bordered/padded element
				if (!this._hasCollapseableMargin(child, topOrBottom)) break;
				child = child[childAttr];
			}

			// if this element has a border, return full child margin immediately
			// as there won't be any margin collapsing
			if (!this._hasCollapseableMargin(element, topOrBottom)){ return parseInt(childMargin); }

			// find margin supplied by nearest sibling
			var contextMargin = 0;
			var sibling = element[siblingAttr];
			while (sibling) {
				if (isSignificantNode(sibling)) {
					contextMargin = dojo.html.getPixelValue(sibling,
															 siblingMarginProp,
															 false);
					break;
				}
				sibling = sibling[siblingAttr];
			}
			if (!sibling) { // no sibling, look at parent's margin instead
				contextMargin = dojo.html.getPixelValue(element.parentNode,
												marginProp, false);
			}

			if (childMargin > elementMargin) {
				return parseInt(Math.max((childMargin-elementMargin)-contextMargin, 0));
			} else {
				return 0;
			}

		},

		_drawIframe: function (/*String*/html){
			// summary:
			//		Draws an iFrame using the existing one if one exists.
			//		Used by Mozilla, Safari, and Opera

			// detect firefox < 1.5, which has some iframe loading issues
			var oldMoz = Boolean(dojo.render.html.moz && (
									typeof window.XML == 'undefined'))

			if(!this.iframe){
				var currentDomain = (new dojo.uri.Uri(dojo.doc().location)).host;
				this.iframe = dojo.doc().createElement("iframe");
				// dojo.body().appendChild(this.iframe);
				with(this.iframe){
					style.border = "none";
					style.lineHeight = "0"; // squash line height
					style.verticalAlign = "bottom";
					scrolling = this.height ? "auto" : "no";
				}
			}
			// opera likes this to be outside the with block
			this.iframe.src = dojo.uri.dojoUri("src/widget/templates/richtextframe.html") + ((dojo.doc().domain != currentDomain) ? ("#"+dojo.doc().domain) : "");
			this.iframe.width = this.inheritWidth ? this._oldWidth : "100%";
			if(this.height){
				this.iframe.style.height = this.height;
			}else{
				var height = this._oldHeight;
				if(this._hasCollapseableMargin(this.domNode, 'top')){
					height += this._firstChildContributingMargin;
				}
				if(this._hasCollapseableMargin(this.domNode, 'bottom')){
					height += this._lastChildContributingMargin;
				}
				this.iframe.height = height;
			}

			var tmpContent = dojo.doc().createElement('div');
			tmpContent.innerHTML = html;

			// make relative image urls absolute
			if(this.relativeImageUrls){
				var imgs = tmpContent.getElementsByTagName('img');
				for(var i=0; i<imgs.length; i++){
					imgs[i].src = (new dojo.uri.Uri(dojo.global().location, imgs[i].src)).toString();
				}
				html = tmpContent.innerHTML;
			}

			// fix margins on tmpContent
			var firstChild = dojo.html.firstElement(tmpContent);
			var lastChild = dojo.html.lastElement(tmpContent);
			if(firstChild){
				firstChild.style.marginTop = this._firstChildContributingMargin+"px";
			}
			if(lastChild){
				lastChild.style.marginBottom = this._lastChildContributingMargin+"px";
			}

			// show existing content behind iframe for now
			tmpContent.style.position = "absolute";
			this.editingArea.appendChild(tmpContent);
			this.editingArea.appendChild(this.iframe);
			if(dojo.render.html.safari){
				this.iframe.src = this.iframe.src;
			}

			var _iframeInitialized = false;

			// now we wait for onload. Janky hack!
			var ifrFunc = dojo.lang.hitch(this, function(){
				if(!_iframeInitialized){
					_iframeInitialized = true;
				}else{ return; }
				if(!this.editNode){
					if(this.iframe.contentWindow){
						this.window = this.iframe.contentWindow;
						this.document = this.iframe.contentWindow.document
					}else if(this.iframe.contentDocument){
						// for opera
						this.window = this.iframe.contentDocument.window;
						this.document = this.iframe.contentDocument;
					}

					// curry the getStyle function
					var getStyle = (function (domNode) { return function (style) {
						return dojo.html.getStyle(domNode, style);
					}; })(this.domNode);

					var font =
						getStyle('font-weight') + " " +
						getStyle('font-size') + " " +
						getStyle('font-family');

					// line height is tricky - applying a units value will mess things up.
					// if we can't get a non-units value, bail out.
					var lineHeight = "1.0";
					var lineHeightStyle = dojo.html.getUnitValue(this.domNode, 'line-height');
					if (lineHeightStyle.value && lineHeightStyle.units=="") {
						lineHeight = lineHeightStyle.value;
					}

					dojo.html.insertCssText(
						'    body,html { background: transparent; padding: 0; margin: 0; }\n' +
						// TODO: left positioning will case contents to disappear out of view
						//       if it gets too wide for the visible area
						'    body { top: 0; left: 0; right: 0;' +
						(((this.height)||(dojo.render.html.opera)) ? '' : ' position: fixed; ') +
						'        font: ' + font + ';\n' +
						'        min-height: ' + this.minHeight + '; \n' +
						'        line-height: ' + lineHeight + '} \n' +
						'    p { margin: 1em 0 !important; }\n' +
						'    body > *:first-child { padding-top: 0 !important; margin-top: ' + this._firstChildContributingMargin + 'px !important; }\n' + // FIXME: test firstChild nodeType
						'    body > *:last-child { padding-bottom: 0 !important; margin-bottom: ' + this._lastChildContributingMargin + 'px !important; }\n' +
						'    li > ul:-moz-first-node, li > ol:-moz-first-node { padding-top: 1.2em; }\n' +
						'    li { min-height: 1.2em; }\n' +
						//'    p,ul,li { padding-top: 0; padding-bottom: 0; margin-top:0; margin-bottom: 0; }\n' +
						'', this.document);

					tmpContent.parentNode.removeChild(tmpContent);
					this.document.body.innerHTML = html;
					if(oldMoz||dojo.render.html.safari){
						this.document.designMode = "on";
					}
					this.onLoad();
				}else{
					tmpContent.parentNode.removeChild(tmpContent);
					this.editNode.innerHTML = html;
					this.onDisplayChanged();
				}
			});

			if(this.editNode){
				ifrFunc(); // iframe already exists, just set content
			}else if(dojo.render.html.moz){
				// FIXME: if we put this on a delay, we get a height of 20px.
				// Otherwise we get the correctly specified minHeight value.
				this.iframe.onload = function(){
					setTimeout(ifrFunc, 250);
				}
			}else{ // new mozillas, opera, safari
				this.iframe.onload = ifrFunc;
			}
		},

		_applyEditingAreaStyleSheets: function(){
			// summary:
			//		apply the specified css files in styleSheets
			var files = [];
			if(this.styleSheets){
				files = this.styleSheets.split(';');
			}

			//empty this.editingAreaStyleSheets here, as it will be filled in addStyleSheet
			files = files.concat(this.editingAreaStyleSheets);
			this.editingAreaStyleSheets = [];

			if(files.length>0){
				for(var i=0;i<files.length;i++){
					var url = files[i];
					if(url){
						this.addStyleSheet(new dojo.uri.Uri(url));
	 				}
	 			}
			}
		},

		addStyleSheet: function(/*dojo.uri.Uri*/uri) {
			// summary:
			//		add an external stylesheet for the editing area
			// uri:	a dojo.uri.Uri pointing to the url of the external css file
			var url=uri.toString();
			if(dojo.lang.find(this.editingAreaStyleSheets, url) > -1){
				dojo.debug("dojo.widget.RichText.addStyleSheet: Style sheet "+url+" is already applied to the editing area!");
				return;
			}

			//if uri is relative, then convert it to absolute so that it can be resolved correctly in iframe
			if(url.charAt(0) == '.' || (url.charAt(0) != '/' && !uri.host)){
				url = (new dojo.uri.Uri(dojo.global().location, url)).toString();
			}

			this.editingAreaStyleSheets.push(url);
			if(this.document.createStyleSheet){ //IE
				this.document.createStyleSheet(url);
			}else{ //other browser
				var head = this.document.getElementsByTagName("head")[0];
				var stylesheet = this.document.createElement("link");
				with(stylesheet){
					rel="stylesheet";
					type="text/css";
					href=url;
				}
				head.appendChild(stylesheet);
			}
		},

		removeStyleSheet: function (/*dojo.uri.Uri*/uri) {
			// summary:
			//		remove an external stylesheet for the editing area
			var url=uri.toString();
			var index = dojo.lang.find(this.editingAreaStyleSheets, url);
			if(index == -1){
				dojo.debug("dojo.widget.RichText.removeStyleSheet: Style sheet "+url+" is not applied to the editing area so it can not be removed!");
				return;
			}
			delete this.editingAreaStyleSheets[index];

			var links = this.document.getElementsByTagName("link");
			for(var i=0;i<links.length;i++){
				if(links[i].href == url){
					if(dojo.render.html.ie){//we need to empty the href first, to get IE to remove the rendered styles
						links[i].href="";
					}
					dojo.html.removeNode(links[i]);
					break;
				}
			}
		},

		_drawObject: function (/*String*/html) {
			// summary:
			//		Draws an active x object, used by IE
			this.object = dojo.html.createExternalElement(dojo.doc(), "object");

			with (this.object) {
				classid = "clsid:2D360201-FFF5-11D1-8D03-00A0C959BC0A";
				width = this.inheritWidth ? this._oldWidth : "100%";
				style.height = this.height ? this.height : (this._oldHeight+"px");
				Scrollbars = this.height ? true : false;
				Appearance = this._activeX.appearance.flat;
			}
			this.editorObject = this.object;
			this.editingArea.appendChild(this.object);

			this.object.attachEvent("DocumentComplete", dojo.lang.hitch(this, "onLoad"));
			//DisplayChanged is fired too often even no change is made, so we ignore it
			//and call onDisplayChanged manually in execCommand instead
//			this.object.attachEvent("DisplayChanged", dojo.lang.hitch(this, "onDisplayChanged"));

			dojo.lang.forEach(this.events, function(e){
				this.object.attachEvent(e.toLowerCase(), dojo.lang.hitch(this, e));
			}, this);

			this.object.DocumentHTML = '<!doctype HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">' +
				'<html><title></title>' +
				'<style type="text/css">' +
				'    body,html { padding: 0; margin: 0; }' + //font: ' + font + '; }' +
				(this.height ? '' : '    body,  { overflow: hidden; }') +
				'</style>' +
				//'<base href="' + dojo.global().location + '">' +
				'<body><div>' + html + '<div></body></html>';

			this._cacheLocalBlockFormatNames();
		},

		//static cache variables shared among all instance of this class
		_local2NativeFormatNames: {},
		_native2LocalFormatNames: {},
		//in IE, names for blockformat is locale dependent, so we cache the values here
		//we use activeX to obtain the list, if success or the names are already cached,
		//return true
		_cacheLocalBlockFormatNames: function(){
			// summary:
			//		in IE, names for blockformat is locale dependent, so we cache the values here
			//		we use activeX to obtain the list, if success or the names are already cached,
			//		return true
			if(!this._native2LocalFormatNames['p']){
				var obj = this.object;
				if(!obj){
					//create obj temporarily
					try{
						obj = dojo.html.createExternalElement(dojo.doc(), "object");
						obj.classid = "clsid:2D360201-FFF5-11D1-8D03-00A0C959BC0A";
						dojo.body().appendChild(obj);
						obj.DocumentHTML = "<html><head></head><body></body></html>";
					}catch(e){
						return false;
					}
				}
				var oNamesParm = new ActiveXObject("DEGetBlockFmtNamesParam.DEGetBlockFmtNamesParam");
				obj.ExecCommand(this._activeX.command['getblockformatnames'], 0, oNamesParm);
				var vbNamesArray = new VBArray(oNamesParm.Names);
				var localFormats = vbNamesArray.toArray();
				var nativeFormats = ['p', 'pre', 'address', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'ol', 'ul', '', '', '','','div'];
				for(var i=0;i<nativeFormats.length;++i){
					if(nativeFormats[i].length>0){
						this._local2NativeFormatNames[localFormats[i]] = nativeFormats[i];
						this._native2LocalFormatNames[nativeFormats[i]] = localFormats[i];
					}
				}
				if(!this.object){
					//delete the temporary obj
					dojo.body().removeChild(obj);
				}
			}
			return true;
		},
	/* Event handlers
	 *****************/

		_isResized: function(){ return false; },

		onLoad: function(e){
			// summary: handler after the content of the document finishes loading
			this.isLoaded = true;
			if (this.object){
				this.document = this.object.DOM;
				this.window = this.document.parentWindow;
				this.editNode = this.document.body.firstChild;
				this.editingArea.style.height = this.height ? this.height : this.minHeight;
				this.connect(this, "onDisplayChanged", "_updateHeight");
				//pretend the object as an iframe, so that the context menu for the
				//editor can be placed correctly when shown
				this.window._frameElement = this.object;
			}else if (this.iframe && !dojo.render.html.ie){
				this.editNode = this.document.body;
				this.connect(this, "onDisplayChanged", "_updateHeight");

				try { // sanity check for Mozilla
					this.document.execCommand("useCSS", false, true); // old moz call
					this.document.execCommand("styleWithCSS", false, false); // new moz call
					//this.document.execCommand("insertBrOnReturn", false, false); // new moz call
				}catch(e2){ }

				if (dojo.render.html.safari) {
					/*
					this.iframe.style.visiblity = "visible";
					this.iframe.style.border = "1px solid black";
					this.editNode.style.visiblity = "visible";
					this.editNode.style.border = "1px solid black";
					*/
					// this.onDisplayChanged();
					this.connect(this.editNode, "onblur", "onBlur");
					this.connect(this.editNode, "onfocus", "onFocus");
					this.connect(this.editNode, "onclick", "onFocus");

					this.interval = setInterval(dojo.lang.hitch(this, "onDisplayChanged"), 750);
					// dojo.raise("onload");
					// dojo.debug(this.editNode.parentNode.parentNode.parentNode.nodeName);
				} else if (dojo.render.html.mozilla || dojo.render.html.opera) {
					var doc = this.document;
					var addListener = dojo.event.browser.addListener;
					var self = this;
					dojo.lang.forEach(this.events, function(e){
						var l = addListener(self.document, e.substr(2).toLowerCase(), dojo.lang.hitch(self, e));
						if(e=="onBlur"){
							// We need to unhook the blur event listener on close as we
							// can encounter a garunteed crash in FF if another event is
							// also fired
							var unBlur = { unBlur: function(e){
									dojo.event.browser.removeListener(doc, "blur", l);
							} };
							dojo.event.connect("before", self, "close", unBlur, "unBlur");
						}
					});
				}
				// FIXME: when scrollbars appear/disappear this needs to be fired
			}else if(dojo.render.html.ie){
				// IE contentEditable
				this.connect(this, "onDisplayChanged", "_updateHeight");
				this.editNode.style.zoom = 1.0;
			}

			this._applyEditingAreaStyleSheets();

			if(this.focusOnLoad){
				this.focus();
			}
			this.onDisplayChanged(e);
		},

		onKeyDown: function(e){
			// summary: Fired on keydown
			if((!e)&&(this.object)){
				e = dojo.event.browser.fixEvent(this.window.event);
			}
			// dojo.debug("onkeydown:", e.keyCode);
			// we need this event at the moment to get the events from control keys
			// such as the backspace. It might be possible to add this to Dojo, so that
			// keyPress events can be emulated by the keyDown and keyUp detection.
			if((dojo.render.html.ie)&&(e.keyCode == e.KEY_TAB)){
				e.preventDefault();
				e.stopPropagation();
				// FIXME: this is a poor-man's indent/outdent. It would be
				// better if it added 4 "&nbsp;" chars in an undoable way.
				// Unfortuantly pasteHTML does not prove to be undoable
				this.execCommand((e.shiftKey ? "outdent" : "indent"));
			}else if(dojo.render.html.ie){
				if((65 <= e.keyCode)&&(e.keyCode <= 90)){
					e.charCode = e.keyCode;
					this.onKeyPress(e);
				}
				// dojo.debug(e.ctrlKey);
				// dojo.debug(e.keyCode);
				// dojo.debug(e.charCode);
				// this.onKeyPress(e);
			}
		},

		onKeyUp: function(e){
			// summary: Fired on keyup
			return;
		},

		KEY_CTRL: 1,

		onKeyPress: function(e){
			// summary: Fired on keypress
			if((!e)&&(this.object)){
				e = dojo.event.browser.fixEvent(this.window.event);
			}
			// handle the various key events

			var modifiers = e.ctrlKey ? this.KEY_CTRL : 0;

			if (this._keyHandlers[e.key]) {
				// dojo.debug("char:", e.key);
				var handlers = this._keyHandlers[e.key], i = 0, handler;
				while (handler = handlers[i++]) {
					if (modifiers == handler.modifiers) {
						handler.handler.call(this);
						e.preventDefault();
						break;
					}
				}
			}

			// function call after the character has been inserted
			dojo.lang.setTimeout(this, this.onKeyPressed, 1, e);
		},

		addKeyHandler: function (/*String*/key, /*Int*/modifiers, /*Function*/handler) {
			// summary: add a handler for a keyboard shortcut
			if (!(this._keyHandlers[key] instanceof Array)) { this._keyHandlers[key] = []; }
			this._keyHandlers[key].push({
				modifiers: modifiers || 0,
				handler: handler
			});
		},

		onKeyPressed: function (e) {
			// summary:
			//		Fired after a keypress event has occured and it's action taken. This
		 	//		is useful if action needs to be taken after text operations have finished

			// Mozilla adds a single <p> with an embedded <br> when you hit enter once:
			//   <p><br>\n</p>
			// when you hit enter again it adds another <br> inside your enter
			//   <p><br>\n<br>\n</p>
			// and if you hit enter again it splits the <br>s over 2 <p>s
			//   <p><br>\n</p>\n<p><br>\n</p>
			// now this assumes that <p>s have double the line-height of <br>s to work
			// and so we need to remove the <p>s to ensure the position of the cursor
			// changes from the users perspective when they hit enter, as the second two
			// html snippets render the same when margins are set to 0.

			// TODO: doesn't really work; is this really needed?
			//if (dojo.render.html.moz) {
			//	for (var i = 0; i < this.document.getElementsByTagName("p").length; i++) {
			//		var p = this.document.getElementsByTagName("p")[i];
			//		if (p.innerHTML.match(/^<br>\s$/m)) {
			//			while (p.hasChildNodes()) { p.parentNode.insertBefore(p.firstChild, p); }
			//			p.parentNode.removeChild(p);
			//		}
			//	}
			//}
			this.onDisplayChanged(/*e*/); // can't pass in e
		},

		onClick: function(e){ this.onDisplayChanged(e); },
		onBlur: function(e){ },
		_initialFocus: true,
		onFocus: function(e){
			// summary: Fired on focus
			if( (dojo.render.html.mozilla)&&(this._initialFocus) ){
				this._initialFocus = false;
				if(dojo.string.trim(this.editNode.innerHTML) == "&nbsp;"){
					this.placeCursorAtStart();
//					this.execCommand("selectall");
//					this.window.getSelection().collapseToStart();
				}
			}
		},

		blur: function () {
			// summary: remove focus from this instance
			if(this.iframe) { this.window.blur(); }
			else if(this.object) { this.document.body.blur(); }
			else if(this.editNode) { this.editNode.blur(); }
		},

		focus: function () {
			// summary: move focus to this instance
			if(this.iframe && !dojo.render.html.ie) { this.window.focus(); }
			else if(this.object) { this.document.focus(); }
			// editNode may be hidden in display:none div, lets just punt in this case
			else if(this.editNode && this.editNode.focus) { this.editNode.focus(); }
			else{
				dojo.debug("Have no idea how to focus into the editor!");
			}
		},

		/** this event will be fired everytime the display context changes and the
		 result needs to be reflected in the UI */
		onDisplayChanged: function (e){ },


	/* Formatting commands
	 **********************/

		// Object: IE's Active X codes: see http://www.computerbytesman.com/js/activex/dhtmledit.htm
		_activeX: {
			command: {
				bold: 5000,
				italic: 5023,
				underline: 5048,

				justifycenter: 5024,
				justifyleft: 5025,
				justifyright: 5026,

				cut: 5003,
				copy: 5002,
				paste: 5032,
				"delete": 5004,

				undo: 5049,
				redo: 5033,

				removeformat: 5034,
				selectall: 5035,
				unlink: 5050,

				indent: 5018,
				outdent: 5031,

				insertorderedlist: 5030,
				insertunorderedlist: 5051,

				// table commands
				inserttable: 5022,
				insertcell: 5019,
				insertcol: 5020,
				insertrow: 5021,
				deletecells: 5005,
				deletecols: 5006,
				deleterows: 5007,
				mergecells: 5029,
				splitcell: 5047,

				// the command need mapping, they don't translate directly
				// to the contentEditable commands
				setblockformat: 5043,
				getblockformat: 5011,
				getblockformatnames: 5012,
				setfontname: 5044,
				getfontname: 5013,
				setfontsize: 5045,
				getfontsize: 5014,
				setbackcolor: 5042,
				getbackcolor: 5010,
				setforecolor: 5046,
				getforecolor: 5015,

				findtext: 5008,
				font: 5009,
				hyperlink: 5016,
				image: 5017,

				lockelement: 5027,
				makeabsolute: 5028,
				sendbackward: 5036,
				bringforward: 5037,
				sendbelowtext: 5038,
				bringabovetext: 5039,
				sendtoback: 5040,
				bringtofront: 5041,

				properties: 5052
			},

			ui: {
				"default": 0,
				prompt: 1,
				noprompt: 2
			},

			status: {
				notsupported: 0,
				disabled: 1,
				enabled: 3,
				latched: 7,
				ninched: 11
			},

			appearance: {
				flat: 0,
				inset: 1
			},

			state: {
				unchecked: 0,
				checked: 1,
				gray: 2
			}
		},

		_normalizeCommand: function (/*String*/cmd){
			// summary:
			//		Used as the advice function by dojo.event.connect to map our
		 	//		normalized set of commands to those supported by the target
		 	//		browser
			var drh = dojo.render.html;

			var command = cmd.toLowerCase();
			if(command == "formatblock"){
				if(drh.safari){ command = "heading"; }
			}else if(this.object){
				switch(command){
					case "createlink":
						command = "hyperlink";
						break;
					case "insertimage":
						command = "image";
						break;
				}
			}else if(command == "hilitecolor" && !drh.mozilla){
				command = "backcolor";
			}

			return command;
		},

		queryCommandAvailable: function (/*String*/command) {
			// summary:
			//		Tests whether a command is supported by the host. Clients SHOULD check
			//		whether a command is supported before attempting to use it, behaviour
			//		for unsupported commands is undefined.
			// command: The command to test for
			var ie = 1;
			var mozilla = 1 << 1;
			var safari = 1 << 2;
			var opera = 1 << 3;
			var safari420 = 1 << 4;

			var gt420 = false;
			if(dojo.render.html.safari){
				var tmp = dojo.render.html.UA.split("AppleWebKit/")[1];
				var ver = parseFloat(tmp.split(" ")[0]);
				if(ver >= 420){ gt420 = true; }
			}

			function isSupportedBy (browsers) {
				return {
					ie: Boolean(browsers & ie),
					mozilla: Boolean(browsers & mozilla),
					safari: Boolean(browsers & safari),
					safari420: Boolean(browsers & safari420),
					opera: Boolean(browsers & opera)
				}
			}

			var supportedBy = null;

			switch (command.toLowerCase()) {
				case "bold": case "italic": case "underline":
				case "subscript": case "superscript":
				case "fontname": case "fontsize":
				case "forecolor": case "hilitecolor":
				case "justifycenter": case "justifyfull": case "justifyleft":
				case "justifyright": case "delete": case "selectall":
					supportedBy = isSupportedBy(mozilla | ie | safari | opera);
					break;

				case "createlink": case "unlink": case "removeformat":
				case "inserthorizontalrule": case "insertimage":
				case "insertorderedlist": case "insertunorderedlist":
				case "indent": case "outdent": case "formatblock":
				case "inserthtml": case "undo": case "redo": case "strikethrough":
					supportedBy = isSupportedBy(mozilla | ie | opera | safari420);
					break;

				case "blockdirltr": case "blockdirrtl":
				case "dirltr": case "dirrtl":
				case "inlinedirltr": case "inlinedirrtl":
					supportedBy = isSupportedBy(ie);
					break;
				case "cut": case "copy": case "paste":
					supportedBy = isSupportedBy( ie | mozilla | safari420);
					break;

				case "inserttable":
					supportedBy = isSupportedBy(mozilla | (this.object ? ie : 0));
					break;

				case "insertcell": case "insertcol": case "insertrow":
				case "deletecells": case "deletecols": case "deleterows":
				case "mergecells": case "splitcell":
					supportedBy = isSupportedBy(this.object ? ie : 0);
					break;

				default: return false;
			}

			return (dojo.render.html.ie && supportedBy.ie) ||
				(dojo.render.html.mozilla && supportedBy.mozilla) ||
				(dojo.render.html.safari && supportedBy.safari) ||
				(gt420 && supportedBy.safari420) ||
				(dojo.render.html.opera && supportedBy.opera);  // Boolean: return true if the command is supported, false otherwise
		},

		execCommand: function (/*String*/command, argument){
			// summary: Executes a command in the Rich Text area
			// command: The command to execute
			// argument: An optional argument to the command
			var returnValue;

			//focus() is required for IE (none-activeX mode) to work
			//In addition, focus() makes sure after the execution of
			//the command, the editor receives the focus as expected
			this.focus();

			command = this._normalizeCommand(command);
			if (argument != undefined) {
				if(command == "heading") { throw new Error("unimplemented"); }
				else if(command == "formatblock"){
					if(this.object){ //IE activeX mode
						argument = this._native2LocalFormatNames[argument];
					}
					else if(drh.ie){ argument = '<'+argument+'>'; }
				}
			}
			if(this.object){
				switch (command) {
					case "hilitecolor":
						command = "setbackcolor";
						break;
					case "forecolor":
					case "backcolor":
					case "fontsize":
					case "fontname":
						command = "set" + command;
						break;
					case "formatblock":
						command = "setblockformat";
				}

				if(command == "strikethrough"){
					command = "inserthtml";
					var range = this.document.selection.createRange();
					if(!range.htmlText){
						return;
					}
					argument=range.htmlText.strike();
				}else if(command == "inserthorizontalrule"){
					command = "inserthtml";
					argument="<hr>";
				}

				if(command == "inserthtml"){
					var range = this.document.selection.createRange();
					if(this.document.selection.type.toUpperCase() == "CONTROL"){
						//if selection is controlrange, no pasteHTML is available,
						//we replace the outerHTML directly
						for(var i=0;i<range.length;i++){
							range.item(i).outerHTML = argument;
						}
					}else{
						// on IE, we can use the pasteHTML method of the textRange object
						// to get an undo-able innerHTML modification
						range.pasteHTML(argument);
						range.select();
					}
					returnValue = true;
				}else if(arguments.length == 1){
					returnValue = this.object.ExecCommand(this._activeX.command[command],
						this._activeX.ui.noprompt);
				}else{
					returnValue = this.object.ExecCommand(this._activeX.command[command],
						this._activeX.ui.noprompt, argument);
				}
			}else if(command == "inserthtml"){
				if(dojo.render.html.ie){
					//dojo.debug("inserthtml breaks the undo stack when not using the ActiveX version of the control!");
					var insertRange = this.document.selection.createRange();
					insertRange.pasteHTML(argument);
					insertRange.select();
					//insertRange.collapse(true);
					return true;
				}else{
					return this.document.execCommand(command, false, argument);
				}
			/* */
			// fix up unlink in Mozilla to unlink the link and not just the selection
			}else if((command == "unlink")&&
				(this.queryCommandEnabled("unlink"))&&
				(dojo.render.html.mozilla)){
				// grab selection
				// Mozilla gets upset if we just store the range so we have to
				// get the basic properties and recreate to save the selection
				var selection = this.window.getSelection();
				var selectionRange = selection.getRangeAt(0);
				var selectionStartContainer = selectionRange.startContainer;
				var selectionStartOffset = selectionRange.startOffset;
				var selectionEndContainer = selectionRange.endContainer;
				var selectionEndOffset = selectionRange.endOffset;

				// select our link and unlink
				var a = dojo.withGlobal(this.window, "getAncestorElement", dojo.html.selection, ['a']);
				dojo.withGlobal(this.window, "selectElement", dojo.html.selection, [a]);

				returnValue = this.document.execCommand("unlink", false, null);

				// restore original selection
				var selectionRange = this.document.createRange();
				selectionRange.setStart(selectionStartContainer, selectionStartOffset);
				selectionRange.setEnd(selectionEndContainer, selectionEndOffset);
				selection.removeAllRanges();
				selection.addRange(selectionRange);

				return returnValue;
			}else if((command == "hilitecolor")&&(dojo.render.html.mozilla)){
				// mozilla doesn't support hilitecolor properly when useCSS is
				// set to false (bugzilla #279330)

				this.document.execCommand("useCSS", false, false);
				returnValue = this.document.execCommand(command, false, argument);
				this.document.execCommand("useCSS", false, true);

			}else if((dojo.render.html.ie)&&( (command == "backcolor")||(command == "forecolor") )){
				// Tested under IE 6 XP2, no problem here, comment out
				// IE weirdly collapses ranges when we exec these commands, so prevent it
//				var tr = this.document.selection.createRange();
				argument = arguments.length > 1 ? argument : null;
				returnValue = this.document.execCommand(command, false, argument);

				// timeout is workaround for weird IE behavior were the text
				// selection gets correctly re-created, but subsequent input
				// apparently isn't bound to it
//				setTimeout(function(){tr.select();}, 1);
			}else{
				// dojo.debug("command:", command, "arg:", argument);

				argument = arguments.length > 1 ? argument : null;
//				if(dojo.render.html.moz){
//					this.document = this.iframe.contentWindow.document
//				}

				if(argument || command!="createlink") {
					returnValue = this.document.execCommand(command, false, argument);
				}
			}

			this.onDisplayChanged();
			return returnValue;
		},

		queryCommandEnabled: function(/*String*/command){
			// summary: check whether a command is enabled or not
			command = this._normalizeCommand(command);
			if(this.object){
				switch (command) {
					case "hilitecolor":
						command = "setbackcolor";
						break;
					case "forecolor":
					case "backcolor":
					case "fontsize":
					case "fontname":
						command = "set" + command;
						break;
					case "formatblock":
						command = "setblockformat";
						break;
					//below are not natively supported commands, we fake them
					case "strikethrough":
						command = "bold"; //whenever bold is enabled, strikethrough should be so as well
						break;
					case "inserthorizontalrule":
						return true;
				}

				if(typeof this._activeX.command[command] == "undefined"){ return false; }
				var status = this.object.QueryStatus(this._activeX.command[command]);
				return ((status != this._activeX.status.notsupported)&&
					(status != this._activeX.status.disabled));
			}else{
				if(dojo.render.html.mozilla){
					if(command == "unlink"){ // mozilla returns true always
						return dojo.withGlobal(this.window, "hasAncestorElement", dojo.html.selection, ['a']);
					} else if (command == "inserttable") {
						return true;
					}
				}

				// return this.document.queryCommandEnabled(command);
				var elem = (dojo.render.html.ie) ? this.document.selection.createRange() : this.document;
				return elem.queryCommandEnabled(command);
			}
		},

		queryCommandState: function(command){
			// summary: check the state of a given command
			command = this._normalizeCommand(command);
			if(this.object){
				if(command == "forecolor"){
					command = "setforecolor";
				}else if(command == "backcolor"){
					command = "setbackcolor";
				}else if(command == "strikethrough"){
					//check whether we are under a <strike>
					return dojo.withGlobal(this.window, "hasAncestorElement", dojo.html.selection, ['strike']);
				}else if(command == "inserthorizontalrule"){
					return false;
				}

				if(typeof this._activeX.command[command] == "undefined"){ return null; }
				var status = this.object.QueryStatus(this._activeX.command[command]);
				return ((status == this._activeX.status.latched)||
					(status == this._activeX.status.ninched));
			}else{
				return this.document.queryCommandState(command);
			}
		},

		queryCommandValue: function (command) {
			// summary: check the value of a given command
			command = this._normalizeCommand(command);
			if (this.object) {
				switch (command) {
					case "forecolor":
					case "backcolor":
					case "fontsize":
					case "fontname":
						command = "get" + command;
						return this.object.execCommand(
							this._activeX.command[command],
							this._activeX.ui.noprompt);
					case "formatblock":
						var retvalue = this.object.execCommand(
							this._activeX.command["getblockformat"],
							this._activeX.ui.noprompt);
						if(retvalue){
							return this._local2NativeFormatNames[retvalue];
						}
				}
			} else {
				if(dojo.render.html.ie && command == "formatblock"){
					return this._local2NativeFormatNames[this.document.queryCommandValue(command)] || this.document.queryCommandValue(command);
				}
				return this.document.queryCommandValue(command);
			}
		},


	/* Misc.
	 ********/

		placeCursorAtStart: function(){
			// summary:
			//		place the cursor at the start of the editing area
			this.focus();
			//see comments in placeCursorAtEnd
			if(dojo.render.html.moz && this.editNode.firstChild &&
				this.editNode.firstChild.nodeType != dojo.dom.TEXT_NODE){
				dojo.withGlobal(this.window, "selectElementChildren", dojo.html.selection, [this.editNode.firstChild]);
			}else{
				dojo.withGlobal(this.window, "selectElementChildren", dojo.html.selection, [this.editNode]);
			}
			dojo.withGlobal(this.window, "collapse", dojo.html.selection, [true]);
		},

		placeCursorAtEnd: function(){
			// summary:
			//		place the cursor at the end of the editing area
			this.focus();
			//In mozilla, if last child is not a text node, we have to use selectElementChildren on this.editNode.lastChild
			//otherwise the cursor would be placed at the end of the closing tag of this.editNode.lastChild
			if(dojo.render.html.moz && this.editNode.lastChild &&
				this.editNode.lastChild.nodeType != dojo.dom.TEXT_NODE){
				dojo.withGlobal(this.window, "selectElementChildren", dojo.html.selection, [this.editNode.lastChild]);
			}else{
				dojo.withGlobal(this.window, "selectElementChildren", dojo.html.selection, [this.editNode]);
			}
			dojo.withGlobal(this.window, "collapse", dojo.html.selection, [false]);
		},

		replaceEditorContent: function(/*String*/html){
			// summary:
			//		this function set the content while trying to maintain the undo stack
			html = this._preFilterContent(html);
			if(this.isClosed){
				this.domNode.innerHTML = html;
			}else if(this.window && this.window.getSelection && !dojo.render.html.moz){ // Safari
				// look ma! it's a totally f'd browser!
				this.editNode.innerHTML = html;
			}else if((this.window && this.window.getSelection) || (this.document && this.document.selection)){ // Moz/IE
				this.execCommand("selectall");
				this.execCommand("inserthtml", html);
			}
		},

		_preFilterContent: function(/*String*/html){
			// summary:
			//		filter the input before setting the content of the editing area
			var ec = html;
			dojo.lang.forEach(this.contentPreFilters, function(ef){
				ec = ef(ec);
			});
			if(this.contentDomPreFilters.length>0){
				var dom = dojo.doc().createElement('div');
				dom.style.display = "none";
				dojo.body().appendChild(dom);
				dom.innerHTML = ec;
				dojo.lang.forEach(this.contentDomPreFilters, function(ef){
					dom = ef(dom);
				});
				ec = dom.innerHTML;
				dojo.body().removeChild(dom);
			}
			return ec;
		},
		_postFilterContent: function(/*String*/html){
			// summary:
			//		filter the output after getting the content of the editing area
			var ec = html;
			if(this.contentDomPostFilters.length>0){
				var dom = this.document.createElement('div');
				dom.innerHTML = ec;
				dojo.lang.forEach(this.contentDomPostFilters, function(ef){
					dom = ef(dom);
				});
				ec = dom.innerHTML;
			}
			dojo.lang.forEach(this.contentPostFilters, function(ef){
				ec = ef(ec);
			});
			return ec;
		},

		//Int: stored last time height
		_lastHeight: 0,

		_updateHeight: function(){
			// summary:
			//		Updates the height of the editor area to fit the contents.
			if(!this.isLoaded){ return; }
			if(this.height){ return; }

			var height = dojo.html.getBorderBox(this.editNode).height;
			//height maybe zero in some cases even though the content is not empty,
			//we try the height of body instead
			if(!height){
				height = dojo.html.getBorderBox(this.document.body).height;
			}
			if(height == 0){
				dojo.debug("Can not figure out the height of the editing area!");
				return; //prevent setting height to 0
			}
			this._lastHeight = height;
			this.editorObject.style.height = this._lastHeight + "px";
			this.window.scrollTo(0, 0);
		},

		_saveContent: function(e){
			// summary:
			//		Saves the content in an onunload event if the editor has not been closed
			var saveTextarea = dojo.doc().getElementById("dojo.widget.RichText.savedContent");
			saveTextarea.value += this._SEPARATOR + this.saveName + ":" + this.getEditorContent();
		},

		getEditorContent: function(){
			// summary:
			//		return the current content of the editing area (post filters are applied)
			var ec = "";
			try{
				ec = (this._content.length > 0) ? this._content : this.editNode.innerHTML;
				if(dojo.string.trim(ec) == "&nbsp;"){ ec = ""; }
			}catch(e){ /* squelch */ }

			if(dojo.render.html.ie && !this.object){
				//removing appended <P>&nbsp;</P> for IE in none-activeX mode
				var re = new RegExp("(?:<p>&nbsp;</p>[\n\r]*)+$", "i");
				ec = ec.replace(re,"");
			}

			ec = this._postFilterContent(ec);

			if (this.relativeImageUrls) {
				// why use a regexp instead of dom? because IE is stupid
				// and won't let us set img.src to a relative URL
				// this comes after contentPostFilters because once content
				// gets innerHTML'd img urls will be fully qualified
				var siteBase = dojo.global().location.protocol + "//" + dojo.global().location.host;
				var pathBase = dojo.global().location.pathname;
				if (pathBase.match(/\/$/)) {
					// ends with slash, match full path
				} else {
					// match parent path to find siblings
					var pathParts = pathBase.split("/");
					if (pathParts.length) {
						pathParts.pop();
					}
					pathBase = pathParts.join("/") + "/";

				}

				var sameSite = new RegExp("(<img[^>]*\ src=[\"'])("+siteBase+"("+pathBase+")?)", "ig");
				ec = ec.replace(sameSite, "$1");
			}
			return ec;
		},

		close: function(/*Boolean*/save, /*Boolean*/force){
			// summery:
			//		Kills the editor and optionally writes back the modified contents to the
			//		element from which it originated.
			// save:
			//		Whether or not to save the changes. If false, the changes are discarded.
			// force:
			if(this.isClosed){return false; }

			if (arguments.length == 0) { save = true; }
			this._content = this._postFilterContent(this.editNode.innerHTML);
			var changed = (this.savedContent.innerHTML != this._content);

			// line height is squashed for iframes
			// FIXME: why was this here? if (this.iframe){ this.domNode.style.lineHeight = null; }

			if(this.interval){ clearInterval(this.interval); }

			if(dojo.render.html.ie && !this.object){
				dojo.event.browser.clean(this.editNode);
			}

			if (this.iframe) {
				// FIXME: should keep iframe around for later re-use
				delete this.iframe;
			}

			if(this.textarea){
				with(this.textarea.style){
					position = "";
					left = top = "";
					if(dojo.render.html.ie){
						overflow = this.__overflow;
						this.__overflow = null;
					}
				}

				this.domNode.parentNode.removeChild(this.domNode);
				this.domNode = this.textarea;
			}else{
				this.domNode.innerHTML = "";
			}

			if(save){
				// kill listeners on the saved content
				dojo.event.browser.clean(this.savedContent);
				if(dojo.render.html.moz){
					var nc = dojo.doc().createElement("span");
					this.domNode.appendChild(nc);
					nc.innerHTML = this.editNode.innerHTML;
				}else{
					this.domNode.innerHTML = this._content;
				}
			} else {
				while (this.savedContent.hasChildNodes()) {
					this.domNode.appendChild(this.savedContent.firstChild);
				}
			}
			delete this.savedContent;

			dojo.html.removeClass(this.domNode, "RichTextEditable");
			this.isClosed = true;
			this.isLoaded = false;
			// FIXME: is this always the right thing to do?
			delete this.editNode;

			if(this.window._frameElement){
				this.window._frameElement = null;
			}

			this.window = null;
			this.document = null;
			this.object = null;
			this.editingArea = null;
			this.editorObject = null;

			return changed; // Boolean: whether the content has been modified
		},

		destroyRendering: function(){}, // stub!

		destroy: function (){
			this.destroyRendering();
			if(!this.isClosed){ this.close(false); }

			dojo.widget.RichText.superclass.destroy.call(this);
		},

		connect: function (targetObj, targetFunc, thisFunc) {
			// summary: convenient method for dojo.event.connect
			dojo.event.connect(targetObj, targetFunc, this, thisFunc);
		},

		disconnect: function (targetObj, targetFunc, thisFunc) {
			// summary: convenient method for dojo.event.disconnect
			dojo.event.disconnect(targetObj, targetFunc, this, thisFunc);
		},

		disconnectAllWithRoot: function (targetObj) {
			dojo.deprecated("disconnectAllWithRoot", "is deprecated. No need to disconnect manually", "0.5");
		},

		_fixContentForMoz: function(html){
			// summary:
			//		Moz can not handle strong/em tags correctly, correct them here
			html = html.replace(/<strong([ \>])/gi, '<b$1' );
			html = html.replace(/<\/strong>/gi, '<\/b>' );
			html = html.replace(/<em([ \>])/gi, '<i$1' );
			html = html.replace(/<\/em>/gi, '<\/i>' );
			return html;
		}
	},
	"html",
	function(){
		// summary:
		//		Constructor for this widget, initialize per-instance variables

		// Array: pre content filter function register array
		this.contentPreFilters = [];
		// Array: post content filter function register array
		this.contentPostFilters = [];
		// Array: pre content dom filter function register array
		this.contentDomPreFilters = [];
		// Array: post content dom filter function register array
		this.contentDomPostFilters = [];
		// String: semicolon (";") separated list of css files for the editing area
		this.styleSheets = "";
		// Array: array to store all the stylesheets applied to the editing area
		this.editingAreaStyleSheets=[];
		if(dojo.render.html.moz){
			this.contentPreFilters.push(this._fixContentForMoz);
		}

		this._keyHandlers = {};
	}
);
