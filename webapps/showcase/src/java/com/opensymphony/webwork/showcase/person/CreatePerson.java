package com.opensymphony.webwork.showcase.person;

import com.opensymphony.xwork.ActionSupport;

/**
 * User: plightbo
 * Date: Aug 9, 2005
 * Time: 9:24:03 PM
 */
public class CreatePerson extends ActionSupport {
    PersonManager personManager;
    Person person;

    public void setPersonManager(PersonManager personManager) {
        this.personManager = personManager;
    }

    public String execute() {
        personManager.createPerson(person);

        return SUCCESS;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
