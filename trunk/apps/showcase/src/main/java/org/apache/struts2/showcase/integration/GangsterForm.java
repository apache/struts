package org.apache.struts2.showcase.integration;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorForm;

public class GangsterForm extends ValidatorForm {
    
    private String name;
    private String age;
    private String description;
    private boolean bustedBefore;
    
    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
        bustedBefore = false;
    }
    
    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);
        if (name == null || name.length() == 0) {
            errors.add("name", new ActionMessage("The name must not be blank"));
        }
        
        return errors;
    }
    
    /**
     * @return the age
     */
    public String getAge() {
        return age;
    }
    /**
     * @param age the age to set
     */
    public void setAge(String age) {
        this.age = age;
    }
    /**
     * @return the bustedBefore
     */
    public boolean isBustedBefore() {
        return bustedBefore;
    }
    /**
     * @param bustedBefore the bustedBefore to set
     */
    public void setBustedBefore(boolean bustedBefore) {
        this.bustedBefore = bustedBefore;
    }
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    
}
