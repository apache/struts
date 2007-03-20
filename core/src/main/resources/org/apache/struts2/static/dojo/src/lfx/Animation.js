/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.lfx.Animation");

dojo.require("dojo.lang.func");

/*
	Animation package based on Dan Pupius' work: http://pupius.co.uk/js/Toolkit.Drawing.js
*/
dojo.lfx.Line = function(/*int*/ start, /*int*/ end){
	// summary: dojo.lfx.Line is the object used to generate values
	//			from a start value to an end value
	this.start = start;
	this.end = end;
	if(dojo.lang.isArray(start)){
		/* start: Array
		   end: Array
		   pId: a */
		var diff = [];
		dojo.lang.forEach(this.start, function(s,i){
			diff[i] = this.end[i] - s;
		}, this);
		
		this.getValue = function(/*float*/ n){
			var res = [];
			dojo.lang.forEach(this.start, function(s, i){
				res[i] = (diff[i] * n) + s;
			}, this);
			return res; // Array
		}
	}else{
		var diff = end - start;
			
		this.getValue = function(/*float*/ n){
			//	summary: returns the point on the line
			//	n: a floating point number greater than 0 and less than 1
			return (diff * n) + this.start; // Decimal
		}
	}
}

dojo.lfx.easeDefault = function(/*Decimal?*/ n){
	//	summary: Returns the point for point n on a sin wave.
	if(dojo.render.html.khtml){
		// the cool kids are obviously not using konqueror...
		// found a very wierd bug in floats constants, 1.5 evals as 1
		// seems somebody mixed up ints and floats in 3.5.4 ??
		// FIXME: investigate more and post a KDE bug (Fredrik)
		return (parseFloat("0.5")+((Math.sin( (n+parseFloat("1.5")) * Math.PI))/2));
	}else{
		return (0.5+((Math.sin( (n+1.5) * Math.PI))/2));
	}
}

dojo.lfx.easeIn = function(/*Decimal?*/ n){
	//	summary: returns the point on an easing curve
	//	n: a floating point number greater than 0 and less than 1
	return Math.pow(n, 3);
}

dojo.lfx.easeOut = function(/*Decimal?*/ n){
	//	summary: returns the point on the line
	//	n: a floating point number greater than 0 and less than 1
	return ( 1 - Math.pow(1 - n, 3) );
}

dojo.lfx.easeInOut = function(/*Decimal?*/ n){
	//	summary: returns the point on the line
	//	n: a floating point number greater than 0 and less than 1
	return ( (3 * Math.pow(n, 2)) - (2 * Math.pow(n, 3)) );
}

dojo.lfx.IAnimation = function(){
	// summary: dojo.lfx.IAnimation is an interface that implements
	//			commonly used functions of animation objects
}
dojo.lang.extend(dojo.lfx.IAnimation, {
	// public properties
	curve: null,
	duration: 1000,
	easing: null,
	repeatCount: 0,
	rate: 25,
	
	// events
	handler: null,
	beforeBegin: null,
	onBegin: null,
	onAnimate: null,
	onEnd: null,
	onPlay: null,
	onPause: null,
	onStop: null,
	
	// public methods
	play: null,
	pause: null,
	stop: null,
	
	connect: function(/*Event*/ evt, /*Object*/ scope, /*Function*/ newFunc){
		// summary: Convenience function.  Quickly connect to an event
		//			of this object and save the old functions connected to it.
		// evt: The name of the event to connect to.
		// scope: the scope in which to run newFunc.
		// newFunc: the function to run when evt is fired.
		if(!newFunc){
			/* scope: Function
			   newFunc: null
			   pId: f */
			newFunc = scope;
			scope = this;
		}
		newFunc = dojo.lang.hitch(scope, newFunc);
		var oldFunc = this[evt]||function(){};
		this[evt] = function(){
			var ret = oldFunc.apply(this, arguments);
			newFunc.apply(this, arguments);
			return ret;
		}
		return this; // dojo.lfx.IAnimation
	},

	fire: function(/*Event*/ evt, /*Array*/ args){
		// summary: Convenience function.  Fire event "evt" and pass it
		//			the arguments specified in "args".
		// evt: The event to fire.
		// args: The arguments to pass to the event.
		if(this[evt]){
			this[evt].apply(this, (args||[]));
		}
		return this; // dojo.lfx.IAnimation
	},
	
	repeat: function(/*int*/ count){
		// summary: Set the repeat count of this object.
		// count: How many times to repeat the animation.
		this.repeatCount = count;
		return this; // dojo.lfx.IAnimation
	},

	// private properties
	_active: false,
	_paused: false
});

dojo.lfx.Animation = function(	/*Object*/ handlers, 
								/*int*/ duration, 
								/*dojo.lfx.Line*/ curve, 
								/*function*/ easing, 
								/*int*/ repeatCount, 
								/*int*/ rate){
	//	summary
	//		a generic animation object that fires callbacks into it's handlers
	//		object at various states
	//	handlers: { handler: Function?, onstart: Function?, onstop: Function?, onanimate: Function? }
	dojo.lfx.IAnimation.call(this);
	if(dojo.lang.isNumber(handlers)||(!handlers && duration.getValue)){
		// no handlers argument:
		rate = repeatCount;
		repeatCount = easing;
		easing = curve;
		curve = duration;
		duration = handlers;
		handlers = null;
	}else if(handlers.getValue||dojo.lang.isArray(handlers)){
		// no handlers or duration:
		rate = easing;
		repeatCount = curve;
		easing = duration;
		curve = handlers;
		duration = null;
		handlers = null;
	}
	if(dojo.lang.isArray(curve)){
		/* curve: Array
		   pId: a */
		this.curve = new dojo.lfx.Line(curve[0], curve[1]);
	}else{
		this.curve = curve;
	}
	if(duration != null && duration > 0){ this.duration = duration; }
	if(repeatCount){ this.repeatCount = repeatCount; }
	if(rate){ this.rate = rate; }
	if(handlers){
		dojo.lang.forEach([
				"handler", "beforeBegin", "onBegin", 
				"onEnd", "onPlay", "onStop", "onAnimate"
			], function(item){
				if(handlers[item]){
					this.connect(item, handlers[item]);
				}
			}, this);
	}
	if(easing && dojo.lang.isFunction(easing)){
		this.easing=easing;
	}
}
dojo.inherits(dojo.lfx.Animation, dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Animation, {
	// "private" properties
	_startTime: null,
	_endTime: null,
	_timer: null,
	_percent: 0,
	_startRepeatCount: 0,

	// public methods
	play: function(/*int?*/ delay, /*bool?*/ gotoStart){
		// summary: Start the animation.
		// delay: How many milliseconds to delay before starting.
		// gotoStart: If true, starts the animation from the beginning; otherwise,
		//            starts it from its current position.
		if(gotoStart){
			clearTimeout(this._timer);
			this._active = false;
			this._paused = false;
			this._percent = 0;
		}else if(this._active && !this._paused){
			return this; // dojo.lfx.Animation
		}
		
		this.fire("handler", ["beforeBegin"]);
		this.fire("beforeBegin");

		if(delay > 0){
			setTimeout(dojo.lang.hitch(this, function(){ this.play(null, gotoStart); }), delay);
			return this; // dojo.lfx.Animation
		}
		
		this._startTime = new Date().valueOf();
		if(this._paused){
			this._startTime -= (this.duration * this._percent / 100);
		}
		this._endTime = this._startTime + this.duration;

		this._active = true;
		this._paused = false;
		
		var step = this._percent / 100;
		var value = this.curve.getValue(step);
		if(this._percent == 0 ){
			if(!this._startRepeatCount){
				this._startRepeatCount = this.repeatCount;
			}
			this.fire("handler", ["begin", value]);
			this.fire("onBegin", [value]);
		}

		this.fire("handler", ["play", value]);
		this.fire("onPlay", [value]);

		this._cycle();
		return this; // dojo.lfx.Animation
	},

	pause: function(){
		// summary: Pauses a running animation.
		clearTimeout(this._timer);
		if(!this._active){ return this; /*dojo.lfx.Animation*/}
		this._paused = true;
		var value = this.curve.getValue(this._percent / 100);
		this.fire("handler", ["pause", value]);
		this.fire("onPause", [value]);
		return this; // dojo.lfx.Animation
	},

	gotoPercent: function(/*Decimal*/ pct, /*bool?*/ andPlay){
		// summary: Sets the progress of the animation.
		// pct: A percentage in decimal notation (between and including 0.0 and 1.0).
		// andPlay: If true, play the animation after setting the progress.
		clearTimeout(this._timer);
		this._active = true;
		this._paused = true;
		this._percent = pct;
		if(andPlay){ this.play(); }
		return this; // dojo.lfx.Animation
	},

	stop: function(/*bool?*/ gotoEnd){
		// summary: Stops a running animation.
		// gotoEnd: If true, the animation will end.
		clearTimeout(this._timer);
		var step = this._percent / 100;
		if(gotoEnd){
			step = 1;
		}
		var value = this.curve.getValue(step);
		this.fire("handler", ["stop", value]);
		this.fire("onStop", [value]);
		this._active = false;
		this._paused = false;
		return this; // dojo.lfx.Animation
	},

	status: function(){
		// summary: Returns a string representation of the status of
		//			the animation.
		if(this._active){
			return this._paused ? "paused" : "playing"; // String
		}else{
			return "stopped"; // String
		}
		return this;
	},

	// "private" methods
	_cycle: function(){
		clearTimeout(this._timer);
		if(this._active){
			var curr = new Date().valueOf();
			var step = (curr - this._startTime) / (this._endTime - this._startTime);

			if(step >= 1){
				step = 1;
				this._percent = 100;
			}else{
				this._percent = step * 100;
			}
			
			// Perform easing
			if((this.easing)&&(dojo.lang.isFunction(this.easing))){
				step = this.easing(step);
			}

			var value = this.curve.getValue(step);
			this.fire("handler", ["animate", value]);
			this.fire("onAnimate", [value]);

			if( step < 1 ){
				this._timer = setTimeout(dojo.lang.hitch(this, "_cycle"), this.rate);
			}else{
				this._active = false;
				this.fire("handler", ["end"]);
				this.fire("onEnd");

				if(this.repeatCount > 0){
					this.repeatCount--;
					this.play(null, true);
				}else if(this.repeatCount == -1){
					this.play(null, true);
				}else{
					if(this._startRepeatCount){
						this.repeatCount = this._startRepeatCount;
						this._startRepeatCount = 0;
					}
				}
			}
		}
		return this; // dojo.lfx.Animation
	}
});

dojo.lfx.Combine = function(/*dojo.lfx.IAnimation...*/ animations){
	// summary: An animation object to play animations passed to it at the same time.
	dojo.lfx.IAnimation.call(this);
	this._anims = [];
	this._animsEnded = 0;
	
	var anims = arguments;
	if(anims.length == 1 && (dojo.lang.isArray(anims[0]) || dojo.lang.isArrayLike(anims[0]))){
		/* animations: dojo.lfx.IAnimation[]
		   pId: a */
		anims = anims[0];
	}
	
	dojo.lang.forEach(anims, function(anim){
		this._anims.push(anim);
		anim.connect("onEnd", dojo.lang.hitch(this, "_onAnimsEnded"));
	}, this);
}
dojo.inherits(dojo.lfx.Combine, dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Combine, {
	// private members
	_animsEnded: 0,
	
	// public methods
	play: function(/*int?*/ delay, /*bool?*/ gotoStart){
		// summary: Start the animations.
		// delay: How many milliseconds to delay before starting.
		// gotoStart: If true, starts the animations from the beginning; otherwise,
		//            starts them from their current position.
		if( !this._anims.length ){ return this; /*dojo.lfx.Combine*/}

		this.fire("beforeBegin");

		if(delay > 0){
			setTimeout(dojo.lang.hitch(this, function(){ this.play(null, gotoStart); }), delay);
			return this; // dojo.lfx.Combine
		}
		
		if(gotoStart || this._anims[0].percent == 0){
			this.fire("onBegin");
		}
		this.fire("onPlay");
		this._animsCall("play", null, gotoStart);
		return this; // dojo.lfx.Combine
	},
	
	pause: function(){
		// summary: Pauses the running animations.
		this.fire("onPause");
		this._animsCall("pause"); 
		return this; // dojo.lfx.Combine
	},
	
	stop: function(/*bool?*/ gotoEnd){
		// summary: Stops the running animations.
		// gotoEnd: If true, the animations will end.
		this.fire("onStop");
		this._animsCall("stop", gotoEnd);
		return this; // dojo.lfx.Combine
	},
	
	// private methods
	_onAnimsEnded: function(){
		this._animsEnded++;
		if(this._animsEnded >= this._anims.length){
			this.fire("onEnd");
		}
		return this; // dojo.lfx.Combine
	},
	
	_animsCall: function(/*String*/ funcName){
		var args = [];
		if(arguments.length > 1){
			for(var i = 1 ; i < arguments.length ; i++){
				args.push(arguments[i]);
			}
		}
		var _this = this;
		dojo.lang.forEach(this._anims, function(anim){
			anim[funcName](args);
		}, _this);
		return this; // dojo.lfx.Combine
	}
});

dojo.lfx.Chain = function(/*dojo.lfx.IAnimation...*/ animations) {
	// summary: An animation object to play animations passed to it
	//			one after another.
	dojo.lfx.IAnimation.call(this);
	this._anims = [];
	this._currAnim = -1;
	
	var anims = arguments;
	if(anims.length == 1 && (dojo.lang.isArray(anims[0]) || dojo.lang.isArrayLike(anims[0]))){
		/* animations: dojo.lfx.IAnimation[]
		   pId: a */
		anims = anims[0];
	}
	
	var _this = this;
	dojo.lang.forEach(anims, function(anim, i, anims_arr){
		this._anims.push(anim);
		if(i < anims_arr.length - 1){
			anim.connect("onEnd", dojo.lang.hitch(this, "_playNext") );
		}else{
			anim.connect("onEnd", dojo.lang.hitch(this, function(){ this.fire("onEnd"); }) );
		}
	}, this);
}
dojo.inherits(dojo.lfx.Chain, dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Chain, {
	// private members
	_currAnim: -1,
	
	// public methods
	play: function(/*int?*/ delay, /*bool?*/ gotoStart){
		// summary: Start the animation sequence.
		// delay: How many milliseconds to delay before starting.
		// gotoStart: If true, starts the sequence from the beginning; otherwise,
		//            starts it from its current position.
		if( !this._anims.length ) { return this; /*dojo.lfx.Chain*/}
		if( gotoStart || !this._anims[this._currAnim] ) {
			this._currAnim = 0;
		}

		var currentAnimation = this._anims[this._currAnim];

		this.fire("beforeBegin");
		if(delay > 0){
			setTimeout(dojo.lang.hitch(this, function(){ this.play(null, gotoStart); }), delay);
			return this; // dojo.lfx.Chain
		}
		
		if(currentAnimation){
			if(this._currAnim == 0){
				this.fire("handler", ["begin", this._currAnim]);
				this.fire("onBegin", [this._currAnim]);
			}
			this.fire("onPlay", [this._currAnim]);
			currentAnimation.play(null, gotoStart);
		}
		return this; // dojo.lfx.Chain
	},
	
	pause: function(){
		// summary: Pauses the running animation sequence.
		if( this._anims[this._currAnim] ) {
			this._anims[this._currAnim].pause();
			this.fire("onPause", [this._currAnim]);
		}
		return this; // dojo.lfx.Chain
	},
	
	playPause: function(){
		// summary: If the animation sequence is playing, pause it; otherwise,
		//			play it.
		if(this._anims.length == 0){ return this; }
		if(this._currAnim == -1){ this._currAnim = 0; }
		var currAnim = this._anims[this._currAnim];
		if( currAnim ) {
			if( !currAnim._active || currAnim._paused ) {
				this.play();
			} else {
				this.pause();
			}
		}
		return this; // dojo.lfx.Chain
	},
	
	stop: function(){
		// summary: Stops the running animations.
		var currAnim = this._anims[this._currAnim];
		if(currAnim){
			currAnim.stop();
			this.fire("onStop", [this._currAnim]);
		}
		return currAnim; // dojo.lfx.IAnimation
	},
	
	// private methods
	_playNext: function(){
		if( this._currAnim == -1 || this._anims.length == 0 ) { return this; }
		this._currAnim++;
		if( this._anims[this._currAnim] ){
			this._anims[this._currAnim].play(null, true);
		}
		return this; // dojo.lfx.Chain
	}
});

dojo.lfx.combine = function(/*dojo.lfx.IAnimation...*/ animations){
	// summary: Convenience function.  Returns a dojo.lfx.Combine created
	//			using the animations passed in.
	var anims = arguments;
	if(dojo.lang.isArray(arguments[0])){
		/* animations: dojo.lfx.IAnimation[]
		   pId: a */
		anims = arguments[0];
	}
	if(anims.length == 1){ return anims[0]; }
	return new dojo.lfx.Combine(anims); // dojo.lfx.Combine
}

dojo.lfx.chain = function(/*dojo.lfx.IAnimation...*/ animations){
	// summary: Convenience function.  Returns a dojo.lfx.Chain created
	//			using the animations passed in.
	var anims = arguments;
	if(dojo.lang.isArray(arguments[0])){
		/* animations: dojo.lfx.IAnimation[]
		   pId: a */
		anims = arguments[0];
	}
	if(anims.length == 1){ return anims[0]; }
	return new dojo.lfx.Chain(anims); // dojo.lfx.Combine
}
