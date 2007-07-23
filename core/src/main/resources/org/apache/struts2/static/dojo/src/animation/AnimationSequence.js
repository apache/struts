/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.animation.AnimationSequence");
dojo.require("dojo.animation.AnimationEvent");
dojo.require("dojo.animation.Animation");

dojo.deprecated("dojo.animation.AnimationSequence is slated for removal in 0.5; use dojo.lfx.* instead.", "0.5");

dojo.animation.AnimationSequence = function(/*int?*/ repeatCount){
	// summary: Sequence of Animations, played one after the other.
	// repeatCount: Number of times to repeat the entire sequence.  Default is 0 (play once only).
	// description: Calls the following events: "onBegin", "onEnd", "onNext"
	// 				If the animation implements a "handler" function, that will be called before each event is called.
	this._anims = [];
	this.repeatCount = repeatCount || 0;
}

dojo.lang.extend(dojo.animation.AnimationSequence, {
	repeatCount: 0,

	_anims: [],
	_currAnim: -1,

	onBegin: null,
	onEnd: null,
	onNext: null,
	handler: null,

	add: function() {
		// summary: Add one or more Animations to the sequence.
		// description:  args: Animations (dojo.animation.Animation) to add to the sequence.
		for(var i = 0; i < arguments.length; i++) {
			this._anims.push(arguments[i]);
			arguments[i]._animSequence = this;
		}
	},

	remove: function(/*dojo.animation.Animation*/ anim) {
		// summary: Remove one particular animation from the sequence.
		//	amim: Animation to remove.
		for(var i = 0; i < this._anims.length; i++) {
			if( this._anims[i] == anim ) {
				this._anims[i]._animSequence = null;
				this._anims.splice(i, 1);
				break;
			}
		}
	},

	removeAll: function() {
		// summary: Remove all animations from the sequence.
		for(var i = 0; i < this._anims.length; i++) {
			this._anims[i]._animSequence = null;
		}
		this._anims = [];
		this._currAnim = -1;
	},

	clear: function() {
		// summary: Remove all animations from the sequence.
		this.removeAll();
	},

	play: function(/*Boolean?*/ gotoStart) {
		// summary: Play the animation sequence.
		// gotoStart: If true, will start at the beginning of the first sequence.
		//				Otherwise, starts at the current play counter of the current animation.
		// description: Sends an "onBegin" event to any observers.
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
		// summary: temporarily stop the current animation.  Resume later with sequence.play()
		if( this._anims[this._currAnim] ) {
			this._anims[this._currAnim].pause();
		}
	},

	playPause: function() {
		// summary: Toggle between play and paused states.
		if( this._anims.length == 0 ) { return; }
		if( this._currAnim == -1 ) { this._currAnim = 0; }
		if( this._anims[this._currAnim] ) {
			this._anims[this._currAnim].playPause();
		}
	},

	stop: function() {
		// summary: Stop the current animation.
		if( this._anims[this._currAnim] ) {
			this._anims[this._currAnim].stop();
		}
	},

	status: function() {
		// summary: Return the status of the current animation.
		// description: Returns one of "playing", "paused" or "stopped".
		if( this._anims[this._currAnim] ) {
			return this._anims[this._currAnim].status();
		} else {
			return "stopped";
		}
	},

	_setCurrent: function(/*dojo.animation.Animation*/ anim) {
		// summary: Set the current animation.
		// anim: Animation to make current, must have already been added to the sequence.
		for(var i = 0; i < this._anims.length; i++) {
			if( this._anims[i] == anim ) {
				this._currAnim = i;
				break;
			}
		}
	},

	_playNext: function() {
		// summary: Play the next animation in the sequence.
		// description:  Sends an "onNext" event to any observers.
		//				 Also sends "onEnd" if the last animation is finished.
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
