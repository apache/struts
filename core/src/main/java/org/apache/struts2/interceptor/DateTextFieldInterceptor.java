package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        Map<String, Object> parameters = ai.getInvocationContext().getParameters();
        Set<Entry<String, Object>> entries = parameters.entrySet();
        Map<String, Map<String, String>> dates = new HashMap<>();
        
        DateWord[] dateWords = DateWord.getAll();

        // Get all the values of date type
        for (Iterator<Entry<String, Object>> iterator = entries.iterator(); iterator.hasNext();) {
            Entry<String, ?> entry = iterator.next();
            String key = entry.getKey();

            for (DateWord dateWord : dateWords) {
            	String dateKey = "__" + dateWord.getDescription() + "_";
            	if (key.startsWith(dateKey)) {
                    String name = key.substring(dateKey.length());

                    if (entry.getValue() instanceof String[]) {
                    	String[] values = (String[])entry.getValue();
                    	if (values.length > 0 && !"".equals(values[0])) {
                    		iterator.remove();
                    		Map<String, String> map = dates.get(name);
                    		if (map == null) {
                                map = new HashMap<>();
                                dates.put(name, map);
                    		}
                            map.put(dateWord.getDateType(), values[0]);
                    	}
                    }
                    break;
                }
            }
        }

        // Create all the date objects
        Map<String, Date> newParams = new HashMap<>();
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
        parameters.putAll(newParams);

        return ai.invoke();
    }

}
