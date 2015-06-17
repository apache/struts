package com.opensymphony.xwork2.ognl;

/**
 * Helper class to build error messages.
 */
public class ErrorMessageBuilder {

    private StringBuilder message = new StringBuilder();

    public static ErrorMessageBuilder create() {
        return new ErrorMessageBuilder();
    }

    private ErrorMessageBuilder() {
    }

    public ErrorMessageBuilder errorSettingExpressionWithValue(String expr, Object value) {
        appenExpression(expr);
        if (value instanceof Object[]) {
            appendValueAsArray((Object[]) value, message);
        } else {
            appendValue(value);
        }
        return this;
    }

    private void appenExpression(String expr) {
        message.append("Error setting expression '");
        message.append(expr);
        message.append("' with value ");
    }

    private void appendValue(Object value) {
        message.append("'");
        message.append(value);
        message.append("'");
    }

    private void appendValueAsArray(Object[] valueArray, StringBuilder msg) {
        msg.append("[");
        for (int index = 0; index < valueArray.length; index++) {
            appendValue(valueArray[index]);
            if (hasMoreElements(valueArray, index)) {
                msg.append(", ");
            }
        }
        msg.append("]");
    }

    private boolean hasMoreElements(Object[] valueArray, int index) {
        return index < (valueArray.length + 1);
    }

    public String build() {
        return message.toString();
    }

}
