/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.struts2.components;

import java.util.Map;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * DoubleListUIBean is the standard superclass of all Struts Action Framework 2.0 double list handling components.
 *
 * <p/>
 *
 * <!-- START SNIPPET: javadoc -->
 * 
 * Note that the doublelistkey and doublelistvalue attribute will default to "key" and "value"
 * respectively only when the doublelist attribute is evaluated to a Map or its decendant.
 * Other thing else, will result in doublelistkey and doublelistvalue to be null and not used.
 * 
 * <!-- END SNIPPET: javadoc -->
 *
 */
public abstract class DoubleListUIBean extends ListUIBean {
	
	protected String emptyOption;
    protected String headerKey;
    protected String headerValue;
    protected String multiple;
    protected String size;
	
    protected String doubleList;
    protected String doubleListKey;
    protected String doubleListValue;
    protected String doubleName;
    protected String doubleValue;
    protected String formName;
    
    protected String doubleId;
    protected String doubleDisabled;
    protected String doubleMultiple;
    protected String doubleSize;
    protected String doubleHeaderKey;
    protected String doubleHeaderValue;
    protected String doubleEmptyOption;
    
    protected String doubleCssClass;
    protected String doubleCssStyle;
    
    protected String doubleOnclick;
    protected String doubleOndblclick;
    protected String doubleOnmousedown;
    protected String doubleOnmouseup;
    protected String doubleOnmouseover;
    protected String doubleOnmousemove;
    protected String doubleOnmouseout;
    protected String doubleOnfocus;
    protected String doubleOnblur;
    protected String doubleOnkeypress;
    protected String doubleOnkeydown;
    protected String doubleOnkeyup;
    protected String doubleOnselect;
    protected String doubleOnchange;
    

    public DoubleListUIBean(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        //Object doubleName = null;
        
        if (emptyOption != null) {
            addParameter("emptyOption", findValue(emptyOption, Boolean.class));
        }

        if (multiple != null) {
            addParameter("multiple", findValue(multiple, Boolean.class));
        }

        if (size != null) {
            addParameter("size", findString(size));
        }

        if ((headerKey != null) && (headerValue != null)) {
            addParameter("headerKey", findString(headerKey));
            addParameter("headerValue", findString(headerValue));
        }
        
        
        if (doubleMultiple != null) {
        	addParameter("doubleMultiple", findValue(doubleMultiple, Boolean.class));
        }
        
        if (doubleSize != null) {
        	addParameter("doubleSize", findString(doubleSize));
        }
        
        if (doubleDisabled != null) {
        	addParameter("doubleDisbled", findValue(doubleDisabled, Boolean.class));
        }

        if (doubleName != null) {
            addParameter("doubleName", findString(this.doubleName));
        }

        if (doubleList != null) {
            addParameter("doubleList", doubleList);
        }
        
        Object tmpDoubleList = findValue(doubleList);
        if (doubleListKey != null) {
            addParameter("doubleListKey", doubleListKey);
        }else if (tmpDoubleList instanceof Map) {
        	addParameter("doubleListKey", "key");
        }
        
        if (doubleListValue != null) {
            addParameter("doubleListValue", doubleListValue);
        }else if (tmpDoubleList instanceof Map) {
        	addParameter("doubleListValue", "value");
        }


        if (formName != null) {
            addParameter("formName", findString(formName));
        } else {
            // ok, let's look it up
            Component form = findAncestor(Form.class);
            if (form != null) {
                addParameter("formName", form.getParameters().get("name"));
            }
        }

        Class valueClazz = getValueClassType();

        if (valueClazz != null) {
            if (doubleValue != null) {
                addParameter("doubleNameValue", findValue(doubleValue, valueClazz));
            } else if (doubleName != null) {
                addParameter("doubleNameValue", findValue(doubleName.toString(), valueClazz));
            }
        } else {
            if (doubleValue != null) {
                addParameter("doubleNameValue", findValue(doubleValue));
            } else if (doubleName != null) {
                addParameter("doubleNameValue", findValue(doubleName.toString()));
            }
        }
        
        Form form = (Form) findAncestor(Form.class);
        if (doubleId != null) {
            // this check is needed for backwards compatibility with 2.1.x
            if (altSyntax()) {
                addParameter("doubleId", findString(doubleId));
            } else {
                addParameter("doubleId", doubleId);
            }
        } else if (form != null) {
            addParameter("doubleId", form.getParameters().get("id") + "_" +escape(this.doubleName));
        }
        
        if (doubleOnclick != null) {
        	addParameter("doubleOnclick", findString(doubleOnclick));
        }
        
        if (doubleOndblclick != null) {
        	addParameter("doubleOndblclick", findString(doubleOndblclick));
        }
        
        if (doubleOnmousedown != null) {
        	addParameter("doubleOnmousedown", findString(doubleOnmousedown));
        }
        
        if (doubleOnmouseup != null) {
        	addParameter("doubleOnmouseup", findString(doubleOnmouseup));
        }
        
        if (doubleOnmouseover != null) {
        	addParameter("doubleOnmouseover", findString(doubleOnmouseover));
        }
        
        if (doubleOnmousemove != null) {
        	addParameter("doubleOnmousemove", findString(doubleOnmousemove));
        }
        
        if (doubleOnmouseout != null) {
        	addParameter("doubleOnmouseout", findString(doubleOnmouseout));
        }
        
        if (doubleOnfocus != null) {
        	addParameter("doubleOnfocus", findString(doubleOnfocus));
        }
        
        if (doubleOnblur != null) {
        	addParameter("doubleOnblur", findString(doubleOnblur));
        }
        
        if (doubleOnkeypress != null) {
        	addParameter("doubleOnkeypress", findString(doubleOnkeypress));
        }
        
        if (doubleOnkeydown != null) {
        	addParameter("doubleOnkeydown", findString(doubleOnkeydown));
        }
        
        if (doubleOnselect != null) {
        	addParameter("doubleOnselect", findString(doubleOnselect));
        }
        
        if (doubleOnchange != null) {
        	addParameter("doubleOnchange", findString(doubleOnchange));
        }
        
        if (doubleCssClass != null) {
        	addParameter("doubleCss", findString(doubleCssClass));
        }
        
        if (doubleCssStyle != null) {
        	addParameter("doubleStyle", findString(doubleCssStyle));
        }
        
        if (doubleHeaderKey != null && doubleHeaderValue != null) {
        	addParameter("doubleHeaderKey", findString(doubleHeaderKey));
        	addParameter("doubleHeaderValue", findString(doubleHeaderValue));
        }
        
        if (doubleEmptyOption != null) {
        	addParameter("doubleEmptyOption", findValue(doubleEmptyOption, Boolean.class));
        }
    }

    /**
     * The second iterable source to populate from.
     * @a2.tagattribute required="true"
     */
    public void setDoubleList(String doubleList) {
        this.doubleList = doubleList;
    }

    /**
     * The key expression to use for second list
     * @a2.tagattribute required="false"
     */
    public void setDoubleListKey(String doubleListKey) {
        this.doubleListKey = doubleListKey;
    }

    /**
     * The value expression to use for second list
     * @a2.tagattribute required="false"
     */
    public void setDoubleListValue(String doubleListValue) {
        this.doubleListValue = doubleListValue;
    }

    /**
     * The name for complete component
     * @a2.tagattribute required="true"
     */
    public void setDoubleName(String doubleName) {
        this.doubleName = doubleName;
    }

    /**
     * The value expression for complete component
     * @a2.tagattribute required="false"
     */
    public void setDoubleValue(String doubleValue) {
        this.doubleValue = doubleValue;
    }

    /**
     * The form name this component resides in and populates to
     * @a2.tagattribute required="false"
     */
    public void setFormName(String formName) {
        this.formName = formName;
    }
    
    public String getFormName() {
    	return formName;
    }
    
    /**
     * The css class for the second list
     * @a2.tagattribute required="false"
     */
    public void setDoubleCssClass(String doubleCssClass) {
    	this.doubleCssClass = doubleCssClass;
    }
    
    public String getDoubleCssClass() {
    	return doubleCssClass;
    }
    
    /**
     * The css style for the second list
     * @a2.tagattribute required="false"
     */
    public void setDoubleCssStyle(String doubleCssStyle) {
    	this.doubleCssStyle = doubleCssStyle;
    }
    
    public String getDoubleCssStyle() {
    	return doubleCssStyle;
    }
    
    /**
     * The header key for the second list
     * @a2.tagattribute required="false"
     */
    public void setDoubleHeaderKey(String doubleHeaderKey) {
    	this.doubleHeaderKey = doubleHeaderKey;
    }
    
    public String getDoubleHeaderKey() {
    	return doubleHeaderKey;
    }
    
    /**
     * The header value for the second list
     * @a2.tagattribute required="false"
     */
    public void setDoubleHeaderValue(String doubleHeaderValue) {
    	this.doubleHeaderValue = doubleHeaderValue;
    }
    
    public String getDoubleHeaderValue() {
    	return doubleHeaderValue;
    }

    /**
     * Decides if the second list will add an empty option
     * @a2.tagattribute required="false"
     */
    public void setDoubleEmptyOption(String doubleEmptyOption) {
    	this.doubleEmptyOption = doubleEmptyOption;
    }
    
    public String getDoubleEmptyOption() {
    	return this.doubleEmptyOption;
    }

    
	public String getDoubleDisabled() {
		return doubleDisabled;
	}

	/**
     * Decides if a disable attribute should be added to the second list
     * @a2.tagattribute required="false"
     */
	public void setDoubleDisabled(String doubleDisabled) {
		this.doubleDisabled = doubleDisabled;
	}

	public String getDoubleId() {
		return doubleId;
	}

	/**
     * The id of the second list
     * @a2.tagattribute required="false"
     */
	public void setDoubleId(String doubleId) {
		this.doubleId = doubleId;
	}

	public String getDoubleMultiple() {
		return doubleMultiple;
	}

	/**
     * Decides if multiple attribute should be set on the second list
     * @a2.tagattribute required="false"
     */
	public void setDoubleMultiple(String doubleMultiple) {
		this.doubleMultiple = doubleMultiple;
	}

	public String getDoubleOnblur() {
		return doubleOnblur;
	}

	/**
     * Set the onblur attribute of the second list
     * @a2.tagattribute required="false"
     */
	public void setDoubleOnblur(String doubleOnblur) {
		this.doubleOnblur = doubleOnblur;
	}

	public String getDoubleOnchange() {
		return doubleOnchange;
	}

	/**
     * Set the onchange attribute of the second list
     * @a2.tagattribute required="false"
     */
	public void setDoubleOnchange(String doubleOnchange) {
		this.doubleOnchange = doubleOnchange;
	}

	public String getDoubleOnclick() {
		return doubleOnclick;
	}

	/**
     * Set the onclick attribute of the second list
     * @a2.tagattribute required="false"
     */
	public void setDoubleOnclick(String doubleOnclick) {
		this.doubleOnclick = doubleOnclick;
	}

	public String getDoubleOndblclick() {
		return doubleOndblclick;
	}

	/**
     * Set the ondbclick attribute of the second list
     * @a2.tagattribute required="false"
     */
	public void setDoubleOndblclick(String doubleOndblclick) {
		this.doubleOndblclick = doubleOndblclick;
	}

	public String getDoubleOnfocus() {
		return doubleOnfocus;
	}

	/**
     * Set the onfocus attribute of the second list
     * @a2.tagattribute required="false"
     */
	public void setDoubleOnfocus(String doubleOnfocus) {
		this.doubleOnfocus = doubleOnfocus;
	}

	public String getDoubleOnkeydown() {
		return doubleOnkeydown;
	}

	/**
     * Set the onkeydown attribute of the second list
     * @a2.tagattribute required="false"
     */
	public void setDoubleOnkeydown(String doubleOnkeydown) {
		this.doubleOnkeydown = doubleOnkeydown;
	}

	public String getDoubleOnkeypress() {
		return doubleOnkeypress;
	}

	/**
     * Set the onkeypress attribute of the second list
     * @a2.tagattribute required="false"
     */
	public void setDoubleOnkeypress(String doubleOnkeypress) {
		this.doubleOnkeypress = doubleOnkeypress;
	}

	public String getDoubleOnkeyup() {
		return doubleOnkeyup;
	}

	/**
     * Set the onkeyup attribute of the second list
     * @a2.tagattribute required="false"
     */
	public void setDoubleOnkeyup(String doubleOnkeyup) {
		this.doubleOnkeyup = doubleOnkeyup;
	}

	public String getDoubleOnmousedown() {
		return doubleOnmousedown;
	}

	/**
     * Set the onmousedown attribute of the second list
     * @a2.tagattribute required="false"
     */
	public void setDoubleOnmousedown(String doubleOnmousedown) {
		this.doubleOnmousedown = doubleOnmousedown;
	}

	public String getDoubleOnmousemove() {
		return doubleOnmousemove;
	}

	/**
     * Set the onmousemove attribute of the second list
     * @a2.tagattribute required="false"
     */
	public void setDoubleOnmousemove(String doubleOnmousemove) {
		this.doubleOnmousemove = doubleOnmousemove;
	}

	public String getDoubleOnmouseout() {
		return doubleOnmouseout;
	}

	/**
     * Set the onmouseout attribute of the second list
     * @a2.tagattribute required="false"
     */
	public void setDoubleOnmouseout(String doubleOnmouseout) {
		this.doubleOnmouseout = doubleOnmouseout;
	}

	public String getDoubleOnmouseover() {
		return doubleOnmouseover;
	}

	/**
     * Set the onmouseover attribute of the second list
     * @a2.tagattribute required="false"
     */
	public void setDoubleOnmouseover(String doubleOnmouseover) {
		this.doubleOnmouseover = doubleOnmouseover;
	}

	public String getDoubleOnmouseup() {
		return doubleOnmouseup;
	}

	/**
     * Set the onmouseup attribute of the second list
     * @a2.tagattribute required="false"
     */
	public void setDoubleOnmouseup(String doubleOnmouseup) {
		this.doubleOnmouseup = doubleOnmouseup;
	}

	public String getDoubleOnselect() {
		return doubleOnselect;
	}

	/**
     * Set the onselect attribute of the second list
     * @a2.tagattribute required="false"
     */
	public void setDoubleOnselect(String doubleOnselect) {
		this.doubleOnselect = doubleOnselect;
	}

	public String getDoubleSize() {
		return doubleSize;
	}

	/**
     * Set the size attribute of the second list
     * @a2.tagattribute required="false"
     */
	public void setDoubleSize(String doubleSize) {
		this.doubleSize = doubleSize;
	}

	public String getDoubleList() {
		return doubleList;
	}

	/**
     * Set the list key of the second attribute
     * @a2.tagattribute required="false"
     */
	public String getDoubleListKey() {
		return doubleListKey;
	}

	public String getDoubleListValue() {
		return doubleListValue;
	}

	public String getDoubleName() {
		return doubleName;
	}

	public String getDoubleValue() {
		return doubleValue;
	}
	
	/**
     * Decides of an empty option is to be inserted in the second list
     * @a2.tagattribute required="false" default="false" type="Boolean"
     */
    public void setEmptyOption(String emptyOption) {
        this.emptyOption = emptyOption;
    }

    /**
     * Set the header key of the second list. Must not be empty! "'-1'" and "''" is correct, "" is bad.
     * @a2.tagattribute required="false"
     */
    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    /**
     * Set the header value of the second list
     * @a2.tagattribute required="false"
     */
    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    /**
     * Creates a multiple select. The tag will pre-select multiple values if the values are passed as an Array (of appropriate types) via the value attribute.
     * @a2.tagattribute required="false"
     */
    public void setMultiple(String multiple) {
        // TODO: Passing a Collection may work too?
        this.multiple = multiple;
    }

    /**
     * Size of the element box (# of elements to show)
     * @a2.tagattribute required="false" type="Integer"
     */
    public void setSize(String size) {
        this.size = size;
    }
}
