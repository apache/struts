package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;

/**
 * <!-- START SNIPPET: javadoc -->
 * The ConditionalVisitorFieldValidator will forward validation to the VisitorFieldValidator
 * only if the expression will evaluate to true.
 * <!-- END SNIPPET: javadoc -->
 *
 * <!-- START SNIPPET: parameters -->
 * <ul>
 *     <li>expression - an OGNL expression which should evaluate to true to pass validation to the VisitorFieldValidator</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;field name="colleaguePosition"&gt;
 *     &lt;field-validator type="conditionalvisitor"&gt;
 *         &lt;param name="expression"&gt;reason == 'colleague' and colleaguePositionID == 'OTHER'&lt;/param&gt;
 *         &lt;message&gt;You must select reason Colleague and position Other&lt;/message&gt;
 *     &lt;/field-validator&gt;
 * &lt;/field&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Matt Raible
 */
public class ConditionalVisitorFieldValidator extends VisitorFieldValidator {

    private String expression;

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    /**
     * If expression evaluates to true, invoke visitor validation.
     *
     * @param object the object being validated
     * @throws ValidationException
     */
    @Override
    public void validate(Object object) throws ValidationException {
        if (validateExpression(object)) {
            super.validate(object);
        }
    }

    /**
     * Validate the expression contained in the "expression" paramter.
     *
     * @param object the object you're validating
     * @return true if expression evaluates to true (implying a validation
     *         failure)
     * @throws ValidationException if anything goes wrong
     */
    public boolean validateExpression(Object object) throws ValidationException {
        Boolean answer = Boolean.FALSE;
        Object obj = null;

        try {
            obj = getFieldValue(expression, object);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            // let this pass, but it will be logged right below
        }

        if ((obj != null) && (obj instanceof Boolean)) {
            answer = (Boolean) obj;
        } else {
            log.warn("Got result of " + obj + " when trying to get Boolean.");
        }

        return answer;
    }

} 