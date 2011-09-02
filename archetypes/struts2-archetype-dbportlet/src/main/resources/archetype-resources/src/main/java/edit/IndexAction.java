package ${package}.edit;

import org.apache.struts2.dispatcher.DefaultActionSupport;

/**
 *
 */
public class IndexAction extends DefaultActionSupport {
    private String pref1;
    private String pref2;
    private boolean submit;

    public String execute() {

        if (submit) {

            // Save the preferences somehow

            return SUCCESS;
        } else {    
        
            // Preform any logic here
            
            return INPUT;
        }    
    }

    public void setSubmit(String val) {
        this.submit = (val != null);
    }

    public String getPref1() {
        return pref1;
    }
    public String getPref2() {
        return pref2;
    }

    public void setPref1(String pref) {
        this.pref1 = pref;
    }     

    public void setPref2(String pref) {
        this.pref2 = pref;
    }     
}
