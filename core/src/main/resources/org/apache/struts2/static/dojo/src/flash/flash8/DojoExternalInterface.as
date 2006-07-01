/**
	A wrapper around Flash 8's ExternalInterface; this is needed so that we
	can do a Flash 6 implementation of ExternalInterface, and be able
	to support having a single codebase that uses DojoExternalInterface
	across Flash versions rather than having two seperate source bases,
	where one uses ExternalInterface and the other uses DojoExternalInterface.
	
	@author Brad Neuberg, bkn3@columbia.edu
*/
import flash.external.ExternalInterface;

class DojoExternalInterface{
	public static var available:Boolean;
	
	public static function initialize(){
		// set whether communication is available
		DojoExternalInterface.available = ExternalInterface.available;
		DojoExternalInterface.call("loaded");
	}
	
	public static function addCallback(methodName:String, instance:Object, 
										 								 method:Function) : Boolean{
		return ExternalInterface.addCallback(methodName, instance, method);									 
	}
	
	public static function call(methodName:String) : Object{
		// we might have any number of optional arguments, so we have to 
		// pass them in dynamically
		return ExternalInterface.call.apply(ExternalInterface, arguments);
	}
	
	/** 
			Called by Flash to indicate to JavaScript that we are ready to have
			our Flash functions called. Calling loaded()
			will fire the dojo.flash.loaded() event, so that JavaScript can know that
			Flash has finished loading and adding its callbacks, and can begin to
			interact with the Flash file.
	*/
	public static function loaded(){
		DojoExternalInterface.call("dojo.flash.loaded");
	}
}

// vim:ts=4:noet:tw=0:
