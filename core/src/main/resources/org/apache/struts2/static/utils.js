var StrutsUtils = {};

// gets an object with validation errors from string returned by 
// the ajaxValidation interceptor
StrutsUtils.getValidationErrors = function(data) {
  if(data.indexOf("/* {") == 0) {
    return eval("( " + data.substring(2, data.length - 2) + " )");
  } else {
    return null;
  }  
};

StrutsUtils.clearValidationErrors = function(form) {
  var firstNode = StrutsUtils.firstElement(form);
  var xhtml = firstNode.tagName.toLowerCase() == "table";
  
  if(xhtml) {
    clearErrorMessagesXHTML(form);
    clearErrorLabelsXHTML(form);
  } else {
    clearErrorMessagesCSS(form);
    clearErrorLabelsCSS(form);
  }
};  

// shows validation errors using functions from xhtml/validation.js
// or css_xhtml/validation.js
StrutsUtils.showValidationErrors = function(form, errors) {
  StrutsUtils.clearValidationErrors(form, errors);

  var firstNode = StrutsUtils.firstElement(form);
  var xhtml = firstNode.tagName.toLowerCase() == "table";  
  if(errors.fieldErrors) {
    for(var fieldName in errors.fieldErrors) {
      for(var i = 0; i < errors.fieldErrors[fieldName].length; i++) {
        if(xhtml) {
          addErrorXHTML(form.elements[fieldName], errors.fieldErrors[fieldName][i]);
        } else {
          addErrorCSS(form.elements[fieldName], errors.fieldErrors[fieldName][i]);
        }  
      }
    }
  }
};

StrutsUtils.firstElement  = function(parentNode, tagName) {
  var node = parentNode.firstChild;
  while(node && node.nodeType != 1){
    node = node.nextSibling;
  }
  if(tagName && node && node.tagName && node.tagName.toLowerCase() != tagName.toLowerCase()) {
    node = StrutsUtils.nextElement(node, tagName);
  }
  return node;  
};

StrutsUtils.nextElement = function(node, tagName) {
  if(!node) { return null; }
  do {
    node = node.nextSibling;
  } while(node && node.nodeType != 1);

  if(node && tagName && tagName.toLowerCase() != node.tagName.toLowerCase()) {
    return StrutsUtils.nextElement(node, tagName);
  }
  return node;  
}

StrutsUtils.previousElement = function(node, tagName) {
  if(!node) { return null; }
  if(tagName) { tagName = tagName.toLowerCase(); }
  do {
    node = node.previousSibling;
  } while(node && node.nodeType != 1);
  
  if(node && tagName && tagName.toLowerCase() != node.tagName.toLowerCase()) {
    return StrutsUtils.previousElement(node, tagName);
  }
  return node;  
}
