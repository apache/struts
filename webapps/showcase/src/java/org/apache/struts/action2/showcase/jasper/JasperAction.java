/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.showcase.jasper;

import java.util.Set;
import java.io.File;

import net.sf.jasperreports.engine.JasperCompileManager;

import com.opensymphony.xwork.ActionSupport;
import org.apache.struts.action2.ServletActionContext;
import org.apache.struts.action2.showcase.person.PersonManager;

/**
 * @author Philip Luppens
 * @author Rainer Hermanns
 */
public class JasperAction extends ActionSupport {

	private static final long serialVersionUID = 6320765173392624324L;
	
	private PersonManager personManager;

    public void setPersonManager(PersonManager personManager) {
        this.personManager = personManager;
    }

    /*
      * (non-Javadoc)
      *
      * @see com.opensymphony.xwork.ActionSupport#execute()
      */
    public String execute() throws Exception {

        /*
           * Here we compile our xml jasper template to a jasper file.
           * Note: this isn't exactly considered 'good practice'.
           * You should either use precompiled jasper files (.jasper) or provide some kind of check
           * to make sure you're not compiling the file on every request.
           * If you don't have to compile the report, you just setup your data source (eg. a List), and skip this
           */
        try {
            String reportSource = ServletActionContext.getServletContext().getRealPath("/jasper/sample_report.jrxml");
            File parent = new File(reportSource).getParentFile();
            JasperCompileManager.compileReportToFile(
                    reportSource,
                    new File(parent, "sample_report.jasper").getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
        //if all goes well ..
        return SUCCESS;
    }

    /**
     * @return Returns the people.
     */
    public Set getPeople() {
        return personManager.getPeople();
    }

}
