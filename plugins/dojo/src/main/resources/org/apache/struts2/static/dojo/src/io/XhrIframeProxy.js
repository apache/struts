/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.io.XhrIframeProxy");
dojo.require("dojo.experimental");
dojo.experimental("dojo.io.XhrIframeProxy");
dojo.require("dojo.io.IframeIO");
dojo.require("dojo.dom");
dojo.require("dojo.uri.Uri");
dojo.io.XhrIframeProxy = {xipClientUrl:djConfig["xipClientUrl"] || dojo.uri.moduleUri("dojo.io", "xip_client.html"), _state:{}, _stateIdCounter:0, needFrameRecursion:function () {
	return (true == dojo.render.html.ie70);
}, send:function (facade) {
	var stateId = "XhrIframeProxy" + (this._stateIdCounter++);
	facade._stateId = stateId;
	var frameUrl = this.xipClientUrl + "#0:init:id=" + stateId + "&server=" + encodeURIComponent(facade._ifpServerUrl) + "&fr=false";
	if (this.needFrameRecursion()) {
		var fullClientUrl = window.location.href;
		if ((this.xipClientUrl + "").charAt(0) == "/") {
			var endIndex = fullClientUrl.indexOf("://");
			endIndex = fullClientUrl.indexOf("/", endIndex + 1);
			fullClientUrl = fullClientUrl.substring(0, endIndex);
		} else {
			fullClientUrl = fullClientUrl.substring(0, fullClientUrl.lastIndexOf("/") + 1);
		}
		fullClientUrl += this.xipClientUrl;
		var serverUrl = facade._ifpServerUrl + (facade._ifpServerUrl.indexOf("?") == -1 ? "?" : "&") + "dojo.fr=1";
		frameUrl = serverUrl + "#0:init:id=" + stateId + "&client=" + encodeURIComponent(fullClientUrl) + "&fr=" + this.needFrameRecursion();
	}
	this._state[stateId] = {facade:facade, stateId:stateId, clientFrame:dojo.io.createIFrame(stateId, "", frameUrl)};
	return stateId;
}, receive:function (stateId, urlEncodedData) {
	var response = {};
	var nvPairs = urlEncodedData.split("&");
	for (var i = 0; i < nvPairs.length; i++) {
		if (nvPairs[i]) {
			var nameValue = nvPairs[i].split("=");
			response[decodeURIComponent(nameValue[0])] = decodeURIComponent(nameValue[1]);
		}
	}
	var state = this._state[stateId];
	var facade = state.facade;
	facade._setResponseHeaders(response.responseHeaders);
	if (response.status == 0 || response.status) {
		facade.status = parseInt(response.status, 10);
	}
	if (response.statusText) {
		facade.statusText = response.statusText;
	}
	if (response.responseText) {
		facade.responseText = response.responseText;
		var contentType = facade.getResponseHeader("Content-Type");
		if (contentType && (contentType == "application/xml" || contentType == "text/xml")) {
			facade.responseXML = dojo.dom.createDocumentFromText(response.responseText, contentType);
		}
	}
	facade.readyState = 4;
	this.destroyState(stateId);
}, clientFrameLoaded:function (stateId) {
	var state = this._state[stateId];
	var facade = state.facade;
	if (this.needFrameRecursion()) {
		var clientWindow = window.open("", state.stateId + "_clientEndPoint");
	} else {
		var clientWindow = state.clientFrame.contentWindow;
	}
	var reqHeaders = [];
	for (var param in facade._requestHeaders) {
		reqHeaders.push(param + ": " + facade._requestHeaders[param]);
	}
	var requestData = {uri:facade._uri};
	if (reqHeaders.length > 0) {
		requestData.requestHeaders = reqHeaders.join("\r\n");
	}
	if (facade._method) {
		requestData.method = facade._method;
	}
	if (facade._bodyData) {
		requestData.data = facade._bodyData;
	}
	clientWindow.send(dojo.io.argsFromMap(requestData, "utf8"));
}, destroyState:function (stateId) {
	var state = this._state[stateId];
	if (state) {
		delete this._state[stateId];
		var parentNode = state.clientFrame.parentNode;
		parentNode.removeChild(state.clientFrame);
		state.clientFrame = null;
		state = null;
	}
}, createFacade:function () {
	if (arguments && arguments[0] && arguments[0]["iframeProxyUrl"]) {
		return new dojo.io.XhrIframeFacade(arguments[0]["iframeProxyUrl"]);
	} else {
		return dojo.io.XhrIframeProxy.oldGetXmlhttpObject.apply(dojo.hostenv, arguments);
	}
}};
dojo.io.XhrIframeProxy.oldGetXmlhttpObject = dojo.hostenv.getXmlhttpObject;
dojo.hostenv.getXmlhttpObject = dojo.io.XhrIframeProxy.createFacade;
dojo.io.XhrIframeFacade = function (ifpServerUrl) {
	this._requestHeaders = {};
	this._allResponseHeaders = null;
	this._responseHeaders = {};
	this._method = null;
	this._uri = null;
	this._bodyData = null;
	this.responseText = null;
	this.responseXML = null;
	this.status = null;
	this.statusText = null;
	this.readyState = 0;
	this._ifpServerUrl = ifpServerUrl;
	this._stateId = null;
};
dojo.lang.extend(dojo.io.XhrIframeFacade, {open:function (method, uri) {
	this._method = method;
	this._uri = uri;
	this.readyState = 1;
}, setRequestHeader:function (header, value) {
	this._requestHeaders[header] = value;
}, send:function (stringData) {
	this._bodyData = stringData;
	this._stateId = dojo.io.XhrIframeProxy.send(this);
	this.readyState = 2;
}, abort:function () {
	dojo.io.XhrIframeProxy.destroyState(this._stateId);
}, getAllResponseHeaders:function () {
	return this._allResponseHeaders;
}, getResponseHeader:function (header) {
	return this._responseHeaders[header];
}, _setResponseHeaders:function (allHeaders) {
	if (allHeaders) {
		this._allResponseHeaders = allHeaders;
		allHeaders = allHeaders.replace(/\r/g, "");
		var nvPairs = allHeaders.split("\n");
		for (var i = 0; i < nvPairs.length; i++) {
			if (nvPairs[i]) {
				var nameValue = nvPairs[i].split(": ");
				this._responseHeaders[nameValue[0]] = nameValue[1];
			}
		}
	}
}});

