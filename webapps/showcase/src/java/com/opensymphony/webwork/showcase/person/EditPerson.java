package com.opensymphony.webwork.showcase.person;

import com.opensymphony.xwork.ActionSupport;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * <code>EditPerson</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id: EditPerson.java,v 1.2 2006/03/12 17:03:20 rainerh Exp $
 */
public class EditPerson extends ActionSupport {

    PersonManager personManager;
    List persons = new ArrayList();

    public void setPersonManager(PersonManager personManager) {
        this.personManager = personManager;
    }

    public List getPersons() {
        return persons;
    }

    public void setPersons(List persons) {
        this.persons = persons;
    }

    /**
     * A default implementation that does nothing an returns "success".
     *
     * @return {@link #SUCCESS}
     */
    public String execute() throws Exception {
        persons.addAll(personManager.getPeople());
        return SUCCESS;
    }

    /**
     * A default implementation that does nothing an returns "success".
     *
     * @return {@link #SUCCESS}
     */
    public String save() throws Exception {

        // Set people = personManager.getPeople();

        for ( Iterator iter = persons.iterator(); iter.hasNext();) {
            Person p = (Person) iter.next();
            personManager.getPeople().remove(p);
            personManager.getPeople().add(p);
        }
        return SUCCESS;
    }

}
