/**
 *
 */
package org.apache.struts2.components;

/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.Currency;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p/>
 * Format Number object in different ways.
 * <p>
 * The number tag will allow you to format a Number in a quick and easy way,
 * based on the java.text.NumberFormat class. There are four basic number types,
 * a number, a currency, a percentage and an integer. If a currency is
 * specified, the number format will match the given currency. Further
 * parameters can be overridden as needed.
 * <p/>
 * If a type is not defined, it will finally fall back to the default
 * NumberFormat.getNumberInstance() formatting.
 * <p/>
 * <b>Note</b>: If the requested Number object isn't found on the stack, a blank
 * will be returned.
 * </p>
 * <p/>
 * Configurable attributes are :-
 * <ul>
 * <li>name</li>
 * <li>currency - you can specify your own currency or as an OGNL expression</li>
 * <li>type - if not specified try to find base on struts.number.format property</li>
 * <li>groupingUsed - see NumberFormat.isGroupingUsed</li>
 * <li>maximumFractionDigits - see NumberFormat.setMaximumFractionDigits</li>
 * <li>maximumIntegerDigits - see NumberFormat.setMaximumIntegerDigits</li>
 * <li>minimumFractionDigits - see NumberFormat.setMinimumFractionDigits</li>
 * <li>minimumIntegerDigits - see NumberFormat.setMinimumIntegerDigits</li>
 * <li>parseIntegerOnly - see NumberFormat.isParseIntegerOnly</li>
 * <li>roundingMode - see below</li>
 * </ul>
 * <p/>
 * <p/>
 * <p/>
 * Possible values for rounding mode are :-
 * <ul>
 * <li>ceiling</li>
 * <li>down</li>
 * <li>floor</li>
 * <li>half-down</li>
 * <li>half-even</li>
 * <li>half-up</li>
 * <li>unnecessary</li>
 * <li>up</li>
 * </ul>
 * <p/>
 * <p/>
 * <p/>
 * <!-- END SNIPPET: javadoc -->
 * <p/>
 * <p/>
 * <b>Examples</b>
 * <p/>
 * <pre>
 *  &lt;!-- START SNIPPET: example --&gt;
 *  &lt;s:number name=&quot;invoice.total&quot; type=&quot;currency&quot; currency=&quot;XYZ&quot; /&gt;
 *  &lt;s:number name=&quot;invoice.quantity&quot; type=&quot;number&quot; /&gt;
 *  &lt;s:number name=&quot;invoice.discount&quot; type=&quot;percentage&quot; /&gt;
 *  &lt;s:number name=&quot;invoice.terms&quot; type=&quot;integer&quot; /&gt;
 *  &lt;!-- END SNIPPET: example --&gt;
 * </pre>
 * <p/>
 * <code>Number</code>
 */
@StrutsTag(name = "number", tldBodyContent = "empty", tldTagClass = "org.apache.struts2.views.jsp.NumberTag", description = "Render a formatted number.")
public class Number extends ContextBean {

    private static final Logger LOG = LoggerFactory.getLogger(Number.class);
    /**
     * Property name to fall back when no format is specified
     */
    public static final String NUMBERTAG_PROPERTY = "struts.number.format";

    private String name;

    private String currency;

    private String type;

    private Boolean groupingUsed;

    private Integer maximumFractionDigits;

    private Integer maximumIntegerDigits;

    private Integer minimumFractionDigits;

    private Integer minimumIntegerDigits;

    private Boolean parseIntegerOnly;

    private String roundingMode;

    public Number(ValueStack stack) {
        super(stack);
    }

    public boolean end(Writer writer, String body) {
        java.lang.Number number = findNumberName();

        if (number != null) {

            NumberFormat format = getNumberFormat();
            findCurrency(format);
            setNumberFormatParameters(format);
            setRoundingMode(format);

            String msg = format.format(number);
            if (msg != null) {
                try {
                    if (getVar() == null) {
                        writer.write(msg);
                    } else {
                        putInContext(msg);
                    }
                } catch (IOException e) {
                    LOG.error("Could not write out Number tag", e);
                }
            }
        }
        return super.end(writer, "");
    }

    // try to find the currency, percentage and integer on the stack
    private void findCurrency(NumberFormat format) {
        if (currency != null) {
            Object currencyValue = findValue(currency);
            if (currencyValue != null) {
                currency = currencyValue.toString();
            }
            try {
                format.setCurrency(Currency.getInstance(currency));
            } catch (IllegalArgumentException iae) {
                LOG.error("Could not recognise a currency of [" + currency + "]");
            }
        }
    }

    private void setNumberFormatParameters(NumberFormat format) {
        if (groupingUsed != null) {
            format.setGroupingUsed(groupingUsed);
        }
        if (maximumFractionDigits != null) {
            format.setMaximumFractionDigits(maximumFractionDigits);
        }
        if (maximumIntegerDigits != null) {
            format.setMaximumIntegerDigits(maximumIntegerDigits);
        }
        if (minimumFractionDigits != null) {
            format.setMinimumFractionDigits(minimumFractionDigits);
        }
        if (minimumIntegerDigits != null) {
            format.setMinimumIntegerDigits(minimumIntegerDigits);
        }
        if (parseIntegerOnly != null) {
            format.setParseIntegerOnly(parseIntegerOnly);
        }
    }

    private java.lang.Number findNumberName() {
        java.lang.Number number = null;
        // find the name on the valueStack
        try {
            // suport Calendar also
            Object numberObject = findValue(name);
            if (numberObject instanceof java.lang.Number) {
                number = (java.lang.Number) numberObject;
            }
        } catch (Exception e) {
            LOG.error("Could not convert object with key [" + name + "] to a java.lang.Number instance");
        }
        return number;
    }

    private void setRoundingMode(NumberFormat format) {
    /*
        TODO lukaszlenart: enable when switched to Java 1.6
        if (roundingMode != null) {
            roundingMode = findString(roundingMode);
            if ("ceiling".equals(roundingMode)) {
                format.setRoundingMode(RoundingMode.CEILING);
            } else if ("down".equals(roundingMode)) {
                format.setRoundingMode(RoundingMode.DOWN);
            } else if ("floor".equals(roundingMode)) {
                format.setRoundingMode(RoundingMode.FLOOR);
            } else if ("half-down".equals(roundingMode)) {
                format.setRoundingMode(RoundingMode.HALF_DOWN);
            } else if ("half-even".equals(roundingMode)) {
                format.setRoundingMode(RoundingMode.HALF_EVEN);
            } else if ("half-up".equals(roundingMode)) {
                format.setRoundingMode(RoundingMode.HALF_UP);
            } else if ("unnecessary".equals(roundingMode)) {
                format.setRoundingMode(RoundingMode.UNNECESSARY);
            } else if ("up".equals(roundingMode)) {
                format.setRoundingMode(RoundingMode.UP);
            } else {
                LOG.error("Could not recognise a roundingMode of [" + roundingMode + "]");
            }
        }
    */
    }

    private NumberFormat getNumberFormat() {
        NumberFormat format = null;
        if (type == null) {
            try {
                type = findString(NUMBERTAG_PROPERTY);
            } catch (Exception e) {
                LOG.error("Could not find [" + NUMBERTAG_PROPERTY + "] on the stack!", e);
            }
        }
        if (type != null) {
            type = findString(type);
            if ("currency".equals(type)) {
                format = NumberFormat.getCurrencyInstance(ActionContext.getContext().getLocale());
            } else if ("integer".equals(type)) {
                format = NumberFormat.getIntegerInstance(ActionContext.getContext().getLocale());
            } else if ("number".equals(type)) {
                format = NumberFormat.getNumberInstance(ActionContext.getContext().getLocale());
            } else if ("percent".equals(type)) {
                format = NumberFormat.getPercentInstance(ActionContext.getContext().getLocale());
            }
        }
        if (format == null) {
            format = NumberFormat.getInstance(ActionContext.getContext().getLocale());
        }
        return format;
    }

    @StrutsTagAttribute(description = "Type of number formatter (currency, integer, number or percent, default is number)", rtexprvalue = false)
    public void setType(String type) {
        this.type = type;
    }

    @StrutsTagAttribute(description = "The currency to use for a currency format", type = "String", defaultValue = "")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    @StrutsTagAttribute(description = "The number value to format", required = true)
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the format.
     */
    public String getType() {
        return type;
    }

    /**
     * @return Returns the currency.
     */
    public String getCurrency() {
        return currency;
    }

    @StrutsTagAttribute(description = "Whether grouping is used", type = "Boolean")
    public void setGroupingUsed(Boolean groupingUsed) {
        this.groupingUsed = groupingUsed;
    }

    /**
     * @return Returns the grouping used.
     */
    public Boolean isGroupingUsed() {
        return groupingUsed;
    }

    /**
     * @return the maximumFractionDigits
     */
    public Integer getMaximumFractionDigits() {
        return maximumFractionDigits;
    }

    /**
     * @param maximumFractionDigits the maximumFractionDigits to set
     */
    @StrutsTagAttribute(description = "Maximum fraction digits", type = "Integer")
    public void setMaximumFractionDigits(Integer maximumFractionDigits) {
        this.maximumFractionDigits = maximumFractionDigits;
    }

    /**
     * @return the maximumIntegerDigits
     */
    public Integer getMaximumIntegerDigits() {
        return maximumIntegerDigits;
    }

    /**
     * @param maximumIntegerDigits the maximumIntegerDigits to set
     */
    @StrutsTagAttribute(description = "Maximum integer digits", type = "Integer")
    public void setMaximumIntegerDigits(Integer maximumIntegerDigits) {
        this.maximumIntegerDigits = maximumIntegerDigits;
    }

    /**
     * @return the minimumFractionDigits
     */
    public Integer getMinimumFractionDigits() {
        return minimumFractionDigits;
    }

    /**
     * @param minimumFractionDigits the minimumFractionDigits to set
     */
    @StrutsTagAttribute(description = "Minimum fraction digits", type = "Integer")
    public void setMinimumFractionDigits(Integer minimumFractionDigits) {
        this.minimumFractionDigits = minimumFractionDigits;
    }

    /**
     * @return the minimumIntegerDigits
     */
    public Integer getMinimumIntegerDigits() {
        return minimumIntegerDigits;
    }

    /**
     * @param minimumIntegerDigits the minimumIntegerDigits to set
     */
    @StrutsTagAttribute(description = "Maximum integer digits", type = "Integer")
    public void setMinimumIntegerDigits(Integer minimumIntegerDigits) {
        this.minimumIntegerDigits = minimumIntegerDigits;
    }

    /**
     * @return the parseIntegerOnly
     */
    public Boolean isParseIntegerOnly() {
        return parseIntegerOnly;
    }

    /**
     * @param parseIntegerOnly the parseIntegerOnly to set
     */
    @StrutsTagAttribute(description = "Parse integer only", type = "Boolean")
    public void setParseIntegerOnly(Boolean parseIntegerOnly) {
        this.parseIntegerOnly = parseIntegerOnly;
    }

    /**
     * @return the roundingMode
     */
    public String getRoundingMode() {
        return roundingMode;
    }

    /**
     * @param roundingMode the roundingMode to set
     */
    @StrutsTagAttribute(description = "The rounding mode to use - not implemented yet as this required Java 1.6", type = "String")
    public void setRoundingMode(String roundingMode) {
        this.roundingMode = roundingMode;
    }

}
