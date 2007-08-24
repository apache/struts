/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.validate.check");
dojo.require("dojo.validate.common");
dojo.require("dojo.lang.common");
dojo.validate.check = function (form, profile) {
	var missing = [];
	var invalid = [];
	var results = {isSuccessful:function () {
		return (!this.hasInvalid() && !this.hasMissing());
	}, hasMissing:function () {
		return (missing.length > 0);
	}, getMissing:function () {
		return missing;
	}, isMissing:function (elemname) {
		for (var i = 0; i < missing.length; i++) {
			if (elemname == missing[i]) {
				return true;
			}
		}
		return false;
	}, hasInvalid:function () {
		return (invalid.length > 0);
	}, getInvalid:function () {
		return invalid;
	}, isInvalid:function (elemname) {
		for (var i = 0; i < invalid.length; i++) {
			if (elemname == invalid[i]) {
				return true;
			}
		}
		return false;
	}};
	if (profile.trim instanceof Array) {
		for (var i = 0; i < profile.trim.length; i++) {
			var elem = form[profile.trim[i]];
			if (dj_undef("type", elem) || elem.type != "text" && elem.type != "textarea" && elem.type != "password") {
				continue;
			}
			elem.value = elem.value.replace(/(^\s*|\s*$)/g, "");
		}
	}
	if (profile.uppercase instanceof Array) {
		for (var i = 0; i < profile.uppercase.length; i++) {
			var elem = form[profile.uppercase[i]];
			if (dj_undef("type", elem) || elem.type != "text" && elem.type != "textarea" && elem.type != "password") {
				continue;
			}
			elem.value = elem.value.toUpperCase();
		}
	}
	if (profile.lowercase instanceof Array) {
		for (var i = 0; i < profile.lowercase.length; i++) {
			var elem = form[profile.lowercase[i]];
			if (dj_undef("type", elem) || elem.type != "text" && elem.type != "textarea" && elem.type != "password") {
				continue;
			}
			elem.value = elem.value.toLowerCase();
		}
	}
	if (profile.ucfirst instanceof Array) {
		for (var i = 0; i < profile.ucfirst.length; i++) {
			var elem = form[profile.ucfirst[i]];
			if (dj_undef("type", elem) || elem.type != "text" && elem.type != "textarea" && elem.type != "password") {
				continue;
			}
			elem.value = elem.value.replace(/\b\w+\b/g, function (word) {
				return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
			});
		}
	}
	if (profile.digit instanceof Array) {
		for (var i = 0; i < profile.digit.length; i++) {
			var elem = form[profile.digit[i]];
			if (dj_undef("type", elem) || elem.type != "text" && elem.type != "textarea" && elem.type != "password") {
				continue;
			}
			elem.value = elem.value.replace(/\D/g, "");
		}
	}
	if (profile.required instanceof Array) {
		for (var i = 0; i < profile.required.length; i++) {
			if (!dojo.lang.isString(profile.required[i])) {
				continue;
			}
			var elem = form[profile.required[i]];
			if (!dj_undef("type", elem) && (elem.type == "text" || elem.type == "textarea" || elem.type == "password") && /^\s*$/.test(elem.value)) {
				missing[missing.length] = elem.name;
			} else {
				if (!dj_undef("type", elem) && (elem.type == "select-one" || elem.type == "select-multiple") && (elem.selectedIndex == -1 || /^\s*$/.test(elem.options[elem.selectedIndex].value))) {
					missing[missing.length] = elem.name;
				} else {
					if (elem instanceof Array) {
						var checked = false;
						for (var j = 0; j < elem.length; j++) {
							if (elem[j].checked) {
								checked = true;
							}
						}
						if (!checked) {
							missing[missing.length] = elem[0].name;
						}
					}
				}
			}
		}
	}
	if (profile.required instanceof Array) {
		for (var i = 0; i < profile.required.length; i++) {
			if (!dojo.lang.isObject(profile.required[i])) {
				continue;
			}
			var elem, numRequired;
			for (var name in profile.required[i]) {
				elem = form[name];
				numRequired = profile.required[i][name];
			}
			if (elem instanceof Array) {
				var checked = 0;
				for (var j = 0; j < elem.length; j++) {
					if (elem[j].checked) {
						checked++;
					}
				}
				if (checked < numRequired) {
					missing[missing.length] = elem[0].name;
				}
			} else {
				if (!dj_undef("type", elem) && elem.type == "select-multiple") {
					var selected = 0;
					for (var j = 0; j < elem.options.length; j++) {
						if (elem.options[j].selected && !/^\s*$/.test(elem.options[j].value)) {
							selected++;
						}
					}
					if (selected < numRequired) {
						missing[missing.length] = elem.name;
					}
				}
			}
		}
	}
	if (dojo.lang.isObject(profile.dependencies) || dojo.lang.isObject(profile.dependancies)) {
		if (profile["dependancies"]) {
			dojo.deprecated("dojo.validate.check", "profile 'dependancies' is deprecated, please use " + "'dependencies'", "0.5");
			profile.dependencies = profile.dependancies;
		}
		for (name in profile.dependencies) {
			var elem = form[name];
			if (dj_undef("type", elem)) {
				continue;
			}
			if (elem.type != "text" && elem.type != "textarea" && elem.type != "password") {
				continue;
			}
			if (/\S+/.test(elem.value)) {
				continue;
			}
			if (results.isMissing(elem.name)) {
				continue;
			}
			var target = form[profile.dependencies[name]];
			if (target.type != "text" && target.type != "textarea" && target.type != "password") {
				continue;
			}
			if (/^\s*$/.test(target.value)) {
				continue;
			}
			missing[missing.length] = elem.name;
		}
	}
	if (dojo.lang.isObject(profile.constraints)) {
		for (name in profile.constraints) {
			var elem = form[name];
			if (!elem) {
				continue;
			}
			if (!dj_undef("tagName", elem) && (elem.tagName.toLowerCase().indexOf("input") >= 0 || elem.tagName.toLowerCase().indexOf("textarea") >= 0) && /^\s*$/.test(elem.value)) {
				continue;
			}
			var isValid = true;
			if (dojo.lang.isFunction(profile.constraints[name])) {
				isValid = profile.constraints[name](elem.value);
			} else {
				if (dojo.lang.isArray(profile.constraints[name])) {
					if (dojo.lang.isArray(profile.constraints[name][0])) {
						for (var i = 0; i < profile.constraints[name].length; i++) {
							isValid = dojo.validate.evaluateConstraint(profile, profile.constraints[name][i], name, elem);
							if (!isValid) {
								break;
							}
						}
					} else {
						isValid = dojo.validate.evaluateConstraint(profile, profile.constraints[name], name, elem);
					}
				}
			}
			if (!isValid) {
				invalid[invalid.length] = elem.name;
			}
		}
	}
	if (dojo.lang.isObject(profile.confirm)) {
		for (name in profile.confirm) {
			var elem = form[name];
			var target = form[profile.confirm[name]];
			if (dj_undef("type", elem) || dj_undef("type", target) || (elem.type != "text" && elem.type != "textarea" && elem.type != "password") || (target.type != elem.type) || (target.value == elem.value) || (results.isInvalid(elem.name)) || (/^\s*$/.test(target.value))) {
				continue;
			}
			invalid[invalid.length] = elem.name;
		}
	}
	return results;
};
dojo.validate.evaluateConstraint = function (profile, constraint, fieldName, elem) {
	var isValidSomething = constraint[0];
	var params = constraint.slice(1);
	params.unshift(elem.value);
	if (typeof isValidSomething != "undefined") {
		return isValidSomething.apply(null, params);
	}
	return false;
};

