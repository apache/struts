package org.apache.struts2.dispatcher;

public interface Parameter {

    String getName();

    String getValue();

    boolean isExpression();

    boolean isDefined();

    boolean isMultiple();

    String[] getMultipleValue();

    class Request implements Parameter {

        private final String name;
        private final String[] value;

        public Request(String name, String[] value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getValue() {
            return (value != null && value.length > 0) ? value[0] : null;
        }

        @Override
        public boolean isExpression() {
            return false;
        }

        @Override
        public boolean isDefined() {
            return value != null && value.length > 0;
        }

        @Override
        public boolean isMultiple() {
            return isDefined() && value.length > 1;
        }

        @Override
        public String[] getMultipleValue() {
            return value;
        }
    }

    class EmptyHttpParameter implements Parameter {

        private String name;

        public EmptyHttpParameter(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getValue() {
            return null;
        }

        @Override
        public boolean isExpression() {
            return false;
        }

        @Override
        public boolean isDefined() {
            return false;
        }

        @Override
        public boolean isMultiple() {
            return false;
        }

        @Override
        public String[] getMultipleValue() {
            return new String[0];
        }
    }

}
