package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.Parameter;
import org.apache.struts2.dispatcher.HttpParameters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class DateTextFieldInterceptor implements Interceptor {

    private static final Logger LOG = LogManager.getLogger(DateTextFieldInterceptor.class);

    public static enum DateWord {

		S("millisecond", 3, "SSS"),
		s("second", 2, "ss"),
		m("minute", 2, "mm"),
		H("hour", 2, "HH"),
		d("day", 2, "dd"),
		M("month", 2, "MM"),
		y("year", 4, "yyyy");

		private String description;
		private Integer length;
		private String dateType;

        DateWord(String n, Integer l, String t) {
            description = n;
			length = l;
			dateType = t;
		}

        public String getDescription() {
            return description;
        }

        public Integer getLength() {
            return length;
        }

        public String getDateType() {
            return dateType;
        }

        public static DateWord get(Character c) {
            return valueOf(DateWord.class, c.toString());
        }

        public static DateWord[] getAll() {
            return values();
        }
    }
    
    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation ai) throws Exception {
        HttpParameters parameters = ai.getInvocationContext().getParameters();
        Map<String, Map<String, String>> dates = new HashMap<>();
        
        DateWord[] dateWords = DateWord.getAll();

        // Get all the values of date type
        Set<String> names = parameters.getNames();
        for (String name : names) {

            for (DateWord dateWord : dateWords) {
            	String dateKey = "__" + dateWord.getDescription() + "_";
            	if (name.startsWith(dateKey)) {
                    String key = name.substring(dateKey.length());

                    Parameter param = parameters.get(name);

                    if (param.isDefined()) {
                        Map<String, String> map = dates.get(key);
                        if (map == null) {
                            map = new HashMap<>();
                            dates.put(key, map);
                        }
                        map.put(dateWord.getDateType(), param.getValue());

                        parameters = parameters.remove(name);
                    }
                    break;
                }
            }
        }

        // Create all the date objects
        Map<String, Object> newParams = new HashMap<>();
        Set<Entry<String, Map<String, String>>> dateEntries = dates.entrySet();
        for (Entry<String, Map<String, String>> dateEntry : dateEntries) {
        	Set<Entry<String, String>> dateFormatEntries = dateEntry.getValue().entrySet();
        	String dateFormat = "";
        	String dateValue = "";
        	for (Entry<String, String> dateFormatEntry : dateFormatEntries) {
        		dateFormat += dateFormatEntry.getKey() + "__";
        		dateValue += dateFormatEntry.getValue() + "__";
        	}
            try {
            	SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            	formatter.setLenient(false);
                Date value = formatter.parse(dateValue);
                newParams.put(dateEntry.getKey(), value);
            } catch (ParseException e) {
                LOG.warn("Cannot parse the parameter '{}' with format '{}' and with value '{}'", dateEntry.getKey(), dateFormat, dateValue);
            }
        }

        ai.getInvocationContext().setParameters(parameters.clone(newParams));

        return ai.invoke();
    }

}
