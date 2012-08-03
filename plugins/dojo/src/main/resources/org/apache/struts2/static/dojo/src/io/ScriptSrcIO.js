/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.io.ScriptSrcIO");
dojo.require("dojo.io.BrowserIO");
dojo.require("dojo.undo.browser");
dojo.io.ScriptSrcTransport = new function () {
	this.preventCache = false;
	this.maxUrlLength = 1000;
	this.inFlightTimer = null;
	this.DsrStatusCodes = {Continue:100, Ok:200, Error:500};
	this.startWatchingInFlight = function () {
		if (!this.inFlightTimer) {
			this.inFlightTimer = setInterval("dojo.io.ScriptSrcTransport.watchInFlight();", 100);
		}
	};
	this.watchInFlight = function () {
		var totalCount = 0;
		var doneCount = 0;
		for (var param in this._state) {
			totalCount++;
			var currentState = this._state[param];
			if (currentState.isDone) {
				doneCount++;
				delete this._state[param];
			} else {
				if (!currentState.isFinishing) {
					var listener = currentState.kwArgs;
					try {
						if (currentState.checkString && eval("typeof(" + currentState.checkString + ") != 'undefined'")) {
							currentState.isFinishing = true;
							this._finish(currentState, "load");
							doneCount++;
							delete this._state[param];
						} else {
							if (listener.timeoutSeconds && listener.timeout) {
								if (currentState.startTime + (listener.timeoutSeconds * 1000) < (new Date()).getTime()) {
									currentState.isFinishing = true;
									this._finish(currentState, "timeout");
									doneCount++;
									delete this._state[param];
								}
							} else {
								if (!listener.timeoutSeconds) {
									doneCount++;
								}
							}
						}
					}
					catch (e) {
						currentState.isFinishing = true;
						this._finish(currentState, "error", {status:this.DsrStatusCodes.Error, response:e});
					}
				}
			}
		}
		if (doneCount >= totalCount) {
			clearInterval(this.inFlightTimer);
			this.inFlightTimer = null;
		}
	};
	this.canHandle = function (kwArgs) {
		return dojo.lang.inArray(["text/javascript", "text/json", "application/json"], (kwArgs["mimetype"].toLowerCase())) && (kwArgs["method"].toLowerCase() == "get") && !(kwArgs["formNode"] && dojo.io.formHasFile(kwArgs["formNode"])) && (!kwArgs["sync"] || kwArgs["sync"] == false) && !kwArgs["file"] && !kwArgs["multipart"];
	};
	this.removeScripts = function () {
		var scripts = document.getElementsByTagName("script");
		for (var i = 0; scripts && i < scripts.length; i++) {
			var scriptTag = scripts[i];
			if (scriptTag.className == "ScriptSrcTransport") {
				var parent = scriptTag.parentNode;
				parent.removeChild(scriptTag);
				i--;
			}
		}
	};
	this.bind = function (kwArgs) {
		var url = kwArgs.url;
		var query = "";
		if (kwArgs["formNode"]) {
			var ta = kwArgs.formNode.getAttribute("action");
			if ((ta) && (!kwArgs["url"])) {
				url = ta;
			}
			var tp = kwArgs.formNode.getAttribute("method");
			if ((tp) && (!kwArgs["method"])) {
				kwArgs.method = tp;
			}
			query += dojo.io.encodeForm(kwArgs.formNode, kwArgs.encoding, kwArgs["formFilter"]);
		}
		if (url.indexOf("#") > -1) {
			dojo.debug("Warning: dojo.io.bind: stripping hash values from url:", url);
			url = url.split("#")[0];
		}
		var urlParts = url.split("?");
		if (urlParts && urlParts.length == 2) {
			url = urlParts[0];
			query += (query ? "&" : "") + urlParts[1];
		}
		if (kwArgs["backButton"] || kwArgs["back"] || kwArgs["changeUrl"]) {
			dojo.undo.browser.addToHistory(kwArgs);
		}
		var id = kwArgs["apiId"] ? kwArgs["apiId"] : "id" + this._counter++;
		var content = kwArgs["content"];
		var jsonpName = kwArgs.jsonParamName;
		if (kwArgs.sendTransport || jsonpName) {
			if (!content) {
				content = {};
			}
			if (kwArgs.sendTransport) {
				content["dojo.transport"] = "scriptsrc";
			}
			if (jsonpName) {
				content[jsonpName] = "dojo.io.ScriptSrcTransport._state." + id + ".jsonpCall";
			}
		}
		if (kwArgs.postContent) {
			query = kwArgs.postContent;
		} else {
			if (content) {
				query += ((query) ? "&" : "") + dojo.io.argsFromMap(content, kwArgs.encoding, jsonpName);
			}
		}
		if (kwArgs["apiId"]) {
			kwArgs["useRequestId"] = true;
		}
		var state = {"id":id, "idParam":"_dsrid=" + id, "url":url, "query":query, "kwArgs":kwArgs, "startTime":(new Date()).getTime(), "isFinishing":false};
		if (!url) {
			this._finish(state, "error", {status:this.DsrStatusCodes.Error, statusText:"url.none"});
			return;
		}
		if (content && content[jsonpName]) {
			state.jsonp = content[jsonpName];
			state.jsonpCall = function (data) {
				if (data["Error"] || data["error"]) {
					if (dojo["json"] && dojo["json"]["serialize"]) {
						dojo.debug(dojo.json.serialize(data));
					}
					dojo.io.ScriptSrcTransport._finish(this, "error", data);
				} else {
					dojo.io.ScriptSrcTransport._finish(this, "load", data);
				}
			};
		}
		if (kwArgs["useRequestId"] || kwArgs["checkString"] || state["jsonp"]) {
			this._state[id] = state;
		}
		if (kwArgs["checkString"]) {
			state.checkString = kwArgs["checkString"];
		}
		state.constantParams = (kwArgs["constantParams"] == null ? "" : kwArgs["constantParams"]);
		if (kwArgs["preventCache"] || (this.preventCache == true && kwArgs["preventCache"] != false)) {
			state.nocacheParam = "dojo.preventCache=" + new Date().valueOf();
		} else {
			state.nocacheParam = "";
		}
		var urlLength = state.url.length + state.query.length + state.constantParams.length + state.nocacheParam.length + this._extraPaddingLength;
		if (kwArgs["useRequestId"]) {
			urlLength += state.idParam.length;
		}
		if (!kwArgs["checkString"] && kwArgs["useRequestId"] && !state["jsonp"] && !kwArgs["forceSingleRequest"] && urlLength > this.maxUrlLength) {
			if (url > this.maxUrlLength) {
				this._finish(state, "error", {status:this.DsrStatusCodes.Error, statusText:"url.tooBig"});
				return;
			} else {
				this._multiAttach(state, 1);
			}
		} else {
			var queryParams = [state.constantParams, state.nocacheParam, state.query];
			if (kwArgs["useRequestId"] && !state["jsonp"]) {
				queryParams.unshift(state.idParam);
			}
			var finalUrl = this._buildUrl(state.url, queryParams);
			state.finalUrl = finalUrl;
			this._attach(state.id, finalUrl);
		}
		this.startWatchingInFlight();
	};
	this._counter = 1;
	this._state = {};
	this._extraPaddingLength = 16;
	this._buildUrl = function (url, nameValueArray) {
		var finalUrl = url;
		var joiner = "?";
		for (var i = 0; i < nameValueArray.length; i++) {
			if (nameValueArray[i]) {
				finalUrl += joiner + nameValueArray[i];
				joiner = "&";
			}
		}
		return finalUrl;
	};
	this._attach = function (id, url) {
		var element = document.createElement("script");
		element.type = "text/javascript";
		element.src = url;
		element.id = id;
		element.className = "ScriptSrcTransport";
		document.getElementsByTagName("head")[0].appendChild(element);
	};
	this._multiAttach = function (state, part) {
		if (state.query == null) {
			this._finish(state, "error", {status:this.DsrStatusCodes.Error, statusText:"query.null"});
			return;
		}
		if (!state.constantParams) {
			state.constantParams = "";
		}
		var queryMax = this.maxUrlLength - state.idParam.length - state.constantParams.length - state.url.length - state.nocacheParam.length - this._extraPaddingLength;
		var isDone = state.query.length < queryMax;
		var currentQuery;
		if (isDone) {
			currentQuery = state.query;
			state.query = null;
		} else {
			var ampEnd = state.query.lastIndexOf("&", queryMax - 1);
			var eqEnd = state.query.lastIndexOf("=", queryMax - 1);
			if (ampEnd > eqEnd || eqEnd == queryMax - 1) {
				currentQuery = state.query.substring(0, ampEnd);
				state.query = state.query.substring(ampEnd + 1, state.query.length);
			} else {
				currentQuery = state.query.substring(0, queryMax);
				var queryName = currentQuery.substring((ampEnd == -1 ? 0 : ampEnd + 1), eqEnd);
				state.query = queryName + "=" + state.query.substring(queryMax, state.query.length);
			}
		}
		var queryParams = [currentQuery, state.idParam, state.constantParams, state.nocacheParam];
		if (!isDone) {
			queryParams.push("_part=" + part);
		}
		var url = this._buildUrl(state.url, queryParams);
		this._attach(state.id + "_" + part, url);
	};
	this._finish = function (state, callback, event) {
		if (callback != "partOk" && !state.kwArgs[callback] && !state.kwArgs["handle"]) {
			if (callback == "error") {
				state.isDone = true;
				throw event;
			}
		} else {
			switch (callback) {
			  case "load":
				var response = event ? event.response : null;
				if (!response) {
					response = event;
				}
				state.kwArgs[(typeof state.kwArgs.load == "function") ? "load" : "handle"]("load", response, event, state.kwArgs);
				state.isDone = true;
				break;
			  case "partOk":
				var part = parseInt(event.response.part, 10) + 1;
				if (event.response.constantParams) {
					state.constantParams = event.response.constantParams;
				}
				this._multiAttach(state, part);
				state.isDone = false;
				break;
			  case "error":
				state.kwArgs[(typeof state.kwArgs.error == "function") ? "error" : "handle"]("error", event.response, event, state.kwArgs);
				state.isDone = true;
				break;
			  default:
				state.kwArgs[(typeof state.kwArgs[callback] == "function") ? callback : "handle"](callback, event, event, state.kwArgs);
				state.isDone = true;
			}
		}
	};
	dojo.io.transports.addTransport("ScriptSrcTransport");
};
window.onscriptload = function (event) {
	var state = null;
	var transport = dojo.io.ScriptSrcTransport;
	if (transport._state[event.id]) {
		state = transport._state[event.id];
	} else {
		var tempState;
		for (var param in transport._state) {
			tempState = transport._state[param];
			if (tempState.finalUrl && tempState.finalUrl == event.id) {
				state = tempState;
				break;
			}
		}
		if (state == null) {
			var scripts = document.getElementsByTagName("script");
			for (var i = 0; scripts && i < scripts.length; i++) {
				var scriptTag = scripts[i];
				if (scriptTag.getAttribute("class") == "ScriptSrcTransport" && scriptTag.src == event.id) {
					state = transport._state[scriptTag.id];
					break;
				}
			}
		}
		if (state == null) {
			throw "No matching state for onscriptload event.id: " + event.id;
		}
	}
	var callbackName = "error";
	switch (event.status) {
	  case dojo.io.ScriptSrcTransport.DsrStatusCodes.Continue:
		callbackName = "partOk";
		break;
	  case dojo.io.ScriptSrcTransport.DsrStatusCodes.Ok:
		callbackName = "load";
		break;
	}
	transport._finish(state, callbackName, event);
};

