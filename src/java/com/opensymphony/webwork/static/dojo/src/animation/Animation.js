/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.animation");
dojo.provide("dojo.animation.Animation");

dojo.require("dojo.lang");
dojo.require("dojo.math");
dojo.require("dojo.math.curves");

/*
Animation package based off of Dan Pupius' work on Animations:
http://pupius.co.uk/js/Toolkit.Drawing.js
*/

dojo.animation.Animation = function(curve, duration, accel, repeatCount, rate) {
	// public properties
	this.curve = curve;
	this.duration = duration;
	this.repeatCount = repeatCount || 0;
	this.rate = rate || 10;
	if(accel) {
		if(dojo.lang.isFunction(accel.getValue)) {
			this.accel = accel;
		} else {
			var i = 0.35*accel+0.5;	// 0.15 <= i <= 0.85
			this.accel = new dojo.math.curves.CatmullRom([[0], [i], [1]], 0.45);
		}
	}
}
dojo.lang.extend(dojo.animation.Animation, {
	// public properties
	curve: null,
	duration: 0,
	repeatCount: 0,
	accel: null,

	// events
	onBegin: null,
	onAnimate: null,
	onEnd: null,
	onPlay: null,
	onPause: null,
	onStop: null,
	handler: null,

	// "private" properties
	_animSequence: null,
	_startTime: null,
	_endTime: null,
	_lastFrame: null,
	_timer: null,
	_percent: 0,
	_active: false,
	_paused: false,
	_startRepeatCount: 0,

	// public methods
	play: function(gotoStart) {
		if( gotoStart ) {
			clearTimeout(this._timer);
			this._active = false;
			this._paused = false;
			this._percent = 0;
		} else if( this._active && !this._paused ) {
			return;
		}

		this._startTime = new Date().valueOf();
		if( this._paused ) {
			this._startTime -= (this.duration * this._percent / 100);
		}
		this._endTime = this._startTime + this.duration;
		this._lastFrame = this._startTime;

		var e = new dojo.animation.AnimationEvent(this, null, this.curve.getValue(this._percent),
			this._startTime, this._startTime, this._endTime, this.duration, this._percent, 0);

		this._active = true;
		this._paused = false;

		if( this._percent == 0 ) {
			if(!this._startRepeatCount) {
				this._startRepeatCount = this.repeatCount;
			}
			e.type = "begin";
			if(typeof this.handler == "function") { this.handler(e); }
			if(typeof this.onBegin == "function") { this.onBegin(e); }
		}

		e.type = "play";
		if(typeof this.handler == "function") { this.handler(e); }
		if(typeof this.onPlay == "function") { this.onPlay(e); }

		if(this._animSequence) { this._animSequence._setCurrent(this); }

		//dojo.lang.hitch(this, cycle)();
		this._cycle();
	},

	pause: function() {
		clearTimeout(this._timer);
		if( !this._active ) { return; }
		this._paused = true;
		var e = new dojo.animation.AnimationEvent(this, "pause", this.curve.getValue(this._percent),
			this._startTime, new Date().valueOf(), this._endTime, this.duration, this._percent, 0);
		if(typeof this.handler == "function") { this.handler(e); }
		if(typeof this.onPause == "function") { this.onPause(e); }
	},

	playPause: function() {
		if( !this._active || this._paused ) {
			this.play();
		} else {
			this.pause();
		}
	},

	gotoPercent: function(pct, andPlay) {
		clearTimeout(this._timer);
		this._active = true;
		this._paused = true;
		this._percent = pct;
		if( andPlay ) { this.play(); }
	},

	stop: function(gotoEnd) {
		clearTimeout(this._timer);
		var step = this._percent / 100;
		if( gotoEnd ) {
			step = 1;
		}
		var e = new dojo.animation.AnimationEvent(this, "stop", this.curve.getValue(step),
			this._startTime, new Date().valueOf(), this._endTime, this.duration, this._percent, Math.round(fps));
		if(typeof this.handler == "function") { this.handler(e); }
		if(typeof this.onStop == "function") { this.onStop(e); }
		this._active = false;
		this._paused = false;
	},

	status: function() {
		if( this._active ) {
			return this._paused ? "paused" : "playing";
		} else {
			return "stopped";
		}
	},

	// "private" methods
	_cycle: function() {
		clearTimeout(this._timer);
		if( this._active ) {
			var curr = new Date().valueOf();
			var step = (curr - this._startTime) / (this._endTime - this._startTime);
			fps = 1000 / (curr - this._lastFrame);
			this._lastFrame = curr;

			if( step >= 1 ) {
				step = 1;
				this._percent = 100;
			} else {
				this._percent = step * 100;
			}
			
			// Perform accelleration
			if(this.accel && this.accel.getValue) {
				step = this.accel.getValue(step);
			}

			var e = new dojo.animation.AnimationEvent(this, "animate", this.curve.getValue(step),
				this._startTime, curr, this._endTime, this.duration, this._percent, Math.round(fps));

			if(typeof this.handler == "function") { this.handler(e); }
			if(typeof this.onAnimate == "function") { this.onAnimate(e); }

			if( step < 1 ) {
				this._timer = setTimeout(dojo.lang.hitch(this, "_cycle"), this.rate);
			} else {
				e.type = "end";
				this._active = false;
				if(typeof this.handler == "function") { this.handler(e); }
				if(typeof this.onEnd == "function") { this.onEnd(e); }

				if( this.repeatCount > 0 ) {
					this.repeatCount--;
					this.play(true);
				} else if( this.repeatCount == -1 ) {
					this.play(true);
				} else {
					if(this._startRepeatCount) {
						this.repeatCount = this._startRepeatCount;
						this._startRepeatCount = 0;
					}
					if( this._animSequence ) {
						this._animSequence._playNext();
					}
				}
			}
		}
	}
});

dojo.animation.AnimationEvent = function(anim, type, coords, sTime, cTime, eTime, dur, pct, fps) {
	this.type = type; // "animate", "begin", "end", "play", "pause", "stop"
	this.animation = anim;

	this.coords = coords;
	this.x = coords[0];
	this.y = coords[1];
	this.z = coords[2];

	this.startTime = sTime;
	this.currentTime = cTime;
	this.endTime = eTime;

	this.duration = dur;
	this.percent = pct;
	this.fps = fps;
};
dojo.lang.extend(dojo.animation.AnimationEvent, {
	coordsAsInts: function() {
		var cints = new Array(this.coords.length);
		for(var i = 0; i < this.coords.length; i++) {
			cints[i] = Math.round(this.coords[i]);
		}
		return cints;
	}
});

dojo.animation.AnimationSequence = function(repeatCount) {
	this.repeatCount = repeatCount || 0;
}
dojo.lang.extend(dojo.animation.AnimationSequence, {
	repeateCount: 0,

	_anims: [],
	_currAnim: -1,

	onBegin: null,
	onEnd: null,
	onNext: null,
	handler: null,

	add: function() {
		for(var i = 0; i < arguments.length; i++) {
			this._anims.push(arguments[i]);
			arguments[i]._animSequence = this;
		}
	},

	remove: function(anim) {
		for(var i = 0; i < this._anims.length; i++) {
			if( this._anims[i] == anim ) {
				this._anims[i]._animSequence = null;
				this._anims.splice(i, 1);
				break;
			}
		}
	},

	removeAll: function() {
		for(var i = 0; i < this._anims.length; i++) {
			this._anims[i]._animSequence = null;
		}
		this._anims = [];
		this._currAnim = -1;
	},

	clear: function() {
		this.removeAll();
	},

	play: function(gotoStart) {
		if( this._anims.length == 0 ) { return; }
		if( gotoStart || !this._anims[this._currAnim] ) {
			this._currAnim = 0;
		}
		if( this._anims[this._currAnim] ) {
			if( this._currAnim == 0 ) {
				var e = {type: "begin", animation: this._anims[this._currAnim]};
				if(typeof this.handler == "function") { this.handler(e); }
				if(typeof this.onBegin == "function") { this.onBegin(e); }
			}
			this._anims[this._currAnim].play(gotoStart);
		}
	},

	pause: function() {
		if( this._anims[this._currAnim] ) {
			this._anims[this._currAnim].pause();
		}
	},

	playPause: function() {
		if( this._anims.length == 0 ) { return; }
		if( this._currAnim == -1 ) { this._currAnim = 0; }
		if( this._anims[this._currAnim] ) {
			this._anims[this._currAnim].playPause();
		}
	},

	stop: function() {
		if( this._anims[this._currAnim] ) {
			this._anims[this._currAnim].stop();
		}
	},

	status: function() {
		if( this._anims[this._currAnim] ) {
			return this._anims[this._currAnim].status();
		} else {
			return "stopped";
		}
	},

	_setCurrent: function(anim) {
		for(var i = 0; i < this._anims.length; i++) {
			if( this._anims[i] == anim ) {
				this._currAnim = i;
				break;
			}
		}
	},

	_playNext: function() {
		if( this._currAnim == -1 || this._anims.length == 0 ) { return; }
		this._currAnim++;
		if( this._anims[this._currAnim] ) {
			var e = {type: "next", animation: this._anims[this._currAnim]};
			if(typeof this.handler == "function") { this.handler(e); }
			if(typeof this.onNext == "function") { this.onNext(e); }
			this._anims[this._currAnim].play(true);
		} else {
			var e = {type: "end", animation: this._anims[this._anims.length-1]};
			if(typeof this.handler == "function") { this.handler(e); }
			if(typeof this.onEnd == "function") { this.onEnd(e); }
			if(this.repeatCount > 0) {
				this._currAnim = 0;
				this.repeatCount--;
				this._anims[this._currAnim].play(true);
			} else if(this.repeatCount == -1) {
				this._currAnim = 0;
				this._anims[this._currAnim].play(true);
			} else {
				this._currAnim = -1;
			}
		}
	}
});
