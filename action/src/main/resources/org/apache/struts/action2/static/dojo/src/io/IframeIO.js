/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.io.IframeIO");
dojo.require("dojo.io.BrowserIO");
dojo.require("dojo.uri.*");

dojo.io.createIFrame = function(fname, onloadstr){
	if(window[fname]){ return window[fname]; }
	if(window.frames[fname]){ return window.frames[fname]; }
	var r = dojo.render.html;
	var cframe = null;
	var turi = dojo.uri.dojoUri("iframe_history.html?noInit=true");
	var ifrstr = ((r.ie)&&(dojo.render.os.win)) ? "<iframe name='"+fname+"' src='"+turi+"' onload='"+onloadstr+"'>" : "iframe";
	cframe = document.createElement(ifrstr);
	with(cframe){
		name = fname;
		setAttribute("name", fname);
		id = fname;
	}
	(document.body||document.getElementsByTagName("body")[0]).appendChild(cframe);
	window[fname] = cframe;
	with(cframe.style){
		position = "absolute";
		left = top = "0px";
		height = width = "1px";
		visibility = "hidden";
		/*
		if(djConfig.isDebug){
			position = "relative";
			height = "300px";
			width = "600px";
			visibility = "visible";
		}
		*/
	}

	if(!r.ie){
		dojo.io.setIFrameSrc(cframe, turi, true);
		cframe.onload = new Function(onloadstr);
	}
	return cframe;
}

// thanks burstlib!
dojo.io.iframeContentWindow = function(iframe_el) {
	var win = iframe_el.contentWindow || // IE
		dojo.io.iframeContentDocument(iframe_el).defaultView || // Moz, opera
		// Moz. TODO: is this available when defaultView isn't?
		dojo.io.iframeContentDocument(iframe_el).__parent__ || 
		(iframe_el.name && document.frames[iframe_el.name]) || null;
	return win;
}

dojo.io.iframeContentDocument = function(iframe_el){
	var doc = iframe_el.contentDocument || // W3
		(
			(iframe_el.contentWindow)&&(iframe_el.contentWindow.document)
		) ||  // IE
		(
			(iframe_el.name)&&(document.frames[iframe_el.name])&&
			(document.frames[iframe_el.name].document)
		) || null;
	return doc;
}

dojo.io.IframeTransport = new function(){
	var _this = this;
	this.currentRequest = null;
	this.requestQueue = [];
	this.iframeName = "dojoIoIframe";

	this.fireNextRequest = function(){
		if((this.currentRequest)||(this.requestQueue.length == 0)){ return; }
		// dojo.debug("fireNextRequest");
		var cr = this.currentRequest = this.requestQueue.shift();
		var fn = cr["formNode"];
		if(fn){
			if(cr["content"]){
				// if we have things in content, we need to add them to the form
				// before submission
				for(var x in cr.content){
					if(!fn[x]){
						var tn;
						if(dojo.render.html.ie){
							tn = document.createElement("<input type='hidden' name='"+x+"' value='"+cr.content[x]+"'>");
							fn.appendChild(tn);
						}else{
							tn = document.createElement("input");
							fn.appendChild(tn);
							tn.type = "hidden";
							tn.name = x;
							tn.value = cr.content[x];
						}
					}else{
						fn[x].value = cr.content[x];
					}
				}
			}
			if(cr["url"]){
				fn.setAttribute("action", cr.url);
			}
			if(!fn.getAttribute("method")){
				fn.setAttribute("method", (cr["method"]) ? cr["method"] : "post");
			}
			fn.setAttribute("target", this.iframeName);
			fn.target = this.iframeName;
			fn.submit();
		}else{
			// otherwise we post a GET string by changing URL location for the
			// iframe
			var query = dojo.io.argsFromMap(this.currentRequest.content);
			var tmpUrl = (cr.url.indexOf("?") > -1 ? "&" : "?") + query;
			dojo.io.setIFrameSrc(this.iframe, tmpUrl, true);
		}
	}

	this.canHandle = function(kwArgs){
		return (
			(
				// FIXME: can we really handle text/plain and
				// text/javascript requests?
				dojo.lang.inArray(kwArgs["mimetype"], 
				[	"text/plain", "text/html", 
					"application/xml", "text/xml", 
					"text/javascript", "text/json"])
			)&&(
				// make sur we really only get used in file upload cases	
				(kwArgs["formNode"])&&(dojo.io.checkChildrenForFile(kwArgs["formNode"]))
			)&&(
				dojo.lang.inArray(kwArgs["method"].toLowerCase(), ["post", "get"])
			)&&(
				// never handle a sync request
				!  ((kwArgs["sync"])&&(kwArgs["sync"] == true))
			)
		);
	}

	this.bind = function(kwArgs){
		this.requestQueue.push(kwArgs);
		this.fireNextRequest();
		return;
	}

	this.setUpIframe = function(){

		// NOTE: IE 5.0 and earlier Mozilla's don't support an onload event for
		//       iframes. OTOH, we don't care.
		this.iframe = dojo.io.createIFrame(this.iframeName, "dojo.io.IframeTransport.iframeOnload();");
	}

	this.iframeOnload = function(){
		if(!_this.currentRequest){
			_this.fireNextRequest();
			return;
		}
		var ifr = _this.iframe;
		var ifw = dojo.io.iframeContentWindow(ifr);
		// handle successful returns
		// FIXME: how do we determine success for iframes? Is there an equiv of
		// the "status" property?
		var value;
		try{
			var cmt = _this.currentRequest.mimetype;
			if((cmt == "text/javascript")||(cmt == "text/json")){
				// FIXME: not sure what to do here? try to pull some evalulable
				// text from a textarea or cdata section? 
				// how should we set up the contract for that?
				var cd = dojo.io.iframeContentDocument(_this.iframe);
				var js = cd.getElementsByTagName("textarea")[0].value;
				if(cmt == "text/json") { js = "(" + js + ")"; }
				value = dj_eval(js);
			}else if((cmt == "application/xml")||(cmt == "text/xml")){
				value = dojo.io.iframeContentDocument(_this.iframe);
			}else{ // text/plain
				value = ifw.innerHTML;
			}
			if(typeof _this.currentRequest.load == "function"){
				_this.currentRequest.load("load", value, _this.currentRequest);
			}
		}catch(e){ 
			// looks like we didn't get what we wanted!
			var errObj = new dojo.io.Error("IframeTransport Error");
			if(typeof _this.currentRequest["error"] == "function"){
				_this.currentRequest.error("error", errObj, _this.currentRequest);
			}
		}
		_this.currentRequest = null;
		_this.fireNextRequest();
	}

	dojo.io.transports.addTransport("IframeTransport");
}

dojo.addOnLoad(function(){
	dojo.io.IframeTransport.setUpIframe();
});
