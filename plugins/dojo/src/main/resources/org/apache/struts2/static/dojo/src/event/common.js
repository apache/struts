/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.event.common");
dojo.require("dojo.lang.array");
dojo.require("dojo.lang.extras");
dojo.require("dojo.lang.func");
dojo.event = new function () {
	this._canTimeout = dojo.lang.isFunction(dj_global["setTimeout"]) || dojo.lang.isAlien(dj_global["setTimeout"]);
	function interpolateArgs(args, searchForNames) {
		var dl = dojo.lang;
		var ao = {srcObj:dj_global, srcFunc:null, adviceObj:dj_global, adviceFunc:null, aroundObj:null, aroundFunc:null, adviceType:(args.length > 2) ? args[0] : "after", precedence:"last", once:false, delay:null, rate:0, adviceMsg:false, maxCalls:-1};
		switch (args.length) {
		  case 0:
			return;
		  case 1:
			return;
		  case 2:
			ao.srcFunc = args[0];
			ao.adviceFunc = args[1];
			break;
		  case 3:
			if ((dl.isObject(args[0])) && (dl.isString(args[1])) && (dl.isString(args[2]))) {
				ao.adviceType = "after";
				ao.srcObj = args[0];
				ao.srcFunc = args[1];
				ao.adviceFunc = args[2];
			} else {
				if ((dl.isString(args[1])) && (dl.isString(args[2]))) {
					ao.srcFunc = args[1];
					ao.adviceFunc = args[2];
				} else {
					if ((dl.isObject(args[0])) && (dl.isString(args[1])) && (dl.isFunction(args[2]))) {
						ao.adviceType = "after";
						ao.srcObj = args[0];
						ao.srcFunc = args[1];
						var tmpName = dl.nameAnonFunc(args[2], ao.adviceObj, searchForNames);
						ao.adviceFunc = tmpName;
					} else {
						if ((dl.isFunction(args[0])) && (dl.isObject(args[1])) && (dl.isString(args[2]))) {
							ao.adviceType = "after";
							ao.srcObj = dj_global;
							var tmpName = dl.nameAnonFunc(args[0], ao.srcObj, searchForNames);
							ao.srcFunc = tmpName;
							ao.adviceObj = args[1];
							ao.adviceFunc = args[2];
						}
					}
				}
			}
			break;
		  case 4:
			if ((dl.isObject(args[0])) && (dl.isObject(args[2]))) {
				ao.adviceType = "after";
				ao.srcObj = args[0];
				ao.srcFunc = args[1];
				ao.adviceObj = args[2];
				ao.adviceFunc = args[3];
			} else {
				if ((dl.isString(args[0])) && (dl.isString(args[1])) && (dl.isObject(args[2]))) {
					ao.adviceType = args[0];
					ao.srcObj = dj_global;
					ao.srcFunc = args[1];
					ao.adviceObj = args[2];
					ao.adviceFunc = args[3];
				} else {
					if ((dl.isString(args[0])) && (dl.isFunction(args[1])) && (dl.isObject(args[2]))) {
						ao.adviceType = args[0];
						ao.srcObj = dj_global;
						var tmpName = dl.nameAnonFunc(args[1], dj_global, searchForNames);
						ao.srcFunc = tmpName;
						ao.adviceObj = args[2];
						ao.adviceFunc = args[3];
					} else {
						if ((dl.isString(args[0])) && (dl.isObject(args[1])) && (dl.isString(args[2])) && (dl.isFunction(args[3]))) {
							ao.srcObj = args[1];
							ao.srcFunc = args[2];
							var tmpName = dl.nameAnonFunc(args[3], dj_global, searchForNames);
							ao.adviceObj = dj_global;
							ao.adviceFunc = tmpName;
						} else {
							if (dl.isObject(args[1])) {
								ao.srcObj = args[1];
								ao.srcFunc = args[2];
								ao.adviceObj = dj_global;
								ao.adviceFunc = args[3];
							} else {
								if (dl.isObject(args[2])) {
									ao.srcObj = dj_global;
									ao.srcFunc = args[1];
									ao.adviceObj = args[2];
									ao.adviceFunc = args[3];
								} else {
									ao.srcObj = ao.adviceObj = ao.aroundObj = dj_global;
									ao.srcFunc = args[1];
									ao.adviceFunc = args[2];
									ao.aroundFunc = args[3];
								}
							}
						}
					}
				}
			}
			break;
		  case 6:
			ao.srcObj = args[1];
			ao.srcFunc = args[2];
			ao.adviceObj = args[3];
			ao.adviceFunc = args[4];
			ao.aroundFunc = args[5];
			ao.aroundObj = dj_global;
			break;
		  default:
			ao.srcObj = args[1];
			ao.srcFunc = args[2];
			ao.adviceObj = args[3];
			ao.adviceFunc = args[4];
			ao.aroundObj = args[5];
			ao.aroundFunc = args[6];
			ao.once = args[7];
			ao.delay = args[8];
			ao.rate = args[9];
			ao.adviceMsg = args[10];
			ao.maxCalls = (!isNaN(parseInt(args[11]))) ? args[11] : -1;
			break;
		}
		if (dl.isFunction(ao.aroundFunc)) {
			var tmpName = dl.nameAnonFunc(ao.aroundFunc, ao.aroundObj, searchForNames);
			ao.aroundFunc = tmpName;
		}
		if (dl.isFunction(ao.srcFunc)) {
			ao.srcFunc = dl.getNameInObj(ao.srcObj, ao.srcFunc);
		}
		if (dl.isFunction(ao.adviceFunc)) {
			ao.adviceFunc = dl.getNameInObj(ao.adviceObj, ao.adviceFunc);
		}
		if ((ao.aroundObj) && (dl.isFunction(ao.aroundFunc))) {
			ao.aroundFunc = dl.getNameInObj(ao.aroundObj, ao.aroundFunc);
		}
		if (!ao.srcObj) {
			dojo.raise("bad srcObj for srcFunc: " + ao.srcFunc);
		}
		if (!ao.adviceObj) {
			dojo.raise("bad adviceObj for adviceFunc: " + ao.adviceFunc);
		}
		if (!ao.adviceFunc) {
			dojo.debug("bad adviceFunc for srcFunc: " + ao.srcFunc);
			dojo.debugShallow(ao);
		}
		return ao;
	}
	this.connect = function () {
		if (arguments.length == 1) {
			var ao = arguments[0];
		} else {
			var ao = interpolateArgs(arguments, true);
		}
		if (dojo.lang.isString(ao.srcFunc) && (ao.srcFunc.toLowerCase() == "onkey")) {
			if (dojo.render.html.ie) {
				ao.srcFunc = "onkeydown";
				this.connect(ao);
			}
			ao.srcFunc = "onkeypress";
		}
		if (dojo.lang.isArray(ao.srcObj) && ao.srcObj != "") {
			var tmpAO = {};
			for (var x in ao) {
				tmpAO[x] = ao[x];
			}
			var mjps = [];
			dojo.lang.forEach(ao.srcObj, function (src) {
				if ((dojo.render.html.capable) && (dojo.lang.isString(src))) {
					src = dojo.byId(src);
				}
				tmpAO.srcObj = src;
				mjps.push(dojo.event.connect.call(dojo.event, tmpAO));
			});
			return mjps;
		}
		var mjp = dojo.event.MethodJoinPoint.getForMethod(ao.srcObj, ao.srcFunc);
		if (ao.adviceFunc) {
			var mjp2 = dojo.event.MethodJoinPoint.getForMethod(ao.adviceObj, ao.adviceFunc);
		}
		mjp.kwAddAdvice(ao);
		return mjp;
	};
	this.log = function (a1, a2) {
		var kwArgs;
		if ((arguments.length == 1) && (typeof a1 == "object")) {
			kwArgs = a1;
		} else {
			kwArgs = {srcObj:a1, srcFunc:a2};
		}
		kwArgs.adviceFunc = function () {
			var argsStr = [];
			for (var x = 0; x < arguments.length; x++) {
				argsStr.push(arguments[x]);
			}
			dojo.debug("(" + kwArgs.srcObj + ")." + kwArgs.srcFunc, ":", argsStr.join(", "));
		};
		this.kwConnect(kwArgs);
	};
	this.connectBefore = function () {
		var args = ["before"];
		for (var i = 0; i < arguments.length; i++) {
			args.push(arguments[i]);
		}
		return this.connect.apply(this, args);
	};
	this.connectAround = function () {
		var args = ["around"];
		for (var i = 0; i < arguments.length; i++) {
			args.push(arguments[i]);
		}
		return this.connect.apply(this, args);
	};
	this.connectOnce = function () {
		var ao = interpolateArgs(arguments, true);
		ao.once = true;
		return this.connect(ao);
	};
	this.connectRunOnce = function () {
		var ao = interpolateArgs(arguments, true);
		ao.maxCalls = 1;
		return this.connect(ao);
	};
	this._kwConnectImpl = function (kwArgs, disconnect) {
		var fn = (disconnect) ? "disconnect" : "connect";
		if (typeof kwArgs["srcFunc"] == "function") {
			kwArgs.srcObj = kwArgs["srcObj"] || dj_global;
			var tmpName = dojo.lang.nameAnonFunc(kwArgs.srcFunc, kwArgs.srcObj, true);
			kwArgs.srcFunc = tmpName;
		}
		if (typeof kwArgs["adviceFunc"] == "function") {
			kwArgs.adviceObj = kwArgs["adviceObj"] || dj_global;
			var tmpName = dojo.lang.nameAnonFunc(kwArgs.adviceFunc, kwArgs.adviceObj, true);
			kwArgs.adviceFunc = tmpName;
		}
		kwArgs.srcObj = kwArgs["srcObj"] || dj_global;
		kwArgs.adviceObj = kwArgs["adviceObj"] || kwArgs["targetObj"] || dj_global;
		kwArgs.adviceFunc = kwArgs["adviceFunc"] || kwArgs["targetFunc"];
		return dojo.event[fn](kwArgs);
	};
	this.kwConnect = function (kwArgs) {
		return this._kwConnectImpl(kwArgs, false);
	};
	this.disconnect = function () {
		if (arguments.length == 1) {
			var ao = arguments[0];
		} else {
			var ao = interpolateArgs(arguments, true);
		}
		if (!ao.adviceFunc) {
			return;
		}
		if (dojo.lang.isString(ao.srcFunc) && (ao.srcFunc.toLowerCase() == "onkey")) {
			if (dojo.render.html.ie) {
				ao.srcFunc = "onkeydown";
				this.disconnect(ao);
			}
			ao.srcFunc = "onkeypress";
		}
		if (!ao.srcObj[ao.srcFunc]) {
			return null;
		}
		var mjp = dojo.event.MethodJoinPoint.getForMethod(ao.srcObj, ao.srcFunc, true);
		mjp.removeAdvice(ao.adviceObj, ao.adviceFunc, ao.adviceType, ao.once);
		return mjp;
	};
	this.kwDisconnect = function (kwArgs) {
		return this._kwConnectImpl(kwArgs, true);
	};
};
dojo.event.MethodInvocation = function (join_point, obj, args) {
	this.jp_ = join_point;
	this.object = obj;
	this.args = [];
	for (var x = 0; x < args.length; x++) {
		this.args[x] = args[x];
	}
	this.around_index = -1;
};
dojo.event.MethodInvocation.prototype.proceed = function () {
	this.around_index++;
	if (this.around_index >= this.jp_.around.length) {
		return this.jp_.object[this.jp_.methodname].apply(this.jp_.object, this.args);
	} else {
		var ti = this.jp_.around[this.around_index];
		var mobj = ti[0] || dj_global;
		var meth = ti[1];
		return mobj[meth].call(mobj, this);
	}
};
dojo.event.MethodJoinPoint = function (obj, funcName) {
	this.object = obj || dj_global;
	this.methodname = funcName;
	this.methodfunc = this.object[funcName];
	this.squelch = false;
};
dojo.event.MethodJoinPoint.getForMethod = function (obj, funcName) {
	if (!obj) {
		obj = dj_global;
	}
	var ofn = obj[funcName];
	if (!ofn) {
		ofn = obj[funcName] = function () {
		};
		if (!obj[funcName]) {
			dojo.raise("Cannot set do-nothing method on that object " + funcName);
		}
	} else {
		if ((typeof ofn != "function") && (!dojo.lang.isFunction(ofn)) && (!dojo.lang.isAlien(ofn))) {
			return null;
		}
	}
	var jpname = funcName + "$joinpoint";
	var jpfuncname = funcName + "$joinpoint$method";
	var joinpoint = obj[jpname];
	if (!joinpoint) {
		var isNode = false;
		if (dojo.event["browser"]) {
			if ((obj["attachEvent"]) || (obj["nodeType"]) || (obj["addEventListener"])) {
				isNode = true;
				dojo.event.browser.addClobberNodeAttrs(obj, [jpname, jpfuncname, funcName]);
			}
		}
		var origArity = ofn.length;
		obj[jpfuncname] = ofn;
		joinpoint = obj[jpname] = new dojo.event.MethodJoinPoint(obj, jpfuncname);
		if (!isNode) {
			obj[funcName] = function () {
				return joinpoint.run.apply(joinpoint, arguments);
			};
		} else {
			obj[funcName] = function () {
				var args = [];
				if (!arguments.length) {
					var evt = null;
					try {
						if (obj.ownerDocument) {
							evt = obj.ownerDocument.parentWindow.event;
						} else {
							if (obj.documentElement) {
								evt = obj.documentElement.ownerDocument.parentWindow.event;
							} else {
								if (obj.event) {
									evt = obj.event;
								} else {
									evt = window.event;
								}
							}
						}
					}
					catch (e) {
						evt = window.event;
					}
					if (evt) {
						args.push(dojo.event.browser.fixEvent(evt, this));
					}
				} else {
					for (var x = 0; x < arguments.length; x++) {
						if ((x == 0) && (dojo.event.browser.isEvent(arguments[x]))) {
							args.push(dojo.event.browser.fixEvent(arguments[x], this));
						} else {
							args.push(arguments[x]);
						}
					}
				}
				return joinpoint.run.apply(joinpoint, args);
			};
		}
		obj[funcName].__preJoinArity = origArity;
	}
	return joinpoint;
};
dojo.lang.extend(dojo.event.MethodJoinPoint, {squelch:false, unintercept:function () {
	this.object[this.methodname] = this.methodfunc;
	this.before = [];
	this.after = [];
	this.around = [];
}, disconnect:dojo.lang.forward("unintercept"), run:function () {
	var obj = this.object || dj_global;
	var args = arguments;
	var aargs = [];
	for (var x = 0; x < args.length; x++) {
		aargs[x] = args[x];
	}
	var unrollAdvice = function (marr) {
		if (!marr) {
			dojo.debug("Null argument to unrollAdvice()");
			return;
		}
		var callObj = marr[0] || dj_global;
		var callFunc = marr[1];
		if (!callObj[callFunc]) {
			dojo.raise("function \"" + callFunc + "\" does not exist on \"" + callObj + "\"");
		}
		var aroundObj = marr[2] || dj_global;
		var aroundFunc = marr[3];
		var msg = marr[6];
		var maxCount = marr[7];
		if (maxCount > -1) {
			if (maxCount == 0) {
				return;
			}
			marr[7]--;
		}
		var undef;
		var to = {args:[], jp_:this, object:obj, proceed:function () {
			return callObj[callFunc].apply(callObj, to.args);
		}};
		to.args = aargs;
		var delay = parseInt(marr[4]);
		var hasDelay = ((!isNaN(delay)) && (marr[4] !== null) && (typeof marr[4] != "undefined"));
		if (marr[5]) {
			var rate = parseInt(marr[5]);
			var cur = new Date();
			var timerSet = false;
			if ((marr["last"]) && ((cur - marr.last) <= rate)) {
				if (dojo.event._canTimeout) {
					if (marr["delayTimer"]) {
						clearTimeout(marr.delayTimer);
					}
					var tod = parseInt(rate * 2);
					var mcpy = dojo.lang.shallowCopy(marr);
					marr.delayTimer = setTimeout(function () {
						mcpy[5] = 0;
						unrollAdvice(mcpy);
					}, tod);
				}
				return;
			} else {
				marr.last = cur;
			}
		}
		if (aroundFunc) {
			aroundObj[aroundFunc].call(aroundObj, to);
		} else {
			if ((hasDelay) && ((dojo.render.html) || (dojo.render.svg))) {
				dj_global["setTimeout"](function () {
					if (msg) {
						callObj[callFunc].call(callObj, to);
					} else {
						callObj[callFunc].apply(callObj, args);
					}
				}, delay);
			} else {
				if (msg) {
					callObj[callFunc].call(callObj, to);
				} else {
					callObj[callFunc].apply(callObj, args);
				}
			}
		}
	};
	var unRollSquelch = function () {
		if (this.squelch) {
			try {
				return unrollAdvice.apply(this, arguments);
			}
			catch (e) {
				dojo.debug(e);
			}
		} else {
			return unrollAdvice.apply(this, arguments);
		}
	};
	if ((this["before"]) && (this.before.length > 0)) {
		dojo.lang.forEach(this.before.concat(new Array()), unRollSquelch);
	}
	var result;
	try {
		if ((this["around"]) && (this.around.length > 0)) {
			var mi = new dojo.event.MethodInvocation(this, obj, args);
			result = mi.proceed();
		} else {
			if (this.methodfunc) {
				result = this.object[this.methodname].apply(this.object, args);
			}
		}
	}
	catch (e) {
		if (!this.squelch) {
			dojo.debug(e, "when calling", this.methodname, "on", this.object, "with arguments", args);
			dojo.raise(e);
		}
	}
	if ((this["after"]) && (this.after.length > 0)) {
		dojo.lang.forEach(this.after.concat(new Array()), unRollSquelch);
	}
	return (this.methodfunc) ? result : null;
}, getArr:function (kind) {
	var type = "after";
	if ((typeof kind == "string") && (kind.indexOf("before") != -1)) {
		type = "before";
	} else {
		if (kind == "around") {
			type = "around";
		}
	}
	if (!this[type]) {
		this[type] = [];
	}
	return this[type];
}, kwAddAdvice:function (args) {
	this.addAdvice(args["adviceObj"], args["adviceFunc"], args["aroundObj"], args["aroundFunc"], args["adviceType"], args["precedence"], args["once"], args["delay"], args["rate"], args["adviceMsg"], args["maxCalls"]);
}, addAdvice:function (thisAdviceObj, thisAdvice, thisAroundObj, thisAround, adviceType, precedence, once, delay, rate, asMessage, maxCalls) {
	var arr = this.getArr(adviceType);
	if (!arr) {
		dojo.raise("bad this: " + this);
	}
	var ao = [thisAdviceObj, thisAdvice, thisAroundObj, thisAround, delay, rate, asMessage, maxCalls];
	if (once) {
		if (this.hasAdvice(thisAdviceObj, thisAdvice, adviceType, arr) >= 0) {
			return;
		}
	}
	if (precedence == "first") {
		arr.unshift(ao);
	} else {
		arr.push(ao);
	}
}, hasAdvice:function (thisAdviceObj, thisAdvice, adviceType, arr) {
	if (!arr) {
		arr = this.getArr(adviceType);
	}
	var ind = -1;
	for (var x = 0; x < arr.length; x++) {
		var aao = (typeof thisAdvice == "object") ? (new String(thisAdvice)).toString() : thisAdvice;
		var a1o = (typeof arr[x][1] == "object") ? (new String(arr[x][1])).toString() : arr[x][1];
		if ((arr[x][0] == thisAdviceObj) && (a1o == aao)) {
			ind = x;
		}
	}
	return ind;
}, removeAdvice:function (thisAdviceObj, thisAdvice, adviceType, once) {
	var arr = this.getArr(adviceType);
	var ind = this.hasAdvice(thisAdviceObj, thisAdvice, adviceType, arr);
	if (ind == -1) {
		return false;
	}
	while (ind != -1) {
		arr.splice(ind, 1);
		if (once) {
			break;
		}
		ind = this.hasAdvice(thisAdviceObj, thisAdvice, adviceType, arr);
	}
	return true;
}});

