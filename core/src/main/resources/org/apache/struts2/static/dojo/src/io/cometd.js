/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.require("dojo.io.common"); // io/common.js provides setIFrameSrc and the IO module
dojo.provide("dojo.io.cometd");
dojo.require("dojo.AdapterRegistry");
dojo.require("dojo.json");
dojo.require("dojo.io.BrowserIO"); // we need XHR for the handshake, etc.
// FIXME: determine if we can use XMLHTTP to make x-domain posts despite not
//        being able to hear back about the result
dojo.require("dojo.io.IframeIO");
dojo.require("dojo.io.ScriptSrcIO"); // for x-domain long polling
dojo.require("dojo.io.cookie"); // for peering
dojo.require("dojo.event.*");
dojo.require("dojo.lang.common");
dojo.require("dojo.lang.func");

/*
 * this file defines Comet protocol client. Actual message transport is
 * deferred to one of several connection type implementations. The default is a
 * forever-frame implementation. A single global object named "cometd" is
 * used to mediate for these connection types in order to provide a stable
 * interface.
 */

// TODO: the auth handling in this file is a *mess*. It should probably live in
// the cometd object with the ability to mix in or call down to an auth-handler
// object, the prototypical variant of which is a no-op

cometd = new function(){

	this.initialized = false;
	this.connected = false;

	this.connectionTypes = new dojo.AdapterRegistry(true);

	this.version = 0.1;
	this.minimumVersion = 0.1;
	this.clientId = null;

	this._isXD = false;
	this.handshakeReturn = null;
	this.currentTransport = null;
	this.url = null;
	this.lastMessage = null;
	this.globalTopicChannels = {};
	this.backlog = [];

	this.tunnelInit = function(childLocation, childDomain){
		// placeholder
	}

	this.tunnelCollapse = function(){
		dojo.debug("tunnel collapsed!");
		// placeholder
	}

	this.init = function(props, root, bargs){
		// FIXME: if the root isn't from the same host, we should automatically
		// try to select an XD-capable transport
		props = props||{};
		// go ask the short bus server what we can support
		props.version = this.version;
		props.minimumVersion = this.minimumVersion;
		props.channel = "/meta/handshake";
		// FIXME: do we just assume that the props knows
		// everything we care about WRT to auth? Should we be trying to
		// call back into it for subsequent auth actions? Should we fire
		// local auth functions to ask for/get auth data?

		// FIXME: what about ScriptSrcIO for x-domain comet?
		this.url = root||djConfig["cometdRoot"];
		if(!this.url){
			dojo.debug("no cometd root specified in djConfig and no root passed");
			return;
		}
		
		// FIXME: we need to select a way to handle JSONP-style stuff
		// generically here. We already know if the server is gonna be on
		// another domain (or can know it), so we should select appropriate
		// negotiation methods here as well as in final transport type
		// selection.
		var bindArgs = {
			url: this.url,
			method: "POST",
			mimetype: "text/json",
			load: dojo.lang.hitch(this, "finishInit"),
			content: { "message": dojo.json.serialize([props]) }
		};

		// borrowed from dojo.uri.Uri in lieu of fixed host and port properties
        var regexp = "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?$";
		var r = (""+window.location).match(new RegExp(regexp));
		if(r[4]){
			var tmp = r[4].split(":");
			var thisHost = tmp[0];
			var thisPort = tmp[1]||"80"; // FIXME: match 443

			r = this.url.match(new RegExp(regexp));
			if(r[4]){
				tmp = r[4].split(":");
				var urlHost = tmp[0];
				var urlPort = tmp[1]||"80";
				if(	(urlHost != thisHost)||
					(urlPort != thisPort) ){
					dojo.debug(thisHost, urlHost);
					dojo.debug(thisPort, urlPort);

					this._isXD = true;
					bindArgs.transport = "ScriptSrcTransport";
					bindArgs.jsonParamName = "jsonp";
					bindArgs.method = "GET";
				}
			}
		}
		if(bargs){
			dojo.lang.mixin(bindArgs, bargs);
		}
		return dojo.io.bind(bindArgs);
	}

	this.finishInit = function(type, data, evt, request){
		data = data[0];
		this.handshakeReturn = data;
		// pick a transport
		if(data["authSuccessful"] == false){
			dojo.debug("cometd authentication failed");
			return;
		}
		if(data.version < this.minimumVersion){
			dojo.debug("cometd protocol version mismatch. We wanted", this.minimumVersion, "but got", data.version);
			return;
		}
		this.currentTransport = this.connectionTypes.match(
			data.supportedConnectionTypes,
			data.version,
			this._isXD
		);
		this.currentTransport.version = data.version;
		this.clientId = data.clientId;
		this.tunnelInit = dojo.lang.hitch(this.currentTransport, "tunnelInit");
		this.tunnelCollapse = dojo.lang.hitch(this.currentTransport, "tunnelCollapse");
		this.initialized = true;
		this.currentTransport.startup(data);
		while(this.backlog.length != 0){
			var cur = this.backlog.shift();
			var fn = cur.shift();
			this[fn].apply(this, cur);
		}
	}

	this._getRandStr = function(){
		return Math.random().toString().substring(2, 10);
	}

	// public API functions called by cometd or by the transport classes
	this.deliver = function(messages){
		dojo.lang.forEach(messages, this._deliver, this);
	}

	this._deliver = function(message){
		// dipatch events along the specified path
		if(!message["channel"]){
			dojo.debug("cometd error: no channel for message!");
			return;
		}
		if(!this.currentTransport){
			this.backlog.push(["deliver", message]);
			return;
		}
		this.lastMessage = message;
		// check to see if we got a /meta channel message that we care about
		if(	(message.channel.length > 5)&&
			(message.channel.substr(0, 5) == "/meta")){
			// check for various meta topic actions that we need to respond to
			switch(message.channel){
				case "/meta/subscribe":
					if(!message.successful){
						dojo.debug("cometd subscription error for channel", message.channel, ":", message.error);
						return;
					}
					this.subscribed(message.subscription, message);
					break;
				case "/meta/unsubscribe":
					if(!message.successful){
						dojo.debug("cometd unsubscription error for channel", message.channel, ":", message.error);
						return;
					}
					this.unsubscribed(message.subscription, message);
					break;
			}
		}
		// send the message down for processing by the transport
		this.currentTransport.deliver(message);

		// dispatch the message to any locally subscribed listeners
		var tname = (this.globalTopicChannels[message.channel]) ? message.channel : "/cometd"+message.channel;
		dojo.event.topic.publish(tname, message);
	}

	this.disconnect = function(){
		if(!this.currentTransport){
			dojo.debug("no current transport to disconnect from");
			return;
		}
		this.currentTransport.disconnect();
	}

	// public API functions called by end users
	this.publish = function(/*string*/channel, /*object*/data, /*object*/properties){
		// summary: 
		//		publishes the passed message to the cometd server for delivery
		//		on the specified topic
		// channel:
		//		the destination channel for the message
		// data:
		//		a JSON object containing the message "payload"
		// properties:
		//		Optional. Other meta-data to be mixed into the top-level of the
		//		message
		if(!this.currentTransport){
			this.backlog.push(["publish", channel, data, properties]);
			return;
		}
		var message = {
			data: data,
			channel: channel
		};
		if(properties){
			dojo.lang.mixin(message, properties);
		}
		return this.currentTransport.sendMessage(message);
	}

	this.subscribe = function(	/*string*/				channel, 
								/*boolean, optional*/	useLocalTopics, 
								/*object, optional*/	objOrFunc, 
								/*string, optional*/	funcName){ // return: boolean
		// summary:
		//		inform the server of this client's interest in channel
		// channel:
		//		name of the cometd channel to subscribe to
		// useLocalTopics:
		//		Determines if up a local event topic subscription to the passed
		//		function using the channel name that was passed is constructed,
		//		or if the topic name will be prefixed with some other
		//		identifier for local message distribution. Setting this to
		//		"true" is a good way to hook up server-sent message delivery to
		//		pre-existing local topics.
		// objOrFunc:
		//		an object scope for funcName or the name or reference to a
		//		function to be called when messages are delivered to the
		//		channel
		// funcName:
		//		the second half of the objOrFunc/funcName pair for identifying
		//		a callback function to notifiy upon channel message delivery
		if(!this.currentTransport){
			this.backlog.push(["subscribe", channel, useLocalTopics, objOrFunc, funcName]);
			return;
		}
		if(objOrFunc){
			var tname = (useLocalTopics) ? channel : "/cometd"+channel;
			if(useLocalTopics){
				this.globalTopicChannels[channel] = true;
			}
			dojo.event.topic.subscribe(tname, objOrFunc, funcName);
		}
		// FIXME: would we handle queuing of the subscription if not connected?
		// Or should the transport object?
		return this.currentTransport.sendMessage({
			channel: "/meta/subscribe",
			subscription: channel
		});
	}

	this.subscribed = function(	/*string*/				channel, 
								/*obj*/					message){
		dojo.debug(channel);
		dojo.debugShallow(message);
	}

	this.unsubscribe = function(/*string*/				channel, 
								/*boolean, optional*/	useLocalTopics, 
								/*object, optional*/	objOrFunc, 
								/*string, optional*/	funcName){ // return: boolean
		// summary:
		//		inform the server of this client's disinterest in channel
		// channel:
		//		name of the cometd channel to subscribe to
		// useLocalTopics:
		//		Determines if up a local event topic subscription to the passed
		//		function using the channel name that was passed is destroyed,
		//		or if the topic name will be prefixed with some other
		//		identifier for stopping message distribution.
		// objOrFunc:
		//		an object scope for funcName or the name or reference to a
		//		function to be called when messages are delivered to the
		//		channel
		// funcName:
		//		the second half of the objOrFunc/funcName pair for identifying
		if(!this.currentTransport){
			this.backlog.push(["unsubscribe", channel, useLocalTopics, objOrFunc, funcName]);
			return;
		}
		//		a callback function to notifiy upon channel message delivery
		if(objOrFunc){
			// FIXME: should actual local topic unsubscription be delayed for
			// successful unsubcribe notices from the other end? (guessing "no")
			// FIXME: if useLocalTopics is false, should we go ahead and
			// destroy the local topic?
			var tname = (useLocalTopics) ? channel : "/cometd"+channel;
			dojo.event.topic.unsubscribe(tname, objOrFunc, funcName);
		}
		return this.currentTransport.sendMessage({
			channel: "/meta/unsubscribe",
			subscription: channel
		});
	}

	this.unsubscribed = function(/*string*/				channel, 
								/*obj*/					message){
		dojo.debug(channel);
		dojo.debugShallow(message);
	}

	// FIXME: add an "addPublisher" function

}

/*
transport objects MUST expose the following methods:
	- check
	- startup
	- sendMessage
	- deliver
	- disconnect
optional, standard but transport dependent methods are:
	- tunnelCollapse
	- tunnelInit

Transports SHOULD be namespaced under the cometd object and transports MUST
register themselves with cometd.connectionTypes

here's a stub transport defintion:

cometd.blahTransport = new function(){
	this.connected = false;
	this.connectionId = null;
	this.authToken = null;
	this.lastTimestamp = null;
	this.lastId = null;

	this.check = function(types, version, xdomain){
		// summary:
		//		determines whether or not this transport is suitable given a
		//		list of transport types that the server supports
		return dojo.lang.inArray(types, "blah");
	}

	this.startup = function(){
		if(this.connected){ return; }
		// FIXME: fill in startup routine here
		this.connected = true;
	}

	this.sendMessage = function(message){
		// FIXME: fill in message sending logic
	}

	this.deliver = function(message){
		if(message["timestamp"]){
			this.lastTimestamp = message.timestamp;
		}
		if(message["id"]){
			this.lastId = message.id;
		}
		if(	(message.channel.length > 5)&&
			(message.channel.substr(0, 5) == "/meta")){
			// check for various meta topic actions that we need to respond to
			// switch(message.channel){
			// 	case "/meta/connect":
			//		// FIXME: fill in logic here
			//		break;
			//	// case ...: ...
			//	}
		}
	}

	this.disconnect = function(){
		if(!this.connected){ return; }
		// FIXME: fill in shutdown routine here
		this.connected = false;
	}
}
cometd.connectionTypes.register("blah", cometd.blahTransport.check, cometd.blahTransport);
*/

cometd.iframeTransport = new function(){
	this.connected = false;
	this.connectionId = null;

	this.rcvNode = null;
	this.rcvNodeName = "";
	this.phonyForm = null;
	this.authToken = null;
	this.lastTimestamp = null;
	this.lastId = null;
	this.backlog = [];

	this.check = function(types, version, xdomain){
		return ((!xdomain)&&
				(!dojo.render.html.safari)&&
				(dojo.lang.inArray(types, "iframe")));
	}

	this.tunnelInit = function(){
		// we've gotten our initialization document back in the iframe, so
		// now open up a connection and start passing data!
		this.postToIframe({
			message: dojo.json.serialize([
				{
					channel:	"/meta/connect",
					clientId:	cometd.clientId,
					connectionType: "iframe"
					// FIXME: auth not passed here!
					// "authToken": this.authToken
				}
			])
		});
	}

	this.tunnelCollapse = function(){
		if(this.connected){
			// try to restart the tunnel
			this.connected = false;

			this.postToIframe({
				message: dojo.json.serialize([
					{
						channel:	"/meta/reconnect",
						clientId:	cometd.clientId,
						connectionId:	this.connectionId,
						timestamp:	this.lastTimestamp,
						id:			this.lastId
						// FIXME: no authToken provision!
					}
				])
			});
		}
	}

	this.deliver = function(message){
		// handle delivery details that this transport particularly cares
		// about. Most functions of should be handled by the main cometd object
		// with only transport-specific details and state being tracked here.
		if(message["timestamp"]){
			this.lastTimestamp = message.timestamp;
		}
		if(message["id"]){
			this.lastId = message.id;
		}
		// check to see if we got a /meta channel message that we care about
		if(	(message.channel.length > 5)&&
			(message.channel.substr(0, 5) == "/meta")){
			// check for various meta topic actions that we need to respond to
			switch(message.channel){
				case "/meta/connect":
					if(!message.successful){
						dojo.debug("cometd connection error:", message.error);
						return;
					}
					this.connectionId = message.connectionId;
					this.connected = true;
					this.processBacklog();
					break;
				case "/meta/reconnect":
					if(!message.successful){
						dojo.debug("cometd reconnection error:", message.error);
						return;
					}
					this.connected = true;
					break;
				case "/meta/subscribe":
					if(!message.successful){
						dojo.debug("cometd subscription error for channel", message.channel, ":", message.error);
						return;
					}
					// this.subscribed(message.channel);
					dojo.debug(message.channel);
					break;
			}
		}
	}

	this.widenDomain = function(domainStr){
		// allow us to make reqests to the TLD
		var cd = domainStr||document.domain;
		if(cd.indexOf(".")==-1){ return; } // probably file:/// or localhost
		var dps = cd.split(".");
		if(dps.length<=2){ return; } // probably file:/// or an RFC 1918 address
		dps = dps.slice(dps.length-2);
		document.domain = dps.join(".");
		return document.domain;
	}

	this.postToIframe = function(content, url){
		if(!this.phonyForm){
			if(dojo.render.html.ie){
				this.phonyForm = document.createElement("<form enctype='application/x-www-form-urlencoded' method='POST' style='display: none;'>");
				dojo.body().appendChild(this.phonyForm);
			}else{
				this.phonyForm = document.createElement("form");
				this.phonyForm.style.display = "none"; // FIXME: will this still work?
				dojo.body().appendChild(this.phonyForm);
				this.phonyForm.enctype = "application/x-www-form-urlencoded";
				this.phonyForm.method = "POST";
			}
		}

		this.phonyForm.action = url||cometd.url;
		this.phonyForm.target = this.rcvNodeName;
		this.phonyForm.setAttribute("target", this.rcvNodeName);

		while(this.phonyForm.firstChild){
			this.phonyForm.removeChild(this.phonyForm.firstChild);
		}

		for(var x in content){
			var tn;
			if(dojo.render.html.ie){
				tn = document.createElement("<input type='hidden' name='"+x+"' value='"+content[x]+"'>");
				this.phonyForm.appendChild(tn);
			}else{
				tn = document.createElement("input");
				this.phonyForm.appendChild(tn);
				tn.type = "hidden";
				tn.name = x;
				tn.value = content[x];
			}
		}
		this.phonyForm.submit();
	}

	this.processBacklog = function(){
		while(this.backlog.length > 0){
			this.sendMessage(this.backlog.shift(), true);
		}
	}

	this.sendMessage = function(message, bypassBacklog){
		// FIXME: what about auth fields?
		if((bypassBacklog)||(this.connected)){
			message.connectionId = this.connectionId;
			message.clientId = cometd.clientId;
			var bindArgs = {
				url: cometd.url||djConfig["cometdRoot"],
				method: "POST",
				mimetype: "text/json",
				// FIXME: we should be able to do better than this given that we're sending an array!
				content: { message: dojo.json.serialize([ message ]) }
			};
			return dojo.io.bind(bindArgs);
		}else{
			this.backlog.push(message);
		}
	}

	this.startup = function(handshakeData){
		dojo.debug("startup!");
		dojo.debug(dojo.json.serialize(handshakeData));

		if(this.connected){ return; }

		// this.widenDomain();

		// NOTE: we require the server to cooperate by hosting
		// cometdInit.html at the designated endpoint
		this.rcvNodeName = "cometdRcv_"+cometd._getRandStr();
		// the "forever frame" approach

		var initUrl = cometd.url+"/?tunnelInit=iframe"; // &domain="+document.domain;
		if(false && dojo.render.html.ie){ // FIXME: DISALBED FOR NOW
			// use the "htmlfile hack" to prevent the background click junk
			this.rcvNode = new ActiveXObject("htmlfile");
			this.rcvNode.open();
			this.rcvNode.write("<html>");
			this.rcvNode.write("<script>document.domain = '"+document.domain+"'");
			this.rcvNode.write("</html>");
			this.rcvNode.close();

			var ifrDiv = this.rcvNode.createElement("div");
			this.rcvNode.appendChild(ifrDiv);
			this.rcvNode.parentWindow.dojo = dojo;
			ifrDiv.innerHTML = "<iframe src='"+initUrl+"'></iframe>"
		}else{
			this.rcvNode = dojo.io.createIFrame(this.rcvNodeName, "", initUrl);
			// dojo.io.setIFrameSrc(this.rcvNode, initUrl);
			// we're still waiting on the iframe to call back up to use and
			// advertise that it's been initialized via tunnelInit
		}
	}
}

cometd.mimeReplaceTransport = new function(){
	this.connected = false;
	this.connectionId = null;
	this.xhr = null;

	this.authToken = null;
	this.lastTimestamp = null;
	this.lastId = null;
	this.backlog = [];

	this.check = function(types, version, xdomain){
		return ((!xdomain)&&
				(dojo.render.html.mozilla)&& // seems only Moz really supports this right now = (
				(dojo.lang.inArray(types, "mime-message-block")));
	}

	this.tunnelInit = function(){
		if(this.connected){ return; }
		// FIXME: open up the connection here
		this.openTunnelWith({
			message: dojo.json.serialize([
				{
					channel:	"/meta/connect",
					clientId:	cometd.clientId,
					connectionType: "mime-message-block"
					// FIXME: auth not passed here!
					// "authToken": this.authToken
				}
			])
		});
		this.connected = true;
	}

	this.tunnelCollapse = function(){
		if(this.connected){
			// try to restart the tunnel
			this.connected = false;
			this.openTunnelWith({
				message: dojo.json.serialize([
					{
						channel:	"/meta/reconnect",
						clientId:	cometd.clientId,
						connectionId:	this.connectionId,
						timestamp:	this.lastTimestamp,
						id:			this.lastId
						// FIXME: no authToken provision!
					}
				])
			});
		}
	}

	this.deliver = cometd.iframeTransport.deliver;
	// the logic appears to be the same

	this.handleOnLoad = function(resp){
		cometd.deliver(dojo.json.evalJson(this.xhr.responseText));
	}

	this.openTunnelWith = function(content, url){
		// set up the XHR object and register the multipart callbacks
		this.xhr = dojo.hostenv.getXmlhttpObject();
		this.xhr.multipart = true; // FIXME: do Opera and Safari support this flag?
		if(dojo.render.html.mozilla){
			this.xhr.addEventListener("load", dojo.lang.hitch(this, "handleOnLoad"), false);
		}else if(dojo.render.html.safari){
			// Blah. WebKit doesn't actually populate responseText and/or responseXML. Useless.
			dojo.debug("Webkit is broken with multipart responses over XHR = (");
			this.xhr.onreadystatechange = dojo.lang.hitch(this, "handleOnLoad");
		}else{
			this.xhr.onload = dojo.lang.hitch(this, "handleOnLoad");
		}
		this.xhr.open("POST", (url||cometd.url), true); // async post
		this.xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		dojo.debug(dojo.json.serialize(content));
		this.xhr.send(dojo.io.argsFromMap(content, "utf8"));
	}

	this.processBacklog = function(){
		while(this.backlog.length > 0){
			this.sendMessage(this.backlog.shift(), true);
		}
	}

	this.sendMessage = function(message, bypassBacklog){
		// FIXME: what about auth fields?
		if((bypassBacklog)||(this.connected)){
			message.connectionId = this.connectionId;
			message.clientId = cometd.clientId;
			var bindArgs = {
				url: cometd.url||djConfig["cometdRoot"],
				method: "POST",
				mimetype: "text/json",
				content: { message: dojo.json.serialize([ message ]) }
			};
			return dojo.io.bind(bindArgs);
		}else{
			this.backlog.push(message);
		}
	}

	this.startup = function(handshakeData){
		dojo.debugShallow(handshakeData);
		if(this.connected){ return; }
		this.tunnelInit();
	}
}

cometd.longPollTransport = new function(){
	this.connected = false;
	this.connectionId = null;

	this.authToken = null;
	this.lastTimestamp = null;
	this.lastId = null;
	this.backlog = [];

	this.check = function(types, version, xdomain){
		return ((!xdomain)&&(dojo.lang.inArray(types, "long-polling")));
	}

	this.tunnelInit = function(){
		if(this.connected){ return; }
		// FIXME: open up the connection here
		this.openTunnelWith({
			message: dojo.json.serialize([
				{
					channel:	"/meta/connect",
					clientId:	cometd.clientId,
					connectionType: "long-polling"
					// FIXME: auth not passed here!
					// "authToken": this.authToken
				}
			])
		});
		this.connected = true;
	}

	this.tunnelCollapse = function(){
		if(!this.connected){
			// try to restart the tunnel
			this.connected = false;
			dojo.debug("clientId:", cometd.clientId);
			this.openTunnelWith({
				message: dojo.json.serialize([
					{
						channel:	"/meta/reconnect",
						connectionType: "long-polling",
						clientId:	cometd.clientId,
						connectionId:	this.connectionId,
						timestamp:	this.lastTimestamp,
						id:			this.lastId
						// FIXME: no authToken provision!
					}
				])
			});
		}
	}

	this.deliver = cometd.iframeTransport.deliver;
	// the logic appears to be the same

	this.openTunnelWith = function(content, url){
		dojo.io.bind({
			url: (url||cometd.url),
			method: "post",
			content: content,
			mimetype: "text/json",
			load: dojo.lang.hitch(this, function(type, data, evt, args){
				// dojo.debug(evt.responseText);
				cometd.deliver(data);
				this.connected = false;
				this.tunnelCollapse();
			}),
			error: function(){ dojo.debug("tunnel opening failed"); }
		});
		this.connected = true;
	}

	this.processBacklog = function(){
		while(this.backlog.length > 0){
			this.sendMessage(this.backlog.shift(), true);
		}
	}

	this.sendMessage = function(message, bypassBacklog){
		// FIXME: what about auth fields?
		if((bypassBacklog)||(this.connected)){
			message.connectionId = this.connectionId;
			message.clientId = cometd.clientId;
			var bindArgs = {
				url: cometd.url||djConfig["cometdRoot"],
				method: "post",
				mimetype: "text/json",
				content: { message: dojo.json.serialize([ message ]) }
			};
			return dojo.io.bind(bindArgs);
		}else{
			this.backlog.push(message);
		}
	}

	this.startup = function(handshakeData){
		if(this.connected){ return; }
		this.tunnelInit();
	}
}

cometd.callbackPollTransport = new function(){
	this.connected = false;
	this.connectionId = null;

	this.authToken = null;
	this.lastTimestamp = null;
	this.lastId = null;
	this.backlog = [];

	this.check = function(types, version, xdomain){
		// we handle x-domain!
		return dojo.lang.inArray(types, "callback-polling");
	}

	this.tunnelInit = function(){
		if(this.connected){ return; }
		// FIXME: open up the connection here
		this.openTunnelWith({
			message: dojo.json.serialize([
				{
					channel:	"/meta/connect",
					clientId:	cometd.clientId,
					connectionType: "callback-polling"
					// FIXME: auth not passed here!
					// "authToken": this.authToken
				}
			])
		});
		this.connected = true;
	}

	this.tunnelCollapse = function(){
		if(!this.connected){
			// try to restart the tunnel
			this.connected = false;
			this.openTunnelWith({
				message: dojo.json.serialize([
					{
						channel:	"/meta/reconnect",
						connectionType: "long-polling",
						clientId:	cometd.clientId,
						connectionId:	this.connectionId,
						timestamp:	this.lastTimestamp,
						id:			this.lastId
						// FIXME: no authToken provision!
					}
				])
			});
		}
	}

	this.deliver = cometd.iframeTransport.deliver;
	// the logic appears to be the same

	this.openTunnelWith = function(content, url){
		// create a <script> element to generate the request
		var req = dojo.io.bind({
			url: (url||cometd.url),
			content: content,
			mimetype: "text/json",
			transport: "ScriptSrcTransport",
			jsonParamName: "jsonp",
			load: dojo.lang.hitch(this, function(type, data, evt, args){
				dojo.debug(dojo.json.serialize(data));
				cometd.deliver(data);
				this.connected = false;
				this.tunnelCollapse();
			}),
			error: function(){ dojo.debug("tunnel opening failed"); }
		});
		this.connected = true;
	}

	this.processBacklog = function(){
		while(this.backlog.length > 0){
			this.sendMessage(this.backlog.shift(), true);
		}
	}

	this.sendMessage = function(message, bypassBacklog){
		// FIXME: what about auth fields?
		if((bypassBacklog)||(this.connected)){
			message.connectionId = this.connectionId;
			message.clientId = cometd.clientId;
			var bindArgs = {
				url: cometd.url||djConfig["cometdRoot"],
				mimetype: "text/json",
				transport: "ScriptSrcTransport",
				jsonParamName: "jsonp",
				content: { message: dojo.json.serialize([ message ]) }
			};
			return dojo.io.bind(bindArgs);
		}else{
			this.backlog.push(message);
		}
	}

	this.startup = function(handshakeData){
		if(this.connected){ return; }
		this.tunnelInit();
	}
}

cometd.connectionTypes.register("mime-message-block", cometd.mimeReplaceTransport.check, cometd.mimeReplaceTransport);
cometd.connectionTypes.register("long-polling", cometd.longPollTransport.check, cometd.longPollTransport);
cometd.connectionTypes.register("callback-polling", cometd.callbackPollTransport.check, cometd.callbackPollTransport);
cometd.connectionTypes.register("iframe", cometd.iframeTransport.check, cometd.iframeTransport);

// FIXME: need to implement fallback-polling, IE XML block

dojo.io.cometd = cometd;
