package com.opensymphony.webwork.showcase.person;

import com.opensymphony.xwork.ActionSupport;

import java.util.List;
import java.util.ArrayList;

/**
 * User: plightbo
 * Date: Aug 10, 2005
 * Time: 12:03:15 AM
 */
public class ListPeople extends ActionSupport {
    PersonManager personManager;
    List people = new ArrayList();

    public void setPersonManager(PersonManager personManager) {
        this.personManager = personManager;
    }

    public String execute() {
        people.addAll(personManager.getPeople());

        return SUCCESS;
    }

    public List getPeople() {
        return people;
    }

    public int getPeopleCount() {
        return people.size();
    }

}
