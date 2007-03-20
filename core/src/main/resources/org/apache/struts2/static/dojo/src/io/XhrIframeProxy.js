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
dojo.require("dojo.html.iframe");
dojo.require("dojo.dom");
dojo.require("dojo.uri.Uri");

/*
TODO: This page might generate a "loading unsecure items on a secure page"
popup in browsers if it is served on a https URL, given that we are not
setting a src on the iframe element.
//TODO: Document that it doesn't work from local disk in Safari.

*/

dojo.io.XhrIframeProxy = new function(){
	this.xipClientUrl = dojo.uri.dojoUri("src/io/xip_client.html");

	this._state = {};
	this._stateIdCounter = 0;

	this.send = function(facade){		
		var stateId = "XhrIframeProxy" + (this._stateIdCounter++);
		facade._stateId = stateId;

		this._state[stateId] = {
			facade: facade,
			stateId: stateId,
			clientFrame: dojo.io.createIFrame(stateId,
				"dojo.io.XhrIframeProxy.clientFrameLoaded('" + stateId + "');",
				this.xipClientUrl)
		};
	}
	
	this.receive = function(stateId, urlEncodedData){
		/* urlEncodedData should have the following params:
				- responseHeaders
				- status
				- statusText
				- responseText
		*/
		//Decode response data.
		var response = {};
		var nvPairs = urlEncodedData.split("&");
		for(var i = 0; i < nvPairs.length; i++){
			if(nvPairs[i]){
				var nameValue = nvPairs[i].split("=");
				response[decodeURIComponent(nameValue[0])] = decodeURIComponent(nameValue[1]);
			}
		}

		//Set data on facade object.
		var state = this._state[stateId];
		var facade = state.facade;

		facade._setResponseHeaders(response.responseHeaders);
		if(response.status == 0 || response.status){
			facade.status = parseInt(response.status, 10);
		}
		if(response.statusText){
			facade.statusText = response.statusText;
		}
		if(response.responseText){
			facade.responseText = response.responseText;
			
			//Fix responseXML.
			var contentType = facade.getResponseHeader("Content-Type");
			if(contentType && (contentType == "application/xml" || contentType == "text/xml")){
				facade.responseXML = dojo.dom.createDocumentFromText(response.responseText, contentType);
			}
		}
		facade.readyState = 4;
		
		this.destroyState(stateId);
	}

	this.clientFrameLoaded = function(stateId){
		var state = this._state[stateId];
		var facade = state.facade;
		var clientWindow = dojo.html.iframeContentWindow(state.clientFrame);
		
		var reqHeaders = [];
		for(var param in facade._requestHeaders){
			reqHeaders.push(param + ": " + facade._requestHeaders[param]);
		}
		
		var requestData = {
			uri: facade._uri
		};
		if(reqHeaders.length > 0){
			requestData.requestHeaders = reqHeaders.join("\r\n");		
		}
		if(facade._method){
			requestData.method = facade._method;
		}
		if(facade._bodyData){
			requestData.data = facade._bodyData;
		}

		clientWindow.send(stateId, facade._ifpServerUrl, dojo.io.argsFromMap(requestData, "utf8"));
	}
	
	this.destroyState = function(stateId){
		var state = this._state[stateId];
		if(state){
			delete this._state[stateId];
			var parentNode = state.clientFrame.parentNode;
			parentNode.removeChild(state.clientFrame);
			state.clientFrame = null;
			state = null;
		}
	}

	this.createFacade = function(){
		if(arguments && arguments[0] && arguments[0]["iframeProxyUrl"]){
			return new dojo.io.XhrIframeFacade(arguments[0]["iframeProxyUrl"]);
		}else{
			return dojo.io.XhrIframeProxy.oldGetXmlhttpObject.apply(dojo.hostenv, arguments);
		}
	}
}

//Replace the normal XHR factory with the proxy one.
dojo.io.XhrIframeProxy.oldGetXmlhttpObject = dojo.hostenv.getXmlhttpObject;
dojo.hostenv.getXmlhttpObject = dojo.io.XhrIframeProxy.createFacade;

/**
	Using this a reference: http://www.w3.org/TR/XMLHttpRequest/

	Does not implement the onreadystate callback since dojo.io.BrowserIO does
	not use it.
*/
dojo.io.XhrIframeFacade = function(ifpServerUrl){
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
}

dojo.lang.extend(dojo.io.XhrIframeFacade, {
	//The open method does not properly reset since Dojo does not reuse XHR objects.
	open: function(method, uri){
		this._method = method;
		this._uri = uri;

		this.readyState = 1;
	},
	
	setRequestHeader: function(header, value){
		this._requestHeaders[header] = value;
	},
	
	send: function(stringData){
		this._bodyData = stringData;
		
		dojo.io.XhrIframeProxy.send(this);
		
		this.readyState = 2;
	},
	abort: function(){
		dojo.io.XhrIframeProxy.destroyState(this._stateId);
	},
	
	getAllResponseHeaders: function(){
		return this._allResponseHeaders;

	},
	
	getResponseHeader: function(header){
		return this._responseHeaders[header];
	},
	
	_setResponseHeaders: function(allHeaders){
		if(allHeaders){
			this._allResponseHeaders = allHeaders;
			
			//Make sure ther are now CR characters in the headers.
			allHeaders = allHeaders.replace(/\r/g, "");
			var nvPairs = allHeaders.split("\n");
			for(var i = 0; i < nvPairs.length; i++){
				if(nvPairs[i]){
					var nameValue = nvPairs[i].split(": ");
					this._responseHeaders[nameValue[0]] = nameValue[1];
				}
			}
		}
	}
});
