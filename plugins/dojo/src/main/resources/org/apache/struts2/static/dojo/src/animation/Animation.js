/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.animation.Animation");
dojo.require("dojo.animation.AnimationEvent");
dojo.require("dojo.lang.func");
dojo.require("dojo.math");
dojo.require("dojo.math.curves");
dojo.deprecated("dojo.animation.Animation is slated for removal in 0.5; use dojo.lfx.* instead.", "0.5");
dojo.animation.Animation = function (curve, duration, accel, repeatCount, rate) {
	if (dojo.lang.isArray(curve)) {
		curve = new dojo.math.curves.Line(curve[0], curve[1]);
	}
	this.curve = curve;
	this.duration = duration;
	this.repeatCount = repeatCount || 0;
	this.rate = rate || 25;
	if (accel) {
		if (dojo.lang.isFunction(accel.getValue)) {
			this.accel = accel;
		} else {
			var i = 0.35 * accel + 0.5;
			this.accel = new dojo.math.curves.CatmullRom([[0], [i], [1]], 0.45);
		}
	}
};
dojo.lang.extend(dojo.animation.Animation, {curve:null, duration:0, repeatCount:0, accel:null, onBegin:null, onAnimate:null, onEnd:null, onPlay:null, onPause:null, onStop:null, handler:null, _animSequence:null, _startTime:null, _endTime:null, _lastFrame:null, _timer:null, _percent:0, _active:false, _paused:false, _startRepeatCount:0, play:function (gotoStart) {
	if (gotoStart) {
		clearTimeout(this._timer);
		this._active = false;
		this._paused = false;
		this._percent = 0;
	} else {
		if (this._active && !this._paused) {
			return;
		}
	}
	this._startTime = new Date().valueOf();
	if (this._paused) {
		this._startTime -= (this.duration * this._percent / 100);
	}
	this._endTime = this._startTime + this.duration;
	this._lastFrame = this._startTime;
	var e = new dojo.animation.AnimationEvent(this, null, this.curve.getValue(this._percent), this._startTime, this._startTime, this._endTime, this.duration, this._percent, 0);
	this._active = true;
	this._paused = false;
	if (this._percent == 0) {
		if (!this._startRepeatCount) {
			this._startRepeatCount = this.repeatCount;
		}
		e.type = "begin";
		if (typeof this.handler == "function") {
			this.handler(e);
		}
		if (typeof this.onBegin == "function") {
			this.onBegin(e);
		}
	}
	e.type = "play";
	if (typeof this.handler == "function") {
		this.handler(e);
	}
	if (typeof this.onPlay == "function") {
		this.onPlay(e);
	}
	if (this._animSequence) {
		this._animSequence._setCurrent(this);
	}
	this._cycle();
}, pause:function () {
	clearTimeout(this._timer);
	if (!this._active) {
		return;
	}
	this._paused = true;
	var e = new dojo.animation.AnimationEvent(this, "pause", this.curve.getValue(this._percent), this._startTime, new Date().valueOf(), this._endTime, this.duration, this._percent, 0);
	if (typeof this.handler == "function") {
		this.handler(e);
	}
	if (typeof this.onPause == "function") {
		this.onPause(e);
	}
}, playPause:function () {
	if (!this._active || this._paused) {
		this.play();
	} else {
		this.pause();
	}
}, gotoPercent:function (pct, andPlay) {
	clearTimeout(this._timer);
	this._active = true;
	this._paused = true;
	this._percent = pct;
	if (andPlay) {
		this.play();
	}
}, stop:function (gotoEnd) {
	clearTimeout(this._timer);
	var step = this._percent / 100;
	if (gotoEnd) {
		step = 1;
	}
	var e = new dojo.animation.AnimationEvent(this, "stop", this.curve.getValue(step), this._startTime, new Date().valueOf(), this._endTime, this.duration, this._percent);
	if (typeof this.handler == "function") {
		this.handler(e);
	}
	if (typeof this.onStop == "function") {
		this.onStop(e);
	}
	this._active = false;
	this._paused = false;
}, status:function () {
	if (this._active) {
		return this._paused ? "paused" : "playing";
	} else {
		return "stopped";
	}
}, _cycle:function () {
	clearTimeout(this._timer);
	if (this._active) {
		var curr = new Date().valueOf();
		var step = (curr - this._startTime) / (this._endTime - this._startTime);
		var fps = 1000 / (curr - this._lastFrame);
		this._lastFrame = curr;
		if (step >= 1) {
			step = 1;
			this._percent = 100;
		} else {
			this._percent = step * 100;
		}
		if (this.accel && this.accel.getValue) {
			step = this.accel.getValue(step);
		}
		var e = new dojo.animation.AnimationEvent(this, "animate", this.curve.getValue(step), this._startTime, curr, this._endTime, this.duration, this._percent, Math.round(fps));
		if (typeof this.handler == "function") {
			this.handler(e);
		}
		if (typeof this.onAnimate == "function") {
			this.onAnimate(e);
		}
		if (step < 1) {
			this._timer = setTimeout(dojo.lang.hitch(this, "_cycle"), this.rate);
		} else {
			e.type = "end";
			this._active = false;
			if (typeof this.handler == "function") {
				this.handler(e);
			}
			if (typeof this.onEnd == "function") {
				this.onEnd(e);
			}
			if (this.repeatCount > 0) {
				this.repeatCount--;
				this.play(true);
			} else {
				if (this.repeatCount == -1) {
					this.play(true);
				} else {
					if (this._startRepeatCount) {
						this.repeatCount = this._startRepeatCount;
						this._startRepeatCount = 0;
					}
					if (this._animSequence) {
						this._animSequence._playNext();
					}
				}
			}
		}
	}
}});

