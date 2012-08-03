/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.require("dojo.event.*");
dojo.require("dojo.io.BrowserIO");
dojo.provide("dojo.io.RepubsubIO");
dojo.io.repubsubTranport = new function () {
	var rps = dojo.io.repubsub;
	this.canHandle = function (kwArgs) {
		if ((kwArgs["mimetype"] == "text/javascript") && (kwArgs["method"] == "repubsub")) {
			return true;
		}
		return false;
	};
	this.bind = function (kwArgs) {
		if (!rps.isInitialized) {
			rps.init();
		}
		if (!rps.topics[kwArgs.url]) {
			kwArgs.rpsLoad = function (evt) {
				kwArgs.load("load", evt);
			};
			rps.subscribe(kwArgs.url, kwArgs, "rpsLoad");
		}
		if (kwArgs["content"]) {
			var cEvt = dojo.io.repubsubEvent.initFromProperties(kwArgs.content);
			rps.publish(kwArgs.url, cEvt);
		}
	};
	dojo.io.transports.addTransport("repubsubTranport");
};
dojo.io.repubsub = new function () {
	this.initDoc = "init.html";
	this.isInitialized = false;
	this.subscriptionBacklog = [];
	this.debug = true;
	this.rcvNodeName = null;
	this.sndNodeName = null;
	this.rcvNode = null;
	this.sndNode = null;
	this.canRcv = false;
	this.canSnd = false;
	this.canLog = false;
	this.sndTimer = null;
	this.windowRef = window;
	this.backlog = [];
	this.tunnelInitCount = 0;
	this.tunnelFrameKey = "tunnel_frame";
	this.serverBaseURL = location.protocol + "//" + location.host + location.pathname;
	this.logBacklog = [];
	this.getRandStr = function () {
		return Math.random().toString().substring(2, 10);
	};
	this.userid = "guest";
	this.tunnelID = this.getRandStr();
	this.attachPathList = [];
	this.topics = [];
	this.parseGetStr = function () {
		var baseUrl = document.location.toString();
		var params = baseUrl.split("?", 2);
		if (params.length > 1) {
			var paramStr = params[1];
			var pairs = paramStr.split("&");
			var opts = [];
			for (var x in pairs) {
				var sp = pairs[x].split("=");
				try {
					opts[sp[0]] = eval(sp[1]);
				}
				catch (e) {
					opts[sp[0]] = sp[1];
				}
			}
			return opts;
		} else {
			return [];
		}
	};
	var getOpts = this.parseGetStr();
	for (var x in getOpts) {
		this[x] = getOpts[x];
	}
	if (!this["tunnelURI"]) {
		this.tunnelURI = ["/who/", escape(this.userid), "/s/", this.getRandStr(), "/kn_journal"].join("");
	}
	if (window["repubsubOpts"] || window["rpsOpts"]) {
		var optObj = window["repubsubOpts"] || window["rpsOpts"];
		for (var x in optObj) {
			this[x] = optObj[x];
		}
	}
	this.tunnelCloseCallback = function () {
		dojo.io.setIFrameSrc(this.rcvNode, this.initDoc + "?callback=repubsub.rcvNodeReady&domain=" + document.domain);
	};
	this.receiveEventFromTunnel = function (evt, srcWindow) {
		if (!evt["elements"]) {
			this.log("bailing! event received without elements!", "error");
			return;
		}
		var e = {};
		for (var i = 0; i < evt.elements.length; i++) {
			var ee = evt.elements[i];
			e[ee.name || ee.nameU] = (ee.value || ee.valueU);
			this.log("[event]: " + (ee.name || ee.nameU) + ": " + e[ee.name || ee.nameU]);
		}
		this.dispatch(e);
	};
	this.widenDomain = function (domainStr) {
		var cd = domainStr || document.domain;
		if (cd.indexOf(".") == -1) {
			return;
		}
		var dps = cd.split(".");
		if (dps.length <= 2) {
			return;
		}
		dps = dps.slice(dps.length - 2);
		document.domain = dps.join(".");
	};
	this.parseCookie = function () {
		var cs = document.cookie;
		var keypairs = cs.split(";");
		for (var x = 0; x < keypairs.length; x++) {
			keypairs[x] = keypairs[x].split("=");
			if (x != keypairs.length - 1) {
				cs += ";";
			}
		}
		return keypairs;
	};
	this.setCookie = function (keypairs, clobber) {
		if ((clobber) && (clobber == true)) {
			document.cookie = "";
		}
		var cs = "";
		for (var x = 0; x < keypairs.length; x++) {
			cs += keypairs[x][0] + "=" + keypairs[x][1];
			if (x != keypairs.length - 1) {
				cs += ";";
			}
		}
		document.cookie = cs;
	};
	this.log = function (str, lvl) {
		if (!this.debug) {
			return;
		}
		while (this.logBacklog.length > 0) {
			if (!this.canLog) {
				break;
			}
			var blo = this.logBacklog.shift();
			this.writeLog("[" + blo[0] + "]: " + blo[1], blo[2]);
		}
		this.writeLog(str, lvl);
	};
	this.writeLog = function (str, lvl) {
		dojo.debug(((new Date()).toLocaleTimeString()) + ": " + str);
	};
	this.init = function () {
		this.widenDomain();
		this.openTunnel();
		this.isInitialized = true;
		while (this.subscriptionBacklog.length) {
			this.subscribe.apply(this, this.subscriptionBacklog.shift());
		}
	};
	this.clobber = function () {
		if (this.rcvNode) {
			this.setCookie([[this.tunnelFrameKey, "closed"], ["path", "/"]], false);
		}
	};
	this.openTunnel = function () {
		this.rcvNodeName = "rcvIFrame_" + this.getRandStr();
		this.setCookie([[this.tunnelFrameKey, this.rcvNodeName], ["path", "/"]], false);
		this.rcvNode = dojo.io.createIFrame(this.rcvNodeName);
		dojo.io.setIFrameSrc(this.rcvNode, this.initDoc + "?callback=repubsub.rcvNodeReady&domain=" + document.domain);
		this.sndNodeName = "sndIFrame_" + this.getRandStr();
		this.sndNode = dojo.io.createIFrame(this.sndNodeName);
		dojo.io.setIFrameSrc(this.sndNode, this.initDoc + "?callback=repubsub.sndNodeReady&domain=" + document.domain);
	};
	this.rcvNodeReady = function () {
		var statusURI = [this.tunnelURI, "/kn_status/", this.getRandStr(), "_", String(this.tunnelInitCount++)].join("");
		this.log("rcvNodeReady");
		var initURIArr = [this.serverBaseURL, "/kn?kn_from=", escape(this.tunnelURI), "&kn_id=", escape(this.tunnelID), "&kn_status_from=", escape(statusURI)];
		dojo.io.setIFrameSrc(this.rcvNode, initURIArr.join(""));
		this.subscribe(statusURI, this, "statusListener", true);
		this.log(initURIArr.join(""));
	};
	this.sndNodeReady = function () {
		this.canSnd = true;
		this.log("sndNodeReady");
		this.log(this.backlog.length);
		if (this.backlog.length > 0) {
			this.dequeueEvent();
		}
	};
	this.statusListener = function (evt) {
		this.log("status listener called");
		this.log(evt.status, "info");
	};
	this.dispatch = function (evt) {
		if (evt["to"] || evt["kn_routed_from"]) {
			var rf = evt["to"] || evt["kn_routed_from"];
			var topic = rf.split(this.serverBaseURL, 2)[1];
			if (!topic) {
				topic = rf;
			}
			this.log("[topic] " + topic);
			if (topic.length > 3) {
				if (topic.slice(0, 3) == "/kn") {
					topic = topic.slice(3);
				}
			}
			if (this.attachPathList[topic]) {
				this.attachPathList[topic](evt);
			}
		}
	};
	this.subscribe = function (topic, toObj, toFunc, dontTellServer) {
		if (!this.isInitialized) {
			this.subscriptionBacklog.push([topic, toObj, toFunc, dontTellServer]);
			return;
		}
		if (!this.attachPathList[topic]) {
			this.attachPathList[topic] = function () {
				return true;
			};
			this.log("subscribing to: " + topic);
			this.topics.push(topic);
		}
		var revt = new dojo.io.repubsubEvent(this.tunnelURI, topic, "route");
		var rstr = [this.serverBaseURL + "/kn", revt.toGetString()].join("");
		dojo.event.kwConnect({once:true, srcObj:this.attachPathList, srcFunc:topic, adviceObj:toObj, adviceFunc:toFunc});
		if (!this.rcvNode) {
		}
		if (dontTellServer) {
			return;
		}
		this.log("sending subscription to: " + topic);
		this.sendTopicSubToServer(topic, rstr);
	};
	this.sendTopicSubToServer = function (topic, str) {
		if (!this.attachPathList[topic]["subscriptions"]) {
			this.enqueueEventStr(str);
			this.attachPathList[topic].subscriptions = 0;
		}
		this.attachPathList[topic].subscriptions++;
	};
	this.unSubscribe = function (topic, toObj, toFunc) {
		dojo.event.kwDisconnect({srcObj:this.attachPathList, srcFunc:topic, adviceObj:toObj, adviceFunc:toFunc});
	};
	this.publish = function (topic, event) {
		var evt = dojo.io.repubsubEvent.initFromProperties(event);
		evt.to = topic;
		var evtURLParts = [];
		evtURLParts.push(this.serverBaseURL + "/kn");
		evtURLParts.push(evt.toGetString());
		this.enqueueEventStr(evtURLParts.join(""));
	};
	this.enqueueEventStr = function (evtStr) {
		this.log("enqueueEventStr");
		this.backlog.push(evtStr);
		this.dequeueEvent();
	};
	this.dequeueEvent = function (force) {
		this.log("dequeueEvent");
		if (this.backlog.length <= 0) {
			return;
		}
		if ((this.canSnd) || (force)) {
			dojo.io.setIFrameSrc(this.sndNode, this.backlog.shift() + "&callback=repubsub.sndNodeReady");
			this.canSnd = false;
		} else {
			this.log("sndNode not available yet!", "debug");
		}
	};
};
dojo.io.repubsubEvent = function (to, from, method, id, routeURI, payload, dispname, uid) {
	this.to = to;
	this.from = from;
	this.method = method || "route";
	this.id = id || repubsub.getRandStr();
	this.uri = routeURI;
	this.displayname = dispname || repubsub.displayname;
	this.userid = uid || repubsub.userid;
	this.payload = payload || "";
	this.flushChars = 4096;
	this.initFromProperties = function (evt) {
		if (evt.constructor = dojo.io.repubsubEvent) {
			for (var x in evt) {
				this[x] = evt[x];
			}
		} else {
			for (var x in evt) {
				if (typeof this.forwardPropertiesMap[x] == "string") {
					this[this.forwardPropertiesMap[x]] = evt[x];
				} else {
					this[x] = evt[x];
				}
			}
		}
	};
	this.toGetString = function (noQmark) {
		var qs = [((noQmark) ? "" : "?")];
		for (var x = 0; x < this.properties.length; x++) {
			var tp = this.properties[x];
			if (this[tp[0]]) {
				qs.push(tp[1] + "=" + encodeURIComponent(String(this[tp[0]])));
			}
		}
		return qs.join("&");
	};
};
dojo.io.repubsubEvent.prototype.properties = [["from", "kn_from"], ["to", "kn_to"], ["method", "do_method"], ["id", "kn_id"], ["uri", "kn_uri"], ["displayname", "kn_displayname"], ["userid", "kn_userid"], ["payload", "kn_payload"], ["flushChars", "kn_response_flush"], ["responseFormat", "kn_response_format"]];
dojo.io.repubsubEvent.prototype.forwardPropertiesMap = {};
dojo.io.repubsubEvent.prototype.reversePropertiesMap = {};
for (var x = 0; x < dojo.io.repubsubEvent.prototype.properties.length; x++) {
	var tp = dojo.io.repubsubEvent.prototype.properties[x];
	dojo.io.repubsubEvent.prototype.reversePropertiesMap[tp[0]] = tp[1];
	dojo.io.repubsubEvent.prototype.forwardPropertiesMap[tp[1]] = tp[0];
}
dojo.io.repubsubEvent.initFromProperties = function (evt) {
	var eventObj = new dojo.io.repubsubEvent();
	eventObj.initFromProperties(evt);
	return eventObj;
};

