/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.Deferred");
dojo.require("dojo.lang.func");
dojo.Deferred = function (canceller) {
	this.chain = [];
	this.id = this._nextId();
	this.fired = -1;
	this.paused = 0;
	this.results = [null, null];
	this.canceller = canceller;
	this.silentlyCancelled = false;
};
dojo.lang.extend(dojo.Deferred, {getFunctionFromArgs:function () {
	var a = arguments;
	if ((a[0]) && (!a[1])) {
		if (dojo.lang.isFunction(a[0])) {
			return a[0];
		} else {
			if (dojo.lang.isString(a[0])) {
				return dj_global[a[0]];
			}
		}
	} else {
		if ((a[0]) && (a[1])) {
			return dojo.lang.hitch(a[0], a[1]);
		}
	}
	return null;
}, makeCalled:function () {
	var deferred = new dojo.Deferred();
	deferred.callback();
	return deferred;
}, repr:function () {
	var state;
	if (this.fired == -1) {
		state = "unfired";
	} else {
		if (this.fired == 0) {
			state = "success";
		} else {
			state = "error";
		}
	}
	return "Deferred(" + this.id + ", " + state + ")";
}, toString:dojo.lang.forward("repr"), _nextId:(function () {
	var n = 1;
	return function () {
		return n++;
	};
})(), cancel:function () {
	if (this.fired == -1) {
		if (this.canceller) {
			this.canceller(this);
		} else {
			this.silentlyCancelled = true;
		}
		if (this.fired == -1) {
			this.errback(new Error(this.repr()));
		}
	} else {
		if ((this.fired == 0) && (this.results[0] instanceof dojo.Deferred)) {
			this.results[0].cancel();
		}
	}
}, _pause:function () {
	this.paused++;
}, _unpause:function () {
	this.paused--;
	if ((this.paused == 0) && (this.fired >= 0)) {
		this._fire();
	}
}, _continue:function (res) {
	this._resback(res);
	this._unpause();
}, _resback:function (res) {
	this.fired = ((res instanceof Error) ? 1 : 0);
	this.results[this.fired] = res;
	this._fire();
}, _check:function () {
	if (this.fired != -1) {
		if (!this.silentlyCancelled) {
			dojo.raise("already called!");
		}
		this.silentlyCancelled = false;
		return;
	}
}, callback:function (res) {
	this._check();
	this._resback(res);
}, errback:function (res) {
	this._check();
	if (!(res instanceof Error)) {
		res = new Error(res);
	}
	this._resback(res);
}, addBoth:function (cb, cbfn) {
	var enclosed = this.getFunctionFromArgs(cb, cbfn);
	if (arguments.length > 2) {
		enclosed = dojo.lang.curryArguments(null, enclosed, arguments, 2);
	}
	return this.addCallbacks(enclosed, enclosed);
}, addCallback:function (cb, cbfn) {
	var enclosed = this.getFunctionFromArgs(cb, cbfn);
	if (arguments.length > 2) {
		enclosed = dojo.lang.curryArguments(null, enclosed, arguments, 2);
	}
	return this.addCallbacks(enclosed, null);
}, addErrback:function (cb, cbfn) {
	var enclosed = this.getFunctionFromArgs(cb, cbfn);
	if (arguments.length > 2) {
		enclosed = dojo.lang.curryArguments(null, enclosed, arguments, 2);
	}
	return this.addCallbacks(null, enclosed);
	return this.addCallbacks(null, cbfn);
}, addCallbacks:function (cb, eb) {
	this.chain.push([cb, eb]);
	if (this.fired >= 0) {
		this._fire();
	}
	return this;
}, _fire:function () {
	var chain = this.chain;
	var fired = this.fired;
	var res = this.results[fired];
	var self = this;
	var cb = null;
	while (chain.length > 0 && this.paused == 0) {
		var pair = chain.shift();
		var f = pair[fired];
		if (f == null) {
			continue;
		}
		try {
			res = f(res);
			fired = ((res instanceof Error) ? 1 : 0);
			if (res instanceof dojo.Deferred) {
				cb = function (res) {
					self._continue(res);
				};
				this._pause();
			}
		}
		catch (err) {
			fired = 1;
			res = err;
		}
	}
	this.fired = fired;
	this.results[fired] = res;
	if ((cb) && (this.paused)) {
		res.addBoth(cb);
	}
}});

