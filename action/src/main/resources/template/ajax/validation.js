var webworkValidator = new ValidationClient("$!base/validation");
webworkValidator.onErrors = function(input, errors) {

	var form = input.form;

	clearErrorMessages(form);
	clearErrorLabels(form);

    if (errors.fieldErrors) {
        for (var fieldName in errors.fieldErrors) {
            if (form.elements[fieldName].touched) {
                for (var i = 0; i < errors.fieldErrors[fieldName].length; i++) {
                    addError(form.elements[fieldName], errors.fieldErrors[fieldName][i]);
                }
            }
        }
    }
}

function validate(element) {
    // mark the element as touch
    element.touched = true;
    var namespace = element.form.attributes['namespace'].nodeValue;
    var actionName = element.form.attributes['name'].nodeValue;
	webworkValidator.validate(element, namespace, actionName);
}
