/**
 *
 * Common code to interface with the validationServlet 
 *
 */

function ValidationClient(servletUrl) {

	this.servletUrl = servletUrl;

	this.validate = function(input, namespace, actionName) {
		var vc = this;
		var form = input.form;
		var params = new Object();
	    for (var i = 0; i < form.elements.length; i++) {
	        var e = form.elements[i];
            if (e.name != null && e.name != '') {
                params[e.name] = e.value;
            }
        }

		validator.doPost(function(action) {
            if (action) {
                vc.onErrors(input, action);
            }
        }, namespace, actionName, params);
    }
    

	// @param formObject - the form object that triggered the validate call
	// @param errors - a javascript object representing the action errors and field errors
	// client should overwrite this handler to display the new error messages
	this.onErrors = function(inputObject, errors) {
	}
	
	return this;
}
