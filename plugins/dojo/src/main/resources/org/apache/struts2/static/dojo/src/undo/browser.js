/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.undo.browser");
dojo.require("dojo.io.common");
try {
	if ((!djConfig["preventBackButtonFix"]) && (!dojo.hostenv.post_load_)) {
		document.write("<iframe style='border: 0px; width: 1px; height: 1px; position: absolute; bottom: 0px; right: 0px; visibility: visible;' name='djhistory' id='djhistory' src='" + (djConfig["dojoIframeHistoryUrl"] || dojo.hostenv.getBaseScriptUri() + "iframe_history.html") + "'></iframe>");
	}
}
catch (e) {
}
if (dojo.render.html.opera) {
	dojo.debug("Opera is not supported with dojo.undo.browser, so back/forward detection will not work.");
}
dojo.undo.browser = {initialHref:(!dj_undef("window")) ? window.location.href : "", initialHash:(!dj_undef("window")) ? window.location.hash : "", moveForward:false, historyStack:[], forwardStack:[], historyIframe:null, bookmarkAnchor:null, locationTimer:null, setInitialState:function (args) {
	this.initialState = this._createState(this.initialHref, args, this.initialHash);
}, addToHistory:function (args) {
	this.forwardStack = [];
	var hash = null;
	var url = null;
	if (!this.historyIframe) {
		if (djConfig["useXDomain"] && !djConfig["dojoIframeHistoryUrl"]) {
			dojo.debug("dojo.undo.browser: When using cross-domain Dojo builds," + " please save iframe_history.html to your domain and set djConfig.dojoIframeHistoryUrl" + " to the path on your domain to iframe_history.html");
		}
		this.historyIframe = window.frames["djhistory"];
	}
	if (!this.bookmarkAnchor) {
		this.bookmarkAnchor = document.createElement("a");
		dojo.body().appendChild(this.bookmarkAnchor);
		this.bookmarkAnchor.style.display = "none";
	}
	if (args["changeUrl"]) {
		hash = "#" + ((args["changeUrl"] !== true) ? args["changeUrl"] : (new Date()).getTime());
		if (this.historyStack.length == 0 && this.initialState.urlHash == hash) {
			this.initialState = this._createState(url, args, hash);
			return;
		} else {
			if (this.historyStack.length > 0 && this.historyStack[this.historyStack.length - 1].urlHash == hash) {
				this.historyStack[this.historyStack.length - 1] = this._createState(url, args, hash);
				return;
			}
		}
		this.changingUrl = true;
		setTimeout("window.location.href = '" + hash + "'; dojo.undo.browser.changingUrl = false;", 1);
		this.bookmarkAnchor.href = hash;
		if (dojo.render.html.ie) {
			url = this._loadIframeHistory();
			var oldCB = args["back"] || args["backButton"] || args["handle"];
			var tcb = function (handleName) {
				if (window.location.hash != "") {
					setTimeout("window.location.href = '" + hash + "';", 1);
				}
				oldCB.apply(this, [handleName]);
			};
			if (args["back"]) {
				args.back = tcb;
			} else {
				if (args["backButton"]) {
					args.backButton = tcb;
				} else {
					if (args["handle"]) {
						args.handle = tcb;
					}
				}
			}
			var oldFW = args["forward"] || args["forwardButton"] || args["handle"];
			var tfw = function (handleName) {
				if (window.location.hash != "") {
					window.location.href = hash;
				}
				if (oldFW) {
					oldFW.apply(this, [handleName]);
				}
			};
			if (args["forward"]) {
				args.forward = tfw;
			} else {
				if (args["forwardButton"]) {
					args.forwardButton = tfw;
				} else {
					if (args["handle"]) {
						args.handle = tfw;
					}
				}
			}
		} else {
			if (dojo.render.html.moz) {
				if (!this.locationTimer) {
					this.locationTimer = setInterval("dojo.undo.browser.checkLocation();", 200);
				}
			}
		}
	} else {
		url = this._loadIframeHistory();
	}
	this.historyStack.push(this._createState(url, args, hash));
}, checkLocation:function () {
	if (!this.changingUrl) {
		var hsl = this.historyStack.length;
		if ((window.location.hash == this.initialHash || window.location.href == this.initialHref) && (hsl == 1)) {
			this.handleBackButton();
			return;
		}
		if (this.forwardStack.length > 0) {
			if (this.forwardStack[this.forwardStack.length - 1].urlHash == window.location.hash) {
				this.handleForwardButton();
				return;
			}
		}
		if ((hsl >= 2) && (this.historyStack[hsl - 2])) {
			if (this.historyStack[hsl - 2].urlHash == window.location.hash) {
				this.handleBackButton();
				return;
			}
		}
	}
}, iframeLoaded:function (evt, ifrLoc) {
	if (!dojo.render.html.opera) {
		var query = this._getUrlQuery(ifrLoc.href);
		if (query == null) {
			if (this.historyStack.length == 1) {
				this.handleBackButton();
			}
			return;
		}
		if (this.moveForward) {
			this.moveForward = false;
			return;
		}
		if (this.historyStack.length >= 2 && query == this._getUrlQuery(this.historyStack[this.historyStack.length - 2].url)) {
			this.handleBackButton();
		} else {
			if (this.forwardStack.length > 0 && query == this._getUrlQuery(this.forwardStack[this.forwardStack.length - 1].url)) {
				this.handleForwardButton();
			}
		}
	}
}, handleBackButton:function () {
	var current = this.historyStack.pop();
	if (!current) {
		return;
	}
	var last = this.historyStack[this.historyStack.length - 1];
	if (!last && this.historyStack.length == 0) {
		last = this.initialState;
	}
	if (last) {
		if (last.kwArgs["back"]) {
			last.kwArgs["back"]();
		} else {
			if (last.kwArgs["backButton"]) {
				last.kwArgs["backButton"]();
			} else {
				if (last.kwArgs["handle"]) {
					last.kwArgs.handle("back");
				}
			}
		}
	}
	this.forwardStack.push(current);
}, handleForwardButton:function () {
	var last = this.forwardStack.pop();
	if (!last) {
		return;
	}
	if (last.kwArgs["forward"]) {
		last.kwArgs.forward();
	} else {
		if (last.kwArgs["forwardButton"]) {
			last.kwArgs.forwardButton();
		} else {
			if (last.kwArgs["handle"]) {
				last.kwArgs.handle("forward");
			}
		}
	}
	this.historyStack.push(last);
}, _createState:function (url, args, hash) {
	return {"url":url, "kwArgs":args, "urlHash":hash};
}, _getUrlQuery:function (url) {
	var segments = url.split("?");
	if (segments.length < 2) {
		return null;
	} else {
		return segments[1];
	}
}, _loadIframeHistory:function () {
	var url = (djConfig["dojoIframeHistoryUrl"] || dojo.hostenv.getBaseScriptUri() + "iframe_history.html") + "?" + (new Date()).getTime();
	this.moveForward = true;
	dojo.io.setIFrameSrc(this.historyIframe, url, false);
	return url;
}};

