dojo.provide("struts.Util");

struts.Util.passThroughArgs = function(args, target){
  // pass through the extra args, catering for special cases of style and class for html elements
  for (n in args) {
    var v = args[n];
    if (n == "style") {
      target.style.cssText = v;
    }else if (n == "class") {
      target.className = v;
    }else if (n == "dojoType") {
    }else if (n == "dojotype") {
    }else{
      target[n] = v;
    }
  }
}

struts.Util.passThroughWidgetTagContent = function(widget, frag, target) {
  // fill in the target with the contents of the widget tag
  var widgetTag = frag["dojo:" + widget.widgetType.toLowerCase()].nodeRef;
  if(widgetTag) target.innerHTML = widgetTag.innerHTML;
}

struts.Util.copyProperties = function(source, target){
  // pass through the extra args, catering for special cases of style and class for html elements
  for (key in source) target[key] = source[key];
}


struts.Util.globalCallbackCount = 0;

struts.Util.makeGlobalCallback = function(target) {
  var name = 'callback_hack_' + struts.Util.globalCallbackCount++;
  window[name] = target;
  return name;
}

struts.Util.setTimeout = function(callback, method, millis) {
  window.setTimeout(callback + "." + method + "()", millis);
}
struts.Util.clearTimeout = function(callback) {
  window.clearTimeout(callback);
}


struts.Util.nextIdValue = 0;

struts.Util.nextId = function(scope) {
  return (scope==null?"id":scope) + struts.Util.nextIdValue++;
}
