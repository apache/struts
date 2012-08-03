/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.require("dojo.event.common");
dojo.provide("dojo.event.topic");
dojo.event.topic = new function () {
	this.topics = {};
	this.getTopic = function (topic) {
		if (!this.topics[topic]) {
			this.topics[topic] = new this.TopicImpl(topic);
		}
		return this.topics[topic];
	};
	this.registerPublisher = function (topic, obj, funcName) {
		var topic = this.getTopic(topic);
		topic.registerPublisher(obj, funcName);
	};
	this.subscribe = function (topic, obj, funcName) {
		var topic = this.getTopic(topic);
		topic.subscribe(obj, funcName);
	};
	this.unsubscribe = function (topic, obj, funcName) {
		var topic = this.getTopic(topic);
		topic.unsubscribe(obj, funcName);
	};
	this.destroy = function (topic) {
		this.getTopic(topic).destroy();
		delete this.topics[topic];
	};
	this.publishApply = function (topic, args) {
		var topic = this.getTopic(topic);
		topic.sendMessage.apply(topic, args);
	};
	this.publish = function (topic, message) {
		var topic = this.getTopic(topic);
		var args = [];
		for (var x = 1; x < arguments.length; x++) {
			args.push(arguments[x]);
		}
		topic.sendMessage.apply(topic, args);
	};
};
dojo.event.topic.TopicImpl = function (topicName) {
	this.topicName = topicName;
	this.subscribe = function (listenerObject, listenerMethod) {
		var tf = listenerMethod || listenerObject;
		var to = (!listenerMethod) ? dj_global : listenerObject;
		return dojo.event.kwConnect({srcObj:this, srcFunc:"sendMessage", adviceObj:to, adviceFunc:tf});
	};
	this.unsubscribe = function (listenerObject, listenerMethod) {
		var tf = (!listenerMethod) ? listenerObject : listenerMethod;
		var to = (!listenerMethod) ? null : listenerObject;
		return dojo.event.kwDisconnect({srcObj:this, srcFunc:"sendMessage", adviceObj:to, adviceFunc:tf});
	};
	this._getJoinPoint = function () {
		return dojo.event.MethodJoinPoint.getForMethod(this, "sendMessage");
	};
	this.setSquelch = function (shouldSquelch) {
		this._getJoinPoint().squelch = shouldSquelch;
	};
	this.destroy = function () {
		this._getJoinPoint().disconnect();
	};
	this.registerPublisher = function (publisherObject, publisherMethod) {
		dojo.event.connect(publisherObject, publisherMethod, this, "sendMessage");
	};
	this.sendMessage = function (message) {
	};
};

