package org.apache.struts2.views.java.simple;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.struts2.interceptor.DateTextFieldInterceptor.DateWord;
import org.apache.struts2.views.java.Attributes;
import org.apache.struts2.views.java.TagGenerator;


public class DateTextFieldHandler extends AbstractTagHandler implements TagGenerator {

    @SuppressWarnings("unchecked")
	public void generate() throws IOException {
        Map<String, Object> params = context.getParameters();
        Attributes attr = null;

        // Get format
        String format = (String)params.get("format");
        String id = (String)params.get("id");
        String name = (String)params.get("name");
        if (id == null) {
        	id = name;
        }
        Date date = (Date)params.get("nameValue");
        
        if (format != null) {
        	// Verify if it's correct
            new SimpleDateFormat(format);
            
            attr = new Attributes();
            attr.addIfExists("id", id);
            super.start("div", attr);
            
            Character antC = null;
            for (Character c : format.toCharArray()) {
            	
            	try {
            		DateWord dateWord = DateWord.get(c);
            		if (!c.equals(antC)) {
            			
            			String cssClass = "date_" + dateWord.getDescription();
            			if (params.get("cssClass") != null) {
            				cssClass += " " + params.get("cssClass");
            			}
            			
            			attr = new Attributes();
            	        attr.add("type", "text")
            	                .addIfExists("class", cssClass)
            	                .addIfExists("size", dateWord.getLength())
            	                .addIfExists("maxlength", dateWord.getLength())
            	                .addIfTrue("disabled", params.get("disabled"))
            	                .addIfTrue("readonly", params.get("readonly"))
            	                .addIfExists("tabindex", params.get("tabindex"))
            	                .addIfExists("style", params.get("cssStyle"))
            	                .addIfExists("title", params.get("title"));
            	        
            	        if (id != null && !"".equals(id)) {
                	        attr.addDefaultToEmpty("id", "__" + dateWord.getDescription() + "_" + id);
            	        }
            	        if (name != null && !"".equals(id)) {
                	        attr.addDefaultToEmpty("name", "__" + dateWord.getDescription() + "_" + name);
            	        } else {
                	        attr.addDefaultToEmpty("name", dateWord.getDescription());
            	        }
            	        if (date != null) {
            	        	SimpleDateFormat formatter = new SimpleDateFormat(dateWord.getDateType());
            	        	attr.addIfExists("value", formatter.format(date), false);
            	        }
            	        
            	        super.start("input", attr);
            	        super.end("input");
            	        
            		}
            	} catch (IllegalArgumentException e) {
            		super.characters(c.toString());	
            	}
            	antC = c;
            
            }
            super.end("div");
        }
        
    }
    
    
    
}

