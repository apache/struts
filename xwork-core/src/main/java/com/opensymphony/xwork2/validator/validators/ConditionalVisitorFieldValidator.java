package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;

/**
 * <code>ConditionalVisitorFieldValidator</code>
 *
 *
 * &lt;field name="colleaguePosition"&gt;
 *   &lt;field-validator type="fieldexpression" short-circuit="true"&gt;
 *     reason == 'colleague' and colleaguePositionID == '_CHOOSE_'
 *     &lt;message&gt;You must choose a position where you worked with this person,
 * or choose "Other..."&lt;/message&gt;
 *   &lt;/field-validator&gt;
 *   &lt;field-validator type="conditionalvisitor"&gt;
 *     reason == 'colleague' and colleaguePositionID == 'OTHER'
 *     &lt;message/&gt;
 *   &lt;/field-validator&gt;
 * &lt;/field&gt;
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
        }
        catch (ValidationException e) {
            throw e;
        }
        catch (Exception e) {
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