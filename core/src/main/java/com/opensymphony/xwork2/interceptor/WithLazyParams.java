package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.TextParser;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;

import java.util.Map;

/**
 * Interceptors marked with this interface won't be fully initialised during initialisation.
 * Appropriated params will be injected just before usage of the interceptor.
 *
 * Please be aware that in such case {@link Interceptor#init()} method must be prepared for this.
 *
 * @since 2.5.9
 */
public interface WithLazyParams {

    class LazyParamInjector {

        protected OgnlUtil ognlUtil;
        protected TextParser textParser;
        protected ReflectionProvider reflectionProvider;

        private final TextParseUtil.ParsedValueEvaluator valueEvaluator;

        public LazyParamInjector(final ValueStack valueStack) {
            valueEvaluator = new TextParseUtil.ParsedValueEvaluator() {
                public Object evaluate(String parsedValue) {
                    return valueStack.findValue(parsedValue); // no asType !!!
                }
            };
        }

        @Inject
        public void setTextParser(TextParser textParser) {
            this.textParser = textParser;
        }

        @Inject
        public void setReflectionProvider(ReflectionProvider reflectionProvider) {
            this.reflectionProvider = reflectionProvider;
        }

        @Inject
        public void setOgnlUtil(OgnlUtil ognlUtil) {
            this.ognlUtil = ognlUtil;
        }

        public Interceptor injectParams(Interceptor interceptor, Map<String, String> params, ActionContext invocationContext) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                Object paramValue = textParser.evaluate(new char[]{ '$' }, entry.getValue(), valueEvaluator, TextParser.DEFAULT_LOOP_COUNT);
                ognlUtil.setProperty(entry.getKey(), paramValue, interceptor, invocationContext.getContextMap());
            }

            return interceptor;
        }
    }
}
