/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.validator.annotations.ConditionalVisitorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.DateRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.DoubleRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.EmailValidator;
import com.opensymphony.xwork2.validator.annotations.ExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.FieldExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.IntRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.ShortRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;
import com.opensymphony.xwork2.validator.annotations.UrlValidator;
import com.opensymphony.xwork2.validator.annotations.Validation;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;
import com.opensymphony.xwork2.validator.annotations.Validations;
import com.opensymphony.xwork2.validator.annotations.VisitorFieldValidator;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <code>AnnotationValidationConfigurationBuilder</code>
 *
 * @author Rainer Hermanns
 * @author jepjep
 * @version $Id$
 */
public class AnnotationValidationConfigurationBuilder {

    private static final Pattern SETTER_PATTERN = Pattern.compile("set([A-Z][A-Za-z0-9]*)$");
    private static final Pattern GETTER_PATTERN = Pattern.compile("(get|is|has)([A-Z][A-Za-z0-9]*)$");

    private ValidatorFactory validatorFactory;

    public AnnotationValidationConfigurationBuilder(ValidatorFactory fac) {
        this.validatorFactory = fac;
    }

    private List<ValidatorConfig> processAnnotations(Object o) {

        List<ValidatorConfig> result = new ArrayList<ValidatorConfig>();

        String fieldName = null;
        String methodName = null;

        Annotation[] annotations = null;

        if (o instanceof Class) {
            Class clazz = (Class) o;
            annotations = clazz.getAnnotations();
        }

        if (o instanceof Method) {
            Method method = (Method) o;
            fieldName = resolvePropertyName(method);
            methodName = method.getName();

            annotations = method.getAnnotations();
        }

        if (annotations != null) {
            for (Annotation a : annotations) {

                // Process collection of custom validations
                if (a instanceof Validations) {
                    processValidationAnnotation(a, fieldName, methodName, result);

                }

                // Process single custom validator
                if (a instanceof Validation) {
                    Validation v = (Validation) a;
                    if (v.validations() != null) {
                        for (Validations val : v.validations()) {
                            processValidationAnnotation(val, fieldName, methodName, result);
                        }
                    }
                }
                // Process single custom validator
                else if (a instanceof ExpressionValidator) {
                    ExpressionValidator v = (ExpressionValidator) a;
                    ValidatorConfig temp = processExpressionValidatorAnnotation(v, fieldName, methodName);
                    if (temp != null) {
                        result.add(temp);
                    }
                }
                // Process single custom validator
                else if (a instanceof CustomValidator) {
                    CustomValidator v = (CustomValidator) a;
                    ValidatorConfig temp = processCustomValidatorAnnotation(v, fieldName, methodName);
                    if (temp != null) {
                        result.add(temp);
                    }
                }
                // Process ConversionErrorFieldValidator
                else if (a instanceof ConversionErrorFieldValidator) {
                    ConversionErrorFieldValidator v = (ConversionErrorFieldValidator) a;
                    ValidatorConfig temp = processConversionErrorFieldValidatorAnnotation(v, fieldName, methodName);
                    if (temp != null) {
                        result.add(temp);
                    }

                }
                // Process DateRangeFieldValidator
                else if (a instanceof DateRangeFieldValidator) {
                    DateRangeFieldValidator v = (DateRangeFieldValidator) a;
                    ValidatorConfig temp = processDateRangeFieldValidatorAnnotation(v, fieldName, methodName);
                    if (temp != null) {
                        result.add(temp);
                    }

                }
                // Process EmailValidator
                else if (a instanceof EmailValidator) {
                    EmailValidator v = (EmailValidator) a;
                    ValidatorConfig temp = processEmailValidatorAnnotation(v, fieldName, methodName);
                    if (temp != null) {
                        result.add(temp);
                    }
                }
                // Process FieldExpressionValidator
                else if (a instanceof FieldExpressionValidator) {
                    FieldExpressionValidator v = (FieldExpressionValidator) a;
                    ValidatorConfig temp = processFieldExpressionValidatorAnnotation(v, fieldName, methodName);
                    if (temp != null) {
                        result.add(temp);
                    }
                }
                // Process IntRangeFieldValidator
                else if (a instanceof IntRangeFieldValidator) {
                    IntRangeFieldValidator v = (IntRangeFieldValidator) a;
                    ValidatorConfig temp = processIntRangeFieldValidatorAnnotation(v, fieldName, methodName);
                    if (temp != null) {
                        result.add(temp);
                    }
                }
                // Process ShortRangeFieldValidator
                else if (a instanceof ShortRangeFieldValidator) {
                    ShortRangeFieldValidator v = (ShortRangeFieldValidator) a;
                    ValidatorConfig temp = processShortRangeFieldValidatorAnnotation(v, fieldName, methodName);
                    if (temp != null) {
                        result.add(temp);
                    }
                }
                // Process DoubleRangeFieldValidator
                else if (a instanceof DoubleRangeFieldValidator) {
                    DoubleRangeFieldValidator v = (DoubleRangeFieldValidator) a;
                    ValidatorConfig temp = processDoubleRangeFieldValidatorAnnotation(v, fieldName, methodName);
                    if (temp != null) {
                        result.add(temp);
                    }
                }
                // Process RequiredFieldValidator
                else if (a instanceof RequiredFieldValidator) {
                    RequiredFieldValidator v = (RequiredFieldValidator) a;
                    ValidatorConfig temp = processRequiredFieldValidatorAnnotation(v, fieldName, methodName);
                    if (temp != null) {
                        result.add(temp);
                    }
                }
                // Process RequiredStringValidator
                else if (a instanceof RequiredStringValidator) {
                    RequiredStringValidator v = (RequiredStringValidator) a;
                    ValidatorConfig temp = processRequiredStringValidatorAnnotation(v, fieldName, methodName);
                    if (temp != null) {
                        result.add(temp);
                    }
                }
                // Process StringLengthFieldValidator
                else if (a instanceof StringLengthFieldValidator) {
                    StringLengthFieldValidator v = (StringLengthFieldValidator) a;
                    ValidatorConfig temp = processStringLengthFieldValidatorAnnotation(v, fieldName, methodName);
                    if (temp != null) {
                        result.add(temp);
                    }
                }
                // Process UrlValidator
                else if (a instanceof UrlValidator) {
                    UrlValidator v = (UrlValidator) a;
                    ValidatorConfig temp = processUrlValidatorAnnotation(v, fieldName, methodName);
                    if (temp != null) {
                        result.add(temp);
                    }

                }
                // Process ConditionalVisitorFieldValidator
                else if (a instanceof ConditionalVisitorFieldValidator) {
                    ConditionalVisitorFieldValidator v = (ConditionalVisitorFieldValidator) a;
                    ValidatorConfig temp = processConditionalVisitorFieldValidatorAnnotation(v, fieldName, methodName);
                    if (temp != null) {
                        result.add(temp);
                    }
                }
                // Process VisitorFieldValidator
                else if (a instanceof VisitorFieldValidator) {
                    VisitorFieldValidator v = (VisitorFieldValidator) a;
                    ValidatorConfig temp = processVisitorFieldValidatorAnnotation(v, fieldName, methodName);
                    if (temp != null) {
                        result.add(temp);
                    }
                }
                // Process RegexFieldValidator
                else if (a instanceof RegexFieldValidator) {
                    RegexFieldValidator v = (RegexFieldValidator) a;
                    ValidatorConfig temp = processRegexFieldValidatorAnnotation(v, fieldName, methodName);
                    if (temp != null) {
                        result.add(temp);
                    }
                }
            }
        }
        return result;
    }

    private void processValidationAnnotation(Annotation a, String fieldName, String methodName, List<ValidatorConfig> result) {
        Validations validations = (Validations) a;
        CustomValidator[] cv = validations.customValidators();
        if (cv != null) {
            for (CustomValidator v : cv) {
                ValidatorConfig temp = processCustomValidatorAnnotation(v, fieldName, methodName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        ExpressionValidator[] ev = validations.expressions();
        if (ev != null) {
            for (ExpressionValidator v : ev) {
                ValidatorConfig temp = processExpressionValidatorAnnotation(v, fieldName, methodName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        ConversionErrorFieldValidator[] cef = validations.conversionErrorFields();
        if (cef != null) {
            for (ConversionErrorFieldValidator v : cef) {
                ValidatorConfig temp = processConversionErrorFieldValidatorAnnotation(v, fieldName, methodName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        DateRangeFieldValidator[] drfv = validations.dateRangeFields();
        if (drfv != null) {
            for (DateRangeFieldValidator v : drfv) {
                ValidatorConfig temp = processDateRangeFieldValidatorAnnotation(v, fieldName, methodName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        EmailValidator[] emv = validations.emails();
        if (emv != null) {
            for (EmailValidator v : emv) {
                ValidatorConfig temp = processEmailValidatorAnnotation(v, fieldName, methodName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        FieldExpressionValidator[] fev = validations.fieldExpressions();
        if (fev != null) {
            for (FieldExpressionValidator v : fev) {
                ValidatorConfig temp = processFieldExpressionValidatorAnnotation(v, fieldName, methodName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        IntRangeFieldValidator[] irfv = validations.intRangeFields();
        if (irfv != null) {
            for (IntRangeFieldValidator v : irfv) {
                ValidatorConfig temp = processIntRangeFieldValidatorAnnotation(v, fieldName, methodName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        RegexFieldValidator[] rfv = validations.regexFields();
        if (rfv != null) {
            for (RegexFieldValidator v : rfv) {
                ValidatorConfig temp = processRegexFieldValidatorAnnotation(v, fieldName, methodName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        RequiredFieldValidator[] rv = validations.requiredFields();
        if (rv != null) {
            for (RequiredFieldValidator v : rv) {
                ValidatorConfig temp = processRequiredFieldValidatorAnnotation(v, fieldName, methodName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        RequiredStringValidator[] rsv = validations.requiredStrings();
        if (rsv != null) {
            for (RequiredStringValidator v : rsv) {
                ValidatorConfig temp = processRequiredStringValidatorAnnotation(v, fieldName, methodName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        StringLengthFieldValidator[] slfv = validations.stringLengthFields();
        if (slfv != null) {
            for (StringLengthFieldValidator v : slfv) {
                ValidatorConfig temp = processStringLengthFieldValidatorAnnotation(v, fieldName, methodName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        UrlValidator[] uv = validations.urls();
        if (uv != null) {
            for (UrlValidator v : uv) {
                ValidatorConfig temp = processUrlValidatorAnnotation(v, fieldName, methodName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        ConditionalVisitorFieldValidator[] cvfv = validations.conditionalVisitorFields();
        if (cvfv != null) {
            for (ConditionalVisitorFieldValidator v : cvfv) {
                ValidatorConfig temp = processConditionalVisitorFieldValidatorAnnotation(v, fieldName, methodName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        VisitorFieldValidator[] vfv = validations.visitorFields();
        if (vfv != null) {
            for (VisitorFieldValidator v : vfv) {
                ValidatorConfig temp = processVisitorFieldValidatorAnnotation(v, fieldName, methodName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
    }

    private ValidatorConfig processExpressionValidatorAnnotation(ExpressionValidator v, String fieldName, String methodName) {
        String validatorType = "expression";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        }

        params.put("expression", v.expression());

        validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType)
                .addParams(params)
                .addParam("methodName", methodName)
                .shortCircuit(v.shortCircuit())
                .defaultMessage(v.message())
                .messageKey(v.key())
                .messageParams(v.messageParams())
                .build();
    }

    private ValidatorConfig processCustomValidatorAnnotation(CustomValidator v, String fieldName, String methodName) {

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0) {
            params.put("fieldName", v.fieldName());
        }


        String validatorType = v.type();

        validatorFactory.lookupRegisteredValidatorType(validatorType);

        Annotation[] recursedAnnotations = v.parameters();

        if (recursedAnnotations != null) {
            for (Annotation a2 : recursedAnnotations) {

                if (a2 instanceof ValidationParameter) {

                    ValidationParameter parameter = (ValidationParameter) a2;
                    String parameterName = parameter.name();
                    String parameterValue = parameter.value();
                    params.put(parameterName, parameterValue);
                }

            }
        }

        return new ValidatorConfig.Builder(validatorType)
                .addParams(params)
                .addParam("methodName", methodName)
                .shortCircuit(v.shortCircuit())
                .defaultMessage(v.message())
                .messageKey(v.key())
                .messageParams(v.messageParams())
                .build();
    }

    private ValidatorConfig processRegexFieldValidatorAnnotation(RegexFieldValidator v, String fieldName, String methodName) {
        String validatorType = "regex";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0) {
            params.put("fieldName", v.fieldName());
        }

        params.put("regex", v.regex());
        params.put("regexExpression", v.regexExpression());

        validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType)
                .addParams(params)
                .addParam("methodName", methodName)
                .addParam("trim", v.trim())
                .addParam("trimExpression", v.trimExpression())
                .addParam("caseSensitive", v.caseSensitive())
                .addParam("caseSensitiveExpression", v.caseSensitiveExpression())
                .shortCircuit(v.shortCircuit())
                .defaultMessage(v.message())
                .messageKey(v.key())
                .messageParams(v.messageParams())
                .build();
    }

    private ValidatorConfig processConditionalVisitorFieldValidatorAnnotation(ConditionalVisitorFieldValidator v, String fieldName, String methodName) {
        String validatorType = "conditionalvisitor";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0) {
            params.put("fieldName", v.fieldName());
        }

        params.put("expression", v.expression());
        params.put("context", v.context());
        params.put("appendPrefix", String.valueOf(v.appendPrefix()));

        validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType)
                .addParams(params)
                .addParam("methodName", methodName)
                .shortCircuit(v.shortCircuit())
                .defaultMessage(v.message())
                .messageKey(v.key())
                .messageParams(v.messageParams())
                .build();
    }


    private ValidatorConfig processVisitorFieldValidatorAnnotation(VisitorFieldValidator v, String fieldName, String methodName) {
        String validatorType = "visitor";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0) {
            params.put("fieldName", v.fieldName());
        }

        params.put("context", v.context());
        params.put("appendPrefix", String.valueOf(v.appendPrefix()));

        validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType)
                .addParams(params)
                .addParam("methodName", methodName)
                .shortCircuit(v.shortCircuit())
                .defaultMessage(v.message())
                .messageKey(v.key())
                .messageParams(v.messageParams())
                .build();
    }

    private ValidatorConfig processUrlValidatorAnnotation(UrlValidator v, String fieldName, String methodName) {
        String validatorType = "url";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0) {
            params.put("fieldName", v.fieldName());
        }
        if (StringUtils.isNotEmpty(v.urlRegex())) {
            params.put("urlRegex", v.urlRegex());
        }
        if (StringUtils.isNotEmpty(v.urlRegexExpression())) {
            params.put("urlRegexExpression", v.urlRegexExpression());
        }

        validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType)
                .addParams(params)
                .addParam("methodName", methodName)
                .shortCircuit(v.shortCircuit())
                .defaultMessage(v.message())
                .messageKey(v.key())
                .messageParams(v.messageParams())
                .build();
    }

    private ValidatorConfig processStringLengthFieldValidatorAnnotation(StringLengthFieldValidator v, String fieldName, String methodName) {
        String validatorType = "stringlength";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (StringUtils.isNotEmpty(v.fieldName())) {
            params.put("fieldName", v.fieldName());
        }

        if (StringUtils.isNotEmpty(v.maxLength())) {
            params.put("maxLength", v.maxLength());
        }
        if (StringUtils.isNotEmpty(v.minLength())) {
            params.put("minLength", v.minLength());
        }
        if (StringUtils.isNotEmpty(v.maxLengthExpression())) {
            params.put("maxLengthExpression", v.maxLengthExpression());
        }
        if (StringUtils.isNotEmpty(v.minLengthExpression())) {
            params.put("minLengthExpression", v.minLengthExpression());
        }
        if (StringUtils.isNotEmpty(v.trimExpression())){
            params.put("trimExpression", v.trimExpression());
        } else {
            params.put("trim", String.valueOf(v.trim()));
        }
        validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType)
                .addParams(params)
                .addParam("methodName", methodName)
                .shortCircuit(v.shortCircuit())
                .defaultMessage(v.message())
                .messageKey(v.key())
                .messageParams(v.messageParams())
                .build();
    }

    private Date parseDateString(String value, String format) {

        SimpleDateFormat d0 = null;
        if (StringUtils.isNotEmpty(format)) {
            d0 = new SimpleDateFormat(format);
        }
        SimpleDateFormat d1 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, Locale.getDefault());
        SimpleDateFormat d2 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.getDefault());
        SimpleDateFormat d3 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        SimpleDateFormat[] dfs = (d0 != null ? new SimpleDateFormat[]{d0, d1, d2, d3} : new SimpleDateFormat[]{d1, d2, d3});
        for (SimpleDateFormat df : dfs)
            try {
                Date check = df.parse(value);
                if (check != null) {
                    return check;
                }
            } catch (ParseException ignore) {
            }
        return null;
    }

    private ValidatorConfig processRequiredStringValidatorAnnotation(RequiredStringValidator v, String fieldName, String methodName) {
        String validatorType = "requiredstring";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0) {
            params.put("fieldName", v.fieldName());
        }

        params.put("trim", String.valueOf(v.trim()));

        validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType)
                .addParams(params)
                .addParam("methodName", methodName)
                .shortCircuit(v.shortCircuit())
                .defaultMessage(v.message())
                .messageParams(v.messageParams())
                .messageKey(v.key())
                .build();
    }

    private ValidatorConfig processRequiredFieldValidatorAnnotation(RequiredFieldValidator v, String fieldName, String methodName) {
        String validatorType = "required";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0) {
            params.put("fieldName", v.fieldName());
        }

        validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType)
                .addParams(params)
                .addParam("methodName", methodName)
                .shortCircuit(v.shortCircuit())
                .defaultMessage(v.message())
                .messageKey(v.key())
                .messageParams(v.messageParams())
                .build();
    }

    private ValidatorConfig processIntRangeFieldValidatorAnnotation(IntRangeFieldValidator v, String fieldName, String methodName) {
        String validatorType = "int";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0) {
            params.put("fieldName", v.fieldName());
        }

        if (v.min() != null && v.min().length() > 0) {
            params.put("min", v.min());
        }
        if (v.max() != null && v.max().length() > 0) {
            params.put("max", v.max());
        }
        if (StringUtils.isNotEmpty(v.maxExpression())) {
            params.put("maxExpression", v.maxExpression());
        }
        if (StringUtils.isNotEmpty(v.minExpression())) {
            params.put("minExpression", v.minExpression());
        }

        validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType)
                .addParams(params)
                .addParam("methodName", methodName)
                .shortCircuit(v.shortCircuit())
                .defaultMessage(v.message())
                .messageKey(v.key())
                .messageParams(v.messageParams())
                .build();
    }

    private ValidatorConfig processShortRangeFieldValidatorAnnotation(ShortRangeFieldValidator v, String fieldName, String methodName) {
        String validatorType = "short";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0) {
            params.put("fieldName", v.fieldName());
        }

        if (StringUtils.isNotEmpty(v.min())) {
            params.put("min", v.min());
        }
        if (StringUtils.isNotEmpty(v.max())) {
            params.put("max", v.max());
        }
        if (StringUtils.isNotEmpty(v.maxExpression())) {
            params.put("maxExpression", v.maxExpression());
        }
        if (StringUtils.isNotEmpty(v.minExpression())) {
            params.put("minExpression", v.minExpression());
        }

        validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType)
                .addParams(params)
                .addParam("methodName", methodName)
                .shortCircuit(v.shortCircuit())
                .defaultMessage(v.message())
                .messageKey(v.key())
                .messageParams(v.messageParams())
                .build();
    }

    private ValidatorConfig processDoubleRangeFieldValidatorAnnotation(DoubleRangeFieldValidator v, String fieldName, String methodName) {
        String validatorType = "double";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0) {
            params.put("fieldName", v.fieldName());
        }

        if (v.minInclusive() != null && v.minInclusive().length() > 0) {
            params.put("minInclusive", v.minInclusive());
        }
        if (v.maxInclusive() != null && v.maxInclusive().length() > 0) {
            params.put("maxInclusive", v.maxInclusive());
        }

        if (v.minExclusive() != null && v.minExclusive().length() > 0) {
            params.put("minExclusive", v.minExclusive());
        }
        if (v.maxExclusive() != null && v.maxExclusive().length() > 0) {
            params.put("maxExclusive", v.maxExclusive());
        }

        if (StringUtils.isNotEmpty(v.minInclusiveExpression())) {
            params.put("minInclusiveExpression", v.minInclusiveExpression());
        }
        if (StringUtils.isNotEmpty(v.maxInclusiveExpression())) {
            params.put("maxInclusiveExpression", v.maxInclusiveExpression());
        }

        if (StringUtils.isNotEmpty(v.minExclusiveExpression())) {
            params.put("minExclusiveExpression", v.minExclusiveExpression());
        }
        if (StringUtils.isNotEmpty(v.maxExclusiveExpression())) {
            params.put("maxExclusiveExpression", v.maxExclusiveExpression());
        }

        validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType)
                .addParams(params)
                .addParam("methodName", methodName)
                .shortCircuit(v.shortCircuit())
                .defaultMessage(v.message())
                .messageKey(v.key())
                .messageParams(v.messageParams())
                .build();
    }

    private ValidatorConfig processFieldExpressionValidatorAnnotation(FieldExpressionValidator v, String fieldName, String methodName) {
        String validatorType = "fieldexpression";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0) {
            params.put("fieldName", v.fieldName());
        }

        params.put("expression", v.expression());

        validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType)
                .addParams(params)
                .addParam("methodName", methodName)
                .shortCircuit(v.shortCircuit())
                .defaultMessage(v.message())
                .messageKey(v.key())
                .messageParams(v.messageParams())
                .build();
    }

    private ValidatorConfig processEmailValidatorAnnotation(EmailValidator v, String fieldName, String methodName) {
        String validatorType = "email";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0) {
            params.put("fieldName", v.fieldName());
        }

        validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType)
                .addParams(params)
                .addParam("methodName", methodName)
                .shortCircuit(v.shortCircuit())
                .defaultMessage(v.message())
                .messageKey(v.key())
                .messageParams(v.messageParams())
                .build();
    }

    private ValidatorConfig processDateRangeFieldValidatorAnnotation(DateRangeFieldValidator v, String fieldName, String methodName) {
        String validatorType = "date";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0) {
            params.put("fieldName", v.fieldName());
        }
        if (v.min() != null && v.min().length() > 0) {
            final Date minDate = parseDateString(v.min(), v.dateFormat());
            params.put("min", minDate == null ? v.min() : minDate);
        }
        if (v.max() != null && v.max().length() > 0) {
            final Date maxDate = parseDateString(v.max(), v.dateFormat());
            params.put("max", maxDate == null ? v.max() : maxDate);
        }

        if (StringUtils.isNotEmpty(v.minExpression())) {
            params.put("minExpression", v.minExpression());
        }
        if (StringUtils.isNotEmpty(v.maxExpression())) {
            params.put("maxExpression", v.maxExpression());
        }

        validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType)
                .addParams(params)
                .addParam("methodName", methodName)
                .shortCircuit(v.shortCircuit())
                .defaultMessage(v.message())
                .messageKey(v.key())
                .messageParams(v.messageParams())
                .build();
    }

    private ValidatorConfig processConversionErrorFieldValidatorAnnotation(ConversionErrorFieldValidator v, String fieldName, String methodName) {
        String validatorType = "conversion";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0) {
            params.put("fieldName", v.fieldName());
        }

        validatorFactory.lookupRegisteredValidatorType(validatorType);
        return new ValidatorConfig.Builder(validatorType)
                .addParams(params)
                .addParam("methodName", methodName)
                .addParam("repopulateField", v.repopulateField())
                .shortCircuit(v.shortCircuit())
                .defaultMessage(v.message())
                .messageKey(v.key())
                .messageParams(v.messageParams())
                .build();
    }

    public List<ValidatorConfig> buildAnnotationClassValidatorConfigs(Class aClass) {

        List<ValidatorConfig> result = new ArrayList<ValidatorConfig>();

        List<ValidatorConfig> temp = processAnnotations(aClass);
        if (temp != null) {
            result.addAll(temp);
        }

        Method[] methods = aClass.getDeclaredMethods();

        if (methods != null) {
            for (Method method : methods) {
                temp = processAnnotations(method);
                if (temp != null) {
                    result.addAll(temp);
                }
            }
        }

        return result;

    }

    /**
     * Returns the property name for a method.
     * This method is independant from property fields.
     *
     * @param method The method to get the property name for.
     * @return the property name for given method; null if non could be resolved.
     */
    public String resolvePropertyName(Method method) {

        Matcher matcher = SETTER_PATTERN.matcher(method.getName());
        if (matcher.matches() && method.getParameterTypes().length == 1) {
            String raw = matcher.group(1);
            return raw.substring(0, 1).toLowerCase() + raw.substring(1);
        }

        matcher = GETTER_PATTERN.matcher(method.getName());
        if (matcher.matches() && method.getParameterTypes().length == 0) {
            String raw = matcher.group(2);
            return raw.substring(0, 1).toLowerCase() + raw.substring(1);
        }

        return null;
    }

}
